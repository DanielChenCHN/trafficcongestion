package map;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@ClassPreamble (
    author = "Daniel Chen",
    date = "01/14/2020",
    currentRevision = 13.3,
    lastModified = "05/11/2020",
    lastModifiedBy = "Daniel Chen"
)
public class DisplayPanel extends JPanel implements Runnable {

  public static final String CAR_IMAGE_FILE_NAME = "Car.png";
  public static final String PEDESTRIAN_IMAGE_FILE_NAME = "Pedestrian.png";
  public static final String BACKGROUND_IMAGE_FILE_NAME = "Background_final.png";

  private final Crossroad crossroad;
  private final ArrayList<Obstacle> obstacles;
  private final boolean record;
  private final int frameNumber;
  private int frameCount;
  private double timeElapsed = 0.;
  private boolean completed;

  private BufferedImage carImage;
  private BufferedImage pedestrianImage;
  private BufferedImage backgroundImage;

  /**
   *
   * @param record whether or not the board is recorded by frame
   * @param frameNumber total number of frames. insignificant if record is set to false
   */
  public DisplayPanel(Crossroad crossroad, boolean record, int frameNumber) {

    setPreferredSize(new Dimension((int)Math.round(Main.PANEL_ALONG * Main.PIXELS_PER_METER),
        (int)Math.round(Main.PANEL_ACROSS * Main.PIXELS_PER_METER)));

    this.crossroad = crossroad;

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
    completed = false;

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

  public void passTime(double factor) {

    crossroad.passTime(factor);
    obstacles.forEach(obstacle -> obstacle.passTime(factor));

    timeElapsed += Main.INTERVAL * factor;

    if(crossroad.completed()) {
      System.out.println(record ? "IMAGES GENERATION COMPLETED." : "SIMULATION COMPLETED.");
      completed = true;
    }

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

      try {
        crossroad.getVehicles().forEach((vehicle) -> drawVehicle(imageBufferGraphics2D, vehicle));
      } catch(Exception ignored) {
      }

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

      try {
        crossroad.getVehicles().forEach(vehicle -> drawVehicle(graphics2D, vehicle));
      } catch(Exception ignored) {
      }

      obstacles.forEach((obstacle) -> drawObstacle(graphics2D, obstacle));

    }

    frameCount++;

    if(frameCount == frameNumber) {
      System.out.println(record ? "IMAGES GENERATION COMPLETED." : "SIMULATION COMPLETED.");
      completed = true;
    }

  }

  /**
   *
   * @param graphics2D the graphics object to draw vehicles on
   * @param vehicle the vehicle to draw
   */
  private void drawVehicle(Graphics2D graphics2D, Vehicle vehicle) {
    drawBody(graphics2D, vehicle, vehicle.getColor(), carImage);
  }

  private void drawObstacle(Graphics2D graphics2D, Obstacle obstacle) {
    drawBody(graphics2D, obstacle, obstacle.getColor(), pedestrianImage);
  }

  /**
   *
   * @param graphics2D graphics2D
   * @param body the rectangle block to be colored
   * @param color the base, supplementary color of the rectangle
   * @param bufferedImage the image to be drawn onto the rectangle
   */
  public void drawBody(Graphics2D graphics2D, Body body, Color color, BufferedImage bufferedImage) {

    AffineTransform originalTransform = graphics2D.getTransform();

    graphics2D.setColor(color);

    graphics2D.rotate(body.getVelocity().getOrientation(),
        (int)Math.round(body.getPosition().getXPosition() * Main.PIXELS_PER_METER),
        (int)Math.round(body.getPosition().getYPosition() * Main.PIXELS_PER_METER));

    graphics2D.drawImage(bufferedImage,
        (int)Math.round(body.getPosition().getXPosition() * Main.PIXELS_PER_METER)
            - (int)Math.round(body.getSize().getAlong() * Main.PIXELS_PER_METER / 2),
        (int)Math.round(body.getPosition().getYPosition() * Main.PIXELS_PER_METER)
            - (int)Math.round(body.getSize().getAcross() * Main.PIXELS_PER_METER / 2),
        (int)Math.round(body.getSize().getAlong() * Main.PIXELS_PER_METER),
        (int)Math.round(body.getSize().getAcross() * Main.PIXELS_PER_METER),
        color,
        null);

    graphics2D.setTransform(originalTransform);
    Toolkit.getDefaultToolkit().sync();

  }

  public void run() {

    long startTime, timeDifference, correctedInterval;

    startTime = System.currentTimeMillis();

    while(!completed) {

      passTime(Frame.factor);
      repaint();

      timeDifference = System.currentTimeMillis() - startTime;
      correctedInterval = (int)Math.round(Main.INTERVAL * Main.MILLISECONDS_PER_SECOND) - timeDifference;

      // I don't think this would happen
      if(correctedInterval < 0) {
        System.out.printf("WARNING: INTERVAL TOO SHORT: %d.\n", correctedInterval);
        correctedInterval = 1;
      }

      try {
        TimeUnit.MILLISECONDS.sleep(correctedInterval);
      } catch (InterruptedException e) {
        String msg = String.format("ERROR RUNNING THREAD: %s", e.getMessage());
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
      }

      startTime = System.currentTimeMillis();

    }

    System.out.println(String.format("TOTAL TIME ELAPSED FOR %d CARS: %.2f SECONDS", crossroad.getTotalNumVehicles(), timeElapsed));

    try {
      TimeUnit.SECONDS.sleep(2);
    } catch(InterruptedException e) {
      e.printStackTrace();
    }

    System.exit(0);

  }

}
