# src
There are five packages/folders: gmaths, Handlers, Inputs, Models and Scene. The gmaths contains all vectors and matrix manipulations, the input package implements keyboard and mouse commands while the handlers hold special classes used throughout the program such as camera, shader, texture library SGNode etc. All objects rendered to the screen are stored in the models package which contains different object classes. The scene is where the object data are drawn and rendered to the screen by applying different set of fragment and vertex shaders. Outside these packages/folders are the main Anilamp file that loads the program, the event listener that implements the objects information and render animations to the screen, and the scene graph that creates object models and transforms as well as the scene graph itself. 