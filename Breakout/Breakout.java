/*
 * Creates an interactive version of Breakout
 * 
 * Author: Lara Quiring
 * Date: 6/16/18
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 4;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 1;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;
	
	/** Ball velocity */
	private double vx, vy;
	
	/** Frame rate */
	private double PAUSE = 1000 / 60;
	
	/** Number of lives */
	private int numLives = 3;

	public void run() {
		//set size of window
		this.setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
		GCanvas g = this.getGCanvas();
		g.setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
		
		//make paddle
		GRect paddle = makePaddle();
		add(paddle);
		g.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				double x = e.getX() - paddle.getWidth() / 2.0;
				
				if(x < 0) {
					x = 0;
				} else if(x + paddle.getWidth() > APPLICATION_WIDTH) {
					x = APPLICATION_WIDTH - paddle.getWidth();
				}
				paddle.setLocation(x, paddle.getY());
			}
		});
		
		int initX = (APPLICATION_WIDTH - (NBRICKS_PER_ROW * BRICK_WIDTH + (NBRICKS_PER_ROW - 1) * BRICK_SEP)) / 2;
		//make bricks
		for(int i = 0; i < NBRICK_ROWS; i++) {
			for(int j = 0; j < NBRICKS_PER_ROW; j++) {
				GRect brick = new GRect(initX + j * (BRICK_WIDTH + BRICK_SEP), BRICK_Y_OFFSET + i * (BRICK_HEIGHT + BRICK_SEP), BRICK_WIDTH, BRICK_HEIGHT);
				//set colors
				if(i / 2.0 < 1) {
					brick.setFillColor(Color.RED); 
					brick.setColor(Color.RED); 
				} else if(i / 2.0 < 2) {
					brick.setFillColor(Color.ORANGE); 
					brick.setColor(Color.ORANGE); 
				} else if(i / 2.0 < 3) {
					brick.setFillColor(Color.YELLOW); 
					brick.setColor(Color.YELLOW); 
				} else if(i / 2.0 < 4) {
					brick.setFillColor(Color.GREEN); 
					brick.setColor(Color.GREEN); 
				} else {
					brick.setFillColor(Color.CYAN); 
					brick.setColor(Color.CYAN); 
				}
				brick.setFilled(true);
				add(brick);
			}
		}
		
		//make ball
		GOval ball = makeBall();
		add(ball);
		
		//set init velocity
		vy = 3;
		vx = random();
		
		boolean bool = true;
		int num = NBRICK_ROWS * NBRICKS_PER_ROW;

		//start game
		waitForClick();
		//ball movement
		while(bool) {
			//win condition
			if(num == 0) {
				bool = false;
				GLabel end = new GLabel("You won!", APPLICATION_WIDTH / 2 - 30, 150);
				add(end);
			} else { //continue play
				//fell out the bottom
				if(ball.getY() >= g.getHeight()) {
					numLives--;
					removeAll();
					if(numLives > 0) {
						run();
					} else {
						bool = false;
						GLabel end = new GLabel("You lost!", APPLICATION_WIDTH / 2 - 15, 150);
						add(end);
					}
				}
				
				//ball interaction with walls
				if(ball.getY() <= 0) {
					vy = -vy;
				}
				if(ball.getX() <= 0 || ball.getX() + BALL_RADIUS >= g.getWidth()) {
					vx = -vx;
				}
				
				//ball interaction with GObjects
				GObject obj = getCollision(g, ball);
				if(obj == paddle) {
					//edge case - what if the ball hits the side
					vy = -vy;
				} else if(obj != null) {
					remove(obj);
					num--;
					vy = -vy;
				}
				
				//move ball
				ball.move(vx, vy);
				pause(PAUSE);
			}
		}
	}
	
	/**
	 * Makes the paddle rectangle and places on screen
	 * @return GRect
	 */
	private GRect makePaddle() {
		GRect paddle = new GRect((APPLICATION_WIDTH - PADDLE_WIDTH) / 2, APPLICATION_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.darkGray);
		paddle.setFillColor(Color.darkGray);
		paddle.setFilled(true);
		return paddle;
	}
	
	/**
	 * Makes the ball and centers it in the screen to start
	 * @return GOval
	 */
	private GOval makeBall() {
		GOval ball = new GOval((APPLICATION_WIDTH - BALL_RADIUS) / 2, (APPLICATION_HEIGHT - BALL_RADIUS) / 2, BALL_RADIUS, BALL_RADIUS);
		ball.setColor(Color.gray);
		ball.setFillColor(Color.gray);
		ball.setFilled(true);
		return ball;
	}
	
	/**
	 * Determines if the ball has collided with anything
	 * @param g | GCanvas
	 * @param ball | GOval
	 * @return GObject | null
	 */
	private GObject getCollision(GCanvas g, GOval ball) {
		if(g.getElementAt(ball.getX(), ball.getY()) != null) {
			return g.getElementAt(ball.getX(), ball.getY());
		} else if(g.getElementAt(ball.getX(), ball.getY() + ball.getHeight()) != null) {
			return g.getElementAt(ball.getX(), ball.getY() + ball.getHeight());
		} else if(g.getElementAt(ball.getX() + ball.getWidth(), ball.getY()) != null) {
			return g.getElementAt(ball.getX() + ball.getWidth(), ball.getY());
		} else if(g.getElementAt(ball.getX() + ball.getWidth(), ball.getY() + ball.getHeight()) != null) {
			return g.getElementAt(ball.getX() + ball.getWidth(), ball.getY() + ball.getHeight());
		}
		return null;
	}
	
	/**
	 * Random velocity generator for the initial x component of the ball
	 * @return double
	 */
	private double random() {
		RandomGenerator r = RandomGenerator.getInstance();
		double vx = r.nextDouble(1.0, 3.0);
		if(r.nextBoolean(0.5)) {
			vx = -vx;
		}
		return vx;
	}
}
