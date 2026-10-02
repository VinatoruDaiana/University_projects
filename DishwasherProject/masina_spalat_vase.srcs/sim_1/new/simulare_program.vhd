----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 25.05.2023 11:26:34
-- Design Name: 
-- Module Name: test_counter - Behavioral
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

entity test_counter is
--  Port ( );
end test_counter;

architecture Behavioral of test_counter is
signal hour: std_logic_vector(1 downto 0);
signal min1,min0: std_logic_vector(3 downto 0);
signal clk, reset, enable, done: std_logic;
signal timp : std_logic_vector(9 downto 0);
begin
    l1: entity work.counter_hm port map (hour=>hour, d1_min=>min1, d0_min=>min0, clk=>clk, reset=>reset, enable=>enable,done=>done, timp=>timp);

    process
    begin
        clk<='0';
        wait for 10 ns;
        clk<='1';
        wait for 10 ns;
    end process;
    
    process
    begin
        reset<='1';
        hour<="00";
        min1<="0010";
        min0<="0000";
        enable<='0';
        wait for 20 ns;
        
        reset<='0';
        hour<="00";
        min1<="0010";
        min0<="0000";
        enable<='1';
        wait;
 
        
    end process;
end Behavioral;
