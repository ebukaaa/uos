/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 30th November 2018

    Description: 
        This java class uses the object data and assigned vertex attributes to
        create buffer and vertex array objects. All models are drawn on screen
        using its triangle length when draw action is called. And cleared from
        screen when dispose action is called.
*/

// Current package
package Scene;

// Import packages
import java.nio.*;

// JOGL library
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class FillBuffer {
  
//    Data
    private float[] vertices;
    private int[] triangles;

//    Vertex attributes
    private int vertexStride;
    private int vertexPositionFloats ;
    private int vertexNormalFloats;
    private int vertexTextureFloats;

//    Element buffer and vertex array object IDs
    private int[] vertexBufferID = new int[1];
    private int[] vertexArrayID = new int[1];
    private int[] elementBufferID = new int[1];

//    Initialise data and vertex attributes
    public FillBuffer(GL3 gl, float[] vertices, int[] triangles, int stride, int positionFloats, int normalFloats, int textureFloats) {
        this.vertices = vertices;
        this.triangles = triangles;
        this.vertexStride = stride;
        this.vertexPositionFloats = positionFloats;
        this.vertexNormalFloats = normalFloats;
        this.vertexTextureFloats = textureFloats;

//        generate element buffer and vertex array objects
        fillBuffers(gl);
    }
    public FillBuffer(GL3 gl, float[] vertices, int[] triangles, int vertexStride, int positionFloats) {
        this(gl, vertices, triangles, vertexStride, positionFloats, 0, 0);
    }

//    Create element buffer and vertex array objects
    private void fillBuffers(GL3 gl) {
        
//        generate 1 or multiple vertex array object(s) returning an address in GPU memory as ID
        gl.glGenVertexArrays(1, vertexArrayID, 0);
        gl.glBindVertexArray(vertexArrayID[0]);
        
//        generate 1 or multiple buffer object(s) returning an address in GPU memory as ID
        gl.glGenBuffers(1, vertexBufferID, 0);
        
//        bind buffer object to current buffer type target
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferID[0]);
        
        FloatBuffer floatBuffer = Buffers.newDirectFloatBuffer(vertices);

//        copies defined vertex data into the buffer's memory:
        gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, floatBuffer, GL.GL_STATIC_DRAW);

//        states how big each set of attributes for a vertex is
        int stride = vertexStride;
        
        /*
            FOR POSITION
        */
        int positionFloats = vertexPositionFloats;
        
//        offset is relative to the start of the array of data
        int offset = 0;
        
//        set position vertex attributes pointers - location, size, type, normalize?, stride, offset 
        gl.glVertexAttribPointer(0, positionFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
        
//        enables at location 0
        gl.glEnableVertexAttribArray(0);

//        for objects with textures i.e. except the lights
        if (vertexNormalFloats != 0 && vertexTextureFloats != 0) {
            /*
                NORMAL PER VERTEX X,Y,Z
            */
            int normalFloats = vertexNormalFloats; 

//            normal values are 3 floats after x,y,z values so offset value changes
            offset = positionFloats * Float.BYTES;

//            the vertex shader uses location 1 (sometimes called index 1) for the normal information
            gl.glVertexAttribPointer(1, normalFloats, GL.GL_FLOAT, false, stride * Float.BYTES, offset);

//            enables at location 1
            gl.glEnableVertexAttribArray(1);

            /*
                FOR TEXTURES
            */
            int textureFloats = vertexTextureFloats;
            offset = (positionFloats + normalFloats) * Float.BYTES;

//            texture attributes
            gl.glVertexAttribPointer(2, textureFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);

//            enables at location 2
            gl.glEnableVertexAttribArray(2);
        }
        
//        generate 1 or multiple element buffer object(s) returning an address in GPU memory as ID
        gl.glGenBuffers(1, elementBufferID, 0);
        
        IntBuffer intBuffer = Buffers.newDirectIntBuffer(triangles);
        
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferID[0]);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * triangles.length, intBuffer, GL.GL_STATIC_DRAW);
        gl.glBindVertexArray(0);
    }

//    Draw triangles using its array length
    public void draw(GL3 gl) {
        
        gl.glBindVertexArray(vertexArrayID[0]);
        gl.glDrawElements(GL.GL_TRIANGLES, triangles.length, GL.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
    }
    
//    Delete all buffers and vertex arrays 
    public void dispose(GL3 gl) {
        
        gl.glDeleteBuffers(1, vertexBufferID, 0);
        gl.glDeleteVertexArrays(1, vertexArrayID, 0);
        gl.glDeleteBuffers(1, elementBufferID, 0);
    }
  
}