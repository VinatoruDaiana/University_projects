----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 04.04.2024 19:35:56
-- Design Name: 
-- Module Name: MainControl - Behavioral
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

entity MainControl is
  Port(
  clk:in std_logic;
  instr:in std_logic_vector(5 downto 0);
  RegDst:out std_logic;
  ExtOp:out std_logic;
  ALUSrc:out std_logic_vector(3 downto 0);
  Branch:out std_logic;
  Jump:out std_logic;
  MemWrite:out std_logic;
  MemtoReg:out std_logic;
  RegWrite:out std_logic;
  AluOp: out std_logic_vector(3 downto 0)
    );
end MainControl;

architecture Behavioral of MainControl is
begin

RegDst<='0';
ExtOp<='0';
ALUSrc<="0000";
Branch<='0';
Jump<='0';
MemWrite<='0';
MemtoReg<='0';
AluOp<="0000";

    process(instr)
    begin
    
    
        case instr is
            when "000000" => -- Instructiune de tip R
                    
                        RegDst <= '1';
                        ExtOp <= '0';
                        ALUSrc <= "0000";
                        Branch <= '0';
                        Jump <= '0';
                        MemWrite <= '0';
                        MemtoReg <= '0';
                        RegWrite <= '1';
                        AluOp<="0000";
               
               
            
              
                    -- add immediate
                    when "000010" =>
                        RegDst <= '0';
                        ExtOp <= '1';
                        ALUSrc <= "0001";
                        Branch <= '0';
                        Jump <= '0';
                        MemWrite <= instr(5);
                        MemtoReg <= '1';
                        RegWrite <= '1';
                         AluOp<="0001";
                    
                    -- load word
                    when "000100"  =>
                        RegDst <= '0';
                        ExtOp <= '1';
                        ALUSrc <= "0001";
                        Branch <= '0';
                        Jump <= '0';
                        MemWrite <= '0';
                        MemtoReg <= instr(5);
                        RegWrite <= '1';
                        AluOp<="0010";
                        
                     --load byte 
                     when  "001010"  =>
                        RegDst <= '0';
                        ExtOp <= '1';
                        ALUSrc <= "0001";
                        Branch <= '0';
                        Jump <= '0';
                        MemWrite <= '0';
                        MemtoReg <= instr(5);
                        RegWrite <= '1';
                        AluOp<="0101";
                        
                       --store byte
                    when  "001100"  =>
                        RegDst <= '0';
                        ExtOp <= '1';
                        ALUSrc <= "0001";
                        Branch <= '0';
                        Jump <= '0';
                        MemWrite <= '0';
                        MemtoReg <= instr(5);
                        RegWrite <= '1';
                        AluOp<="0110";
                     
                    -- store word
                    when "000110" =>
                        RegDst <= '0';
                        ExtOp <= '1';
                        ALUSrc <= "0001";
                        Branch <= '0';
                        Jump <= '0';
                        MemWrite <= '1';
                        MemtoReg <= '0';
                        RegWrite <= '0';
                        AluOp<="0011";
                    -- branch on equal
                    when "001000" =>
                        RegDst <= '0';
                        ExtOp <= '1';
                        ALUSrc <= "0001";
                        Branch <= '1';
                        Jump <= '0';
                        MemWrite <= '0';
                        MemtoReg <= '0';
                        RegWrite <= '0';
                        AluOp<="0100";
             
    
                    when "001111" =>   --Jump
                        RegDst <= '0';
                        ExtOp <= '0';
                        ALUSrc <= (others => '0');
                        Branch <= '0';
                        Jump <= '1'; -- Jump este întotdeauna 1 pentru instruc?iunea de tip J
                        MemWrite <= '0';
                        MemtoReg <= '0';
                        RegWrite <= '0';
                        AluOp<="0111";
                        
                    when others =>
                        -- Dacã opcode-ul nu corespunde instruc?iunii de tip J, setez toate semnalele la 0
                        RegDst <= '0';
                        ExtOp <= '0';
                        ALUSrc <= (others => '0');
                        Branch <= '0';
                        Jump <= '0';
                        MemWrite <= '0';
                        MemtoReg <= '0';
                        RegWrite <= '0';
                         AluOp<="0000";
                end case;
               
    end process;
end Behavioral;
