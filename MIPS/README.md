# MIPS Processor Implementations

University project developed in **VHDL** using **Xilinx Vivado**, containing two MIPS processor implementations:

- **Single-Cycle MIPS**
- **Pipelined MIPS**

## Single-Cycle MIPS

The single-cycle implementation executes each instruction in one clock cycle.

Main components include:

- ALU
- Register File
- Instruction Decode
- Main Control Unit
- Instruction Execution
- RAM / Memory Unit
- Top-level MIPS module

The project also contains documentation for the RTL schematic, control signals and instruction execution tracing.

## Pipelined MIPS

The pipelined implementation divides instruction execution into the main pipeline stages:

- **IF** – Instruction Fetch
- **ID** – Instruction Decode
- **EX** – Execute
- **MEM** – Memory Access

The VHDL project contains separate modules for these stages, together with the control unit and test environment.

Additional documentation includes the pipeline register design and RTL schematic.

## Technologies

- VHDL
- Xilinx Vivado
- MIPS Architecture
- Digital Design

## Project Structure

```text
MIPS/
├── Mips_ciclu_unic/
└── Mips_pipeline/
```

## Purpose

The project was developed to study and implement two different MIPS processor architectures and to understand the differences between single-cycle and pipelined instruction execution.
