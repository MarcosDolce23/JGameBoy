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
		Cpu.cycles += 4;
	}
	
//	LD BC, d16
//	3  12
//	- - - -
	public static void LD_BC_d16() {
		Cpu.C = Cpu.fetch();
		Cpu.B = Cpu.fetch();
		Cpu.cycles += 12;
	}
	
//	LD (BC), A
//	1  8
//	- - - -
	public static void LD_BC_A() {
		Main.mmu.setByte(Cpu.getBC(), Cpu.A);
		Cpu.cycles += 8;
	}
	
//	INC BC
//	1  8
//	- - - -
	public static void INC_BC() {
		int aux = Cpu.getBC();
		aux += 1;
		Cpu.B = ((aux >> 8) & 0xff); // Set high 8 bits
		Cpu.C = (aux & 0xff); // Set low 8 bits
		Cpu.cycles += 8;
	}
	
//	INC B
//	1  4
//	Z 0 H -
	public static void INC_B() {
		Cpu.checkHalfCarry8bit(Cpu.B,1); // Check if there is Half Carry before the operation
		Cpu.B = (Cpu.B + 1) & 0xff;
		Cpu.checkZero8bit(Cpu.B);
		Cpu.resetFlagN();
		Cpu.cycles += 4;
	}
	
//	DEC B
//	1  4
//	Z 1 H -
	public static void DEC_B() {
		Cpu.checkHalfCarry8bitSub(Cpu.B,1); // Check if there is Half Carry before the operation
		Cpu.B = (Cpu.B - 1) & 0xff;
		Cpu.checkZero8bit(Cpu.B);
		Cpu.setFlagN();
		Cpu.cycles += 4;
	}
	
//	LD B, d8
//	2  8
//	- - - -
	public static void LD_B_d8() {
		Cpu.B = Cpu.fetch();
		Cpu.cycles += 8;
	}
	
//	RLCA
//	1  4
//	0 0 0 C
	public static void RLCA() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		if ((Cpu.A & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.A = ((Cpu.A << 1) | (Cpu.A >> 7)) & 0xff; // Rotate to left
		Cpu.cycles += 4;
	}
	
//	LD (a16), SP
//	3  20
//	- - - -
	public static void LD_a16_SP() {
		int index = (Cpu.fetch() + (Cpu.fetch() << 8)) & 0xffff;
		Main.mmu.setByte(index, Cpu.SP & 0xFF);
		Main.mmu.setByte(index + 1, (Cpu.SP >> 8) & 0xff);
		Cpu.cycles += 20;
	}
	
//	ADD HL, BC
//	1  8
//	- 0 H C
	public static void ADD_HL_BC() {
		Cpu.resetFlagN();
		Cpu.checkHalfCarry16bit(Cpu.getHL(), Cpu.getBC());
		int res = (Cpu.getHL() + Cpu.getBC());
		Cpu.checkCarry16bit(res);
		Cpu.L = res & 0xff;
		Cpu.H = (res >> 8) & 0xff;
		Cpu.cycles += 8;
	}
	
//	LD A, (BC)
//	1  8
//	- - - -
	public static void LD_A_BC() {
		Cpu.A = Main.mmu.getByte(Cpu.getBC());
		Cpu.cycles += 8;
	}
	
//	DEC BC
//	1  8
//	- - - -
	public static void DEC_BC() {
		int aux = Cpu.getBC();
		aux -= 1;
		Cpu.B = ((aux >> 8) & 0xff); // Set high 8 bits
		Cpu.C = (aux & 0xff); // Set low 8 bits
		Cpu.cycles += 8;
	}
	
//	INC C
//	1  4
//	Z 0 H -
	public static void INC_C() {
		Cpu.checkHalfCarry8bit(Cpu.C,1); // Check if there is Half Carry before the operation
		Cpu.C = (Cpu.C + 1) & 0xff;
		Cpu.checkZero8bit(Cpu.C);
		Cpu.resetFlagN();
		Cpu.cycles += 4;
	}
	
//	DEC C
//	1  4
//	Z 1 H -
	public static void DEC_C() {
		Cpu.checkHalfCarry8bitSub(Cpu.C,1); // Check if there is Half Carry before the operation
		Cpu.C = (Cpu.C - 1) & 0xff;
		Cpu.checkZero8bit(Cpu.C);
		Cpu.setFlagN();
		Cpu.cycles += 4;
	}
	
//	LD C, d8
//	2  8
//	- - - -
	public static void LD_C_d8() {
		Cpu.C = Cpu.fetch();
		Cpu.cycles += 8;
	}
	
//	RRCA
//	1  4
//	0 0 0 C
	public static void RRCA() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		if ((Cpu.A & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.A = ((Cpu.A << 7) | (Cpu.A >> 1)) & 0xff; // Rotate to right
		Cpu.cycles += 4;
	}
	
//	STOP d8
//	2  4
//	- - - -
	public static void STOP() {
		Cpu.cycles += 4;
	}
	
//	LD DE, d16
//	3  12
//	- - - -
	public static void LD_DE_d16() {
		Cpu.E = Cpu.fetch();
		Cpu.D = Cpu.fetch();
		Cpu.cycles += 12;
	}
	
//	LD (DE), A
//	1  8
//	- - - -
	public static void LD_DE_A() {
		Main.mmu.setByte(Cpu.getDE(), Cpu.A);
		Cpu.cycles += 8;
	}
	
//	INC DE
//	1  8
//	- - - -
	public static void INC_DE() {
		int aux = Cpu.getDE();
		aux += 1;
		Cpu.D = ((aux >> 8) & 0xff); // Set high 8 bits
		Cpu.E = (aux & 0xff); // Set low 8 bits
		Cpu.cycles += 8;
	}
	
//	INC D
//	1  4
//	Z 0 H -
	public static void INC_D() {
		Cpu.checkHalfCarry8bit(Cpu.D,1); // Check if there is Half Carry before the operation
		Cpu.D = (Cpu.D + 1) & 0xff;
		Cpu.checkZero8bit(Cpu.D);
		Cpu.resetFlagN();
		Cpu.cycles += 4;
	}

//	DEC D
//	1  4
//	Z 1 H -
	public static void DEC_D() {
		Cpu.checkHalfCarry8bitSub(Cpu.D,1); // Check if there is Half Carry before the operation
		Cpu.D = (Cpu.D - 1) & 0xff;
		Cpu.checkZero8bit(Cpu.D);
		Cpu.setFlagN();
		Cpu.cycles += 4;
	}
	
//	LD D, d8
//	2  8
//	- - - -
	public static void LD_D_d8() {
		Cpu.D = Cpu.fetch();
		Cpu.cycles += 8;
	}
	
//	RLA
//	1  4
//	0 0 0 C
	public static void RLA() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = Cpu.getFlagC();
		
		// Set carry flag
		if ((Cpu.A & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.A = (Cpu.A << 1) & 0xff; // Rotate to the right
		Cpu.A |= c;
		Cpu.cycles += 4;
	}

//	JR r8
//	2  12
//	- - - -
	public static void JR_s8() {
		int s8 = Cpu.fetchSigned();
		Cpu.PC = (Cpu.PC + s8) & 0xffff;
		Cpu.cycles += 12;
	}
	
//	ADD HL, DE
//	1  8
//	- 0 H C
	public static void ADD_HL_DE() {
		Cpu.resetFlagN();
		Cpu.checkHalfCarry16bit(Cpu.getHL(), Cpu.getDE());
		int res = (Cpu.getHL() + Cpu.getDE());
		Cpu.checkCarry16bit(res);
		Cpu.L = res & 0xff;
		Cpu.H = (res >> 8) & 0xff;
		Cpu.cycles += 8;
	}

//	LD A, (DE)
//	1  8
//	- - - -
	public static void LD_A_DE() {
		Cpu.A = Main.mmu.getByte(Cpu.getDE());
		Cpu.cycles += 8;
	}

//	DEC DE
//	1  8
//	- - - -
	public static void DEC_DE() {
		int aux = Cpu.getDE();
		aux -= 1;
		Cpu.D = ((aux >> 8) & 0xff); // Set high 8 bits
		Cpu.E = (aux & 0xff); // Set low 8 bits
		Cpu.cycles += 8;
	}

//	INC E
//	1  4
//	Z 0 H -
	public static void INC_E() {
		Cpu.checkHalfCarry8bit(Cpu.E,1); // Check if there is Half Carry before the operation
		Cpu.E = (Cpu.E + 1) & 0xff;
		Cpu.checkZero8bit(Cpu.E);
		Cpu.resetFlagN();
		Cpu.cycles += 4;
	}

//	DEC E
//	1  4
//	Z 1 H -
	public static void DEC_E() {
		Cpu.checkHalfCarry8bitSub(Cpu.E,1); // Check if there is Half Carry before the operation
		Cpu.E = (Cpu.E - 1) & 0xff;
		Cpu.checkZero8bit(Cpu.E);
		Cpu.setFlagN();
		Cpu.cycles += 4;
	}

//	LD E, d8
//	2  8
//	- - - -
	public static void LD_E_d8() {
		Cpu.E = Cpu.fetch();
		Cpu.cycles += 8;
	}
	
//	RRA
//	1  4
//	0 0 0 C
	public static void RRA() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = (Cpu.getFlagC() << 7);

		// Set carry flag
		if ((Cpu.A & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.A = (Cpu.A >> 1) & 0xff; // Rotate to the right
		Cpu.A |= c;
		Cpu.cycles += 4;
	}
	
	
//	JR NZ, r8
//	2  12/8
//	- - - -
	public static void JR_NZ_s8() {
		if (Cpu.getFlagZ() == 0) {
			JR_s8();
		} else {
			Cpu.fetch();
			Cpu.cycles += 8;
		}
	}

//	LD HL, d16
//	3  12
//	- - - -
	public static void LD_HL_d16() {
		Cpu.L = Cpu.fetch();
		Cpu.H = Cpu.fetch();
		Cpu.cycles += 12;
	}

//	LD (HL+), A
//	1  8
//	- - - -
	public static void LD_HLinc_A() {
		Main.mmu.setByte(Cpu.getHL(), Cpu.A);
		
		// TO DO: que pasa cuando incremento enteros paso los 8 bits y luego los paso a la mem?
		// en principio parece no generar problemas.
		int aux  = Cpu.getHL() + 1;
		Cpu.L = aux & 0xFF; // Set the lower 8 bits
		Cpu.H = (aux >> 8) & 0xff; // Set the higher 8 bits
		Cpu.cycles += 8;
	}

//	INC HL
//	1  8
//	- - - -
	public static void INC_HL() {
		int aux = Cpu.getHL();
		aux += 1;
		Cpu.H = ((aux >> 8) & 0xff); // Set high 8 bits
		Cpu.L = (aux & 0xff); // Set low 8 bits
		Cpu.cycles += 8;
	}

//	INC H
//	1  4
//	Z 0 H -
	public static void INC_H() {
		Cpu.checkHalfCarry8bit(Cpu.H,1); // Check if there is Half Carry before the operation
		Cpu.H = (Cpu.H + 1) & 0xff;
		Cpu.checkZero8bit(Cpu.H);
		Cpu.resetFlagN();
		Cpu.cycles += 4;
	}

//	DEC H
//	1  4
//	Z 1 H -
	public static void DEC_H() {
		Cpu.checkHalfCarry8bitSub(Cpu.H,1); // Check if there is Half Carry before the operation
		Cpu.H = (Cpu.H - 1) & 0xff;
		Cpu.checkZero8bit(Cpu.H);
		Cpu.setFlagN();
		Cpu.cycles += 4;
	}

//	LD H, d8
//	2  8
//	- - - -
	public static void LD_H_d8() {
		Cpu.H = Cpu.fetch();
		Cpu.cycles += 8;
	}
	
//	DAA
//	1  4
//	Z - 0 C
	public static void DAA() {
		// note: assumes a is a uint8_t and wraps from 0xff to 0
		if (Cpu.getFlagN() == 0) {  // after an addition, adjust if (half-)carry occurred or if result is out of bounds
		  
			if ((Cpu.getFlagC() == 1) || (Cpu.A > 0x99)) {
			  Cpu.A += 0x60;
			  Cpu.setFlagC();
		  }
		  
		  if ((Cpu.getFlagH() == 1) || ((Cpu.A & 0x0f) > 0x09)) {
			  Cpu.A += 0x6; 
		  }
		
		} else {  // after a subtraction, only adjust if (half-)carry occurred
			
			if (Cpu.getFlagC() == 1) {
				Cpu.A -= 0x60;
			}
			
			if (Cpu.getFlagH() == 1) {
				Cpu.A -= 0x6;
			}
			
		}
		
		Cpu.A &= 0xff;
		
		// these flags are always updated
		Cpu.checkZero8bit(Cpu.A);; // the usual z flag
		Cpu.resetFlagH(); // h flag is always cleared
		
		Cpu.cycles += 4;
	}

//	JR Z, r8
//	2  12/8
//	- - - -
	public static void JR_Z_s8() {
		if (Cpu.getFlagZ() == 1) {
			JR_s8();
		} else {
			Cpu.fetch();
			Cpu.cycles += 8;
		}
	}
	
//	ADD HL, HL
//	1  8
//	- 0 H C
	public static void ADD_HL_HL() {
		Cpu.resetFlagN();
		Cpu.checkHalfCarry16bit(Cpu.getHL(), Cpu.getHL());
		int res = (Cpu.getHL() + Cpu.getHL());
		Cpu.checkCarry16bit(res);
		Cpu.L = res & 0xff;
		Cpu.H = (res >> 8) & 0xff;
		Cpu.cycles += 8;
	}

//	LD A, (HL+)
//	1  8
//	- - - -
	public static void LD_A_HLinc() {
		Cpu.A = Main.mmu.getByte(Cpu.getHL());
		int aux  = Cpu.getHL() + 1;
		Cpu.L = aux & 0xFF; // Set the lower 8 bits
		Cpu.H = (aux >> 8) & 0xff; // Set the higher 8 bits
		Cpu.cycles += 8;
	}

//	DEC HL
//	1  8
//	- - - -
	public static void DEC_HL() {
		int aux = Cpu.getHL();
		aux -= 1;
		Cpu.H = ((aux >> 8) & 0xff); // Set high 8 bits
		Cpu.L = (aux & 0xff); // Set low 8 bits
		Cpu.cycles += 8;
	}

//	INC L
//	1  4
//	Z 0 H -
	public static void INC_L() {
		Cpu.checkHalfCarry8bit(Cpu.L,1); // Check if there is Half Carry before the operation
		Cpu.L = (Cpu.L + 1) & 0xff;
		Cpu.checkZero8bit(Cpu.L);
		Cpu.resetFlagN();
		Cpu.cycles += 4;
	}

//	DEC L
//	1  4
//	Z 1 H -
	public static void DEC_L() {
		Cpu.checkHalfCarry8bitSub(Cpu.L,1); // Check if there is Half Carry before the operation
		Cpu.L = (Cpu.L - 1) & 0xff;
		Cpu.checkZero8bit(Cpu.L);
		Cpu.setFlagN();
		Cpu.cycles += 4;
	}

//	LD L, d8
//	2  8
//	- - - -
	public static void LD_L_d8() {
		Cpu.L = Cpu.fetch();
		Cpu.cycles += 8;
	}
	
//	CPL
//	1  4
//	- 1 1 -
	public static void CPL() {
		Cpu.setFlagN();
		Cpu.setFlagH();
		Cpu.A = (~Cpu.A) & 0xff;
		Cpu.cycles += 4;
	}

//	JR NC, r8
//	2  12/8
//	- - - -
	public static void JR_NC_s8() {
		if (Cpu.getFlagC() == 0) {
			JR_s8();
		} else {
			Cpu.fetch();
			Cpu.cycles += 8;
		}
	}

//	LD SP, d16
//	3  12
//	- - - -
	public static void LD_SP_d16() {
		int value = Cpu.fetch() + (Cpu.fetch() << 8);
		Cpu.SP = value & 0xffff;
		Cpu.cycles += 12;
	}

//	LD (HL-), A
//	1  8
//	- - - -
	public static void LD_HLdec_A() {
		Main.mmu.setByte(Cpu.getHL(), Cpu.A);
		
		// TO DO: que pasa cuando decremento enteros y luego los paso a la mem?
		// en principio parece no generar problemas.
		int aux  = Cpu.getHL() - 1;
		Cpu.L = aux & 0xFF; // Set the lower 8 bits
		Cpu.H = (aux >> 8) & 0xff; // Set the higher 8 bits
		Cpu.cycles += 8;
	}

//	INC SP
//	1  8
//	- - - -
	public static void INC_SP() {
		Cpu.SP += 1;
		Cpu.cycles += 8;
	}

//	INC (HL)
//	1  12
//	Z 0 H -
	public static void INC_HLmem() {
		Cpu.checkHalfCarry8bit(Main.mmu.getByte(Cpu.getHL()),1); // Check if there is Half Carry before the operation
		Main.mmu.setByte(Cpu.getHL(), (Main.mmu.getByte(Cpu.getHL()) + 1) & 0xff);
		Cpu.checkZero8bit(Main.mmu.getByte(Cpu.getHL()));
		Cpu.resetFlagN();
		Cpu.cycles += 12;
	}

//	DEC (HL)
//	1  12
//	Z 1 H -
	public static void DEC_HLmem() {
		Cpu.checkHalfCarry8bitSub(Main.mmu.getByte(Cpu.getHL()),1); // Check if there is Half Carry before the operation
		Main.mmu.setByte(Cpu.getHL(), (Main.mmu.getByte(Cpu.getHL()) - 1) & 0xff);
		Cpu.checkZero8bit(Main.mmu.getByte(Cpu.getHL()));
		Cpu.setFlagN();
		Cpu.cycles += 12;
	}

//	LD (HL), d8
//	2  12
//	- - - -
	public static void LD_HL_d8() {
		Main.mmu.setByte(Cpu.getHL(), Cpu.fetch());
		Cpu.cycles += 12;
	}
	
//	SCF
//	1  4
//	- 0 0 1
	public static void SCF() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.setFlagC();
		Cpu.cycles += 4;
	}

//	JR C, r8
//	2  12/8
//	- - - -
	public static void JR_C_s8() {
		if (Cpu.getFlagC() == 1) {
			JR_s8();
		} else {
			Cpu.fetch();
			Cpu.cycles += 8;
		}
	}
	
//	ADD HL, SP
//	1  8
//	- 0 H C
	public static void ADD_HL_SP() {
		Cpu.resetFlagN();
		int res = Cpu.getHL() + Cpu.SP;
		Cpu.checkHalfCarry16bit(Cpu.getHL(), Cpu.SP);
		Cpu.checkCarry16bit(res);
		Cpu.L = res & 0xff;
		Cpu.H = (res >> 8) & 0xff;
		Cpu.cycles += 8;
	}

//	LD A, (HL-)
//	1  8
//	- - - -
	public static void LD_A_HLdec() {
		Cpu.A = Main.mmu.getByte(Cpu.getHL());
		int aux  = Cpu.getHL() - 1;
		Cpu.L = aux & 0xFF; // Set the lower 8 bits
		Cpu.H = (aux >> 8) & 0xff; // Set the higher 8 bits
		Cpu.cycles += 8;
	}

//	DEC SP
//	1  8
//	- - - -
	public static void DEC_SP() {
		Cpu.SP -= 1;
		Cpu.cycles += 8;
	}

//	INC A
//	1  4
//	Z 0 H -
	public static void INC_A() {
		Cpu.checkHalfCarry8bit(Cpu.A,1); // Check if there is Half Carry before the operation
		Cpu.A = (Cpu.A + 1) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.cycles += 4;
	}

//	DEC A
//	1  4
//	Z 1 H -
	public static void DEC_A() {
		Cpu.checkHalfCarry8bitSub(Cpu.A,1); // Check if there is Half Carry before the operation
		Cpu.A = (Cpu.A - 1) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
		Cpu.cycles += 4;
	}

//	LD A, d8
//	2  8
//	- - - -
	public static void LD_A_d8() {
		Cpu.A = Cpu.fetch();
		Cpu.cycles += 8;
	}

//	CCF
//	1  4
//	- 0 0 C
	public static void CFF() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		if (Cpu.getFlagC() == 0) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		Cpu.cycles += 4;
	}
	
//	LD B, B
	public static void LD_B_B() {
		Cpu.cycles += 4;
	}
	
//	LD B, C
	public static void LD_B_C() {
		Cpu.B = Cpu.C;
		Cpu.cycles += 4;
	}

//	LD B, D
	public static void LD_B_D() {
		Cpu.B = Cpu.D;
		Cpu.cycles += 4;
	}
	
//	LD B, E
	public static void LD_B_E() {
		Cpu.B = Cpu.E;
		Cpu.cycles += 4;
	}
	
//	LD B, H
	public static void LD_B_H() {
		Cpu.B = Cpu.H;
		Cpu.cycles += 4;
	}
	
//	LD B, L
	public static void LD_B_L() {
		Cpu.B = Cpu.L;
		Cpu.cycles += 4;
	}

//	 LD B, (HL)
	public static void LD_B_HL() {
		Cpu.B = Main.mmu.getByte(Cpu.getHL());
		Cpu.cycles += 8;
	}

//	LD B, A
	public static void LD_B_A() {
		Cpu.B = Cpu.A;
		Cpu.cycles += 4;
	}
	
//	LD C, B
	public static void LD_C_B() {
		Cpu.C = Cpu.B;
		Cpu.cycles += 4;
	}
	
//	LD C, C
	public static void LD_C_C() {
		Cpu.cycles += 4;
	}
	
//	LD C, D
	public static void LD_C_D() {
		Cpu.C = Cpu.D;
		Cpu.cycles += 4;
	}
	
//	LD C, E
	public static void LD_C_E() {
		Cpu.C = Cpu.E;
		Cpu.cycles += 4;
	}
	
//	LD C, H
	public static void LD_C_H() {
		Cpu.C = Cpu.H;
		Cpu.cycles += 4;
	}
	
//	LD C, L
	public static void LD_C_L() {
		Cpu.C = Cpu.L;
		Cpu.cycles += 4;
	}
	
//	 LD C, (HL)
	public static void LD_C_HL() {
		Cpu.C = Main.mmu.getByte(Cpu.getHL());
		Cpu.cycles += 8;
	}

//	LD C, A
	public static void LD_C_A() {
		Cpu.C = Cpu.A;
		Cpu.cycles += 4;
	}
	
//	LD D, B
	public static void LD_D_B() {
		Cpu.D = Cpu.B;
		Cpu.cycles += 4;
	}
	
//	LD D, C
	public static void LD_D_C() {
		Cpu.D = Cpu.C;
		Cpu.cycles += 4;
	}
	
//	LD D, D
	public static void LD_D_D() {
		Cpu.cycles += 4;
	}
	
//	LD D, E
	public static void LD_D_E() {
		Cpu.D = Cpu.E;
		Cpu.cycles += 4;
	}
	
//	LD D, H
	public static void LD_D_H() {
		Cpu.D = Cpu.H;
		Cpu.cycles += 4;
	}
	
//	LD D, L
	public static void LD_D_L() {
		Cpu.D = Cpu.L;
		Cpu.cycles += 4;
	}

//	LD D, (HL)
	public static void LD_D_HL() {
		Cpu.D = Main.mmu.getByte(Cpu.getHL());
		Cpu.cycles += 8;
	}

//	LD D, A
	public static void LD_D_A() {
		Cpu.D = Cpu.A;
		Cpu.cycles += 4;
	}
	
//	LD E, B
	public static void LD_E_B() {
		Cpu.E = Cpu.B;
		Cpu.cycles += 4;
	}
	
//	LD E, C
	public static void LD_E_C() {
		Cpu.E = Cpu.C;
		Cpu.cycles += 4;
	}
	
//	LD E, D
	public static void LD_E_D() {
		Cpu.E = Cpu.D;
		Cpu.cycles += 4;
	}
	
//	LD E, E
	public static void LD_E_E() {
		Cpu.cycles += 4;
	}
	
//	LD E, H
	public static void LD_E_H() {
		Cpu.E = Cpu.H;
		Cpu.cycles += 4;
	}
	
//	LD E, L
	public static void LD_E_L() {
		Cpu.E = Cpu.L;
		Cpu.cycles += 4;
	}

//	LD E, (HL)
	public static void LD_E_HL() {
		Cpu.E = Main.mmu.getByte(Cpu.getHL());
		Cpu.cycles += 8;
	}

//	LD E, A
	public static void LD_E_A() {
		Cpu.E = Cpu.A;
		Cpu.cycles += 4;
	}
		
//	LD H, B
	public static void LD_H_B() {
		Cpu.H = Cpu.B;
		Cpu.cycles += 4;
	}
	
//	LD H, C
	public static void LD_H_C() {
		Cpu.H = Cpu.C;
		Cpu.cycles += 4;
	}
	
//	LD H, D
	public static void LD_H_D() {
		Cpu.H = Cpu.D;
		Cpu.cycles += 4;
	}
	
//	LD H, E
	public static void LD_H_E() {
		Cpu.H = Cpu.E;
		Cpu.cycles += 4;
	}
	
//	LD H, H
	public static void LD_H_H() {
		Cpu.cycles += 4;
	}
	
//	LD H, L
	public static void LD_H_L() {
		Cpu.H = Cpu.L;
		Cpu.cycles += 4;
	}

//	LD H, (HL)
	public static void LD_H_HL() {
		Cpu.H = Main.mmu.getByte(Cpu.getHL());
		Cpu.cycles += 8;
	}

//	LD H, A
	public static void LD_H_A() {
		Cpu.H = Cpu.A;
		Cpu.cycles += 4;
	}
	
//	LD L, B
	public static void LD_L_B() {
		Cpu.L = Cpu.B;
		Cpu.cycles += 4;
	}
	
//	LD L, C
	public static void LD_L_C() {
		Cpu.L = Cpu.C;
		Cpu.cycles += 4;
	}
	
//	LD L, D
	public static void LD_L_D() {
		Cpu.L = Cpu.D;
		Cpu.cycles += 4;
	}
	
//	LD L, E
	public static void LD_L_E() {
		Cpu.L = Cpu.E;
		Cpu.cycles += 4;
	}
	
//	LD L, H
	public static void LD_L_H() {
		Cpu.L = Cpu.H;
		Cpu.cycles += 4;
	}
	
//	LD L, L
	public static void LD_L_L() {
		Cpu.cycles += 4;
	}

//	LD L, (HL)
	public static void LD_L_HL() {
		Cpu.L = Main.mmu.getByte(Cpu.getHL());
		Cpu.cycles += 8;
	}

//	LD L, A
	public static void LD_L_A() {
		Cpu.L = Cpu.A;
		Cpu.cycles += 4;
	}
	
//	LD (HL), B
	public static void LD_HL_B() {
		Main.mmu.setByte(Cpu.getHL(), Cpu.B);
		Cpu.cycles += 8;
	}
	
//	LD (HL), C
	public static void LD_HL_C() {
		Main.mmu.setByte(Cpu.getHL(), Cpu.C);
		Cpu.cycles += 8;
	}
	
//	LD (HL), D
	public static void LD_HL_D() {
		Main.mmu.setByte(Cpu.getHL(), Cpu.D);
		Cpu.cycles += 8;
	}
	
//	LD (HL), E
	public static void LD_HL_E() {
		Main.mmu.setByte(Cpu.getHL(), Cpu.E);
		Cpu.cycles += 8;
	}
	
//	LD (HL), H
	public static void LD_HL_H() {
		Main.mmu.setByte(Cpu.getHL(), Cpu.H);
		Cpu.cycles += 8;
	}
	
//	LD (HL), L
	public static void LD_HL_L() {
		Main.mmu.setByte(Cpu.getHL(), Cpu.L);
		Cpu.cycles += 8;
	}
	
//	HALT
	public static void HALT() {
        int IF = Main.mmu.getByte(0xff0f);
        int IE = Main.mmu.getByte(0xffff);

        // Exit hell if an interrupt is valid to be serviced
        if ((IF & IE & 0x1f) > 0) {
            Cpu.haltbugAtm = !(Cpu.HALT || Cpu.IME);
            Cpu.HALT = false;
        }
         // If we cannot exit hell, stay in hell.
        else {
            Cpu.HALT = true;

            Cpu.PC--;
            Cpu.PC &= 0xffff;
        }
        
		Cpu.cycles += 4;
	}
	
//	LD (HL), A
	public static void LD_HL_A() {
		Main.mmu.setByte(Cpu.getHL(), Cpu.A);
		Cpu.cycles += 8;
	}

//	LD A, B
	public static void LD_A_B() {
		Cpu.A = Cpu.B;
		Cpu.cycles += 4;
	}
	
//	LD A, C
	public static void LD_A_C() {
		Cpu.A = Cpu.C;
		Cpu.cycles += 4;
	}
	
//	LD A, D
	public static void LD_A_D() {
		Cpu.A = Cpu.D;
		Cpu.cycles += 4;
	}
	
//	LD A, E
	public static void LD_A_E() {
		Cpu.A = Cpu.E;
		Cpu.cycles += 4;
	}
	
//	LD A, H
	public static void LD_A_H() {
		Cpu.A = Cpu.H;
		Cpu.cycles += 4;
	}
	
//	LD A, L
	public static void LD_A_L() {
		Cpu.A = Cpu.L;
		Cpu.cycles += 4;
	}

//	LD A, (HL)
	public static void LD_A_HL() {
		Cpu.A = Main.mmu.getByte(Cpu.getHL());
		Cpu.cycles += 8;
	}
	
//	LD A, A
	public static void LD_A_A() {
		Cpu.cycles += 4;
	}

//	ADD A, B
//	Z 0 H C
	public static void ADD_A_B() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.B);
		Cpu.checkCarry8bit(Cpu.A + Cpu.B);
		Cpu.A = (Cpu.A + Cpu.B) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.cycles += 4;
	}
	
//	ADD A, C
//	Z 0 H C
	public static void ADD_A_C() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.C);
		Cpu.checkCarry8bit(Cpu.A + Cpu.C);
		Cpu.A = (Cpu.A + Cpu.C) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.cycles += 4;
	}
	
//	ADD A, D
//	Z 0 H C
	public static void ADD_A_D() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.D);
		Cpu.checkCarry8bit(Cpu.A + Cpu.D);
		Cpu.A = (Cpu.A + Cpu.D) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.cycles += 4;
	}
	
//	ADD A, E
//	Z 0 H C
	public static void ADD_A_E() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.E);
		Cpu.checkCarry8bit(Cpu.A + Cpu.E);
		Cpu.A = (Cpu.A + Cpu.E) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.cycles += 4;
	}
	
//	ADD A, H
//	Z 0 H C
	public static void ADD_A_H() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.H);
		Cpu.checkCarry8bit(Cpu.A + Cpu.H);
		Cpu.A = (Cpu.A + Cpu.H) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.cycles += 4;
	}
	
//	ADD A, L
//	Z 0 H C
	public static void ADD_A_L() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.L);
		Cpu.checkCarry8bit(Cpu.A + Cpu.L);
		Cpu.A = (Cpu.A + Cpu.L) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.cycles += 4;
	}
	
//	ADD A, (HL)
//	Z 0 H C
	public static void ADD_A_HL() {
		Cpu.checkHalfCarry8bit(Cpu.A, Main.mmu.getByte(Cpu.getHL()));
		Cpu.checkCarry8bit(Cpu.A + Main.mmu.getByte(Cpu.getHL()));
		Cpu.A = (Cpu.A + Main.mmu.getByte(Cpu.getHL())) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		
		Cpu.resetFlagN();
		Cpu.cycles += 8;
	}

//	ADD A, A
//	Z 0 H C
	public static void ADD_A_A() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.A);
		Cpu.checkCarry8bit(Cpu.A + Cpu.A);
		Cpu.A = (Cpu.A + Cpu.A) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.cycles += 4;
	}
	
//	ADC A, B
//	Z 0 H C
	public static void ADC_A_B() {
		int val = Cpu.B + Cpu.getFlagC();
		
		int sum = Cpu.A + val;
		int res = sum & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.resetFlagH();
		Cpu.resetFlagN();
		if ((Cpu.A & 0xf) + (Cpu.B & 0xf) + Cpu.getFlagC() > 0xf) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bit(sum);

		Cpu.A = res;
		Cpu.cycles += 4;
	}
	
//	ADC A, C
//	Z 0 H C
	public static void ADC_A_C() {
		int val = Cpu.C + Cpu.getFlagC();
		
		int sum = Cpu.A + val;
		int res = sum & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.resetFlagH();
		Cpu.resetFlagN();
		if ((Cpu.A & 0xf) + (Cpu.C & 0xf) + Cpu.getFlagC() > 0xf) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bit(sum);

		Cpu.A = res;
		Cpu.cycles += 4;
	}
	
//	ADC A, D
//	Z 0 H C
	public static void ADC_A_D() {
		int val = Cpu.D + Cpu.getFlagC();
		
		int sum = Cpu.A + val;
		int res = sum & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.resetFlagH();
		Cpu.resetFlagN();
		if ((Cpu.A & 0xf) + (Cpu.D & 0xf) + Cpu.getFlagC() > 0xf) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bit(sum);

		Cpu.A = res;
		Cpu.cycles += 4;
	}
	
//	ADC A, E
//	Z 0 H C
	public static void ADC_A_E() {
		int val = Cpu.E + Cpu.getFlagC();
		
		int sum = Cpu.A + val;
		int res = sum & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.resetFlagH();
		Cpu.resetFlagN();
		if ((Cpu.A & 0xf) + (Cpu.E & 0xf) + Cpu.getFlagC() > 0xf) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bit(sum);

		Cpu.A = res;
		Cpu.cycles += 4;
	}
	
//	ADC A, H
//	Z 0 H C
	public static void ADC_A_H() {
		int val = Cpu.H + Cpu.getFlagC();
		
		int sum = Cpu.A + val;
		int res = sum & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.resetFlagH();
		Cpu.resetFlagN();
		if ((Cpu.A & 0xf) + (Cpu.H & 0xf) + Cpu.getFlagC() > 0xf) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bit(sum);

		Cpu.A = res;
		Cpu.cycles += 4;
	}
	
//	ADC A, L
//	Z 0 H C
	public static void ADC_A_L() {
		int val = Cpu.L+ Cpu.getFlagC();
		
		int sum = Cpu.A + val;
		int res = sum & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.resetFlagH();
		Cpu.resetFlagN();
		if ((Cpu.A & 0xf) + (Cpu.L & 0xf) + Cpu.getFlagC() > 0xf) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bit(sum);

		Cpu.A = res;
		Cpu.cycles += 4;
	}
	
//	ADC A, (HL)
//	Z 0 H C
	public static void ADC_A_HL() {
		int val = Main.mmu.getByte(Cpu.getHL()) + Cpu.getFlagC();
		
		int sum = Cpu.A + val;
		int res = sum & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.resetFlagH();
		Cpu.resetFlagN();
		if ((Cpu.A & 0xf) + (Main.mmu.getByte(Cpu.getHL()) & 0xf) + Cpu.getFlagC() > 0xf) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bit(sum);

		Cpu.A = res;
		Cpu.cycles += 8;
	}

//	ADC A, A
//	Z 0 H C
	public static void ADC_A_A() {
		int val = Cpu.A + Cpu.getFlagC();
		
		int sum = Cpu.A + val;
		int res = sum & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.resetFlagH();
		Cpu.resetFlagN();
		if ((Cpu.A & 0xf) + (Cpu.A & 0xf) + Cpu.getFlagC() > 0xf) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bit(sum);

		Cpu.A = res;
		Cpu.cycles += 4;
	}
	
//	SUB B
//	Z 1 H C
	public static void SUB_A_B() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.B);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.B);
		Cpu.A = (Cpu.A - Cpu.B) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
		Cpu.cycles += 4;
	}
	
//	SUB C
//	Z 1 H C
	public static void SUB_A_C() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.C);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.C);
		Cpu.A = (Cpu.A - Cpu.C) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
		Cpu.cycles += 4;
	}
	
//	SUB D
//	Z 1 H C
	public static void SUB_A_D() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.D);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.D);
		Cpu.A = (Cpu.A - Cpu.D) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
		Cpu.cycles += 4;
	}
	
//	SUB E
//	Z 1 H C
	public static void SUB_A_E() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.E);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.E);
		Cpu.A = (Cpu.A - Cpu.E) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
		Cpu.cycles += 4;
	}
	
//	SUB H
//	Z 1 H C
	public static void SUB_A_H() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.H);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.H);
		Cpu.A = (Cpu.A - Cpu.H) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
		Cpu.cycles += 4;
	}
	
//	SUB L
//	Z 1 H C
	public static void SUB_A_L() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.L);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.L);
		Cpu.A = (Cpu.A - Cpu.L) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
		Cpu.cycles += 4;
	}
	
//	SUB (HL)
//	Z 1 H C
	public static void SUB_A_HL() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Main.mmu.getByte(Cpu.getHL()));
		Cpu.checkCarry8bitSub(Cpu.A, Main.mmu.getByte(Cpu.getHL()));
		Cpu.A = (Cpu.A - Main.mmu.getByte(Cpu.getHL())) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
		Cpu.cycles += 8;
	}
	
//	SUB A
//	1 1 0 0
	public static void SUB_A_A() {
		Cpu.A = (Cpu.A - Cpu.A) & 0xff;
		Cpu.setFlagZ();
		Cpu.setFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	SBC A, B
//	Z 1 H C
	public static void SBC_A_B() {
		int val = Cpu.B + Cpu.getFlagC();
		
		int res = (Cpu.A - val) & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.setFlagN();
		Cpu.resetFlagH();
		if ((Cpu.A & 0xf) < ((Cpu.B & 0xf) + Cpu.getFlagC())) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bitSub(Cpu.A, val);

		Cpu.A = res;
		Cpu.cycles += 4;
	}
	
//	SBC A, C
//	Z 1 H C
	public static void SBC_A_C() {
		int val = Cpu.C + Cpu.getFlagC();
		
		int res = (Cpu.A - val) & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.setFlagN();
		Cpu.resetFlagH();
		if ((Cpu.A & 0xf) < ((Cpu.C & 0xf) + Cpu.getFlagC())) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bitSub(Cpu.A, val);

		Cpu.A = res;
		Cpu.cycles += 4;
	}
	
//	SBC A, D
//	Z 1 H C
	public static void SBC_A_D() {
		int val = Cpu.D + Cpu.getFlagC();
		
		int res = (Cpu.A - val) & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.setFlagN();
		Cpu.resetFlagH();
		if ((Cpu.A & 0xf) < ((Cpu.D & 0xf) + Cpu.getFlagC())) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bitSub(Cpu.A, val);

		Cpu.A = res;
		Cpu.cycles += 4;
	}
	
//	SBC A, E
//	Z 1 H C
	public static void SBC_A_E() {
		int val = Cpu.E + Cpu.getFlagC();
		
		int res = (Cpu.A - val) & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.setFlagN();
		Cpu.resetFlagH();
		if ((Cpu.A & 0xf) < ((Cpu.E & 0xf) + Cpu.getFlagC())) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bitSub(Cpu.A, val);

		Cpu.A = res;
		Cpu.cycles += 4;
	}
	
//	SBC A, H
//	Z 1 H C
	public static void SBC_A_H() {
		int val = Cpu.H + Cpu.getFlagC();
		
		int res = (Cpu.A - val) & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.setFlagN();
		Cpu.resetFlagH();
		if ((Cpu.A & 0xf) < ((Cpu.H & 0xf) + Cpu.getFlagC())) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bitSub(Cpu.A, val);

		Cpu.A = res;
		Cpu.cycles += 4;
	}
	
//	SBC A, L
//	Z 1 H C
	public static void SBC_A_L() {
		int val = Cpu.L + Cpu.getFlagC();
		
		int res = (Cpu.A - val) & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.setFlagN();
		Cpu.resetFlagH();
		if ((Cpu.A & 0xf) < ((Cpu.L & 0xf) + Cpu.getFlagC())) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bitSub(Cpu.A, val);

		Cpu.A = res;
		Cpu.cycles += 4;
	}
	
//	SBC A, (HL)
//	Z 1 H C
	public static void SBC_A_HL() {
		int val = Main.mmu.getByte(Cpu.getHL()) + Cpu.getFlagC();
		
		int res = (Cpu.A - val) & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.setFlagN();
		Cpu.resetFlagH();
		if ((Cpu.A & 0xf) < ((Main.mmu.getByte(Cpu.getHL()) & 0xf) + Cpu.getFlagC())) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bitSub(Cpu.A, val);

		Cpu.A = res;
		Cpu.cycles += 8;
	}
	
//	SBC A, A
//	Z 1 H -
	public static void SBC_A_A() {
		int val = Cpu.A + Cpu.getFlagC();
		
		int res = (Cpu.A - val) & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.setFlagN();
		Cpu.resetFlagH();
		if ((Cpu.A & 0xf) < ((Cpu.A & 0xf) + Cpu.getFlagC())) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bitSub(Cpu.A, val);

		Cpu.A = res;
		Cpu.cycles += 4;
	}	
	
//	 AND B
//	 Z 0 1 0
	public static void AND_B() {
		Cpu.A = Cpu.A & Cpu.B;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}	
	
//	 AND C
//	 Z 0 1 0
	public static void AND_C() {
		Cpu.A = Cpu.A & Cpu.C;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	 AND D
//	 Z 0 1 0
	public static void AND_D() {
		Cpu.A = Cpu.A & Cpu.D;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	AND E
//	Z 0 1 0
	public static void AND_E() {
		Cpu.A = Cpu.A & Cpu.E;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	AND H
//	Z 0 1 0
	public static void AND_H() {
		Cpu.A = Cpu.A & Cpu.H;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	AND L
//	Z 0 1 0
	public static void AND_L() {
		Cpu.A = Cpu.A & Cpu.L;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	AND (HL)
//	Z 0 1 0
	public static void AND_HL() {
		Cpu.A = Cpu.A & Main.mmu.getByte(Cpu.getHL());
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 8;
	}
	
//	AND A
//	Z 0 1 0
	public static void AND_A() {
		Cpu.A = Cpu.A & Cpu.A;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	XOR B
//	Z 0 0 0
	public static void XOR_B() {
		Cpu.A = Cpu.A ^ Cpu.B;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	XOR C
//	Z 0 0 0
	public static void XOR_C() {
		Cpu.A = Cpu.A ^ Cpu.C;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}

//	XOR D
//	Z 0 0 0
	public static void XOR_D() {
		Cpu.A = Cpu.A ^ Cpu.D;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	XOR E
//	Z 0 0 0
	public static void XOR_E() {
		Cpu.A = Cpu.A ^ Cpu.E;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	XOR H
//	Z 0 0 0
	public static void XOR_H() {
		Cpu.A = Cpu.A ^ Cpu.H;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	XOR L
//	Z 0 0 0
	public static void XOR_L() {
		Cpu.A = Cpu.A ^ Cpu.L;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	XOR (HL)
//	Z 0 0 0
	public static void XOR_HL() {
		Cpu.A = Cpu.A ^ Main.mmu.getByte(Cpu.getHL());
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 8;
	}
	
//	XOR A
//	1 0 0 0
	public static void XOR_A() {
		Cpu.A = Cpu.A ^ Cpu.A;
		Cpu.setFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	OR B
//	Z 0 0 0
	public static void OR_B() {
		Cpu.A = Cpu.A | Cpu.B;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	OR C
//	Z 0 0 0
	public static void OR_C() {
		Cpu.A = Cpu.A | Cpu.C;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	OR D
//	Z 0 0 0
	public static void OR_D() {
		Cpu.A = Cpu.A | Cpu.D;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	OR E
//	Z 0 0 0
	public static void OR_E() {
		Cpu.A = Cpu.A | Cpu.E;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	OR H
//	Z 0 0 0
	public static void OR_H() {
		Cpu.A = Cpu.A | Cpu.H;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	OR L
//	Z 0 0 0
	public static void OR_L() {
		Cpu.A = Cpu.A | Cpu.L;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	OR (HL)
//	Z 0 0 0
	public static void OR_HL() {
		Cpu.A = Cpu.A | Main.mmu.getByte(Cpu.getHL());
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 8;
	}
	
//	OR A
//	Z 0 0 0
	public static void OR_A() {
		Cpu.A = Cpu.A | Cpu.A;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}

//	CP B
//	Z 1 H C
	public static void CP_B() {
		Cpu.checkZero8bit(Cpu.A - Cpu.B);
		Cpu.setFlagN();
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.B);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.B);
		Cpu.cycles += 4;
	}

//	CP C
//	Z 1 H C
	public static void CP_C() {
		Cpu.checkZero8bit(Cpu.A - Cpu.C);
		Cpu.setFlagN();
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.C);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.C);
		Cpu.cycles += 4;
	}
	
//	CP D
//	Z 1 H C
	public static void CP_D() {
		Cpu.checkZero8bit(Cpu.A - Cpu.D);
		Cpu.setFlagN();
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.D);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.D);
		Cpu.cycles += 4;
	}
	
//	CP E
//	Z 1 H C
	public static void CP_E() {
		Cpu.checkZero8bit(Cpu.A - Cpu.E);
		Cpu.setFlagN();
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.E);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.E);
		Cpu.cycles += 4;
	}
	
//	CP H
//	Z 1 H C
	public static void CP_H() {
		Cpu.checkZero8bit(Cpu.A - Cpu.H);
		Cpu.setFlagN();
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.H);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.H);
		Cpu.cycles += 4;
	}
	
//	CP L
//	Z 1 H C
	public static void CP_L() {
		Cpu.checkZero8bit(Cpu.A - Cpu.L);
		Cpu.setFlagN();
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.L);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.L);
		Cpu.cycles += 4;
	}
	
//	CP (HL)
//	Z 1 H C
	public static void CP_HL() {
		Cpu.checkZero8bit(Cpu.A - Main.mmu.getByte(Cpu.getHL()));
		Cpu.setFlagN();
		Cpu.checkHalfCarry8bitSub(Cpu.A, Main.mmu.getByte(Cpu.getHL()));
		Cpu.checkCarry8bitSub(Cpu.A, Main.mmu.getByte(Cpu.getHL()));
		Cpu.cycles += 8;
	}
	
//	CP A
//	1 1 0 0
	public static void CP_A() {
		Cpu.setFlagZ();
		Cpu.setFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 4;
	}
	
//	RET NZ
//	1  20/8
//	- - - -
	public static void RET_NZ() {
		if (Cpu.getFlagZ() == 0) {
			RET();
		} else {
			Cpu.cycles += 8;
		}
	}
	
//	POP BC
//	1  12
//	- - - -
	public static void POP_BC() {
		Cpu.C = Main.mmu.getByte(Cpu.SP);
		Cpu.SP += 1;
		Cpu.B = Main.mmu.getByte(Cpu.SP);
		Cpu.SP += 1;
		Cpu.cycles += 12;
	}
	
//	JP NZ, a16
//	3  16/12
//	- - - -
	public static void JP_NZ_a16() {
		if (Cpu.getFlagZ() == 0) {
			JP_a16();
		} else {
			Cpu.fetch();
			Cpu.fetch();
			Cpu.cycles += 12;
		}
	}

//	JP a16
//	3  16
//	- - - -
	public static void JP_a16() {
		int l = Cpu.fetch();
		int h = Cpu.fetch();
		int res = (h << 8) + l;
		Cpu.PC = res & 0xffff;
		Cpu.cycles += 16;
	}
	
//	CALL NZ, a16
//	3  24/12
//	- - - -
	public static void CALL_NZ_a16( ) {
		if (Cpu.getFlagZ() == 0) {
			CALL_a16();
		} else {
			Cpu.fetch();
			Cpu.fetch();
			Cpu.cycles += 12;
		}
	}
	
//	PUSH BC
//	1  16
//	- - - -
	public static void PUSH_BC() {
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, Cpu.B);
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, Cpu.C);
		Cpu.cycles += 16;
	}

//	ADD A, d8
//	2  8
//	Z 0 H C
	public static void ADD_A_d8() {
		int d8 = Cpu.fetch();
		Cpu.checkHalfCarry8bit(Cpu.A, d8);
		Cpu.checkCarry8bit(Cpu.A + d8);
		Cpu.A = (Cpu.A + d8) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.cycles += 8;
	}
	
//	RST 00H
//	1  16
//	- - - -
	public static void RST_0() {
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
//		Cpu.PC = Main.mmu.getByte(0x00);
		Cpu.PC = 0x0000;
		Cpu.cycles += 16;
	}
	
//	RET Z
//	1  20/8
//	- - - -
	public static void RET_Z() {
		if (Cpu.getFlagZ() == 1) {
			RET();
			Cpu.cycles += 4;
		} else {
			Cpu.cycles += 8;
		}
	}

//	RET
//	1  16
//	- - - -
	public static void RET() {
		int l = Cpu.fetchSP();
		int h = Cpu.fetchSP();
		int res = (h << 8) + l;
		Cpu.PC = res & 0xffff;
		Cpu.cycles += 20;
	}

//	JP Z, a16
//	3  16/12
//	- - - -
	public static void JP_Z_a16() {
		if (Cpu.getFlagZ() == 1) {
			JP_a16();
		} else {
			Cpu.fetch();
			Cpu.fetch();
			Cpu.cycles += 12;
		}
	}
	
//	CALL Z, a16
//	3  24/12
//	- - - -
	public static void CALL_Z_a16( ) {
		if (Cpu.getFlagZ() == 1) {
			CALL_a16();
		} else {
			Cpu.fetch();
			Cpu.fetch();
			Cpu.cycles += 12;
		}
	}
	
//	CALL a16
//	3  24
//	- - - -
	public static void CALL_a16() {
		int l = Cpu.fetch();
		int h = Cpu.fetch();
		int res = (h << 8) + l;
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
		Cpu.PC = res & 0xffff;
		Cpu.cycles += 24;
	}
	
//	ADC A, d8
//	2  8
//	Z 0 H C
	public static void ADC_A_d8() {
		int d8 = Cpu.fetch();
		int val = d8 + Cpu.getFlagC();
		
		int sum = Cpu.A + val;
		int res = sum & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.resetFlagH();
		Cpu.resetFlagN();
		if ((Cpu.A & 0xf) + (d8 & 0xf) + Cpu.getFlagC() > 0xf) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bit(sum);

		Cpu.A = res;
		Cpu.cycles += 8;
	}
	
//	RST 08H
//	1  16
//	- - - -
	public static void RST_1() {
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
//		Cpu.PC = Main.mmu.getByte(0x08);
		Cpu.PC = 0x0008;
		Cpu.cycles += 16;
	}

//	RET NC
//	1  20/8
//	- - - -
	public static void RET_NC() {
		if (Cpu.getFlagC() == 0) {
			RET();
		} else {
			Cpu.cycles += 8;
		}
	}

//	POP DE
//	1  12
//	- - - -
	public static void POP_DE() {
		Cpu.E = Main.mmu.getByte(Cpu.SP);
		Cpu.SP += 1;
		Cpu.D = Main.mmu.getByte(Cpu.SP);
		Cpu.SP += 1;
		Cpu.cycles += 12;
	}

//	JP NC, a16
//	3  16/12
//	- - - -
	public static void JP_NC_a16() {
		if (Cpu.getFlagC() == 0) {
			JP_a16();
		} else {
			Cpu.fetch();
			Cpu.fetch();
			Cpu.cycles += 12;
		}
	}

//	CALL NC, a16
//	3  24/12
//	- - - -
	public static void CALL_NC_a16( ) {
		if (Cpu.getFlagC() == 0) {
			CALL_a16();
		} else {
			Cpu.fetch();
			Cpu.fetch();
			Cpu.cycles += 12;
		}
	}

//	PUSH DE
//	1  16
//	- - - -
	public static void PUSH_DE() {
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, Cpu.D);
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, Cpu.E);
		Cpu.cycles += 16;
	}

//	SUB d8
//	2  8
//	Z 1 H C
	public static void SUB_A_d8() {
		int d8 = Cpu.fetch();
		Cpu.checkHalfCarry8bitSub(Cpu.A, d8);
		Cpu.checkCarry8bitSub(Cpu.A, d8);
		Cpu.A = (Cpu.A - d8) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
		Cpu.cycles += 8;
	}

//	RST 10H
//	1  16
//	- - - -
	public static void RST_2() {
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
		Cpu.PC = Main.mmu.getByte(0x10);
		Cpu.PC = 0x0010;
		Cpu.cycles += 16;
	}

//	RET C
//	1  20/8
//	- - - -
	public static void RET_C() {
		if (Cpu.getFlagC() == 1) {
			RET();
		} else  {
			Cpu.cycles += 8;
		}
	}

//	RETI
//	1  16
//	- - - -
	public static void RETI() {
		int l = Cpu.fetchSP();
		int h = Cpu.fetchSP();
		int res = (h << 8) + l;
		Cpu.PC = res & 0xffff;
		Cpu.cycles += 16;
	}

//	JP C, a16
//	3  16/12
//	- - - -
	public static void JP_C_a16() {
		if (Cpu.getFlagC() == 1) {
			JP_a16();
		} else {
			Cpu.fetch();
			Cpu.fetch();
			Cpu.cycles += 12;
		}
	}

//	CALL C, a16
//	3  24/12
//	- - - -
	public static void CALL_C_a16( ) {
		if (Cpu.getFlagC() == 1) {
			CALL_a16();
		} else {
			Cpu.fetch();
			Cpu.fetch();
			Cpu.cycles += 12;
		}
	}

//	SBC A, d8
//	2  8
//	Z 1 H C
	public static void SBC_A_d8() {
		int d8 = Cpu.fetch();
		int val = d8 + Cpu.getFlagC();
		
		int res = (Cpu.A - val) & 0xff;
		
		Cpu.checkZero8bit(res);
		Cpu.setFlagN();
		Cpu.resetFlagH();
		if ((Cpu.A & 0xf) < ((d8 & 0xf) + Cpu.getFlagC())) {
			Cpu.setFlagH();
		}
		Cpu.checkCarry8bitSub(Cpu.A, val);

		Cpu.A = res;
		Cpu.cycles += 8;
	}	

//	RST 18H
//	1  16
//	- - - -
	public static void RST_3() {
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
//		Cpu.PC = Main.mmu.getByte(0x18);
		Cpu.PC = 0x0018;
		Cpu.cycles += 16;
	}
	
	
//	LDH (a8), A
//	2  12
//	- - - -
	public static void LD_a8_A() {
		// Debe guardar el contenido del reg A en 0xFF(a8)
		Main.mmu.setByte(0xFF00 | Cpu.fetch(), Cpu.A);
		Cpu.cycles += 12;
	}

//	POP HL
//	1  12
//	- - - -
	public static void POP_HL() {
		Cpu.L = Main.mmu.getByte(Cpu.SP);
		Cpu.SP += 1;
		Cpu.H = Main.mmu.getByte(Cpu.SP);
		Cpu.SP += 1;
		Cpu.cycles += 12;
	}
	
//	LD (C), A
//	1  8
//	- - - -
	public static void LD_Cmem_A() {
		Main.mmu.setByte(0xff00 | Cpu.C, Cpu.A);
		Cpu.cycles += 8;
	}

//	PUSH HL
//	1  16
//	- - - -
	public static void PUSH_HL() {
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, Cpu.H);
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, Cpu.L);
		Cpu.cycles += 16;
	}
	
//	AND d8
//	2  8
//	Z 0 1 0
	public static void AND_d8() {
		Cpu.A = Cpu.A & Cpu.fetch();
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.resetFlagC();
		Cpu.cycles += 8;
	}

//	RST 20H
//	1  16
//	- - - -
	public static void RST_4() {
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
//		Cpu.PC = Main.mmu.getByte(0x20);
		Cpu.PC = 0x0020;
		Cpu.cycles += 16;
	}
	
//	ADD SP, r8
//	2  16
//	0 0 H C
	public static void ADD_SP_s8() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagC();
		
		int s8 = Cpu.fetchSigned();
		int sum = Cpu.SP + s8;
		
		Cpu.checkHalfCarry8bit(Cpu.SP, s8);

		if ((sum & 0xff) < (Cpu.SP & 0xff)) {
			Cpu.setFlagC();
		}
		
		Cpu.SP = sum & 0xffff;
		Cpu.cycles += 16;
	}
	
//	JP HL
//	1  4
//	- - - -
	public static void JP_HL() {
		int res = (Cpu.H << 8) + Cpu.L;
		Cpu.PC = res & 0xffff;
		Cpu.cycles += 4;
	}
	
//	LD (a16), A
//	3  16
//	- - - -
	public static void LD_a16_A() {
		int index = (Cpu.fetch() + (Cpu.fetch() << 8)) & 0xffff;
		Main.mmu.setByte(index, Cpu.A);
		Cpu.cycles += 16;
	}
	
//	XOR d8
//	2  8
//	Z 0 0 0
	public static void XOR_d8() {
		Cpu.A = Cpu.A ^ Cpu.fetch();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.checkZero8bit(Cpu.A);
		Cpu.cycles += 8;
	}

//	RST 28H
//	1  16
//	- - - -
	public static void RST_5() {
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
//		Cpu.PC = Main.mmu.getByte(0x28);
		Cpu.PC = 0x0028;
		Cpu.cycles += 16;
	}

//	LDH A, (a8)
//	2  12
//	- - - -
	public static void LD_A_a8() {
		Cpu.A = Main.mmu.getByte(0xff00 | Cpu.fetch());
		Cpu.cycles += 12;
	}

//	POP AF
//	1  12
//	Z N H C
	public static void POP_AF() {
		Cpu.F = Main.mmu.getByte(Cpu.SP) & 0xf0;
		Cpu.SP += 1;
		Cpu.A = Main.mmu.getByte(Cpu.SP);
		Cpu.SP += 1;
		Cpu.cycles += 12;
	}

//	LD A, (C)
//	1  8
//	- - - -
	public static void LD_A_Cmem() {
		Cpu.A = Main.mmu.getByte(0xff00 | Cpu.C);
		Cpu.cycles += 8;
	}

//	DI
//	1  4
//	- - - -
	public static void DI() {
		Cpu.IME = false;
		Cpu.cycles += 4;
	}

//	PUSH AF
//	1  16
//	- - - -
	public static void PUSH_AF() {
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, Cpu.A);
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, Cpu.F);
		Cpu.cycles += 16;
	}

//	OR d8
//	2  8
//	Z 0 0 0
	public static void OR_d8() {
		Cpu.A = Cpu.A | Cpu.fetch();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		Cpu.checkZero8bit(Cpu.A);
		Cpu.cycles += 8;
	}

//	RST 30H
//	1  16
//	- - - -
	public static void RST_6() {
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
//		Cpu.PC = Main.mmu.getByte(0x30);
		Cpu.PC = 0x0030;
		Cpu.cycles += 16;
	}
	
//	LD HL, SP + r8
//	2  12
//	0 0 H C
	public static void LD_HL_SPplusS8() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagC();
		
		int s8 = Cpu.fetchSigned();
		int sum = Cpu.SP + s8;
		
		Cpu.checkHalfCarry8bit(Cpu.SP, s8);
		if ((sum & 0xff) < (Cpu.SP & 0xff)) {
			Cpu.setFlagC();
		}
		
		Cpu.L = sum & 0xff;
		Cpu.H = (sum >> 8) & 0xff;
		Cpu.cycles += 12;
	}

//	LD SP, HL
//	1  8
//	- - - -
	public static void LD_SP_HL() {
		int value = (Cpu.H << 8) + Cpu.L;
		Cpu.SP = value;
		Cpu.cycles += 8;
	}

//	LD A, (a16)
//	3  16
//	- - - -
	public static void LD_A_a16() {
		int index = (Cpu.fetch() + (Cpu.fetch() << 8)) & 0xffff;
		Cpu.A = Main.mmu.getByte(index);
		Cpu.cycles += 16;
	}

//	EI
//	1  4
//	- - - -
	public static void EI() {
		Cpu.IME = true;
		Cpu.cycles += 4;
	}
	
//	CP d8
//	2  8
//	Z 1 H C
	public static void CP_d8() {
		int d8 = Cpu.fetch();
		Cpu.checkZero8bit(Cpu.A - d8);
		Cpu.setFlagN();
		Cpu.checkHalfCarry8bitSub(Cpu.A, d8);
		Cpu.checkCarry8bitSub(Cpu.A, d8);
		Cpu.cycles += 8;
	}

//	RST 38H
//	1  16
//	- - - -
	public static void RST_7() {
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Main.mmu.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
//		Cpu.PC = Main.mmu.getByte(0x38);
		Cpu.PC = 0x0038;
		Cpu.cycles += 16;
	}

// 	###############################################	
//	16-bit opcodes, where the first 8 bits are 0xCB
// 	###############################################	
	
//	RLC B
//	2  8
//	Z 0 0 C
	public static void RLC_B() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.B & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.B = ((Cpu.B << 1) | (Cpu.B >> 7)) & 0xff; // Rotate to the left
		Cpu.checkZero8bit(Cpu.B);
		Cpu.cycles += 8;
	}
	
//	RLC C
//	2  8
//	Z 0 0 C
	public static void RLC_C() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.C & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.C = ((Cpu.C << 1) | (Cpu.C >> 7)) & 0xff; // Rotate to the left
		Cpu.checkZero8bit(Cpu.C);
		Cpu.cycles += 8;
	}
	
//	RLC D
//	2  8
//	Z 0 0 C
	public static void RLC_D() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.D & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.D = ((Cpu.D << 1) | (Cpu.D >> 7)) & 0xff; // Rotate to the left
		Cpu.checkZero8bit(Cpu.D);
		Cpu.cycles += 8;
	}
	
//	RLC E
//	2  8
//	Z 0 0 C
	public static void RLC_E() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.E & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.E = ((Cpu.E << 1) | (Cpu.E >> 7)) & 0xff; // Rotate to the left
		Cpu.checkZero8bit(Cpu.E);
		Cpu.cycles += 8;
	}
	
//	RLC H
//	2  8
//	Z 0 0 C
	public static void RLC_H() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.H & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.H = ((Cpu.H << 1) | (Cpu.H >> 7)) & 0xff; // Rotate to the left
		Cpu.checkZero8bit(Cpu.H);
		Cpu.cycles += 8;
	}
	
//	RLC L
//	2  8
//	Z 0 0 C
	public static void RLC_L() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.L & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.L = ((Cpu.L << 1) | (Cpu.L >> 7)) & 0xff; // Rotate to the left
		Cpu.checkZero8bit(Cpu.L);
		Cpu.cycles += 8;
	}
	
//	RLC (HL)
//	2  16
//	Z 0 0 C
	public static void RLC_HL() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.mmu.getByte(Cpu.getHL()) & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		int res = ((Main.mmu.getByte(Cpu.getHL()) << 1) | (Main.mmu.getByte(Cpu.getHL()) >> 7)) & 0xff; // Rotate to the left
		Cpu.checkZero8bit(res);
		Main.mmu.setByte(Cpu.getHL(), res);
		Cpu.cycles += 16;
	}
	
//	RLC A
//	2  8
//	Z 0 0 C
	public static void RLC_A() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.A & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.A = ((Cpu.A << 1) | (Cpu.A >> 7)) & 0xff; // Rotate to the left
		Cpu.checkZero8bit(Cpu.A);
		Cpu.cycles += 8;
	}
	
//	RRC B
//	2  8
//	Z 0 0 C
	public static void RRC_B() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();

		if ((Cpu.B & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.B = ((Cpu.B << 7) | (Cpu.B >> 1)) & 0xff; // Rotate to the right
		Cpu.checkZero8bit(Cpu.B);
		Cpu.cycles += 8;
	}
	
//	RRC C
//	2  8
//	Z 0 0 C
	public static void RRC_C() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();

		if ((Cpu.C & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.C = ((Cpu.C << 7) | (Cpu.C >> 1)) & 0xff; // Rotate to right
		Cpu.checkZero8bit(Cpu.C);
		Cpu.cycles += 8;
	}
	
//	RRC D
//	2  8
//	Z 0 0 C
	public static void RRC_D() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();

		if ((Cpu.D & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.D = ((Cpu.D << 7) | (Cpu.D >> 1)) & 0xff; // Rotate to right
		Cpu.checkZero8bit(Cpu.D);
		Cpu.cycles += 8;
	}
	
//	RRC E
//	2  8
//	Z 0 0 C
	public static void RRC_E() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();

		if ((Cpu.E & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.E = ((Cpu.E << 7) | (Cpu.E >> 1)) & 0xff; // Rotate to right
		Cpu.checkZero8bit(Cpu.E);
		Cpu.cycles += 8;
	}
	
//	RRC H
//	2  8
//	Z 0 0 C
	public static void RRC_H() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();

		if ((Cpu.H & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.H = ((Cpu.H << 7) | (Cpu.H >> 1)) & 0xff; // Rotate to right
		Cpu.checkZero8bit(Cpu.H);
		Cpu.cycles += 8;
	}
	
//	RRC L
//	2  8
//	Z 0 0 C
	public static void RRC_L() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();

		if ((Cpu.L & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.L = ((Cpu.L << 7) | (Cpu.L >> 1)) & 0xff; // Rotate to right
		Cpu.checkZero8bit(Cpu.L);
		Cpu.cycles += 8;
	}
	
//	RRC (HL)
//	2  16
//	Z 0 0 C
	public static void RRC_HL() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		if ((Main.mmu.getByte(Cpu.getHL()) & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Main.mmu.setByte(Cpu.getHL(), ((Main.mmu.getByte(Cpu.getHL()) << 7) | (Main.mmu.getByte(Cpu.getHL()) >> 1)) & 0xff); // Rotate to right
		Cpu.checkZero8bit(Main.mmu.getByte(Cpu.getHL()));
		Cpu.cycles += 16;
	}
	
//	RRC A
//	2  8
//	Z 0 0 C
	public static void RRC_A() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();

		if ((Cpu.A & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.A = ((Cpu.A << 7) | (Cpu.A >> 1)) & 0xff; // Rotate to right
		Cpu.checkZero8bit(Cpu.A);
		Cpu.cycles += 8;
	}
	
//	RL B
//	2  8
//	Z 0 0 C
	public static void RL_B() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = Cpu.getFlagC();
		
		// Set carry flag
		if ((Cpu.B & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.B = (Cpu.B << 1) & 0xff; // Rotate to the right
		Cpu.B |= c;
		Cpu.checkZero8bit(Cpu.B);
		Cpu.cycles += 8;
	}
	
//	RL C
//	2  8
//	Z 0 0 C
	public static void RL_C() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = Cpu.getFlagC();
		
		// Set carry flag
		if ((Cpu.C & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.C = (Cpu.C << 1) & 0xff; // Rotate to the right
		Cpu.C |= c;
		Cpu.checkZero8bit(Cpu.C);
		Cpu.cycles += 8;
	}
	
//	RL D
//	2  8
//	Z 0 0 C
	public static void RL_D() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = Cpu.getFlagC();

		// Set carry flag
		if ((Cpu.D & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.D = (Cpu.D << 1) & 0xff; // Rotate to the right
		Cpu.D |= c;
		Cpu.checkZero8bit(Cpu.D);
		Cpu.cycles += 8;
	}
	
//	RL E
//	2  8
//	Z 0 0 C
	public static void RL_E() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = Cpu.getFlagC();

		// Set carry flag
		if ((Cpu.E & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.E = (Cpu.E << 1) & 0xff; // Rotate to the right
		Cpu.E |= c;
		Cpu.checkZero8bit(Cpu.E);
		Cpu.cycles += 8;
	}
	
//	RL H
//	2  8
//	Z 0 0 C
	public static void RL_H() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = Cpu.getFlagC();

		// Set carry flag
		if ((Cpu.H & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.H = (Cpu.H << 1) & 0xff; // Rotate to the right
		Cpu.H |= c;
		Cpu.checkZero8bit(Cpu.H);
		Cpu.cycles += 8;
	}
	
//	RL L
//	2  8
//	Z 0 0 C
	public static void RL_L() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = Cpu.getFlagC();
		
		// Set carry flag
		if ((Cpu.L & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.L = (Cpu.L << 1) & 0xff; // Rotate to the right
		Cpu.L |= c;
		Cpu.checkZero8bit(Cpu.L);
		Cpu.cycles += 8;
	}
	
//	RL (HL)
//	2  16
//	Z 0 0 C
	public static void RL_HL() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = Cpu.getFlagC();

		// Set carry flag
		if ((Main.mmu.getByte(Cpu.getHL()) & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Main.mmu.setByte(Cpu.getHL(), (Main.mmu.getByte(Cpu.getHL()) << 1) & 0xff);
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) | c);
		Cpu.checkZero8bit(Main.mmu.getByte(Cpu.getHL()));
		Cpu.cycles += 16;
	}
	
//	RL A
//	2  8
//	Z 0 0 C
	public static void RL_A() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = Cpu.getFlagC();

		// Set carry flag
		if ((Cpu.A & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.A = (Cpu.A << 1) & 0xff; // Rotate to the right
		Cpu.A |= c;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.cycles += 8;
	}
	
//	RR B
//	2  8
//	Z 0 0 C
	public static void RR_B() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = (Cpu.getFlagC() << 7);

		// Set carry flag
		if ((Cpu.B & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.B = (Cpu.B >> 1) & 0xff; // Rotate to the right
		Cpu.B |= c;
		Cpu.checkZero8bit(Cpu.B);
		Cpu.cycles += 8;
	}
	
//	RR C
//	2  8
//	Z 0 0 C
	public static void RR_C() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = (Cpu.getFlagC() << 7);

		// Set carry flag
		if ((Cpu.C & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.C = (Cpu.C >> 1) & 0xff; // Rotate to the right
		Cpu.C |= c;
		Cpu.checkZero8bit(Cpu.C);
		Cpu.cycles += 8;
	}
		
//	RR D
//	2  8
//	Z 0 0 C
	public static void RR_D() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = (Cpu.getFlagC() << 7);

		// Set carry flag
		if ((Cpu.D & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.D = (Cpu.D >> 1) & 0xff; // Rotate to the right
		Cpu.D |= c;
		Cpu.checkZero8bit(Cpu.D);
		Cpu.cycles += 8;
	}
	
//	RR E
//	2  8
//	Z 0 0 C
	public static void RR_E() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = (Cpu.getFlagC() << 7);

		// Set carry flag
		if ((Cpu.E & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.E = (Cpu.E >> 1) & 0xff; // Rotate to the right
		Cpu.E |= c;
		Cpu.checkZero8bit(Cpu.E);
		Cpu.cycles += 8;
	}
	
//	RR H
//	2  8
//	Z 0 0 C
	public static void RR_H() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = (Cpu.getFlagC() << 7);

		// Set carry flag
		if ((Cpu.H & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.H = (Cpu.H >> 1) & 0xff; // Rotate to the right
		Cpu.H |= c;
		Cpu.checkZero8bit(Cpu.H);
		Cpu.cycles += 8;
	}
	
//	RR L
//	2  8
//	Z 0 0 C
	public static void RR_L() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = (Cpu.getFlagC() << 7);

		// Set carry flag
		if ((Cpu.L & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.L = (Cpu.L >> 1) & 0xff; // Rotate to the right
		Cpu.L |= c;
		Cpu.checkZero8bit(Cpu.L);
		Cpu.cycles += 8;
	}
	
//	RR (HL)
//	2  16
//	Z 0 0 C
	public static void RR_HL() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = (Cpu.getFlagC() << 7);

		// Set carry flag
		if ((Main.mmu.getByte(Cpu.getHL()) & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Main.mmu.setByte(Cpu.getHL(), (Main.mmu.getByte(Cpu.getHL()) >> 1) & 0xff);
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) | c);
		Cpu.checkZero8bit(Main.mmu.getByte(Cpu.getHL()));
		Cpu.cycles += 16;
	}
	
//	RR A
//	2  8
//	Z 0 0 C
	public static void RR_A() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
	
		int c = (Cpu.getFlagC() << 7);

		// Set carry flag
		if ((Cpu.A & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.A = (Cpu.A >> 1) & 0xff; // Rotate to the right
		Cpu.A |= c;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.cycles += 8;
	}
	
//	SLA B
//	2  8
//	Z 0 0 C
	public static void SLA_B() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		if ((Cpu.B & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.B = (Cpu.B << 1) & 0xff;
		Cpu.checkZero8bit(Cpu.B);
		Cpu.cycles += 8;
	}
	
//	SLA C
//	2  8
//	Z 0 0 C
	public static void SLA_C() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		if ((Cpu.C & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.C = (Cpu.C << 1) & 0xff;
		Cpu.checkZero8bit(Cpu.C);
		Cpu.cycles += 8;
	}
	
//	SLA D
//	2  8
//	Z 0 0 C
	public static void SLA_D() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		if ((Cpu.D & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.D = (Cpu.D << 1) & 0xff;
		Cpu.checkZero8bit(Cpu.D);
		Cpu.cycles += 8;
	}
	
//	SLA E
//	2  8
//	Z 0 0 C
	public static void SLA_E() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		if ((Cpu.E & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.E = (Cpu.E << 1) & 0xff;
		Cpu.checkZero8bit(Cpu.E);
		Cpu.cycles += 8;
	}
	
//	SLA H
//	2  8
//	Z 0 0 C
	public static void SLA_H() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		if ((Cpu.H & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.H = (Cpu.H << 1) & 0xff;
		Cpu.checkZero8bit(Cpu.H);
		Cpu.cycles += 8;
	}
	
//	SLA L
//	2  8
//	Z 0 0 C
	public static void SLA_L() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		if ((Cpu.L & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.L = (Cpu.L << 1) & 0xff;
		Cpu.checkZero8bit(Cpu.L);
		Cpu.cycles += 8;
	}
	
//	SLA (HL)
//	2  16
//	Z 0 0 C
	public static void SLA_HL() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		int res = Main.mmu.getByte(Cpu.getHL());
		
		if ((res & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		res = (res << 1) & 0xff;
		Cpu.checkZero8bit(res);
		Main.mmu.setByte(Cpu.getHL(), res);
		Cpu.cycles += 16;
	}
	
//	SLA A
//	2  8
//	Z 0 0 C
	public static void SLA_A() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		if ((Cpu.A & 0x80) == 0x80) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.A = (Cpu.A << 1) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.cycles += 8;
	}
	
//	SRA B
//	2  8
//	Z 0 0 C
	public static void SRA_B() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int bit = Cpu.B & 0x80;
		int res = (Cpu.B >> 1) | bit; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.B & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.B = res & 0xff;
		Cpu.cycles += 8;
	}
	
//	SRA C
//	2  8
//	Z 0 0 C
	public static void SRA_C() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int bit = Cpu.C & 0x80;
		int res = (Cpu.C >> 1) | bit; // Rotate to the right

		//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.C & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.C = res & 0xff;
		Cpu.cycles += 8;
	}
	
//	SRA D
//	2  8
//	Z 0 0 C
	public static void SRA_D() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int bit = Cpu.D & 0x80;
		int res = (Cpu.D >> 1) | bit; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.D & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.D = res & 0xff;
		Cpu.cycles += 8;
	}
	
//	SRA E
//	2  8
//	Z 0 0 C
	public static void SRA_E() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int bit = Cpu.E & 0x80;
		int res = (Cpu.E >> 1) | bit; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.E & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.E = res & 0xff;
		Cpu.cycles += 8;
	}
	
//	SRA H
//	2  8
//	Z 0 0 C
	public static void SRA_H() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int bit = Cpu.H & 0x80;
		int res = (Cpu.H >> 1) | bit; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.H & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.H = res & 0xff;
		Cpu.cycles += 8;
	}
	
//	SRA L
//	2  8
//	Z 0 0 C
	public static void SRA_L() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int bit = Cpu.L & 0x80;
		int res = (Cpu.L >> 1) | bit; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.L & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.L = res & 0xff;
		Cpu.cycles += 8;
	}
//	SRA (HL)
//	2  16
//	Z 0 0 C
	public static void SRA_HL() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int bit = Main.mmu.getByte(Cpu.getHL()) & 0x80;
		int res = (Main.mmu.getByte(Cpu.getHL()) >> 1) | bit; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.mmu.getByte(Cpu.getHL()) & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Main.mmu.setByte(Cpu.getHL(), res & 0xff);
		Cpu.cycles += 16;
	}
	
//	SRA A
//	2  8
//	Z 0 0 C
	public static void SRA_A() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int bit = Cpu.A & 0x80;
		int res = (Cpu.A >> 1) | bit; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.A & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.A = res & 0xff;
		Cpu.cycles += 8;
	}
	
//	SWAP B
//	2  8
//	Z 0 0 0
	public static void SWAP_B() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		int l = Cpu.B & 0x0f;
		int h = Cpu.B & 0xf0;
		Cpu.B = (l << 4) | (h >> 4);
		Cpu.checkZero8bit(Cpu.B);
		Cpu.cycles += 8;
	}
	
//	SWAP C
//	2  8
//	Z 0 0 0
	public static void SWAP_C() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		int l = Cpu.C & 0x0f;
		int h = Cpu.C & 0xf0;
		Cpu.C = (l << 4) | (h >> 4);
		Cpu.checkZero8bit(Cpu.C);
		Cpu.cycles += 8;
	}
	
//	SWAP D
//	2  8
//	Z 0 0 0
	public static void SWAP_D() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		int l = Cpu.D & 0x0f;
		int h = Cpu.D & 0xf0;
		Cpu.D = (l << 4) | (h >> 4);
		Cpu.checkZero8bit(Cpu.D);
		Cpu.cycles += 8;
	}
	
//	SWAP E
//	2  8
//	Z 0 0 0
	public static void SWAP_E() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		int l = Cpu.E & 0x0f;
		int h = Cpu.E & 0xf0;
		Cpu.E = (l << 4) | (h >> 4);
		Cpu.checkZero8bit(Cpu.E);
		Cpu.cycles += 8;
	}
	
//	SWAP H
//	2  8
//	Z 0 0 0
	public static void SWAP_H() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		int l = Cpu.H & 0x0f;
		int h = Cpu.H & 0xf0;
		Cpu.H = (l << 4) | (h >> 4);
		Cpu.checkZero8bit(Cpu.H);
		Cpu.cycles += 8;
	}
	
//	SWAP L
//	2  8
//	Z 0 0 0
	public static void SWAP_L() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		int l = Cpu.L & 0x0f;
		int h = Cpu.L & 0xf0;
		Cpu.L = (l << 4) | (h >> 4);
		Cpu.checkZero8bit(Cpu.L);
		Cpu.cycles += 8;
	}
	
//	SWAP (HL)
//	2  16
//	Z 0 0 0
	public static void SWAP_HL() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		int l = Main.mmu.getByte(Cpu.getHL()) & 0x0f;
		int h = Main.mmu.getByte(Cpu.getHL()) & 0xf0;
		Main.mmu.setByte(Cpu.getHL(), (l << 4) | (h >> 4));
		Cpu.checkZero8bit(Main.mmu.getByte(Cpu.getHL()));
		Cpu.cycles += 16;
	}
	
//	SWAP A
//	2  8
//	Z 0 0 0
	public static void SWAP_A() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		int l = Cpu.A & 0x0f;
		int h = Cpu.A & 0xf0;
		Cpu.A = (l << 4) | (h >> 4);
		Cpu.checkZero8bit(Cpu.A);
		Cpu.cycles += 8;
	}
	
//	SRL B
//	2  8
//	Z 0 0 C
	public static void SRL_B() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int res = Cpu.B >> 1; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.B & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.B = res & 0xff;
		Cpu.cycles += 8;
	}
	
//	SRL C
//	2  8
//	Z 0 0 C
	public static void SRL_C() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int res = Cpu.C >> 1; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.C & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.C = res & 0xff;
		Cpu.cycles += 8;
	}
	
//	SRL D
//	2  8
//	Z 0 0 C
	public static void SRL_D() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int res = Cpu.D >> 1; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.D & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.D = res & 0xff;
		Cpu.cycles += 8;
	}
	
//	SRL E
//	2  8
//	Z 0 0 C
	public static void SRL_E() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int res = Cpu.E >> 1; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.E & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.E = res & 0xff;
		Cpu.cycles += 8;
	}
	
//	SRL H
//	2  8
//	Z 0 0 C
	public static void SRL_H() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int res = Cpu.H >> 1; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.H & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.H = res & 0xff;
		Cpu.cycles += 8;
	}
	
//	SRL L
//	2  8
//	Z 0 0 C
	public static void SRL_L() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int res = Cpu.L >> 1; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.L & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.L = res & 0xff;
		Cpu.cycles += 8;
	}
	
//	SRL (HL)
//	2  16
//	Z 0 0 C
	public static void SRL_HL() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int res = Main.mmu.getByte(Cpu.getHL()) >> 1; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Main.mmu.getByte(Cpu.getHL()) & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Main.mmu.setByte(Cpu.getHL(), res & 0xff);
		Cpu.cycles += 16;
	}
	
//	SRL A
//	2  8
//	Z 0 0 C
	public static void SRL_A() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int res = Cpu.A >> 1; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.A & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.A = res & 0xff;
		Cpu.cycles += 8;
	}
	
//	BIT 0, B
//	2  8
//	Z 0 1 -
	public static void BIT_0_B() {
		if ((Cpu.B & 0x01) == 0x01) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 0, C
//	2  8
//	Z 0 1 -
	public static void BIT_0_C() {
		if ((Cpu.C & 0x01) == 0x01) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 0, D
//	2  8
//	Z 0 1 -
	public static void BIT_0_D() {
		if ((Cpu.D & 0x01) == 0x01) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 0, E
//	2  8
//	Z 0 1 -
	public static void BIT_0_E() {
		if ((Cpu.E & 0x01) == 0x01) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 0, H
//	2  8
//	Z 0 1 -
	public static void BIT_0_H() {
		if ((Cpu.H & 0x01) == 0x01) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 0, L
//	2  8
//	Z 0 1 -
	public static void BIT_0_L() {
		if ((Cpu.L & 0x01) == 0x01) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 0, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_0_HL() {
		if ((Main.mmu.getByte(Cpu.getHL()) & 0x01) == 0x01) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 12;
	}
	
//	BIT 0, A
//	2  8
//	Z 0 1 -
	public static void BIT_0_A() {
		if ((Cpu.A & 0x01) == 0x01) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 1, B
//	2  8
//	Z 0 1 -
	public static void BIT_1_B() {
		if ((Cpu.B & 0x02) == 0x02) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 1, C
//	2  8
//	Z 0 1 -
	public static void BIT_1_C() {
		if ((Cpu.C & 0x02) == 0x02) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 1, D
//	2  8
//	Z 0 1 -
	public static void BIT_1_D() {
		if ((Cpu.D & 0x02) == 0x02) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 1, E
//	2  8
//	Z 0 1 -
	public static void BIT_1_E() {
		if ((Cpu.E & 0x02) == 0x02) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 1, H
//	2  8
//	Z 0 1 -
	public static void BIT_1_H() {
		if ((Cpu.H & 0x02) == 0x02) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 1, L
//	2  8
//	Z 0 1 -
	public static void BIT_1_L() {
		if ((Cpu.L & 0x02) == 0x02) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 1, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_1_HL() {
		if ((Main.mmu.getByte(Cpu.getHL()) & 0x02) == 0x02) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 12;
	}
	
//	BIT 1, A
//	2  8
//	Z 0 1 -
	public static void BIT_1_A() {
		if ((Cpu.A & 0x02) == 0x02) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 2, B
//	2  8
//	Z 0 1 -
	public static void BIT_2_B() {
		if ((Cpu.B & 0x04) == 0x04) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 2, C
//	2  8
//	Z 0 1 -
	public static void BIT_2_C() {
		if ((Cpu.C & 0x04) == 0x04) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
		
//	BIT 2, D
//	2  8
//	Z 0 1 -
	public static void BIT_2_D() {
		if ((Cpu.D & 0x04) == 0x04) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 2, E
//	2  8
//	Z 0 1 -
	public static void BIT_2_E() {
		if ((Cpu.E & 0x04) == 0x04) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 2, H
//	2  8
//	Z 0 1 -
	public static void BIT_2_H() {
		if ((Cpu.H & 0x04) == 0x04) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 2, L
//	2  8
//	Z 0 1 -
	public static void BIT_2_L() {
		if ((Cpu.L & 0x04) == 0x04) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 2, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_2_HL() {
		if ((Main.mmu.getByte(Cpu.getHL()) & 0x04) == 0x04) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 12;
	}
	
//	BIT 2, A
//	2  8
//	Z 0 1 -
	public static void BIT_2_A() {
		if ((Cpu.A & 0x04) == 0x04) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 3, B
//	2  8
//	Z 0 1 -
	public static void BIT_3_B() {
		if ((Cpu.B & 0x08) == 0x08) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 3, C
//	2  8
//	Z 0 1 -
	public static void BIT_3_C() {
		if ((Cpu.C & 0x08) == 0x08) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
		
//	BIT 3, D
//	2  8
//	Z 0 1 -
	public static void BIT_3_D() {
		if ((Cpu.D & 0x08) == 0x08) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 3, E
//	2  8
//	Z 0 1 -
	public static void BIT_3_E() {
		if ((Cpu.E & 0x08) == 0x08) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 3, H
//	2  8
//	Z 0 1 -
	public static void BIT_3_H() {
		if ((Cpu.H & 0x08) == 0x08) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 3, L
//	2  8
//	Z 0 1 -
	public static void BIT_3_L() {
		if ((Cpu.L & 0x08) == 0x08) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 3, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_3_HL() {
		if ((Main.mmu.getByte(Cpu.getHL()) & 0x08) == 0x08) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 12;
	}
	
//	BIT 3, A
//	2  8
//	Z 0 1 -
	public static void BIT_3_A() {
		if ((Cpu.A & 0x08) == 0x08) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 4, B
//	2  8
//	Z 0 1 -
	public static void BIT_4_B() {
		if ((Cpu.B & 0x10) == 0x10) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 4, C
//	2  8
//	Z 0 1 -
	public static void BIT_4_C() {
		if ((Cpu.C & 0x10) == 0x10) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
		
//	BIT 4, D
//	2  8
//	Z 0 1 -
	public static void BIT_4_D() {
		if ((Cpu.D & 0x10) == 0x10) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 4, E
//	2  8
//	Z 0 1 -
	public static void BIT_4_E() {
		if ((Cpu.E & 0x10) == 0x10) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 4, H
//	2  8
//	Z 0 1 -
	public static void BIT_4_H() {
		if ((Cpu.H & 0x10) == 0x10) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 4, L
//	2  8
//	Z 0 1 -
	public static void BIT_4_L() {
		if ((Cpu.L & 0x10) == 0x10) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 4, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_4_HL() {
		if ((Main.mmu.getByte(Cpu.getHL()) & 0x10) == 0x10) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 12;
	}
	
//	BIT 4, A
//	2  8
//	Z 0 1 -
	public static void BIT_4_A() {
		if ((Cpu.A & 0x10) == 0x10) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 5, B
//	2  8
//	Z 0 1 -
	public static void BIT_5_B() {
		if ((Cpu.B & 0x20) == 0x20) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 5, C
//	2  8
//	Z 0 1 -
	public static void BIT_5_C() {
		if ((Cpu.C & 0x20) == 0x20) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
		
//	BIT 5, D
//	2  8
//	Z 0 1 -
	public static void BIT_5_D() {
		if ((Cpu.D & 0x20) == 0x20) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 5, E
//	2  8
//	Z 0 1 -
	public static void BIT_5_E() {
		if ((Cpu.E & 0x20) == 0x20) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 5, H
//	2  8
//	Z 0 1 -
	public static void BIT_5_H() {
		if ((Cpu.H & 0x20) == 0x20) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 5, L
//	2  8
//	Z 0 1 -
	public static void BIT_5_L() {
		if ((Cpu.L & 0x20) == 0x20) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 5, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_5_HL() {
		if ((Main.mmu.getByte(Cpu.getHL()) & 0x20) == 0x20) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 12;
	}
	
//	BIT 5, A
//	2  8
//	Z 0 1 -
	public static void BIT_5_A() {
		if ((Cpu.A & 0x20) == 0x20) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 6, B
//	2  8
//	Z 0 1 -
	public static void BIT_6_B() {
		if ((Cpu.B & 0x40) == 0x40) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 6, C
//	2  8
//	Z 0 1 -
	public static void BIT_6_C() {
		if ((Cpu.C & 0x40) == 0x40) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
		
//	BIT 6, D
//	2  8
//	Z 0 1 -
	public static void BIT_6_D() {
		if ((Cpu.D & 0x40) == 0x40) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 6, E
//	2  8
//	Z 0 1 -
	public static void BIT_6_E() {
		if ((Cpu.E & 0x40) == 0x40) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 6, H
//	2  8
//	Z 0 1 -
	public static void BIT_6_H() {
		if ((Cpu.H & 0x40) == 0x40) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 6, L
//	2  8
//	Z 0 1 -
	public static void BIT_6_L() {
		if ((Cpu.L & 0x40) == 0x40) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 6, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_6_HL() {
		if ((Main.mmu.getByte(Cpu.getHL()) & 0x40) == 0x40) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 12;
	}
	
//	BIT 6, A
//	2  8
//	Z 0 1 -
	public static void BIT_6_A() {
		if ((Cpu.A & 0x40) == 0x40) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 7, B
//	2  8
//	Z 0 1 -
	public static void BIT_7_B() {
		if ((Cpu.B & 0x80) == 0x80) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 7, C
//	2  8
//	Z 0 1 -
	public static void BIT_7_C() {
		if ((Cpu.C & 0x80) == 0x80) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 7, D
//	2  8
//	Z 0 1 -
	public static void BIT_7_D() {
		if ((Cpu.D & 0x80) == 0x80) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 7, E
//	2  8
//	Z 0 1 -
	public static void BIT_7_E() {
		if ((Cpu.E & 0x80) == 0x80) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 7, H
//	2  8
//	Z 0 1 -
	public static void BIT_7_H() {
		if ((Cpu.H & 0x80) == 0x80) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 7, L
//	2  8
//	Z 0 1 -
	public static void BIT_7_L() {
		if ((Cpu.L & 0x80) == 0x80) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	BIT 7, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_7_HL() {
		if ((Main.mmu.getByte(Cpu.getHL()) & 0x80) == 0x80) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 12;
	}
	
//	BIT 7, A
//	2  8
//	Z 0 1 -
	public static void BIT_7_A() {
		if ((Cpu.A & 0x80) == 0x80) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
		Cpu.cycles += 8;
	}
	
//	RES 0, B
//	2  8
//	- - - -
	public static void RES_0_B() {
		Cpu.B &= 0xfe;
		Cpu.cycles += 8;
	}
	
//	RES 0, C
//	2  8
//	- - - -
	public static void RES_0_C() {
		Cpu.C &= 0xfe;
		Cpu.cycles += 8;
	}
	
//	RES 0, D
//	2  8
//	- - - -
	public static void RES_0_D() {
		Cpu.D &= 0xfe;
		Cpu.cycles += 8;
	}
		
//	RES 0, E
//	2  8
//	- - - -
	public static void RES_0_E() {
		Cpu.E &= 0xfe;
		Cpu.cycles += 8;
	}
	
//	RES 0, H
//	2  8
//	- - - -
	public static void RES_0_H() {
		Cpu.H &= 0xfe;
		Cpu.cycles += 8;
	}
	
//	RES 0, L
//	2  8
//	- - - -
	public static void RES_0_L() {
		Cpu.L &= 0xfe;
		Cpu.cycles += 8;
	}
	
//	RES 0, (HL)
//	2  16
//	- - - -
	public static void RES_0_HL() {
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) & 0xfe);
		Cpu.cycles += 16;
	}
	
//	RES 0, A
//	2  8
//	- - - -
	public static void RES_0_A() {
		Cpu.A &= 0xfe;
		Cpu.cycles += 8;
	}
	
//	RES 1, B
//	2  8
//	- - - -
	public static void RES_1_B() {
		Cpu.B &= 0xfd;
		Cpu.cycles += 8;
	}
		
//	RES 1, C
//	2  8
//	- - - -
	public static void RES_1_C() {
		Cpu.C &= 0xfd;
		Cpu.cycles += 8;
	}
	
//	RES 1, D
//	2  8
//	- - - -
	public static void RES_1_D() {
		Cpu.D &= 0xfd;
		Cpu.cycles += 8;
	}

//	RES 1, E
//	2  8
//	- - - -
	public static void RES_1_E() {
		Cpu.E &= 0xfd;
		Cpu.cycles += 8;
	}

//	RES 1, H
//	2  8
//	- - - -
	public static void RES_1_H() {
		Cpu.H &= 0xfd;
		Cpu.cycles += 8;
	}

//	RES 1, L
//	2  8
//	- - - -
	public static void RES_1_L() {
		Cpu.L &= 0xfd;
		Cpu.cycles += 8;
	}

//	RES 1, (HL)
//	2  16
//	- - - -
	public static void RES_1_HL() {
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) & 0xfd);
		Cpu.cycles += 16;
	}
	
//	RES 1, A
//	2  8
//	- - - -
	public static void RES_1_A() {
		Cpu.A &= 0xfd;
		Cpu.cycles += 8;
	}

//	RES 2, B
//	2  8
//	- - - -
	public static void RES_2_B() {
		Cpu.B &= 0xfb;
		Cpu.cycles += 8;
	}

//	RES 2, C
//	2  8
//	- - - -
	public static void RES_2_C() {
		Cpu.C &= 0xfb;
		Cpu.cycles += 8;
	}
	
//	RES 2, D
//	2  8
//	- - - -
	public static void RES_2_D() {
		Cpu.D &= 0xfb;
		Cpu.cycles += 8;
	}

//	RES 2, E
//	2  8
//	- - - -
	public static void RES_2_E() {
		Cpu.E &= 0xfb;
		Cpu.cycles += 8;
	}

//	RES 2, H
//	2  8
//	- - - -
	public static void RES_2_H() {
		Cpu.H &= 0xfb;
		Cpu.cycles += 8;
	}

//	RES 2, L
//	2  8
//	- - - -
	public static void RES_2_L() {
		Cpu.L &= 0xfb;
		Cpu.cycles += 8;
	}

//	RES 2, (HL)
//	2  16
//	- - - -
	public static void RES_2_HL() {
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) & 0xfb);
		Cpu.cycles += 16;
	}
	
//	RES 2, A
//	2  8
//	- - - -
	public static void RES_2_A() {
		Cpu.A &= 0xfb;
		Cpu.cycles += 8;
	}
	
//	RES 3, B
//	2  8
//	- - - -
	public static void RES_3_B() {
		Cpu.B &= 0xf7;
		Cpu.cycles += 8;
	}
		
//	RES 3, C
//	2  8
//	- - - -
	public static void RES_3_C() {
		Cpu.C &= 0xf7;
		Cpu.cycles += 8;
	}
		
//	RES 3, D
//	2  8
//	- - - -
	public static void RES_3_D() {
		Cpu.D &= 0xf7;
		Cpu.cycles += 8;
	}
	
//	RES 3, E
//	2  8
//	- - - -
	public static void RES_3_E() {
		Cpu.E &= 0xf7;
		Cpu.cycles += 8;
	}
	
//	RES 3, H
//	2  8
//	- - - -
	public static void RES_3_H() {
		Cpu.H &= 0xf7;
		Cpu.cycles += 8;
	}
	
//	RES 3, L
//	2  8
//	- - - -
	public static void RES_3_L() {
		Cpu.L &= 0xf7;
		Cpu.cycles += 8;
	}
	
//	RES 3, (HL)
//	2  16
//	- - - -
	public static void RES_3_HL() {
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) & 0xf7);
		Cpu.cycles += 16;
	}
	
//	RES 3, A
//	2  8
//	- - - -
	public static void RES_3_A() {
		Cpu.A &= 0xf7;
		Cpu.cycles += 8;
	}
	
//	RES 4, B
//	2  8
//	- - - -
	public static void RES_4_B() {
		Cpu.B &= 0xef;
		Cpu.cycles += 8;
	}
	
//	RES 4, C
//	2  8
//	- - - -
	public static void RES_4_C() {
		Cpu.C &= 0xef;
		Cpu.cycles += 8;
	}
	
//	RES 4, D
//	2  8
//	- - - -
	public static void RES_4_D() {
		Cpu.D &= 0xef;
		Cpu.cycles += 8;
	}
	
//	RES 4, E
//	2  8
//	- - - -
	public static void RES_4_E() {
		Cpu.E &= 0xef;
		Cpu.cycles += 8;
	}
	
//	RES 4, H
//	2  8
//	- - - -
	public static void RES_4_H() {
		Cpu.H &= 0xef;
		Cpu.cycles += 8;
	}
	
//	RES 4, L
//	2  8
//	- - - -
	public static void RES_4_L() {
		Cpu.L &= 0xef;
		Cpu.cycles += 8;
	}
	
//	RES 4, (HL)
//	2  16
//	- - - -
	public static void RES_4_HL() {
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) & 0xef);
		Cpu.cycles += 16;
	}
	
//	RES 4, A
//	2  8
//	- - - -
	public static void RES_4_A() {
		Cpu.A &= 0xef;
		Cpu.cycles += 8;
	}
	
//	RES 5, B
//	2  8
//	- - - -
	public static void RES_5_B() {
		Cpu.B &= 0xdf;
		Cpu.cycles += 8;
	}
		
//	RES 5, C
//	2  8
//	- - - -
	public static void RES_5_C() {
		Cpu.C &= 0xdf;
		Cpu.cycles += 8;
	}
	
//	RES 5, D
//	2  8
//	- - - -
	public static void RES_5_D() {
		Cpu.D &= 0xdf;
		Cpu.cycles += 8;
	}

//	RES 5, E
//	2  8
//	- - - -
	public static void RES_5_E() {
		Cpu.E &= 0xdf;
		Cpu.cycles += 8;
	}

//	RES 5, H
//	2  8
//	- - - -
	public static void RES_5_H() {
		Cpu.H &= 0xdf;
		Cpu.cycles += 8;
	}

//	RES 5, L
//	2  8
//	- - - -
	public static void RES_5_L() {
		Cpu.L &= 0xdf;
		Cpu.cycles += 8;
	}

//	RES 5, (HL)
//	2  16
//	- - - -
	public static void RES_5_HL() {
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) & 0xdf);
		Cpu.cycles += 16;
	}
	
//	RES 5, A
//	2  8
//	- - - -
	public static void RES_5_A() {
		Cpu.A &= 0xdf;
		Cpu.cycles += 8;
	}

//	RES 6, B
//	2  8
//	- - - -
	public static void RES_6_B() {
		Cpu.B &= 0xbf;
		Cpu.cycles += 8;
	}
	
//	RES 6, C
//	2  8
//	- - - -
	public static void RES_6_C() {
		Cpu.C &= 0xbf;
		Cpu.cycles += 8;
	}
		
//	RES 6, D
//	2  8
//	- - - -
	public static void RES_6_D() {
		Cpu.D &= 0xbf;
		Cpu.cycles += 8;
	}
	
//	RES 6, E
//	2  8
//	- - - -
	public static void RES_6_E() {
		Cpu.E &= 0xbf;
		Cpu.cycles += 8;
	}
	
//	RES 6, H
//	2  8
//	- - - -
	public static void RES_6_H() {
		Cpu.H &= 0xbf;
		Cpu.cycles += 8;
	}
	
//	RES 6, L
//	2  8
//	- - - -
	public static void RES_6_L() {
		Cpu.L &= 0xbf;
		Cpu.cycles += 8;
	}
	
//	RES 6, (HL)
//	2  16
//	- - - -
	public static void RES_6_HL() {
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) & 0xbf);
		Cpu.cycles += 16;
	}
	
//	RES 6, A
//	2  8
//	- - - -
	public static void RES_6_A() {
		Cpu.A &= 0xbf;
		Cpu.cycles += 8;
	}
	
//	RES 7, B
//	2  8
//	- - - -
	public static void RES_7_B() {
		Cpu.B &= 0x7f;
		Cpu.cycles += 8;
	}
	
//	RES 7, C
//	2  8
//	- - - -
	public static void RES_7_C() {
		Cpu.C &= 0x7f;
		Cpu.cycles += 8;
	}
		
//	RES 7, D
//	2  8
//	- - - -
	public static void RES_7_D() {
		Cpu.D &= 0x7f;
		Cpu.cycles += 8;
	}
	
//	RES 7, E
//	2  8
//	- - - -
	public static void RES_7_E() {
		Cpu.E &= 0x7f;
		Cpu.cycles += 8;
	}
	
//	RES 7, H
//	2  8
//	- - - -
	public static void RES_7_H() {
		Cpu.H &= 0x7f;
		Cpu.cycles += 8;
	}
	
//	RES 7, L
//	2  8
//	- - - -
	public static void RES_7_L() {
		Cpu.L &= 0x7f;
		Cpu.cycles += 8;
	}
	
//	RES 7, (HL)
//	2  16
//	- - - -
	public static void RES_7_HL() {
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) & 0x7f);
		Cpu.cycles += 16;
	}

//	RES 7, A
//	2  8
//	- - - -
	public static void RES_7_A() {
		Cpu.A &= 0x7f;
		Cpu.cycles += 8;
	}
	
//	SET 0, B
//	2  8
//	- - - -
	public static void SET_0_B() {
		Cpu.B |= 0x01;
		Cpu.cycles += 8;
	}
	
//	SET 0, C
//	2  8
//	- - - -
	public static void SET_0_C() {
		Cpu.C |= 0x01;
		Cpu.cycles += 8;
	}
		
//	SET 0, D
//	2  8
//	- - - -
	public static void SET_0_D() {
		Cpu.D |= 0x01;
		Cpu.cycles += 8;
	}
	
//	SET 0, E
//	2  8
//	- - - -
	public static void SET_0_E() {
		Cpu.E |= 0x01;
		Cpu.cycles += 8;
	}
	
//	SET 0, H
//	2  8
//	- - - -
	public static void SET_0_H() {
		Cpu.H |= 0x01;
		Cpu.cycles += 8;
	}
	
//	SET 0, L
//	2  8
//	- - - -
	public static void SET_0_L() {
		Cpu.L |= 0x01;
		Cpu.cycles += 8;
	}
	
//	SET 0, (HL)
//	2  16
//	- - - -
	public static void SET_0_HL() {
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) | 0x01);
		Cpu.cycles += 16;
	}
	
//	SET 0, A
//	2  8
//	- - - -
	public static void SET_0_A() {
		Cpu.A |= 0x01;
		Cpu.cycles += 8;
	}
	
//	SET 1, B
//	2  8
//	- - - -
	public static void SET_1_B() {
		Cpu.B |= 0x02;
		Cpu.cycles += 8;
	}
	
//	SET 1, C
//	2  8
//	- - - -
	public static void SET_1_C() {
		Cpu.C |= 0x02;
		Cpu.cycles += 8;
	}
	
//	SET 1, D
//	2  8
//	- - - -
	public static void SET_1_D() {
		Cpu.D |= 0x02;
		Cpu.cycles += 8;
	}
	
//	SET 1, E
//	2  8
//	- - - -
	public static void SET_1_E() {
		Cpu.E |= 0x02;
		Cpu.cycles += 8;
	}
	
//	SET 1, H
//	2  8
//	- - - -
	public static void SET_1_H() {
		Cpu.H |= 0x02;
		Cpu.cycles += 8;
	}
	
//	SET 1, L
//	2  8
//	- - - -
	public static void SET_1_L() {
		Cpu.L |= 0x02;
		Cpu.cycles += 8;
	}
	
//	SET 1, (HL)
//	2  16
//	- - - -
	public static void SET_1_HL() {
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) | 0x02);
		Cpu.cycles += 16;
	}
	
//	SET 1, A
//	2  8
//	- - - -
	public static void SET_1_A() {
		Cpu.A |= 0x02;
		Cpu.cycles += 8;
	}
	
//	SET 2, B
//	2  8
//	- - - -
	public static void SET_2_B() {
		Cpu.B |= 0x04;
		Cpu.cycles += 8;
	}
	
//	SET 2, C
//	2  8
//	- - - -
	public static void SET_2_C() {
		Cpu.C |= 0x04;
		Cpu.cycles += 8;
	}
		
//	SET 2, D
//	2  8
//	- - - -
	public static void SET_2_D() {
		Cpu.D |= 0x04;
		Cpu.cycles += 8;
	}
	
//	SET 2, E
//	2  8
//	- - - -
	public static void SET_2_E() {
		Cpu.E |= 0x04;
		Cpu.cycles += 8;
	}
	
//	SET 2, H
//	2  8
//	- - - -
	public static void SET_2_H() {
		Cpu.H |= 0x04;
		Cpu.cycles += 8;
	}
	
//	SET 2, L
//	2  8
//	- - - -
	public static void SET_2_L() {
		Cpu.L |= 0x04;
		Cpu.cycles += 8;
	}
	
//	SET 2, (HL)
//	2  16
//	- - - -
	public static void SET_2_HL() {
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) | 0x04);
		Cpu.cycles += 16;
	}
	
//	SET 2, A
//	2  8
//	- - - -
	public static void SET_2_A() {
		Cpu.A |= 0x04;
		Cpu.cycles += 8;
	}
	
//	SET 3, B
//	2  8
//	- - - -
	public static void SET_3_B() {
		Cpu.B |= 0x08;
		Cpu.cycles += 8;
	}
	
//	SET 3, C
//	2  8
//	- - - -
	public static void SET_3_C() {
		Cpu.C |= 0x08;
		Cpu.cycles += 8;
	}
		
//	SET 3, D
//	2  8
//	- - - -
	public static void SET_3_D() {
		Cpu.D |= 0x08;
		Cpu.cycles += 8;
	}
	
//	SET 3, E
//	2  8
//	- - - -
	public static void SET_3_E() {
		Cpu.E |= 0x08;
		Cpu.cycles += 8;
	}
	
//	SET 3, H
//	2  8
//	- - - -
	public static void SET_3_H() {
		Cpu.H |= 0x08;
		Cpu.cycles += 8;
	}
	
//	SET 3, L
//	2  8
//	- - - -
	public static void SET_3_L() {
		Cpu.L |= 0x08;
		Cpu.cycles += 8;
	}
	
//	SET 3, (HL)
//	2  16
//	- - - -
	public static void SET_3_HL() {
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) | 0x08);
		Cpu.cycles += 16;
	}
	
//	SET 3, A
//	2  8
//	- - - -
	public static void SET_3_A() {
		Cpu.A |= 0x08;
		Cpu.cycles += 8;
	}
	
//	SET 4, B
//	2  8
//	- - - -
	public static void SET_4_B() {
		Cpu.B |= 0x10;
		Cpu.cycles += 8;
	}
	
//	SET 4, C
//	2  8
//	- - - -
	public static void SET_4_C() {
		Cpu.C |= 0x10;
		Cpu.cycles += 8;
	}
	
//	SET 4, D
//	2  8
//	- - - -
	public static void SET_4_D() {
		Cpu.D |= 0x10;
		Cpu.cycles += 8;
	}
	
//	SET 4, E
//	2  8
//	- - - -
	public static void SET_4_E() {
		Cpu.E |= 0x10;
		Cpu.cycles += 8;
	}
	
//	SET 4, H
//	2  8
//	- - - -
	public static void SET_4_H() {
		Cpu.H |= 0x10;
		Cpu.cycles += 8;
	}
	
//	SET 4, L
//	2  8
//	- - - -
	public static void SET_4_L() {
		Cpu.L |= 0x10;
		Cpu.cycles += 8;
	}
	
//	SET 4, (HL)
//	2  16
//	- - - -
	public static void SET_4_HL() {
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) | 0x10);
		Cpu.cycles += 16;
	}

//	SET 4, A
//	2  8
//	- - - -
	public static void SET_4_A() {
		Cpu.A |= 0x10;
		Cpu.cycles += 8;
	}
	
//	SET 5, B
//	2  8
//	- - - -
	public static void SET_5_B() {
		Cpu.B |= 0x20;
		Cpu.cycles += 8;
	}
	
//	SET 5, C
//	2  8
//	- - - -
	public static void SET_5_C() {
		Cpu.C |= 0x20;
		Cpu.cycles += 8;
	}
	
//	SET 5, D
//	2  8
//	- - - -
	public static void SET_5_D() {
		Cpu.D |= 0x20;
		Cpu.cycles += 8;
	}

//	SET 5, E
//	2  8
//	- - - -
	public static void SET_5_E() {
		Cpu.E |= 0x20;
		Cpu.cycles += 8;
	}

//	SET 5, H
//	2  8
//	- - - -
	public static void SET_5_H() {
		Cpu.H |= 0x20;
		Cpu.cycles += 8;
	}

//	SET 5, L
//	2  8
//	- - - -
	public static void SET_5_L() {
		Cpu.L |= 0x20;
		Cpu.cycles += 8;
	}

//	SET 5, (HL)
//	2  16
//	- - - -
	public static void SET_5_HL() {
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) | 0x20);
		Cpu.cycles += 16;
	}
	
//	SET 5, A
//	2  8
//	- - - -
	public static void SET_5_A() {
		Cpu.A |= 0x20;
		Cpu.cycles += 8;
	}

//	SET 6, B
//	2  8
//	- - - -
	public static void SET_6_B() {
		Cpu.B |= 0x40;
		Cpu.cycles += 8;
	}

//	SET 6, C
//	2  8
//	- - - -
	public static void SET_6_C() {
		Cpu.C |= 0x40;
		Cpu.cycles += 8;
	}
	
//	SET 6, D
//	2  8
//	- - - -
	public static void SET_6_D() {
		Cpu.D |= 0x40;
		Cpu.cycles += 8;
	}

//	SET 6, E
//	2  8
//	- - - -
	public static void SET_6_E() {
		Cpu.E |= 0x40;
		Cpu.cycles += 8;
	}

//	SET 6, H
//	2  8
//	- - - -
	public static void SET_6_H() {
		Cpu.H |= 0x40;
		Cpu.cycles += 8;
	}

//	SET 6, L
//	2  8
//	- - - -
	public static void SET_6_L() {
		Cpu.L |= 0x40;
		Cpu.cycles += 8;
	}

//	SET 6, (HL)
//	2  16
//	- - - -
	public static void SET_6_HL() {
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) | 0x40);
		Cpu.cycles += 16;
	}
	
//	SET 6, A
//	2  8
//	- - - -
	public static void SET_6_A() {
		Cpu.A |= 0x40;
		Cpu.cycles += 8;
	}

//	SET 7, B
//	2  8
//	- - - -
	public static void SET_7_B() {
		Cpu.B |= 0x80;
		Cpu.cycles += 8;
	}

//	SET 7, C
//	2  8
//	- - - -
	public static void SET_7_C() {
		Cpu.C |= 0x80;
		Cpu.cycles += 8;
	}
	
//	SET 7, D
//	2  8
//	- - - -
	public static void SET_7_D() {
		Cpu.D |= 0x80;
		Cpu.cycles += 8;
	}

//	SET 7, E
//	2  8
//	- - - -
	public static void SET_7_E() {
		Cpu.E |= 0x80;
		Cpu.cycles += 8;
	}

//	SET 7, H
//	2  8
//	- - - -
	public static void SET_7_H() {
		Cpu.H |= 0x80;
		Cpu.cycles += 8;
	}

//	SET 7, L
//	2  8
//	- - - -
	public static void SET_7_L() {
		Cpu.L |= 0x80;
		Cpu.cycles += 8;
	}

//	SET 7, (HL)
//	2  16
//	- - - -
	public static void SET_7_HL() {
		Main.mmu.setByte(Cpu.getHL(), Main.mmu.getByte(Cpu.getHL()) | 0x80);
		Cpu.cycles += 16;
	}
	
//	SET 7, A
//	2  8
//	- - - -
	public static void SET_7_A() {
		Cpu.A |= 0x80;
		Cpu.cycles += 8;
	}

}
