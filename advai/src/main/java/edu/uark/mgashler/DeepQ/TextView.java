package edu.uark.mgashler.DeepQ;

import javax.swing.*;
import java.awt.*;

class TextView extends JPanel {
    boolean visualizing;
    TextModel model;

    int x;
    int y;

    TextView(TextModel m) {
        this.visualizing = true;
        this.model = m;
    }

    public void paintComponent(Graphics g) {

        g.drawString("("+x+","+y+")",x,y);

    }

}
