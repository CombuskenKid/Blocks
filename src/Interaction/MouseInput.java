package Interaction;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;
import view.GameCanvas;
import view.SideBar;

public class MouseInput implements MouseWheelListener, MouseMotionListener, MouseListener {

	private GameCanvas canvas;
	private SideBar sideBar;
	private boolean onSideBar = false;
	private Point mouseLoc = null;
	
	public MouseInput(GameCanvas canvas, SideBar sideBar){
		this.canvas = canvas;
		this.sideBar = sideBar;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(onSideBar == true){
			return;
		}
		int mPosX = (int) e.getPoint().getX();
		int mPosY = (int) e.getPoint().getY();
		int pRangeX = mPosX - canvas.getTranslateX();
		int pRangeY = mPosY - canvas.getTranslateY();
		int deltaTranslateX = pRangeX/canvas.getZoom() * 20;
		int deltaTranslateY = pRangeY/canvas.getZoom() * 20;
		int notches = e.getWheelRotation();
	    if (notches < 0) {
	    	canvas.zoomIn(deltaTranslateX, deltaTranslateY);
	    } else {
	    	canvas.zoomOut(deltaTranslateX, deltaTranslateY);
	    }
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(onSideBar == true){
			return;
		}
		if(!SwingUtilities.isRightMouseButton(e)) {
			mouseLoc = null;
		}
		if (SwingUtilities.isRightMouseButton(e)) {
			double dragAmountX = 0;
			double dragAmountY = 0;
			if(mouseLoc != null){
				dragAmountX = e.getPoint().getX() - mouseLoc.getX();
				dragAmountY = e.getPoint().getY() - mouseLoc.getY();
			}
			mouseLoc = e.getPoint();
			canvas.moveWorld(dragAmountX, dragAmountY);
		}
		if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()) {
			Point p = e.getPoint();
			selectTile(p, true);
		}
		else if (SwingUtilities.isLeftMouseButton(e)) {
			Point p = e.getPoint();
			selectTile(p, false);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseLoc = null;
		if(e.getPoint().getX() == 0){
			new Thread() {
		        public void run() {
		        	while(MouseInfo.getPointerInfo().getLocation().getX() == 0){
						moveWorld(1, 0);
					}
		        }
			}.start();
		}
		else if(e.getPoint().getX() == canvas.getWidth()-1){
			new Thread() {
		        public void run() {
		        	while(MouseInfo.getPointerInfo().getLocation().getX() == canvas.getWidth()-1){
						moveWorld(-1, 0);
					}
		        }
			}.start();
		}
		if(e.getPoint().getY() == 0){
			new Thread() {
		        public void run() {
		        	while(MouseInfo.getPointerInfo().getLocation().getY() == 0){
						moveWorld(0, 1);
					}
		        }
			}.start();
		}
		else if(e.getPoint().getY() == canvas.getHeight()-1){
			new Thread() {
		        public void run() {
		        	while(MouseInfo.getPointerInfo().getLocation().getY() == canvas.getHeight()-1){
						moveWorld(0, -1);
					}
		        }
			}.start();
		}
	}
	
	public void mousePressed(MouseEvent e) {
		if(onSideBar == true){
			for (Iterator<Entry<Point, BufferedImage>> iterator = sideBar.getImageLocations().entrySet().iterator(); iterator.hasNext();) {
				Entry<Point, BufferedImage> entry = iterator.next();
				Point point = entry.getKey();
				BufferedImage image = entry.getValue();
				if(e.getPoint().getX() < point.getX() + image.getWidth()
					&& e.getPoint().getX() > point.getX()
					&& e.getPoint().getY() < point.getY() + image.getHeight()
					&& e.getPoint().getY() > point.getY()){
					
				}
			}
			return;
		}
		if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()) {
			Point p = e.getPoint();
			selectTile(p, true);
		}
		else if (SwingUtilities.isLeftMouseButton(e)) {
			Point p = e.getPoint();
			selectTile(p, false);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getComponent() == sideBar){
			onSideBar = true;
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if(e.getComponent() == sideBar){
			onSideBar = false;
		}
	}
	
	private void selectTile(Point p, boolean deselect){
		double diffY = p.getY() - canvas.getTranslateY();
		double diffX = p.getX() - (canvas.getTranslateX() + (((canvas.getFloorSize()+1) * canvas.getZoom())/2));
		
		int totalDiffX = (int) Math.ceil((diffX/canvas.getZoom()) - (diffY/(canvas.getZoom()/2)));
		int totalDiffY = (int) Math.ceil((diffX/canvas.getZoom()) + (diffY/(canvas.getZoom()/2)));
		
		int xCoord = (canvas.getFloorSize()/2) + totalDiffX;
		int yCoord = (canvas.getFloorSize()/2) + totalDiffY - 1;
		
		if(deselect){
			canvas.deselectTile(xCoord, yCoord);
			return;
		}
		canvas.selectTile(xCoord, yCoord);
	}
	
	private void moveWorld(int x, int y) {
		canvas.moveWorld(x, y);
	    try {
	        Thread.sleep(10);
	    } 
	    catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	}
}
