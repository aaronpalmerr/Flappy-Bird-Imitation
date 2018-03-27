package gameComponents;

import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Renderer class repaints the Flappy Bird Frame.
 * @author apalm
 *
 */

public class Renderer extends JPanel {

	private static final long serialVersionUID = 1L;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		FlappyBird.flappyBird.repaint(g);
	}
	
}
