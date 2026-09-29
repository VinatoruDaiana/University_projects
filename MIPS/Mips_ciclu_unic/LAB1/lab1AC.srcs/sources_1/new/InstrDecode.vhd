----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 04.04.2024 18:25:13
-- Design Name: 
-- Module Name: InstrDecode - Behavioral
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

entity InstrDecode is
Port(
clk:in std_logic;
RegWrite:in std_logic;
instr:in std_logic_vector(25 downto 0);
RegDst:in std_logic;
ExtOp:in std_logic;
rd1:out std_logic_vector(31 downto 0);
rd2:out std_logic_vector(31 downto 0);
wd:in std_logic_vector(31 downto 0);
Ext_Imm:out std_logic_vector(31 downto 0);
funct: out std_logic_vector(5 downto 0);
sa: out std_logic_vector(4 downto 0)
);

end InstrDecode;

architecture Behavioral of InstrDecode is

type reg_type is array (0 to 31) of std_logic_vector(31 downto 0);
signal regFile : reg_type := (
x"000000000",x"000000000",x"000000000",x"000000000",x"0000000014",x"000000000",others => X"00000000");

signal mux:std_logic_vector(4 downto 0);

begin


--mux ul
process(RegDst)
begin
    if RegDst='1' then
        mux<=instr(15 downto 11);
    else
        mux<=instr(20 downto 16);
    end if;
end process;


--registru
process(clk)

begin
    if rising_edge(clk) then
        if RegWrite = '1' then
            regFile(conv_integer(mux)) <= wd;
        end if;
    end if;
end process;

rd1<=regFile(conv_integer(instr(25 downto 21)));
rd2<=regFile(conv_integer(instr(20 downto 16)));


--ext unit
process(ExtOp)
begin
  if  ExtOp='0' then
    Ext_Imm<="0000000000000000" & instr(15 downto 0);
    else
    if(instr(15)='0') then
    Ext_Imm<="0000000000000000" & instr(15 downto 0);
    else
    Ext_Imm<="1111111111111111" & instr(15 downto 0);
   end if;
   end if;
end process;

funct<=instr(5 downto 0);
sa<= instr(10 downto 6);
end Behavioral;
