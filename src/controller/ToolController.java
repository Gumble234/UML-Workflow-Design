package controller;

public class ToolController {

    public enum ToolType {
        SELECT, RECT, OVAL,
        ASSOCIATION, COMPOSITION, GENERALIZATION
    }

    private ToolType currentTool = ToolType.SELECT;
    private CanvasController canvasController;

    public ToolController(CanvasController canvasController) {
        this.canvasController = canvasController;
    }

    public void selectTool(ToolType tool) {
        this.currentTool = tool;
        canvasController.setActiveTool(tool);
    }

    public ToolType getCurrentTool() {
        return currentTool;
    }
}
