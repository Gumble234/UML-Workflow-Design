package command;

import model.GraphicObject;

public class LabelStyleCommand implements ICommand {
    private GraphicObject obj;
    private String oldLabel, newLabel;

    public LabelStyleCommand(GraphicObject obj, String newLabel) {
        this.obj = obj;
        this.oldLabel = obj.getLabel();
        this.newLabel = newLabel;
    }
    @Override public void execute() { obj.setLabel(newLabel); }
    @Override public void undo() { obj.setLabel(oldLabel); }
}