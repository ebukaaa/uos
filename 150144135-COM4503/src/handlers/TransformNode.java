/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 30th November 2018

    Description: 
        This java class is an extension of SGNode that uses the transform name to
        construct the scene grpah making it easy to understand. The transform is 
        also set to the model matrix and updated. The updated model transform 
        overrides the world transform set in SGNode class.
*/

// Current package
package Handlers;

// Import package
import gmaths.*;

public class TransformNode extends SGNode {

//    Model matrix
    private Mat4 transform;

//    Initialise transform name and model matrix
    public TransformNode(String name, Mat4 t) {
        super(name);
        
        transform = new Mat4(t);
    }

//    Change to new model transform 
    public void setTransform(Mat4 m) {
        
        transform = new Mat4(m);
    }

//    Override and update model matrix from SGNode
    @Override
    protected void update(Mat4 t) {
        
        worldTransform = t;
        t = Mat4.multiply(worldTransform, transform);
        
        for (int i=0; i<children.size(); i++) {
          children.get(i).update(t);
        }   
    }

//    Override and print scene graph structure
    @Override
    public void print(int indent, boolean inFull) {
        
        System.out.println(getIndentString(indent)+"Name: "+name);
        
        if (inFull) {
            System.out.println("worldTransform");
            System.out.println(worldTransform);
            System.out.println("transform node:");
            System.out.println(transform);
        }
        for (int i=0; i<children.size(); i++) {
            children.get(i).print(indent+1, inFull);
        }
    }
  
}