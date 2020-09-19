/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 30th November 2018

    Description: 
        This java class reads the vertex and fragment shaders, compile and then link
        shaders to respective object models. The shader uniform variables are 
        altered here in shader class using the utility functions and the shader files
        are activated through the use method. 
*/

// Current package
package Handlers;

// Import packages
import gmaths.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;

// JOGL library
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.glsl.*;  
  
public class Shader {
  
    public static boolean displayShaders = false;

    private int ID;
    private String vertexShaderSource;
    private String fragmentShaderSource;

    public Shader(GL3 gl, String vertexPath, String fragmentPath) {

//        open and read files if shader source exists
        try {
            vertexShaderSource = new String(Files.readAllBytes(Paths.get(vertexPath)), Charset.defaultCharset());
            fragmentShaderSource = new String(Files.readAllBytes(Paths.get(fragmentPath)), Charset.defaultCharset());
            
//            or if source file does not exist
        } catch (IOException error) {
            error.printStackTrace();
        }
        ID = compileAndLink(gl);
    }

//    Activate shader
    public void use(GL3 gl) {
        gl.glUseProgram(ID);
    }

//    Utility uniform functions to update shader variables
    public void setInt(GL3 gl, String name, int value) {
        int location = gl.glGetUniformLocation(ID, name);
        
        gl.glUniform1i(location, value);
    }
    public void setFloat(GL3 gl, String name, float value) {
        int location = gl.glGetUniformLocation(ID, name);
      
        gl.glUniform1f(location, value);
    }
    public void setFloat(GL3 gl, String name, float f1, float f2) {
        int location = gl.glGetUniformLocation(ID, name);
        
        gl.glUniform2f(location, f1, f2);
    }
    public void setFloat(GL3 gl, String name, float f1, float f2, float f3) {
        int location = gl.glGetUniformLocation(ID, name);
        
        gl.glUniform3f(location, f1, f2, f3);
    }
    public void setFloat(GL3 gl, String name, float f1, float f2, float f3, float f4) {
        int location = gl.glGetUniformLocation(ID, name);
        
        gl.glUniform4f(location, f1, f2, f3, f4);
    }
    public void setFloatArray(GL3 gl, String name, float[] f) {
        int location = gl.glGetUniformLocation(ID, name);
        
        gl.glUniformMatrix4fv(location, 1, false, f, 0);
    }
    public void setVec3(GL3 gl, String name, Vec3 v) {
        int location = gl.glGetUniformLocation(ID, name);
        
        gl.glUniform3f(location, v.x, v.y, v.z);
    }

//    Compile and link both vertex and fragment shader, then store shader program
    private int compileAndLink(GL3 gl) {
        
        String[][] sources = new String[1][1];
        sources[0] = new String[] { 
            
            vertexShaderSource
        };
        ShaderCode vertexShaderCode = new ShaderCode(GL3.GL_VERTEX_SHADER, sources.length, sources);
        boolean compiled = vertexShaderCode.compile(gl, System.err);
        
        if (!compiled) {
            System.err.println("[error] Unable to compile vertex shader: " + sources);
        }
        sources[0] = new String[] { 
            
            fragmentShaderSource
        };
        ShaderCode fragmentShaderCode = new ShaderCode(GL3.GL_FRAGMENT_SHADER, sources.length, sources);
        compiled = fragmentShaderCode.compile(gl, System.err);
        
        if (!compiled) {
            System.err.println("[error] Unable to compile fragment shader: " + sources);
        }
        
//        shader program
        ShaderProgram shader = new ShaderProgram();
        shader.init(gl);
        shader.add(vertexShaderCode);
        shader.add(fragmentShaderCode);
        shader.link(gl, System.out);
        
        if (!shader.validateProgram(gl, System.out)) {
            System.err.println("[error] Unable to link program");
        }
        return shader.program();
    }
}