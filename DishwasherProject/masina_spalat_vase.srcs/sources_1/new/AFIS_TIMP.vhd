----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 15.03.2023 18:37:11
-- Design Name: 
-- Module Name: SSD - Behavioral
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

entity SSD is
    Port ( CLK : in STD_LOGIC;
           D0 : in STD_LOGIC_VECTOR (3 downto 0);
           D1 : in STD_LOGIC_VECTOR (3 downto 0);
           D2 : in STD_LOGIC_VECTOR (3 downto 0);
           D3 : in STD_LOGIC_VECTOR (3 downto 0);
           an : out STD_LOGIC_VECTOR (3 downto 0);
           cat : out STD_LOGIC_VECTOR (6 downto 0));
end SSD;

architecture Behavioral of SSD is
signal count: std_logic_VECTOR(15 downto 0);
signal input_decoder:std_logic_vector(3 downto 0);
begin

--- counter
    process(clk, count)
    begin
        ---if rising_edge(clk);
        if (clk='1' and clk'event) then count<=count+1;
        end if;
    end process;

--- anod
    process(count)
    begin
        case count(15 downto 14) is
            when "00" =>an <= "1110";
            when "01" =>an <= "1101";
            when "10" =>an <= "1011";
            when others =>an <= "0111";
        end case;
        
    end process;
    
--- digits
    process(count, d0,d1,d2,d3)
    begin
        case count(15 downto 14) is
            when "00" =>input_decoder<= d0;
            when "01" =>input_decoder<= d1;
            when "10" =>input_decoder<= d2;
            when others =>input_decoder<= d3;
        end case;
        
    end process;

process(input_decoder)
begin
	 case input_decoder is
	 
	 ----
	 --  when "111" => cat <= "0010011";
	--   when "110" => cat <= "0000011";
	 --  when "100" => cat <= "0000001";
	  -- when others =>cat <="0000000";
	-----
		when "0000" => cat<="1000000";
		when "0001" => cat<="1111001";
		when "0010" => cat<="0100100";
		when "0011" => cat<="0110000";

		when "0100" => cat<="0011001";
		when "0101" => cat<="0010010";
		when "0110" => cat<="0000010";
		when "0111" => cat<="1111000";

		when "1000" => cat<="0000000";
		when "1001" => cat<="0010000";
		when others => cat<="1000000";
		
		end case;


end process;

end Behavioral;
