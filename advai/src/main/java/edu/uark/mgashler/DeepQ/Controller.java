package edu.uark.mgashler.DeepQ;

// ----------------------------------------------------------------
// The contents of this file are distributed under the CC0 license.
// See http://creativecommons.org/publicdomain/zero/1.0/
// ----------------------------------------------------------------

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

class Controller implements MouseListener, KeyListener
{
	static Random rand = new Random(1234);
	Model model;
	View view;
	boolean training;

	Controller(Model m, View v)
	{
		model = m;
		view = v;
		training = true;
	}

	public void mousePressed(MouseEvent e) {
		//model.onClick(e.getX(), e.getY());
        System.out.println(e.getX() + "," + e.getY());
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void keyPressed(KeyEvent e)
	{
		char c = e.getKeyChar();
		if(c == ' ') {
			view.toggleVisualizing(); // Toggle the display on/off. (Training is much faster when off.)
			if(view.isVisualizing()) {
			    System.out.println("Visualize mode (slow)");
            } else {
			    System.out.println("No-visualize mode (fast)");
            }
		} else if(c == 't') {
			training = !training;
			if(training) {
			    System.out.println("Now training");
            } else {
			    System.out.println("Now testing");
            }
		} else {
			int n = e.getKeyCode();
			model.applyForce(0.0);
			if(n == KeyEvent.VK_RIGHT) {
			    model.applyForce(0.1);
            }
			if(n == KeyEvent.VK_LEFT) {
			    model.applyForce(-0.1);
            }
            if(n == KeyEvent.VK_ENTER) {
			    model.reset();
            }
		}
	}

	public void keyReleased(KeyEvent e)
	{
	}

	public void keyTyped(KeyEvent e)
	{
	}

	void update() {
	    /*
	    // Pick an action
Let Q be a table of q-values; // (we will use a neural network)
Let i be the current state; // (get values from the model)
Let ε = 0.05; // (this tells how frequently to explore)
Let n be the number of possible actions;
if(you are training and rand.nextDouble() < ε)
{
	// Explore (pick a random action)
	action = rand.nextInt(n);
}
else
{
	// Exploit (pick the best action)
	action = 0;
	for(int candidate = 0; candidate < n; candidate++)
		if(Q(i, candidate) > Q(i, action))
			action = candidate;
}

// Do the chosen action
do_action(action); // that is, apply the appropriate force and call Model.update()
Let j be the new state // (new values from the model)

// Learn from that experience
Apply the equation below to update the Q-table.
	a = action.
	Q(i,a) refers to the Q-table entry for doing
		action "a" in state "i".
	Q(j,b) refers to the Q-table entry for doing
		action "b" in state "j".
	1.0 might be a good value for αk when using a neural net for the Q-table.
	(Don't mix up "α" (alpha) with "a" (ay or eh?).)
	0.97 might be a good value for γ (gamma).
	A(j) is the set of four possible actions.
	r(i,a,j) is the reward you obtained when you landed in state j.
	In this game, the objective is to keep the pole upright, so reward it accordingly.

// Reset
If the pendulum falls down, reset the game.
	     */
	}
}
