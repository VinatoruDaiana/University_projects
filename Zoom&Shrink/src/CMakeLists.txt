cmake_minimum_required(VERSION 3.14)
project(Proiect)

set(CMAKE_CXX_STANDARD 17)

# --- Find OpenCV
find_package(OpenCV REQUIRED)
include_directories(${OpenCV_INCLUDE_DIRS})

add_executable(Proiect
        main.cpp
        src/proiect.cpp
        src/proiect.h
)

target_link_libraries(Proiect ${OpenCV_LIBS})
