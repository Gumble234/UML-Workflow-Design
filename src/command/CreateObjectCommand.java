package command;

import java.util.List;
import model.GraphicObject;

public class CreateObjectCommand implements ICommand {
    private List<GraphicObject> list;
    private GraphicObject obj;

    // 僅使用 x, y 中心點即可
    public CreateObjectCommand(List<GraphicObject> list, Class<? extends GraphicObject> clazz,
                               int centerX, int centerY) {
        this.list = list;
        try {
            this.obj = clazz.getConstructor(int.class, int.class)
                            .newInstance(centerX, centerY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute() {
        list.add(obj);
    }

    @Override
    public void undo() {
        list.remove(obj);
    }
}
