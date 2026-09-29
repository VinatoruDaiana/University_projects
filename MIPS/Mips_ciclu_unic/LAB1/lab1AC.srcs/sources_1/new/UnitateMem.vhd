----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 17.04.2024 10:34:47
-- Design Name: 
-- Module Name: UnitateMemorie - Behavioral
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

entity UnitateMem is
Port (
   clk:in std_logic;
   en : in STD_LOGIC;
   MemWrite:in std_logic;
   AluRes_in:in std_logic_vector(31 downto 0);
   rd2:in std_logic_vector(31 downto 0);
   MemData:out std_logic_vector(31 downto 0);
   AluRes_out:out std_logic_vector(31 downto 0)
   
 );
end UnitateMem;

architecture Behavioral of UnitateMem is

type ram_type is array (0 to 63) of std_logic_vector(31 downto 0);
signal ram : ram_type := (
x"000000000",x"000000000",x"000000000",x"000000000",x"0000000014",x"000000000",others => X"00000000");


begin

process(clk)
begin
  if rising_edge(clk) then 
    if en='1' and MemWrite='1' then 
    ram(conv_integer(AluRes_in(31 downto 0)))<=rd2;
    end if;
    end if;
end process;


MemData<= ram(conv_integer(AluRes_in(31 downto 0)));
AluRes_out<=AluRes_in;




end Behavioral;
