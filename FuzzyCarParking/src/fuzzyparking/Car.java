package fuzzyparking;

import java.awt.Graphics;

public abstract class Car {
    
    int length; // car length
    int width; // car width
    
    double theta; // angle between central axis and front wheels
    double phi; // angle between central axis and parking spot back boundary
    
    int x; // car x position
    int y; // car y position
    
    public abstract void move();
    
    public abstract void paint(Graphics g);	
    
    public void fixCoordinates() {
        if ((x - width/2) > ParkingLot.getInstance().getFrameWidth()) {
            x = 0;
        }
        if ((x + width/2) < 0) {
            x = ParkingLot.getInstance().getFrameWidth();
        }
        if ((y - length/2) > ParkingLot.getInstance().getFrameHeight()) {
            y = 0;
        }
        if ((y + length/2) < 0) {
            y = ParkingLot.getInstance().getFrameHeight();
        }
    }
    
}
