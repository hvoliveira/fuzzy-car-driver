package fuzzyparking;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import net.sourceforge.jFuzzyLogic.FIS;

public class ParkingLot extends JPanel implements Runnable {

    private int frameWidth = 1400;
    private int frameHeight = 800;
    private int parkingSpacePosX;
    private int parkingSpacePosY;
    private Car car;
    private final FIS fis = FIS.load("driver.fcl", true);
    
    private static ParkingLot singleton;

    public static ParkingLot getInstance() {
        if (singleton == null) {
            singleton = new ParkingLot();
        }
        return singleton;
    }

    private ParkingLot() {
        setCar(new SimulatedCar(300, 300, 80, 40));
        parkingSpacePosX = frameWidth/2 + 50;
        parkingSpacePosY = 0;
        this.setFocusable(false);
    }

    public void reset() {
        Thread t = new Thread(getCar());
        t.start();
    }
    

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, frameWidth, frameHeight);
        
        // draw parking space
        g.setColor(Color.gray);
        g.fillRect(parkingSpacePosX - (car.width + 40)/2, parkingSpacePosY, 
        		(int) (car.width + 40), (int) (car.length + 40));
        
        // draw car
        getCar().paint(g);       
        
    }
    
    @Override
    public void run() {
        reset();
        try {
            while (true) {
                fis.setVariable("position", ((car.x-parkingSpacePosX)*100/frameWidth));
                fis.setVariable("position", (car.x-parkingSpacePosX));
                fis.setVariable("orientation", Math.toDegrees(car.phi));
                fis.evaluate();
                car.theta = Math.toRadians(fis.getVariable("wheelOrientation").getValue());
            	// Repaints scene
                this.repaint();
                // Sleeps 30ms
                Thread.sleep(30);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}
}
