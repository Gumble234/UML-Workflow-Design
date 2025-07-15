package model;

import java.awt.Graphics;

public abstract class LinkObject {

    protected ConnectionPort p1, p2;

    public LinkObject(ConnectionPort p1, ConnectionPort p2) {
        this.p1 = p1;
        this.p2 = p2;
    }



    public abstract void draw(Graphics g);
}
