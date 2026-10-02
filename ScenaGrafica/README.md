# Scena Grafica 3D

A C++ computer graphics project developed in Visual Studio, focused on rendering and navigating a textured 3D scene.

## Description

The project builds a complete 3D scene composed of multiple models, textures, shaders and a skybox. It includes camera movement and scene rendering logic implemented with OpenGL-based graphics components.

The scene contains objects such as:

- house
- lake
- boat
- bench
- fence
- trees
- rocks
- duck
- wolf

## Main Features

- Rendering of textured 3D models
- Camera movement and scene navigation
- Custom vertex and fragment shaders
- Skybox rendering
- Loading `.obj` and `.mtl` 3D models
- Texture loading
- Separate classes for camera, meshes, models, shaders and window management

## Technologies

- C++
- OpenGL
- GLSL
- Visual Studio
- OBJ / MTL 3D models

## Main Components

```text
Camera.cpp / Camera.hpp
Mesh.cpp / Mesh.hpp
Model3D.cpp / Model3D.hpp
Shader.cpp / Shader.hpp
SkyBox.cpp / SkyBox.hpp
Window.cpp / Window.h
main.cpp
```

The project also contains:

- `shaders/` – vertex and fragment shaders
- `models/` – 3D models and textures used in the scene
- `skybox/` – images used for the environment skybox

## Project Structure

```text
ScenaGrafica/
├── Camera.cpp
├── Mesh.cpp
├── Model3D.cpp
├── Shader.cpp
├── SkyBox.cpp
├── Window.cpp
├── main.cpp
├── shaders/
├── models/
├── skybox/
└── README.md
```

## How to Run

1. Open the Visual Studio project file (`.vcxproj`).
2. Make sure the required OpenGL libraries are correctly configured.
3. Build the project.
4. Run the application from Visual Studio.

## Purpose

This project was developed as a university assignment to practice 3D graphics concepts, model and texture loading, shader programming, camera control and scene rendering.
