/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 30th November 2018

    Description: 
        This java class stores camera position for model objects and changes 
        its position by tracking keyboard input commands 
*/

// Current package
package Handlers;

// Import packages
import gmaths.*;

public class Camera {
  
//    Camera movements
    public enum Movement {
        
        NO_MOVEMENT, LEFT, RIGHT, UP, DOWN, FORWARD, BACK
    };

//    Default camera positions
    public static final Vec3 DEFAULT_POSITION = new Vec3(0,0,25);
    public static final Vec3 DEFAULT_POSITION_2 = new Vec3(25,0,0);
    public static final Vec3 DEFAULT_TARGET = new Vec3(0,0,0);
    public static final Vec3 DEFAULT_UP = new Vec3(0,1,0);

//    Directions
    private float yaw;
    private float pitch;
    
//    Input speeds
    public final float KEYBOARD_SPEED = 0.6f;
    public final float MOUSE_SPEED = 1.0f;

//    Camera attributes
    private Vec3 position;
    private Vec3 target;
    private Vec3 up;
    private Vec3 worldUp;
    private Vec3 front;
    private Vec3 right;
    private Mat4 perspective;

//    Initialise object model camera
    public Camera(Vec3 position, Vec3 target, Vec3 up) {
        setupCamera(position, target, up);
    }
    private void setupCamera(Vec3 position, Vec3 target, Vec3 up) {
        this.position = new Vec3(position);
        this.target = new Vec3(target);
        this.up = new Vec3(up);
        
        front = Vec3.subtract(target, position);
        front.normalize();
        up.normalize();
        calculateYawPitch(front);
        worldUp = new Vec3(up);
        updateCameraVectors();
    }

//    Get or set Camara poaitions
    public Vec3 getPosition() {
        return new Vec3(position);
    }
    public void setPosition(Vec3 p) {
        
        setupCamera(p, target, up);
    }
    public void setTarget(Vec3 t) {
        
        setupCamera(position, t, up);
    }

//    Calculate and update camera direction
    private void calculateYawPitch(Vec3 v) {
        
        yaw = (float)Math.atan2(v.z,v.x);
        pitch = (float)Math.asin(v.y);
    }
    public void updateYawPitch(float y, float p) {
        
        yaw += y;
        pitch += p;
        
        if (pitch > 89) {
            pitch = 89;
            
        } else if (pitch < -89) {
            pitch = -89;
        }
        updateFront();
        updateCameraVectors();
    }
    
//    Set camera front view
    private void updateFront() {
        
        double cy, cp, sy, sp;
        
        cy = Math.cos(yaw);
        sy = Math.sin(yaw);
        cp = Math.cos(pitch);
        sp = Math.sin(pitch);
        
        front.x = (float)(cy*cp);
        front.y = (float)(sp);
        front.z = (float)(sy*cp);
        front.normalize();
        
        target = Vec3.add(position,front);
    }

//    Set camera's right and upward position vectors
    private void updateCameraVectors() {  
        
        right = Vec3.crossProduct(front, worldUp);
        right.normalize();
        
        up = Vec3.crossProduct(right, front);
        up.normalize();
    }
    
//    Camera view and perspective matrix
    public Mat4 getViewMatrix() {
        target = Vec3.add(position, front);
        
        return Mat4Transform.lookAt(position, target, up);
    }
    public void setPerspectiveMatrix(Mat4 m) {
        
        perspective = m;
    }
    public Mat4 getPerspectiveMatrix() {
        return perspective;
    }

//    Track keyboard input to change camera's position
    public void keyboardInput(Movement movement) {
        
        switch (movement) {
            case NO_MOVEMENT: break;
            case LEFT: position.add(Vec3.multiply(right, -KEYBOARD_SPEED)); break;
            case RIGHT: position.add(Vec3.multiply(right, KEYBOARD_SPEED)); break;
            case UP: position.add(Vec3.multiply(up, KEYBOARD_SPEED)); break;
            case DOWN: position.add(Vec3.multiply(up, -KEYBOARD_SPEED)); break;
            case FORWARD: position.add(Vec3.multiply(front, KEYBOARD_SPEED)); break;
            case BACK: position.add(Vec3.multiply(front, -KEYBOARD_SPEED)); break;
        }
    }
 
}