package map;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@ClassPreamble (
        author = "Daniel Chen",
        date = "01/14/2020",
        currentRevision = 9,
        lastModified = "04/23/2020",
        lastModifiedBy = "Daniel Chen"
)
public class DisplayPanel extends JPanel implements Runnable {

    public static boolean vehicleOnBoard(Vehicle vehicle) {
        return vehicle.getPosition().getXPosition() >= 0
                && vehicle.getPosition().getXPosition() <= Main.PANEL_ALONG
                && vehicle.getPosition().getYPosition() >= 0
                && vehicle.getPosition().getYPosition() <= Main.PANEL_ACROSS;
    }
    
    public static final String CAR_IMAGE_FILE_NAME = "Car.png";
    public static final String PEDESTRIAN_IMAGE_FILE_NAME = "Pedestrian.png";
    public static final String BACKGROUND_IMAGE_FILE_NAME = "Background_final.png";

    private final ArrayList<Vehicle> vehicles;
    private final ArrayList<Obstacle> obstacles;
    private final boolean record;
    private final int frameNumber;
    private int frameCount;
    
    private BufferedImage carImage;
    private BufferedImage pedestrianImage;
    private BufferedImage backgroundImage;
    
    /**
     * 
     * @param record whether or not the board is recorded by frame
     * @param frameNumber total number of frames. insignificant if record is set to false
     */
    public DisplayPanel(boolean record, int frameNumber) {

        setPreferredSize(new Dimension((int)Math.round(Main.PANEL_ALONG * Main.PIXELS_PER_METER),
                (int)Math.round(Main.PANEL_ACROSS * Main.PIXELS_PER_METER)));
        
        vehicles = new ArrayList<>();
        obstacles = new ArrayList<>();
        
        carImage = null;
        
        try {
            carImage = ImageIO.read(new File(Main.ASSETS_ADDRESS + CAR_IMAGE_FILE_NAME));
        } catch(IOException e) {
            
            System.out.println("Car Image Not Found");
            System.exit(0);
            
        }
        
        pedestrianImage = null;
        
        try {
            pedestrianImage = ImageIO.read(new File(Main.ASSETS_ADDRESS + PEDESTRIAN_IMAGE_FILE_NAME));
        } catch(IOException e) {
            
            System.out.println("Pedestrian Image Not Found");
            System.exit(0);
            
        }
        
        backgroundImage = null;
        
        try {
            backgroundImage = ImageIO.read(new File(Main.ASSETS_ADDRESS + BACKGROUND_IMAGE_FILE_NAME));
        } catch(IOException e) {
            
            System.out.println("Background Image Not Found");
            System.exit(0);
            
        }
        
        this.record = record;
        this.frameNumber = frameNumber;
        
        frameCount = 0;

    }
    
    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }
    
    public void addVehicles(Vehicle[] vehicles) {
        Collections.addAll(this.vehicles, vehicles);
    }
    
    public void addVehicles(ArrayList<Vehicle> vehicles) {
        this.vehicles.addAll(vehicles);
    }
    
    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
    }
    
    public void addObstacles(Obstacle[] obstacles) {
        this.obstacles.addAll(Arrays.asList(obstacles));
    }
    
    public void addObstacles(ArrayList<Obstacle> obstacles) {
        this.obstacles.addAll(obstacles);
    }
    
    public void passTime() {
        vehicles.forEach(Vehicle::passTime);
        obstacles.forEach(Body::passTime);
    }
    
    public void addNotify() {
        
        super.addNotify();

        Thread animator = new Thread(this);
        animator.start();

    }
    
    public void paintComponent(Graphics graphics) {
        
        super.paintComponent(graphics);
        
        Graphics2D graphics2D = (Graphics2D)graphics;
        
        if(record) {
            
            BufferedImage imageBuffer = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D imageBufferGraphics2D = imageBuffer.createGraphics();
            
            imageBufferGraphics2D.drawImage(backgroundImage, 0, 0,
                (int)Math.round(Main.PANEL_ALONG * Main.PIXELS_PER_METER),
                (int)Math.round(Main.PANEL_ACROSS * Main.PIXELS_PER_METER), this);

            vehicles.forEach((vehicle) -> drawVehicle(imageBufferGraphics2D, vehicle));
            obstacles.forEach((obstacle) -> drawObstacle(imageBufferGraphics2D, obstacle));
        
            graphics2D.drawImage(imageBuffer, 0, 0, this);
        
            File file = new File(Main.OUTPUT_ADDRESS
                    + String.format("FRAME_%0" + Integer.toString(frameNumber).length() + "d.png", frameCount));
            
            try {
                ImageIO.write(imageBuffer, "PNG", file);
            } catch (IOException e) {
                System.out.printf("ERROR WRITING IMAGE: %s", e.getMessage());
                System.exit(0);
            }
            
        } else {
            
            graphics2D.drawImage(backgroundImage, 0, 0,
                (int)Math.round(Main.PANEL_ALONG * Main.PIXELS_PER_METER),
                (int)Math.round(Main.PANEL_ACROSS * Main.PIXELS_PER_METER), this);
            
            vehicles.forEach((vehicle) -> {if(vehicleOnBoard(vehicle)) drawVehicle(graphics2D, vehicle);});
            obstacles.forEach((obstacle) -> drawObstacle(graphics2D, obstacle));

//            graphics2D.drawImage(backgroundImage, 300, 300,
//                    (int)Math.round(Main.PANEL_ALONG * Main.PIXELS_PER_METER),
//                    (int)Math.round(Main.PANEL_ACROSS * Main.PIXELS_PER_METER), this);

        }
        
        frameCount++;
        
        if(frameCount == frameNumber) {
            System.out.println(record ? "IMAGES GENERATION COMPLETED." : "SIMULATION COMPLETED.");
            System.exit(0);
        }
        
    }
    
    /**
     * 
     * @param graphics2D the graphics object to draw vehicles on
     * @param vehicle the vehicle to draw
     */
    private void drawVehicle(Graphics2D graphics2D, Vehicle vehicle) {
        
        AffineTransform originalTransform = graphics2D.getTransform();
        
        graphics2D.setColor(vehicle.getColor());
        
        graphics2D.rotate(vehicle.getVelocity().getOrientation(),
                (int)Math.round(vehicle.getPosition().getXPosition() * Main.PIXELS_PER_METER),
                (int)Math.round(vehicle.getPosition().getYPosition() * Main.PIXELS_PER_METER));
        
//        graphics2D.fillRect(
//                (int)Math.round(vehicle.getPosition().getXPosition() * Main.PIXELS_PER_METER)
//                        - (int)Math.round(vehicle.getSize().getAlong() * Main.PIXELS_PER_METER / 2),
//                (int)Math.round(vehicle.getPosition().getYPosition() * Main.PIXELS_PER_METER
//                        - (int)Math.round(vehicle.getSize().getAcross() * Main.PIXELS_PER_METER / 2)),
//                (int)Math.round(vehicle.getSize().getAlong() * Main.PIXELS_PER_METER),
//                (int)Math.round(vehicle.getSize().getAcross() * Main.PIXELS_PER_METER));
        
        graphics2D.drawImage(carImage,
                (int)Math.round(vehicle.getPosition().getXPosition() * Main.PIXELS_PER_METER)
                        - (int)Math.round(vehicle.getSize().getAlong() * Main.PIXELS_PER_METER / 2),
                (int)Math.round(vehicle.getPosition().getYPosition() * Main.PIXELS_PER_METER)
                        - (int)Math.round(vehicle.getSize().getAcross() * Main.PIXELS_PER_METER / 2),
                (int)Math.round(vehicle.getSize().getAlong() * Main.PIXELS_PER_METER),
                (int)Math.round(vehicle.getSize().getAcross() * Main.PIXELS_PER_METER),
                vehicle.getColor(),
                null);
        
        graphics2D.setTransform(originalTransform);
        Toolkit.getDefaultToolkit().sync();

    }
    
    private void drawObstacle(Graphics2D graphics2D, Obstacle obstacle) {
        
        AffineTransform originalTransform = graphics2D.getTransform();
        
        graphics2D.setColor(obstacle.getColor());
        
        graphics2D.rotate(obstacle.getVelocity().getOrientation(),
                (int)Math.round(obstacle.getPosition().getXPosition() * Main.PIXELS_PER_METER),
                (int)Math.round(obstacle.getPosition().getYPosition() * Main.PIXELS_PER_METER));
        
//        graphics2D.fillRect(
//                (int)Math.round(obstacle.getPosition().getXPosition() * Main.PIXELS_PER_METER)
//                        - (int)Math.round(obstacle.getSize().getAlong() * Main.PIXELS_PER_METER / 2),
//                (int)Math.round(obstacle.getPosition().getYPosition() * Main.PIXELS_PER_METER
//                        - (int)Math.round(obstacle.getSize().getAcross() * Main.PIXELS_PER_METER / 2)),
//                (int)Math.round(obstacle.getSize().getAlong() * Main.PIXELS_PER_METER),
//                (int)Math.round(obstacle.getSize().getAcross() * Main.PIXELS_PER_METER));
        
        graphics2D.drawImage(pedestrianImage,
                (int)Math.round(obstacle.getPosition().getXPosition() * Main.PIXELS_PER_METER)
                        - (int)Math.round(obstacle.getSize().getAlong() * Main.PIXELS_PER_METER / 2),
                (int)Math.round(obstacle.getPosition().getYPosition() * Main.PIXELS_PER_METER)
                        - (int)Math.round(obstacle.getSize().getAcross() * Main.PIXELS_PER_METER / 2),
                (int)Math.round(obstacle.getSize().getAlong() * Main.PIXELS_PER_METER),
                (int)Math.round(obstacle.getSize().getAcross() * Main.PIXELS_PER_METER),
                obstacle.getColor(),
                null);
        
        // Color could be passed as arg 5 to specify the color of transparent pixels in the image.
        
        graphics2D.setTransform(originalTransform);
        Toolkit.getDefaultToolkit().sync();

    }
    
    public void run() {
        
        long startTime, timeDifference, correctedInterval;

        startTime = System.currentTimeMillis();

        while(true) {

            passTime();
            repaint();
            
            timeDifference = System.currentTimeMillis() - startTime;
            correctedInterval = (int)Math.round(Main.INTERVAL * Main.MILLISECONDS_PER_SECOND) - timeDifference;

            // I don't think this would happen
            if(correctedInterval < 0) {
                System.out.printf("WARNING: INTERVAL TOO SHORT: %d.\n", correctedInterval);
                correctedInterval = 1;
            }

            try {
                Thread.sleep(correctedInterval);
            } catch (InterruptedException e) {
                String msg = String.format("ERROR RUNNING THREAD: %s", e.getMessage());
                JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            startTime = System.currentTimeMillis();
            
        }
    }
    
}