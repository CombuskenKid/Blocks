package view;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseListener implements MouseWheelListener, MouseMotionListener{

	private GameCanvas canvas;
	private Point mouseLoc = null;
	
	public MouseListener(GameCanvas canvas){
		this.canvas = canvas;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
	    if (notches < 0) {
	    	canvas.zoomIn();
	    } else {
	    	canvas.zoomOut();
	    }
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		double dragAmountX = 0;
		double dragAmountY = 0;
		if(mouseLoc != null){
			dragAmountX = e.getPoint().getX() - mouseLoc.getX();
			dragAmountY = e.getPoint().getY() - mouseLoc.getY();
		}
		mouseLoc = e.getPoint();
		canvas.moveWorld(dragAmountX, dragAmountY);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseLoc = null;
	}
}
