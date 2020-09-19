/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 30th November 2018

    Description: 
        This java class sets the object properties, data and vertex attributes. The
        object material ambient, diffuse and specular light float values are set
        before the model is assigned to specified object. All object models are
        specified in the scene graph and data are set in the anti-clockwise
        direction. This lamp light class stores the position for updating 
        animations defined in the event listener. The model class uses the 
        isLightOn boolean property to correctly assign fragment shaders.
*/

// Current package
package Models;

// Import packages
import gmaths.*;
import Handlers.*;
import Scene.*;

// JOGL library
import com.jogamp.opengl.*;
  
public class LampLight {
  
//    Properties
    public Material material;
    private Shader shader;
    public Model model;
    private FillBuffer fillBuffers;
    
    public Vec3 position = new Vec3(0, 8, 0);
    public static boolean isLightOn = true;
    
//    Data
    public float[] vertices = new float[] {  
//        position
//        x,     y,     z
        -0.5f, -0.5f, -0.5f,  // 0
        -0.5f, -0.5f,  0.5f,  // 1
        -0.5f,  0.5f, -0.5f,  // 2
        -0.5f,  0.5f,  0.5f,  // 3
         0.5f, -0.5f, -0.5f,  // 4
         0.5f, -0.5f,  0.5f,  // 5
         0.5f,  0.5f, -0.5f,  // 6
         0.5f,  0.5f,  0.5f   // 7
    };
    public int[] triangles =  new int[] {
        
        0,1,3, // x -ve 
        3,2,0, // x -ve
        4,6,7, // x +ve
        7,5,4, // x +ve
        
        1,5,7, // z +ve
        7,3,1, // z +ve
        6,4,0, // z -ve
        0,2,6, // z -ve
        
        0,4,5, // y -ve
        5,1,0, // y -ve
        2,3,7, // y +ve
        7,6,2  // y +ve
    };
    
//    Vertex attributes - space between position, texture and color
    public int stride = 3;
    public int positionFloats = 3;

//    Initialise model
    public LampLight(GL3 gl, Camera camera, Mat4 modelMatrix) {
        
//        set shader, buffer and material
        shader = new Shader(gl, "shaders/light vertex shader.glsl", "shaders/Lamp light fragment shader.glsl");
        fillBuffers = new FillBuffer(gl, vertices, triangles, stride, positionFloats);
        
//        set model material ambient, diffuse, specular lights and model shininess
        material = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), 32.0f);
        
//        set model properties
        model = new Model(gl, camera, shader, material, modelMatrix, fillBuffers);
    }
}