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
            case KeyEvent.VK_P:
            	ParkingLot.getInstance().prepareData();
            	break;
            case KeyEvent.VK_T:
            	ParkingLot.getInstance().startNetworkTraining();
            	break;
            case KeyEvent.VK_S:
            	ParkingLot.getInstance().startNeuralDriving();
            	break;
            case KeyEvent.VK_SPACE:
            	ParkingLot.getInstance().setDriverMode(0);
            	break;
            case KeyEvent.VK_N:
            	ParkingLot.getInstance().setDriverMode(2);
            	break;
            case KeyEvent.VK_F:
            	ParkingLot.getInstance().setDriverMode(1);
            	break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

