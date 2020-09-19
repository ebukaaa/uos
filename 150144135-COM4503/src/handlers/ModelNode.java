/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 30th November 2018

    Description: 
        This java class is an extension of SGNode class that uses the model object
        and draw its model matrix based on the applied transforms from scene graph 
        class. It also sets the model shape name to build the scene graph structure.
*/

// Current package
package Handlers;

// Import packages
import Scene.*;
import com.jogamp.opengl.*;

public class ModelNode extends SGNode {

//    Model object
    protected Model model;

//    Initialise shape name and model object
    public ModelNode(String name, Model m) {
        super(name);
        model = m; 
    }

//    Override and draw updates model matrix
    @Override
    public void draw(GL3 gl) {
        model.draw(gl, worldTransform);
        for (int i=0; i<children.size(); i++) {
            children.get(i).draw(gl);
        }
    }

}