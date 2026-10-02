# Dishwasher Project

A university project implemented in **VHDL** using **Xilinx Vivado**, focused on controlling and simulating the operation of a dishwasher.

## Description

The project models the main stages of a dishwasher program and organizes the control logic into separate VHDL modules.

The implementation includes modules for:

- selecting the washing program;
- storing available programs;
- executing the selected program;
- countdown and time display;
- pre-wash / soaking timing;
- drying timing;
- hour/minute counting;
- simulation and testing.

## Main VHDL Modules

- `SETARE_PROGRAM.vhd` – program selection logic
- `MEMORIE_PROGRAME.vhd` – stores the available washing programs
- `executare_program.vhd` – controls program execution
- `COUNT_DOWN_TIMP.vhd` – countdown timer
- `COUNTER_INMUIERE_PRESP.vhd` – timing for soaking / pre-wash stages
- `COUNTER_USCARE.vhd` – drying-stage counter
- `COUNTER_HM.vhd` – hour/minute counter
- `AFIS_TIMP.vhd` – time display logic

The project also contains testbench and simulation files used to verify the implemented behavior.

## Technologies

- VHDL
- Xilinx Vivado
- Digital Logic Design
- FPGA Simulation

## Project Structure

```text
DishwasherProject/
└── masina_spalat_vase/
    ├── masina_spalat_vase.srcs/
    │   ├── sources_1/
    │   ├── sim_1/
    │   └── constrs_1/
    ├── masina_spalat_vase.sim/
    └── ...
```

## Purpose

The project was developed to practice the design of a digital control system in VHDL, modular hardware description, counters, timing logic and functional simulation in Vivado.
