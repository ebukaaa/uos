/*
    Name: Ebuka Isiadinso
    ID: 150144135
    Module: COM4503
    Date: 30th November 2018

    Description: 
        This java class responds to 4 openGL events. Here all model objects are 
        created by initialising their model properties i.e. fill buffers, shader 
        etc. All objects are built based on respective model matrices. Thereafter,
        the object transforms are set and scene graph created to implement all model
        transformations. This scene graph class serves as a backend to the event
        listener in rendering and disposing all model objects. Such that, parameters 
        for animations are assigned here but controlled in the event listener class.
*/

// Import packages
import Models.*;
import Handlers.*;
import gmaths.*;

// JOGL library
import com.jogamp.opengl.*;

public class SceneGraph {
    
//    Scene and transforms
    private static SGNode root;
    public static TransformNode translateLampBase, rotateLampWireY, 
            rotateLampWireZ, rotateTopLampArmZ, rotateLampHeadY, 
            rotateBottomLampArmZ, rotateBottomLampArmY, rotateClockHandY, scaleLamp;
    
    public static Vec3 translateToPoint = new Vec3(0, 0, 0);
    public static float defaultAngle = 22.5f, rotateLampPart = 22.5f;
    
//    Models
    private static WorldLight roomLight1, roomLight2;
    private static SunLight sunLight;
    public static LampLight lampLight;
    private static Square wall, window, floor, paper;
    private static Sphere lampArm, clockHand, laptopNode, lampHeadSphere, lampBaseNode;
    private static Cube table, windowFrame, lampBase, clock, postBox, laptop, 
            laptopScreen, laptopKeyboard, lampWire, roomLightHolder;
    
    private static Mat4 modelMatrix;
        
//    Animations
    public static float postBoxHeight = 1.3f;
    public static boolean rotateLampArroundAnimation = false;
    public static boolean lampJumpAnimation = false;
    public static boolean randomPoseAnimation = false;
    
    public static boolean moveLeftInFront = true;
    public static boolean moveBackOnTheLeft = false;
    public static boolean moveRightAtTheBack = false;
    public static boolean moveFrontOnTheRight = false;
    
    public static double savedTime;
    public static double startTime;
    

//    Build models in scene
    private static void createModels(GL3 gl, Camera camera){
        
//        create lights
        Vec3 position = new Vec3(5, 12, 0);
        modelMatrix = Mat4Transform.scale(4, 1, 3);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(position), modelMatrix);
        roomLight1 = new WorldLight(gl, camera, position, modelMatrix);
        
        position = new Vec3(-5, 12, 0);
        modelMatrix = Mat4Transform.scale(4, 1, 3);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(position), modelMatrix);
        roomLight2 = new WorldLight(gl, camera, position, modelMatrix);
        
        position = new Vec3(0, 12, 20);//new Vec3(-20, 12, 0);//new Vec3(-20, 12, -40);
        modelMatrix = Mat4Transform.scale(1,1,1);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(position), modelMatrix);
        sunLight = new SunLight(gl, camera, position, modelMatrix);
        
        modelMatrix = Mat4Transform.scale(1,1,1);
        lampLight = new LampLight(gl, camera, modelMatrix);
        
//        create floor
        int [] texture = TextureLibrary.loadTexture(gl, "textures/GroundTireTracks diffuse.jpg");
        int [] texture2 = TextureLibrary.loadTexture(gl, "textures/GroundTireTracks reflect.jpg");
        modelMatrix = Mat4Transform.scale(16,1,16);
        floor = new Square(gl, camera, roomLight1, roomLight1, sunLight, lampLight, texture, texture2, modelMatrix);
        
//        create room light holde
        texture = TextureLibrary.loadTexture(gl, "textures/Metal diffuse.jpg");
        texture2 = TextureLibrary.loadTexture(gl, "textures/Metal reflect.jpg");
        roomLightHolder = new Cube(gl, camera, roomLight1, roomLight2, sunLight, lampLight, texture, texture2, modelMatrix);

//        create window
        texture = TextureLibrary.loadTexture(gl, "textures/outside.jpeg");
        modelMatrix = Mat4Transform.scale(16, 1, 16);
        window = new Square(gl, camera, sunLight, texture, modelMatrix);
        
//        create wall
        texture = TextureLibrary.loadTexture(gl, "textures/Marble diffuse.jpg");
        texture2 = TextureLibrary.loadTexture(gl, "textures/Marble reflection.jpg");
        modelMatrix = Mat4Transform.scale(4,1,4);
        wall = new Square(gl, camera, roomLight1, roomLight2, sunLight, lampLight, texture, texture2, modelMatrix);

//        create table
        texture = TextureLibrary.loadTexture(gl, "textures/Wood diffuse.jpg");
        texture2 = TextureLibrary.loadTexture(gl, "textures/Wood reflect.jpg");
        modelMatrix = Mat4Transform.scale(2,4,2);
        table = new Cube(gl, camera, roomLight1, roomLight2, sunLight, lampLight, texture, texture2, modelMatrix);
        
//        create window frame
        texture = TextureLibrary.loadTexture(gl, "textures/mar0kuu2.jpg");
        texture2 = TextureLibrary.loadTexture(gl, "textures/mar0kuu2_specular.jpg");
        windowFrame = new Cube(gl, camera, roomLight1, roomLight2, sunLight, lampLight, texture, texture2, modelMatrix);
        
//        create lamp
        texture = TextureLibrary.loadTexture(gl, "textures/Plaster diffuse.jpg");
        texture2 = TextureLibrary.loadTexture(gl, "textures/Plaster reflect.jpg");
        modelMatrix = Mat4Transform.scale(4,4,4);
        lampArm = new Sphere(gl, camera, roomLight1, roomLight2, sunLight, lampLight, texture, texture2, modelMatrix);
        
        lampBase = new Cube(gl, camera, roomLight1, roomLight2, sunLight, lampLight, texture, texture2, modelMatrix);
        
        texture = TextureLibrary.loadTexture(gl, "textures/Metal diffuse.jpg");
        texture2 = TextureLibrary.loadTexture(gl, "textures/Metal reflect.jpg");
        lampHeadSphere = new Sphere(gl, camera, roomLight1, roomLight2, sunLight, lampLight, texture, texture2, modelMatrix);
        
        lampWire = new Cube(gl, camera, roomLight1, roomLight2, sunLight, lampLight, texture, texture2, modelMatrix);
        
        lampBaseNode = new Sphere(gl, camera, roomLight1, roomLight2, sunLight, lampLight, texture, texture2, modelMatrix);
        
//        create other objects
        texture = TextureLibrary.loadTexture(gl, "textures/paper.jpg");
        modelMatrix = Mat4Transform.scale(2, 1, 2);
        paper = new Square(gl, camera, roomLight1, roomLight2, sunLight, lampLight, texture, modelMatrix);
        
//        clock
        texture = TextureLibrary.loadTexture(gl, "textures/clock.jpg");
        modelMatrix = new Mat4();
        clock = new Cube(gl, camera, roomLight1, roomLight2, sunLight, lampLight, texture, modelMatrix);
        
        texture = TextureLibrary.loadTexture(gl, "textures/Metal diffuse.jpg");
        texture2 = TextureLibrary.loadTexture(gl, "textures/Metal reflect.jpg");
        modelMatrix = Mat4Transform.scale(1,1,1); 
        clockHand = new Sphere(gl, camera, roomLight1, roomLight2, sunLight, lampLight, texture, texture2, modelMatrix);
        
        texture = TextureLibrary.loadTexture(gl, "textures/FabricRope diffuse.jpg");
        texture2 = TextureLibrary.loadTexture(gl, "textures/FabricRope reflect.jpg");
        modelMatrix = Mat4Transform.scale(1, 1, 1);
        postBox = new Cube(gl, camera, roomLight1, roomLight2, sunLight, lampLight, texture, texture2, modelMatrix);
        
//        laptop
        texture = TextureLibrary.loadTexture(gl, "textures/skin.jpg");   
        modelMatrix = Mat4Transform.scale(1, 1, 1);
        laptop = new Cube(gl, camera, roomLight1, roomLight2, sunLight, lampLight, texture, modelMatrix);
        
        texture = TextureLibrary.loadTexture(gl, "textures/ven0aaa2.jpg");
        texture2 = TextureLibrary.loadTexture(gl, "textures/ven0aaa2_specular.jpg");
        modelMatrix = Mat4Transform.scale(4,1,4);
        laptopScreen = new Cube(gl, camera, roomLight1, roomLight2, sunLight, lampLight, texture, texture2, modelMatrix);
        
        texture = TextureLibrary.loadTexture(gl, "textures/GroundForest diffuse.jpg");   
        texture2 = TextureLibrary.loadTexture(gl, "textures/GroundForest reflect.jpg");
        modelMatrix = Mat4Transform.scale(1, 1, 1);
        laptopNode = new Sphere(gl, camera, roomLight1, roomLight2, sunLight, lampLight, texture, texture2, modelMatrix);
        
        texture = TextureLibrary.loadTexture(gl, "textures/keyboard.jpg");
        laptopKeyboard = new Cube(gl, camera, roomLight1, roomLight2, sunLight, lampLight, texture, modelMatrix);
    }

//    Initialise scene graph
    public static void initialise(GL3 gl, Camera camera) {
        
        createModels(gl, camera);
        
        root = new NameNode("Foundation Structure");
        NameNode room = new NameNode("Room");
        
        /*
            Room light holder transforms
        */
        float roomLigtHolderHeight = 10;
        float roomLigtHolderScale = 0.2f;
        NameNode firstRoomLightHolder = new NameNode("First room light holder");
        TransformNode translateFirstRoomLightHolder = new TransformNode("translateFirstRoomLightHolder", Mat4Transform.translate(roomLight1.position.x, roomLight1.position.y + roomLigtHolderHeight*0.5f, roomLight1.position.z));
            Mat4 transform = Mat4Transform.scale(roomLigtHolderScale, roomLigtHolderHeight, roomLigtHolderScale);
            TransformNode firstRoomLightHolderTransform = new TransformNode("firstRoomLightHolderTransform", transform);
                ModelNode firstRoomLightHolderShape = new ModelNode("firstRoomLightHolderShape", roomLightHolder.model);
                
        NameNode secondRoomLightHolder = new NameNode("Second room light holder");
        TransformNode translateSecondRoomLightHolder = new TransformNode("translateSecondRoomLightHolder", Mat4Transform.translate(roomLight2.position.x, roomLight2.position.y + roomLigtHolderHeight*0.5f, roomLight2.position.z));
            transform = Mat4Transform.scale(roomLigtHolderScale, roomLigtHolderHeight, roomLigtHolderScale);
            TransformNode secondRoomLightHolderTransform = new TransformNode("secondRoomLightHolderTransform", transform);
                ModelNode secondRoomLightHolderShape = new ModelNode("secondRoomLightHolderShape", roomLightHolder.model);
                
        /*
            Wall transforms
        */
        float wallLength = 8;
        float wallWidth = 4;
        TransformNode translateWall = new TransformNode("translateWall", Mat4Transform.translate(0, 0, -8));
            NameNode topWall = new NameNode("Top wall");
                transform = Mat4Transform.rotateAroundX(90); 
                transform = Mat4.multiply(Mat4Transform.scale(wallLength, wallWidth, 1), transform);
                transform = Mat4.multiply(Mat4Transform.translate(0, 14, 0), transform);
                TransformNode topWallTransform = new TransformNode("topWallTransform", transform);
                    ModelNode topWallShape = new ModelNode("Wall(top)", wall.model);

            NameNode bottomWall = new NameNode("Bottom wall");
                transform = Mat4Transform.rotateAroundX(90); 
                transform = Mat4.multiply(Mat4Transform.scale(wallLength, wallWidth,1), transform);
                transform = Mat4.multiply(Mat4Transform.translate(0, 2, 0), transform);
                TransformNode bottomWallTransform = new TransformNode("bottomWallTransform", transform);
                    ModelNode bottomWallShape = new ModelNode("Wall(bottom)", wall.model);
                    
            float cornerWallWidth = 16;
            TransformNode translateCornerWalls = new TransformNode("translateCornerWalls", Mat4Transform.translate(0, 8, 0));
                NameNode leftWall = new NameNode("Left wall");
                    transform = Mat4Transform.rotateAroundX(90);
                    transform = Mat4.multiply(Mat4Transform.scale(wallWidth, cornerWallWidth, 1), transform); 
                    transform = Mat4.multiply(Mat4Transform.translate(-6, 0, 0), transform);
                    TransformNode leftWallTransform = new TransformNode("leftWallTransform", transform);
                        ModelNode leftWallShape = new ModelNode("Wall(left)", wall.model);

                NameNode rightWall = new NameNode("Right wall");
                    transform = Mat4Transform.rotateAroundX(90);
                    transform = Mat4.multiply(Mat4Transform.scale(wallWidth, cornerWallWidth, 1), transform); 
                    transform = Mat4.multiply(Mat4Transform.translate(6, 0, 0), transform);
                    TransformNode rightWallTransform = new TransformNode("rightWallTransform", transform);
                        ModelNode rightWallShape = new ModelNode("Wall(right)", wall.model);
            
        /*
            Window transforms
        */
        TransformNode translateWindow = new TransformNode("translateWindow", Mat4Transform.translate(0, 8, -9));
        NameNode windowView = new NameNode("Window");
            transform = window.model.modelMatrix;
            transform = Mat4.multiply(Mat4Transform.rotateAroundX(90), transform);
            TransformNode windowTransform = new TransformNode("windowTransform", transform);
                ModelNode windowShape = new ModelNode("windowShape", window.model);
                
        /*
            Window frame transforms
        */
        float windowFrameLength = 0.5f;
        float windowFrameHeight = 7.1f;
        float windowFrameDepth = 0.3f;
        TransformNode translateWindowFrames = new TransformNode("translateWindowFrames", Mat4Transform.translate(0, 0, -7.9f));
            NameNode topWindowFrame = new NameNode("Top window frame");
                transform = Mat4Transform.scale(windowFrameLength, windowFrameHeight, windowFrameDepth);
                transform = Mat4.multiply(Mat4Transform.rotateAroundZ(90), transform); 
                transform = Mat4.multiply(Mat4Transform.translate(0, 11.8f, 0), transform);
                transform = Mat4.multiply(Mat4Transform.scale(1.22f, 1, 1), transform);
                TransformNode topWindowFrameTransform = new TransformNode("topWindowFrameTransform", transform);
                    ModelNode topWindowFrameShape = new ModelNode("Window Frame(top)", windowFrame.model);

            NameNode bottomWindowFrame = new NameNode("Bottom window frame");
                transform = Mat4Transform.scale(windowFrameLength, windowFrameHeight, windowFrameDepth);
                transform = Mat4.multiply(Mat4Transform.rotateAroundZ(90), transform);
                transform = Mat4.multiply(Mat4Transform.translate(0, 4.25f, 0), transform); 
                transform = Mat4.multiply(Mat4Transform.scale(1.22f, 1, 1), transform);
                TransformNode bottomWindowFrameTransform = new TransformNode("bottomWindowFrameTransform", transform);
                    ModelNode bottomWindowFrameShape = new ModelNode("Window Frame(bottom)", windowFrame.model);
        
        TransformNode translateOtherFrames = new TransformNode("translateOtherFrames", Mat4Transform.translate(0, 8, 0));
            NameNode leftWindowFrame = new NameNode("Left window frame");
                transform = Mat4Transform.scale(windowFrameLength, windowFrameHeight, windowFrameDepth);
                transform = Mat4.multiply(Mat4Transform.translate(-4.1f, 0, 0), transform);
                TransformNode leftWindowFrameTransform = new TransformNode("leftWindowFrameTransform", transform);
                    ModelNode leftWindowFrameShape = new ModelNode("Window Frame(left)", windowFrame.model);

            NameNode rightWindowFrame = new NameNode("Right window frame");
                transform = Mat4Transform.scale(windowFrameLength, windowFrameHeight, windowFrameDepth);
                transform = Mat4.multiply(Mat4Transform.translate(4.1f, 0, 0), transform);
                TransformNode rightWindowFrameTransform = new TransformNode("rightWindowFrameTransform", transform);
                    ModelNode rightWindowFrameShape = new ModelNode("Window Frame(right)", windowFrame.model);
                
        /*
            Table transforms
        */
        float legScale = 0.1f;
        float legHeight = 1.8f;
        TransformNode translateTable = new TransformNode("translateTable", Mat4Transform.translate(0, legHeight, 0));
        
//            table feets
            TransformNode translateBackTableFeet = new TransformNode("translateBackTableFeet", Mat4Transform.translate(0, 0, -7.5f)); 
                NameNode firstTableFoot = new NameNode("First table foot");
                    transform = table.model.modelMatrix;
                    transform = Mat4.multiply(Mat4Transform.rotateAroundX(90), transform);
                    transform = Mat4.multiply(Mat4Transform.scale(legScale, legHeight, legScale), transform);
                    transform = Mat4.multiply(Mat4Transform.translate(-4, 0, 0), transform);
                    TransformNode firstTableFootTansform = new TransformNode("firstTableFootTansform", transform);
                        ModelNode firstTableFootShape = new ModelNode("Table foot(1)", table.model);

                NameNode secondTableFoot = new NameNode("Second table foot");
                    transform = table.model.modelMatrix;
                    transform = Mat4.multiply(Mat4Transform.rotateAroundX(90), transform);
                    transform = Mat4.multiply(Mat4Transform.scale(legScale, legHeight, legScale), transform);
                    transform = Mat4.multiply(Mat4Transform.translate(4, 0, 0), transform);
                    TransformNode secondTableFootTansform = new TransformNode("secondTableFootTansform", transform);
                        ModelNode secondTableFootShape = new ModelNode("Table foot(2)", table.model);

            TransformNode translateFrontTableFeet = new TransformNode("translateFrontTableFeet", Mat4Transform.translate(0, 0, 4.5f));
                NameNode thirdTableFoot = new NameNode("Third table foot");
                    transform = table.model.modelMatrix;
                    transform = Mat4.multiply(Mat4Transform.rotateAroundX(90), transform);
                    transform = Mat4.multiply(Mat4Transform.scale(legScale, legHeight, legScale), transform);
                    transform = Mat4.multiply(Mat4Transform.translate(4, 0, 0), transform);
                    TransformNode thirdTableFootTansform = new TransformNode("thirdTableFootTansform", transform);
                        ModelNode thirdTableFootShape = new ModelNode("Table foot(3)", table.model);

                NameNode fourthTableFoot = new NameNode("Fourth table foot");
                    transform = table.model.modelMatrix;
                    transform = Mat4.multiply(Mat4Transform.rotateAroundX(90), transform);
                    transform = Mat4.multiply(Mat4Transform.scale(legScale, legHeight, legScale), transform);
                    transform = Mat4.multiply(Mat4Transform.translate(-4, 0, 0), transform);
                    TransformNode fourthTableFootTansform = new TransformNode("fourthTableFootTansform", transform);
                        ModelNode fourthTableFootShape = new ModelNode("Table foot(4)", table.model);
                
//            table top
            NameNode tableTop = new NameNode("Table top");
                transform = Mat4Transform.scale(10, 0.2f, 13);
                transform = Mat4.multiply(Mat4Transform.translate(0, 1.9f, -1.5f), transform);
                TransformNode tableTopTansform = new TransformNode("tableTopTansform", transform);
                    ModelNode tableTopShape = new ModelNode("Table (top)", table.model);
            
//        objects on table transforms
        TransformNode translateToTableTop = new TransformNode("translateToTableTop", Mat4Transform.translate(0, 4f, 0));
       
            /*
                Laptop transforms
            */
            float laptopWidth = 3;
            float laptopDepth = 0.2f;
            float laptopLength = 2;
            
//            laptop base
            NameNode laptopBase = new NameNode("Laptop base");
            TransformNode translateLaptopBase = new TransformNode("translateLaptopBase", Mat4Transform.translate(-3, -0.1f, -4));
                transform = new Mat4(1);
                transform = Mat4.multiply(transform, Mat4Transform.scale(laptopWidth, laptopDepth, laptopLength));
                TransformNode laptopBaseTransform = new TransformNode("laptopBaseTransform", transform);
                    ModelNode laptopBaseShape = new ModelNode("laptopBaseShape", laptop.model);

//            laptop connection node
            float laptopNodeHeight = 3;
            NameNode nodeForLaptop = new NameNode("Laptop node");
            TransformNode translateLaptopNode = new TransformNode("translateLaptopNode", Mat4Transform.translate(0, 0f, -0.8f));
                transform = new Mat4(1);
                transform = Mat4.multiply(transform, Mat4Transform.scale(0.5f, laptopNodeHeight, 0.5f));
                transform = Mat4.multiply(Mat4Transform.rotateAroundZ(90), transform);
                TransformNode laptopNodeTransform = new TransformNode("laptopNodeTransform", transform);
                    ModelNode laptopNodeShape = new ModelNode("Lamp Node", laptopNode.model);

//            laptop head
            NameNode laptopHead = new NameNode("Laptop head");
            TransformNode translateLaptopHead = new TransformNode("translateLaptopHead", Mat4Transform.translate(0, laptopLength*0.5f, -0.5f));
            transform = new Mat4(1);
            transform = Mat4.multiply(transform, Mat4Transform.scale(laptopWidth, laptopDepth, laptopLength));
            transform = Mat4.multiply(Mat4Transform.rotateAroundX(60), transform);
            TransformNode laptopHeadTransform = new TransformNode("laptopHeadTransform", transform);
                ModelNode laptopHeadShape = new ModelNode("laptopHeadShape", laptop.model);

//            laptop screen
            NameNode screenForLaptop = new NameNode("Laptop screen");
            TransformNode translateLaptopScreen = new TransformNode("translateLaptopScreen", Mat4Transform.translate(0, 0, 0.1f));
            transform = new Mat4(1);
            transform = Mat4.multiply(transform, Mat4Transform.scale(2.5f, laptopDepth*0.5f, 1.5f));
            transform = Mat4.multiply(Mat4Transform.rotateAroundX(60), transform);
            TransformNode laptopScreenTransform = new TransformNode("laptopScreenTransform", transform);
                ModelNode laptopScreenShape = new ModelNode("laptopScreenShape", laptopScreen.model);

//            laptop keyboard
            NameNode laptopKeyboardView = new NameNode("Laptop keyboard");
            TransformNode translateLaptopKeyboard = new TransformNode("translateLaptopKeyboard", Mat4Transform.translate(0, 0.07f, 0));
            transform = new Mat4(1);
            transform = Mat4.multiply(transform, Mat4Transform.scale(2.8f, laptopDepth*0.5f, 1.8f));
            TransformNode laptopKeyboardTransform = new TransformNode("laptopKeyboardTransform", transform);
                ModelNode laptopKeyboardShape = new ModelNode("laptopKeyboardShape", laptopKeyboard.model);
        
            /*
                Post box transforms
            */
            float postBoxScale = 2f;
            NameNode postBoxView = new NameNode("Post box");
            TransformNode translatePostBox = new TransformNode("translatePostBox", Mat4Transform.translate(3, 0.3f, 2));
                transform = new Mat4(1);
                transform = Mat4.multiply(transform, Mat4Transform.scale(postBoxScale, postBoxHeight, postBoxScale));
                TransformNode postBoxTransform = new TransformNode("postBoxTransform", transform);
                    ModelNode postBoxShape = new ModelNode("postBoxShape", postBox.model);
                    
//            post paper
            NameNode postPaper = new NameNode("Post paper");
            TransformNode translatePostPaper = new TransformNode("translatePostPaper", Mat4Transform.translate(0, postBoxHeight-0.63f, 0));
                transform = new Mat4(1);
                transform = Mat4.multiply(transform, Mat4Transform.scale(1, 1, 1));
                TransformNode postPaperTransform = new TransformNode("postPaperTransform", transform);
                    ModelNode postPaperShape = new ModelNode("postPaperShape", paper.model);
                    
            /*
                Paper transforms
            */
            TransformNode translatePaper = new TransformNode("translatePaper", Mat4Transform.translate(0, -0.15f, 0));
            NameNode paperView = new NameNode("First paper");
                transform = Mat4Transform.scale(2, 1, 2);
                transform = Mat4.multiply(Mat4Transform.translate(-3, 0, 0), transform);
                TransformNode paperTransform = new TransformNode("firstPaperTransform", transform);
                    ModelNode paperShape = new ModelNode("Paper(1)", paper.model); 

            NameNode secondPaper = new NameNode("Second paper");
                transform = Mat4Transform.scale(2, 1, 2);
                transform = Mat4.multiply(Mat4Transform.rotateAroundY(38), transform);
                transform = Mat4.multiply(Mat4Transform.translate(-3, 0, 0), transform);
                TransformNode secondPaperTransform = new TransformNode("secondPaperTransform", transform);
                    ModelNode secondPaperShape = new ModelNode("Paper(2)", paper.model); 
             
            /*
                Clock transforms
            */
            NameNode clockView = new NameNode("Clock");
                transform = Mat4Transform.scale(3, 0.2f, 3);
                transform = Mat4.multiply(Mat4Transform.rotateAroundX(16), transform);
                transform = Mat4.multiply(Mat4Transform.translate(2, 0.23f, -6.5f), transform);
                TransformNode clockTransform = new TransformNode("clockTransform", transform);
                    ModelNode clockShape = new ModelNode("clockShape", clock.model); 

//            clock hand
            float clockHandScale = 0.1f;
            float clockHandHeight = 0.5f;
            NameNode clockHandView = new NameNode("Clock hand");
            rotateClockHandY = new TransformNode("rotateClockHandY", Mat4Transform.rotateAroundY(rotateLampPart));
            TransformNode translateClockHand = new TransformNode("translateClockHand", Mat4Transform.translate(0, 0.8f, 0));
                transform = new Mat4(1);
                transform = Mat4.multiply(transform, Mat4Transform.scale(clockHandScale, clockHandHeight, clockHandScale));
                transform = Mat4.multiply(Mat4Transform.rotateAroundZ(90), transform);
                transform = Mat4.multiply(Mat4Transform.rotateAroundY(40), transform);
                transform = Mat4.multiply(transform, Mat4Transform.translate(0, 0.5f, 0));
                TransformNode clockHandTransform = new TransformNode("clockHandTransform", transform);
                    ModelNode clockHandShape = new ModelNode("clockHandShape", clockHand.model);
               
            /*
                Lamp transforms
            */
            scaleLamp = new TransformNode("scaleLamp", Mat4.multiply(new Mat4(1), Mat4Transform.scale(1f, 1f, 1f)));
            
    //        lamp base
            float lampBaseHeight = 0.5f;
            float lampBaseScale = 1;
            NameNode baseForLamp = new NameNode("Lamp base");
            translateLampBase = new TransformNode("translateLampBase", Mat4Transform.translate(translateToPoint));
                transform = new Mat4(1);
                transform = Mat4.multiply(transform, Mat4Transform.scale(lampBaseScale, lampBaseHeight, lampBaseScale));
                TransformNode lampBaseTransform = new TransformNode("lampBaseTransform", transform);
                    ModelNode lampBasehape = new ModelNode("lampBasehape", lampBase.model);
                    
//            lamp base node
            float lampBaseNodeScale = 0.3f;
            NameNode nodeForLampBase = new NameNode("Lamp node");
            TransformNode translateLampBaseNode = new TransformNode("translateLampNode", Mat4Transform.translate(-0.5f, 0, 0));
                transform = new Mat4(1);
                transform = Mat4.multiply(transform, Mat4Transform.scale(lampBaseNodeScale, lampBaseNodeScale, lampBaseNodeScale));
                TransformNode lampBaseNodeTransform = new TransformNode("lampNodeTransform", transform);
                    ModelNode lampBaseNodeShape = new ModelNode("lampNodeShape", lampBaseNode.model);
                    
//            lamp wire
            NameNode wireForLamp = new NameNode("Lamp wire");
            rotateLampWireY = new TransformNode("rotateLampWireY", Mat4Transform.rotateAroundY(rotateLampPart));
            rotateLampWireZ = new TransformNode("rotateLampWireY", Mat4Transform.rotateAroundZ(rotateLampPart));
            TransformNode translateLampWire = new TransformNode("translateLampWire", Mat4Transform.translate(0, -0.07f, 0));
                transform = new Mat4(1);
                transform = Mat4.multiply(transform, Mat4Transform.scale(3, 0.1f, 0.1f));
                transform = Mat4.multiply(Mat4Transform.rotateAroundZ(25), transform);
                transform = Mat4.multiply(Mat4Transform.rotateAroundY(180), transform);
                
                transform = Mat4.multiply(transform, Mat4Transform.translate(0.5f, 0.5f, 0));
                TransformNode lampWireTransform = new TransformNode("lampWireTransform", transform);
                    ModelNode lampWireShape = new ModelNode("lampWireShape", lampWire.model);
                    
            float lampArmScale = 0.5f;
            float lampArmHeight = 2;
            
    //        lamp bottom arm
            NameNode bottomLampArm = new NameNode("Lamp bottom arm");
            rotateBottomLampArmY = new TransformNode("rotateBottomLampAroundY", Mat4Transform.rotateAroundY(rotateLampPart));
            rotateBottomLampArmZ = new TransformNode("rotateBottomLampArmZ", Mat4Transform.rotateAroundZ(rotateLampPart));
            TransformNode translateBottomLampArm = new TransformNode("translateBottomLampArm", Mat4Transform.translate(-0.2f, lampBaseHeight, 0));
                transform = new Mat4(1);
                transform = Mat4.multiply(transform, Mat4Transform.scale(lampArmScale, lampArmHeight, lampArmScale));
                transform = Mat4.multiply(Mat4Transform.rotateAroundZ(15), transform); 
                transform = Mat4.multiply(transform, Mat4Transform.translate(0, 0.3f, 0));
                TransformNode bottomLampArmTransform = new TransformNode("bottomLampArmTransform", transform);
                    ModelNode bottomLampArmShape = new ModelNode("bottomLampArmShape", lampArm.model);

    //        lamp connection node
            float lampNodeSclae = 0.5f;
            NameNode lampNode = new NameNode("Lamp node");
            TransformNode translateLampNode = new TransformNode("translateLampNode", Mat4Transform.translate(-(lampNodeSclae-0.1f), lampArmHeight-0.3f, 0));
                transform = new Mat4(1);
                transform = Mat4.multiply(transform, Mat4Transform.scale(lampNodeSclae, lampNodeSclae, lampNodeSclae));
                TransformNode lampNodeTransform = new TransformNode("lampNodeTransform", transform);
                    ModelNode lampNodeShape = new ModelNode("lampNodeShape", lampArm.model);

    //        lamp top arm
            NameNode topLampArm = new NameNode("Lamp upper arm");
            rotateTopLampArmZ = new TransformNode("lamp hop", Mat4Transform.rotateAroundZ(rotateLampPart));
            TransformNode translateTopLampArm = new TransformNode("translateTopLampArm", Mat4Transform.translate(-(lampArmScale-0.1f), lampArmHeight-0.3f, 0));
                transform = new Mat4(1);
                transform = Mat4.multiply(transform, Mat4Transform.scale(lampArmScale, lampArmHeight, lampArmScale));
                transform = Mat4.multiply(Mat4Transform.rotateAroundZ(-60), transform);
                transform = Mat4.multiply(transform, Mat4Transform.translate(0, 0.5f, 0));
                TransformNode topLampArmTransform = new TransformNode("topLampArmTransform", transform);
                    ModelNode topLampArmShape = new ModelNode("topLampArmShape", lampArm.model);

//            lamp head
            float lampHeadScale = 1.5f;
            float lampHeadHeight = 0.9f;
            NameNode headForLamp = new NameNode("Lamp head");
            rotateLampHeadY = new TransformNode("rotateLampHeadY", Mat4Transform.rotateAroundY(0));
            TransformNode translateLampHead = new TransformNode("translateLampHead", Mat4Transform.translate(2f, lampArmHeight-0.1f, 0));
                transform = new Mat4(1);
                transform = Mat4.multiply(transform, Mat4Transform.scale(lampHeadScale, lampHeadHeight, lampHeadScale));
                transform = Mat4.multiply(Mat4Transform.rotateAroundZ(150), transform);
                transform = Mat4.multiply(transform, Mat4Transform.translate(0, 0.5f, 0));
                TransformNode lampHeadTransform = new TransformNode("lampHeadTransform", transform);
                    ModelNode lampHeadShape = new ModelNode("lampHeadShape", lampBase.model);

//            lamp light
            float lampLightYPosition = 8.8f;
            NameNode lightForLamp = new NameNode("lampLight");
            TransformNode translateLampLight = new TransformNode("translateLampLight", Mat4Transform.translate(0.81f, lampHeadHeight-1.5f, 0));
                transform = new Mat4(1);
                transform = Mat4.multiply(transform, Mat4Transform.scale(0.5f, 0.5f, 0.5f));
                transform = Mat4.multiply(Mat4Transform.rotateAroundZ(150), transform);
                transform = Mat4.multiply(transform, Mat4Transform.translate(0, 0.5f, 0));
                lampLight.position = new Vec3(0.3f, lampLightYPosition, 0);
                lampLight.model.modelMatrix =  Mat4Transform.translate(lampLight.position);
                TransformNode lampLightTransform = new TransformNode("lampLightTransform", transform);
                    ModelNode lampLightShape = new ModelNode("lampLightShape", lampLight.model);
                
            NameNode sphereOnLampHead = new NameNode("Sphere on lamp head");
                transform = new Mat4(1);
                transform = Mat4.multiply(transform, Mat4Transform.scale(1, 1, 1));
                TransformNode sphereOnLampHeadTransform = new TransformNode("sphereOnLampHeadTransform", transform);
                    ModelNode sphereOnLampHeadShape = new ModelNode("sphereOnLampHeadShape", lampHeadSphere.model);                    
                 
//        scene graph
        root.addChild(room);
        
//        room light holders
        room.addChild(firstRoomLightHolder);
            firstRoomLightHolder.addChild(translateFirstRoomLightHolder);
                translateFirstRoomLightHolder.addChild(firstRoomLightHolderTransform);
                    firstRoomLightHolderTransform.addChild(firstRoomLightHolderShape);
        
        room.addChild(secondRoomLightHolder);
            secondRoomLightHolder.addChild(translateSecondRoomLightHolder);
                translateSecondRoomLightHolder.addChild(secondRoomLightHolderTransform);
                    secondRoomLightHolderTransform.addChild(secondRoomLightHolderShape);
//        wall
        room.addChild(translateWall);
        
//            top wall
            translateWall.addChild(topWall);
                topWall.addChild(topWallTransform);
                    topWallTransform.addChild(topWallShape);

//            bottom wall
            translateWall.addChild(bottomWall);
                bottomWall.addChild(bottomWallTransform);
                    bottomWallTransform.addChild(bottomWallShape);

//            left and right walls
            translateWall.addChild(translateCornerWalls);
                translateCornerWalls.addChild(leftWall);
                    leftWall.addChild(leftWallTransform);
                        leftWallTransform.addChild(leftWallShape);

                translateCornerWalls.addChild(rightWall);
                    rightWall.addChild(rightWallTransform);
                        rightWallTransform.addChild(rightWallShape);
             
//        window - outside
        room.addChild(translateWindow);
            translateWindow.addChild(windowView);
                windowView.addChild(windowTransform);
                    windowTransform.addChild(windowShape);
                    
//        window frames
        room.addChild(translateWindowFrames);
    
//            top window frame
            translateWindowFrames.addChild(topWindowFrame);
                topWindowFrame.addChild(topWindowFrameTransform);
                    topWindowFrameTransform.addChild(topWindowFrameShape);
                    
//            bottom window frame
            translateWindowFrames.addChild(bottomWindowFrame);
                bottomWindowFrame.addChild(bottomWindowFrameTransform);
                    bottomWindowFrameTransform.addChild(bottomWindowFrameShape);
                    
//            left and right window frame
            translateWindowFrames.addChild(translateOtherFrames);
                translateOtherFrames.addChild(leftWindowFrame);
                    leftWindowFrame.addChild(leftWindowFrameTransform);
                        leftWindowFrameTransform.addChild(leftWindowFrameShape);

                translateOtherFrames.addChild(rightWindowFrame);
                    rightWindowFrame.addChild(rightWindowFrameTransform);
                        rightWindowFrameTransform.addChild(rightWindowFrameShape);

//        table
        room.addChild(translateTable);
        
//            front legs
            translateTable.addChild(translateFrontTableFeet);
                translateFrontTableFeet.addChild(firstTableFoot);
                    firstTableFoot.addChild(firstTableFootTansform);
                        firstTableFootTansform.addChild(firstTableFootShape);

                translateFrontTableFeet.addChild(secondTableFoot);
                    secondTableFoot.addChild(secondTableFootTansform);
                        secondTableFootTansform.addChild(secondTableFootShape);

//            back legs
            translateTable.addChild(translateBackTableFeet);
                translateBackTableFeet.addChild(thirdTableFoot);
                    thirdTableFoot.addChild(thirdTableFootTansform);
                        thirdTableFootTansform.addChild(thirdTableFootShape);

                translateBackTableFeet.addChild(fourthTableFoot);
                    fourthTableFoot.addChild(fourthTableFootTansform);
                        fourthTableFootTansform.addChild(fourthTableFootShape);

//            table top
            translateTable.addChild(tableTop);
                tableTop.addChild(tableTopTansform);
                    tableTopTansform.addChild(tableTopShape);
            
//        objects on table
        room.addChild(translateToTableTop);
        
//            paper
            translateToTableTop.addChild(paperView);
                paperView.addChild(translatePaper); 
                translatePaper.addChild(paperTransform);
                    paperTransform.addChild(paperShape);
                    
                translatePaper.addChild(secondPaper);
                    secondPaper.addChild(secondPaperTransform);
                        secondPaperTransform.addChild(secondPaperShape);
                    
//            clock
            translateToTableTop.addChild(clockView);
                clockView.addChild(clockTransform);
                    clockTransform.addChild(clockShape);
                    
//                    clock hand
                    clockTransform.addChild(clockHandView);
                        clockHandView.addChild(translateClockHand);
                            translateClockHand.addChild(rotateClockHandY);
                                    rotateClockHandY.addChild(clockHandTransform);
                                        clockHandTransform.addChild(clockHandShape);
                                
//            post box
            translateToTableTop.addChild(postBoxView);
                postBoxView.addChild(translatePostBox);
                    translatePostBox.addChild(postBoxTransform);
                        postBoxTransform.addChild(postBoxShape);
                        
                    translatePostBox.addChild(postPaper);
                        postPaper.addChild(translatePostPaper);
                        translatePostPaper.addChild(postPaperTransform);
                            postPaperTransform.addChild(postPaperShape);
                            
//            laptop
            translateToTableTop.addChild(laptopBase);
            
//                laptop base
                laptopBase.addChild(translateLaptopBase);
                    translateLaptopBase.addChild(laptopBaseTransform);
                        laptopBaseTransform.addChild(laptopBaseShape);
                        
//                    laptop keyboard
                    translateLaptopBase.addChild(laptopKeyboardView);
                        laptopKeyboardView.addChild(translateLaptopKeyboard);
                            translateLaptopKeyboard.addChild(laptopKeyboardTransform);
                                laptopKeyboardTransform.addChild(laptopKeyboardShape);
                        
//                    laptop  connection node
                    translateLaptopBase.addChild(nodeForLaptop);
                        nodeForLaptop.addChild(translateLaptopNode);
                            translateLaptopNode.addChild(laptopNodeTransform);
                                laptopNodeTransform.addChild(laptopNodeShape);
                                
//                            laptop head
                            translateLaptopNode.addChild(laptopHead);
                                laptopHead.addChild(translateLaptopHead);
                                    translateLaptopHead.addChild(laptopHeadTransform);
                                        laptopHeadTransform.addChild(laptopHeadShape);
                                        
//                                    laptop screen
                                    translateLaptopHead.addChild(screenForLaptop);
                                        screenForLaptop.addChild(translateLaptopScreen);
                                            translateLaptopScreen.addChild(laptopScreenTransform);
                                                laptopScreenTransform.addChild(laptopScreenShape);
                    
//            lamp
            translateToTableTop.addChild(scaleLamp);
            scaleLamp.addChild(baseForLamp);
                baseForLamp.addChild(translateLampBase);
                    translateLampBase.addChild(lampBaseTransform);
                        lampBaseTransform.addChild(lampBasehape);
                        
//                    lamp base connection node
                    translateLampBase.addChild(nodeForLampBase);
                        nodeForLampBase.addChild(translateLampBaseNode);
                            translateLampBaseNode.addChild(lampBaseNodeTransform);
                                lampBaseNodeTransform.addChild(lampBaseNodeShape);
                        
//                            lamp wire
                            translateLampBaseNode.addChild(wireForLamp);
                                wireForLamp.addChild(translateLampWire);
                                    translateLampWire.addChild(rotateLampWireY);
                                        rotateLampWireY.addChild(rotateLampWireZ);
                                            rotateLampWireZ.addChild(lampWireTransform);
                                                lampWireTransform.addChild(lampWireShape);
                    
//                    bottom lamp arm
                    translateLampBase.addChild(bottomLampArm);
                        bottomLampArm.addChild(translateBottomLampArm);
                            translateBottomLampArm.addChild(rotateBottomLampArmY);
                                rotateBottomLampArmY.addChild(rotateBottomLampArmZ);
                                    rotateBottomLampArmZ.addChild(bottomLampArmTransform);
                                        bottomLampArmTransform.addChild(bottomLampArmShape);

//                                    lamp connection node
                                    rotateBottomLampArmZ.addChild(lampNode);
                                        lampNode.addChild(translateLampNode);
                                            translateLampNode.addChild(lampNodeTransform);
                                                lampNodeTransform.addChild(lampNodeShape);

//                                    top lamp arm
                                    rotateBottomLampArmZ.addChild(topLampArm);
                                        topLampArm.addChild(translateTopLampArm);
                                            translateTopLampArm.addChild(rotateTopLampArmZ);
                                                    rotateTopLampArmZ.addChild(topLampArmTransform);
                                                        topLampArmTransform.addChild(topLampArmShape);

//                                                    lamp head
                                                    rotateTopLampArmZ.addChild(headForLamp);
                                                        headForLamp.addChild(translateLampHead);
                                                            translateLampHead.addChild(rotateLampHeadY);
                                                                rotateLampHeadY.addChild(lampHeadTransform);
                                                                    lampHeadTransform.addChild(lampHeadShape);

//                                                                lamp light
                                                                rotateLampHeadY.addChild(lightForLamp);
                                                                    lightForLamp.addChild(translateLampLight);
                                                                        translateLampLight.addChild(lampLightTransform);
                                                                            lampLightTransform.addChild(lampLightShape);
                                                                            
//                                                                sphere on lamp head
                                                                rotateLampHeadY.addChild(sphereOnLampHead);
                                                                    sphereOnLampHead.addChild(sphereOnLampHeadTransform);
                                                                        sphereOnLampHeadTransform.addChild(sphereOnLampHeadShape);
                                                                            
            
//        apply model transforms
        root.update();
        
//        print scene graph structure 
        root.print(0, false);
    }
    
//    Clear the screen to clear color setup in glEventListener.init method, draw models and associated scenes
    public static void render(GL3 gl) {
        
//        clear background and depth buffer first
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        
//        display models on screen
        roomLight1.render(gl);
        roomLight2.render(gl);
        
        root.draw(gl);
        floor.model.render(gl);
    }
    
//    Dispose models not inside scene graph
    public static void dispose(GL3 gl) {
        
//        remove world light models
        roomLight1.dispose(gl);
        roomLight2.dispose(gl);
        
//        remove floor model
        floor.model.dispose(gl);
    }
}
