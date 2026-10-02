----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 25.05.2023 23:10:31
-- Design Name: 
-- Module Name: SETARE_PROGRAM - Behavioral
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
use IEEE.STD_LOGIC_ARITH.ALL;
-- Uncomment the following library declaration if instantiating
-- any Xilinx leaf cells in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity SETARE_PROGRAM is
    Port (
           DOOR : in STD_LOGIC;
           CLK : in STD_LOGIC;
           MODE : in STD_LOGIC;
           RESET : in STD_LOGIC;
           BTN_SETARE_PROG : in STD_LOGIC;
           OK_START : in STD_LOGIC;
           TEMPERATURA : in STD_LOGIC_VECTOR (1 downto 0);
           SETARE_FUNC : in STD_LOGIC_VECTOR (1 downto 0);
           terminare_prog : out std_logic;
           PROGRAM_OUT : out std_logic_VECTOR(7 DOWNTO 0);
           AN : out STD_LOGIC_VECTOR (3 downto 0);
           CAT : out STD_LOGIC_VECTOR (6 downto 0)
           );
end SETARE_PROGRAM;

architecture Behavioral of SETARE_PROGRAM is
signal TIMP, TIMP_RAMAS : STD_LOGIC_VECTOR (11 downto 0) := x"000";
signal timp_incalzire, timp_evacuare: std_logic_vector(9 downto 0);
signal enable_prog, enable_start, incepere_program: std_logic := '0';
signal functii: std_logic_vector(1 downto 0);
signal alg_prog: std_logic_vector(2 downto 0); 
signal prog_int: integer := 0;
signal program_automat: std_logic_vector(13 downto 0);
begin
debouncer:  entity work.debouncer port map ( CLK=>CLK, BTN=> BTN_SETARE_PROG, ENABLE=>ENABLE_PROG);
alegere_program: entity work.counter_3_bits port map ( CLK=>CLK, en=>ENABLE_PROG, output=> alg_prog);
prog_int <= conv_integer(unsigned(alg_prog));
prog_automat : entity work.MEMORIE_PROGRAME port map (ADD => prog_int, cs=>mode, clk=>clk, data_out=>program_automat);
process(OK_START)
begin
    if(RESET = '1') then
        enable_start <= '0';
    elsif(OK_START = '1') then
        enable_start <= '1';
    end if;
end process;

incepere_program <= enable_start and DOOR;

process(mode)
    begin
     if(RESET ='1') then
        timp <="000001000001";        --41 min
        timp_incalzire <= "0000000000";
        timp_evacuare <= "0000000000";
     elsif(enable_start ='0') then 
        if(MODE ='0') then
            if(SETARE_FUNC(0) ='1') then 
                timp<="000001000110";     --46 min
            end if;
            if( SETARE_FUNC(1) = '1') then 
                timp <="000001010001";    --51 min
            end if;
            if(temperatura ="00") then 
                timp(3 downto 0) <=timp(3 downto 0)+1;    ---> 50 C
                timp_incalzire <= "0000000001";
            elsif(temperatura = "01") then 
                timp(3 downto 0) <=timp(3 downto 0)+2;    ---> 60 C
                timp_incalzire <= "0000000010";
            else
                timp(3 downto 0) <=timp(3 downto 0)+3;    ---> 70 C
                timp_incalzire <= "0000000011";
            end if;
            timp_evacuare <= "0000000001";
            functii(1) <= TEMPERATURA(1);
            functii(0) <= TEMPERATURA(0);
        else 
            if(prog_int = 3) then 
                if(temperatura ="00") then 
                    timp_incalzire <= "0000000001";
                elsif(temperatura = "01") then 
                    timp_incalzire <= "0000000010";
                else
                    timp_incalzire <= "0000000011";
                end if;
            else 
                timp_incalzire <= x"00"&program_automat(13 downto 12);
            end if;
            
            timp_evacuare <= program_automat(11 downto 2);
            functii(1) <= program_automat(1);
            functii(0) <= program_automat(0);
            
            case prog_int is
                when 1 => timp <= x"019";
                when 2 => timp <= x"230";
                when 3 => timp <= x"230";
                when 4 => timp <= x"159";
                when 5 => timp <= x"015";
                when others => timp <= x"000";
            end case;
            
        end if;
      end if;
    end process;

Timp_num: entity work.COUNTER_DOWN_TIMP 
    port map(HOUR=> timp(9 downto 8), 
             D1_MIN=> timp(7 downto 4),
             D0_MIN=> timp(3 downto 0), 
             DOOR=> incepere_program,
             CLK =>CLK, 
             RESET=>RESET,          
             TIMP_RAMAS =>TIMP_RAMAS); 
AFIS_TIMP: entity work.SSD port map (CLK=>CLK, d0 => timp_RAMAS(3 downto 0), d1 => timp_RAMAS(7 downto 4), d2 => timp_RAMAS(11 downto 8), d3 => "0000", an=>an, cat=>cat);
PROG_EXEC: entity work.executare_program  
        port map ( CLK =>CLK,
                   DOOR => INCEPERE_PROGRAM,
                   RESET => RESET,
                   SETARE_FUNC => FUNCTII,   --- (0)- prespalare ; (1)- clatire_suplimenatara
                   TIMP_INCALZIRE => TIMP_INCALZIRE,
                   TIMP_EVACUARE => TIMP_EVACUARE,
                   TERMINARE_PROGRAM =>TERMINARE_PROG,
                   PROGRAM => PROGRAM_OUT);

end Behavioral;
