/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 2nd December 2018

    Description: 
        This object fragment shader uses the model position, texture coordinates and
        normals assigned in the object vertex shader to calculate 2 types of light 
        effects on the object materials. The view position is set in the model class 
        and the lightings for a maximum of 2 textures are applied. The point light, 
        which is the light object itself, share similar properties with the 
        direction light except that the direction light include additional feature  
        to derive light attentions for the object ambient, diffuse and specular 
        properties. The view direction uses the view position set in the model class 
        and the inputted model position, and get normalised along with the normal 
        vector to calculate the directional light for different switch and point 
        lights for each light objects.
*/

// OpenGL version 3.30
#version 330 core

// number of point lights
#define POINT_LIGHTS 4

// input object position and texture coordinates
in vec3 aPos;
in vec2 aTexCoord;

// normal to calculate light directions
in vec3 aNormal;

// output fragment color
out vec4 fragColor;

// camera position
uniform vec3 viewPosition;

// textures
uniform sampler2D first_texture;
uniform sampler2D second_texture;

// object materials properties
struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
}; 
uniform Material material;

// object direction light properties
struct DirectLight {
    vec3 direction;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};
uniform DirectLight directLight;

// light properties
struct PointLight {
    // attenuation 
    float constant;
    float linear;
    float quadratic;

    vec3 position;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};
uniform PointLight pointLights[POINT_LIGHTS];


// Functions to calculate light direction, ambient, diffuse and specular 
vec3 CalculateDirectLight(DirectLight light, vec3 normal, vec3 viewDirection);
vec3 CalculatePointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir);

void main() {

    // normalise input normals from vertex shader and the view direction 
    vec3 norm = normalize(aNormal);
    vec3 viewDir = normalize(viewPosition - aPos);
    
    /* 
        Our lighting is set up in 2 phases: directional and point lights
        For each phase, a calculate function is defined that calculates the corresponding color
        per lamp. In the main() function we take all the calculated colors and sum them up for
        this fragment's final color.
    */

    // phase 1: directional lighting
    vec3 result = CalculateDirectLight(directLight, norm, viewDir);

    // phase 2: point lights
    for(int i = 0; i < POINT_LIGHTS; i++) {
        result += CalculatePointLight(pointLights[i], norm, aPos, viewDir); 
    }   
    
    // set fragment color to model object 
    fragColor = vec4(result, 1.0);
}

// Calculates directional light effect on object color for all light objects.
vec3 CalculateDirectLight(DirectLight light, vec3 normal, vec3 viewDirection) {

    vec3 lightDirection = normalize(-light.direction);

    // diffuse shading
    float diff = max(dot(normal, lightDirection), 0.0);

    // specular shading
    vec3 reflectDirection = reflect(-lightDirection, normal);
    float spec = pow(max(dot(viewDirection, reflectDirection), 0.0), material.shininess);

    // combine results
    vec3 ambient = light.ambient *  vec3(texture(first_texture, aTexCoord));
    vec3 diffuse = light.diffuse * (diff) * vec3(texture(first_texture, aTexCoord));
    vec3 specular = light.specular * (spec ) * vec3(texture(second_texture, aTexCoord));
            
    // return result of fragment color
    return (ambient + diffuse + specular);
}

// Calculates light model effects on object color 
vec3 CalculatePointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir) {
    
    // light direction 
    vec3 lightDir = normalize(light.position - fragPos);

    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);

    // specular shading
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);

    // attenuation
    float distance = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));    
    
    // combine results
    vec3 ambient = light.ambient  * vec3(texture(first_texture, aTexCoord));
    vec3 diffuse = light.diffuse * (diff ) * vec3(texture(first_texture, aTexCoord));
    vec3 specular = light.specular * (spec ) * vec3(texture(second_texture, aTexCoord));
    
    // include attenuation
    ambient *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;

    // return result of fragment color
    return (ambient + diffuse + specular);
}