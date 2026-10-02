# Tetris in Assembly

A university project that implements a simplified **Tetris game in x86 Assembly** using a graphical canvas library.

## Description

The application creates a graphical Tetris board and renders different pieces using Assembly macros and pixel-based drawing.

The project includes several Tetris piece types, such as:

- square
- line
- L-shaped pieces
- Z-shaped pieces
- T-shaped piece

The code also handles piece generation, movement, rotation, drawing, score display and timed updates.

## Technologies

- x86 Assembly
- MASM-style syntax
- Canvas graphics library
- Win32 / Visual Studio toolchain

## Main Files

- `daiana_tetris.asm` – main program containing the game logic, drawing routines and piece movement
- `digits.inc` – pixel data used to draw digits
- `letters.inc` – pixel data used to draw letters
- `culori.inc` – color definitions
- `linie.inc` – graphical data for line elements
- `square.inc` – graphical data for square elements

## Project Structure

```text
TetrisAssembly/
├── daiana_tetris.asm
├── inc/
│   ├── culori.inc
│   ├── digits.inc
│   ├── letters.inc
│   ├── linie.inc
│   └── square.inc
└── README.md
```

If the `.inc` files are moved into the `inc` folder, update the include directives in `daiana_tetris.asm`, for example:

```asm
include inc/digits.inc
include inc/letters.inc
include inc/square.inc
include inc/linie.inc
include inc/culori.inc
```

## Main Functionality

- Draws the Tetris game area
- Generates different Tetris pieces
- Supports different piece orientations
- Updates pieces using timer events
- Displays text and score information
- Uses custom macros for drawing shapes and pixels

## Purpose

This project was developed as a university assignment to practice low-level programming, Assembly macros, memory access, graphical rendering and basic game logic.
