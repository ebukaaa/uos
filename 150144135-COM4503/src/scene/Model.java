/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 30th November 2018

    Description: 
        This java class sets the model properies for each created objects. When  
        rendering, the object's vertex and fragment shaders are applied and the the 
        camera view are set based on object's model matrix. The fragment shaders are
        correctly updated for each object depending on light switch. This model 
        class appropriately apply textures to the objects since not every model use
        a texture. Then the object's fill buffer property is called to draw the 
        model objects and render to screen. Every model object also has a dispose 
        action to clear rendered object from screen.
        
*/

// Current package
package Scene;

// Import packages
import Models.*;
import Handlers.*;
import gmaths.*;

// JOGL library
import com.jogamp.opengl.*;

public class Model {
  
//    Light objects
    private WorldLight roomLight1, roomLight2;
    private SunLight sunLight;
    private LampLight lampLight;
    
//    Object properties
    private Material material;
    private Shader shader;
    private FillBuffer fillBuffers;
    private Camera camera;
    public Mat4 modelMatrix;

//    Attributes
    private int[] texture1; 
    private int[] texture2; 
    
//    Initialise object model properties
    public Model(GL3 gl, Camera camera, WorldLight light1, WorldLight light2, SunLight sunLight, LampLight lampLight, Shader shader, Material material, Mat4 modelMatrix, FillBuffer fillBuffers, int[] textureID1, int[] textureID2) {
        this.fillBuffers = fillBuffers;
        this.material = material;
        this.modelMatrix = modelMatrix;
        this.shader = shader;
        this.camera = camera;
        
        this.roomLight1 = light1;
        this.roomLight2 = light2;
        this.lampLight = lampLight;
        this.sunLight = sunLight;
        
        this.texture1 = textureID1;
        this.texture2 = textureID2;
    }

//    For light object
    public Model(GL3 gl, Camera camera, Shader shader, Material material, Mat4 model, FillBuffer fillBuffers) {
        this(gl, camera, null, null, null, null, shader, material, model, fillBuffers, null, null);
    }
//    For window object
    public Model(GL3 gl, Camera camera, SunLight sunLight, Shader shader, Material material, Mat4 model, FillBuffer fillBuffers, int[] texture1) {
        this(gl, camera, null, null, sunLight, null, shader, material, model, fillBuffers, texture1, null);
    }
    
//    Apply features and draw objects using their model matrix
    public void render(GL3 gl) {
        
        draw(gl, modelMatrix);
    }
    public void draw(GL3 gl, Mat4 modelMatrix) {

//        Model view project matrix using camera's perspective and view position
        Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
        
//        apply vertex and fragment shaders
        shader.use(gl);
        
//        convert matrix to array with "toFloatArrayForGLSL()" and set mvpMatrix in vertex shader
        shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
        
//        apply appropriate lightings to objects
        checkLight(modelMatrix, gl, WorldLight.isLightOn, SunLight.isLightOn, LampLight.isLightOn);
        
//        set fragment shader for objects with textures
        if (texture1 != null) {
//            set 1st texture in fragment shader
            shader.setInt(gl, "first_texture", 0);
            
//            activate 1st texture unit and affect texture
            gl.glActiveTexture(GL.GL_TEXTURE0);
            gl.glBindTexture(GL.GL_TEXTURE_2D, texture1[0]);
            
//          object has 2nd texture
        } if (texture2 != null) {
//            set 2nd texture in fragment shader
            shader.setInt(gl, "second_texture", 1);
            
//            activate 2nd texture unit and affect texture
            gl.glActiveTexture(GL.GL_TEXTURE1);
            gl.glBindTexture(GL.GL_TEXTURE_2D, texture2[0]);
        }
//        draw buffer/vertex array objects, applying features above
        fillBuffers.draw(gl);
    } 

//    Remove all applied features and objects from screen
    public void dispose(GL3 gl) {
        
        fillBuffers.dispose(gl);
        
//        object has a texture
        if (texture1 != null) {
            gl.glDeleteBuffers(1, texture1, 0);
            
//            or two textures
        } if (texture2 != null) {
            gl.glDeleteBuffers(1, texture2, 0);
        }
    }
    
//    Apply correct lighting to environment
    private void checkLight(Mat4 modelMatrix, GL3 gl, boolean roomLights, boolean  dayLight, boolean objectLight) {
        
        Material lightMaterial = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), 32.0f);
        Vec3 vector = new Vec3(0f,0f,0f);
        
//        room lights are set off
        if (roomLights == false) {
//            update room lights fragment shader to grey color
            shader.setFloat(gl, "vertexColor", 0.608f, 0.615f, 0.639f, 0.0f);
            
            if(roomLight1 != null && roomLight2 != null && lampLight != null) {
                
                roomLight1.material.diffuse = vector;
                roomLight1.material.specular = vector;
                roomLight2.material.diffuse = vector;
                roomLight2.material.specular = vector;
                
//                object material property
                material.diffuse = vector;
                material.specular = vector;
                material.shininess = 0f;
                
//                room direction light
                updateDirectLightsWhenLightsAreOff(gl);
                
//                activate room lights effects on object materials
                updateRoomLightsEffect(gl, modelMatrix);
            } 
        } 
//        room ligths are set on
        if(roomLights == true) {
//            update room lights fragment shader to white color
            shader.setFloat(gl, "vertexColor", 1.0f, 1.0f, 1.0f, 1.0f);
            
//            object material property
            material = new Material();
            
            if(roomLight1 != null && roomLight2 != null && lampLight != null) {
                
                roomLight1.material = lightMaterial;
                roomLight2.material = lightMaterial;
                
//                room direction light
                updateDirectLightsWhenLightsAreOn(gl);
                
//                activate room lights effects on object materials
                updateRoomLightsEffect(gl, modelMatrix);
            }  
        } 
//        lamp light is on
        if (objectLight == true) {
//            update lamp light fragment shader to white color
            shader.setFloat(gl, "lampLightVertexColor", 1.0f, 1.0f, 1.0f, 1.0f);
            
//            object material property
            material = new Material();
            
            if(roomLight1 != null && roomLight2 != null && lampLight != null) {
                
                lampLight.material = lightMaterial;
                
//                lamp direction light
                updateDirectLightsWhenLightsAreOn(gl);
                
                shader.setVec3(gl, "directLight.diffuse", lampLight.material.diffuse);
                shader.setVec3(gl, "directLight.specular", lampLight.material.specular);
                
//                lamp point light
                shader.setVec3(gl, "pointLights[3].position", lampLight.position);
                shader.setVec3(gl, "pointLights[3].ambient", lampLight.material.ambient);
                shader.setVec3(gl, "pointLights[3].diffuse", lampLight.material.diffuse);
                shader.setVec3(gl, "pointLights[3].specular", lampLight.material.specular);
                shader.setFloat(gl, "pointLights[3].constant", 1f);
                shader.setFloat(gl, "pointLights[3].linear", 0.09f);
                shader.setFloat(gl, "pointLights[3].quadratic", 0.032f);
            } 
        } 
//        night time
        if (dayLight == false) {
//            object material property
            material= new Material();
            
            if(sunLight != null) {
                sunLight.material.diffuse = new Vec3(0.4f, 0.4f, 0.4f);
                sunLight.material.specular = new Vec3(0.5f, 0.5f, 0.5f);
                
//                change sun light position to display night time
                sunLight.setPosition(new Vec3(-20, 12, 0));
                
                updateWindowShader(gl, modelMatrix);
            } 
            
//            for floor, wall, table and window frame models
            if(roomLight1 != null && roomLight2 != null && lampLight != null) {
//                direct light
                updateDirectLightsWhenLightsAreOff(gl);
                
//                activate sun light effects on object materials
                updateSunLightEffect(gl);
            } 
        } 
//        day time
        if (dayLight == true) {
//            object material property
            material = new Material();
            
            if(sunLight != null) {
                sunLight.material = lightMaterial;
                
//                change sun light position to show day time
                sunLight.setPosition(new Vec3(0, 12, 20));
                
                updateWindowShader(gl, modelMatrix);
            } 
            
//            for floor, wall, table and window frame models
            if(roomLight1 != null && roomLight2 != null && lampLight != null) {
//                direct light
                updateDirectLightsWhenLightsAreOn(gl);
                
//                activate sun light effects on object materials
                updateSunLightEffect(gl);
            }
        }  
    }
    
//    Update object fragment shaders per light switch
    private void updateShaders(GL3 gl, Mat4 modelMatrix) {
        
//        object vertex shader
        shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
        
//        object fragment shader
        shader.setVec3(gl, "viewPosition", camera.getPosition());
        shader.setVec3(gl, "material.ambient", material.ambient);
        shader.setVec3(gl, "material.diffuse", material.diffuse);
        shader.setVec3(gl, "material.specular", material.specular);
        shader.setFloat(gl, "material.shininess", material.shininess);
    }
    
//    Update window fragment shader
    private void updateWindowShader(GL3 gl, Mat4 modelMatrix) {
        
        updateShaders(gl, modelMatrix);

        shader.setVec3(gl, "light.position", sunLight.position);
        shader.setVec3(gl, "light.ambient", sunLight.material.ambient);
        shader.setVec3(gl, "light.diffuse", sunLight.material.diffuse);
        shader.setVec3(gl, "light.specular", sunLight.material.specular);
    }
    
//    Update light directions per light switch
    private void updateDirectLightsWhenLightsAreOff(GL3 gl) {
        
        shader.setVec3(gl, "directLight.ambient", new Vec3(0.05f, 0.05f, 0.05f));
        shader.setVec3(gl, "directLight.direction", new Vec3(-0.2f, -1.0f, -0.3f));
    }
    private void updateDirectLightsWhenLightsAreOn(GL3 gl) {
        
        Material lightMaterial = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), 32.0f);
        
        shader.setVec3(gl, "directLight.ambient", lightMaterial.ambient);
        shader.setVec3(gl, "directLight.direction", new Vec3(-0.8f, -2, -0.8f));
    }
    
//    Light effects on object materials 
    private void updateRoomLightsEffect(GL3 gl, Mat4 modelMatrix) {
        
        updateShaders(gl, modelMatrix);
        
//        room direction light property
        shader.setVec3(gl, "directLight.diffuse", roomLight1.material.diffuse);
        shader.setVec3(gl, "directLight.specular", roomLight1.material.specular);

//        room point light 1
        shader.setVec3(gl, "pointLights[0].position", roomLight1.position);
        shader.setVec3(gl, "pointLights[0].ambient", roomLight1.material.ambient);
        shader.setVec3(gl, "pointLights[0].diffuse", roomLight1.material.diffuse);
        shader.setVec3(gl, "pointLights[0].specular", roomLight1.material.specular);
        shader.setFloat(gl, "pointLights[0].constant", 1f);
        shader.setFloat(gl, "pointLights[0].linear", 0.09f);
        shader.setFloat(gl, "pointLights[0].quadratic", 0.032f);

//        room point light 2
        shader.setVec3(gl, "pointLights[1].position", roomLight2.position);
        shader.setVec3(gl, "pointLights[1].ambient", roomLight2.material.ambient);
        shader.setVec3(gl, "pointLights[1].diffuse", roomLight2.material.diffuse);
        shader.setVec3(gl, "pointLights[1].specular", roomLight2.material.specular);
        shader.setFloat(gl, "pointLights[1].constant", 1f);
        shader.setFloat(gl, "pointLights[1].linear", 0.09f);
        shader.setFloat(gl, "pointLights[1].quadratic", 0.032f);
    }
    private void updateSunLightEffect(GL3 gl) {
        
//        sun direction light property
        shader.setVec3(gl, "directLight.diffuse", sunLight.material.diffuse);
        shader.setVec3(gl, "directLight.specular", sunLight.material.specular);

//        sun point light
        shader.setVec3(gl, "pointLights[2].position", sunLight.position);
        shader.setVec3(gl, "pointLights[2].ambient", sunLight.material.ambient);
        shader.setVec3(gl, "pointLights[2].diffuse", sunLight.material.diffuse);
        shader.setVec3(gl, "pointLights[2].specular", sunLight.material.specular);
        shader.setFloat(gl, "pointLights[2].constant", 1f);
        shader.setFloat(gl, "pointLights[2].linear", 0.09f);
        shader.setFloat(gl, "pointLights[2].quadratic", 0.032f);
    }
}