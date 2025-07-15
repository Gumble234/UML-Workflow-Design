package  model;



public class Port {
    private int x, y;
    private GraphicObject owner; // 所屬圖形物件

    public Port(int x, int y, GraphicObject owner) {
        this.x = x;
        this.y = y;
        this.owner = owner;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public GraphicObject getOwner() {
        return owner;
    }
}
