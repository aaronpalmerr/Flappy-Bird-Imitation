package gameComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * FlappyBird class controls the speed and jump of the bird tile as well
 * as the columns moving across the frame.  
 * @author apalm
 *
 */

public class FlappyBird implements ActionListener, MouseListener, KeyListener {

	/*
	 * Class instance variables
	 */
	public static FlappyBird flappyBird;
	public final int WIDTH = 1000, HEIGHT = 700;
	public Renderer renderer;
	public Rectangle bird;
	public ArrayList<Rectangle> columns;
	public Random r;
	public int ticks, yMotion, score;
	public boolean gameOver, started;
	
	/*
	 * Constructor
	 */
	public FlappyBird() {
		r = new Random();
		
		JFrame frame = new JFrame();
		frame.addMouseListener(this);
		frame.addKeyListener(this);
		frame.setSize(WIDTH, HEIGHT);
		frame.setTitle("Flappy Bird Imitation");
		frame.setVisible(true);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.setResizable(false);
	
		renderer = new Renderer();
		frame.add(renderer);
		
		Timer timer = new Timer(20, this);
		
		// game objects for first round of play
		bird = new Rectangle(WIDTH/2+10, HEIGHT/2-65, 20, 20);
		columns = new ArrayList<Rectangle>();
		
		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);
		
		// starts timer
		timer.start();
	}
	
	/**
	 * Creates new columns to move across the frame.  
	 * @param start
	 */
	public void addColumn(boolean start) {
		// size variables for columns
		int space = 300;
		int width = 100;
		int height = 50 + r.nextInt(300);
		
		// adds columns at beginning of the game
		if (start) {
			columns.add(new Rectangle(WIDTH + width + columns.size() *300, HEIGHT - height -120,
					width, height));
			
			columns.add(new Rectangle(WIDTH + width + (columns.size() -1) * 300, 0, 
					width, HEIGHT - height - space));
		}
		
		// adds columns throughout the game
		else {
			columns.add(new Rectangle(columns.get(columns.size() -1).x+600, HEIGHT - height -120,
					width, height));
			
			columns.add(new Rectangle(columns.get(columns.size() -1).x, 0, 
					width, HEIGHT - height - space));
		}
	}
	

	/**
	 * Controls game reset and motion of the bird tile.
	 */
	private void jump() {
		
		// resets game
		if (gameOver) {
			bird = new Rectangle(WIDTH/2-10, HEIGHT/2-10, 20, 20);
			columns.clear();
			yMotion = 0;
			score = 0;
			
			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);
			
			gameOver = false;
		}
		
		// starts game
		if (!started) {
			started = true;
		}
		
		else if (started && !gameOver) {
			if (yMotion > 0) {
				yMotion = 0;
			}
			
			yMotion -= 10;
		}
		
	}
	
	/**
	 * Paints columns across the frame.  
	 * @param g
	 * @param column
	 */
	public void paintColumn(Graphics g, Rectangle column) {
		g.setColor(Color.green.darker());
		g.fillRect(column.x, column.y, column.width, column.height);
	}
	
	/**
	 * Paints the sky, ground, and bird along with the game text and score. 
	 * @param g
	 */
	public void repaint(Graphics g) {
		// sky
		g.setColor(Color.cyan);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		// dirt
		g.setColor(Color.ORANGE);
		g.fillRect(0, HEIGHT - 120, WIDTH, 120);
		
		// grass
		g.setColor(Color.green);
		g.fillRect(0, HEIGHT - 120, WIDTH, 20);
		
		// bird
		g.setColor(Color.red);
		g.fillRect(bird.x, bird.y, bird.width, bird.height);
		
		// paints the columns
		for (Rectangle column: columns) {
			paintColumn(g, column);
		}
		
		// new font
		g.setColor(Color.white);
		g.setFont(new Font("Arial", 1, 100));
		
		// text that appears at opening of game
		if (!started) {
			g.drawString("Click to start", 215, HEIGHT / 2 - 15);
		}
		// appears if player loses
		if (gameOver) {
			g.drawString("Game Over", 225, HEIGHT / 2 - 15);
		}
		// appears while player is playing
		if (!gameOver & started) {
			g.drawString(""+score, WIDTH / 2 - 25, 100);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		renderer.repaint();
		
		int speed = 10;
		
		ticks++;
		
		if (started) {
			
			// controls speed that columns move across frame
			for (int i = 0; i < columns.size(); i++) {
				Rectangle col = columns.get(i);
				col.x -= speed;
			}
			
			// vertical motion of bird
			if (ticks % 2 == 0 && yMotion < 15) {
				yMotion += 2;
			}
			
			// remove columns that reach end of frame
			for (int i = 0; i < columns.size(); i++) {
				Rectangle col = columns.get(i);
				
				// remove column
				if (col.x + col.width < 0) {
					columns.remove(col);
					
					// set production of column to false
					if (col.y == 0) {
						addColumn(false);
					}
				}
			}
			
			// change location of bird based on yMotion
			bird.y += yMotion;
			
			// check for collision and score
			for (Rectangle column: columns) {
				
				// increment score if pass through column opening
				if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10 &&
						bird.x + bird.width / 2 < column.x + column.width / 2 + 10) {
					score++;
					//System.out.println("The score is: " + score);
				}
				
				// Game over if bird collides with a column
				if (bird.intersects(column)) {
					gameOver = true;
					bird.x = column.x - bird.width;
				}
			}
			
			// Game over if bird exits borders
			if (bird.y > HEIGHT - 120 || bird.y < 0) {
				gameOver = true;
			} 
			
			// bird falls straight down after collision
			if (bird.y + yMotion >= HEIGHT - 120) {
				bird.y = HEIGHT - 120 - bird.height;
			}
			
		}
		
		renderer.repaint();
	}
	
	/*
	 * Main for the class
	 */
	public static void main(String[] args) {
		
		flappyBird = new FlappyBird();
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		jump();
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// empty method
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// empty method
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// empty method
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// empty method
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
			jump();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// empty method
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// empty method
	}

	
}
