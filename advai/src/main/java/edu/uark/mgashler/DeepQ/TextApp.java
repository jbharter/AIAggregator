package edu.uark.mgashler.DeepQ;

import javax.swing.*;

public class TextApp extends JFrame {

    TextModel textModel;
    TextView textView;
    TextController textController;

    volatile boolean running;

    public TextApp() throws Exception {
        this.textModel = new TextModel();
        this.textView = new TextView(textModel);
        this.textController = new TextController(textModel,textView);

        textView.addMouseListener(textController);
        textView.addKeyListener(textController);
        this.setTitle("Text App Window");
        this.setSize(750,750);
        this.getContentPane().add(textView);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        running = true;
    }

    public void run() {
        while(running) {
            textController.update();
            textModel.update();
            if (textView.isVisible()) {
                repaint();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        TextApp textApp = new TextApp();
        textApp.run();
    }
}
