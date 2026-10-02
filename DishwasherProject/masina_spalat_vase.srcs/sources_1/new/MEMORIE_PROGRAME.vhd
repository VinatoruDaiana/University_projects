----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 26.05.2023 09:30:19
-- Design Name: 
-- Module Name: MEMORIE_PROGRAME - Behavioral
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
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;
-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--use IEEE.NUMERIC_STD.ALL;

-- Uncomment the following library declaration if instantiating
-- any Xilinx leaf cells in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity MEMORIE_PROGRAME is
    Port ( ADD : in integer;
           CS : in STD_LOGIC;
           CLK : in STD_LOGIC;
           DATA_OUT : out STD_LOGIC_VECTOR (13 downto 0));
end MEMORIE_PROGRAME;

architecture Behavioral of MEMORIE_PROGRAME is
type memorie is array(7 downto 0) of std_logic_vector(13 downto 0);
signal programe_automate: memorie:=(0 => x"0000",
                                    1 => "11000001100100",
                                    2 => "10100011100101",
                                    3 => "00010100010010",
                                    4 => "11010101100111",
                                    5 => "01000000010000",
                                    others => x"0000");
begin
    data_out <= programe_automate(add) when cs = '1' else"00000000000000";

end Behavioral;
