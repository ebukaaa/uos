/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 2nd December 2018

    Description: 
        This lamp light fragment shader uses lamp light vertex color set in the
        model class to update the fragment color so long as light is on
*/

// OpenGL version 3.30
#version 330 core

// output fragment color
out vec4 fragColor;

// set in model class
uniform vec4 lampLightVertexColor;

void main() {

    // set lamp light fragment color
    fragColor = lampLightVertexColor;
}