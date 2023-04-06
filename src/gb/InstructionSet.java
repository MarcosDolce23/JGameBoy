package gb;

public class InstructionSet {
	
//  #############
//	8-bit opcodes
//  #############
	
//	NOP
//	1  4
//	- - - -
	public static void NOP() {
		// Do nothing
		Main.cpu.cycles += 4;
	}
	
//	LD BC, d16
//	3  12
//	- - - -
	public static void LD_BC_d16() {
		Main.cpu.C = Main.cpu.fetch();
		Main.cpu.B = Main.cpu.fetch();
		Main.cpu.cycles += 12;
	}
	
//	LD (BC), A
//	1  8
//	- - - -
	public static void LD_BC_A() {
		Main.mmu.setByte(Main.cpu.getBC(), Main.cpu.A);
		Main.cpu.cycles += 8;
	}
	
//	INC BC
//	1  8
//	- - - -
	public static void INC_BC() {
		int aux = Main.cpu.getBC();
		aux += 1;
		Main.cpu.B = ((aux >> 8) & 0xff); // Set high 8 bits
		Main.cpu.C = (aux & 0xff); // Set low 8 bits
		Main.cpu.cycles += 8;
	}
	
//	INC B
//	1  4
//	Z 0 H -
	public static void INC_B() {
		Main.cpu.checkHalfCarry8bit(Main.cpu.B,1); // Check if there is Half Carry before the operation
		Main.cpu.B = (Main.cpu.B + 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.B);
		Main.cpu.resetFlagN();
		Main.cpu.cycles += 4;
	}
	
//	DEC B
//	1  4
//	Z 1 H -
	public static void DEC_B() {
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.B,1); // Check if there is Half Carry before the operation
		Main.cpu.B = (Main.cpu.B - 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.B);
		Main.cpu.setFlagN();
		Main.cpu.cycles += 4;
	}
	
//	LD B, d8
//	2  8
//	- - - -
	public static void LD_B_d8() {
		Main.cpu.B = Main.cpu.fetch();
		Main.cpu.cycles += 8;
	}
	
//	RLCA
//	1  4
//	0 0 0 C
	public static void RLCA() {
		Main.cpu.resetFlagZ();
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
		if ((Main.cpu.A & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.A = ((Main.cpu.A << 1) | (Main.cpu.A >> 7)) & 0xff; // Rotate to left
		Main.cpu.cycles += 4;
	}
	
//	LD (a16), SP
//	3  20
//	- - - -
	public static void LD_a16_SP() {
		int index = (Main.cpu.fetch() + (Main.cpu.fetch() << 8)) & 0xffff;
		Main.mmu.setByte(index, Main.cpu.SP & 0xFF);
		Main.mmu.setByte(index + 1, (Main.cpu.SP >> 8) & 0xff);
		Main.cpu.cycles += 20;
	}
	
//	ADD HL, BC
//	1  8
//	- 0 H C
	public static void ADD_HL_BC() {
		Main.cpu.resetFlagN();
		Main.cpu.checkHalfCarry16bit(Main.cpu.getHL(), Main.cpu.getBC());
		int res = (Main.cpu.getHL() + Main.cpu.getBC());
		Main.cpu.checkCarry16bit(res);
		Main.cpu.L = res & 0xff;
		Main.cpu.H = (res >> 8) & 0xff;
		Main.cpu.cycles += 8;
	}
	
//	LD A, (BC)
//	1  8
//	- - - -
	public static void LD_A_BC() {
		Main.cpu.A = Main.mmu.getByte(Main.cpu.getBC());
		Main.cpu.cycles += 8;
	}
	
//	DEC BC
//	1  8
//	- - - -
	public static void DEC_BC() {
		int aux = Main.cpu.getBC();
		aux -= 1;
		Main.cpu.B = ((aux >> 8) & 0xff); // Set high 8 bits
		Main.cpu.C = (aux & 0xff); // Set low 8 bits
		Main.cpu.cycles += 8;
	}
	
//	INC C
//	1  4
//	Z 0 H -
	public static void INC_C() {
		Main.cpu.checkHalfCarry8bit(Main.cpu.C,1); // Check if there is Half Carry before the operation
		Main.cpu.C = (Main.cpu.C + 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.C);
		Main.cpu.resetFlagN();
		Main.cpu.cycles += 4;
	}
	
//	DEC C
//	1  4
//	Z 1 H -
	public static void DEC_C() {
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.C,1); // Check if there is Half Carry before the operation
		Main.cpu.C = (Main.cpu.C - 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.C);
		Main.cpu.setFlagN();
		Main.cpu.cycles += 4;
	}
	
//	LD C, d8
//	2  8
//	- - - -
	public static void LD_C_d8() {
		Main.cpu.C = Main.cpu.fetch();
		Main.cpu.cycles += 8;
	}
	
//	RRCA
//	1  4
//	0 0 0 C
	public static void RRCA() {
		Main.cpu.resetFlagZ();
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
		if ((Main.cpu.A & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.A = ((Main.cpu.A << 7) | (Main.cpu.A >> 1)) & 0xff; // Rotate to right
		Main.cpu.cycles += 4;
	}
	
//	STOP d8
//	2  4
//	- - - -
	public static void STOP() {
		Main.cpu.cycles += 0;
	}
	
//	LD DE, d16
//	3  12
//	- - - -
	public static void LD_DE_d16() {
		Main.cpu.E = Main.cpu.fetch();
		Main.cpu.D = Main.cpu.fetch();
		Main.cpu.cycles += 12;
	}
	
//	LD (DE), A
//	1  8
//	- - - -
	public static void LD_DE_A() {
		Main.mmu.setByte(Main.cpu.getDE(), Main.cpu.A);
		Main.cpu.cycles += 8;
	}
	
//	INC DE
//	1  8
//	- - - -
	public static void INC_DE() {
		int aux = Main.cpu.getDE();
		aux += 1;
		Main.cpu.D = ((aux >> 8) & 0xff); // Set high 8 bits
		Main.cpu.E = (aux & 0xff); // Set low 8 bits
		Main.cpu.cycles += 8;
	}
	
//	INC D
//	1  4
//	Z 0 H -
	public static void INC_D() {
		Main.cpu.checkHalfCarry8bit(Main.cpu.D,1); // Check if there is Half Carry before the operation
		Main.cpu.D = (Main.cpu.D + 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.D);
		Main.cpu.resetFlagN();
		Main.cpu.cycles += 4;
	}

//	DEC D
//	1  4
//	Z 1 H -
	public static void DEC_D() {
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.D,1); // Check if there is Half Carry before the operation
		Main.cpu.D = (Main.cpu.D - 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.D);
		Main.cpu.setFlagN();
		Main.cpu.cycles += 4;
	}
	
//	LD D, d8
//	2  8
//	- - - -
	public static void LD_D_d8() {
		Main.cpu.D = Main.cpu.fetch();
		Main.cpu.cycles += 8;
	}
	
//	RLA
//	1  4
//	0 0 0 C
	public static void RLA() {
		Main.cpu.resetFlagZ();
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = Main.cpu.getFlagC();
		
		// Set carry flag
		if ((Main.cpu.A & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.A = (Main.cpu.A << 1) & 0xff; // Rotate to the right
		Main.cpu.A |= c;
		Main.cpu.cycles += 4;
	}

//	JR r8
//	2  12
//	- - - -
	public static void JR_s8() {
		int s8 = Main.cpu.fetchSigned();
		Main.cpu.PC = (Main.cpu.PC + s8) & 0xffff;
		Main.cpu.cycles += 12;
	}
	
//	ADD HL, DE
//	1  8
//	- 0 H C
	public static void ADD_HL_DE() {
		Main.cpu.resetFlagN();
		Main.cpu.checkHalfCarry16bit(Main.cpu.getHL(), Main.cpu.getDE());
		int res = (Main.cpu.getHL() + Main.cpu.getDE());
		Main.cpu.checkCarry16bit(res);
		Main.cpu.L = res & 0xff;
		Main.cpu.H = (res >> 8) & 0xff;
		Main.cpu.cycles += 8;
	}

//	LD A, (DE)
//	1  8
//	- - - -
	public static void LD_A_DE() {
		Main.cpu.A = Main.mmu.getByte(Main.cpu.getDE());
		Main.cpu.cycles += 8;
	}

//	DEC DE
//	1  8
//	- - - -
	public static void DEC_DE() {
		int aux = Main.cpu.getDE();
		aux -= 1;
		Main.cpu.D = ((aux >> 8) & 0xff); // Set high 8 bits
		Main.cpu.E = (aux & 0xff); // Set low 8 bits
		Main.cpu.cycles += 8;
	}

//	INC E
//	1  4
//	Z 0 H -
	public static void INC_E() {
		Main.cpu.checkHalfCarry8bit(Main.cpu.E,1); // Check if there is Half Carry before the operation
		Main.cpu.E = (Main.cpu.E + 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.E);
		Main.cpu.resetFlagN();
		Main.cpu.cycles += 4;
	}

//	DEC E
//	1  4
//	Z 1 H -
	public static void DEC_E() {
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.E,1); // Check if there is Half Carry before the operation
		Main.cpu.E = (Main.cpu.E - 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.E);
		Main.cpu.setFlagN();
		Main.cpu.cycles += 4;
	}

//	LD E, d8
//	2  8
//	- - - -
	public static void LD_E_d8() {
		Main.cpu.E = Main.cpu.fetch();
		Main.cpu.cycles += 8;
	}
	
//	RRA
//	1  4
//	0 0 0 C
	public static void RRA() {
		Main.cpu.resetFlagZ();
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = (Main.cpu.getFlagC() << 7);

		// Set carry flag
		if ((Main.cpu.A & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.A = (Main.cpu.A >> 1) & 0xff; // Rotate to the right
		Main.cpu.A |= c;
		Main.cpu.cycles += 4;
	}
	
	
//	JR NZ, r8
//	2  12/8
//	- - - -
	public static void JR_NZ_s8() {
		if (Main.cpu.getFlagZ() == 0) {
			JR_s8();
		} else {
			Main.cpu.fetch();
			Main.cpu.cycles += 8;
		}
	}

//	LD HL, d16
//	3  12
//	- - - -
	public static void LD_HL_d16() {
		Main.cpu.L = Main.cpu.fetch();
		Main.cpu.H = Main.cpu.fetch();
		Main.cpu.cycles += 12;
	}

//	LD (HL+), A
//	1  8
//	- - - -
	public static void LD_HLinc_A() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.cpu.A);
		
		// TO DO: que pasa cuando incremento enteros paso los 8 bits y luego los paso a la mem?
		// en principio parece no generar problemas.
		int aux  = Main.cpu.getHL() + 1;
		Main.cpu.L = aux & 0xFF; // Set the lower 8 bits
		Main.cpu.H = (aux >> 8) & 0xff; // Set the higher 8 bits
		Main.cpu.cycles += 8;
	}

//	INC HL
//	1  8
//	- - - -
	public static void INC_HL() {
		int aux = Main.cpu.getHL();
		aux += 1;
		Main.cpu.H = ((aux >> 8) & 0xff); // Set high 8 bits
		Main.cpu.L = (aux & 0xff); // Set low 8 bits
		Main.cpu.cycles += 8;
	}

//	INC H
//	1  4
//	Z 0 H -
	public static void INC_H() {
		Main.cpu.checkHalfCarry8bit(Main.cpu.H,1); // Check if there is Half Carry before the operation
		Main.cpu.H = (Main.cpu.H + 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.H);
		Main.cpu.resetFlagN();
		Main.cpu.cycles += 4;
	}

//	DEC H
//	1  4
//	Z 1 H -
	public static void DEC_H() {
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.H,1); // Check if there is Half Carry before the operation
		Main.cpu.H = (Main.cpu.H - 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.H);
		Main.cpu.setFlagN();
		Main.cpu.cycles += 4;
	}

//	LD H, d8
//	2  8
//	- - - -
	public static void LD_H_d8() {
		Main.cpu.H = Main.cpu.fetch();
		Main.cpu.cycles += 8;
	}
	
//	DAA
//	1  4
//	Z - 0 C
	public static void DAA() {
		// note: assumes a is a uint8_t and wraps from 0xff to 0
		if (Main.cpu.getFlagN() == 0) {  // after an addition, adjust if (half-)carry occurred or if result is out of bounds
		  
			if ((Main.cpu.getFlagC() == 1) || (Main.cpu.A > 0x99)) {
			  Main.cpu.A += 0x60;
			  Main.cpu.setFlagC();
		  }
		  
		  if ((Main.cpu.getFlagH() == 1) || ((Main.cpu.A & 0x0f) > 0x09)) {
			  Main.cpu.A += 0x6; 
		  }
		
		} else {  // after a subtraction, only adjust if (half-)carry occurred
			
			if (Main.cpu.getFlagC() == 1) {
				Main.cpu.A -= 0x60;
			}
			
			if (Main.cpu.getFlagH() == 1) {
				Main.cpu.A -= 0x6;
			}
			
		}
		
		Main.cpu.A &= 0xff;
		
		// these flags are always updated
		Main.cpu.checkZero8bit(Main.cpu.A);; // the usual z flag
		Main.cpu.resetFlagH(); // h flag is always cleared
		
		Main.cpu.cycles += 4;
	}

//	JR Z, r8
//	2  12/8
//	- - - -
	public static void JR_Z_s8() {
		if (Main.cpu.getFlagZ() == 1) {
			JR_s8();
		} else {
			Main.cpu.fetch();
			Main.cpu.cycles += 8;
		}
	}
	
//	ADD HL, HL
//	1  8
//	- 0 H C
	public static void ADD_HL_HL() {
		Main.cpu.resetFlagN();
		Main.cpu.checkHalfCarry16bit(Main.cpu.getHL(), Main.cpu.getHL());
		int res = (Main.cpu.getHL() + Main.cpu.getHL());
		Main.cpu.checkCarry16bit(res);
		Main.cpu.L = res & 0xff;
		Main.cpu.H = (res >> 8) & 0xff;
		Main.cpu.cycles += 8;
	}

//	LD A, (HL+)
//	1  8
//	- - - -
	public static void LD_A_HLinc() {
		Main.cpu.A = Main.mmu.getByte(Main.cpu.getHL());
		int aux  = Main.cpu.getHL() + 1;
		Main.cpu.L = aux & 0xFF; // Set the lower 8 bits
		Main.cpu.H = (aux >> 8) & 0xff; // Set the higher 8 bits
		Main.cpu.cycles += 8;
	}

//	DEC HL
//	1  8
//	- - - -
	public static void DEC_HL() {
		int aux = Main.cpu.getHL();
		aux -= 1;
		Main.cpu.H = ((aux >> 8) & 0xff); // Set high 8 bits
		Main.cpu.L = (aux & 0xff); // Set low 8 bits
		Main.cpu.cycles += 8;
	}

//	INC L
//	1  4
//	Z 0 H -
	public static void INC_L() {
		Main.cpu.checkHalfCarry8bit(Main.cpu.L,1); // Check if there is Half Carry before the operation
		Main.cpu.L = (Main.cpu.L + 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.L);
		Main.cpu.resetFlagN();
		Main.cpu.cycles += 4;
	}

//	DEC L
//	1  4
//	Z 1 H -
	public static void DEC_L() {
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.L,1); // Check if there is Half Carry before the operation
		Main.cpu.L = (Main.cpu.L - 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.L);
		Main.cpu.setFlagN();
		Main.cpu.cycles += 4;
	}

//	LD L, d8
//	2  8
//	- - - -
	public static void LD_L_d8() {
		Main.cpu.L = Main.cpu.fetch();
		Main.cpu.cycles += 8;
	}
	
//	CPL
//	1  4
//	- 1 1 -
	public static void CPL() {
		Main.cpu.setFlagN();
		Main.cpu.setFlagH();
		Main.cpu.A = (~Main.cpu.A) & 0xff;
		Main.cpu.cycles += 4;
	}

//	JR NC, r8
//	2  12/8
//	- - - -
	public static void JR_NC_s8() {
		if (Main.cpu.getFlagC() == 0) {
			JR_s8();
		} else {
			Main.cpu.fetch();
			Main.cpu.cycles += 8;
		}
	}

//	LD SP, d16
//	3  12
//	- - - -
	public static void LD_SP_d16() {
		int value = Main.cpu.fetch() + (Main.cpu.fetch() << 8);
		Main.cpu.SP = value & 0xffff;
		Main.cpu.cycles += 12;
	}

//	LD (HL-), A
//	1  8
//	- - - -
	public static void LD_HLdec_A() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.cpu.A);
		
		// TO DO: que pasa cuando decremento enteros y luego los paso a la mem?
		// en principio parece no generar problemas.
		int aux  = Main.cpu.getHL() - 1;
		Main.cpu.L = aux & 0xFF; // Set the lower 8 bits
		Main.cpu.H = (aux >> 8) & 0xff; // Set the higher 8 bits
		Main.cpu.cycles += 8;
	}

//	INC SP
//	1  8
//	- - - -
	public static void INC_SP() {
		Main.cpu.SP += 1;
		Main.cpu.cycles += 8;
	}

//	INC (HL)
//	1  12
//	Z 0 H -
	public static void INC_HLmem() {
		Main.cpu.checkHalfCarry8bit(Main.mmu.getByte(Main.cpu.getHL()),1); // Check if there is Half Carry before the operation
		Main.mmu.setByte(Main.cpu.getHL(), (Main.mmu.getByte(Main.cpu.getHL()) + 1) & 0xff);
		Main.cpu.checkZero8bit(Main.mmu.getByte(Main.cpu.getHL()));
		Main.cpu.resetFlagN();
		Main.cpu.cycles += 12;
	}

//	DEC (HL)
//	1  12
//	Z 1 H -
	public static void DEC_HLmem() {
		Main.cpu.checkHalfCarry8bitSub(Main.mmu.getByte(Main.cpu.getHL()),1); // Check if there is Half Carry before the operation
		Main.mmu.setByte(Main.cpu.getHL(), (Main.mmu.getByte(Main.cpu.getHL()) - 1) & 0xff);
		Main.cpu.checkZero8bit(Main.mmu.getByte(Main.cpu.getHL()));
		Main.cpu.setFlagN();
		Main.cpu.cycles += 12;
	}

//	LD (HL), d8
//	2  12
//	- - - -
	public static void LD_HL_d8() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.cpu.fetch());
		Main.cpu.cycles += 12;
	}
	
//	SCF
//	1  4
//	- 0 0 1
	public static void SCF() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.setFlagC();
		Main.cpu.cycles += 4;
	}

//	JR C, r8
//	2  12/8
//	- - - -
	public static void JR_C_s8() {
		if (Main.cpu.getFlagC() == 1) {
			JR_s8();
		} else {
			Main.cpu.fetch();
			Main.cpu.cycles += 8;
		}
	}
	
//	ADD HL, SP
//	1  8
//	- 0 H C
	public static void ADD_HL_SP() {
		Main.cpu.resetFlagN();
		int res = Main.cpu.getHL() + Main.cpu.SP;
		Main.cpu.checkHalfCarry16bit(Main.cpu.getHL(), Main.cpu.SP);
		Main.cpu.checkCarry16bit(res);
		Main.cpu.L = res & 0xff;
		Main.cpu.H = (res >> 8) & 0xff;
		Main.cpu.cycles += 8;
	}

//	LD A, (HL-)
//	1  8
//	- - - -
	public static void LD_A_HLdec() {
		Main.cpu.A = Main.mmu.getByte(Main.cpu.getHL());
		int aux  = Main.cpu.getHL() - 1;
		Main.cpu.L = aux & 0xFF; // Set the lower 8 bits
		Main.cpu.H = (aux >> 8) & 0xff; // Set the higher 8 bits
		Main.cpu.cycles += 8;
	}

//	DEC SP
//	1  8
//	- - - -
	public static void DEC_SP() {
		Main.cpu.SP -= 1;
		Main.cpu.cycles += 8;
	}

//	INC A
//	1  4
//	Z 0 H -
	public static void INC_A() {
		Main.cpu.checkHalfCarry8bit(Main.cpu.A,1); // Check if there is Half Carry before the operation
		Main.cpu.A = (Main.cpu.A + 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.cycles += 4;
	}

//	DEC A
//	1  4
//	Z 1 H -
	public static void DEC_A() {
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.A,1); // Check if there is Half Carry before the operation
		Main.cpu.A = (Main.cpu.A - 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.setFlagN();
		Main.cpu.cycles += 4;
	}

//	LD A, d8
//	2  8
//	- - - -
	public static void LD_A_d8() {
		Main.cpu.A = Main.cpu.fetch();
		Main.cpu.cycles += 8;
	}

//	CCF
//	1  4
//	- 0 0 C
	public static void CFF() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		if (Main.cpu.getFlagC() == 0) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		Main.cpu.cycles += 4;
	}
	
//	LD B, B
	public static void LD_B_B() {
		Main.cpu.cycles += 4;
	}
	
//	LD B, C
	public static void LD_B_C() {
		Main.cpu.B = Main.cpu.C;
		Main.cpu.cycles += 4;
	}

//	LD B, D
	public static void LD_B_D() {
		Main.cpu.B = Main.cpu.D;
		Main.cpu.cycles += 4;
	}
	
//	LD B, E
	public static void LD_B_E() {
		Main.cpu.B = Main.cpu.E;
		Main.cpu.cycles += 4;
	}
	
//	LD B, H
	public static void LD_B_H() {
		Main.cpu.B = Main.cpu.H;
		Main.cpu.cycles += 4;
	}
	
//	LD B, L
	public static void LD_B_L() {
		Main.cpu.B = Main.cpu.L;
		Main.cpu.cycles += 4;
	}

//	 LD B, (HL)
	public static void LD_B_HL() {
		Main.cpu.B = Main.mmu.getByte(Main.cpu.getHL());
		Main.cpu.cycles += 8;
	}

//	LD B, A
	public static void LD_B_A() {
		Main.cpu.B = Main.cpu.A;
		Main.cpu.cycles += 4;
	}
	
//	LD C, B
	public static void LD_C_B() {
		Main.cpu.C = Main.cpu.B;
		Main.cpu.cycles += 4;
	}
	
//	LD C, C
	public static void LD_C_C() {
		Main.cpu.cycles += 4;
	}
	
//	LD C, D
	public static void LD_C_D() {
		Main.cpu.C = Main.cpu.D;
		Main.cpu.cycles += 4;
	}
	
//	LD C, E
	public static void LD_C_E() {
		Main.cpu.C = Main.cpu.E;
		Main.cpu.cycles += 4;
	}
	
//	LD C, H
	public static void LD_C_H() {
		Main.cpu.C = Main.cpu.H;
		Main.cpu.cycles += 4;
	}
	
//	LD C, L
	public static void LD_C_L() {
		Main.cpu.C = Main.cpu.L;
		Main.cpu.cycles += 4;
	}
	
//	 LD C, (HL)
	public static void LD_C_HL() {
		Main.cpu.C = Main.mmu.getByte(Main.cpu.getHL());
		Main.cpu.cycles += 8;
	}

//	LD C, A
	public static void LD_C_A() {
		Main.cpu.C = Main.cpu.A;
		Main.cpu.cycles += 4;
	}
	
//	LD D, B
	public static void LD_D_B() {
		Main.cpu.D = Main.cpu.B;
		Main.cpu.cycles += 4;
	}
	
//	LD D, C
	public static void LD_D_C() {
		Main.cpu.D = Main.cpu.C;
		Main.cpu.cycles += 4;
	}
	
//	LD D, D
	public static void LD_D_D() {
		Main.cpu.cycles += 4;
	}
	
//	LD D, E
	public static void LD_D_E() {
		Main.cpu.D = Main.cpu.E;
		Main.cpu.cycles += 4;
	}
	
//	LD D, H
	public static void LD_D_H() {
		Main.cpu.D = Main.cpu.H;
		Main.cpu.cycles += 4;
	}
	
//	LD D, L
	public static void LD_D_L() {
		Main.cpu.D = Main.cpu.L;
		Main.cpu.cycles += 4;
	}

//	LD D, (HL)
	public static void LD_D_HL() {
		Main.cpu.D = Main.mmu.getByte(Main.cpu.getHL());
		Main.cpu.cycles += 8;
	}

//	LD D, A
	public static void LD_D_A() {
		Main.cpu.D = Main.cpu.A;
		Main.cpu.cycles += 4;
	}
	
//	LD E, B
	public static void LD_E_B() {
		Main.cpu.E = Main.cpu.B;
		Main.cpu.cycles += 4;
	}
	
//	LD E, C
	public static void LD_E_C() {
		Main.cpu.E = Main.cpu.C;
		Main.cpu.cycles += 4;
	}
	
//	LD E, D
	public static void LD_E_D() {
		Main.cpu.E = Main.cpu.D;
		Main.cpu.cycles += 4;
	}
	
//	LD E, E
	public static void LD_E_E() {
		Main.cpu.cycles += 4;
	}
	
//	LD E, H
	public static void LD_E_H() {
		Main.cpu.E = Main.cpu.H;
		Main.cpu.cycles += 4;
	}
	
//	LD E, L
	public static void LD_E_L() {
		Main.cpu.E = Main.cpu.L;
		Main.cpu.cycles += 4;
	}

//	LD E, (HL)
	public static void LD_E_HL() {
		Main.cpu.E = Main.mmu.getByte(Main.cpu.getHL());
		Main.cpu.cycles += 8;
	}

//	LD E, A
	public static void LD_E_A() {
		Main.cpu.E = Main.cpu.A;
		Main.cpu.cycles += 4;
	}
		
//	LD H, B
	public static void LD_H_B() {
		Main.cpu.H = Main.cpu.B;
		Main.cpu.cycles += 4;
	}
	
//	LD H, C
	public static void LD_H_C() {
		Main.cpu.H = Main.cpu.C;
		Main.cpu.cycles += 4;
	}
	
//	LD H, D
	public static void LD_H_D() {
		Main.cpu.H = Main.cpu.D;
		Main.cpu.cycles += 4;
	}
	
//	LD H, E
	public static void LD_H_E() {
		Main.cpu.H = Main.cpu.E;
		Main.cpu.cycles += 4;
	}
	
//	LD H, H
	public static void LD_H_H() {
		Main.cpu.cycles += 4;
	}
	
//	LD H, L
	public static void LD_H_L() {
		Main.cpu.H = Main.cpu.L;
		Main.cpu.cycles += 4;
	}

//	LD H, (HL)
	public static void LD_H_HL() {
		Main.cpu.H = Main.mmu.getByte(Main.cpu.getHL());
		Main.cpu.cycles += 8;
	}

//	LD H, A
	public static void LD_H_A() {
		Main.cpu.H = Main.cpu.A;
		Main.cpu.cycles += 4;
	}
	
//	LD L, B
	public static void LD_L_B() {
		Main.cpu.L = Main.cpu.B;
		Main.cpu.cycles += 4;
	}
	
//	LD L, C
	public static void LD_L_C() {
		Main.cpu.L = Main.cpu.C;
		Main.cpu.cycles += 4;
	}
	
//	LD L, D
	public static void LD_L_D() {
		Main.cpu.L = Main.cpu.D;
		Main.cpu.cycles += 4;
	}
	
//	LD L, E
	public static void LD_L_E() {
		Main.cpu.L = Main.cpu.E;
		Main.cpu.cycles += 4;
	}
	
//	LD L, H
	public static void LD_L_H() {
		Main.cpu.L = Main.cpu.H;
		Main.cpu.cycles += 4;
	}
	
//	LD L, L
	public static void LD_L_L() {
		Main.cpu.cycles += 4;
	}

//	LD L, (HL)
	public static void LD_L_HL() {
		Main.cpu.L = Main.mmu.getByte(Main.cpu.getHL());
		Main.cpu.cycles += 8;
	}

//	LD L, A
	public static void LD_L_A() {
		Main.cpu.L = Main.cpu.A;
		Main.cpu.cycles += 4;
	}
	
//	LD (HL), B
	public static void LD_HL_B() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.cpu.B);
		Main.cpu.cycles += 8;
	}
	
//	LD (HL), C
	public static void LD_HL_C() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.cpu.C);
		Main.cpu.cycles += 8;
	}
	
//	LD (HL), D
	public static void LD_HL_D() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.cpu.D);
		Main.cpu.cycles += 8;
	}
	
//	LD (HL), E
	public static void LD_HL_E() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.cpu.E);
		Main.cpu.cycles += 8;
	}
	
//	LD (HL), H
	public static void LD_HL_H() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.cpu.H);
		Main.cpu.cycles += 8;
	}
	
//	LD (HL), L
	public static void LD_HL_L() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.cpu.L);
		Main.cpu.cycles += 8;
	}
	
//	HALT
	public static void HALT() {
        int IF = Main.mmu.getByte(0xff0f);
        int IE = Main.mmu.getByte(0xffff);

        // Exit hell if an interrupt is valid to be serviced
        if ((IF & IE & 0x1f) > 0) {
            Main.cpu.haltbugAtm = !(Main.cpu.HALT || Main.cpu.IME);
            Main.cpu.HALT = false;
        }
         // If we cannot exit hell, stay in hell.
        else {
            Main.cpu.HALT = true;

            Main.cpu.PC--;
            Main.cpu.PC &= 0xffff;
        }
        
		Main.cpu.cycles += 4;
	}
	
//	LD (HL), A
	public static void LD_HL_A() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.cpu.A);
		Main.cpu.cycles += 8;
	}

//	LD A, B
	public static void LD_A_B() {
		Main.cpu.A = Main.cpu.B;
		Main.cpu.cycles += 4;
	}
	
//	LD A, C
	public static void LD_A_C() {
		Main.cpu.A = Main.cpu.C;
		Main.cpu.cycles += 4;
	}
	
//	LD A, D
	public static void LD_A_D() {
		Main.cpu.A = Main.cpu.D;
		Main.cpu.cycles += 4;
	}
	
//	LD A, E
	public static void LD_A_E() {
		Main.cpu.A = Main.cpu.E;
		Main.cpu.cycles += 4;
	}
	
//	LD A, H
	public static void LD_A_H() {
		Main.cpu.A = Main.cpu.H;
		Main.cpu.cycles += 4;
	}
	
//	LD A, L
	public static void LD_A_L() {
		Main.cpu.A = Main.cpu.L;
		Main.cpu.cycles += 4;
	}

//	LD A, (HL)
	public static void LD_A_HL() {
		Main.cpu.A = Main.mmu.getByte(Main.cpu.getHL());
		Main.cpu.cycles += 8;
	}
	
//	LD A, A
	public static void LD_A_A() {
		Main.cpu.cycles += 4;
	}

//	ADD A, B
//	Z 0 H C
	public static void ADD_A_B() {
		Main.cpu.checkHalfCarry8bit(Main.cpu.A, Main.cpu.B);
		Main.cpu.checkCarry8bit(Main.cpu.A + Main.cpu.B);
		Main.cpu.A = (Main.cpu.A + Main.cpu.B) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.cycles += 4;
	}
	
//	ADD A, C
//	Z 0 H C
	public static void ADD_A_C() {
		Main.cpu.checkHalfCarry8bit(Main.cpu.A, Main.cpu.C);
		Main.cpu.checkCarry8bit(Main.cpu.A + Main.cpu.C);
		Main.cpu.A = (Main.cpu.A + Main.cpu.C) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.cycles += 4;
	}
	
//	ADD A, D
//	Z 0 H C
	public static void ADD_A_D() {
		Main.cpu.checkHalfCarry8bit(Main.cpu.A, Main.cpu.D);
		Main.cpu.checkCarry8bit(Main.cpu.A + Main.cpu.D);
		Main.cpu.A = (Main.cpu.A + Main.cpu.D) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.cycles += 4;
	}
	
//	ADD A, E
//	Z 0 H C
	public static void ADD_A_E() {
		Main.cpu.checkHalfCarry8bit(Main.cpu.A, Main.cpu.E);
		Main.cpu.checkCarry8bit(Main.cpu.A + Main.cpu.E);
		Main.cpu.A = (Main.cpu.A + Main.cpu.E) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.cycles += 4;
	}
	
//	ADD A, H
//	Z 0 H C
	public static void ADD_A_H() {
		Main.cpu.checkHalfCarry8bit(Main.cpu.A, Main.cpu.H);
		Main.cpu.checkCarry8bit(Main.cpu.A + Main.cpu.H);
		Main.cpu.A = (Main.cpu.A + Main.cpu.H) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.cycles += 4;
	}
	
//	ADD A, L
//	Z 0 H C
	public static void ADD_A_L() {
		Main.cpu.checkHalfCarry8bit(Main.cpu.A, Main.cpu.L);
		Main.cpu.checkCarry8bit(Main.cpu.A + Main.cpu.L);
		Main.cpu.A = (Main.cpu.A + Main.cpu.L) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.cycles += 4;
	}
	
//	ADD A, (HL)
//	Z 0 H C
	public static void ADD_A_HL() {
		Main.cpu.checkHalfCarry8bit(Main.cpu.A, Main.mmu.getByte(Main.cpu.getHL()));
		Main.cpu.checkCarry8bit(Main.cpu.A + Main.mmu.getByte(Main.cpu.getHL()));
		Main.cpu.A = (Main.cpu.A + Main.mmu.getByte(Main.cpu.getHL())) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		
		Main.cpu.resetFlagN();
		Main.cpu.cycles += 8;
	}

//	ADD A, A
//	Z 0 H C
	public static void ADD_A_A() {
		Main.cpu.checkHalfCarry8bit(Main.cpu.A, Main.cpu.A);
		Main.cpu.checkCarry8bit(Main.cpu.A + Main.cpu.A);
		Main.cpu.A = (Main.cpu.A + Main.cpu.A) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.cycles += 4;
	}
	
//	ADC A, B
//	Z 0 H C
	public static void ADC_A_B() {
		int val = Main.cpu.B + Main.cpu.getFlagC();
		
		int sum = Main.cpu.A + val;
		int res = sum & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagN();
		if ((Main.cpu.A & 0xf) + (Main.cpu.B & 0xf) + Main.cpu.getFlagC() > 0xf) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bit(sum);

		Main.cpu.A = res;
		Main.cpu.cycles += 4;
	}
	
//	ADC A, C
//	Z 0 H C
	public static void ADC_A_C() {
		int val = Main.cpu.C + Main.cpu.getFlagC();
		
		int sum = Main.cpu.A + val;
		int res = sum & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagN();
		if ((Main.cpu.A & 0xf) + (Main.cpu.C & 0xf) + Main.cpu.getFlagC() > 0xf) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bit(sum);

		Main.cpu.A = res;
		Main.cpu.cycles += 4;
	}
	
//	ADC A, D
//	Z 0 H C
	public static void ADC_A_D() {
		int val = Main.cpu.D + Main.cpu.getFlagC();
		
		int sum = Main.cpu.A + val;
		int res = sum & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagN();
		if ((Main.cpu.A & 0xf) + (Main.cpu.D & 0xf) + Main.cpu.getFlagC() > 0xf) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bit(sum);

		Main.cpu.A = res;
		Main.cpu.cycles += 4;
	}
	
//	ADC A, E
//	Z 0 H C
	public static void ADC_A_E() {
		int val = Main.cpu.E + Main.cpu.getFlagC();
		
		int sum = Main.cpu.A + val;
		int res = sum & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagN();
		if ((Main.cpu.A & 0xf) + (Main.cpu.E & 0xf) + Main.cpu.getFlagC() > 0xf) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bit(sum);

		Main.cpu.A = res;
		Main.cpu.cycles += 4;
	}
	
//	ADC A, H
//	Z 0 H C
	public static void ADC_A_H() {
		int val = Main.cpu.H + Main.cpu.getFlagC();
		
		int sum = Main.cpu.A + val;
		int res = sum & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagN();
		if ((Main.cpu.A & 0xf) + (Main.cpu.H & 0xf) + Main.cpu.getFlagC() > 0xf) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bit(sum);

		Main.cpu.A = res;
		Main.cpu.cycles += 4;
	}
	
//	ADC A, L
//	Z 0 H C
	public static void ADC_A_L() {
		int val = Main.cpu.L+ Main.cpu.getFlagC();
		
		int sum = Main.cpu.A + val;
		int res = sum & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagN();
		if ((Main.cpu.A & 0xf) + (Main.cpu.L & 0xf) + Main.cpu.getFlagC() > 0xf) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bit(sum);

		Main.cpu.A = res;
		Main.cpu.cycles += 4;
	}
	
//	ADC A, (HL)
//	Z 0 H C
	public static void ADC_A_HL() {
		int val = Main.mmu.getByte(Main.cpu.getHL()) + Main.cpu.getFlagC();
		
		int sum = Main.cpu.A + val;
		int res = sum & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagN();
		if ((Main.cpu.A & 0xf) + (Main.mmu.getByte(Main.cpu.getHL()) & 0xf) + Main.cpu.getFlagC() > 0xf) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bit(sum);

		Main.cpu.A = res;
		Main.cpu.cycles += 8;
	}

//	ADC A, A
//	Z 0 H C
	public static void ADC_A_A() {
		int val = Main.cpu.A + Main.cpu.getFlagC();
		
		int sum = Main.cpu.A + val;
		int res = sum & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagN();
		if ((Main.cpu.A & 0xf) + (Main.cpu.A & 0xf) + Main.cpu.getFlagC() > 0xf) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bit(sum);

		Main.cpu.A = res;
		Main.cpu.cycles += 4;
	}
	
//	SUB B
//	Z 1 H C
	public static void SUB_A_B() {
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.A, Main.cpu.B);
		Main.cpu.checkCarry8bitSub(Main.cpu.A, Main.cpu.B);
		Main.cpu.A = (Main.cpu.A - Main.cpu.B) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.setFlagN();
		Main.cpu.cycles += 4;
	}
	
//	SUB C
//	Z 1 H C
	public static void SUB_A_C() {
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.A, Main.cpu.C);
		Main.cpu.checkCarry8bitSub(Main.cpu.A, Main.cpu.C);
		Main.cpu.A = (Main.cpu.A - Main.cpu.C) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.setFlagN();
		Main.cpu.cycles += 4;
	}
	
//	SUB D
//	Z 1 H C
	public static void SUB_A_D() {
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.A, Main.cpu.D);
		Main.cpu.checkCarry8bitSub(Main.cpu.A, Main.cpu.D);
		Main.cpu.A = (Main.cpu.A - Main.cpu.D) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.setFlagN();
		Main.cpu.cycles += 4;
	}
	
//	SUB E
//	Z 1 H C
	public static void SUB_A_E() {
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.A, Main.cpu.E);
		Main.cpu.checkCarry8bitSub(Main.cpu.A, Main.cpu.E);
		Main.cpu.A = (Main.cpu.A - Main.cpu.E) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.setFlagN();
		Main.cpu.cycles += 4;
	}
	
//	SUB H
//	Z 1 H C
	public static void SUB_A_H() {
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.A, Main.cpu.H);
		Main.cpu.checkCarry8bitSub(Main.cpu.A, Main.cpu.H);
		Main.cpu.A = (Main.cpu.A - Main.cpu.H) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.setFlagN();
		Main.cpu.cycles += 4;
	}
	
//	SUB L
//	Z 1 H C
	public static void SUB_A_L() {
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.A, Main.cpu.L);
		Main.cpu.checkCarry8bitSub(Main.cpu.A, Main.cpu.L);
		Main.cpu.A = (Main.cpu.A - Main.cpu.L) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.setFlagN();
		Main.cpu.cycles += 4;
	}
	
//	SUB (HL)
//	Z 1 H C
	public static void SUB_A_HL() {
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.A, Main.mmu.getByte(Main.cpu.getHL()));
		Main.cpu.checkCarry8bitSub(Main.cpu.A, Main.mmu.getByte(Main.cpu.getHL()));
		Main.cpu.A = (Main.cpu.A - Main.mmu.getByte(Main.cpu.getHL())) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.setFlagN();
		Main.cpu.cycles += 8;
	}
	
//	SUB A
//	1 1 0 0
	public static void SUB_A_A() {
		Main.cpu.A = (Main.cpu.A - Main.cpu.A) & 0xff;
		Main.cpu.setFlagZ();
		Main.cpu.setFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	SBC A, B
//	Z 1 H C
	public static void SBC_A_B() {
		int val = Main.cpu.B + Main.cpu.getFlagC();
		
		int res = (Main.cpu.A - val) & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.setFlagN();
		Main.cpu.resetFlagH();
		if ((Main.cpu.A & 0xf) < ((Main.cpu.B & 0xf) + Main.cpu.getFlagC())) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bitSub(Main.cpu.A, val);

		Main.cpu.A = res;
		Main.cpu.cycles += 4;
	}
	
//	SBC A, C
//	Z 1 H C
	public static void SBC_A_C() {
		int val = Main.cpu.C + Main.cpu.getFlagC();
		
		int res = (Main.cpu.A - val) & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.setFlagN();
		Main.cpu.resetFlagH();
		if ((Main.cpu.A & 0xf) < ((Main.cpu.C & 0xf) + Main.cpu.getFlagC())) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bitSub(Main.cpu.A, val);

		Main.cpu.A = res;
		Main.cpu.cycles += 4;
	}
	
//	SBC A, D
//	Z 1 H C
	public static void SBC_A_D() {
		int val = Main.cpu.D + Main.cpu.getFlagC();
		
		int res = (Main.cpu.A - val) & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.setFlagN();
		Main.cpu.resetFlagH();
		if ((Main.cpu.A & 0xf) < ((Main.cpu.D & 0xf) + Main.cpu.getFlagC())) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bitSub(Main.cpu.A, val);

		Main.cpu.A = res;
		Main.cpu.cycles += 4;
	}
	
//	SBC A, E
//	Z 1 H C
	public static void SBC_A_E() {
		int val = Main.cpu.E + Main.cpu.getFlagC();
		
		int res = (Main.cpu.A - val) & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.setFlagN();
		Main.cpu.resetFlagH();
		if ((Main.cpu.A & 0xf) < ((Main.cpu.E & 0xf) + Main.cpu.getFlagC())) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bitSub(Main.cpu.A, val);

		Main.cpu.A = res;
		Main.cpu.cycles += 4;
	}
	
//	SBC A, H
//	Z 1 H C
	public static void SBC_A_H() {
		int val = Main.cpu.H + Main.cpu.getFlagC();
		
		int res = (Main.cpu.A - val) & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.setFlagN();
		Main.cpu.resetFlagH();
		if ((Main.cpu.A & 0xf) < ((Main.cpu.H & 0xf) + Main.cpu.getFlagC())) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bitSub(Main.cpu.A, val);

		Main.cpu.A = res;
		Main.cpu.cycles += 4;
	}
	
//	SBC A, L
//	Z 1 H C
	public static void SBC_A_L() {
		int val = Main.cpu.L + Main.cpu.getFlagC();
		
		int res = (Main.cpu.A - val) & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.setFlagN();
		Main.cpu.resetFlagH();
		if ((Main.cpu.A & 0xf) < ((Main.cpu.L & 0xf) + Main.cpu.getFlagC())) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bitSub(Main.cpu.A, val);

		Main.cpu.A = res;
		Main.cpu.cycles += 4;
	}
	
//	SBC A, (HL)
//	Z 1 H C
	public static void SBC_A_HL() {
		int val = Main.mmu.getByte(Main.cpu.getHL()) + Main.cpu.getFlagC();
		
		int res = (Main.cpu.A - val) & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.setFlagN();
		Main.cpu.resetFlagH();
		if ((Main.cpu.A & 0xf) < ((Main.mmu.getByte(Main.cpu.getHL()) & 0xf) + Main.cpu.getFlagC())) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bitSub(Main.cpu.A, val);

		Main.cpu.A = res;
		Main.cpu.cycles += 8;
	}
	
//	SBC A, A
//	Z 1 H -
	public static void SBC_A_A() {
		int val = Main.cpu.A + Main.cpu.getFlagC();
		
		int res = (Main.cpu.A - val) & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.setFlagN();
		Main.cpu.resetFlagH();
		if ((Main.cpu.A & 0xf) < ((Main.cpu.A & 0xf) + Main.cpu.getFlagC())) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bitSub(Main.cpu.A, val);

		Main.cpu.A = res;
		Main.cpu.cycles += 4;
	}	
	
//	 AND B
//	 Z 0 1 0
	public static void AND_B() {
		Main.cpu.A = Main.cpu.A & Main.cpu.B;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}	
	
//	 AND C
//	 Z 0 1 0
	public static void AND_C() {
		Main.cpu.A = Main.cpu.A & Main.cpu.C;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	 AND D
//	 Z 0 1 0
	public static void AND_D() {
		Main.cpu.A = Main.cpu.A & Main.cpu.D;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	AND E
//	Z 0 1 0
	public static void AND_E() {
		Main.cpu.A = Main.cpu.A & Main.cpu.E;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	AND H
//	Z 0 1 0
	public static void AND_H() {
		Main.cpu.A = Main.cpu.A & Main.cpu.H;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	AND L
//	Z 0 1 0
	public static void AND_L() {
		Main.cpu.A = Main.cpu.A & Main.cpu.L;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	AND (HL)
//	Z 0 1 0
	public static void AND_HL() {
		Main.cpu.A = Main.cpu.A & Main.mmu.getByte(Main.cpu.getHL());
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 8;
	}
	
//	AND A
//	Z 0 1 0
	public static void AND_A() {
		Main.cpu.A = Main.cpu.A & Main.cpu.A;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	XOR B
//	Z 0 0 0
	public static void XOR_B() {
		Main.cpu.A = Main.cpu.A ^ Main.cpu.B;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	XOR C
//	Z 0 0 0
	public static void XOR_C() {
		Main.cpu.A = Main.cpu.A ^ Main.cpu.C;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}

//	XOR D
//	Z 0 0 0
	public static void XOR_D() {
		Main.cpu.A = Main.cpu.A ^ Main.cpu.D;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	XOR E
//	Z 0 0 0
	public static void XOR_E() {
		Main.cpu.A = Main.cpu.A ^ Main.cpu.E;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	XOR H
//	Z 0 0 0
	public static void XOR_H() {
		Main.cpu.A = Main.cpu.A ^ Main.cpu.H;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	XOR L
//	Z 0 0 0
	public static void XOR_L() {
		Main.cpu.A = Main.cpu.A ^ Main.cpu.L;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	XOR (HL)
//	Z 0 0 0
	public static void XOR_HL() {
		Main.cpu.A = Main.cpu.A ^ Main.mmu.getByte(Main.cpu.getHL());
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 8;
	}
	
//	XOR A
//	1 0 0 0
	public static void XOR_A() {
		Main.cpu.A = Main.cpu.A ^ Main.cpu.A;
		Main.cpu.setFlagZ();
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	OR B
//	Z 0 0 0
	public static void OR_B() {
		Main.cpu.A = Main.cpu.A | Main.cpu.B;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	OR C
//	Z 0 0 0
	public static void OR_C() {
		Main.cpu.A = Main.cpu.A | Main.cpu.C;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	OR D
//	Z 0 0 0
	public static void OR_D() {
		Main.cpu.A = Main.cpu.A | Main.cpu.D;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	OR E
//	Z 0 0 0
	public static void OR_E() {
		Main.cpu.A = Main.cpu.A | Main.cpu.E;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	OR H
//	Z 0 0 0
	public static void OR_H() {
		Main.cpu.A = Main.cpu.A | Main.cpu.H;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	OR L
//	Z 0 0 0
	public static void OR_L() {
		Main.cpu.A = Main.cpu.A | Main.cpu.L;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	OR (HL)
//	Z 0 0 0
	public static void OR_HL() {
		Main.cpu.A = Main.cpu.A | Main.mmu.getByte(Main.cpu.getHL());
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 8;
	}
	
//	OR A
//	Z 0 0 0
	public static void OR_A() {
		Main.cpu.A = Main.cpu.A | Main.cpu.A;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}

//	CP B
//	Z 1 H C
	public static void CP_B() {
		Main.cpu.checkZero8bit(Main.cpu.A - Main.cpu.B);
		Main.cpu.setFlagN();
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.A, Main.cpu.B);
		Main.cpu.checkCarry8bitSub(Main.cpu.A, Main.cpu.B);
		Main.cpu.cycles += 4;
	}

//	CP C
//	Z 1 H C
	public static void CP_C() {
		Main.cpu.checkZero8bit(Main.cpu.A - Main.cpu.C);
		Main.cpu.setFlagN();
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.A, Main.cpu.C);
		Main.cpu.checkCarry8bitSub(Main.cpu.A, Main.cpu.C);
		Main.cpu.cycles += 4;
	}
	
//	CP D
//	Z 1 H C
	public static void CP_D() {
		Main.cpu.checkZero8bit(Main.cpu.A - Main.cpu.D);
		Main.cpu.setFlagN();
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.A, Main.cpu.D);
		Main.cpu.checkCarry8bitSub(Main.cpu.A, Main.cpu.D);
		Main.cpu.cycles += 4;
	}
	
//	CP E
//	Z 1 H C
	public static void CP_E() {
		Main.cpu.checkZero8bit(Main.cpu.A - Main.cpu.E);
		Main.cpu.setFlagN();
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.A, Main.cpu.E);
		Main.cpu.checkCarry8bitSub(Main.cpu.A, Main.cpu.E);
		Main.cpu.cycles += 4;
	}
	
//	CP H
//	Z 1 H C
	public static void CP_H() {
		Main.cpu.checkZero8bit(Main.cpu.A - Main.cpu.H);
		Main.cpu.setFlagN();
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.A, Main.cpu.H);
		Main.cpu.checkCarry8bitSub(Main.cpu.A, Main.cpu.H);
		Main.cpu.cycles += 4;
	}
	
//	CP L
//	Z 1 H C
	public static void CP_L() {
		Main.cpu.checkZero8bit(Main.cpu.A - Main.cpu.L);
		Main.cpu.setFlagN();
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.A, Main.cpu.L);
		Main.cpu.checkCarry8bitSub(Main.cpu.A, Main.cpu.L);
		Main.cpu.cycles += 4;
	}
	
//	CP (HL)
//	Z 1 H C
	public static void CP_HL() {
		Main.cpu.checkZero8bit(Main.cpu.A - Main.mmu.getByte(Main.cpu.getHL()));
		Main.cpu.setFlagN();
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.A, Main.mmu.getByte(Main.cpu.getHL()));
		Main.cpu.checkCarry8bitSub(Main.cpu.A, Main.mmu.getByte(Main.cpu.getHL()));
		Main.cpu.cycles += 8;
	}
	
//	CP A
//	1 1 0 0
	public static void CP_A() {
		Main.cpu.setFlagZ();
		Main.cpu.setFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 4;
	}
	
//	RET NZ
//	1  20/8
//	- - - -
	public static void RET_NZ() {
		if (Main.cpu.getFlagZ() == 0) {
			RET();
			Main.cpu.cycles += 4;
		} else {
			Main.cpu.cycles += 8;
		}
	}
	
//	POP BC
//	1  12
//	- - - -
	public static void POP_BC() {
		Main.cpu.C = Main.mmu.getByte(Main.cpu.SP);
		Main.cpu.SP += 1;
		Main.cpu.B = Main.mmu.getByte(Main.cpu.SP);
		Main.cpu.SP += 1;
		Main.cpu.cycles += 12;
	}
	
//	JP NZ, a16
//	3  16/12
//	- - - -
	public static void JP_NZ_a16() {
		if (Main.cpu.getFlagZ() == 0) {
			JP_a16();
		} else {
			Main.cpu.fetch();
			Main.cpu.fetch();
			Main.cpu.cycles += 12;
		}
	}

//	JP a16
//	3  16
//	- - - -
	public static void JP_a16() {
		int l = Main.cpu.fetch();
		int h = Main.cpu.fetch();
		int res = (h << 8) + l;
		Main.cpu.PC = res & 0xffff;
		Main.cpu.cycles += 16;
	}
	
//	CALL NZ, a16
//	3  24/12
//	- - - -
	public static void CALL_NZ_a16( ) {
		if (Main.cpu.getFlagZ() == 0) {
			CALL_a16();
		} else {
			Main.cpu.fetch();
			Main.cpu.fetch();
			Main.cpu.cycles += 12;
		}
	}
	
//	PUSH BC
//	1  16
//	- - - -
	public static void PUSH_BC() {
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, Main.cpu.B);
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, Main.cpu.C);
		Main.cpu.cycles += 16;
	}

//	ADD A, d8
//	2  8
//	Z 0 H C
	public static void ADD_A_d8() {
		int d8 = Main.cpu.fetch();
		Main.cpu.checkHalfCarry8bit(Main.cpu.A, d8);
		Main.cpu.checkCarry8bit(Main.cpu.A + d8);
		Main.cpu.A = (Main.cpu.A + d8) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.cycles += 8;
	}
	
//	RST 00H
//	1  16
//	- - - -
	public static void RST_0() {
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, (Main.cpu.PC & 0xff00) >> 8); // High byte of PC
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, Main.cpu.PC & 0xff); // Low byte of PC
//		Main.cpu.PC = Main.mmu.getByte(0x00);
		Main.cpu.PC = 0x0000;
		Main.cpu.cycles += 16;
	}
	
//	RET Z
//	1  20/8
//	- - - -
	public static void RET_Z() {
		if (Main.cpu.getFlagZ() == 1) {
			RET();
			Main.cpu.cycles += 4;
		} else {
			Main.cpu.cycles += 8;
		}
	}

//	RET
//	1  16
//	- - - -
	public static void RET() {
		int l = Main.cpu.fetchSP();
		int h = Main.cpu.fetchSP();
		int res = (h << 8) + l;
		Main.cpu.PC = res & 0xffff;
		Main.cpu.cycles += 16;
	}

//	JP Z, a16
//	3  16/12
//	- - - -
	public static void JP_Z_a16() {
		if (Main.cpu.getFlagZ() == 1) {
			JP_a16();
		} else {
			Main.cpu.fetch();
			Main.cpu.fetch();
			Main.cpu.cycles += 12;
		}
	}
	
//	CALL Z, a16
//	3  24/12
//	- - - -
	public static void CALL_Z_a16( ) {
		if (Main.cpu.getFlagZ() == 1) {
			CALL_a16();
		} else {
			Main.cpu.fetch();
			Main.cpu.fetch();
			Main.cpu.cycles += 12;
		}
	}
	
//	CALL a16
//	3  24
//	- - - -
	public static void CALL_a16() {
		int l = Main.cpu.fetch();
		int h = Main.cpu.fetch();
		int res = (h << 8) + l;
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, (Main.cpu.PC & 0xff00) >> 8); // High byte of PC
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, Main.cpu.PC & 0xff); // Low byte of PC
		Main.cpu.PC = res & 0xffff;
		Main.cpu.cycles += 24;
	}
	
//	ADC A, d8
//	2  8
//	Z 0 H C
	public static void ADC_A_d8() {
		int d8 = Main.cpu.fetch();
		int val = d8 + Main.cpu.getFlagC();
		
		int sum = Main.cpu.A + val;
		int res = sum & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagN();
		if ((Main.cpu.A & 0xf) + (d8 & 0xf) + Main.cpu.getFlagC() > 0xf) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bit(sum);

		Main.cpu.A = res;
		Main.cpu.cycles += 8;
	}
	
//	RST 08H
//	1  16
//	- - - -
	public static void RST_1() {
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, (Main.cpu.PC & 0xff00) >> 8); // High byte of PC
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, Main.cpu.PC & 0xff); // Low byte of PC
//		Main.cpu.PC = Main.mmu.getByte(0x08);
		Main.cpu.PC = 0x0008;
		Main.cpu.cycles += 16;
	}

//	RET NC
//	1  20/8
//	- - - -
	public static void RET_NC() {
		if (Main.cpu.getFlagC() == 0) {
			RET();
			Main.cpu.cycles += 4;
		} else {
			Main.cpu.cycles += 8;
		}
	}

//	POP DE
//	1  12
//	- - - -
	public static void POP_DE() {
		Main.cpu.E = Main.mmu.getByte(Main.cpu.SP);
		Main.cpu.SP += 1;
		Main.cpu.D = Main.mmu.getByte(Main.cpu.SP);
		Main.cpu.SP += 1;
		Main.cpu.cycles += 12;
	}

//	JP NC, a16
//	3  16/12
//	- - - -
	public static void JP_NC_a16() {
		if (Main.cpu.getFlagC() == 0) {
			JP_a16();
		} else {
			Main.cpu.fetch();
			Main.cpu.fetch();
			Main.cpu.cycles += 12;
		}
	}

//	CALL NC, a16
//	3  24/12
//	- - - -
	public static void CALL_NC_a16( ) {
		if (Main.cpu.getFlagC() == 0) {
			CALL_a16();
		} else {
			Main.cpu.fetch();
			Main.cpu.fetch();
			Main.cpu.cycles += 12;
		}
	}

//	PUSH DE
//	1  16
//	- - - -
	public static void PUSH_DE() {
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, Main.cpu.D);
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, Main.cpu.E);
		Main.cpu.cycles += 16;
	}

//	SUB d8
//	2  8
//	Z 1 H C
	public static void SUB_A_d8() {
		int d8 = Main.cpu.fetch();
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.A, d8);
		Main.cpu.checkCarry8bitSub(Main.cpu.A, d8);
		Main.cpu.A = (Main.cpu.A - d8) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.setFlagN();
		Main.cpu.cycles += 8;
	}

//	RST 10H
//	1  16
//	- - - -
	public static void RST_2() {
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, (Main.cpu.PC & 0xff00) >> 8); // High byte of PC
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, Main.cpu.PC & 0xff); // Low byte of PC
		Main.cpu.PC = Main.mmu.getByte(0x10);
		Main.cpu.PC = 0x0010;
		Main.cpu.cycles += 16;
	}

//	RET C
//	1  20/8
//	- - - -
	public static void RET_C() {
		if (Main.cpu.getFlagC() == 1) {
			RET();
			Main.cpu.cycles += 4;
		} else  {
			Main.cpu.cycles += 8;
		}
	}

//	RETI
//	1  16
//	- - - -
	public static void RETI() {
		int l = Main.cpu.fetchSP();
		int h = Main.cpu.fetchSP();
		int res = (h << 8) + l;
		Main.cpu.PC = res & 0xffff;
		Main.cpu.cycles += 16;
	}

//	JP C, a16
//	3  16/12
//	- - - -
	public static void JP_C_a16() {
		if (Main.cpu.getFlagC() == 1) {
			JP_a16();
		} else {
			Main.cpu.fetch();
			Main.cpu.fetch();
			Main.cpu.cycles += 12;
		}
	}

//	CALL C, a16
//	3  24/12
//	- - - -
	public static void CALL_C_a16( ) {
		if (Main.cpu.getFlagC() == 1) {
			CALL_a16();
		} else {
			Main.cpu.fetch();
			Main.cpu.fetch();
			Main.cpu.cycles += 12;
		}
	}

//	SBC A, d8
//	2  8
//	Z 1 H C
	public static void SBC_A_d8() {
		int d8 = Main.cpu.fetch();
		int val = d8 + Main.cpu.getFlagC();
		
		int res = (Main.cpu.A - val) & 0xff;
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.setFlagN();
		Main.cpu.resetFlagH();
		if ((Main.cpu.A & 0xf) < ((d8 & 0xf) + Main.cpu.getFlagC())) {
			Main.cpu.setFlagH();
		}
		Main.cpu.checkCarry8bitSub(Main.cpu.A, val);

		Main.cpu.A = res;
		Main.cpu.cycles += 8;
	}	

//	RST 18H
//	1  16
//	- - - -
	public static void RST_3() {
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, (Main.cpu.PC & 0xff00) >> 8); // High byte of PC
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, Main.cpu.PC & 0xff); // Low byte of PC
//		Main.cpu.PC = Main.mmu.getByte(0x18);
		Main.cpu.PC = 0x0018;
		Main.cpu.cycles += 16;
	}
	
	
//	LDH (a8), A
//	2  12
//	- - - -
	public static void LD_a8_A() {
		// Debe guardar el contenido del reg A en 0xFF(a8)
		Main.mmu.setByte(0xFF00 | Main.cpu.fetch(), Main.cpu.A);
		Main.cpu.cycles += 12;
	}

//	POP HL
//	1  12
//	- - - -
	public static void POP_HL() {
		Main.cpu.L = Main.mmu.getByte(Main.cpu.SP);
		Main.cpu.SP += 1;
		Main.cpu.H = Main.mmu.getByte(Main.cpu.SP);
		Main.cpu.SP += 1;
		Main.cpu.cycles += 12;
	}
	
//	LD (C), A
//	1  8
//	- - - -
	public static void LD_Cmem_A() {
		Main.mmu.setByte(0xff00 | Main.cpu.C, Main.cpu.A);
		Main.cpu.cycles += 8;
	}

//	PUSH HL
//	1  16
//	- - - -
	public static void PUSH_HL() {
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, Main.cpu.H);
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, Main.cpu.L);
		Main.cpu.cycles += 16;
	}
	
//	AND d8
//	2  8
//	Z 0 1 0
	public static void AND_d8() {
		Main.cpu.A = Main.cpu.A & Main.cpu.fetch();
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.cycles += 8;
	}

//	RST 20H
//	1  16
//	- - - -
	public static void RST_4() {
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, (Main.cpu.PC & 0xff00) >> 8); // High byte of PC
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, Main.cpu.PC & 0xff); // Low byte of PC
//		Main.cpu.PC = Main.mmu.getByte(0x20);
		Main.cpu.PC = 0x0020;
		Main.cpu.cycles += 16;
	}
	
//	ADD SP, r8
//	2  16
//	0 0 H C
	public static void ADD_SP_s8() {
		Main.cpu.resetFlagZ();
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagC();
		
		int s8 = Main.cpu.fetchSigned();
		int sum = Main.cpu.SP + s8;
		
		Main.cpu.checkHalfCarry8bit(Main.cpu.SP, s8);

		if ((sum & 0xff) < (Main.cpu.SP & 0xff)) {
			Main.cpu.setFlagC();
		}
		
		Main.cpu.SP = sum & 0xffff;
		Main.cpu.cycles += 16;
	}
	
//	JP HL
//	1  4
//	- - - -
	public static void JP_HL() {
		int res = (Main.cpu.H << 8) + Main.cpu.L;
		Main.cpu.PC = res & 0xffff;
		Main.cpu.cycles += 4;
	}
	
//	LD (a16), A
//	3  16
//	- - - -
	public static void LD_a16_A() {
		int index = (Main.cpu.fetch() + (Main.cpu.fetch() << 8)) & 0xffff;
		Main.mmu.setByte(index, Main.cpu.A);
		Main.cpu.cycles += 16;
	}
	
//	XOR d8
//	2  8
//	Z 0 0 0
	public static void XOR_d8() {
		Main.cpu.A = Main.cpu.A ^ Main.cpu.fetch();
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.cycles += 8;
	}

//	RST 28H
//	1  16
//	- - - -
	public static void RST_5() {
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, (Main.cpu.PC & 0xff00) >> 8); // High byte of PC
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, Main.cpu.PC & 0xff); // Low byte of PC
//		Main.cpu.PC = Main.mmu.getByte(0x28);
		Main.cpu.PC = 0x0028;
		Main.cpu.cycles += 16;
	}

//	LDH A, (a8)
//	2  12
//	- - - -
	public static void LD_A_a8() {
		Main.cpu.A = Main.mmu.getByte(0xff00 | Main.cpu.fetch());
		Main.cpu.cycles += 12;
	}

//	POP AF
//	1  12
//	Z N H C
	public static void POP_AF() {
		Main.cpu.F = Main.mmu.getByte(Main.cpu.SP) & 0xf0;
		Main.cpu.SP += 1;
		Main.cpu.A = Main.mmu.getByte(Main.cpu.SP);
		Main.cpu.SP += 1;
		Main.cpu.cycles += 12;
	}

//	LD A, (C)
//	1  8
//	- - - -
	public static void LD_A_Cmem() {
		Main.cpu.A = Main.mmu.getByte(0xff00 | Main.cpu.C);
		Main.cpu.cycles += 8;
	}

//	DI
//	1  4
//	- - - -
	public static void DI() {
		Main.cpu.IME = false;
		Main.cpu.cycles += 4;
	}

//	PUSH AF
//	1  16
//	- - - -
	public static void PUSH_AF() {
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, Main.cpu.A);
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, Main.cpu.F);
		Main.cpu.cycles += 16;
	}

//	OR d8
//	2  8
//	Z 0 0 0
	public static void OR_d8() {
		Main.cpu.A = Main.cpu.A | Main.cpu.fetch();
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.cycles += 8;
	}

//	RST 30H
//	1  16
//	- - - -
	public static void RST_6() {
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, (Main.cpu.PC & 0xff00) >> 8); // High byte of PC
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, Main.cpu.PC & 0xff); // Low byte of PC
//		Main.cpu.PC = Main.mmu.getByte(0x30);
		Main.cpu.PC = 0x0030;
		Main.cpu.cycles += 16;
	}
	
//	LD HL, SP + r8
//	2  12
//	0 0 H C
	public static void LD_HL_SPplusS8() {
		Main.cpu.resetFlagZ();
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagC();
		
		int s8 = Main.cpu.fetchSigned();
		int sum = Main.cpu.SP + s8;
		
		Main.cpu.checkHalfCarry8bit(Main.cpu.SP, s8);
		if ((sum & 0xff) < (Main.cpu.SP & 0xff)) {
			Main.cpu.setFlagC();
		}
		
		Main.cpu.L = sum & 0xff;
		Main.cpu.H = (sum >> 8) & 0xff;
		Main.cpu.cycles += 12;
	}

//	LD SP, HL
//	1  8
//	- - - -
	public static void LD_SP_HL() {
		int value = (Main.cpu.H << 8) + Main.cpu.L;
		Main.cpu.SP = value;
		Main.cpu.cycles += 8;
	}

//	LD A, (a16)
//	3  16
//	- - - -
	public static void LD_A_a16() {
		int index = (Main.cpu.fetch() + (Main.cpu.fetch() << 8)) & 0xffff;
		Main.cpu.A = Main.mmu.getByte(index);
		Main.cpu.cycles += 16;
	}

//	EI
//	1  4
//	- - - -
	public static void EI() {
		Main.cpu.IME = true;
		Main.cpu.cycles += 4;
	}
	
//	CP d8
//	2  8
//	Z 1 H C
	public static void CP_d8() {
		int d8 = Main.cpu.fetch();
		Main.cpu.checkZero8bit((Main.cpu.A - d8) & 0xff);
		Main.cpu.setFlagN();
		Main.cpu.checkHalfCarry8bitSub(Main.cpu.A, d8);
		Main.cpu.checkCarry8bitSub(Main.cpu.A, d8);
		Main.cpu.cycles += 8;
	}

//	RST 38H
//	1  16
//	- - - -
	public static void RST_7() {
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, (Main.cpu.PC & 0xff00) >> 8); // High byte of PC
		Main.cpu.SP -= 1;
		Main.mmu.setByte(Main.cpu.SP, Main.cpu.PC & 0xff); // Low byte of PC
//		Main.cpu.PC = Main.mmu.getByte(0x38);
		Main.cpu.PC = 0x0038;
		Main.cpu.cycles += 16;
	}

// 	###############################################	
//	16-bit opcodes, where the first 8 bits are 0xCB
// 	###############################################	
	
//	RLC B
//	2  8
//	Z 0 0 C
	public static void RLC_B() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.B & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.B = ((Main.cpu.B << 1) | (Main.cpu.B >> 7)) & 0xff; // Rotate to the left
		Main.cpu.checkZero8bit(Main.cpu.B);
		Main.cpu.cycles += 8;
	}
	
//	RLC C
//	2  8
//	Z 0 0 C
	public static void RLC_C() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.C & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.C = ((Main.cpu.C << 1) | (Main.cpu.C >> 7)) & 0xff; // Rotate to the left
		Main.cpu.checkZero8bit(Main.cpu.C);
		Main.cpu.cycles += 8;
	}
	
//	RLC D
//	2  8
//	Z 0 0 C
	public static void RLC_D() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.D & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.D = ((Main.cpu.D << 1) | (Main.cpu.D >> 7)) & 0xff; // Rotate to the left
		Main.cpu.checkZero8bit(Main.cpu.D);
		Main.cpu.cycles += 8;
	}
	
//	RLC E
//	2  8
//	Z 0 0 C
	public static void RLC_E() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.E & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.E = ((Main.cpu.E << 1) | (Main.cpu.E >> 7)) & 0xff; // Rotate to the left
		Main.cpu.checkZero8bit(Main.cpu.E);
		Main.cpu.cycles += 8;
	}
	
//	RLC H
//	2  8
//	Z 0 0 C
	public static void RLC_H() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.H & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.H = ((Main.cpu.H << 1) | (Main.cpu.H >> 7)) & 0xff; // Rotate to the left
		Main.cpu.checkZero8bit(Main.cpu.H);
		Main.cpu.cycles += 8;
	}
	
//	RLC L
//	2  8
//	Z 0 0 C
	public static void RLC_L() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.L & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.L = ((Main.cpu.L << 1) | (Main.cpu.L >> 7)) & 0xff; // Rotate to the left
		Main.cpu.checkZero8bit(Main.cpu.L);
		Main.cpu.cycles += 8;
	}
	
//	RLC (HL)
//	2  16
//	Z 0 0 C
	public static void RLC_HL() {
		Main.cpu.resetFlagZ();
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.mmu.getByte(Main.cpu.getHL()) & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		int res = ((Main.mmu.getByte(Main.cpu.getHL()) << 1) | (Main.mmu.getByte(Main.cpu.getHL()) >> 7)) & 0xff; // Rotate to the left
		Main.cpu.checkZero8bit(res);
		Main.mmu.setByte(Main.cpu.getHL(), res);
		Main.cpu.cycles += 16;
	}
	
//	RLC A
//	2  8
//	Z 0 0 C
	public static void RLC_A() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.A & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.A = ((Main.cpu.A << 1) | (Main.cpu.A >> 7)) & 0xff; // Rotate to the left
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.cycles += 8;
	}
	
//	RRC B
//	2  8
//	Z 0 0 C
	public static void RRC_B() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();

		if ((Main.cpu.B & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.B = ((Main.cpu.B << 7) | (Main.cpu.B >> 1)) & 0xff; // Rotate to the right
		Main.cpu.checkZero8bit(Main.cpu.B);
		Main.cpu.cycles += 8;
	}
	
//	RRC C
//	2  8
//	Z 0 0 C
	public static void RRC_C() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();

		if ((Main.cpu.C & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.C = ((Main.cpu.C << 7) | (Main.cpu.C >> 1)) & 0xff; // Rotate to right
		Main.cpu.checkZero8bit(Main.cpu.C);
		Main.cpu.cycles += 8;
	}
	
//	RRC D
//	2  8
//	Z 0 0 C
	public static void RRC_D() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();

		if ((Main.cpu.D & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.D = ((Main.cpu.D << 7) | (Main.cpu.D >> 1)) & 0xff; // Rotate to right
		Main.cpu.checkZero8bit(Main.cpu.D);
		Main.cpu.cycles += 8;
	}
	
//	RRC E
//	2  8
//	Z 0 0 C
	public static void RRC_E() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();

		if ((Main.cpu.E & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.E = ((Main.cpu.E << 7) | (Main.cpu.E >> 1)) & 0xff; // Rotate to right
		Main.cpu.checkZero8bit(Main.cpu.E);
		Main.cpu.cycles += 8;
	}
	
//	RRC H
//	2  8
//	Z 0 0 C
	public static void RRC_H() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();

		if ((Main.cpu.H & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.H = ((Main.cpu.H << 7) | (Main.cpu.H >> 1)) & 0xff; // Rotate to right
		Main.cpu.checkZero8bit(Main.cpu.H);
		Main.cpu.cycles += 8;
	}
	
//	RRC L
//	2  8
//	Z 0 0 C
	public static void RRC_L() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();

		if ((Main.cpu.L & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.L = ((Main.cpu.L << 7) | (Main.cpu.L >> 1)) & 0xff; // Rotate to right
		Main.cpu.checkZero8bit(Main.cpu.L);
		Main.cpu.cycles += 8;
	}
	
//	RRC (HL)
//	2  16
//	Z 0 0 C
	public static void RRC_HL() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
		if ((Main.mmu.getByte(Main.cpu.getHL()) & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.mmu.setByte(Main.cpu.getHL(), ((Main.mmu.getByte(Main.cpu.getHL()) << 7) | (Main.mmu.getByte(Main.cpu.getHL()) >> 1)) & 0xff); // Rotate to right
		Main.cpu.checkZero8bit(Main.mmu.getByte(Main.cpu.getHL()));
		Main.cpu.cycles += 16;
	}
	
//	RRC A
//	2  8
//	Z 0 0 C
	public static void RRC_A() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();

		if ((Main.cpu.A & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.A = ((Main.cpu.A << 7) | (Main.cpu.A >> 1)) & 0xff; // Rotate to right
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.cycles += 8;
	}
	
//	RL B
//	2  8
//	Z 0 0 C
	public static void RL_B() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = Main.cpu.getFlagC();
		
		// Set carry flag
		if ((Main.cpu.B & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.B = (Main.cpu.B << 1) & 0xff; // Rotate to the right
		Main.cpu.B |= c;
		Main.cpu.checkZero8bit(Main.cpu.B);
		Main.cpu.cycles += 8;
	}
	
//	RL C
//	2  8
//	Z 0 0 C
	public static void RL_C() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = Main.cpu.getFlagC();
		
		// Set carry flag
		if ((Main.cpu.C & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.C = (Main.cpu.C << 1) & 0xff; // Rotate to the right
		Main.cpu.C |= c;
		Main.cpu.checkZero8bit(Main.cpu.C);
		Main.cpu.cycles += 8;
	}
	
//	RL D
//	2  8
//	Z 0 0 C
	public static void RL_D() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = Main.cpu.getFlagC();

		// Set carry flag
		if ((Main.cpu.D & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.D = (Main.cpu.D << 1) & 0xff; // Rotate to the right
		Main.cpu.D |= c;
		Main.cpu.checkZero8bit(Main.cpu.D);
		Main.cpu.cycles += 8;
	}
	
//	RL E
//	2  8
//	Z 0 0 C
	public static void RL_E() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = Main.cpu.getFlagC();

		// Set carry flag
		if ((Main.cpu.E & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.E = (Main.cpu.E << 1) & 0xff; // Rotate to the right
		Main.cpu.E |= c;
		Main.cpu.checkZero8bit(Main.cpu.E);
		Main.cpu.cycles += 8;
	}
	
//	RL H
//	2  8
//	Z 0 0 C
	public static void RL_H() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = Main.cpu.getFlagC();

		// Set carry flag
		if ((Main.cpu.H & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.H = (Main.cpu.H << 1) & 0xff; // Rotate to the right
		Main.cpu.H |= c;
		Main.cpu.checkZero8bit(Main.cpu.H);
		Main.cpu.cycles += 8;
	}
	
//	RL L
//	2  8
//	Z 0 0 C
	public static void RL_L() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = Main.cpu.getFlagC();
		
		// Set carry flag
		if ((Main.cpu.L & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.L = (Main.cpu.L << 1) & 0xff; // Rotate to the right
		Main.cpu.L |= c;
		Main.cpu.checkZero8bit(Main.cpu.L);
		Main.cpu.cycles += 8;
	}
	
//	RL (HL)
//	2  16
//	Z 0 0 C
	public static void RL_HL() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = Main.cpu.getFlagC();

		// Set carry flag
		if ((Main.mmu.getByte(Main.cpu.getHL()) & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.mmu.setByte(Main.cpu.getHL(), (Main.mmu.getByte(Main.cpu.getHL()) << 1) & 0xff);
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) | c);
		Main.cpu.checkZero8bit(Main.mmu.getByte(Main.cpu.getHL()));
		Main.cpu.cycles += 16;
	}
	
//	RL A
//	2  8
//	Z 0 0 C
	public static void RL_A() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = Main.cpu.getFlagC();

		// Set carry flag
		if ((Main.cpu.A & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.A = (Main.cpu.A << 1) & 0xff; // Rotate to the right
		Main.cpu.A |= c;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.cycles += 8;
	}
	
//	RR B
//	2  8
//	Z 0 0 C
	public static void RR_B() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = (Main.cpu.getFlagC() << 7);

		// Set carry flag
		if ((Main.cpu.B & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.B = (Main.cpu.B >> 1) & 0xff; // Rotate to the right
		Main.cpu.B |= c;
		Main.cpu.checkZero8bit(Main.cpu.B);
		Main.cpu.cycles += 8;
	}
	
//	RR C
//	2  8
//	Z 0 0 C
	public static void RR_C() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = (Main.cpu.getFlagC() << 7);

		// Set carry flag
		if ((Main.cpu.C & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.C = (Main.cpu.C >> 1) & 0xff; // Rotate to the right
		Main.cpu.C |= c;
		Main.cpu.checkZero8bit(Main.cpu.C);
		Main.cpu.cycles += 8;
	}
		
//	RR D
//	2  8
//	Z 0 0 C
	public static void RR_D() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = (Main.cpu.getFlagC() << 7);

		// Set carry flag
		if ((Main.cpu.D & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.D = (Main.cpu.D >> 1) & 0xff; // Rotate to the right
		Main.cpu.D |= c;
		Main.cpu.checkZero8bit(Main.cpu.D);
		Main.cpu.cycles += 8;
	}
	
//	RR E
//	2  8
//	Z 0 0 C
	public static void RR_E() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = (Main.cpu.getFlagC() << 7);

		// Set carry flag
		if ((Main.cpu.E & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.E = (Main.cpu.E >> 1) & 0xff; // Rotate to the right
		Main.cpu.E |= c;
		Main.cpu.checkZero8bit(Main.cpu.E);
		Main.cpu.cycles += 8;
	}
	
//	RR H
//	2  8
//	Z 0 0 C
	public static void RR_H() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = (Main.cpu.getFlagC() << 7);

		// Set carry flag
		if ((Main.cpu.H & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.H = (Main.cpu.H >> 1) & 0xff; // Rotate to the right
		Main.cpu.H |= c;
		Main.cpu.checkZero8bit(Main.cpu.H);
		Main.cpu.cycles += 8;
	}
	
//	RR L
//	2  8
//	Z 0 0 C
	public static void RR_L() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = (Main.cpu.getFlagC() << 7);

		// Set carry flag
		if ((Main.cpu.L & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.L = (Main.cpu.L >> 1) & 0xff; // Rotate to the right
		Main.cpu.L |= c;
		Main.cpu.checkZero8bit(Main.cpu.L);
		Main.cpu.cycles += 8;
	}
	
//	RR (HL)
//	2  16
//	Z 0 0 C
	public static void RR_HL() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = (Main.cpu.getFlagC() << 7);

		// Set carry flag
		if ((Main.mmu.getByte(Main.cpu.getHL()) & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.mmu.setByte(Main.cpu.getHL(), (Main.mmu.getByte(Main.cpu.getHL()) >> 1) & 0xff);
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) | c);
		Main.cpu.checkZero8bit(Main.mmu.getByte(Main.cpu.getHL()));
		Main.cpu.cycles += 16;
	}
	
//	RR A
//	2  8
//	Z 0 0 C
	public static void RR_A() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
	
		int c = (Main.cpu.getFlagC() << 7);

		// Set carry flag
		if ((Main.cpu.A & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.A = (Main.cpu.A >> 1) & 0xff; // Rotate to the right
		Main.cpu.A |= c;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.cycles += 8;
	}
	
//	SLA B
//	2  8
//	Z 0 0 C
	public static void SLA_B() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
		if ((Main.cpu.B & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.B = (Main.cpu.B << 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.B);
		Main.cpu.cycles += 8;
	}
	
//	SLA C
//	2  8
//	Z 0 0 C
	public static void SLA_C() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
		if ((Main.cpu.C & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.C = (Main.cpu.C << 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.C);
		Main.cpu.cycles += 8;
	}
	
//	SLA D
//	2  8
//	Z 0 0 C
	public static void SLA_D() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
		if ((Main.cpu.D & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.D = (Main.cpu.D << 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.D);
		Main.cpu.cycles += 8;
	}
	
//	SLA E
//	2  8
//	Z 0 0 C
	public static void SLA_E() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
		if ((Main.cpu.E & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.E = (Main.cpu.E << 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.E);
		Main.cpu.cycles += 8;
	}
	
//	SLA H
//	2  8
//	Z 0 0 C
	public static void SLA_H() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
		if ((Main.cpu.H & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.H = (Main.cpu.H << 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.H);
		Main.cpu.cycles += 8;
	}
	
//	SLA L
//	2  8
//	Z 0 0 C
	public static void SLA_L() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
		if ((Main.cpu.L & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.L = (Main.cpu.L << 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.L);
		Main.cpu.cycles += 8;
	}
	
//	SLA (HL)
//	2  16
//	Z 0 0 C
	public static void SLA_HL() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
		int res = Main.mmu.getByte(Main.cpu.getHL());
		
		if ((res & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		res = (res << 1) & 0xff;
		Main.cpu.checkZero8bit(res);
		Main.mmu.setByte(Main.cpu.getHL(), res);
		Main.cpu.cycles += 16;
	}
	
//	SLA A
//	2  8
//	Z 0 0 C
	public static void SLA_A() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		
		if ((Main.cpu.A & 0x80) == 0x80) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.A = (Main.cpu.A << 1) & 0xff;
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.cycles += 8;
	}
	
//	SRA B
//	2  8
//	Z 0 0 C
	public static void SRA_B() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		int bit = Main.cpu.B & 0x80;
		int res = (Main.cpu.B >> 1) | bit; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.B & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.B = res & 0xff;
		Main.cpu.cycles += 8;
	}
	
//	SRA C
//	2  8
//	Z 0 0 C
	public static void SRA_C() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		int bit = Main.cpu.C & 0x80;
		int res = (Main.cpu.C >> 1) | bit; // Rotate to the right

		//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.C & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.C = res & 0xff;
		Main.cpu.cycles += 8;
	}
	
//	SRA D
//	2  8
//	Z 0 0 C
	public static void SRA_D() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		int bit = Main.cpu.D & 0x80;
		int res = (Main.cpu.D >> 1) | bit; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.D & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.D = res & 0xff;
		Main.cpu.cycles += 8;
	}
	
//	SRA E
//	2  8
//	Z 0 0 C
	public static void SRA_E() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		int bit = Main.cpu.E & 0x80;
		int res = (Main.cpu.E >> 1) | bit; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.E & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.E = res & 0xff;
		Main.cpu.cycles += 8;
	}
	
//	SRA H
//	2  8
//	Z 0 0 C
	public static void SRA_H() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		int bit = Main.cpu.H & 0x80;
		int res = (Main.cpu.H >> 1) | bit; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.H & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.H = res & 0xff;
		Main.cpu.cycles += 8;
	}
	
//	SRA L
//	2  8
//	Z 0 0 C
	public static void SRA_L() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		int bit = Main.cpu.L & 0x80;
		int res = (Main.cpu.L >> 1) | bit; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.L & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.L = res & 0xff;
		Main.cpu.cycles += 8;
	}
//	SRA (HL)
//	2  16
//	Z 0 0 C
	public static void SRA_HL() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		int bit = Main.mmu.getByte(Main.cpu.getHL()) & 0x80;
		int res = (Main.mmu.getByte(Main.cpu.getHL()) >> 1) | bit; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.mmu.getByte(Main.cpu.getHL()) & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.checkZero8bit(res);
		Main.mmu.setByte(Main.cpu.getHL(), res & 0xff);
		Main.cpu.cycles += 16;
	}
	
//	SRA A
//	2  8
//	Z 0 0 C
	public static void SRA_A() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		int bit = Main.cpu.A & 0x80;
		int res = (Main.cpu.A >> 1) | bit; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.A & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.A = res & 0xff;
		Main.cpu.cycles += 8;
	}
	
//	SWAP B
//	2  8
//	Z 0 0 0
	public static void SWAP_B() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		int l = Main.cpu.B & 0x0f;
		int h = Main.cpu.B & 0xf0;
		Main.cpu.B = (l << 4) | (h >> 4);
		Main.cpu.checkZero8bit(Main.cpu.B);
		Main.cpu.cycles += 8;
	}
	
//	SWAP C
//	2  8
//	Z 0 0 0
	public static void SWAP_C() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		int l = Main.cpu.C & 0x0f;
		int h = Main.cpu.C & 0xf0;
		Main.cpu.C = (l << 4) | (h >> 4);
		Main.cpu.checkZero8bit(Main.cpu.C);
		Main.cpu.cycles += 8;
	}
	
//	SWAP D
//	2  8
//	Z 0 0 0
	public static void SWAP_D() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		int l = Main.cpu.D & 0x0f;
		int h = Main.cpu.D & 0xf0;
		Main.cpu.D = (l << 4) | (h >> 4);
		Main.cpu.checkZero8bit(Main.cpu.D);
		Main.cpu.cycles += 8;
	}
	
//	SWAP E
//	2  8
//	Z 0 0 0
	public static void SWAP_E() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		int l = Main.cpu.E & 0x0f;
		int h = Main.cpu.E & 0xf0;
		Main.cpu.E = (l << 4) | (h >> 4);
		Main.cpu.checkZero8bit(Main.cpu.E);
		Main.cpu.cycles += 8;
	}
	
//	SWAP H
//	2  8
//	Z 0 0 0
	public static void SWAP_H() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		int l = Main.cpu.H & 0x0f;
		int h = Main.cpu.H & 0xf0;
		Main.cpu.H = (l << 4) | (h >> 4);
		Main.cpu.checkZero8bit(Main.cpu.H);
		Main.cpu.cycles += 8;
	}
	
//	SWAP L
//	2  8
//	Z 0 0 0
	public static void SWAP_L() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		int l = Main.cpu.L & 0x0f;
		int h = Main.cpu.L & 0xf0;
		Main.cpu.L = (l << 4) | (h >> 4);
		Main.cpu.checkZero8bit(Main.cpu.L);
		Main.cpu.cycles += 8;
	}
	
//	SWAP (HL)
//	2  16
//	Z 0 0 0
	public static void SWAP_HL() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		int l = Main.mmu.getByte(Main.cpu.getHL()) & 0x0f;
		int h = Main.mmu.getByte(Main.cpu.getHL()) & 0xf0;
		Main.mmu.setByte(Main.cpu.getHL(), (l << 4) | (h >> 4));
		Main.cpu.checkZero8bit(Main.mmu.getByte(Main.cpu.getHL()));
		Main.cpu.cycles += 16;
	}
	
//	SWAP A
//	2  8
//	Z 0 0 0
	public static void SWAP_A() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		Main.cpu.resetFlagC();
		int l = Main.cpu.A & 0x0f;
		int h = Main.cpu.A & 0xf0;
		Main.cpu.A = (l << 4) | (h >> 4);
		Main.cpu.checkZero8bit(Main.cpu.A);
		Main.cpu.cycles += 8;
	}
	
//	SRL B
//	2  8
//	Z 0 0 C
	public static void SRL_B() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		int res = Main.cpu.B >> 1; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.B & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.B = res & 0xff;
		Main.cpu.cycles += 8;
	}
	
//	SRL C
//	2  8
//	Z 0 0 C
	public static void SRL_C() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		int res = Main.cpu.C >> 1; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.C & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.C = res & 0xff;
		Main.cpu.cycles += 8;
	}
	
//	SRL D
//	2  8
//	Z 0 0 C
	public static void SRL_D() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		int res = Main.cpu.D >> 1; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.D & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.D = res & 0xff;
		Main.cpu.cycles += 8;
	}
	
//	SRL E
//	2  8
//	Z 0 0 C
	public static void SRL_E() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		int res = Main.cpu.E >> 1; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.E & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.E = res & 0xff;
		Main.cpu.cycles += 8;
	}
	
//	SRL H
//	2  8
//	Z 0 0 C
	public static void SRL_H() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		int res = Main.cpu.H >> 1; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.H & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.H = res & 0xff;
		Main.cpu.cycles += 8;
	}
	
//	SRL L
//	2  8
//	Z 0 0 C
	public static void SRL_L() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		int res = Main.cpu.L >> 1; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.L & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.L = res & 0xff;
		Main.cpu.cycles += 8;
	}
	
//	SRL (HL)
//	2  16
//	Z 0 0 C
	public static void SRL_HL() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		int res = Main.mmu.getByte(Main.cpu.getHL()) >> 1; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.mmu.getByte(Main.cpu.getHL()) & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.checkZero8bit(res);
		Main.mmu.setByte(Main.cpu.getHL(), res & 0xff);
		Main.cpu.cycles += 16;
	}
	
//	SRL A
//	2  8
//	Z 0 0 C
	public static void SRL_A() {
		Main.cpu.resetFlagN();
		Main.cpu.resetFlagH();
		int res = Main.cpu.A >> 1; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.cpu.A & 0x01) == 0x01) {
			Main.cpu.setFlagC();
		} else {
			Main.cpu.resetFlagC();
		}
		
		Main.cpu.checkZero8bit(res);
		Main.cpu.A = res & 0xff;
		Main.cpu.cycles += 8;
	}
	
//	BIT 0, B
//	2  8
//	Z 0 1 -
	public static void BIT_0_B() {
		if ((Main.cpu.B & 0x01) == 0x01) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 0, C
//	2  8
//	Z 0 1 -
	public static void BIT_0_C() {
		if ((Main.cpu.C & 0x01) == 0x01) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 0, D
//	2  8
//	Z 0 1 -
	public static void BIT_0_D() {
		if ((Main.cpu.D & 0x01) == 0x01) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 0, E
//	2  8
//	Z 0 1 -
	public static void BIT_0_E() {
		if ((Main.cpu.E & 0x01) == 0x01) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 0, H
//	2  8
//	Z 0 1 -
	public static void BIT_0_H() {
		if ((Main.cpu.H & 0x01) == 0x01) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 0, L
//	2  8
//	Z 0 1 -
	public static void BIT_0_L() {
		if ((Main.cpu.L & 0x01) == 0x01) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 0, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_0_HL() {
		if ((Main.mmu.getByte(Main.cpu.getHL()) & 0x01) == 0x01) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 12;
	}
	
//	BIT 0, A
//	2  8
//	Z 0 1 -
	public static void BIT_0_A() {
		if ((Main.cpu.A & 0x01) == 0x01) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 1, B
//	2  8
//	Z 0 1 -
	public static void BIT_1_B() {
		if ((Main.cpu.B & 0x02) == 0x02) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 1, C
//	2  8
//	Z 0 1 -
	public static void BIT_1_C() {
		if ((Main.cpu.C & 0x02) == 0x02) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 1, D
//	2  8
//	Z 0 1 -
	public static void BIT_1_D() {
		if ((Main.cpu.D & 0x02) == 0x02) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 1, E
//	2  8
//	Z 0 1 -
	public static void BIT_1_E() {
		if ((Main.cpu.E & 0x02) == 0x02) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 1, H
//	2  8
//	Z 0 1 -
	public static void BIT_1_H() {
		if ((Main.cpu.H & 0x02) == 0x02) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 1, L
//	2  8
//	Z 0 1 -
	public static void BIT_1_L() {
		if ((Main.cpu.L & 0x02) == 0x02) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 1, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_1_HL() {
		if ((Main.mmu.getByte(Main.cpu.getHL()) & 0x02) == 0x02) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 12;
	}
	
//	BIT 1, A
//	2  8
//	Z 0 1 -
	public static void BIT_1_A() {
		if ((Main.cpu.A & 0x02) == 0x02) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 2, B
//	2  8
//	Z 0 1 -
	public static void BIT_2_B() {
		if ((Main.cpu.B & 0x04) == 0x04) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 2, C
//	2  8
//	Z 0 1 -
	public static void BIT_2_C() {
		if ((Main.cpu.C & 0x04) == 0x04) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
		
//	BIT 2, D
//	2  8
//	Z 0 1 -
	public static void BIT_2_D() {
		if ((Main.cpu.D & 0x04) == 0x04) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 2, E
//	2  8
//	Z 0 1 -
	public static void BIT_2_E() {
		if ((Main.cpu.E & 0x04) == 0x04) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 2, H
//	2  8
//	Z 0 1 -
	public static void BIT_2_H() {
		if ((Main.cpu.H & 0x04) == 0x04) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 2, L
//	2  8
//	Z 0 1 -
	public static void BIT_2_L() {
		if ((Main.cpu.L & 0x04) == 0x04) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 2, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_2_HL() {
		if ((Main.mmu.getByte(Main.cpu.getHL()) & 0x04) == 0x04) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 12;
	}
	
//	BIT 2, A
//	2  8
//	Z 0 1 -
	public static void BIT_2_A() {
		if ((Main.cpu.A & 0x04) == 0x04) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 3, B
//	2  8
//	Z 0 1 -
	public static void BIT_3_B() {
		if ((Main.cpu.B & 0x08) == 0x08) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 3, C
//	2  8
//	Z 0 1 -
	public static void BIT_3_C() {
		if ((Main.cpu.C & 0x08) == 0x08) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
		
//	BIT 3, D
//	2  8
//	Z 0 1 -
	public static void BIT_3_D() {
		if ((Main.cpu.D & 0x08) == 0x08) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 3, E
//	2  8
//	Z 0 1 -
	public static void BIT_3_E() {
		if ((Main.cpu.E & 0x08) == 0x08) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 3, H
//	2  8
//	Z 0 1 -
	public static void BIT_3_H() {
		if ((Main.cpu.H & 0x08) == 0x08) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 3, L
//	2  8
//	Z 0 1 -
	public static void BIT_3_L() {
		if ((Main.cpu.L & 0x08) == 0x08) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 3, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_3_HL() {
		if ((Main.mmu.getByte(Main.cpu.getHL()) & 0x08) == 0x08) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 12;
	}
	
//	BIT 3, A
//	2  8
//	Z 0 1 -
	public static void BIT_3_A() {
		if ((Main.cpu.A & 0x08) == 0x08) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 4, B
//	2  8
//	Z 0 1 -
	public static void BIT_4_B() {
		if ((Main.cpu.B & 0x10) == 0x10) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 4, C
//	2  8
//	Z 0 1 -
	public static void BIT_4_C() {
		if ((Main.cpu.C & 0x10) == 0x10) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
		
//	BIT 4, D
//	2  8
//	Z 0 1 -
	public static void BIT_4_D() {
		if ((Main.cpu.D & 0x10) == 0x10) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 4, E
//	2  8
//	Z 0 1 -
	public static void BIT_4_E() {
		if ((Main.cpu.E & 0x10) == 0x10) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 4, H
//	2  8
//	Z 0 1 -
	public static void BIT_4_H() {
		if ((Main.cpu.H & 0x10) == 0x10) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 4, L
//	2  8
//	Z 0 1 -
	public static void BIT_4_L() {
		if ((Main.cpu.L & 0x10) == 0x10) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 4, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_4_HL() {
		if ((Main.mmu.getByte(Main.cpu.getHL()) & 0x10) == 0x10) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 12;
	}
	
//	BIT 4, A
//	2  8
//	Z 0 1 -
	public static void BIT_4_A() {
		if ((Main.cpu.A & 0x10) == 0x10) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 5, B
//	2  8
//	Z 0 1 -
	public static void BIT_5_B() {
		if ((Main.cpu.B & 0x20) == 0x20) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 5, C
//	2  8
//	Z 0 1 -
	public static void BIT_5_C() {
		if ((Main.cpu.C & 0x20) == 0x20) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
		
//	BIT 5, D
//	2  8
//	Z 0 1 -
	public static void BIT_5_D() {
		if ((Main.cpu.D & 0x20) == 0x20) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 5, E
//	2  8
//	Z 0 1 -
	public static void BIT_5_E() {
		if ((Main.cpu.E & 0x20) == 0x20) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 5, H
//	2  8
//	Z 0 1 -
	public static void BIT_5_H() {
		if ((Main.cpu.H & 0x20) == 0x20) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 5, L
//	2  8
//	Z 0 1 -
	public static void BIT_5_L() {
		if ((Main.cpu.L & 0x20) == 0x20) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 5, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_5_HL() {
		if ((Main.mmu.getByte(Main.cpu.getHL()) & 0x20) == 0x20) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 12;
	}
	
//	BIT 5, A
//	2  8
//	Z 0 1 -
	public static void BIT_5_A() {
		if ((Main.cpu.A & 0x20) == 0x20) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 6, B
//	2  8
//	Z 0 1 -
	public static void BIT_6_B() {
		if ((Main.cpu.B & 0x40) == 0x40) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 6, C
//	2  8
//	Z 0 1 -
	public static void BIT_6_C() {
		if ((Main.cpu.C & 0x40) == 0x40) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
		
//	BIT 6, D
//	2  8
//	Z 0 1 -
	public static void BIT_6_D() {
		if ((Main.cpu.D & 0x40) == 0x40) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 6, E
//	2  8
//	Z 0 1 -
	public static void BIT_6_E() {
		if ((Main.cpu.E & 0x40) == 0x40) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 6, H
//	2  8
//	Z 0 1 -
	public static void BIT_6_H() {
		if ((Main.cpu.H & 0x40) == 0x40) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 6, L
//	2  8
//	Z 0 1 -
	public static void BIT_6_L() {
		if ((Main.cpu.L & 0x40) == 0x40) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 6, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_6_HL() {
		if ((Main.mmu.getByte(Main.cpu.getHL()) & 0x40) == 0x40) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 12;
	}
	
//	BIT 6, A
//	2  8
//	Z 0 1 -
	public static void BIT_6_A() {
		if ((Main.cpu.A & 0x40) == 0x40) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 7, B
//	2  8
//	Z 0 1 -
	public static void BIT_7_B() {
		if ((Main.cpu.B & 0x80) == 0x80) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 7, C
//	2  8
//	Z 0 1 -
	public static void BIT_7_C() {
		if ((Main.cpu.C & 0x80) == 0x80) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 7, D
//	2  8
//	Z 0 1 -
	public static void BIT_7_D() {
		if ((Main.cpu.D & 0x80) == 0x80) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 7, E
//	2  8
//	Z 0 1 -
	public static void BIT_7_E() {
		if ((Main.cpu.E & 0x80) == 0x80) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 7, H
//	2  8
//	Z 0 1 -
	public static void BIT_7_H() {
		if ((Main.cpu.H & 0x80) == 0x80) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 7, L
//	2  8
//	Z 0 1 -
	public static void BIT_7_L() {
		if ((Main.cpu.L & 0x80) == 0x80) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	BIT 7, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_7_HL() {
		if ((Main.mmu.getByte(Main.cpu.getHL()) & 0x80) == 0x80) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 12;
	}
	
//	BIT 7, A
//	2  8
//	Z 0 1 -
	public static void BIT_7_A() {
		if ((Main.cpu.A & 0x80) == 0x80) {
			Main.cpu.resetFlagZ();
		} else {
			Main.cpu.setFlagZ();
		}
		Main.cpu.resetFlagN();
		Main.cpu.setFlagH();
		Main.cpu.cycles += 8;
	}
	
//	RES 0, B
//	2  8
//	- - - -
	public static void RES_0_B() {
		Main.cpu.B &= 0xfe;
		Main.cpu.cycles += 8;
	}
	
//	RES 0, C
//	2  8
//	- - - -
	public static void RES_0_C() {
		Main.cpu.C &= 0xfe;
		Main.cpu.cycles += 8;
	}
	
//	RES 0, D
//	2  8
//	- - - -
	public static void RES_0_D() {
		Main.cpu.D &= 0xfe;
		Main.cpu.cycles += 8;
	}
		
//	RES 0, E
//	2  8
//	- - - -
	public static void RES_0_E() {
		Main.cpu.E &= 0xfe;
		Main.cpu.cycles += 8;
	}
	
//	RES 0, H
//	2  8
//	- - - -
	public static void RES_0_H() {
		Main.cpu.H &= 0xfe;
		Main.cpu.cycles += 8;
	}
	
//	RES 0, L
//	2  8
//	- - - -
	public static void RES_0_L() {
		Main.cpu.L &= 0xfe;
		Main.cpu.cycles += 8;
	}
	
//	RES 0, (HL)
//	2  16
//	- - - -
	public static void RES_0_HL() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) & 0xfe);
		Main.cpu.cycles += 16;
	}
	
//	RES 0, A
//	2  8
//	- - - -
	public static void RES_0_A() {
		Main.cpu.A &= 0xfe;
		Main.cpu.cycles += 8;
	}
	
//	RES 1, B
//	2  8
//	- - - -
	public static void RES_1_B() {
		Main.cpu.B &= 0xfd;
		Main.cpu.cycles += 8;
	}
		
//	RES 1, C
//	2  8
//	- - - -
	public static void RES_1_C() {
		Main.cpu.C &= 0xfd;
		Main.cpu.cycles += 8;
	}
	
//	RES 1, D
//	2  8
//	- - - -
	public static void RES_1_D() {
		Main.cpu.D &= 0xfd;
		Main.cpu.cycles += 8;
	}

//	RES 1, E
//	2  8
//	- - - -
	public static void RES_1_E() {
		Main.cpu.E &= 0xfd;
		Main.cpu.cycles += 8;
	}

//	RES 1, H
//	2  8
//	- - - -
	public static void RES_1_H() {
		Main.cpu.H &= 0xfd;
		Main.cpu.cycles += 8;
	}

//	RES 1, L
//	2  8
//	- - - -
	public static void RES_1_L() {
		Main.cpu.L &= 0xfd;
		Main.cpu.cycles += 8;
	}

//	RES 1, (HL)
//	2  16
//	- - - -
	public static void RES_1_HL() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) & 0xfd);
		Main.cpu.cycles += 16;
	}
	
//	RES 1, A
//	2  8
//	- - - -
	public static void RES_1_A() {
		Main.cpu.A &= 0xfd;
		Main.cpu.cycles += 8;
	}

//	RES 2, B
//	2  8
//	- - - -
	public static void RES_2_B() {
		Main.cpu.B &= 0xfb;
		Main.cpu.cycles += 8;
	}

//	RES 2, C
//	2  8
//	- - - -
	public static void RES_2_C() {
		Main.cpu.C &= 0xfb;
		Main.cpu.cycles += 8;
	}
	
//	RES 2, D
//	2  8
//	- - - -
	public static void RES_2_D() {
		Main.cpu.D &= 0xfb;
		Main.cpu.cycles += 8;
	}

//	RES 2, E
//	2  8
//	- - - -
	public static void RES_2_E() {
		Main.cpu.E &= 0xfb;
		Main.cpu.cycles += 8;
	}

//	RES 2, H
//	2  8
//	- - - -
	public static void RES_2_H() {
		Main.cpu.H &= 0xfb;
		Main.cpu.cycles += 8;
	}

//	RES 2, L
//	2  8
//	- - - -
	public static void RES_2_L() {
		Main.cpu.L &= 0xfb;
		Main.cpu.cycles += 8;
	}

//	RES 2, (HL)
//	2  16
//	- - - -
	public static void RES_2_HL() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) & 0xfb);
		Main.cpu.cycles += 16;
	}
	
//	RES 2, A
//	2  8
//	- - - -
	public static void RES_2_A() {
		Main.cpu.A &= 0xfb;
		Main.cpu.cycles += 8;
	}
	
//	RES 3, B
//	2  8
//	- - - -
	public static void RES_3_B() {
		Main.cpu.B &= 0xf7;
		Main.cpu.cycles += 8;
	}
		
//	RES 3, C
//	2  8
//	- - - -
	public static void RES_3_C() {
		Main.cpu.C &= 0xf7;
		Main.cpu.cycles += 8;
	}
		
//	RES 3, D
//	2  8
//	- - - -
	public static void RES_3_D() {
		Main.cpu.D &= 0xf7;
		Main.cpu.cycles += 8;
	}
	
//	RES 3, E
//	2  8
//	- - - -
	public static void RES_3_E() {
		Main.cpu.E &= 0xf7;
		Main.cpu.cycles += 8;
	}
	
//	RES 3, H
//	2  8
//	- - - -
	public static void RES_3_H() {
		Main.cpu.H &= 0xf7;
		Main.cpu.cycles += 8;
	}
	
//	RES 3, L
//	2  8
//	- - - -
	public static void RES_3_L() {
		Main.cpu.L &= 0xf7;
		Main.cpu.cycles += 8;
	}
	
//	RES 3, (HL)
//	2  16
//	- - - -
	public static void RES_3_HL() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) & 0xf7);
		Main.cpu.cycles += 16;
	}
	
//	RES 3, A
//	2  8
//	- - - -
	public static void RES_3_A() {
		Main.cpu.A &= 0xf7;
		Main.cpu.cycles += 8;
	}
	
//	RES 4, B
//	2  8
//	- - - -
	public static void RES_4_B() {
		Main.cpu.B &= 0xef;
		Main.cpu.cycles += 8;
	}
	
//	RES 4, C
//	2  8
//	- - - -
	public static void RES_4_C() {
		Main.cpu.C &= 0xef;
		Main.cpu.cycles += 8;
	}
	
//	RES 4, D
//	2  8
//	- - - -
	public static void RES_4_D() {
		Main.cpu.D &= 0xef;
		Main.cpu.cycles += 8;
	}
	
//	RES 4, E
//	2  8
//	- - - -
	public static void RES_4_E() {
		Main.cpu.E &= 0xef;
		Main.cpu.cycles += 8;
	}
	
//	RES 4, H
//	2  8
//	- - - -
	public static void RES_4_H() {
		Main.cpu.H &= 0xef;
		Main.cpu.cycles += 8;
	}
	
//	RES 4, L
//	2  8
//	- - - -
	public static void RES_4_L() {
		Main.cpu.L &= 0xef;
		Main.cpu.cycles += 8;
	}
	
//	RES 4, (HL)
//	2  16
//	- - - -
	public static void RES_4_HL() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) & 0xef);
		Main.cpu.cycles += 16;
	}
	
//	RES 4, A
//	2  8
//	- - - -
	public static void RES_4_A() {
		Main.cpu.A &= 0xef;
		Main.cpu.cycles += 8;
	}
	
//	RES 5, B
//	2  8
//	- - - -
	public static void RES_5_B() {
		Main.cpu.B &= 0xdf;
		Main.cpu.cycles += 8;
	}
		
//	RES 5, C
//	2  8
//	- - - -
	public static void RES_5_C() {
		Main.cpu.C &= 0xdf;
		Main.cpu.cycles += 8;
	}
	
//	RES 5, D
//	2  8
//	- - - -
	public static void RES_5_D() {
		Main.cpu.D &= 0xdf;
		Main.cpu.cycles += 8;
	}

//	RES 5, E
//	2  8
//	- - - -
	public static void RES_5_E() {
		Main.cpu.E &= 0xdf;
		Main.cpu.cycles += 8;
	}

//	RES 5, H
//	2  8
//	- - - -
	public static void RES_5_H() {
		Main.cpu.H &= 0xdf;
		Main.cpu.cycles += 8;
	}

//	RES 5, L
//	2  8
//	- - - -
	public static void RES_5_L() {
		Main.cpu.L &= 0xdf;
		Main.cpu.cycles += 8;
	}

//	RES 5, (HL)
//	2  16
//	- - - -
	public static void RES_5_HL() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) & 0xdf);
		Main.cpu.cycles += 16;
	}
	
//	RES 5, A
//	2  8
//	- - - -
	public static void RES_5_A() {
		Main.cpu.A &= 0xdf;
		Main.cpu.cycles += 8;
	}

//	RES 6, B
//	2  8
//	- - - -
	public static void RES_6_B() {
		Main.cpu.B &= 0xbf;
		Main.cpu.cycles += 8;
	}
	
//	RES 6, C
//	2  8
//	- - - -
	public static void RES_6_C() {
		Main.cpu.C &= 0xbf;
		Main.cpu.cycles += 8;
	}
		
//	RES 6, D
//	2  8
//	- - - -
	public static void RES_6_D() {
		Main.cpu.D &= 0xbf;
		Main.cpu.cycles += 8;
	}
	
//	RES 6, E
//	2  8
//	- - - -
	public static void RES_6_E() {
		Main.cpu.E &= 0xbf;
		Main.cpu.cycles += 8;
	}
	
//	RES 6, H
//	2  8
//	- - - -
	public static void RES_6_H() {
		Main.cpu.H &= 0xbf;
		Main.cpu.cycles += 8;
	}
	
//	RES 6, L
//	2  8
//	- - - -
	public static void RES_6_L() {
		Main.cpu.L &= 0xbf;
		Main.cpu.cycles += 8;
	}
	
//	RES 6, (HL)
//	2  16
//	- - - -
	public static void RES_6_HL() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) & 0xbf);
		Main.cpu.cycles += 16;
	}
	
//	RES 6, A
//	2  8
//	- - - -
	public static void RES_6_A() {
		Main.cpu.A &= 0xbf;
		Main.cpu.cycles += 8;
	}
	
//	RES 7, B
//	2  8
//	- - - -
	public static void RES_7_B() {
		Main.cpu.B &= 0x7f;
		Main.cpu.cycles += 8;
	}
	
//	RES 7, C
//	2  8
//	- - - -
	public static void RES_7_C() {
		Main.cpu.C &= 0x7f;
		Main.cpu.cycles += 8;
	}
		
//	RES 7, D
//	2  8
//	- - - -
	public static void RES_7_D() {
		Main.cpu.D &= 0x7f;
		Main.cpu.cycles += 8;
	}
	
//	RES 7, E
//	2  8
//	- - - -
	public static void RES_7_E() {
		Main.cpu.E &= 0x7f;
		Main.cpu.cycles += 8;
	}
	
//	RES 7, H
//	2  8
//	- - - -
	public static void RES_7_H() {
		Main.cpu.H &= 0x7f;
		Main.cpu.cycles += 8;
	}
	
//	RES 7, L
//	2  8
//	- - - -
	public static void RES_7_L() {
		Main.cpu.L &= 0x7f;
		Main.cpu.cycles += 8;
	}
	
//	RES 7, (HL)
//	2  16
//	- - - -
	public static void RES_7_HL() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) & 0x7f);
		Main.cpu.cycles += 16;
	}

//	RES 7, A
//	2  8
//	- - - -
	public static void RES_7_A() {
		Main.cpu.A &= 0x7f;
		Main.cpu.cycles += 8;
	}
	
//	SET 0, B
//	2  8
//	- - - -
	public static void SET_0_B() {
		Main.cpu.B |= 0x01;
		Main.cpu.cycles += 8;
	}
	
//	SET 0, C
//	2  8
//	- - - -
	public static void SET_0_C() {
		Main.cpu.C |= 0x01;
		Main.cpu.cycles += 8;
	}
		
//	SET 0, D
//	2  8
//	- - - -
	public static void SET_0_D() {
		Main.cpu.D |= 0x01;
		Main.cpu.cycles += 8;
	}
	
//	SET 0, E
//	2  8
//	- - - -
	public static void SET_0_E() {
		Main.cpu.E |= 0x01;
		Main.cpu.cycles += 8;
	}
	
//	SET 0, H
//	2  8
//	- - - -
	public static void SET_0_H() {
		Main.cpu.H |= 0x01;
		Main.cpu.cycles += 8;
	}
	
//	SET 0, L
//	2  8
//	- - - -
	public static void SET_0_L() {
		Main.cpu.L |= 0x01;
		Main.cpu.cycles += 8;
	}
	
//	SET 0, (HL)
//	2  16
//	- - - -
	public static void SET_0_HL() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) | 0x01);
		Main.cpu.cycles += 16;
	}
	
//	SET 0, A
//	2  8
//	- - - -
	public static void SET_0_A() {
		Main.cpu.A |= 0x01;
		Main.cpu.cycles += 8;
	}
	
//	SET 1, B
//	2  8
//	- - - -
	public static void SET_1_B() {
		Main.cpu.B |= 0x02;
		Main.cpu.cycles += 8;
	}
	
//	SET 1, C
//	2  8
//	- - - -
	public static void SET_1_C() {
		Main.cpu.C |= 0x02;
		Main.cpu.cycles += 8;
	}
	
//	SET 1, D
//	2  8
//	- - - -
	public static void SET_1_D() {
		Main.cpu.D |= 0x02;
		Main.cpu.cycles += 8;
	}
	
//	SET 1, E
//	2  8
//	- - - -
	public static void SET_1_E() {
		Main.cpu.E |= 0x02;
		Main.cpu.cycles += 8;
	}
	
//	SET 1, H
//	2  8
//	- - - -
	public static void SET_1_H() {
		Main.cpu.H |= 0x02;
		Main.cpu.cycles += 8;
	}
	
//	SET 1, L
//	2  8
//	- - - -
	public static void SET_1_L() {
		Main.cpu.L |= 0x02;
		Main.cpu.cycles += 8;
	}
	
//	SET 1, (HL)
//	2  16
//	- - - -
	public static void SET_1_HL() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) | 0x02);
		Main.cpu.cycles += 16;
	}
	
//	SET 1, A
//	2  8
//	- - - -
	public static void SET_1_A() {
		Main.cpu.A |= 0x02;
		Main.cpu.cycles += 8;
	}
	
//	SET 2, B
//	2  8
//	- - - -
	public static void SET_2_B() {
		Main.cpu.B |= 0x04;
		Main.cpu.cycles += 8;
	}
	
//	SET 2, C
//	2  8
//	- - - -
	public static void SET_2_C() {
		Main.cpu.C |= 0x04;
		Main.cpu.cycles += 8;
	}
		
//	SET 2, D
//	2  8
//	- - - -
	public static void SET_2_D() {
		Main.cpu.D |= 0x04;
		Main.cpu.cycles += 8;
	}
	
//	SET 2, E
//	2  8
//	- - - -
	public static void SET_2_E() {
		Main.cpu.E |= 0x04;
		Main.cpu.cycles += 8;
	}
	
//	SET 2, H
//	2  8
//	- - - -
	public static void SET_2_H() {
		Main.cpu.H |= 0x04;
		Main.cpu.cycles += 8;
	}
	
//	SET 2, L
//	2  8
//	- - - -
	public static void SET_2_L() {
		Main.cpu.L |= 0x04;
		Main.cpu.cycles += 8;
	}
	
//	SET 2, (HL)
//	2  16
//	- - - -
	public static void SET_2_HL() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) | 0x04);
		Main.cpu.cycles += 16;
	}
	
//	SET 2, A
//	2  8
//	- - - -
	public static void SET_2_A() {
		Main.cpu.A |= 0x04;
		Main.cpu.cycles += 8;
	}
	
//	SET 3, B
//	2  8
//	- - - -
	public static void SET_3_B() {
		Main.cpu.B |= 0x08;
		Main.cpu.cycles += 8;
	}
	
//	SET 3, C
//	2  8
//	- - - -
	public static void SET_3_C() {
		Main.cpu.C |= 0x08;
		Main.cpu.cycles += 8;
	}
		
//	SET 3, D
//	2  8
//	- - - -
	public static void SET_3_D() {
		Main.cpu.D |= 0x08;
		Main.cpu.cycles += 8;
	}
	
//	SET 3, E
//	2  8
//	- - - -
	public static void SET_3_E() {
		Main.cpu.E |= 0x08;
		Main.cpu.cycles += 8;
	}
	
//	SET 3, H
//	2  8
//	- - - -
	public static void SET_3_H() {
		Main.cpu.H |= 0x08;
		Main.cpu.cycles += 8;
	}
	
//	SET 3, L
//	2  8
//	- - - -
	public static void SET_3_L() {
		Main.cpu.L |= 0x08;
		Main.cpu.cycles += 8;
	}
	
//	SET 3, (HL)
//	2  16
//	- - - -
	public static void SET_3_HL() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) | 0x08);
		Main.cpu.cycles += 16;
	}
	
//	SET 3, A
//	2  8
//	- - - -
	public static void SET_3_A() {
		Main.cpu.A |= 0x08;
		Main.cpu.cycles += 8;
	}
	
//	SET 4, B
//	2  8
//	- - - -
	public static void SET_4_B() {
		Main.cpu.B |= 0x10;
		Main.cpu.cycles += 8;
	}
	
//	SET 4, C
//	2  8
//	- - - -
	public static void SET_4_C() {
		Main.cpu.C |= 0x10;
		Main.cpu.cycles += 8;
	}
	
//	SET 4, D
//	2  8
//	- - - -
	public static void SET_4_D() {
		Main.cpu.D |= 0x10;
		Main.cpu.cycles += 8;
	}
	
//	SET 4, E
//	2  8
//	- - - -
	public static void SET_4_E() {
		Main.cpu.E |= 0x10;
		Main.cpu.cycles += 8;
	}
	
//	SET 4, H
//	2  8
//	- - - -
	public static void SET_4_H() {
		Main.cpu.H |= 0x10;
		Main.cpu.cycles += 8;
	}
	
//	SET 4, L
//	2  8
//	- - - -
	public static void SET_4_L() {
		Main.cpu.L |= 0x10;
		Main.cpu.cycles += 8;
	}
	
//	SET 4, (HL)
//	2  16
//	- - - -
	public static void SET_4_HL() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) | 0x10);
		Main.cpu.cycles += 16;
	}

//	SET 4, A
//	2  8
//	- - - -
	public static void SET_4_A() {
		Main.cpu.A |= 0x10;
		Main.cpu.cycles += 8;
	}
	
//	SET 5, B
//	2  8
//	- - - -
	public static void SET_5_B() {
		Main.cpu.B |= 0x20;
		Main.cpu.cycles += 8;
	}
	
//	SET 5, C
//	2  8
//	- - - -
	public static void SET_5_C() {
		Main.cpu.C |= 0x20;
		Main.cpu.cycles += 8;
	}
	
//	SET 5, D
//	2  8
//	- - - -
	public static void SET_5_D() {
		Main.cpu.D |= 0x20;
		Main.cpu.cycles += 8;
	}

//	SET 5, E
//	2  8
//	- - - -
	public static void SET_5_E() {
		Main.cpu.E |= 0x20;
		Main.cpu.cycles += 8;
	}

//	SET 5, H
//	2  8
//	- - - -
	public static void SET_5_H() {
		Main.cpu.H |= 0x20;
		Main.cpu.cycles += 8;
	}

//	SET 5, L
//	2  8
//	- - - -
	public static void SET_5_L() {
		Main.cpu.L |= 0x20;
		Main.cpu.cycles += 8;
	}

//	SET 5, (HL)
//	2  16
//	- - - -
	public static void SET_5_HL() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) | 0x20);
		Main.cpu.cycles += 16;
	}
	
//	SET 5, A
//	2  8
//	- - - -
	public static void SET_5_A() {
		Main.cpu.A |= 0x20;
		Main.cpu.cycles += 8;
	}

//	SET 6, B
//	2  8
//	- - - -
	public static void SET_6_B() {
		Main.cpu.B |= 0x40;
		Main.cpu.cycles += 8;
	}

//	SET 6, C
//	2  8
//	- - - -
	public static void SET_6_C() {
		Main.cpu.C |= 0x40;
		Main.cpu.cycles += 8;
	}
	
//	SET 6, D
//	2  8
//	- - - -
	public static void SET_6_D() {
		Main.cpu.D |= 0x40;
		Main.cpu.cycles += 8;
	}

//	SET 6, E
//	2  8
//	- - - -
	public static void SET_6_E() {
		Main.cpu.E |= 0x40;
		Main.cpu.cycles += 8;
	}

//	SET 6, H
//	2  8
//	- - - -
	public static void SET_6_H() {
		Main.cpu.H |= 0x40;
		Main.cpu.cycles += 8;
	}

//	SET 6, L
//	2  8
//	- - - -
	public static void SET_6_L() {
		Main.cpu.L |= 0x40;
		Main.cpu.cycles += 8;
	}

//	SET 6, (HL)
//	2  16
//	- - - -
	public static void SET_6_HL() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) | 0x40);
		Main.cpu.cycles += 16;
	}
	
//	SET 6, A
//	2  8
//	- - - -
	public static void SET_6_A() {
		Main.cpu.A |= 0x40;
		Main.cpu.cycles += 8;
	}

//	SET 7, B
//	2  8
//	- - - -
	public static void SET_7_B() {
		Main.cpu.B |= 0x80;
		Main.cpu.cycles += 8;
	}

//	SET 7, C
//	2  8
//	- - - -
	public static void SET_7_C() {
		Main.cpu.C |= 0x80;
		Main.cpu.cycles += 8;
	}
	
//	SET 7, D
//	2  8
//	- - - -
	public static void SET_7_D() {
		Main.cpu.D |= 0x80;
		Main.cpu.cycles += 8;
	}

//	SET 7, E
//	2  8
//	- - - -
	public static void SET_7_E() {
		Main.cpu.E |= 0x80;
		Main.cpu.cycles += 8;
	}

//	SET 7, H
//	2  8
//	- - - -
	public static void SET_7_H() {
		Main.cpu.H |= 0x80;
		Main.cpu.cycles += 8;
	}

//	SET 7, L
//	2  8
//	- - - -
	public static void SET_7_L() {
		Main.cpu.L |= 0x80;
		Main.cpu.cycles += 8;
	}

//	SET 7, (HL)
//	2  16
//	- - - -
	public static void SET_7_HL() {
		Main.mmu.setByte(Main.cpu.getHL(), Main.mmu.getByte(Main.cpu.getHL()) | 0x80);
		Main.cpu.cycles += 16;
	}
	
//	SET 7, A
//	2  8
//	- - - -
	public static void SET_7_A() {
		Main.cpu.A |= 0x80;
		Main.cpu.cycles += 8;
	}

}
