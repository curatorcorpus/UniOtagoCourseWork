/*
 * RasterEngine.cpp
 *
 * by Stefanie Zollmann
 *
 * Basic model loading with assimp library
 *
 */


// Include standard headers
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <string>
#include <vector>

#include <GL/glew.h>
#include <glfw3.h>
GLFWwindow* window;


// Include GLM
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
using namespace glm;

#include <CMDParser.h>
#include <Texture.hpp>
#include <Scene.hpp>
#include <Triangle.hpp>
#include <Controls.hpp>
#include <Group.hpp>
#include <Objloader.hpp>

bool initWindow(std::string windowName){
    
    // Initialise GLFW
    if( !glfwInit() ){
        fprintf( stderr, "Failed to initialize GLFW\n" );
        getchar();
        return false;
    }
    
    glfwWindowHint(GLFW_SAMPLES, 4);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // To make MacOS happy; should not be needed
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    
    // Open a window and create its OpenGL context
    window = glfwCreateWindow( 1280, 900, windowName.c_str(), NULL, NULL);
    if( window == NULL ){
        fprintf( stderr, "Failed to open GLFW window. If you have an Intel GPU, they are not 3.3 compatible.\n" );
        getchar();
        glfwTerminate();
        return false;
    }
    glfwMakeContextCurrent(window);
    
    return true;
    
}

int main( int argc, char *argv[] )
{
    bool arg_obj = false;
    int render_mode = 0;

    std::string obj_name = "";
    std::string person_dir = "../res/models/person.obj";
    std::string earth_dir = "../res/models/earthobj.obj";

    // Terminal Argument Parser
    CMDParser parser("...");

    parser.addOpt("o",   1, "obj",      "specifies obj file to be rendered");
    parser.addOpt("s",  -1, "sepia",    "enables special effects - sepia effect");
    parser.addOpt("bw", -1, "blckwhte", "enables special effects - black and white effect");
    parser.addOpt("sb", -1, "sobel",    "enables special effects - sobel effect");

    parser.init(argc, argv);

    // handle arguments
    if(parser.isOptSet("o")) {
        // res models path must be changed if the models directory changes.
        obj_name = "../res/models/" + parser.getOptsString("o")[0] + ".obj";
        arg_obj = true;
    }
    if(parser.isOptSet("s")) {
        render_mode = 1;
    }
    if(parser.isOptSet("bw")) {
        render_mode = 2;
    }
    if(parser.isOptSet("sb")) {
        render_mode = 3;
    }

    initWindow("Render Engine");
    glfwMakeContextCurrent(window);
    
    // Initialize GLEW
    glewExperimental = true; // Needed for core profile

    if (glewInit() != GLEW_OK) {
        fprintf(stderr, "Failed to initialize GLEW\n");
        getchar();
        glfwTerminate();
        return -1;
    }
    
    glfwSetInputMode(window, GLFW_STICKY_KEYS, GL_TRUE);          // Ensure we can capture the escape key being pressed below
    glfwSetInputMode(window, GLFW_STICKY_KEYS, GL_TRUE);          // Ensure we can capture the escape key being pressed below
    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);  // Hide the mouse and enable unlimited mouvement

    // Set the mouse at the center of the screen
    glfwPollEvents();
    glfwSetCursorPos(window, 1024/2, 768/2);
    
    glClearColor(0.0f, 0.0f, 0.4f, 0.0f); // Dark blue background
    glEnable(GL_DEPTH_TEST);              // Enable depth test
    glDepthFunc(GL_LESS);                 // Accept fragment if it closer to the camera than the former ones
    glEnable(GL_CULL_FACE);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    //create a Vertex Array Object and set it as the current one
    //we will not go into detail here. but this can be used to optimise the performance by storing all of the state needed to supply vertex data
    GLuint VertexArrayID;
    glGenVertexArrays(1, &VertexArrayID);
    glBindVertexArray(VertexArrayID);
    
    // create scene objects
    Scene *scene = new Scene();

    // create grouped mesh objects.
    Group *person = new Group();
    Group *earth  = new Group();

    // load obj models and materials.
    
    if(arg_obj) {
        bool res = loadOBJMTL(obj_name.c_str(), person);

        if(!res) {
            std::cout << "model didn't successfully load" << std::endl;
            return EXIT_FAILURE;
        }    

        person->init();
        person->setRenderMode(render_mode);

        scene->addObject(person);
    } 
    // default
    else {
        bool load_earth  = loadOBJMTL(person_dir.c_str(), person);
        bool load_person = loadOBJMTL(earth_dir.c_str(), earth);


        // setup up shader for each grouped meshes.
        person->init();
        earth->init();

        // set render mode 0 - default, 1 - special effects
        person->setRenderMode(render_mode);
        earth->setRenderMode(render_mode);

        // add grouped meshes to scene
        scene->addObject(person);
        scene->addObject(earth);
    }

    // check if models successfully loaded.

    // setup camera.
    Camera* camera = new Camera();
    camera->setPosition(glm::vec3(0,100,200)); //set camera to show the models

    // setup inputs manager.
    Controls* myControls = new Controls(camera);
    myControls->setSpeed(30);
    
    //Render loop
    while( glfwGetKey(window, GLFW_KEY_ESCAPE ) != GLFW_PRESS && glfwWindowShouldClose(window) == 0 ){// Clear the screen
        // Clear the screen
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Also clear the depth buffer!!!
        
        // update camera controls with mouse input
        myControls->update();
        
        // render scene.
        scene->render(camera);

        // Swap buffers
        glfwSwapBuffers(window);
        glfwPollEvents();
    }    
    
    glDeleteVertexArrays(1, &VertexArrayID); //delete texture;

    // delete dynamically allocated objects
    delete scene;
    delete camera;

    glfwTerminate(); // Close OpenGL window and terminate GLFW
    
    return 0;
}

