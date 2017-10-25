package edu.uark.mgashler.DeepQ;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TextController implements MouseListener, KeyListener {
    int x;
    int y;

    TextModel textModel;
    TextView textView;

    public TextController(TextModel textModel, TextView textView) {
        this.textModel = textModel;
        this.textView = textView;
    }

    public void update() {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        textView.x = e.getX();
        textView.y = e.getY();
        System.out.println(e.getX() + "," + e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
