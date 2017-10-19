package fuzzyparking;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardListener implements KeyListener {

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                ParkingLot.getInstance().getCar().theta += 0.05;
                break;
            case KeyEvent.VK_LEFT:
                ParkingLot.getInstance().getCar().theta -= 0.05;
                break;
            case KeyEvent.VK_UP:
            	SimulatedCar car = (SimulatedCar) ParkingLot.getInstance().getCar();
                car.phi = car.phi - car.theta;
                car.x += (int) -(15*Math.cos(-car.phi));
                car.y -= (int) -(15*Math.sin(car.phi));
                car.updateWheels();
                car.fixCoordinates();
                ParkingLot.getInstance().setCar(car);
                break;
            case KeyEvent.VK_DOWN:
            	car = (SimulatedCar) ParkingLot.getInstance().getCar();
                car.phi = car.phi + car.theta;
                car.x += (int) (15*Math.cos(-car.phi));
                car.y -= (int) (15*Math.sin(car.phi));
                car.updateWheels();
                car.fixCoordinates();
                ParkingLot.getInstance().setCar(car);
            	break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

