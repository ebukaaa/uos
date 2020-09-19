/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 2nd December 2018

    Description: 
        This is a object vertex shader in which all objects model make use of. Each
        square vertex position and mvp matrix is obtained and passed down to the gl
        position which then set the vertex position in clip space. The model 
        position is gotten from the model matrix in the model class to set the view 
        direction in the square fragment shader. Same thing with normal and texture
        coordinates, however the normals need to be normalised.
*/

// OpenGL version 3.30
#version 330 core

// square vertex positions, normals and texture coordinates
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texCoord;

// output position, normal and texture coordinates to fragment shader
out vec3 aPos;
out vec3 aNormal;
out vec2 aTexCoord;

// set in model class 
uniform mat4 model;
uniform mat4 mvpMatrix;

void main() {

    /*
        set current position of vertices in clip space using the model view 
        project matrix
    */
    gl_Position = mvpMatrix * vec4(position, 1.0);

    // output model position
    aPos = vec3(model * vec4(position, 1.0f));

    // calculate light normals
    mat4 normalMatrix = transpose(inverse(model));
    vec3 norm = normalize(normal);

    // output normal to fragment shader to set lighting 
    aNormal = mat3(normalMatrix) * norm;
 
    // output texture coordinates
    aTexCoord = texCoord;
}