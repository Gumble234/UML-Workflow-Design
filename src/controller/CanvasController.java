package controller;

import command.ICommand;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JFrame;
import model.CompositeObject;
import model.GraphicObject;
import model.LinkObject;
import view.LabelStyleDialog;

public class CanvasController {

    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    private GraphicObject draggingObject = null;

    private final List<GraphicObject> graphicObjects = new ArrayList<>();
    private final List<LinkObject> linkObjects = new ArrayList<>();

    private int endX, endY;
    private final CommandManager commandManager = new CommandManager();
    private ToolController.ToolType activeTool = ToolController.ToolType.SELECT;
    private int startX, startY;
    private final Consumer<Void> repaintCallback;
    private GraphicObject startObject = null;

    public CanvasController(Consumer<Void> repaintCallback) {
        this.repaintCallback = repaintCallback != null ? repaintCallback : v -> {
        };
    }

    public CanvasController() {
        this.repaintCallback = v -> {
        };
    }

    public void groupSelectedObjects() {
        List<GraphicObject> selected = new ArrayList<>();
        for (GraphicObject obj : graphicObjects) {
            if (obj.isSelected()) {
                selected.add(obj);
            }
        }
        if (selected.size() <= 1) {
            return;
        }

        for (GraphicObject obj : selected) {
            obj.setSelected(false);
        }

        GraphicObject first = selected.get(0);
        CompositeObject group = new CompositeObject(first.getCenterX(), first.getCenterY());
        for (GraphicObject obj : selected) {
            group.add(obj);
        }
        graphicObjects.removeAll(selected);
        graphicObjects.add(group);
        group.setSelected(true);
        repaint();
    }

    public void ungroupSelectedObject() {
        CompositeObject selectedGroup = null;
        for (GraphicObject obj : graphicObjects) {
            if (obj.isSelected() && obj instanceof CompositeObject) {
                selectedGroup = (CompositeObject) obj;
                break;
            }
        }
        if (selectedGroup == null) {
            return;
        }

        List<GraphicObject> children = selectedGroup.getChildren();
        graphicObjects.remove(selectedGroup);
        graphicObjects.addAll(children);
        for (GraphicObject obj : graphicObjects) {
            obj.setSelected(false);
        }
        repaint();
    }

    public void setActiveTool(ToolController.ToolType tool) {
        this.activeTool = tool;
    }

    public void onMousePressed(int x, int y) {
        startX = x;
        startY = y;
        draggingObject = null;
        isDragging = false;

        if (activeTool == ToolController.ToolType.SELECT) {
            for (int i = graphicObjects.size() - 1; i >= 0; i--) {
                GraphicObject obj = graphicObjects.get(i);
                if (obj.contains(x, y)) {
                    draggingObject = obj;
                    dragOffsetX = x - obj.getCenterX();
                    dragOffsetY = y - obj.getCenterY();
                    isDragging = true;
                    break;
                }
            }

            if (draggingObject != null) {
                for (GraphicObject obj : graphicObjects) {
                    obj.setSelected(obj == draggingObject);
                }
            } else {
                for (GraphicObject obj : graphicObjects) {
                    obj.setSelected(false);
                }
            }
            repaint();
        } else if (isLinkTool(activeTool)) {
            startObject = findGraphicAt(x, y);
        }
    }

    public void onMouseDragged(int x, int y) {
        if (isDragging && draggingObject != null) {
            draggingObject.setX(x - dragOffsetX);
            draggingObject.setY(y - dragOffsetY);
            repaint();
        }
    }

    public void onMouseReleased(int x, int y) {
        if (activeTool == ToolController.ToolType.SELECT) {
            if (isDragging && draggingObject != null) {
                repaint();
                draggingObject = null;
                isDragging = false;
                return;
            }

            int dx = Math.abs(x - startX);
            int dy = Math.abs(y - startY);
            int dragThreshold = 5;

            if (dx > dragThreshold || dy > dragThreshold) {
                int left = Math.min(startX, x);
                int right = Math.max(startX, x);
                int top = Math.min(startY, y);
                int bottom = Math.max(startY, y);

                boolean anySelected = false;
                for (GraphicObject obj : graphicObjects) {
                    Rectangle bounds = obj.getBounds();
                    if (left <= bounds.x && right >= bounds.x + bounds.width
                            && top <= bounds.y && bottom >= bounds.y + bounds.height) {
                        obj.setSelected(true);
                        anySelected = true;
                    } else {
                        obj.setSelected(false);
                    }
                }
                if (!anySelected) {
                    for (GraphicObject obj : graphicObjects) {
                        obj.setSelected(false);
                    }
                }
            }
            repaint();
            return;
        }

        ICommand cmd = null;
        if (activeTool == ToolController.ToolType.RECT) {
            cmd = new command.CreateObjectCommand(graphicObjects, model.Rect.class, x, y);
        } else if (activeTool == ToolController.ToolType.OVAL) {
            cmd = new command.CreateObjectCommand(graphicObjects, model.Oval.class, x, y);
        } else if (isLinkTool(activeTool)) {
            if (startObject != null) {
                GraphicObject endObject = findGraphicAt(x, y);
                if (endObject != null && endObject != startObject) {
                    cmd = new command.CreateLinkCommand(
                            linkObjects,
                            graphicObjects,
                            startX,
                            startY,
                            x,
                            y,
                            getLinkClassByTool(activeTool)
                    );
                }
            }
            startObject = null;
        }

        if (cmd != null) {
            commandManager.executeCommand(cmd);
            repaint();
        }
    }

    public void customizeLabelStyle(JFrame parent) {
        for (GraphicObject obj : graphicObjects) {
            if (obj.isSelected()) {
                LabelStyleDialog dialog = new LabelStyleDialog(parent, obj);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    obj.setLabel(dialog.getLabelName());
                    obj.setLabelShape(dialog.getSelectedShape());
                    obj.setLabelColor(dialog.getSelectedColor());
                    obj.setLabelFontSize(dialog.getSelectedFontSize());
                    repaint();
                }
                break;
            }
        }
    }

    public void renderAll(Graphics g) {
        for (GraphicObject obj : graphicObjects) {
            if (obj != null) {
                obj.draw(g);
            }
        }
        for (LinkObject link : linkObjects) {
            if (link != null) {
                link.draw(g);
            }
        }
    }

    public void undo() {
        commandManager.undo();
        repaint();
    }

    public void redo() {
        commandManager.redo();
        repaint();
    }

    private void repaint() {
        if (repaintCallback != null) {
            repaintCallback.accept(null);
        }
    }

    private GraphicObject findGraphicAt(int x, int y) {
        for (int i = graphicObjects.size() - 1; i >= 0; i--) {
            GraphicObject obj = graphicObjects.get(i);
            if (obj.contains(x, y)) {
                return obj;
            }
        }
        return null;
    }

    private boolean isLinkTool(ToolController.ToolType tool) {
        return tool == ToolController.ToolType.ASSOCIATION
                || tool == ToolController.ToolType.COMPOSITION
                || tool == ToolController.ToolType.GENERALIZATION;
    }

    private Class<? extends LinkObject> getLinkClassByTool(ToolController.ToolType tool) {
        return switch (tool) {
            case ASSOCIATION ->
                model.AssociationLink.class;
            case COMPOSITION ->
                model.CompositionLink.class;
            case GENERALIZATION ->
                model.GeneralizationLink.class;
            default ->
                null;
        };
    }

    public List<GraphicObject> getGraphicObjects() {
        return new ArrayList<>(graphicObjects);
    }

    public List<LinkObject> getLinkObjects() {
        return new ArrayList<>(linkObjects);
    }
}
