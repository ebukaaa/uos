
/*
    Name: Ebuka Isiadinso
    Module: COM160
    Description: Class file containing all relevant attributes of robot
*/

package robotproject;
import ShefRobot.*;
import ShefRobot.Robot;

/**
 *
 * @author ebuka
 */

// PARENT CLASS

public class Behaviour {
    
    // Variables
    public Robot myRobot;
    public Motor leftMotor;
    public Motor rightMotor;
    
    public final int WALKING_SPEED; // Constants
    public final int WALKING_TIME;
    public final int TURNING_SPEED;
    
    // Constructors
    public Behaviour(Robot robot, Motor left, Motor right, final int WS, final int WT, final int TS) {
        
        myRobot = robot;
        leftMotor = left;
        rightMotor = right;
        
        WALKING_SPEED = WS;
        WALKING_TIME = WT;
        TURNING_SPEED = TS;
    }
    
    // Stop robot
    public void stop() {
        
        leftMotor.stop();
        rightMotor.stop();
    }
    
    // Move forward
    public void forward() {
        
        leftMotor.setSpeed(WALKING_SPEED);  // change speed
        rightMotor.setSpeed(WALKING_SPEED);
        
        leftMotor.forward();
        rightMotor.forward();
    }
    
    // Turn right
    public void right() {
        
        leftMotor.rotate(180);  // leftMotor - forward
        rightMotor.rotate(-180); // rightMotor - backward
    }
    
    // Turn left
    public void left() {
        
        leftMotor.rotate(-180);  // leftMotor - backward
        rightMotor.rotate(180); // rightMotor - forward
    }
    
    // Do nothing for a period
    public void doNothing() {
        
        myRobot.sleep(WALKING_TIME);    // delay
    }
    
    // Rotate on green
    public void rotateOnGreenColor() {
        
        leftMotor.setSpeed(WALKING_SPEED);  // change speed
        rightMotor.setSpeed(WALKING_SPEED);
        
        // Rotate
        leftMotor.forward();
        rightMotor.backward();
        
    }
    
    // End robot
    public void close() {
        
        myRobot.close();
    }
}
