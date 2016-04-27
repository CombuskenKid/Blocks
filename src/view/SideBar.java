package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class SideBar extends JPanel{
	
	private HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();	//Unscaled blocks to be drawn on the panel.
	private HashMap<String, BufferedImage> scaledImages = new HashMap<String, BufferedImage>();	//Scaled blocks to be drawn on the panel.
	private HashMap<Point, String> drawnImages = new HashMap<Point, String>();	//Names of blocks with positions to be drawn on the panel.
	private Timer timer;
	private int yPos = this.getY() + 10;
	private int location;
	private boolean extended = false;

	public SideBar(Dimension frameSize, GameCanvas canvas){
		this.images = canvas.getUnselectedImages();
		this.location = (int) (-frameSize.getWidth()/15);
		this.setBackground(Color.BLACK);
		this.setSize((int) frameSize.getWidth()/15, (int) (frameSize.getHeight()/1.25));
		this.setLocation(-this.getWidth(), 0);
		scaleImages();
		addToDrawnImages();
	}
		
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        addComponentsToPane(g);
	}
	
	private void addComponentsToPane(Graphics g) {
		for (Iterator<Entry<Point, String>> iterator = drawnImages.entrySet().iterator(); iterator.hasNext();) {
			Entry<Point, String> entry = iterator.next();
			Point position = entry.getKey();
			g.drawImage((Image) scaledImages.get(entry.getValue()), (int) position.getX(), (int) position.getY(), getParent());
		}
	}
	
	public void addToDrawnImages(){
		int xPos = this.getWidth()/6;
		for(String s : scaledImages.keySet()){
			drawnImages.put(new Point(xPos, yPos), s);
			yPos = yPos + scaledImages.get(s).getHeight() + 20;
		}
	}
	
	public void scaleImages(){
		for (Iterator<Entry<String, BufferedImage>> iterator = images.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, BufferedImage> entry = iterator.next();
			BufferedImage image = entry.getValue();
			double scalingValue = (double) image.getHeight()/(double) image.getWidth();
			if(scalingValue > 1){	//If image is a block and not a tile.
				scaledImages.put(entry.getKey(), getScaledImage(image, this.getWidth()/2, (int) ((this.getWidth()/2)*scalingValue)));
			}
		}
	}
	
	private BufferedImage getScaledImage(Image img, int w, int h){
	    BufferedImage resized = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
	    Graphics2D g2 = resized.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(img, 0, 0, w, h, null);
	    g2.dispose();
	    return resized;
	}
	
	public synchronized void slidePanel(JPanel panel, int y, boolean extend) {
		timer = new Timer(5, new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(extend){
					if (location < 0){
						location = location + 1;
						panel.setLocation(location, y);
						panel.repaint();
					} else {
						timer.stop();
					}
				}
				else{
					if (location > -panel.getWidth()){
						location = location - 1;
						panel.setLocation(location, y);
						panel.repaint();
					} else {
						timer.stop();
					}
				}
				panel.setLocation(location, y);
				panel.repaint();

			}
		});
		timer.start();
	}
	
	public void selectBlock(String name, BufferedImage image, Point point){
		images.put(name, image);
		double scalingValue = (double) image.getHeight()/(double) image.getWidth();
		scaledImages.put(name, getScaledImage(image, this.getWidth()/2, (int) ((this.getWidth()/2)*scalingValue)));
		drawnImages.put(point, name);
		repaint();
	}
	
	public boolean getExtended(){
		return extended;
	}
	
	public HashMap<Point, String> getDrawnImages(){
		return this.drawnImages;
	}
	
	public HashMap<String, BufferedImage> getScaledImages(){
		return this.scaledImages;
	}
	
	public void setExtended(boolean extended){
		this.extended = extended;
	}
	
	public void setYPos(int yPos){
		this.yPos = yPos;
	}
}
