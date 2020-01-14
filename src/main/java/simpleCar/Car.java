/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleCar;

import java.awt.Color;
import java.util.ArrayList;

@ClassPreamble (
        author = "Daniel Chen",
        date = "01/14/2020",
        currentRevision = 1,
        lastModified = "01/14/2020",
        lastModifiedBy = "Daniel Chen"
)
public class Car extends Vehicle {
    
    public static final Color COLOR = new Color(227, 86, 77);
    public static final double MAX_SPEED = 60;
    public static final double MAX_ACCELERATION = 8;
    
    public static final double WIDTH = 4.5;
    public static final double HEIGHT = 1.8;
    public static final double XPOS = 0;
    public static final double YPOS = 0;
    public static final double SPEED = 20;
    public static final double ORIENTATION = Math.toRadians(45);

    public static Car[] getCarsArray(int numCars) {
        
        Car[] cars = new Car[numCars];
        
        for(int i=0; i < numCars; i++) {
            cars[i] = new Car(new Position(Math.random() * Main.PANEL_WIDTH, Math.random() * Main.FRAME_HEIGHT),
                    new Velocity(Math.random() * MAX_SPEED / 2, Math.random() * 2 * Math.PI));
        }
        
        return cars;
    }
    
    public static ArrayList<Car> getCarsList(int numCars) {
        
        ArrayList<Car> cars = new ArrayList<Car>();
        
        for(int i=0; i < numCars; i++) {
            cars.add(new Car(new Position(Math.random() * Main.PANEL_WIDTH, Math.random() * Main.FRAME_HEIGHT),
                    new Velocity(Math.random() * MAX_SPEED / 2, Math.random() * 2 * Math.PI)));
        }
        
        return cars;
    }
    
    public Car() {
        this(new Size(WIDTH, HEIGHT), new Position(XPOS, YPOS), new Velocity(SPEED, ORIENTATION));
    }
    
    public Car(Position position, Velocity velocity) {
        this(new Size(WIDTH, HEIGHT), position, velocity);
    }
    
    /**
     * The Car class inherits the Vehicle class and is parallel to the Bus class
     * 
     * @param size the size of the car
     * @param position the position of the car
     * @param velocity the velocity of the car
     */
    private Car(Size size, Position position, Velocity velocity) {
        super(size, position, velocity);
    }
    
    public Color getColor() {
        return COLOR;
    }

    public String toString() {
        return String.format("Car:\tSize: %.2f * %.2f;\tPos: (%.2f, %.2f);\tVelocity: %.2f at %.2f.",
                this.getWidth(), this.getHeight(),
                this.getXPosition(), this.getYPosition(),
                this.getSpeed(), this.getOrientation());
    }

    public Acceleration getAcceleration() {
        return new Acceleration(MAX_ACCELERATION, getVelocity().getOrientation());
    }

}
