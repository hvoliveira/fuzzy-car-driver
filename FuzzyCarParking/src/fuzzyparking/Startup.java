package fuzzyparking;	

import java.io.IOException;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Startup extends JFrame {

	public static void main(String[] args) {
		
		Startup game = new Startup();
		game.setVisible(true);
		game.startParking();
                game.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public Startup() {
		this.addKeyListener(new KeyboardListener());
		this.addMouseListener(new MouseClickListener());
		ParkingLot lot = ParkingLot.getInstance();
		add(lot);
		setSize(lot.getFrameWidth(), lot.getFrameHeight());
		setTitle("Fuzzy Parking");
	}
	
	public void startParking() {
		Thread t = new Thread(ParkingLot.getInstance());
		t.start();
	}
	
	
}
