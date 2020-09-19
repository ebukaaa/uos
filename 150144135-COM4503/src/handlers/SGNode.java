/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 30th November 2018

    Description: 
        This java class is a scene graph node that uses the root/transform/shape name to
        construct the scene grpah making it easy to understand. The root is
        initially empty and updated once addChild method is called. The model matrix 
        get updated also during animations and interactions. Then the model matrix
        get drawn based on the applied transforms from scene graph class. Finally 
        the scene graph structure is created and printed after rendering models.
*/

// Current package
package Handlers;

// Import packages
import gmaths.*;
import java.util.ArrayList;

// JOGL library
import com.jogamp.opengl.*;

public class SGNode {

//    Scene graph properties
    protected String name;
    protected ArrayList<SGNode> children;
    protected Mat4 worldTransform;

//    Initialise scene name, model matrix and an empty root
    public SGNode(String name) {
        this.name = name;
        
        children = new ArrayList<SGNode>();
        worldTransform = new Mat4(1);
    }

//    Add transforms to root
    public void addChild(SGNode child) {
        
        children.add(child);
    }

//    Update model matrix
    public void update() {
        
        update(worldTransform);
    }
    protected void update(Mat4 t) {
        
        worldTransform = t;
        
        for (int i=0; i<children.size(); i++) {
          children.get(i).update(t);
        }
    }
    
//    Draw model object on screen
    public void draw(GL3 gl) {
        
        for (int i=0; i<children.size(); i++) {
          children.get(i).draw(gl);
        }
    }

//    Create scene graph structure
    protected String getIndentString(int indent) {
        
        String s = ""+indent+" ";
        
        for (int i=0; i<indent; ++i) {
          s+="  ";
        }
        return s;
    }
    
//    Display scene graph structure in command window
    public void print(int indent, boolean inFull) {
        
        System.out.println(getIndentString(indent)+"Name: "+name);
        
        if (inFull) {
          System.out.println("worldTransform");
          System.out.println(worldTransform);
        }
        for (int i=0; i<children.size(); i++) {
          children.get(i).print(indent+1, inFull);
        }
    }
}