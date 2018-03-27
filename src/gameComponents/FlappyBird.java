package gameComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class FlappyBird implements ActionListener {

	public static FlappyBird flappyBird;
	public final int WIDTH = 1000, HEIGHT = 700;
	public Renderer renderer;
	public Rectangle bird;
	public ArrayList<Rectangle> columns;
	public Random r;
	public int ticks, yMotion;
	public boolean gameOver, started;
	
	public FlappyBird() {
		started = true;
		JFrame frame = new JFrame();
		r = new Random();
		
		renderer = new Renderer();
		frame.add(renderer);
		
		Timer timer = new Timer(20, this);
		
		frame.setSize(WIDTH, HEIGHT);
		frame.setTitle("Flappy Bird Imitation");
		frame.setVisible(true);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		bird = new Rectangle(WIDTH/2-10, HEIGHT/2-10, 20, 20);
		columns = new ArrayList<Rectangle>();
		
		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);
		
		timer.start();
	}
	
	public void addColumn(boolean start) {
		int space = 300;
		int width = 100;
		int height = 50 + r.nextInt(300);
		
		if (start) {
			columns.add(new Rectangle(WIDTH + width + columns.size() *300, HEIGHT - height -120,
					width, height));
			
			columns.add(new Rectangle(WIDTH + width + (columns.size() -1) * 300, 0, 
					width, HEIGHT - height - space));
		}
		else {
			columns.add(new Rectangle(columns.get(columns.size() -1).x+600, HEIGHT - height -120,
					width, height));
			
			columns.add(new Rectangle(columns.get(columns.size() -1).x, 0, 
					width, HEIGHT - height - space));
		}
	}
	
	public void paintColumn(Graphics g, Rectangle column) {
		g.setColor(Color.green.darker());
		g.fillRect(column.x, column.y, column.width, column.height);
	}
	
	
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
		
		for (Rectangle column: columns) {
			paintColumn(g, column);
		}
		
		g.setColor(Color.white);
		g.setFont(new Font("Arial", 1, 100));
		
		if (gameOver) {
			g.drawString("Game Over", 225, HEIGHT / 2 - 15);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		renderer.repaint();
		
		int speed = 10;
		
		ticks++;
		
		if (started) {
			for (int i = 0; i < columns.size(); i++) {
				Rectangle col = columns.get(i);
				col.x -= speed;
			}
			
			if (ticks % 2 == 0 && yMotion < 15) {
				yMotion += 2;
			}
			
			for (int i = 0; i < columns.size(); i++) {
				Rectangle col = columns.get(i);
				
				if (col.x + col.width < 0) {
					columns.remove(col);
					
					if (col.y == 0) {
						addColumn(false);
					}
				}
			}
			
			bird.y += yMotion;
			
			// Game over if bird collides with a column
			for (Rectangle column: columns) {
			if (column.intersects(bird)) {
				gameOver = true;
			}

			}
			
			// Game over if bird exits borders
			if (bird.y < HEIGHT - 120 || bird.y < 0) {
				
				gameOver = true;
			}
			
			if (gameOver) {
				bird.y = HEIGHT - 120 - bird.height;
			}
			
		}
		
		
		renderer.repaint();
	}
	
	
	
	public static void main(String[] args) {
		
		flappyBird = new FlappyBird();
		
	}

	
}
