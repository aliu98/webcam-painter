import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-based drawing 
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {
		// TODO: YOUR CODE HERE
		super.draw(g); //shows webcam 
		g.drawImage(painting, 0, 0, null); //puts painting on top of webcam
	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	//if there is a target color, find the largest region of that target color and 
	//set the color to blue (this will be the paintbrush)
	public void processImage() {
		// TODO: YOUR CODE HERE
		finder.setImage(image); //sets the image using RegionFinder
		if (targetColor != null) { 
			finder.findRegions(targetColor);
			ArrayList<Point> paintbrush = finder.largestRegion(); 
			if (paintbrush != null) {
				for (int x = 0; x < paintbrush.size(); x++) {
					painting.setRGB((int)paintbrush.get(x).getX(), (int)paintbrush.get(x).getY(), Color.BLUE.getRGB()); //recolor paintbrush to blue
				}
			}
		}
	}

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	
	public void handleMousePress(int x, int y) {
		// TODO: YOUR CODE HERE
		if (image != null) { //if there is an image, have the target color be the color at (x,y) in image
			targetColor = new Color(image.getRGB(x, y));
			System.out.println("tracking " + targetColor);
		}
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}
