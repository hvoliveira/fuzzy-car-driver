package fuzzyparking;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JPanel;

import net.sourceforge.jFuzzyLogic.FIS;

@SuppressWarnings("serial")
public class ParkingLot extends JPanel implements Runnable {

	private int frameWidth = 1000;
	private int frameHeight = 600;
	private int parkingSpacePosX;
	private int parkingSpacePosY;
	private SimulatedCar car;
	private final FIS fis = FIS.load("driver.fcl", true);
	private StringBuilder builder = new StringBuilder();
	private int posError = 0;
	private NeuralDriver nDriver;
	private int driverMode;

	private static ParkingLot singleton;

	public static ParkingLot getInstance() {
		if (singleton == null) {
			singleton = new ParkingLot();
		}
		return singleton;
	}

	private ParkingLot() {
		setCar(new SimulatedCar(40, 300, 50, 25));
		parkingSpacePosX = frameWidth / 2;
		parkingSpacePosY = 50;
		nDriver = new NeuralDriver(0.4, "input/rawData.csv");
		this.setFocusable(false);
		this.driverMode = 0;
	}

	public void gatherData() {
//		builder.append(getPosError() + ",");
		builder.append(car.x + ",");
		builder.append(Math.toDegrees(car.phi)+ ",");
		builder.append(Math.toDegrees(car.theta));
		builder.append("\n");
	}

	public void writeData() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("input/rawData.csv"));
		try {
			writer.write(builder.toString());
		} finally {
			writer.close();
		}
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, frameWidth, frameHeight);
		g.setColor(Color.black);
		g.drawString("Press 'F' for fuzzy driving", 800, 470);
		g.drawString("Press 'SPACE' to halt parking", 800, 490);
		g.drawString("Press 'P' to prepare data", 800, 510);
		g.drawString("Press 'T' to train network", 800, 530);
		g.drawString("Press 'N' for neural driving", 800, 550);

		// draw parking space
		g.setColor(Color.gray);
		g.fillRect(parkingSpacePosX - (car.width + 40) / 2, parkingSpacePosY - (car.length + 40) / 2,
				(int) (car.width + 40), (int) (car.length + 40));
		g.setColor(Color.red);
		g.drawOval(parkingSpacePosX, parkingSpacePosY, 2, 2);
		// draw car
		getCar().paint(g);

	}

	@Override
	public void run() {
		try {
			while (true) {
				switch(driverMode) {
				case 0:
					break;
				case 1:
					setPosError(car.x - parkingSpacePosX);
					getFis().setVariable("positionError", getPosError());
					getFis().setVariable("orientation", Math.toDegrees(car.phi));
					getFis().evaluate();
					car.theta = Math.toRadians(getFis().getVariable("wheelOrientation").getValue());
					gatherData();
					writeData();
					car.move();
					break;
				case 2:
					nDriver.drive();
					car.move();
					break;
				}
				double error = Math.sqrt((car.x - parkingSpacePosX) * (car.x - parkingSpacePosX)
						+ (car.y - parkingSpacePosY) * (car.y - parkingSpacePosY));
				if (error <= 15) {
					Thread.sleep(500);
					car.reset();
				}

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

	public SimulatedCar getCar() {
		return car;
	}

	public void setCar(SimulatedCar car) {
		this.car = car;
	}

	public int getParkingSpacePosY() {
		return parkingSpacePosY;
	}

	public void setParkingSpacePosY(int parkingSpacePosY) {
		this.parkingSpacePosY = parkingSpacePosY;
	}

	public void setParkingSpacePosX(int x) {
		this.parkingSpacePosX = x;

	}

	public void startNetworkTraining() {
		try {
			nDriver.trainNetwork();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void startNeuralDriving() {
		

	}

	public void prepareData() {
		try {
			nDriver.prepareData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FIS getFis() {
		return fis;
	}

	public int getDriverMode() {
		return driverMode;
	}

	public void setDriverMode(int driverMode) {
		this.driverMode = driverMode;
	}

	public int getPosError() {
		return posError;
	}

	public void setPosError(int posError) {
		this.posError = posError;
	}
}
