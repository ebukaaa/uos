/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 2nd December 2018

    Description: 
        This is a light vertex shader in which the world lights and sun light use. 
        Each light vertex position and mvp matrix is obtained and passed down to the
        gl position which then set the vertex position in clip space. 
*/

// OpenGL version 3.30
#version 330 core

// light vertex position
layout (location = 0) in vec3 position;

// set in model class
uniform mat4 mvpMatrix;

void main() {

    /*
        set current position of vertices in clip space using the model view 
        project matrix
    */
    gl_Position = mvpMatrix * vec4(position, 1.0);
}