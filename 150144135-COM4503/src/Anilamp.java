/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 30th November 2018

    Description: I declare that this program code is my own work
        This program draws a window and displays a rendered room with 2 world 
        lights, window, floor, wall, table, a lamp and other objects. The world
        lights and enitre room can be switched on/off and day/night respectively
        from the buttons at the bottom of window. 

        The camera is initially set at a far distance but can be changed to zoom in
        to objects on table. The lamp is animated using the corresponding buttons.
        The pose is changed with the 'random pose' button and can be reset. All
        animations can be stopped with the 'stop animation'. 
    
*/

// Import packages
import Handlers.*;
import Models.*;
import Inputs.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// JOGL library
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;

public class Anilamp extends JFrame implements ActionListener {
  
//    Frame dimension
    private static int frameWidth = 1024;
    private static int frameHeight = 768;
    private static Dimension dimension = new Dimension(frameWidth, frameHeight);

//    Canvas to draw on
    private GLCanvas canvas;
    
    private Camera camera;
    
//    Listen for different event/animation/interaction from buttons
    private EventListener glEventListener;
    
//    Display 'n' times per second
    private final FPSAnimator animator; 

    public static void main(String[] args) {
        
//        create frame
        Anilamp anilampFrame = new Anilamp("Anilamp");
        
//        set size
        anilampFrame.getContentPane().setPreferredSize(dimension);
        
//        check that all contents of the frame are >= preferred sizes
        anilampFrame.pack();
        
//        make frame visible and mouse-focus in window
        anilampFrame.setVisible(true);
        anilampFrame.canvas.requestFocusInWindow();
    }

//    Construct frame/window
    public Anilamp(String windowTitle) {
        super(windowTitle);

  //      specify the capabilities of OpenGL rendering context
        GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));

        /*
            create GLCanvas where all OpenGL drawing occur
            GLCanvas is from com.jogamp.opengl.GLAutoDrawable;
            GLCanvas maintains the context
        */
        canvas = new GLCanvas(glcapabilities);

        camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);

//        create new instance of event listener class
        glEventListener = new EventListener(camera);

//        add new instance to canvas
        canvas.addGLEventListener(glEventListener);

//        add inputs - mouse and keyboard commands
        canvas.addMouseMotionListener(new MouseInput(camera));
        canvas.addKeyListener(new KeyboardInput(camera));

//        add canvas to center of frame
        getContentPane().add(canvas, BorderLayout.CENTER);

//        add buttons to bottom of window
        addButtons();

//        on closing window, stop all animations, clear canvas/elements on screen and exit
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

                animator.stop();
                remove(canvas);
                
                dispose();
                System.exit(0);
            }
        });
        /*
            create a JOGL FPSAnimator instance and attach to convas
            canvas should redraw at 60 frames per second
            i.e. display method in EventListener is called 60 times per second
            'frame' signifies one scene rendering in an animation loop not Frame for the window
        */
        animator = new FPSAnimator(canvas, 60);
        animator.start();
    }
    
//    Create buttons for interactions
    private void addButtons() {
        
        JPanel panel = new JPanel();

        JButton button = new JButton("Switch world light (day/night)");
        button.addActionListener(this);
        panel.add(button);
        
        button = new JButton("Switch room light (on/off)");
        button.addActionListener(this);
        panel.add(button);

        button = new JButton("Lamp jump");
        button.addActionListener(this);
        panel.add(button);

        button = new JButton("Random pose");
        button.addActionListener(this);
        panel.add(button);
        
        button = new JButton("Reset pose");
        button.addActionListener(this);
        panel.add(button);

        button = new JButton("Start Animation");
        button.addActionListener(this);
        panel.add(button);

        button = new JButton("Stop Animation");
        button.addActionListener(this);
        panel.add(button);
        
        button = new JButton("Zoom to objects");
        button.addActionListener(this);
        panel.add(button);

        button = new JButton("Zoom out");
        button.addActionListener(this);
        panel.add(button);
        
        this.add(panel, BorderLayout.SOUTH);
    }
    
//    Set interaction based on button clicked
    @Override
    public void actionPerformed(ActionEvent event) {
        
        if (event.getActionCommand().equalsIgnoreCase("Switch room light (on/off)")) {
            
            if (WorldLight.isLightOn == true) {
                WorldLight.isLightOn = false;
                
            } else {
                WorldLight.isLightOn = true;
            }
        } else if (event.getActionCommand().equalsIgnoreCase("Switch world light (day/night)")) {
            if (SunLight.isLightOn == true) {
                SunLight.isLightOn = false;
                
            } else {
                SunLight.isLightOn = true;
            }
        } else if (event.getActionCommand().equalsIgnoreCase("Lamp jump")) {
            glEventListener.lampJump();
            
        } else if (event.getActionCommand().equalsIgnoreCase("Random pose")) {
            glEventListener.randomPose();
            
        } else if (event.getActionCommand().equalsIgnoreCase("Reset pose")) {
            glEventListener.resetPose();
            
        } else if (event.getActionCommand().equalsIgnoreCase("Start Animation")) {
            glEventListener.rotateLampAround();
            
        } else if (event.getActionCommand().equalsIgnoreCase("Stop Animation")) {
            glEventListener.stopAnimation();
            
        } else if (event.getActionCommand().equalsIgnoreCase("Zoom to objects")) {
            glEventListener.zoomToObjectsOnTable();
            
        } else if (event.getActionCommand().equalsIgnoreCase("Zoom out")) {
            glEventListener.zoomOut();
        } 
    }
}
 