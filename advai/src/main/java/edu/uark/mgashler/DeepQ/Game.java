package edu.uark.mgashler.DeepQ;
// ----------------------------------------------------------------
// The contents of this file are distributed under the CC0 license.
// See http://creativecommons.org/publicdomain/zero/1.0/
// ----------------------------------------------------------------

import javax.swing.*;


public class Game extends JFrame
{
	volatile boolean running;
	Thread pump = null;
	Model model;
	View view;
	Controller controller;

	public Game() throws Exception
	{
		model = new Model();
		view = new View(model);
		controller = new Controller(model, view);
		view.addMouseListener(controller);
		addKeyListener(controller);
		this.setTitle("Cart Pole");
		this.setSize(500, 500);
		this.getContentPane().add(view);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);
		running = true;
	}

	public void run()
	{
		while(running)
		{
			//long time = System.currentTimeMillis();
			controller.update();
			model.update();
			if(view.isVisualizing())
			{
				repaint(); // Indirectly calls View.paintComponent
				try {
					Thread.sleep(50);
				} catch(InterruptedException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}

	public static void main(String[] args) throws Exception
	{
		System.out.println("Press [space] to toggle view mode");
		System.out.println("Press [t] to toggle train/test");
		System.out.println("Press [<-] or [->] to manually drive the cart");
		Game g = new Game();
		g.run();
	}
}
