
// Includerea bibliotecilor OpenGL ?i GLFW pentru ini?ializare ?i randare
#if defined (__APPLE__)
#define GLFW_INCLUDE_GLCOREARB
#define GL_SILENCE_DEPRECATION
#else
#define GLEW_STATIC
#include <GL/glew.h>
#endif

#include <GLFW/glfw3.h>

#include <glm/glm.hpp> 
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/matrix_inverse.hpp> 
#include <glm/gtc/type_ptr.hpp> 

#include "Window.h"// Clasa care gestioneazã fereastra
#include "Shader.hpp"// Clasa pentru încãrcarea ?i utilizarea shader-elor
#include "Camera.hpp" // Clasa pentru gestionarea camerei
#include "SkyBox.hpp"// Clasa pentru afi?area Skybox-ului
#include "Model3D.hpp"// Clasa pentru încãrcarea ?i desenarea modelelor 3D

#include <iostream>

// window
gps::Window window;// Ini?ializarea ferestrei OpenGL





// matrici
glm::mat4 model;// Matricea model – aplicã transformãrile asupra obiectelor 3D
glm::mat4 originlModelHand = model;
glm::mat4 originlModelPanou = model;
glm::mat4 view; // Matricea view – pozi?ia ?i direc?ia camerei
glm::mat4 projection;// Matricea projection – pentru proiec?ie în perspectivã
glm::mat3 normalMatrix; // Matricea normalMatrix – utilizatã pentru calculele de iluminare





// parametrii de lumina
glm::vec3 lightDir; // Direc?ia luminii direc?ionale
glm::vec3 lightColor;// Culoarea luminii





// locatii de shader uniform
GLint modelLoc;// Loc pentru matricea model în shader
GLint viewLoc; // Loc pentru matricea view în shader
GLint projectionLoc; // Loc pentru matricea de proiec?ie
GLint normalMatrixLoc;// Loc pentru matricea normalMatrix
GLint lightDirLoc; // Loc pentru direc?ia luminii
GLint lightColorLoc;// Loc pentru culoarea luminii





// camera
gps::Camera myCamera( // Ini?ializarea camerei cu pozi?ia, direc?ia ?i vectorul "sus"
    glm::vec3(10.0f, 2.0f, -10.0f), // Pozi?ia ini?ialã a camerei
    glm::vec3(10.0f, 0.0f, 0.0f),  // Direc?ia cãtre care prive?te camera
    glm::vec3(0.0f, 1.0f, 0.0f)); // Vectorul "sus" al camerei (pe axa Y)





//Mouse
bool firstMouse = true;// Verificã dacã mi?carea mouse-ului este prima pentru resetarea valorilor
float lastX = 400, lastY = 300; // Pozi?ia precedentã a mouse-ului (centrul ferestrei)
float pitch = 0.0f, yaw = +180.0f;// Unghiurile de rota?ie ale camerei

GLfloat cameraSpeed = 0.1f;
GLboolean pressedKeys[1024]; // Array care re?ine starea tastelor apãsate





//SkyBox
gps::SkyBox mySkyBox;// Obiect pentru Skybox
gps::Shader skyBoxShader;// Shader-ul utilizat pentru randarea Skybox-ului




//ceata
int activateFog = 0;// Variabilã pentru activarea/dezactivarea ce?ii (0 – dezactivatã, 1 – activatã)




//point light
int activatePointLight = 0;// Activare/dezactivare luminã punctualã (0 – dezactivatã, 1 – activatã)
glm::vec3 luminaUfoPos = glm::vec3(18.589f, -19.837f, 11.224f);// Pozi?ia sursei de luminã punctualã





//spot light
float spotlight1;// Unghiul de început al fasciculului luminos
float spotlight2;// Unghiul maxim al fasciculului luminos
glm::vec3 spotLightDirection; // Direc?ia fasciculului de luminã spot
glm::vec3 spotLightPosition; // Pozi?ia sursei de luminã spot
int activateSpotLight = 1;// Activare/dezactivare luminã spot (1 – activatã, 0 – dezactivatã)




//modelele
gps::Model3D banca;
gps::Model3D barca;
gps::Model3D brad;
gps::Model3D casa;
gps::Model3D gard;
gps::Model3D lac;
gps::Model3D lup;
gps::Model3D pamant;
gps::Model3D piatra;
gps::Model3D rata;




//Shader
gps::Shader basicShader; // Shader utilizat pentru randarea obiectelor 3D




// Prezentare automatã a scenei
bool startPresentation = false; // Variabilã pentru activarea modului de prezentare automatã a scenei

GLenum glCheckError_(const char* file, int line)
{
    GLenum errorCode;
    while ((errorCode = glGetError()) != GL_NO_ERROR) {
        std::string error;
        switch (errorCode) {
        case GL_INVALID_ENUM:
            error = "INVALID_ENUM";
            break;
        case GL_INVALID_VALUE:
            error = "INVALID_VALUE";
            break;
        case GL_INVALID_OPERATION:
            error = "INVALID_OPERATION";
            break;
        case GL_OUT_OF_MEMORY:
            error = "OUT_OF_MEMORY";
            break;
        case GL_INVALID_FRAMEBUFFER_OPERATION:
            error = "INVALID_FRAMEBUFFER_OPERATION";
            break;
        }
        std::cout << error << " | " << file << " (" << line << ")" << std::endl;
    }
    return errorCode;
}
#define glCheckError() glCheckError_(__FILE__, __LINE__)




// Callback pentru redimensionarea ferestrei
void windowResizeCallback(GLFWwindow* window, int width, int height) {
    glViewport(0, 0, width, height);// Actualizeazã zona de randare a OpenGL conform noilor dimensiuni ale ferestrei
}





// Callback pentru gestionarea input-ului de la tastaturã
void keyboardCallback(GLFWwindow* window, int key, int scancode, int action, int mode) {
    if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
        glfwSetWindowShouldClose(window, GL_TRUE);
    }
    // Deplasare înainte (tasta W)
    if (pressedKeys[GLFW_KEY_W]) {
        myCamera.move(gps::MOVE_FORWARD, 4 * cameraSpeed);// Mi?cã camera înainte
        //update view matrix
        view = myCamera.getViewMatrix();
        basicShader.useShaderProgram();
        glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
        // compute normal matrix for teapot
        normalMatrix = glm::mat3(glm::inverseTranspose(view * model));
    }

    if (pressedKeys[GLFW_KEY_S]) {
        myCamera.move(gps::MOVE_BACKWARD, 4 * cameraSpeed);
        //update view matrix
        view = myCamera.getViewMatrix();
        basicShader.useShaderProgram();
        glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
        // compute normal matrix for teapot
        normalMatrix = glm::mat3(glm::inverseTranspose(view * model));
    }

    if (pressedKeys[GLFW_KEY_A]) {
        myCamera.move(gps::MOVE_LEFT, 4 * cameraSpeed);
        //update view matrix
        view = myCamera.getViewMatrix();
        basicShader.useShaderProgram();
        glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
        // compute normal matrix for teapot
        normalMatrix = glm::mat3(glm::inverseTranspose(view * model));
    }

    if (pressedKeys[GLFW_KEY_D]) {
        myCamera.move(gps::MOVE_RIGHT, 4 * cameraSpeed);
        //update view matrix
        view = myCamera.getViewMatrix();
        basicShader.useShaderProgram();
        glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
        // compute normal matrix for teapot
        normalMatrix = glm::mat3(glm::inverseTranspose(view * model));
    }

    // Activare/dezactivare cea?ã (tasta F)
    if (key == GLFW_KEY_F && action == GLFW_PRESS)
    {
        activateFog == 0 ? activateFog = 1 : activateFog = 0;// Comutã între activat/dezactivat
        basicShader.useShaderProgram();
        glUniform1i(glGetUniformLocation(basicShader.shaderProgram, "activateFog"), activateFog);// Trimite valoarea ce?ii la shader
    }
    // Activare/dezactivare luminã punctualã (tasta P)
    if (key == GLFW_KEY_P && action == GLFW_PRESS)
    {
        activatePointLight == 0 ? activatePointLight = 1 : activatePointLight = 0; // Comutã activarea luminii punctiforme
        basicShader.useShaderProgram();
        glUniform1i(glGetUniformLocation(basicShader.shaderProgram, "activatePointLight"), activatePointLight);// Trimite valoarea la shader
    }
    // Activare/dezactivare luminã spot (tasta O)
    if (key == GLFW_KEY_O && action == GLFW_PRESS)
    {
        activateSpotLight == 0 ? activateSpotLight = 1 : activateSpotLight = 0;
        basicShader.useShaderProgram();
        glUniform1i(glGetUniformLocation(basicShader.shaderProgram, "activateSpotLight"), activateSpotLight);
    }

    if (key >= 0 && key < 1024) {
        if (action == GLFW_PRESS) {
            pressedKeys[key] = true;
        }
        else if (action == GLFW_RELEASE) {
            pressedKeys[key] = false;
        }
    }

    // Schimbã modurile de afi?are
    if (pressedKeys[GLFW_KEY_J])
    {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL); // Modul solid (randare completã)
    }

    if (pressedKeys[GLFW_KEY_K])
    {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

    if (pressedKeys[GLFW_KEY_L])
    {
        glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);// Modul puncte (afi?eazã doar vertec?ii obiectelor)
    }
    // Activare prezentare automatã (tasta Q)
    if (key == GLFW_KEY_Q && action == GLFW_PRESS) {
        startPresentation = !startPresentation; // Comutã între mod automat de prezentare ?i control manual
    }
}




// Callback pentru mi?carea mouse-ului
void mouseCallback(GLFWwindow* window, double xpos, double ypos) {
    // Dacã este prima mi?care a mouse-ului, se seteazã coordonatele ini?iale

    if (firstMouse)
    {
        lastX = xpos;
        lastY = ypos;
        firstMouse = false;
    }
    // Calculeazã offset-urile miscãrii mouse-ului
    float xoffset = xpos - lastX;
    float yoffset = lastY - ypos;
    lastX = xpos;
    lastY = ypos;
    // Sensibilitatea miscãrii camerei
    float sensitivity = 0.05f;
    xoffset *= sensitivity;
    yoffset *= sensitivity;
    // Actualizeazã valorile pentru rota?ie
    yaw += xoffset;// Rotatie pe orizontalã (stânga/dreapta)
    pitch += yoffset;// Rotatie pe verticalã (sus/jos)

    if (pitch > 89.0f)
        pitch = 89.0f;
    if (pitch < -89.0f)
        pitch = -89.0f;
    // Actualizeazã direc?ia camerei în functie de noile unghiuri
    myCamera.rotate(pitch, yaw);// Apeleazã func?ia de rota?ie a camerei
    basicShader.useShaderProgram(); // Activeazã shader-ul de bazã
    view = myCamera.getViewMatrix(); // Obtine matricea view actualizatã
    glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
    // Recalculeazã matricea normalã pentru iluminare
    normalMatrix = glm::mat3(glm::inverseTranspose(view * model));
    glUniformMatrix3fv(normalMatrixLoc, 1, GL_FALSE, glm::value_ptr(normalMatrix));
    // Actualizeazã directia luminii în raport cu pozitia camerei
    glUniform3fv(lightDirLoc, 1, glm::value_ptr(glm::inverseTranspose(glm::mat3(view)) * lightDir));
}




void setWindowCallbacks() {
    glfwSetWindowSizeCallback(window.getWindow(), windowResizeCallback);
    glfwSetKeyCallback(window.getWindow(), keyboardCallback);
    glfwSetCursorPosCallback(window.getWindow(), mouseCallback);
    glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
}




//initiatlizare fereastra
void initWindow() {
    window.Create(1920, 1000, "OpenGL Project Core");
}




void initOpenGLState() {
    glClearColor(0.7f, 0.7f, 0.7f, 1.0f);
    glViewport(0, 0, window.getWindowDimensions().width, window.getWindowDimensions().height);
    glEnable(GL_FRAMEBUFFER_SRGB);
    glEnable(GL_DEPTH_TEST); 
    glDepthFunc(GL_LESS); 
    glEnable(GL_CULL_FACE); 
    glCullFace(GL_BACK); 
    glFrontFace(GL_CCW); 
}



void initializareModele() {

    banca.LoadModel("models/Scena/banca.obj");
    barca.LoadModel("models/Scena/barca.obj");
    brad.LoadModel("models/Scena/brad.obj");
    casa.LoadModel("models/Scena/casa.obj");
    gard.LoadModel("models/Scena/gard.obj");
    lac.LoadModel("models/Scena/lac.obj");
    lup.LoadModel("models/Scena/lup.obj");
    pamant.LoadModel("models/Scena/pamant.obj");
    piatra.LoadModel("models/Scena/piatra.obj");
    rata.LoadModel("models/Scena/rata.obj");

}

void initializareSkyBox() {
    std::vector<const GLchar*> faces;
    faces.push_back("skybox/left.JPEG");
    faces.push_back("skybox/right.JPEG");
    faces.push_back("skybox/up.JPEG");
    faces.push_back("skybox/down.JPEG");
    faces.push_back("skybox/back.JPEG");
    faces.push_back("skybox/front.JPEG");
    mySkyBox.Load(faces);
}

void initializareShadere() {
    basicShader.loadShader(
        "shaders/basic.vert",
        "shaders/basic.frag");

    //skybox
    skyBoxShader.loadShader(
        "shaders/skyBox.vert",
        "shaders/skyBox.frag");
    skyBoxShader.useShaderProgram();
}


//presentation
void updateCameraPresentation(float time) {


    std::vector<glm::vec3> cameraPositions = {
        glm::vec3(6.0f, -10.0f, -70.0f),
        glm::vec3(86.0f, 0.0f, -70.0f),
        glm::vec3(106.0f, 10.0f, -70.0f),
        glm::vec3(116.0f, 30.0f, -120.0f),
        glm::vec3(106.0f, 10.0f, -90.0f),
        glm::vec3(86.0f, 20.0f, -75.0f)
    };



    int numPoints = cameraPositions.size();
    float segmentTime = 5.0f;  // Durata între puncte
    int currentSegment = static_cast<int>(time / segmentTime) % numPoints;
    int nextSegment = (currentSegment + 1) % numPoints;


    float t = fmod(time, segmentTime) / segmentTime;


    glm::vec3 newPosition = glm::mix(cameraPositions[currentSegment], cameraPositions[nextSegment], t);


    myCamera.setPosition(newPosition);
    myCamera.setTarget(glm::vec3(56.0f, -30.0f, -80.0f));



    view = myCamera.getViewMatrix();
    basicShader.useShaderProgram();
    glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));

    normalMatrix = glm::mat3(glm::inverseTranspose(view * model));
    glUniformMatrix3fv(normalMatrixLoc, 1, GL_FALSE, glm::value_ptr(normalMatrix));
}


// Initializarea variabilelor uniforme din shader
void initializareUniforme() {
    basicShader.useShaderProgram(); // Activeazã shader-ul de bazã

    // create model matrix 
    model = glm::mat4(1.0f);
    modelLoc = glGetUniformLocation(basicShader.shaderProgram, "model");

    // get view matrix for current camera
    view = myCamera.getViewMatrix();
    viewLoc = glGetUniformLocation(basicShader.shaderProgram, "view");
    // send view matrix to shader
    glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));

    // compute normal matrix 
    normalMatrix = glm::mat3(glm::inverseTranspose(view * model));
    normalMatrixLoc = glGetUniformLocation(basicShader.shaderProgram, "normalMatrix");

    // Trimiterea matricei model (dacã existã o transformare staticã necesarã)
    glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));
    glUniformMatrix3fv(normalMatrixLoc, 1, GL_FALSE, glm::value_ptr(normalMatrix));

    // create projection matrix
    projection = glm::perspective(glm::radians(45.0f),
        (float)window.getWindowDimensions().width / (float)window.getWindowDimensions().height,
        0.1f, 2000.0f);
    projectionLoc = glGetUniformLocation(basicShader.shaderProgram, "projection");
    // send projection matrix to shader
    glUniformMatrix4fv(projectionLoc, 1, GL_FALSE, glm::value_ptr(projection));

    //set the light direction (direction towards the light)
    lightDir = glm::vec3(0.0f, 1.0f, 1.0f);
    lightDirLoc = glGetUniformLocation(basicShader.shaderProgram, "lightDir");
    // send light dir to shader
    glUniform3fv(lightDirLoc, 1, glm::value_ptr(lightDir));

    //set light color
    lightColor = glm::vec3(1.0f, 1.0f, 1.0f); //white light
    lightColorLoc = glGetUniformLocation(basicShader.shaderProgram, "lightColor");
    // send light color to shader
    glUniform3fv(lightColorLoc, 1, glm::value_ptr(lightColor));

    // spotlight
    spotlight1 = glm::cos(glm::radians(40.5f));
    spotlight2 = glm::cos(glm::radians(100.5f));

    spotLightDirection = glm::vec3(0, -1, 0);
    spotLightPosition = glm::vec3(42.266f, 10.0f, 12.0f);

    // setup a material for the scene
    glUniform1f(glGetUniformLocation(basicShader.shaderProgram, "material.shininess"), 100.0f);

    glUniform3fv(glGetUniformLocation(basicShader.shaderProgram, "dirLight.direction"), 1, glm::value_ptr(lightDir));
    glUniform3f(glGetUniformLocation(basicShader.shaderProgram, "dirLight.ambient"), 0.2f, 0.2f, 0.2f);
    glUniform3f(glGetUniformLocation(basicShader.shaderProgram, "dirLight.diffuse"), 0.5f, 0.5f, 0.5f);
    glUniform3f(glGetUniformLocation(basicShader.shaderProgram, "dirLight.specular"), 1.0f, 1.0f, 1.0f);

   
    glUniform3fv(glGetUniformLocation(basicShader.shaderProgram, "pointLight.position"), 1, glm::value_ptr(luminaUfoPos));
    glUniform1f(glGetUniformLocation(basicShader.shaderProgram, "pointLight.constant"), 1.0f);
    glUniform1f(glGetUniformLocation(basicShader.shaderProgram, "pointLight.linear"), 0.09f);
    glUniform1f(glGetUniformLocation(basicShader.shaderProgram, "pointLight.quadratic"), 0.032f);
    glUniform3f(glGetUniformLocation(basicShader.shaderProgram, "pointLight.ambient"), 0.2f, 0.2f, 0.2f);
    glUniform3f(glGetUniformLocation(basicShader.shaderProgram, "pointLight.diffuse"), 0.5f, 0.5f, 0.5f);
    glUniform3f(glGetUniformLocation(basicShader.shaderProgram, "pointLight.specular"), 1.0f, 1.0f, 1.0f);


    glUniform1f(glGetUniformLocation(basicShader.shaderProgram, "spotlight1"), spotlight1);
    glUniform1f(glGetUniformLocation(basicShader.shaderProgram, "spotlight2"), spotlight2);
    glUniform3fv(glGetUniformLocation(basicShader.shaderProgram, "spotLightDirection"), 1, glm::value_ptr(spotLightDirection));
    glUniform3fv(glGetUniformLocation(basicShader.shaderProgram, "spotLightPosition"), 1, glm::value_ptr(spotLightPosition));

    glUniform1i(glGetUniformLocation(basicShader.shaderProgram, "activateSpotLight"), activateSpotLight);
    glUniform1i(glGetUniformLocation(basicShader.shaderProgram, "activatePointLight"), activatePointLight);


}






//render
void renderScene(gps::Shader shader){

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    shader.useShaderProgram();

    banca.Draw(shader);
    barca.Draw(shader);
    brad.Draw(shader);
    casa.Draw(shader);
    gard.Draw(shader);
    lac.Draw(shader);
    lup.Draw(shader);
    pamant.Draw(shader);
    piatra.Draw(shader);
    rata.Draw(shader);
    

    mySkyBox.Draw(skyBoxShader, view, projection);
}




//cleanup
void cleanWindow() {
    window.Delete();
}




//main
int main(int argc, const char* argv[]) {

    try {
        initWindow();
    }
    catch (const std::exception& e) {
        std::cerr << e.what() << std::endl;
        return EXIT_FAILURE;
    }

    initOpenGLState();
    initializareModele();
    initializareShadere();
    initializareSkyBox();
    initializareUniforme();
    setWindowCallbacks();

    glCheckError();

    while (!glfwWindowShouldClose(window.getWindow())) {

        renderScene(basicShader);

        if (startPresentation) {
            updateCameraPresentation(glfwGetTime());
        }

        glfwPollEvents();
        glfwSwapBuffers(window.getWindow());

        glCheckError();
    }

    cleanWindow();

    return EXIT_SUCCESS;
}
