/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 30th November 2018

    Description: 
        This java class only deals wit rgb jpg file that uses the texture name 
        to load texture file with repeated wrapping and linear filter. A new ID
        set and the texture object is created. The loaded texture file is then 
        applied to the texture object as a 2D image. If there no texture file 
        exists, error message is printed to the console.
*/

// Current package
package Handlers;

// Import packages
import java.io.*;

// JOGL library
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;

// Only load rgb jpg files
public class TextureLibrary {
    
//    Initialise file name
    public static int[] loadTexture(GL3 gl, String filename) {
        return loadTexture(gl, filename, GL.GL_REPEAT, GL.GL_REPEAT, GL.GL_LINEAR, GL.GL_LINEAR);
    }

    public static int[] loadTexture(GL3 gl, String filename, int wrappingS, int wrappingT, int filterS, int filterT) {

        int[] textureID = new int[1];

//        if texture file exists
        try {
            File file = new File(filename);  
            
//            read image
            JPEGImage img = JPEGImage.read(new FileInputStream(file));
            
//            generate texture object and set reference ID in textureID array
            gl.glGenTextures(1, textureID, 0);
            
//            bind texture to configure
            gl.glBindTexture(GL.GL_TEXTURE_2D, textureID[0]); 
            
//            texture wrapping
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, wrappingS);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, wrappingT);
            
//            texture filtering
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, filterS);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, filterT);
            
//            generate texture using loaded image
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, img.getWidth(),img.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, img.getData());
            
            gl.glGenerateMipmap(GL.GL_TEXTURE_2D);
            gl.glBindTexture(GL.GL_TEXTURE_2D, 0); 
            
//            or if texture loading fails
        } catch(Exception error) {
            System.out.println("Error loading texture " + filename); 
        }
        return textureID;
    }
  
}