----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 25.04.2024 18:53:04
-- Design Name: 
-- Module Name: IFetch - Behavioral
-- Project Name: 
-- Target Devices: 
-- Tool Versions: 
-- Description: 
-- 
-- Dependencies: 
-- 
-- Revision:
-- Revision 0.01 - File Created
-- Additional Comments:
-- 
----------------------------------------------------------------------------------


library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

entity IFetch is
    Port (clk : in STD_LOGIC;
          rst : in STD_LOGIC;
          en : in STD_LOGIC;
          BranchAddress : in STD_LOGIC_VECTOR(31 downto 0);
          JumpAddress : in STD_LOGIC_VECTOR(31 downto 0);
          Jump : in STD_LOGIC;
          PCSrc : in STD_LOGIC;
          Instruction : out STD_LOGIC_VECTOR(31 downto 0);
          PCp4 : out STD_LOGIC_VECTOR(31 downto 0));
end IFetch;

architecture Behavioral of IFetch is

-- Memorie ROM
type tROM is array (0 to 31) of STD_LOGIC_VECTOR(31 downto 0);
signal ROM : tROM := (

-------------- PROGRAM DE TEST --------------


b"000010_00000_00100_0000000000010100", --addi $4,$0,20   salvez nr maxim de iteratii
b"000000_00000_00000_00110_00000_000000",  --add $6,$0,$0  NooOp  
b"000000_00000_00000_00110_00000_000000",  --add $6,$0,$0  NooOp
b"001000_00100_00001_0000000000001110", --beq $1,$4,endlopp=14    daca s-au facut 20 de iteratii se iese din bucla
b"000000_00000_00000_00110_00000_000000",  --add $6,$0,$0  NooOp
b"000000_00000_00000_00110_00000_000000",  --add $6,$0,$0  NooOp
b"000000_00000_00000_00110_00000_000000",  --add $6,$0,$0  NooOp
b"000100_00010_00011_0000000000001010", --lw $3,Adr($2)   pun elem. curent din sir in reg3
b"000000_00000_00000_00110_00000_000000",  --add $6,$0,$0  NooOp
b"000000_00000_00000_00110_00000_000000",  --add $6,$0,$0  NooOp
b"100000_00101_00011_00101_00000_100000",  --add $5,$5,$3  adun la suma elem. curent
b"000010_00010_00010_0000000000000100",   --addi $2,$2,4 iau urmatorul elem. din sir
b"000010_00001_00001_0000000000000001",     --addi $1,$1,1 incrementez contorul i=i+1
b"001111_00000000000000000000000100",   --j begin loop (j4)   sar la inceputul buclei
b"000000_00000_00000_00110_00000_000000",  --add $6,$0,$0  NooOp
b"000110_00000_00101_0000000000001100" , --sw $5,sum_adr($0)     salvez suma in memorie 
others=>X"00000000"
);

signal PC : STD_LOGIC_VECTOR(31 downto 0) := (others => '0');
signal PCAux, NextAddr, AuxSgn : STD_LOGIC_VECTOR(31 downto 0);

begin

    -- Program Counter
    process(clk, rst)
    begin
        if rst = '1' then
            PC <= (others => '0');
        elsif rising_edge(clk) then
            if en = '1' then
                PC <= NextAddr;
            end if;
        end if;
    end process;

    -- Instruction OUT
    Instruction <= ROM(conv_integer(PC(6 downto 2)));

    -- PC + 4
    PCAux <= PC + 4;
    PCp4 <= PCAux;

    -- MUX for branch
    AuxSgn <= BranchAddress when PCSrc = '1' else PCAux;  
    
    -- MUX for jump
    NextAddr <= JumpAddress when Jump = '1' else AuxSgn;
    
end Behavioral;
