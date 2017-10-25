package edu.uark.mgashler.DeepQ;

// ----------------------------------------------------------------
// The contents of this file are distributed under the CC0 license.
// See http://creativecommons.org/publicdomain/zero/1.0/
// ----------------------------------------------------------------

class Model {
	// Constants
	final double cart_mass = 10.0;
	final double pole_mass = 0.1;
	final double pole_length = 10.0; // in pixels
	final double gravitational_constant = 1.0;
	final double cart_friction = 0.1; // resistance to cart motion
	final double pole_friction = 0.3; // resistance to pole angular motion
	final double boundary = 1.0; // how far from the center the cart is allowed to travel

	// Variables
	double cart_position; // 0 = center
	double cart_velocity; // in position units per time frame
	double pole_angle; // 0 is straight down.
	double pole_angular_velocity; // in radians per time frame
	double applied_force; // force applied to the cart by the agent

	Model() {
		reset();
	}

	void reset() {
		applied_force = 0.0;
		cart_position = 0.0;
		cart_velocity = 0.0;
		pole_angle = Math.PI + 0.001;
		pole_angular_velocity = 0.0;
	}

	public void update() {
		double c = Math.cos(pole_angle);
		double s = Math.sin(pole_angle);

		// This was derived from the second-to-last equation at http://www.myphysicslab.com/pendulum_cart.html
		double cart_acceleration = 
			(
				pole_mass * pole_length * pole_angular_velocity * pole_angular_velocity * s +
				pole_mass * gravitational_constant * s * c +
				applied_force -
				cart_friction * cart_velocity +
				pole_friction / pole_length * pole_angular_velocity * c
			) / (
				cart_mass + pole_mass * s * s
			);

		// This was derived from the last equation at http://www.myphysicslab.com/pendulum_cart.html
		double pole_angular_acceleration = 
			(
				-pole_mass * pole_length * pole_angular_velocity * pole_angular_velocity * s * c -
				(cart_mass + pole_mass) * gravitational_constant * s +
				applied_force * c +
				cart_friction * cart_velocity * c -
				(1.0 + cart_mass / pole_mass) * (pole_friction / pole_length) * pole_angular_velocity
			) / (
				pole_length * (cart_mass + pole_mass * s * s)
			);

		// Do kinematics
		cart_velocity += cart_acceleration;
		cart_position += cart_velocity;
		if(cart_position < -boundary) {
			cart_position = -boundary;
			cart_velocity = 0.0;
		}
		if(cart_position > boundary) {
			cart_position = boundary;
			cart_velocity = 0.0;
		}
		pole_angular_velocity += pole_angular_acceleration;
		pole_angle += pole_angular_velocity;

		// Keep the angle between -PI and PI
		if(Math.abs(pole_angle) > Math.PI) {
		    pole_angle -= (2.0 * Math.PI * Math.floor((pole_angle + Math.PI) / (2.0 * Math.PI)));
        }
	}

	public void applyForce(double f) {
		this.applied_force = f;
	}
}
