# Zoom & Shrink

A C++ image processing project developed with OpenCV for resizing grayscale images using multiple interpolation methods.

## Description

The application allows interactive image zooming and shrinking while switching between different resizing algorithms.

Implemented methods:

- Nearest Neighbor
- Bilinear Interpolation
- Bicubic Interpolation
- Average-based shrinking

## Features

- Load and process grayscale images
- Zoom in and zoom out interactively
- Switch between resizing methods at runtime
- Display the resized image in an OpenCV window
- Limit large images for easier on-screen visualization

## Controls

```text
1  - Nearest Neighbor
2  - Bilinear
3  - Bicubic
4  - Average (shrink only)
+  - Zoom in
-  - Zoom out
ESC - Exit
```

## Technologies

- C++
- OpenCV
- CMake
- CLion

## Project Structure

```text
Zoom&Shrink/
├── src/
│   ├── main.cpp
│   ├── proiect.cpp
│   ├── proiect.h
│   └── CMakeLists.txt
├── imagini/
│   └── catel.jpg
└── README.md
```

## Main Functions

- `nearestNeighborResize()` – resizes the image using nearest-neighbor interpolation
- `bilinearResize()` – applies bilinear interpolation
- `bicubicResize()` – applies bicubic interpolation
- `averageResize()` – shrinks the image using the average value of source pixels
- `interactiveZoom()` – handles keyboard input and interactive visualization

## How to Run

1. Install OpenCV and configure it for the project.
2. Open the project in CLion or another C++ IDE.
3. Configure and build the project with CMake.
4. Update the image path in `main.cpp` if needed.
5. Run the application and use the keyboard controls to test the resizing methods.

## Purpose

This project was developed as a university assignment to practice image resizing algorithms, interpolation methods and basic image processing with OpenCV.
