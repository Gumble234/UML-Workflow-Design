package command;

import java.util.List;
import model.*;

public class CreateLinkCommand implements ICommand {

    private List<LinkObject> linkList;
    private LinkObject line;

    public CreateLinkCommand(List<LinkObject> linkList, List<GraphicObject> graphicList,
            int x1, int y1, int x2, int y2,
            Class<? extends LinkObject> lineType) {

        this.linkList = linkList;

        // 找到最接近的連接埠
        ConnectionPort p1 = findNearestPort(graphicList, x1, y1);
        ConnectionPort p2 = findNearestPort(graphicList, x2, y2);

        if (p1 != null && p2 != null && p1.getOwner() != p2.getOwner()) {
            try {
                // 利用反射建構對應型別的 link
                this.line = lineType.getConstructor(ConnectionPort.class, ConnectionPort.class)
                                    .newInstance(p1, p2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ConnectionPort findNearestPort(List<GraphicObject> graphicList, int x, int y) {
        double minDist = Double.MAX_VALUE;
        ConnectionPort nearest = null;

        for (GraphicObject obj : graphicList) {
            List<ConnectionPort> ports = obj.getConnectionPorts();
            for (ConnectionPort port : ports) {
                double dist = Math.hypot(port.getX() - x, port.getY() - y);
                if (dist < minDist) {
                    minDist = dist;
                    nearest = port;
                }
            }
        }
        return nearest;
    }

    @Override
    public void execute() {
        if (line != null) {
            linkList.add(line);
        }
    }

    @Override
    public void undo() {
        if (line != null) {
            linkList.remove(line);
        }
    }
}
