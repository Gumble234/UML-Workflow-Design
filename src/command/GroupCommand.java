package command;

import java.awt.Rectangle;
import java.util.List;
import model.CompositeObject;
import model.GraphicObject;

public class GroupCommand implements ICommand {
    private final List<GraphicObject> list;
    private final CompositeObject group;
    private final List<GraphicObject> selected;

    public GroupCommand(List<GraphicObject> list, List<GraphicObject> selected) {
        this.list = list;
        this.selected = selected;

        // 計算選取物件的整體邊界，作為群組中心
        Rectangle bounds = calculateBounds(selected);
        int centerX = bounds.x + bounds.width / 2;
        int centerY = bounds.y + bounds.height / 2;

        this.group = new CompositeObject(centerX, centerY);
    }

    @Override
    public void execute() {
        for (GraphicObject obj : selected) {
            list.remove(obj);
            group.add(obj);
        }
        list.add(group);
    }

    @Override
    public void undo() {
        list.remove(group);
        for (GraphicObject obj : selected) {
            list.add(obj);
        }
    }

    private Rectangle calculateBounds(List<GraphicObject> objs) {
        if (objs.isEmpty()) {
            return new Rectangle(0, 0, 0, 0);
        }

        Rectangle result = objs.get(0).getBounds();
        for (int i = 1; i < objs.size(); i++) {
            result = result.union(objs.get(i).getBounds());
        }
        return result;
    }
}
