
/*
    Name: Ebuka Isiadinso
    Module: COM160
    Description: Class file containing the necessary tasks for robot
*/

package robotproject;
import ShefRobot.*;

/**
 *
 * @author ebuka
 */

// SUB CLASS

public class Actions extends Behaviour {
    
    // Variables
    private Speaker speaker;
    private ColorSensor color;
    private ColorSensor.Color dotColor;
    
    // Constructors
    public Actions(Speaker sound, ColorSensor c, ColorSensor.Color dC, Robot robot, Motor left, Motor right, final int WS, final int WT, final int TS) {
        
        // Behaviour class
        super(robot, left, right, WS, WT, TS);
        
        // Action class
        speaker = sound;
        color = c;
        dotColor = dC;
    }
    
    // For the while - loop
    public boolean isNotDotColors() {
        
        return (color.getColor() != color.getColor().RED) && (color.getColor() != color.getColor().YELLOW) && (color.getColor() != color.getColor().GREEN);
    }
    
    // Move forward if on white 
    public void isWhite() {
        
        while(color.getColor() == color.getColor().WHITE) {
            
            forward();
        }
    }
    
    // Turn right if black is spotted
    public void isBlack() {
        
        if(color.getColor() == color.getColor().BLACK) {
            
            forward();
            myRobot.sleep(200);
            
            stop();
            
            speaker.playTone(500, 200);    // play sounds
            
            right();
        }
    }
    
    // Follow balck line
    public void followBlackLine() {
        
        while(isNotDotColors()) {
            
            // On black line
            if(color.getColor() == color.getColor().BLACK) {               

                switch(dotColor) {

                    // dotColor red ?
                    case RED:
                        leftMotor.setSpeed(TURNING_SPEED);   
                        leftMotor.forward();    // right
                        rightMotor.stop(); 
                        
                        rightMotor.setSpeed(TURNING_SPEED);  
                        rightMotor.forward();   // left
                        leftMotor.stop(); 
                        
                        break;

                        // dotColor yellow/no color ?
                    case YELLOW: case NONE:
                        rightMotor.setSpeed(TURNING_SPEED);  
                        rightMotor.forward();   // left
                        leftMotor.stop(); 
                        
                        leftMotor.setSpeed(TURNING_SPEED);   
                        leftMotor.forward();    // right
                        rightMotor.stop();
                        
                        break;
                }
            }

            // On white
            else if(color.getColor() == color.getColor().WHITE){

                switch(dotColor) {

                    case RED:
                        leftMotor.setSpeed(TURNING_SPEED);
                        leftMotor.forward();    // right 
                        rightMotor.stop(); 
                        break;

                    case YELLOW: case NONE:
                        rightMotor.setSpeed(TURNING_SPEED);
                        rightMotor.forward();   // left
                        leftMotor.stop(); 
                        break;
                }
            }
            
        }
    }
    
    // Turn right if on yellow or left if on red
    public void isRed_isYellow() {
        
        while(color.getColor() != color.getColor().GREEN) {
            
            if(color.getColor() == color.getColor().RED) {
            
                // Enter the circle
                forward();
                doNothing();
                stop();

                // dotColor is red
                dotColor = ColorSensor.Color.RED;
                speaker.playTone(1000, 200);

                // Turn left
                left();

                forward();
                doNothing();
                followBlackLine();
            }

            // On yellow
            else if(color.getColor() == color.getColor().YELLOW) {

                // Enter the circle
                forward();
                //doNothing();
                myRobot.sleep(1400);
                stop();

                // dotColor is yellow
                dotColor = ColorSensor.Color.YELLOW;
                speaker.playTone(1000, 200);

                // Turn right
                right();

                forward();
                //doNothing();
                //myRobot.sleep(1000);
                
                followBlackLine();
            }
        }
    }
    
    // Rotate 360 degrees if on green and stop
    public void isGreen() {
        
        // On green
        if(color.getColor() == color.getColor().GREEN) {
            
            forward();
            doNothing();    // for 2 secs
            stop();

            speaker.playTone(1000, 200);

            // Do the dance thing
            rotateOnGreenColor();
            
            doNothing();    // for 2 secs
            
            stop();

            close();
        }
    }
}
