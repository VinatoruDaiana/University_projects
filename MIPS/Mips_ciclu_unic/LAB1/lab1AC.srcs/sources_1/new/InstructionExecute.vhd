----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 11.04.2024 18:42:17
-- Design Name: 
-- Module Name: InstructionExecute - Behavioral
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

entity InstructionExecute is
 Port ( 
 rd1:in std_logic_vector(31 downto 0);
 AluSrc:in std_logic;
 rd2:in std_logic_vector(31 downto 0);
 ExtImm:in std_logic_vector(31 downto 0);
 sa:in std_logic_vector(4 downto 0);
 func:in std_logic_vector(5 downto 0);
 AluOp:in std_logic_vector (5 downto 0);
 PC_4:in std_logic_vector(31 downto 0);
 Zero:out std_logic;
 AluRes:out std_logic_vector(31 downto 0);
 BranchAddress:out std_logic_vector(31 downto 0)
 );
end InstructionExecute;

architecture Behavioral of InstructionExecute is

signal rd1_sig:std_logic_vector(31 downto 0);
signal Mux_sig:std_logic_vector(31 downto 0);
signal AluOut_sig:std_logic_vector(31 downto 0);
signal AluCtrl:std_logic_vector(5 downto 0);
signal shiftR: std_logic_vector(31 downto 0);
signal shiftL:std_logic_vector(31 downto 0);


begin

--pt mux
  Mux_sig<=rd2 when AluSrc='0' else ExtImm;


--pt alu
process(AluOp,func)
begin

   case AluOp is
      when "000000"=>
       case func is
          when "000000" => AluCtrl<="100000"; --add
          when "000001" => AluCtrl<="100010"; --sub
          when "000010" => AluCtrl<="100100"; --sll
          when "000011" => AluCtrl<="100110"; --srl
          when "000100" => AluCtrl<="101000"; --and
          when "000101" => AluCtrl<="101010"; --or
          when "000110" => AluCtrl<="101100"; --mfhi
          when "000111" => AluCtrl<="101110"; --mflow
          when others =>AluCtrl<="000000";
       end case;
       
     when "000001" => AluCtrl<="000010"; --addi
     when "000010" => AluCtrl<="000100"; --lw
     when "000011" => AluCtrl<="000110"; --sw
     when "000100" => AluCtrl<="001000"; --beq
     when "000101" => AluCtrl<="001010"; --lb
     when "000110" => AluCtrl<="001100"; --sb
     when "000111" => AluCtrl<="001111"; --jump
     when others =>AluCtrl<="000000";
    end case;
end process;


--pt shiftare 
process(sa)

begin
    if sa="000001" then
    shiftL<=Mux_sig(29 downto 0) & "00";
    else
    shiftL<=Mux_sig;

  end if;
end process;


process(sa)

begin
    if sa="000001" then
    shiftR<="00" & Mux_sig(31 downto 2) ;
    else
    shiftR<=Mux_sig;
  end if;
end process;



--pt alU
process(AluCtrl,rd1,Mux_sig,sa)
begin


   case AluCtrl is
   when"000000" => AluOut_sig<= rd1+Mux_sig; --add
   when"000001" => AluOut_sig<=rd1-Mux_sig; --sub
   when"000010" => AluOut_sig<=shiftL ; --sll
   when"000011" => AluOut_sig<=shiftR ; --srl
   when"000100" => AluOut_sig<=rd1 and Mux_sig; --add
   when"000101" => AluOut_sig<=rd1 or Mux_sig; --or
   when"000110" => AluOut_sig<=Mux_sig(31 downto 0); --mvfhi
   when"000111" => AluOut_sig<= Mux_sig(31 downto 0); --mvflow
   when others =>AluOut_sig<=X"00000000";
   end case;
   
   case AluOut_sig is
   when X"00000000" => Zero<='1';
   when others => Zero<='0';
   end case;
   
end process;

AluRes<=AluOut_sig;
BranchAddress<=PC_4+ExtImm;


end Behavioral;
