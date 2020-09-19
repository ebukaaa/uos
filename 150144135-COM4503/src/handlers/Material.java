/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 30th November 2018

    Description: 
        This java class stores the Material properties for a model object
*/

// Current package
package Handlers;

// Import packages
import gmaths.*;

public class Material {

//    default values
    public static final Vec3 DEFAULT_AMBIENT = new Vec3(0.2f, 0.2f, 0.2f);
    public static final Vec3 DEFAULT_DIFFUSE = new Vec3(0.8f, 0.8f, 0.8f);
    public static final Vec3 DEFAULT_SPECULAR = new Vec3(0.5f, 0.5f, 0.5f);
    public static final float DEFAULT_SHININESS = 32;

//    Sets attributes as used in Phong local reflection model
    public Vec3 ambient;
    public Vec3 diffuse;
    public Vec3 specular;
    public float shininess;

    /**
     * Constructor. Sets attributes to default initial values.
     */    
    public Material() {
      ambient = new Vec3(DEFAULT_AMBIENT);
      diffuse = new Vec3(DEFAULT_DIFFUSE);
      specular = new Vec3(DEFAULT_SPECULAR);
      shininess = DEFAULT_SHININESS;
    }

     /**
     * Constructor. Sets the ambient, diffuse, specular, emission and shininess values
     * 
     * @param  ambient    vector of 3 values: red, green and blue, in the range 0.0..1.0.
     * @param  diffuse    vector of 3 values: red, green and blue, in the range 0.0..1.0.
     * @param  specular   vector of 3 values: red, green and blue, in the range 0.0..1.0.
     * @param  shininess   float value in the range 0.0..1.0.
     */  

    public Material(Vec3 ambient, Vec3 diffuse, Vec3 specular, float shininess) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }
}