// File: command/UngroupCommand.java
package command;

import java.util.List;
import model.CompositeObject;
import model.GraphicObject;

public class UngroupCommand implements ICommand {
    private List<GraphicObject> list;
    private CompositeObject group;
    private List<GraphicObject> children;

    public UngroupCommand(List<GraphicObject> list, CompositeObject group) {
        this.list     = list;
        this.group    = group;
        this.children = group.getChildren();
    }

    @Override
    public void execute() {
        list.remove(group);
        for (GraphicObject o : children) {
            list.add(o);
        }
    }

    @Override
    public void undo() {
        for (GraphicObject o : children) {
            list.remove(o);
        }
        list.add(group);
    }
}
