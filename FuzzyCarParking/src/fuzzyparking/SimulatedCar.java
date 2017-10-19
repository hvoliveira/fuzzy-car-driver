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
        
    }

    @Override
    public void run() {
        
    }
    
    @Override
    public void paint(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g;
    	g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
    	g2d.rotate(-phi, this.x, this.y);
    	// draw car
    	g.setColor(Color.black);
        g.drawRect(this.x - this.length/2, this.y - this.width/2, this.length, this.width);
        // draw back wheels
        g.fillRect(this.wheelX2, this.wheelY1, this.wheelLength, this.wheelWidth);
        g.fillRect(this.wheelX2, this.wheelY2, this.wheelLength, this.wheelWidth);
        // draw front wheels        
        AffineTransform at = g2d.getTransform();
        g2d.rotate(theta, wheelX1+wheelLength/2, wheelY1+wheelWidth/2);
        g.fillRect(this.wheelX1, this.wheelY1, this.wheelLength, this.wheelWidth);
        g2d.setTransform(at);
        g2d.rotate(theta, wheelX1+wheelLength/2, wheelY2+wheelWidth/2);
        g.fillRect(this.wheelX1, this.wheelY2, this.wheelLength, this.wheelWidth);
        
        
    }
    
    
}
