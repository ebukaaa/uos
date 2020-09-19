/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 30th November 2018

    Description: 
        This java class extends mouse motion adapter class to implements 2 mouse 
        commands namely mouse dragged and mouse press. The camera uses mouse
        last point to calculate camera position when mouse is dragged. Once the
        mouse is clicked, the last position is set to update camera position
*/

// Current package
package Inputs;

// Import packages
import Handlers.*;
import java.awt.*;
import java.awt.event.*;

public class MouseInput extends MouseMotionAdapter {
    
//    Camera position 
    private Camera camera;
    private Point lastpoint;
    float xPosition, yPosition;

//    Stores camera property
    public MouseInput(Camera camera) {
        this.camera = camera;
    }

    /**
    * mouse is used to control camera position
    *
    * @param event  instance of MouseEvent
    */    
    @Override
    public void mouseDragged(MouseEvent event) {
        
        Point currentPoint = event.getPoint();
        
        float sensitivity = 0.001f;
        xPosition = (float) (currentPoint.x - lastpoint.x) * sensitivity;
        yPosition = (float) (currentPoint.y - lastpoint.y) * sensitivity;
        
        if (event.getModifiers() == MouseEvent.BUTTON1_MASK) {
            camera.updateYawPitch(xPosition, -yPosition);
        }
        lastpoint = currentPoint;
    }
    @Override
    public void mouseMoved(MouseEvent event) {   
        
        lastpoint = event.getPoint(); 
    }
}
