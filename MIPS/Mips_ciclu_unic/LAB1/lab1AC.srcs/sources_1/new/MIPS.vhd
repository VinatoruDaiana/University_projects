----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 28.03.2024 18:18:30
-- Design Name: 
-- Module Name: MIPS - Behavioral
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

-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--use IEEE.NUMERIC_STD.ALL;

-- Uncomment the following library declaration if instantiating
-- any Xilinx leaf cells in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity instrFetch is
  Port(
  clk:in STD_LOGIC;
  enable:in STD_LOGIC;
  reset:in STD_LOGIC;
  jump: in STD_LOGIC;
  PCSrc:in STD_LOGIC;
  Jump_Address:in STD_LOGIC_VECTOR(31 downto 0);
  Branch_Address:in STD_LOGIC_VECTOR(31 downto 0);
  Out_pc:out STD_LOGIC_VECTOR(31 downto 0);
  Instruction:out STD_LOGIC_VECTOR(31 downto 0)
  
  );
  
end instrFetch;

architecture Behavioral of instrFetch is


signal PC_out:std_logic_vector(31 downto 0);
signal PC_in:std_logic_vector(31 downto 0);
signal Sumator_out:std_logic_vector(31 downto 0);
signal MuxBranch_out:std_logic_vector(31 downto 0);
signal MuxJump_out:std_logic_vector(31 downto 0);


type rom_type is array (0 to 31) of std_logic_vector(31 downto 0);
signal rom : rom_type := (
b"000010_00000_00100_0000000000010100", --addi $4,$0,20   salvez nr maxim de iteratii  X" 20040014"
b"001000_00100_00001_0000000000001110", --beq $1,$4,endlopp=14    daca s-au facut 20 de iteratii se iese din bucla  X" 11040008"
b"000100_00010_00011_0000000000001010", --lw $3,Adr($2)   pun elem. curent din sir in reg3 X" 0x8c62000a"
b"100000_00101_00011_00101_00000_100000",  --add $5,$5,$3  adun la suma elem. curent  X" 0x00a32020"
b"000010_00010_00010_0000000000000100",   --addi $2,$2,4 iau urmatorul elem. din sir   X" 0x21420004"
b"000010_00001_00001_0000000000000001",     --addi $1,$1,1 incrementez contorul i=i+1   X" 0x20210001"
b"001111_00000000000000000000000100",   --j begin loop (j4)   sar la inceputul buclei    X" 0x08000009"
b"000110_00000_00101_0000000000001100" , --sw $5,sum_adr($0)     salvez suma in memorie   X" 0xac0d0005"
others=>X"00000000"
);


begin

--pt PC
process(clk)
begin
    if reset ='1' then  
     PC_out<="00000000000000000000000000000000";
    else 
     if rising_edge(clk) then
        if enable='1' then
       
            PC_out<=MuxJump_out;
      
      end if;
      end if;
      end if;
  
end process;

--pt mem ROM
Instruction <= rom(conv_integer(PC_out(6 downto 2)));

--pt adunare
Sumator_out<=PC_out+4;

--pt mux Branch
process(PCSrc)
   begin
   
   if PCSrc='1' then
    MuxBranch_out<=Branch_Address;
    else
    MuxBranch_out<=Sumator_out;
   
 end if;
end process;

--pt mux jump

process(jump)
begin

 if jump='1' then
   MuxJump_out<=Jump_Address;
   else
   MuxJump_out<=MuxBranch_out;
   end if;
end process;
Out_pc<=PC_out;

end Behavioral;
