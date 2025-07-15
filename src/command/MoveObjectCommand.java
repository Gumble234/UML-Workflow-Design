// File: command/MoveObjectCommand.java
package command;

import model.GraphicObject;

public class MoveObjectCommand implements ICommand {

    private GraphicObject obj;
    private int dx, dy;

    public MoveObjectCommand(GraphicObject obj, int dx, int dy) {
        this.obj = obj;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void execute() {
        obj.moveBy(dx, dy);
    }

    @Override
    public void undo() {
        obj.moveBy(-dx, -dy);
    }
}
