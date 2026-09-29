
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity test_env is
    Port ( clk : in STD_LOGIC;
           btn : in STD_LOGIC_VECTOR (4 downto 0);
           sw : in STD_LOGIC_VECTOR (15 downto 0);
           led : out STD_LOGIC_VECTOR (15 downto 0);
           an : out STD_LOGIC_VECTOR (7 downto 0);
           cat : out STD_LOGIC_VECTOR (6 downto 0));
end test_env;

architecture Behavioral of test_env is

component MPG is
    Port ( enable : out STD_LOGIC;
           btn : in STD_LOGIC;
           clk : in STD_LOGIC);
end component;

component SSD is
    Port ( clk : in STD_LOGIC;
           digits : in STD_LOGIC_VECTOR(31 downto 0);
           an : out STD_LOGIC_VECTOR(7 downto 0);
           cat : out STD_LOGIC_VECTOR(6 downto 0));
end component;

component IFetch
    Port ( clk : in STD_LOGIC;
           rst : in STD_LOGIC;
           en : in STD_LOGIC;
           BranchAddress : in STD_LOGIC_VECTOR(31 downto 0);
           JumpAddress : in STD_LOGIC_VECTOR(31 downto 0);
           Jump : in STD_LOGIC;
           PCSrc : in STD_LOGIC;
           Instruction : out STD_LOGIC_VECTOR(31 downto 0);
           PCp4 : out STD_LOGIC_VECTOR(31 downto 0));
end component;

component ID
    Port ( clk : in STD_LOGIC;
           en : in STD_LOGIC;    
           Instr : in STD_LOGIC_VECTOR(25 downto 0);
           WD : in STD_LOGIC_VECTOR(31 downto 0);
           RegWrite : in STD_LOGIC;
           ExtOp : in STD_LOGIC;
           RD1 : out STD_LOGIC_VECTOR(31 downto 0);
           RD2 : out STD_LOGIC_VECTOR(31 downto 0);
           Ext_Imm : out STD_LOGIC_VECTOR(31 downto 0);
           func : out STD_LOGIC_VECTOR(5 downto 0);
           sa : out STD_LOGIC_VECTOR(4 downto 0)
           );
end component;

component UC
    Port ( Instr : in STD_LOGIC_VECTOR(5 downto 0);
           RegDst : out STD_LOGIC;
           ExtOp : out STD_LOGIC;
           ALUSrc : out STD_LOGIC;
           Branch : out STD_LOGIC;
           Jump : out STD_LOGIC;
           ALUOp : out STD_LOGIC_VECTOR(2 downto 0);
           MemWrite : out STD_LOGIC;
           MemtoReg : out STD_LOGIC;
           RegWrite : out STD_LOGIC);
end component;

component EX is
    Port ( PCp4 : in STD_LOGIC_VECTOR(31 downto 0);
           RD1 : in STD_LOGIC_VECTOR(31 downto 0);
           RD2 : in STD_LOGIC_VECTOR(31 downto 0);
           Ext_Imm : in STD_LOGIC_VECTOR(31 downto 0);
           func : in STD_LOGIC_VECTOR(5 downto 0);
           sa : in STD_LOGIC_VECTOR(4 downto 0);
           ALUSrc : in STD_LOGIC;
           ALUOp : in STD_LOGIC_VECTOR(2 downto 0);
           BranchAddress : out STD_LOGIC_VECTOR(31 downto 0);
           ALURes : out STD_LOGIC_VECTOR(31 downto 0);
           Zero : out STD_LOGIC;
           RegDst : in STD_LOGIC;
           WriteAddress:out std_logic_vector(4 downto 0);
           rt:in std_logic_vector(4 downto 0);
           rd:in std_logic_vector(4 downto 0)
           );
end component;

component MEM
    port ( clk : in STD_LOGIC;
           en : in STD_LOGIC;
           ALUResIn : in STD_LOGIC_VECTOR(31 downto 0);
           RD2 : in STD_LOGIC_VECTOR(31 downto 0);
           MemWrite : in STD_LOGIC;			
           MemData : out STD_LOGIC_VECTOR(31 downto 0);
           ALUResOut : out STD_LOGIC_VECTOR(31 downto 0));
end component;

signal Instruction, PCp4, RD1, RD2, WD, Ext_imm : STD_LOGIC_VECTOR(31 downto 0); 
signal JumpAddress, BranchAddress, ALURes, ALURes1, MemData : STD_LOGIC_VECTOR(31 downto 0);
signal func : STD_LOGIC_VECTOR(5 downto 0);
signal sa : STD_LOGIC_VECTOR(4 downto 0);
signal zero : STD_LOGIC;
signal digits : STD_LOGIC_VECTOR(31 downto 0);
signal en, rst, PCSrc : STD_LOGIC; 
-- main controls 
signal RegDst, ExtOp, ALUSrc, Branch, Jump, MemWrite, MemtoReg, RegWrite : STD_LOGIC;
signal ALUOp : STD_LOGIC_VECTOR(2 downto 0);


--signals for pipeline

signal IF_ID: std_logic_vector(63 downto 0); --  63-32 -instruction
                                             --  31-0 pc+4
signal ID_EX: std_logic_vector(157 downto 0); --  157-153 instr(15,11)
                                             --  152-148 instr(20,16)
                                             --  147-142 instr(5,0)
                                             --  141-110 ext_unit
                                             --  109-105 instr(10,6)
                                             --  104-73 readdata2
                                             --  72-41 readdata1
                                             --  40-9 pc+4
                                             --  8 regdst
                                             --  7 aluSRC
                                             --  6-4 aluOP
                                             --  3 branch
                                             --  2 memwrite
                                             --  1 regwrite
                                             -- 0 memtoreg
signal EX_MEM:std_logic_vector(105 downto 0); -- 105-101 writeadress
                                             -- 100-69 readdata2
                                             -- 68-37 alures
                                             -- 36 zero
                                             -- 35-4 branchadress
                                             -- 3 branch
                                             -- 2 memwrite
                                             -- 1 regwrite
                                             -- 0 memtoreg
signal MEM_WB:std_logic_vector(70 downto 0); -- 70-66 writeadress
                                             -- 65-34 alures
                                             -- 33-2 readdata
                                             -- 1 regwrite
                                             -- 0 memtoreg


begin

    monopulse : MPG port map(en, btn(0), clk);
    
    -- main units
     inst_IFetch : IFetch port map(clk, btn(1), en, EX_MEM(35 downto 4), JumpAddress, Jump, PCSrc, IF_ID(63 downto 32),IF_ID(31 downto 0));
    inst_ID : ID port map(clk, en, IF_ID(57 downto 32), WD, MEM_WB(1), ExtOp, ID_EX(72 downto 41), ID_EX(104 downto 73), ID_EX(141 downto 110), ID_EX(147 downto 142), ID_EX(109 downto 105));
    inst_UC : UC port map(IF_ID(63 downto 58), ID_EX(8), ExtOp, ID_EX(7), ID_EX(3), Jump, ID_EX(6 downto 4), ID_EX(2), ID_EX(0), ID_EX(1));
    inst_EX : EX port map(ID_EX(40 downto 9), ID_EX(72 downto 41), ID_EX(104 downto 73), ID_EX(141 downto 110), ID_EX(147 downto 142), ID_EX(109 downto 105), ID_EX(7), ID_EX(6 downto 4), EX_MEM(35 downto 4), EX_MEM(68 downto 37), EX_MEM(36),ID_EX(8),EX_MEM(105 downto 101),ID_EX(152 downto 148),ID_EX(157 downto 153)); 
    inst_MEM : MEM port map(clk, en, EX_MEM(68 downto 37), EX_MEM(100 downto 69), EX_MEM(1), MEM_WB(33 downto 2), MEM_WB(65 downto 34));

    process(clk)
    begin
        ID_EX(40 downto 9)<=IF_ID(31 downto 0);  --pc+4
        ID_EX(109 downto 105)<=IF_ID(42 downto 38);  --instr(10,6)
        ID_EX(157 downto 153)<=IF_ID(47 downto 43);  --instr(15,11)
        ID_EX(152 downto 148)<=IF_ID(52 downto 48);  --instr(20,16)
        EX_MEM(0)<=ID_EX(0);  --memtoReg
        EX_MEM(1)<=ID_EX(1);   -- 1 regwrite                                
        EX_MEM(2)<=ID_EX(2); -- 2 memwrite
        EX_MEM(3)<=ID_EX(3);  -- 3 branch
        MEM_WB(0)<=EX_MEM(0);  -- 0 memtoreg
        MEM_WB(1)<=EX_MEM(1);    -- 1 regwrite
        MEM_WB(70 downto 66)<=EX_MEM(105 downto 101); --writeadress
    end process;

    -- Write-Back unit 
    WD <= MemData when MemtoReg = '1' else ALURes1; 

    -- branch control
    PCSrc <= Zero and Branch;

    -- jump address
    JumpAddress <= PCp4(31 downto 28) & Instruction(25 downto 0) & "00";

   -- SSD display MUX
    with sw(7 downto 5) select
        digits <=  Instruction when "000", 
                   PCp4 when "001",
                   RD1 when "010",
                   RD2 when "011",
                   Ext_Imm when "100",
                   ALURes when "101",
                   MemData when "110",
                   WD when "111",
                   (others => 'X') when others; 

    display : SSD port map(clk, digits, an, cat);
    
    -- main controls on the leds
    led(10 downto 0) <= ID_EX(6 downto 4) & ID_EX(8) & ExtOp & ID_EX(7) & ID_EX(3) & Jump & ID_EX(2) & ID_EX(0) & ID_EX(1);
    
end Behavioral;