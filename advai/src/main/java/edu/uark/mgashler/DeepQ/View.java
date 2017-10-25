package edu.uark.mgashler.DeepQ;
// ----------------------------------------------------------------
// The contents of this file are distributed under the CC0 license.
// See http://creativecommons.org/publicdomain/zero/1.0/
// ----------------------------------------------------------------

import javax.swing.*;
import java.awt.*;

class View extends JPanel
{
	private final int ground_y = 400;
	private final int width = 500;
	private final int cart_width = 100;
	private final int cart_height = 30;
	private final int cart_wheel_diameter = 10;
	private final double scale = 100.0;

	private Model model;
	private volatile boolean visualizing;

	View(Model m) {
		this.visualizing = true;
		this.model = m;
	}

	boolean isVisualizing() {
	    return this.visualizing;
    }

    boolean toggleVisualizing() {
	    this.visualizing = !this.visualizing;
	    return isVisualizing();
    }

	public void paintComponent(Graphics g) {
		// Draw the ground and boundaries
		int left = width / 2 - (int)(model.boundary * scale) - cart_width / 2;
		int right = width / 2 + (int)(model.boundary * scale) + cart_width / 2;
		g.drawLine(left, ground_y, right, ground_y);
		g.drawLine(left, ground_y, left, ground_y - 30);
		g.drawLine(right, ground_y, right, ground_y - 30);
		
		// Draw the cart
		int pole_x = width / 2 + (int)(scale * model.cart_position);
		int cart_x = pole_x - cart_width / 2;
		int cart_y = ground_y - cart_wheel_diameter - cart_height;
		g.drawRect(cart_x, cart_y, cart_width, cart_height);
		g.drawOval(cart_x, cart_y + cart_height, cart_wheel_diameter, cart_wheel_diameter);
		g.drawOval(cart_x + cart_width - cart_wheel_diameter, cart_y + cart_height, cart_wheel_diameter, cart_wheel_diameter);

		// Draw the pole
		double horiz_reach = scale * Math.cos(model.pole_angle + Math.PI / 2);
		double vert_reach = scale * Math.sin(model.pole_angle + Math.PI / 2);
		g.drawLine(pole_x, cart_y, pole_x + (int)horiz_reach, cart_y + (int)vert_reach);
	}
}
