----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 25.05.2023 04:18:09
-- Design Name: 
-- Module Name: simualare_program - Behavioral
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

-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--use IEEE.NUMERIC_STD.ALL;

-- Uncomment the following library declaration if instantiating
-- any Xilinx leaf cells in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity simulare_program is
--  Port ( );
end simulare_program;

architecture Behavioral of simulare_program is
signal clk, done, door,reset: std_logic;
signal set_func: std_logic_vector(1 downto 0);
signal program: std_logic_vector(7 downto 0);
signal timp: std_logic_vector(9 downto 0);
begin
  l1:entity  work.executare_program port map (CLK=>clk, DOOR=>door, RESET=>reset, SETARE_FUNC=>set_func,done=>done, PROGRAM=>program, timp_out=>timp);
    process
    begin
        clk <= '0';
        wait for 10 ns;
        clk <= '1';
        wait for 10 ns;
    end process;

    process
    begin 
        reset <= '1';
        door <='0';
        set_func <= "00";
        wait for 20 ns;
        
        
        reset <= '0';
        door <='1';
        set_func <= "00";
        wait;
       
    end process;

end Behavioral;
