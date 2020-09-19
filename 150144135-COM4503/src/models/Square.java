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
        direction. This square class sets fragment shader correctly base on model
        object.
*/

// Current package
package Models;

// Import package
import gmaths.*;
import Handlers.*;
import Scene.*;

// JOGL library
import com.jogamp.opengl.*;

public class Square {
    
//    Properties
    public Material material = new Material();
    public Model model;
    private FillBuffer fillBuffers;
    private Shader shader;
    
//    Data
    public float[] vertices = {      
        
//       position,             colour,         texture
      -0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 1f,  // top left
      -0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom left
       0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  1f, 0.0f,  // bottom right
       0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  1f, 1f   // top right
    };
    public int[] triangles = {         // Note that we start from 0!
        0, 1, 2,
        0, 2, 3
    };
    
//    Vertex attributes - space between position, texture and color
    public int stride = 8;
    public int positionFloats = 3;
    public int normalFloats = 3;
    public int textureFloats = 2;

//    Initialise model
    public Square(GL3 gl, Camera camera, WorldLight light1, WorldLight light2, SunLight sunLight, LampLight lampLight, int [] texture1, int[] texture2, Mat4 modelMatrix) {

//        using lights to set fragment shader to window or other square models
        setShadersAndBuffers(gl, light1, light2, lampLight, vertices);
        
//        set model properties 
        model = new Model(gl, camera, light1, light2, sunLight, lampLight, shader, material, modelMatrix, fillBuffers, texture1, texture2);
    }

//    For paper model
    public Square(GL3 gl, Camera camera, WorldLight light1, WorldLight light2, SunLight sunLight, LampLight lampLight, int [] texture, Mat4 modelMatrix) {
        this(gl, camera, light1, light2, sunLight, lampLight, texture, null, modelMatrix);
    }
    
//    For window model
    public Square(GL3 gl, Camera camera, SunLight light, int [] texture, Mat4 modelMatrix) {
        this(gl, camera, null, null, light, null, texture, null, modelMatrix);
    }
    
//    Check to set shader to window model or other square models
    private void setShadersAndBuffers(GL3 gl, WorldLight light1, WorldLight light2, LampLight lampLight, float[] vertices) {
        
        String shaderFile = "";
        
//        window model
        if(light1 == null & light2 == null && lampLight == null) {
            shaderFile = "shaders/Window fragment shader.glsl";
            
//            other square models
        } else {
            shaderFile = "shaders/Object fragment shader.glsl";
        }
        
//        set shader and buffer
        shader = new Shader(gl, "shaders/Object vertex shader.glsl", shaderFile);
        fillBuffers = new FillBuffer(gl, vertices, triangles, stride, positionFloats, normalFloats, textureFloats);
    }
}
