
/*
    Name: Ebuka Isiadinso
    Module: COM160
    Description: This program allows a robot to move forward
                 till a black line is detected and turns it right.
                 The robot then moves forward on the black line
                 till a circle colour is detected.
                 It turns right if on yellow and left when on red,
                 following the black line till it reaches a green colour.
                 The program makes the robot performs a spin on the green
                 colour before putting it to a full stop.
*/

package robotproject;
import ShefRobot.*;
import ShefRobot.Robot;
/**
 *
 * @author ebuka
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Constants variables
        final int TURNING_SPEED = 75;
        final int WALKING_SPEED = 100;
        final int WALKING_TIME = 2000;

        //Create a robot object to use and connect to it
        Robot myRobot = new Robot();
        
        // Components
        
        Motor leftMotor = myRobot.getLargeMotor(Motor.Port.A);
        Motor rightMotor = myRobot.getLargeMotor(Motor.Port.B);
        
        Speaker speaker = myRobot.getSpeaker();
        
        ColorSensor color = myRobot.getColorSensor(Sensor.Port.S3);
        ColorSensor.Color dotColor = ColorSensor.Color.NONE;
        
        //boolean isGreenColor = false;   // check this
        // try while(isGreenColor != true)
        
        // Objects
        
        // Behaviour object
        Behaviour robot = new Behaviour(myRobot, leftMotor, rightMotor, WALKING_SPEED, WALKING_TIME, TURNING_SPEED);
        
        // Actions object
        Actions robotDoes = new Actions(speaker, color, dotColor, myRobot, leftMotor, rightMotor, WALKING_SPEED, WALKING_TIME, TURNING_SPEED);
        
        // START
        
        // Initially, robot is set to not move
        robot.stop();
        
        // Moves forward on white
        robotDoes.isWhite();

        // Turns right if black is spotted
        robotDoes.isBlack();
        
        // Moves forward
        robot.forward();
        robot.doNothing();  // for 2 secs
        
        robot.forward();
        robot.doNothing();  // for 2 secs

        // Follows black line
        
        // Turns right if on yellow or left if on red
        robotDoes.isRed_isYellow();

        // Follows black line

        // Rotates 360 degrees if on green and stops
        robotDoes.isGreen();
        
        // END
        
        robot.close();
        
    }
    
}
