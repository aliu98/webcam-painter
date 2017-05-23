import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;			// a region is a list of points
															// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */

	public void findRegions(Color targetColor) {
		// TODO: YOUR CODE HERE
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB); //create new image to use as point tracker 
		ArrayList<Point> to_visit = new ArrayList<Point>(); //queue for pixels that need to be visited 
		regions = new ArrayList<ArrayList<Point>>();
				
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) { //loop over all pixels				
				
				if (visited.getRGB(x, y) == 0 && colorMatch(new Color(image.getRGB(x, y)), targetColor)) { // if pixel is unvisited, aka black in recolored image, and if pixel color is in range of target color
					to_visit.add(new Point(x,y)); //add pixel that matches color to be visited 
					visited.setRGB(x,y,1); //marks point as visited
					if (to_visit.size() == 0) { //if no other neighbors match target color
						continue; 
					} 
					ArrayList<Point> region = new ArrayList<Point>(); //creates a new region
					while (to_visit.size() > 0) { 
						Point toAdd = to_visit.get(to_visit.size() - 1);
						region.add(toAdd); //add point to region
						to_visit.remove(to_visit.size() - 1); // remove first item in to visit list
				
						for (int i= (int)toAdd.getX() - 1; i <= (int)toAdd.getX() +1; i++) { //loop over all neighbors
							if (i == -1 || i == image.getWidth()) {
								continue;
							}
							for (int j=(int)toAdd.getY()-1; j <=(int)toAdd.getY()+1; j++) {		
								if (j == -1 || j == image.getHeight()) {
									continue; 
								}
								if (i==x && j==y) { //if same point
									continue;
								}
								if (visited.getRGB(i,  j) == 0 && colorMatch(targetColor, new Color(image.getRGB(i, j)))) { //if neighbor in same color range and not visited 
									to_visit.add(new Point(i,j)); //add neighbor that matches color to be visited
									visited.setRGB(i, j, 1); //marks neighbor pixel as visited
								}
							}
						}
					} 
					if (region.size() >= minRegion) { //if region is bigger or equal to minimum region size
						regions.add(region); //add region to regions (list of all the region) 
					}
				}	
			}
		}
	}
			

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		// TODO: YOUR CODE HERE
		if ((Math.abs(c2.getRed()) - maxColorDiff) <= Math.abs(c1.getRed()) && Math.abs(c1.getRed()) <= ((Math.abs(c2.getRed()) + maxColorDiff)))  { //checks if R is within range
			if ((Math.abs(c2.getGreen()) - maxColorDiff) <= Math.abs(c1.getGreen()) && Math.abs(c1.getGreen()) <= ((Math.abs(c2.getGreen()) + maxColorDiff)))  { //checks if green is within range
				if ((Math.abs(c2.getBlue()) - maxColorDiff) <= Math.abs(c1.getBlue()) && Math.abs(c1.getBlue()) <= ((Math.abs(c2.getBlue()) + maxColorDiff)))  { //checks if blue is within range					
					return true; 
				}
			}
		} 
		return false;	
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		// TODO: YOUR CODE HERE
		if (regions.size() == 0) {  // if there's no region in regions, return null
			System.out.println("no regions");
			return null; 
		}
		
		// find the largest region in regions 
		int maxregion = regions.get(0).size(); 
		int max_index = 0;
		for (int i = 0; i < regions.size(); i++) {
			if (maxregion <= regions.get(i).size()) {
				maxregion = regions.get(i).size(); 
				max_index = i; 
			} 
		}

		return regions.get(max_index); 
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		// TODO: YOUR CODE HERE
//		regions = new ArrayList<ArrayList<Point>>(); 
//		findRegions(new Color(130, 100, 100)); 
		for (int x = 0; x < regions.size(); x ++) { 
			int red = (int)(Math.random() * 255); 
			int green = (int)(Math.random() * 255); 
			int blue = (int)(Math.random() * 255); 
			Color random = new Color(red, green, blue); 
			for (int y = 0; y < regions.get(x).size(); y++){
				recoloredImage.setRGB((int)regions.get(x).get(y).getX(), (int)regions.get(x).get(y).getY(), random.getRGB()); //recolor each pixel
			}

		}

	}
}
