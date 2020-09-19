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
        direction. This sphere class sets the longitude and latitude to position
        the vertices and triangles properly.
*/

// Current package
package Models;

// Import packages
import Handlers.*;
import Scene.*;

import gmaths.*;

// JOGL library
import com.jogamp.opengl.*;

public class Sphere {
  
//    Properties
    public Material material = new Material();
    public Model model;
    private Shader shader;
    private FillBuffer fillBuffers;

//    Longitude and latitude for creating data
    private final int XLONG = 30;
    private final int YLAT = 30;

//    Data
    public float[] vertices = createVertices();
    public int[] indices = createIndices();

//    Vertex attributes - space between position, texture and color
    public int stride = 8;
    public int positionFloats = 3;
    public int normalFloats = 3;
    public int textureFloats = 2;
    
//    Initialise model
    public Sphere(GL3 gl, Camera camera, WorldLight light1, WorldLight light2, SunLight sunLight, LampLight lampLight, int[] texture1, int[] texture2, Mat4 modelMatrix) {
        
//        set shader, buffer and material
        shader = new Shader(gl, "shaders/Object vertex shader.glsl", "shaders/Object fragment shader.glsl");
        fillBuffers = new FillBuffer(gl, vertices, indices, stride, positionFloats, normalFloats, textureFloats);
        
//        set model material ambient, diffuse, specular lights and model shininess
        material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        
//        set model properties 
        model = new Model(gl, camera, light1, light2, sunLight, lampLight, shader, material, modelMatrix, fillBuffers, texture1, texture2);
    }
     
//    Position vertices of sphere data 
    private float[] createVertices() {
        double r = 0.5;
        int step = 8;
        
        float[] vertex = new float[XLONG*YLAT*step];
        for (int j = 0; j<YLAT; ++j) {
          double b = Math.toRadians(-90+180*(double)(j)/(YLAT-1));
          
            for (int i = 0; i<XLONG; ++i) {
                double a = Math.toRadians(360*(double)(i)/(XLONG-1));
                double z = Math.cos(b) * Math.cos(a);
                double x = Math.cos(b) * Math.sin(a);
                double y = Math.sin(b);

                int base = j*XLONG*step;
                
                vertex[base + i*step+0] = (float)(r*x);
                vertex[base + i*step+1] = (float)(r*y);
                vertex[base + i*step+2] = (float)(r*z); 
                vertex[base + i*step+3] = (float)x;
                vertex[base + i*step+4] = (float)y;
                vertex[base + i*step+5] = (float)z;
                vertex[base + i*step+6] = (float)(i)/(float)(XLONG-1);
                vertex[base + i*step+7] = (float)(j)/(float)(YLAT-1);
            }
        }
        return vertex;
    }
    
//    Position triangles of sphere data 
    private int[] createIndices() {

        int[] index = new int[(XLONG-1)*(YLAT-1)*6];
        
        for (int j = 0; j<YLAT-1; ++j) {
            for (int i = 0; i<XLONG-1; ++i) {
                int base = j*(XLONG-1)*6;

                index[base + i*6+0] = j*XLONG+i;
                index[base + i*6+1] = j*XLONG+i+1;
                index[base + i*6+2] = (j+1)*XLONG+i+1;
                index[base + i*6+3] = j*XLONG+i;
                index[base + i*6+4] = (j+1)*XLONG+i+1;
                index[base + i*6+5] = (j+1)*XLONG+i;
            }
        }
        return index;
    }
}