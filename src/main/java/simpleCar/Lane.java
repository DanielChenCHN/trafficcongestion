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
        date = "02/14/2020",
        currentRevision = 1,
        lastModified = "02/14/2020",
        lastModifiedBy = "Daniel Chen"
)
public class Lane {

    public static final Color COLOR = new Color(92, 92, 92);
    
    private Size size;
    private Position position;
    private Velocity velocity;
    
    public Lane(Size size, Position position, Velocity velocity) {
        
        this.size = size;
        this.position = position;
        this.velocity = velocity;
        
    }
    
    public Size getSize() {
        return size;
    }

    public Position getPosition() {
        return position;
    }
    
    public ArrayList<Position> getCornerPositions() {
        
        ArrayList<Position> cornerPositions = new ArrayList<Position>();
        
        double halfDiagonal = Math.sqrt(Math.pow(size.getWidth(), 2) + Math.pow(size.getHeight(), 2)) / 2;
        double angle1 = velocity.getOrientation() + Math.atan2(size.getHeight(), size.getWidth());
        double angle2 = velocity.getOrientation() - Math.atan2(size.getHeight(), size.getWidth());
        
//        System.out.println(halfDiagonal);
//        System.out.println(angle / Math.PI);
        
        cornerPositions.add(new Position(
                position.getXPosition() + halfDiagonal * Math.cos(angle1),
                position.getYPosition() + halfDiagonal * Math.sin(angle1)));
        
        cornerPositions.add(new Position(
                position.getXPosition() - halfDiagonal * Math.cos(angle1),
                position.getYPosition() - halfDiagonal * Math.sin(angle1)));
        
        cornerPositions.add(new Position(
                position.getXPosition() + halfDiagonal * Math.cos(angle2),
                position.getYPosition() + halfDiagonal * Math.sin(angle2)));
        
        cornerPositions.add(new Position(
                position.getXPosition() - halfDiagonal * Math.cos(angle2),
                position.getYPosition() - halfDiagonal * Math.sin(angle2)));
        
        return cornerPositions;
    }

    public Velocity getVelocity() {
        return velocity;
    }

    public Color getColor() {
        return COLOR;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
    }
    
    public boolean contains(Position otherPosition) {
        
        ArrayList<Position> cornerPositions = getCornerPositions();
        
        double maxLength = 0.;
        int maxIndex = 0;
        
        for(int i=1; i < 4; i++) {
            
            if(cornerPositions.get(0).distanceTo(cornerPositions.get(i)) > maxLength) {
                maxLength = cornerPositions.get(0).distanceTo(cornerPositions.get(i));
                maxIndex = i;
            }
            
        }
        
        int index0;
        int index1;
        int index2;
        int index3;
        
        if(maxIndex == 1) {
            index0 = 0;
            index1 = 2;
            index2 = 1;
            index3 = 3;
        } else if(maxIndex == 2) {
            index0 = 0;
            index1 = 1;
            index2 = 2;
            index3 = 3;
        } else {
            index0 = 0;
            index1 = 1;
            index2 = 3;
            index3 = 2;
        }
        
        double sumDistances = otherPosition.distanceTo(cornerPositions.get(index0), cornerPositions.get(index1))
                + otherPosition.distanceTo(cornerPositions.get(index1), cornerPositions.get(index2))
                + otherPosition.distanceTo(cornerPositions.get(index2), cornerPositions.get(index3))
                + otherPosition.distanceTo(cornerPositions.get(index3), cornerPositions.get(index0));
        
        if(sumDistances)
        
    }
    
}