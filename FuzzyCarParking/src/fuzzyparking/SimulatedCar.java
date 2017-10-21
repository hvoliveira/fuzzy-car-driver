package fuzzyparking;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

public class SimulatedCar extends Car {
	
	private int wheelX1;
	private int wheelX2;
	private int wheelY1;
	private int wheelY2;
	private int wheelWidth;
	private int wheelLength;
    private int counter;
	
    public SimulatedCar(int x, int y, int length, int width) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.width = width;
        this.wheelLength = 20;
        this.wheelWidth = 4;
        updateWheels();
        this.phi = 0.0;
        this.theta = 0.0;
        this.counter = 1;
    }
    
    public void updateWheels() {
    	// back x
        this.wheelX1 = this.x - this.length/2;
        // front x
        this.wheelX2 = this.x + this.length/2 - this.wheelLength;
        // left y
        this.wheelY1 = this.y - this.width/2 - this.wheelWidth - 1;
        // right y
        this.wheelY2 = this.y + this.width/2 + 2;
    }

    @Override
    public void move() {
    	
    	if(this.y < ParkingLot.getInstance().getParkingSpacePosY()) {
	    	this.phi = this.phi + this.theta;
	        this.x += (int) (15*Math.cos(-this.phi));
	        this.y -= (int) (15*Math.sin(-this.phi));
	        this.updateWheels();
	        this.fixCoordinates();
	    } else {
	    	this.phi = this.phi + this.theta;
	        this.x += (int) (15*Math.cos(-this.phi));
	        this.y -= (int) (15*Math.sin(this.phi));
	        this.updateWheels();
	        this.fixCoordinates();
    	}
        
    }

    public void reset() {
    	int accumulator = counter*100;
    	if(counter <= 10) {
    		this.x = accumulator;
    		accumulator += accumulator;
    		this.y = 50;
    	} else if (counter == 11) {
    		accumulator = 100;
    		this.x = 40;
    		this.y = 700;
    	} else if(11 < counter || counter <= 20) {
    		this.x = accumulator;
    		accumulator += accumulator;
    		this.y = 700;
    	} else {
    		counter = 0;
    	}
    	
    	counter++;
    }
    
    @Override
    public void paint(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g;
    	g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
    	
    	g2d.rotate(-phi*ParkingSpaceYOrientation(), this.x, this.y);
    	
    	// draw car
    	g.setColor(Color.black);
        g.drawRect(this.x - this.length/2, this.y - this.width/2, this.length, this.width);
        // draw back wheels
        g.fillRect(this.wheelX2, this.wheelY1, this.wheelLength, this.wheelWidth);
        g.fillRect(this.wheelX2, this.wheelY2, this.wheelLength, this.wheelWidth);
        // draw front wheels        
        AffineTransform at = g2d.getTransform();
        g2d.rotate(theta*ParkingSpaceYOrientation(), wheelX1+wheelLength/2, wheelY1+wheelWidth/2);
        g.fillRect(this.wheelX1, this.wheelY1, this.wheelLength, this.wheelWidth);
        g2d.setTransform(at);
        g2d.rotate(theta*ParkingSpaceYOrientation(), wheelX1+wheelLength/2, wheelY2+wheelWidth/2);
        g.fillRect(this.wheelX1, this.wheelY2, this.wheelLength, this.wheelWidth);
        
    }
    
    public int ParkingSpaceYOrientation() {
    	if(this.y < ParkingLot.getInstance().getParkingSpacePosY()) {
    		return -1;
    	}
    	return 1;
    }
    
    
}
