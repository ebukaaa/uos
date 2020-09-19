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
        direction.
*/

// Current package
package Models;

// Import package
import gmaths.*;
import Handlers.*;
import Scene.*;

// JOGL library
import com.jogamp.opengl.*;

public class Cube {
    
//    properties
    public Material material = new Material();
    public Model model;
    private Shader shader;
    private FillBuffer fillBuffers;
    
//    data
    public float[] vertices = new float[] {   
//        position,             colour,    texture
        -0.5f, -0.5f, -0.5f,  -1, 0, 0,  0.0f, 0.0f,  // 0
        -0.5f, -0.5f,  0.5f,  -1, 0, 0,  1.0f, 0.0f,  // 1
        -0.5f,  0.5f, -0.5f,  -1, 0, 0,  0.0f, 1.0f,  // 2
        -0.5f,  0.5f,  0.5f,  -1, 0, 0,  1.0f, 1.0f,  // 3
         0.5f, -0.5f, -0.5f,   1, 0, 0,  1.0f, 0.0f,  // 4
         0.5f, -0.5f,  0.5f,   1, 0, 0,  0.0f, 0.0f,  // 5
         0.5f,  0.5f, -0.5f,   1, 0, 0,  1.0f, 1.0f,  // 6
         0.5f,  0.5f,  0.5f,   1, 0, 0,  0.0f, 1.0f,  // 7

        -0.5f, -0.5f, -0.5f,  0,0,-1,  1.0f, 0.0f,  // 8
        -0.5f, -0.5f,  0.5f,  0,0,1,   0.0f, 0.0f,  // 9
        -0.5f,  0.5f, -0.5f,  0,0,-1,  1.0f, 1.0f,  // 10
        -0.5f,  0.5f,  0.5f,  0,0,1,   0.0f, 1.0f,  // 11
         0.5f, -0.5f, -0.5f,  0,0,-1,  0.0f, 0.0f,  // 12
         0.5f, -0.5f,  0.5f,  0,0,1,   1.0f, 0.0f,  // 13
         0.5f,  0.5f, -0.5f,  0,0,-1,  0.0f, 1.0f,  // 14
         0.5f,  0.5f,  0.5f,  0,0,1,   1.0f, 1.0f,  // 15

        -0.5f, -0.5f, -0.5f,  0,-1,0,  0.0f, 0.0f,  // 16
        -0.5f, -0.5f,  0.5f,  0,-1,0,  0.0f, 1.0f,  // 17
        -0.5f,  0.5f, -0.5f,  0,1,0,   0.0f, 1.0f,  // 18
        -0.5f,  0.5f,  0.5f,  0,1,0,   0.0f, 0.0f,  // 19
         0.5f, -0.5f, -0.5f,  0,-1,0,  1.0f, 0.0f,  // 20
         0.5f, -0.5f,  0.5f,  0,-1,0,  1.0f, 1.0f,  // 21
         0.5f,  0.5f, -0.5f,  0,1,0,   1.0f, 1.0f,  // 22
         0.5f,  0.5f,  0.5f,  0,1,0,   1.0f, 0.0f   // 23
    };
    public int[] triangles =  new int[] {
        0,1,3, // x -ve 
        3,2,0, // x -ve
        4,6,7, // x +ve
        7,5,4, // x +ve
        
        9,13,15, // z +ve
        15,11,9, // z +ve
        8,10,14, // z -ve
        14,12,8, // z -ve
        
        16,20,21, // y -ve
        21,17,16, // y -ve
        23,22,18, // y +ve
        18,19,23  // y +ve
    };
    
//    space between position, texture and color
    public int stride = 8;
    public int positionFloats = 3;
    public int normalFloats = 3;
    public int textureFloats = 2;
    
//    Initialise model
    public Cube(GL3 gl, Camera camera, WorldLight light1, WorldLight light2, SunLight sunLight,  LampLight light4, int[] texture1, int[] texture2, Mat4 modelMatrix) {
        
//        set shader, buffer and material
        shader = new Shader(gl, "shaders/Object vertex shader.glsl", "shaders/Object fragment shader.glsl");
        fillBuffers = new FillBuffer(gl, vertices, triangles, stride, positionFloats, normalFloats, textureFloats);
        
//        set model material ambient, diffuse, specular lights and model shininess
        material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        
//        set model properties
        model = new Model(gl, camera, light1, light2, sunLight, light4, shader, material, modelMatrix, fillBuffers, texture1, texture2);
    }
    public Cube(GL3 gl, Camera camera, WorldLight light1, WorldLight light2, SunLight sunLight,  LampLight light4, int[] texture1, Mat4 modelMatrix) {
        this(gl, camera, light1, light2, sunLight, light4, texture1, null, modelMatrix);
    }
}
