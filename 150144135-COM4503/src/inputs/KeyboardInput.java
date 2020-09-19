/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 30th November 2018

    Description: 
        This java class extends key adapter class to implements keyboard commands
        based on camera movements
*/

// Current package
package Inputs;

// Import packages
import Handlers.*;
import java.awt.event.*;

public class KeyboardInput extends KeyAdapter {
    
    private Camera camera;

    public KeyboardInput(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        
        Camera.Movement movement = Camera.Movement.NO_MOVEMENT;
        
        switch (event.getKeyCode()) {
            case KeyEvent.VK_LEFT:  movement = Camera.Movement.LEFT;  break;
            case KeyEvent.VK_RIGHT: movement = Camera.Movement.RIGHT; break;
            case KeyEvent.VK_UP:    movement = Camera.Movement.UP;    break;
            case KeyEvent.VK_DOWN:  movement = Camera.Movement.DOWN;  break;
            
            case KeyEvent.VK_A: movement = Camera.Movement.FORWARD;  break;
            case KeyEvent.VK_Z: movement = Camera.Movement.BACK;  break;
        }
        camera.keyboardInput(movement);
    }
}
