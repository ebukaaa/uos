/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 2nd December 2018

    Description: 
        This window fragment shader uses the model position, texture coordinates and
        normals assigned in the square vertex shader to calculate the sun light 
        effects on the object materials. The view position is set in the model class 
        and the lightings for the texture is applied. The light object has 4  
        properties which are light object position, ambient, diffuse and specular. 
        The object materials are used to calculate the ambient, diffuse and specular
        effects on the applied texture. 
*/

// OpenGL version 3.30
#version 330 core

// input object position and texture coordinates
in vec3 aPos;
in vec2 aTexCoord;

// normal to calculate light directions
in vec3 aNormal;

// output fragment color
out vec4 fragColor;

// window texture
uniform sampler2D first_texture;

// camera position
uniform vec3 viewPosition;

// light properties
struct Light {

    vec3 position;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};
uniform Light light;  

// object materials properties
struct Material {

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};
uniform Material material;

void main() {

    // ambient effect on texture
    vec3 ambient = light.ambient * material.ambient * vec3(texture(first_texture, aTexCoord));//.rgb;

    // diffuse effect on texture
    vec3 norm = normalize(aNormal);
    vec3 lightDir = normalize(light.position - aPos);  
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = light.diffuse * (diff * material.diffuse) * vec3(texture(first_texture, aTexCoord));//.rgb;

    // specular effect on texture using object matrial shininess
    vec3 viewDir = normalize(viewPosition - aPos);
    vec3 reflectDir = reflect(-lightDir, norm);  
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = light.specular * (spec * material.specular);

    // set fragment color from derived result
    vec3 result = ambient + diffuse + specular;
    fragColor = vec4(result, 1.0);
}