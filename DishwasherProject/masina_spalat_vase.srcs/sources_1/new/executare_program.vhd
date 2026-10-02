----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 25.05.2023 02:50:57
-- Design Name: 
-- Module Name: executare_program - Behavioral
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

entity executare_program is
    Port ( CLK : in STD_LOGIC;
           DOOR : in STD_LOGIC;
           RESET : in STD_LOGIC;
           SETARE_FUNC : in STD_LOGIC_VECTOR (1 DOWNTO 0);   --- (0)- prespalare ; (1)- clatire_suplimenatara
           TIMP_INCALZIRE : in std_logic_VECTOR (9 DOWNTO 0);
           TIMP_EVACUARE : in std_logic_VECTOR (9 DOWNTO 0);
           TERMINARE_PROGRAM : out std_logic := '0';
           PROGRAM : out STD_LOGIC_VECTOR (7 downto 0));
end executare_program;

architecture Behavioral of executare_program is
type etape is (idle, inmuiere_vase, spalare_principala, eliberare_det, recirc_apa, evac_apa, clatire_apa,clatire_suplimentara,uscare_vase);
signal stare_act: etape := idle;
signal program_stare: std_logic_vector(7 downto 0);
signal timp: std_logic_vector(9 downto 0);
signal terminare_prog,DIVCLOCK, reset_sig: std_logic:='0';
signal c: std_logic_VECTOR(0 TO 7):="00000000";
signal count:integer:=0;
begin
    process(clk, reset) 
    begin
        if (reset = '1') then
            count<= 0;
            
        elsif (rising_edge(clk))then
            if(count= 300000) then 
                    divclock <= not divclock;
                    count<=0;
            else 
                count<= count+1;
            end if;
       end if;
    end process;
    
    process(RESET,divclock)
    begin
        if(RESET = '1') then
            stare_act <= idle;
            program <= "00000000";
            terminare_prog <= '0';
            terminare_program <='0';
        else
            if(rising_edge(divclock) and reset = '0' and terminare_prog = '0') then
               -- done <= c1 or c2 or c3 or c4 or c5 or c6 or c7 or c8 ;
            case stare_act is
                when idle => 
                            if(DOOR ='1') then 
                                program <= "00000001";
                                stare_act <= inmuiere_vase;

                             else 
                                stare_act <= idle;
                             end if;
               -------------------------------INMUIERE VASE----------------------------                            
                when inmuiere_vase =>  
                                    if(c(0) ='1' and DOOR ='1') then 
                                        program <="00000010";
                                        Stare_act <= spalare_principala;
                                     else 
                                        stare_act <= inmuiere_vase; 
                                       end if;   
                -------------------------------SPALARE PRINCIPALA----------------------------                            
                when spalare_principala =>
                                            if(c(1) ='1' and DOOR ='1') then 
                                                stare_act <= eliberare_det;
                                                  program <="00000100";
                                             else 
                                                stare_act <= spalare_principala; 
                                               end if; 
                -------------------------------ELIBERARE DETERGENT----------------------------               
                when eliberare_det => 
                                      if(program_stare(2) ='1' and DOOR ='1') then 
                                        stare_act <= recirc_apa;
                                        program <="00001000";
                                      else 
                                        stare_act <= eliberare_det; 
                                       end if;     
               -------------------------------RECIRCULARE APA----------------------------                            
                when recirc_apa => 
                                 if(c(3) ='1' and DOOR ='1') then 
                                    stare_act <= evac_apa;
                                    program <="00010000";
                                 else 
                                    stare_act <= recirc_apa; 
                                   end if; 
               -------------------------------EVACUARE VASE----------------------------                            
                when evac_apa => 
                            if(c(4) ='1' and DOOR ='1') then 
                                stare_act <= clatire_apa;
                                program <="00100000";
                             else 
                                stare_act <= evac_apa; 
                               end if;  
               -------------------------------CLATIRE----------------------------                                                    
                 when clatire_apa =>
                                    if(c(5) ='1' and DOOR ='1') then 
                                        if(SETARE_FUNC(1) ='1') then
                                            stare_act <= clatire_suplimentara;
                                            program <="01000000";
                                        else
                                            stare_act <= uscare_vase;
                                            program <="10000000";
                                        end if;

                                     else 
                                        stare_act <= clatire_apa; 
                                     end if;  
               -------------------------------CLATIRE SUPLIMENTARA----------------------------                                                                                          
                when clatire_suplimentara => 
                                            if(c(6) ='1' and DOOR ='1') then 
                                                stare_act <= uscare_vase;
                                                program <="10000000";
                                             else 
                                                stare_act <= clatire_suplimentara; 
                                               end if; 
               -------------------------------USCARE----------------------------                                                                                    
                when uscare_vase => if(c(7) = '1' and DOOR = '1') then 
                                        terminare_program <= '1';
                                        terminare_prog <= '1';
                                        stare_act <= idle;
                                    else
                                        stare_act <= uscare_vase; 
                                    end if;
                when others => stare_act <= idle;  program<="00000000";
            end case;   
            end if;                             
         end if;
    end process;

num_inmuiere     :entity work.COUNTER_HM port map (HOUR => timp(9 downto 8), D1_MIN=> timp (7 downto 4), D0_MIN=> timp (3 downto 0),DOOR => DOOR ,CLK=>CLK, RESET=>RESET, ENABLE=>program_stare(0), done=> c(0));
num_spalare_prin :entity work.COUNTER_HM port map (HOUR => timp(9 downto 8), D1_MIN=> timp (7 downto 4), D0_MIN=> timp (3 downto 0),DOOR => DOOR ,CLK=>CLK, RESET=>RESET, ENABLE=>program_stare(1), done=> c(1));
num_eliberare_apa:entity work.COUNTER_HM port map (HOUR => timp(9 downto 8), D1_MIN=> timp (7 downto 4), D0_MIN=> timp (3 downto 0),DOOR => DOOR ,CLK=>CLK, RESET=>RESET, ENABLE=>program_stare(2), done=> c(2));
num_recirculare  :entity work.COUNTER_HM port map (HOUR => timp(9 downto 8), D1_MIN=> timp (7 downto 4), D0_MIN=> timp (3 downto 0),DOOR => DOOR ,CLK=>CLK, RESET=>RESET, ENABLE=>program_stare(3), done=> c(3));
num_evacuare     :entity work.COUNTER_HM port map (HOUR => timp(9 downto 8), D1_MIN=> timp (7 downto 4), D0_MIN=> timp (3 downto 0),DOOR => DOOR ,CLK=>CLK, RESET=>RESET, ENABLE=>program_stare(4), done=> c(4));
num_clatire      :entity work.COUNTER_HM port map (HOUR => timp(9 downto 8), D1_MIN=> timp (7 downto 4), D0_MIN=> timp (3 downto 0),DOOR => DOOR ,CLK=>CLK, RESET=>RESET, ENABLE=>program_stare(5), done=> c(5));
num_clatire_sup  :entity work.COUNTER_HM port map (HOUR => timp(9 downto 8), D1_MIN=> timp (7 downto 4), D0_MIN=> timp (3 downto 0),DOOR => DOOR ,CLK=>CLK, RESET=>RESET, ENABLE=>program_stare(6), done=> c(6));
num_uscare       :entity work.COUNTER_HM port map (HOUR => timp(9 downto 8), D1_MIN=> timp (7 downto 4), D0_MIN=> timp (3 downto 0),DOOR => DOOR ,CLK=>CLK, RESET=>RESET, ENABLE=>program_stare(7), done=> c(7));

 
---------------------------------for outputs----------------------
process (reset)
    begin
        if(RESET ='1') THEN
            program_stare<= "00000000";
            timp<= "0000000000";
            --timp_out <="0000000000";
        else
            case stare_act is
                when idle =>program_stare <="00000000"; 
                when inmuiere_vase => program_stare <="00000001"; 
                                     if(SETARE_FUNC(0) = '1') then 
                                            timp <="0000010000";
                                      else
                                            timp <="0000000101";
                                      end if;
                when spalare_principala => program_stare <="00000010";  timp <=TIMP_INCALZIRE;
                when eliberare_det => program_stare <="00000100";
                when recirc_apa => program_stare <="00001000"; timp <=TIMP_EVACUARE;
                when evac_apa => program_stare <="00010000"; timp <= "0000000001"; 
                when clatire_apa => program_stare <="00100000"; timp <="0000000101";  
                when clatire_suplimentara => program_stare <="01000000"; timp <="0000000101"; 
                when uscare_vase => program_stare <="10000000"; timp <="0000110000";
                when others => program_stare <="00000000";
            end case;
        end if;
end process;
    
end Behavioral;
