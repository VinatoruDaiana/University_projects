----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 29.02.2024 18:38:57
-- Design Name: 
-- Module Name: test_env - Behavioral
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
use IEEE.STD_LOGIC_UNSIGNED.ALL;
use IEEE.STD_LOGIC_1164.ALL;

-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--use IEEE.NUMERIC_STD.ALL;

-- Uncomment the following library declaration if instantiating
-- any Xilinx leaf cells in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity test_env is
    Port ( 
           clk : in STD_LOGIC;
           btn : in STD_LOGIC_VECTOR (4 downto 0);
           sw : in STD_LOGIC_VECTOR (15 downto 0);
           led : out STD_LOGIC_VECTOR (15 downto 0);
           an : out STD_LOGIC_VECTOR (7 downto 0);
           cat : out STD_LOGIC_VECTOR (6 downto 0));
           
           
end test_env;


architecture Behavioral of test_env is



                         -----------------------------------SEMNALELE--------------------------------------------
                         
signal semnal: std_logic_vector(31 downto 0);
signal en:std_logic;
signal en1:std_logic;
signal ra1 : std_logic_vector(5 downto 0);
signal rd1 :  std_logic_vector(31 downto 0);
signal rd2: std_logic_vector(31 downto 0);
signal data:  std_logic_vector(31 downto 0);
signal data1:  std_logic_vector(31 downto 0);
signal do: std_logic_vector(31 downto 0);
signal instruction:std_logic_vector(31 downto 0);
signal PC_4:std_logic_vector(31 downto 0);
signal wd :  std_logic_vector(31 downto 0);


--pt instr_decode
signal Ext_Imm:std_logic_vector(31 downto 0);
signal funct:std_logic_vector(5 downto 0);
signal sa:std_logic_vector(4 downto 0);
signal instrD:std_logic_vector(25 downto 0);

--pt main_control
signal RegDst:std_logic;
signal ExtOp: std_logic;
signal AluSrc:std_logic_vector(3 downto 0);
signal Branch:std_logic;
signal Jump: std_logic;
signal MemWrite:std_logic;
signal MemtoReg:std_logic;
signal RegWrite:std_logic;
signal instrMC:std_logic_vector(5 downto 0);


--pt instruction_execute
signal zero: std_logic;
signal branch_address:std_logic_vector(31 downto 0);
signal AluRes: std_logic_vector(31 downto 0);
signal AluSrc_execute:std_logic;
signal PCsrc: std_logic;
signal JumpAddress:std_logic_vector(31 downto 0);

--pt unitate memorie
signal MemData:std_logic_vector(31 downto 0);
signal AluRes_out: std_logic_vector(31 downto 0);
signal AluOp:std_logic_vector(5 downto 0);




       ------------------------------------------COMPONENTELE-------------------------------------
component MPG is
    Port ( enable : out STD_LOGIC;
           btn : in STD_LOGIC;
           clk : in STD_LOGIC);
end component ;

component SSD is
    Port ( clk : in STD_LOGIC;
           digits : in STD_LOGIC_VECTOR(31 downto 0);
           an : out STD_LOGIC_VECTOR(7 downto 0);
           cat : out STD_LOGIC_VECTOR(6 downto 0));
end component ;

component reg_file is
port ( clk : in std_logic;
ra1 : in std_logic_vector(4 downto 0);
ra2 : in std_logic_vector(4 downto 0);
wa : in std_logic_vector(4 downto 0);
wd : in std_logic_vector(31 downto 0);
regwr : in std_logic;
rd1 : out std_logic_vector(31 downto 0);
rd2 : out std_logic_vector(31 downto 0));
end component;

component ram_wr_1st is
port ( clk : in std_logic;
we : in std_logic;
--en : in std_logic; -- op?ional
addr : in std_logic_vector(5 downto 0);
di : in std_logic_vector(31 downto 0);
do : out std_logic_vector(31 downto 0));
end component ;


component instrFetch is
  Port(
  clk:in STD_LOGIC;
  enable:in STD_LOGIC;
  reset:in STD_LOGIC;
  jump: in STD_LOGIC;
  PCSrc:in STD_LOGIC;
  Jump_Address:in STD_LOGIC_VECTOR(31 downto 0);
  Branch_Address:in STD_LOGIC_VECTOR(31 downto 0);
  Out_pc:out STD_LOGIC_VECTOR(31 downto 0);
  Instruction:out STD_LOGIC_VECTOR(31 downto 0)
  
  );
  
end component;

component InstrDecode is
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

end component;


component MainControl is
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
  RegWrite:out std_logic
    );
end component;


component InstructionExecute is
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
end component;

component UnitateMem is
Port (
   clk:in std_logic;
   en : in STD_LOGIC;
   MemWrite:in std_logic;
   AluRes_in:in std_logic_vector(31 downto 0);
   rd2:in std_logic_vector(31 downto 0);
   MemData:out std_logic_vector(31 downto 0);
   AluRes_out:out std_logic_vector(31 downto 0)
   
 );
end component;


begin


             ----------------------------------------------FUNCTIONALITATEA---------------------------------------------------------------


process(rd1,rd2,Ext_Imm,sa,funct,PC_4,instruction)
begin
   case sw(8 downto 2) is 
    when"000000" =>data1<="000000000000000000000000000"&sa;
    when"000001" =>data1<=rd1;
    when"000010" =>data1<=rd2;
    when"000100" =>data1<=Ext_Imm;
    when"001000" =>data1<=Pc_4;
    when"010000" =>data1<=instruction;
    when"100000" =>data1<="00000000000000000000000000"& funct;
    when others=> data1<=X"00000011";
    
    end case;
end process;


process(MemtoReg)
begin
  if MemtoReg='0' then 
     wd<=AluRes_out;
     else
     wd<=MemData;
     end if;
end process;

PCSrc<=zero and Branch;
JumpAddress<=PC_4(31 downto 28) & instruction(25 downto 0)& "00";

instrFetch1:instrFetch port map(clk,en,sw(0),sw(1),sw(2),X"00000000",X"00000004",PC_4,instruction);
instr_Decode: InstrDecode port map(clk,RegWrite,instrD,RegDst,ExtOp,rd1,rd2,X"00000001",Ext_Imm,funct,sa);
main_contro: MainControl port map(clk,instrMC,RegDst,ExtOp,AluSrc,Branch,Jump,MemWrite,MemtoReg,RegWrite);
instrExecute:InstructionExecute port map  (rd1,AluSrc_execute,rd2,Ext_Imm,sa,funct,AluOp,PC_4,zero,AluRes_out,branch_address);
unitate_Memorie:UnitateMem port map(clk,en,MemWrite,AluRes,rd2,MemData,AluRes_out);
MPG1: MPG port map (en, btn(0), clk);
SSD1: SSD port map(clk=>clk, digits=>data1 , an=>an, cat=>cat);



end Behavioral;
