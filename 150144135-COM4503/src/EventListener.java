/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 30th November 2018

    Description: 
        This java class implements 4 openGL events for the canvas to respond to. 
        Each of these events (in order below) perform specific tasks as follows:
            1st event to initialise the OpenGL context; 
            2nd event to dispose of any memory that has been used handling openGL on GPU;
            3rd event to display the result of some OpenGL commands on the canvas;
            4th event to do something when the window is resized by the user;
        However, the openGL context, its command results and disose of memory is 
        operated behind the scene in the scene graph class. All object interactions 
        (with/no animations) are handled in this  event listener class.
*/

// Import packages
import Handlers.*;
import gmaths.*;

// JOGL library
import com.jogamp.opengl.*;

public class EventListener implements GLEventListener {
  
//    Camera settings
    private final Camera camera;
    private float aspect;
    
    Vec3 lampLightPosition;
    
    public EventListener(Camera camera) {
        this.camera = camera;
        
//        set camera to default zoom
        defaultZoom();
    }

    /*
        METHODS DEFINED BY GLEventListener
    */
//    Initialise OpenGL context
    @Override
    public void init(GLAutoDrawable drawable) {   
        
//        retrieve the gl context from drawable
        GL3 gl = drawable.getGL().getGL3();
        
//        print some diagnostic info.
        System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
        
//        set the background colour for the OpenGL context to sky blue when canvas area is cleared
        gl.glClearColor(0.69f, 0.80f, 0.89f, 1.0f); 
        
        /*
            z buffer used when rendering objects with depth
            i.e. set at different distances from the camera system
            usually used for hidden surface removal such that 
            one surface appears in front of another or a surface part in case of intersecting surfaces
        */
        gl.glClearDepth(1.0f);
        
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glFrontFace(GL.GL_CCW);   
        gl.glEnable(GL.GL_CULL_FACE); 
        gl.glCullFace(GL.GL_BACK);   
        
//        initialise scene
        SceneGraph.initialise(gl, camera);
        lampLightPosition = SceneGraph.lampLight.position;
        SceneGraph.startTime = getSeconds();
    }
    
//    Dispose of any memory used while handling OpenGL on GPU
    @Override
    public void dispose(GLAutoDrawable drawable) {
        
        GL3 gl = drawable.getGL().getGL3();
        
//        dispose scene
        SceneGraph.dispose(gl);
    }
    
//    Display result of some OpenGL animations and draw model objects
    @Override
    public void display(GLAutoDrawable drawable) {
        
        GL3 gl = drawable.getGL().getGL3();
        
//        render correct animations
        checkAnimations(gl);
        
//        renderAnimations scene
        SceneGraph.render(gl);
    }

//    Re-position model objects when window is resized
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        
        GL3 gl = drawable.getGL().getGL3();
        gl.glViewport(x, y, width, height);

//        position camera perspective matrix
        aspect = (float)width/(float)height;
        camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
    }
    
//    Check and render correct animations
    public void checkAnimations(GL3 gl) {
        
        if(SceneGraph.rotateLampArroundAnimation) {
            rotateLampAround();
            
        } if(SceneGraph.lampJumpAnimation) {
            lampJump();
            
        } if(SceneGraph.randomPoseAnimation) {
            randomPose();
        }
    }
    private float randomNumberZ() {
        float randomLampArmsZ = (float)Math.random() * 23f;
        
        return  randomLampArmsZ;
    }
    
//    Interactions
    public void randomPose() {
        stopAnimation();
//        startRandomPoseAnimation();
        
//        lamp arms around z interval: 0 < randomLampArmsZ < 23
        float randomLampArmsZ = (float)Math.random() * 23f;
        
//        lamp head around y interval: 0 < randomLampHeadY < 180
        float randomLampHeadY = (float)Math.random() * 180;
        
//        rotate lamp arms around z
        SceneGraph.rotateLampPart = randomLampArmsZ;
        SceneGraph.rotateTopLampArmZ.setTransform(Mat4Transform.rotateAroundZ(SceneGraph.rotateLampPart));
        SceneGraph.rotateBottomLampArmZ.setTransform(Mat4Transform.rotateAroundZ(SceneGraph.rotateLampPart));

//        rotate lamp head around y
        SceneGraph.rotateLampHeadY.setTransform(Mat4Transform.rotateAroundY(randomLampHeadY));
        
//        update model transforms
        SceneGraph.rotateTopLampArmZ.update();
        SceneGraph.rotateLampHeadY.update();
        SceneGraph.rotateBottomLampArmZ.update();
    }
    public void resetPose() {
        stopAnimation();
        
//        rotate lamp arms around z
        SceneGraph.rotateLampPart = SceneGraph.defaultAngle;
        SceneGraph.rotateBottomLampArmZ.setTransform(Mat4Transform.rotateAroundZ(SceneGraph.defaultAngle));
        SceneGraph.rotateTopLampArmZ.setTransform(Mat4Transform.rotateAroundZ(SceneGraph.defaultAngle));
        
//        roate lamp head and bottom arm around y
        SceneGraph.rotateLampHeadY.setTransform(Mat4Transform.rotateAroundY(0));
        SceneGraph.rotateBottomLampArmY.setTransform(Mat4Transform.rotateAroundY(0));
        
//        update model transforms
        SceneGraph.rotateBottomLampArmY.update();
        SceneGraph.rotateLampHeadY.update();
        SceneGraph.rotateBottomLampArmZ.update();
        SceneGraph.rotateTopLampArmZ.update();
    }
    public void rotateLampAround() {
        stopAnimation();
        startRotateLampYAnimation();
        
//        timer
        double elapsedTime = getSeconds() - SceneGraph.startTime;
        
//        for bottom lamp arm to move around y continuously
        float bottomLampArmAngle = (float)(elapsedTime * 30);
        
//        angle is between interval -22.5 <-> 22.5
        float angle = SceneGraph.rotateLampPart * (float)(Math.sin(Math.toRadians(elapsedTime*50)));
        
//        updated position interval: -lampLightPosition < x,y,z < lampLightPosition
        float x = lampLightPosition.x * (float)(Math.cos(Math.toRadians(bottomLampArmAngle)));
        float y = lampLightPosition.y * (float)(Math.cos(Math.toRadians(elapsedTime * 50)));
        float z = lampLightPosition.z * (float)(Math.cos(Math.toRadians(bottomLampArmAngle)));
        
//        update lamp light position
        SceneGraph.lampLight.position = new Vec3(x, y, z);
        
//        rotate lamp wire and arms around z
        SceneGraph.rotateBottomLampArmZ.setTransform(Mat4Transform.rotateAroundZ(angle));
        SceneGraph.rotateBottomLampArmY.setTransform(Mat4Transform.rotateAroundY(bottomLampArmAngle));
        SceneGraph.rotateTopLampArmZ.setTransform(Mat4Transform.rotateAroundZ(angle));
        SceneGraph.rotateLampWireZ.setTransform(Mat4Transform.rotateAroundZ((22.5f-angle)*0.5f));
        
//        rotate clock hand, lamp head and wire around y
        SceneGraph.rotateLampHeadY.setTransform(Mat4Transform.rotateAroundY(1.5f*angle));
        SceneGraph.rotateLampWireY.setTransform(Mat4Transform.rotateAroundY(1.5f*angle));
        SceneGraph.rotateClockHandY.setTransform(Mat4Transform.rotateAroundY(2f * angle));
        
//        update model transforms
        SceneGraph.rotateLampWireY.update();
        SceneGraph.rotateClockHandY.update();
        SceneGraph.rotateLampHeadY.update();
        SceneGraph.rotateLampWireZ.update();
        SceneGraph.rotateTopLampArmZ.update();
        SceneGraph.rotateBottomLampArmY.update();
        SceneGraph.rotateBottomLampArmZ.update();
    }
    
    public void lampJump() {
        stopAnimation();
        startlampJumpAnimation();
        
//        timer
        double elapsedTime = getSeconds() - SceneGraph.startTime;
        
//        angle is between interval -22.5 <-> 22.5
        float angle = SceneGraph.rotateLampPart * (float)(Math.cos(Math.toRadians(elapsedTime * 70)));
        
//        lamp move to 4 possible positions
        Vec3 firstPosition = new Vec3(-2, 0, 2);
        Vec3 secondPosition = new Vec3(-2, 0, -2);
        Vec3 thirdPosition = new Vec3(2, 0, -2);
        Vec3 fourthPosition = new Vec3(2, SceneGraph.postBoxHeight-0.1f, 2);
        
//        check correct lamp arm position to jump
        if(angle <= 22.5f && angle > 19) {
            SceneGraph.translateToPoint = new Vec3(SceneGraph.translateToPoint.x, (22.5f-angle)/2, (22.5f-angle)/2);
            SceneGraph.translateLampBase.setTransform(Mat4Transform.translate(SceneGraph.translateToPoint));

//            update lamp light position
            SceneGraph.lampLight.position.y = SceneGraph.translateToPoint.y;
            SceneGraph.lampLight.position.z = SceneGraph.translateToPoint.z;

//            translate lamp 
            if (angle > 20) {
//                based on current position
                if (SceneGraph.moveLeftInFront) {
                    SceneGraph.translateToPoint = new Vec3(-(22.5f-angle)/2, SceneGraph.translateToPoint.y, SceneGraph.translateToPoint.z);
                    SceneGraph.translateLampBase.setTransform(Mat4Transform.translate(SceneGraph.translateToPoint));

                    SceneGraph.lampLight.position.x = SceneGraph.translateToPoint.x;
                    SceneGraph.moveLeftInFront = false;
                    SceneGraph.moveBackOnTheLeft = true;

                    SceneGraph.translateToPoint = firstPosition;
                    SceneGraph.translateLampBase.setTransform(Mat4Transform.translate(SceneGraph.translateToPoint));
                } 
                else if (SceneGraph.moveBackOnTheLeft) {
                    SceneGraph.translateToPoint = new Vec3(SceneGraph.translateToPoint.x, SceneGraph.translateToPoint.y, -(22.5f-angle)/2);
                    SceneGraph.translateLampBase.setTransform(Mat4Transform.translate(SceneGraph.translateToPoint));

                    SceneGraph.lampLight.position.z = SceneGraph.translateToPoint.z;
                    SceneGraph.moveBackOnTheLeft = false;
                    SceneGraph.moveRightAtTheBack = true;

                    SceneGraph.translateToPoint = secondPosition;
                    SceneGraph.translateLampBase.setTransform(Mat4Transform.translate(SceneGraph.translateToPoint));

                } else if (SceneGraph.moveRightAtTheBack) {
                    SceneGraph.translateToPoint = new Vec3((22.5f-angle)/2, SceneGraph.translateToPoint.y, SceneGraph.translateToPoint.z);
                    SceneGraph.translateLampBase.setTransform(Mat4Transform.translate(SceneGraph.translateToPoint));

                    SceneGraph.lampLight.position.x = SceneGraph.translateToPoint.x;
                    SceneGraph.moveRightAtTheBack = false;
                    SceneGraph.moveFrontOnTheRight = true;

                    SceneGraph.translateToPoint = thirdPosition;
                    SceneGraph.translateLampBase.setTransform(Mat4Transform.translate(SceneGraph.translateToPoint));

                } else if (SceneGraph.moveFrontOnTheRight) {
                    SceneGraph.translateToPoint = new Vec3(SceneGraph.translateToPoint.x, SceneGraph.translateToPoint.y, (22.5f-angle)/2);
                    SceneGraph.translateLampBase.setTransform(Mat4Transform.translate(SceneGraph.translateToPoint));

                    SceneGraph.lampLight.position.z = SceneGraph.translateToPoint.z;
                    SceneGraph.moveFrontOnTheRight = false;
                    SceneGraph.moveLeftInFront = true;

                    SceneGraph.translateToPoint = fourthPosition;
                    SceneGraph.translateLampBase.setTransform(Mat4Transform.translate(SceneGraph.translateToPoint));
                }
                stopAnimation();
            }
        }
        
//        rotate lamp wire and arms around z
        SceneGraph.rotateBottomLampArmZ.setTransform(Mat4Transform.rotateAroundZ((-2.5f+angle)/2));
        SceneGraph.rotateTopLampArmZ.setTransform(Mat4Transform.rotateAroundZ((-2.5f+angle)/2));
        SceneGraph.rotateLampWireZ.setTransform(Mat4Transform.rotateAroundZ((-2.5f+angle)/2));
        
//        update model transforms
        SceneGraph.rotateLampWireZ.update();
        SceneGraph.rotateBottomLampArmZ.update();
        SceneGraph.rotateTopLampArmZ.update();
        SceneGraph.translateLampBase.update();
    }
    
//    Change camera position and target
    public void zoomToObjectsOnTable() {
        
        camera.setPosition(new Vec3(4f,10f,20f));
        camera.setTarget(new Vec3(0f,5f,0f));
    }
    public void zoomOut() {
        
        defaultZoom();
    } 
    private void defaultZoom() {
        
        camera.setPosition(new Vec3(4f,10f,25f));
        camera.setTarget(new Vec3(0f,5f,0f));
    }
    
//    Animation time
    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }
//    
    private void startRotateLampYAnimation() {
        
        SceneGraph.rotateLampArroundAnimation = true;
        SceneGraph.startTime = getSeconds() - SceneGraph.savedTime;
    }
    private void startlampJumpAnimation() {
        
        SceneGraph.lampJumpAnimation = true;
        SceneGraph.startTime = getSeconds() - SceneGraph.savedTime;
    }
    private void startRandomPoseAnimation() {
        
        SceneGraph.randomPoseAnimation = true;
        SceneGraph.startTime = getSeconds() - SceneGraph.savedTime;
    }

    public void stopAnimation() {
        
        SceneGraph.rotateLampArroundAnimation = false;
        SceneGraph.lampJumpAnimation = false;
        SceneGraph.randomPoseAnimation = false;
        
        double elapsedTime = getSeconds() - SceneGraph.startTime;
        SceneGraph.savedTime = elapsedTime;
    }
}
