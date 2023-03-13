package gba;

public class InstructionSet {
//  #############
//	8-bit opcodes
//  #############
	
//	NOP
	public static void NOP() {
		// Do nothing
		Cpu.cycles += 4;
	}
	
//	LD BC, d16
	public static void LD_BC_d16() {
		Cpu.C = Cpu.fetch();
		Cpu.B = Cpu.fetch();
	}
	
//	LD (BC), A
	public static void LD_BC_A() {
		Ram.setByte(Cpu.getBC(), Cpu.A);
	}
	
//	INC BC
	public static void INC_BC() {
		int aux = Cpu.getBC();
		aux += 1;
		Cpu.B = ((aux >> 8) & 0xff); // Set high 8 bits
		Cpu.C = (aux & 0xff); // Set low 8 bits
	}
	
//	 INC B
	public static void INC_B() {
		Cpu.checkHalfCarry8bit(Cpu.B,1); // Check if there is Half Carry before the operation
		Cpu.B = (Cpu.B + 1) & 0xff;
		Cpu.checkZero8bit(Cpu.B);
		Cpu.resetFlagN();
	}
	
//	 DEC B
	public static void DEC_B() {
		Cpu.checkHalfCarry8bitSub(Cpu.B,1); // Check if there is Half Carry before the operation
		Cpu.B = (Cpu.B - 1) & 0xff;
		Cpu.checkZero8bit(Cpu.B);
		Cpu.setFlagN();
	}
	
//	 LD B, d8
	public static void LD_B_d8() {
		Cpu.B = Cpu.fetch();
	}
	
//	RLCA
//	0 0 0 A7
	public static void RLCA() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		Cpu.A = ((Cpu.A << 1) | (Cpu.A >> 7)) & 0xff; // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.A & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	LD (a16), SP
	public static void LD_a16_SP() {
		int index = ((Cpu.fetch() << 8) + Cpu.fetch()) & 0xffff;
		Ram.setByte(index, Cpu.SP & 0xFF);
		Ram.setByte(index + 1, (Cpu.SP >>8) & 0xff);
	}
	
//	ADD HL, BC
//	- 0 H CY
	public static void ADD_HL_BC() {
		Cpu.resetFlagN();
		Cpu.checkHalfCarry16bit(Cpu.getHL(), Cpu.getBC());
		int res = (Cpu.getHL() + Cpu.getBC());
		Cpu.checkCarry16bit(res);
		Cpu.L = res & 0xff;
		Cpu.H = (res >> 8) & 0xff;
	}
	
//	 LD A, (BC)
	public static void LD_A_BC() {
		Cpu.A = Ram.getByte(Cpu.getBC());
	}
	
//	DEC BC
	public static void DEC_BC() {
		int aux = Cpu.getBC();
		aux -= 1;
		Cpu.B = ((aux >> 8) & 0xff); // Set high 8 bits
		Cpu.C = (aux & 0xff); // Set low 8 bits
	}
	
//	INC C
	public static void INC_C() {
		Cpu.checkHalfCarry8bit(Cpu.C,1); // Check if there is Half Carry before the operation
		Cpu.C = (Cpu.C + 1) & 0xff;
		Cpu.checkZero8bit(Cpu.C);
		Cpu.resetFlagN();
	}
	
//	DEC C
	public static void DEC_C() {
		Cpu.checkHalfCarry8bitSub(Cpu.C,1); // Check if there is Half Carry before the operation
		Cpu.C = (Cpu.C - 1) & 0xff;
		Cpu.checkZero8bit(Cpu.C);
		Cpu.setFlagN();
	}
	
//	 LD C, d8
	public static void LD_C_d8() {
		Cpu.C = Cpu.fetch();
	}
	
//	RRCA
//	0 0 0 A0
	public static void RRCA() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.A = ((Cpu.A << 7) | (Cpu.A >> 1)) & 0xff; // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.A & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	STOP
	public static void STOP() {
		
	}
	
//	LD DE, d16 
	public static void LD_DE_d16() {
		Cpu.E = Cpu.fetch();
		Cpu.D = Cpu.fetch();
	}
	
//	LD (DE), A
	public static void LD_DE_A() {
		Ram.setByte(Cpu.getDE(), Cpu.A);
	}
	
//	INC DE
	public static void INC_DE() {
		int aux = Cpu.getDE();
		aux += 1;
		Cpu.D = ((aux >> 8) & 0xff); // Set high 8 bits
		Cpu.E = (aux & 0xff); // Set low 8 bits
	}
	
//	INC D
	public static void INC_D() {
		Cpu.checkHalfCarry8bit(Cpu.D,1); // Check if there is Half Carry before the operation
		Cpu.D = (Cpu.D + 1) & 0xff;
		Cpu.checkZero8bit(Cpu.D);
		Cpu.resetFlagN();
	}

//	DEC D
	public static void DEC_D() {
		Cpu.checkHalfCarry8bitSub(Cpu.D,1); // Check if there is Half Carry before the operation
		Cpu.D = (Cpu.D - 1) & 0xff;
		Cpu.checkZero8bit(Cpu.D);
		Cpu.setFlagN();
	}
	
//	LD D, d8
	public static void LD_D_d8() {
		Cpu.D = Cpu.fetch();
	}
	
//	RLA
//	0 0 0 A7
	public static void RLA() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Cpu.A = ((Cpu.A << 1) | (Cpu.A >> 7)) & 0xff; // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.A & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.A = Cpu.A & c;
	}

//	JR s8
	public static void JR_s8() {
		Cpu.PC = (Cpu.PC + Ram.getByte(Cpu.fetch())) & 0xffff;
	}
	
//	ADD HL, DE
//	- 0 H CY
	public static void ADD_HL_DE() {
		Cpu.resetFlagN();
		Cpu.checkHalfCarry16bit(Cpu.getHL(), Cpu.getDE());
		int res = (Cpu.getHL() + Cpu.getDE());
		Cpu.checkCarry16bit(res);
		Cpu.L = res & 0xff;
		Cpu.H = (res >> 8) & 0xff;
	}

//	LD A, (DE)
	public static void LD_A_DE() {
		Cpu.A = Ram.getByte(Cpu.getDE());
	}

//	DEC DE
	public static void DEC_DE() {
		int aux = Cpu.getDE();
		aux -= 1;
		Cpu.D = ((aux >> 8) & 0xff); // Set high 8 bits
		Cpu.E = (aux & 0xff); // Set low 8 bits
	}

//	INC E
	public static void INC_E() {
		Cpu.checkHalfCarry8bit(Cpu.E,1); // Check if there is Half Carry before the operation
		Cpu.E = (Cpu.E + 1) & 0xff;
		Cpu.checkZero8bit(Cpu.E);
		Cpu.resetFlagN();
	}

//	DEC E
	public static void DEC_E() {
		Cpu.checkHalfCarry8bitSub(Cpu.E,1); // Check if there is Half Carry before the operation
		Cpu.E = (Cpu.E - 1) & 0xff;
		Cpu.checkZero8bit(Cpu.E);
		Cpu.setFlagN();
	}

//	 LD E, d8
	public static void LD_E_d8() {
		Cpu.E = Cpu.fetch();
	}
	
//	RRA
//	0 0 0 A7
	public static void RRA() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Cpu.A = ((Cpu.A << 7) | (Cpu.A >> 1)) & 0xff; // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.A & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.A = Cpu.A & c;
	}
	
//	JR NZ, s8
	public static void JR_NZ_s8() {
		if (Cpu.getFlagZ() == 0) {
			JR_s8();
		}
	}

//	LD HL, d16
	public static void LD_HL_d16() {
		Cpu.L = Cpu.fetch();
		Cpu.H = Cpu.fetch();
	}

//	 LD (HL+), A
	public static void LD_HLinc_A() {
		Ram.setByte(Cpu.getHL(), Cpu.A);
		
		// TO DO: que pasa cuando incremento enteros paso los 8 bits y luego los paso a la ram?
		// en principio parece no generar problemas.
		int aux  = Cpu.getHL() + 1;
		Cpu.L = aux & 0xFF; // Set the lower 8 bits
		Cpu.H = (aux >> 8) & 0xff; // Set the higher 8 bits
	}

//	INC HL
	public static void INC_HL() {
		int aux = Cpu.getHL();
		aux += 1;
		Cpu.H = ((aux >> 8) & 0xff); // Set high 8 bits
		Cpu.L = (aux & 0xff); // Set low 8 bits
	}

//	INC H
	public static void INC_H() {
		Cpu.checkHalfCarry8bit(Cpu.H,1); // Check if there is Half Carry before the operation
		Cpu.H = (Cpu.H + 1) & 0xff;
		Cpu.checkZero8bit(Cpu.H);
		Cpu.resetFlagN();
	}

//	DEC H
	public static void DEC_H() {
		Cpu.checkHalfCarry8bitSub(Cpu.H,1); // Check if there is Half Carry before the operation
		Cpu.H = (Cpu.H - 1) & 0xff;
		Cpu.checkZero8bit(Cpu.H);
		Cpu.setFlagN();
	}

//	LD H, d8
	public static void LD_H_d8() {
		Cpu.H = Cpu.fetch();
	}
	
//	DAA
	public static void DAA() {
//		TO DO: no entiendo que hace
	}

//	JR Z, s8
	public static void JR_Z_s8() {
		if (Cpu.getFlagZ() == 1) {
			JR_s8();
		}
	}
	
//	ADD HL, HL
//	- 0 H CY
	public static void ADD_HL_HL() {
		Cpu.resetFlagN();
		Cpu.checkHalfCarry16bit(Cpu.getHL(), Cpu.getHL());
		int res = (Cpu.getHL() + Cpu.getHL());
		Cpu.checkCarry16bit(res);
		Cpu.L = res & 0xff;
		Cpu.H = (res >> 8) & 0xff;
	}

//	LD A, (HL+)
	public static void LD_A_HLinc() {
		Cpu.A = Ram.getByte(Cpu.getHL());
		int aux  = Cpu.getHL() + 1;
		Cpu.L = aux & 0xFF; // Set the lower 8 bits
		Cpu.H = (aux >> 8) & 0xff; // Set the higher 8 bits
	}

//	DEC HL
	public static void DEC_HL() {
		int aux = Cpu.getHL();
		aux -= 1;
		Cpu.H = ((aux >> 8) & 0xff); // Set high 8 bits
		Cpu.L = (aux & 0xff); // Set low 8 bits
	}

//	INC L
	public static void INC_L() {
		Cpu.checkHalfCarry8bit(Cpu.L,1); // Check if there is Half Carry before the operation
		Cpu.L = (Cpu.L + 1) & 0xff;
		Cpu.checkZero8bit(Cpu.L);
		Cpu.resetFlagN();
	}

//	DEC L
	public static void DEC_L() {
		Cpu.checkHalfCarry8bitSub(Cpu.L,1); // Check if there is Half Carry before the operation
		Cpu.L = (Cpu.L - 1) & 0xff;
		Cpu.checkZero8bit(Cpu.L);
		Cpu.setFlagN();
	}

//	LD L, d8
	public static void LD_L_d8() {
		Cpu.L = Cpu.fetch();
	}
	
//	CPL
//	- 1 1 -
	public static void CPL() {
		Cpu.setFlagN();
		Cpu.setFlagH();
		Cpu.A = (~Cpu.A) & 0xff;
	}

//	JR NC, s8
	public static void JR_NC_s8() {
		if (Cpu.getFlagC() == 0) {
			JR_s8();
		}
	}

//	LD SP, d16
	public static void LD_SP_d16() {
		int value = (Cpu.fetch() << 8) + Cpu.fetch();
		Cpu.SP = value & 0xff;
	}

//	LD (HL-), A
	public static void LD_HLdec_A() {
		Ram.setByte(Cpu.getHL(), Cpu.A);
		
		// TO DO: que pasa cuando decremento enteros y luego los paso a la ram?
		// en principio parece no generar problemas.
		int aux  = Cpu.getHL() - 1;
		Cpu.L = aux & 0xFF; // Set the lower 8 bits
		Cpu.H = (aux >> 8) & 0xff; // Set the higher 8 bits
	}

//	INC SP
	public static void INC_SP() {
		Cpu.SP += 1;
	}

//	INC (HL)
	public static void INC_HLmem() {
		Cpu.checkHalfCarry8bit(Ram.getByte(Cpu.getHL()),1); // Check if there is Half Carry before the operation
		Ram.setByte(Cpu.getHL(), (Ram.getByte(Cpu.getHL()) + 1) & 0xff);
		Cpu.checkZero8bit(Ram.getByte(Cpu.getHL()));
		Cpu.resetFlagN();
	}

//	DEC (HL)
	public static void DEC_HLmem() {
		Cpu.checkHalfCarry8bitSub(Ram.getByte(Cpu.getHL()),1); // Check if there is Half Carry before the operation
		Ram.setByte(Cpu.getHL(), (Ram.getByte(Cpu.getHL()) - 1) & 0xff);
		Cpu.checkZero8bit(Ram.getByte(Cpu.getHL()));
		Cpu.setFlagN();
	}

//	LD (HL), d8
	public static void LD_HL_d8() {
		Ram.setByte(Cpu.getHL(), Cpu.fetch());
	}
	
//	SCF
//	- 0 0 !CY
	public static void SCF() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		if (Cpu.getFlagC() == 0) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}

//	JR C, s8
	public static void JR_C_s8() {
		if (Cpu.getFlagC() == 1) {
			JR_s8();
		}
	}
	
//	ADD HL, SP
//	- 0 H CY
	public static void ADD_HL_SP() {
		Cpu.resetFlagN();
		Cpu.checkHalfCarry16bit(Cpu.getHL(), Cpu.SP);
		int res = (Cpu.getHL() + Cpu.SP);
		Cpu.checkCarry16bit(res);
		Cpu.L = res & 0xff;
		Cpu.H = (res >> 8) & 0xff;
	}

//	LD A, (HL-)
	public static void LD_A_HLdec() {
		Cpu.A = Ram.getByte(Cpu.getHL());
		int aux  = Cpu.getHL() - 1;
		Cpu.L = aux & 0xFF; // Set the lower 8 bits
		Cpu.H = (aux >> 8) & 0xff; // Set the higher 8 bits
	}

//	DEC SP
	public static void DEC_SP() {
		Cpu.SP -= 1;
	}

//	INC A
	public static void INC_A() {
		Cpu.checkHalfCarry8bit(Cpu.A,1); // Check if there is Half Carry before the operation
		Cpu.A = (Cpu.A + 1) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}

//	DEC A
	public static void DEC_A() {
		Cpu.checkHalfCarry8bitSub(Cpu.A,1); // Check if there is Half Carry before the operation
		Cpu.A = (Cpu.A - 1) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
	}

//	LD A, d8
	public static void LD_A_d8() {
		Cpu.A = Cpu.fetch();
	}

//	CCF
//	- 0 0 !CY
	public static void CFF() {
		if (Cpu.getFlagC() == 0) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	LD B, B
	public static void LD_B_B() {
		Cpu.B = Cpu.B;
	}
	
//	LD B, C
	public static void LD_B_C() {
		Cpu.B = Cpu.C;
	}

//	LD B, D
	public static void LD_B_D() {
		Cpu.B = Cpu.D;
	}
	
//	LD B, E
	public static void LD_B_E() {
		Cpu.B = Cpu.E;
	}
	
//	LD B, H
	public static void LD_B_H() {
		Cpu.B = Cpu.H;
	}
	
//	LD B, L
	public static void LD_B_L() {
		Cpu.B = Cpu.L;
	}

//	 LD B, (HL)
	public static void LD_B_HL() {
		Cpu.B = Ram.getByte(Cpu.getHL());
	}

//	LD B, A
	public static void LD_B_A() {
		Cpu.B = Cpu.A;
	}
	
//	LD C, B
	public static void LD_C_B() {
		Cpu.C = Cpu.B;
	}
	
//	LD C, C
	public static void LD_C_C() {
		Cpu.C = Cpu.C;
	}
	
//	LD C, D
	public static void LD_C_D() {
		Cpu.C = Cpu.D;
	}
	
//	LD C, E
	public static void LD_C_E() {
		Cpu.C = Cpu.E;
	}
	
//	LD C, H
	public static void LD_C_H() {
		Cpu.C = Cpu.H;
	}
	
//	LD C, L
	public static void LD_C_L() {
		Cpu.C = Cpu.L;
	}
	
//	 LD C, (HL)
	public static void LD_C_HL() {
		Cpu.C = Ram.getByte(Cpu.getHL());
	}

//	LD C, A
	public static void LD_C_A() {
		Cpu.C = Cpu.A;
	}
	
//	LD D, B
	public static void LD_D_B() {
		Cpu.D = Cpu.B;
	}
	
//	LD D, C
	public static void LD_D_C() {
		Cpu.D = Cpu.C;
	}
	
//	LD D, D
	public static void LD_D_D() {
		Cpu.D = Cpu.D;
	}
	
//	LD D, E
	public static void LD_D_E() {
		Cpu.E = Cpu.E;
	}
	
//	LD D, H
	public static void LD_D_H() {
		Cpu.D = Cpu.H;
	}
	
//	LD D, L
	public static void LD_D_L() {
		Cpu.D = Cpu.L;
	}

//	LD D, (HL)
	public static void LD_D_HL() {
		Cpu.D = Ram.getByte(Cpu.getHL());
	}

//	LD D, A
	public static void LD_D_A() {
		Cpu.C = Cpu.A;
	}
	
//	LD E, B
	public static void LD_E_B() {
		Cpu.E = Cpu.B;
	}
	
//	LD E, C
	public static void LD_E_C() {
		Cpu.E = Cpu.C;
	}
	
//	LD E, D
	public static void LD_E_D() {
		Cpu.E = Cpu.D;
	}
	
//	LD E, E
	public static void LD_E_E() {
		Cpu.E = Cpu.E;
	}
	
//	LD E, H
	public static void LD_E_H() {
		Cpu.E = Cpu.H;
	}
	
//	LD E, L
	public static void LD_E_L() {
		Cpu.E = Cpu.L;
	}

//	LD E, (HL)
	public static void LD_E_HL() {
		Cpu.E = Ram.getByte(Cpu.getHL());
	}

//	LD E, A
	public static void LD_E_A() {
		Cpu.E = Cpu.A;
	}
		
//	LD H, B
	public static void LD_H_B() {
		Cpu.H = Cpu.B;
	}
	
//	LD H, C
	public static void LD_H_C() {
		Cpu.H = Cpu.C;
	}
	
//	LD H, D
	public static void LD_H_D() {
		Cpu.H = Cpu.D;
	}
	
//	LD H, E
	public static void LD_H_E() {
		Cpu.H = Cpu.E;
	}
	
//	LD H, H
	public static void LD_H_H() {
		Cpu.H = Cpu.H;
	}
	
//	LD H, L
	public static void LD_H_L() {
		Cpu.H = Cpu.L;
	}

//	LD H, (HL)
	public static void LD_H_HL() {
		Cpu.H = Ram.getByte(Cpu.getHL());
	}

//	LD H, A
	public static void LD_H_A() {
		Cpu.H = Cpu.A;
	}
	
//	LD L, B
	public static void LD_L_B() {
		Cpu.L = Cpu.B;
	}
	
//	LD L, C
	public static void LD_L_C() {
		Cpu.L = Cpu.C;
	}
	
//	LD L, D
	public static void LD_L_D() {
		Cpu.L = Cpu.D;
	}
	
//	LD L, E
	public static void LD_L_E() {
		Cpu.L = Cpu.E;
	}
	
//	LD L, H
	public static void LD_L_H() {
		Cpu.L = Cpu.H;
	}
	
//	LD L, L
	public static void LD_L_L() {
		Cpu.L = Cpu.L;
	}

//	LD L, (HL)
	public static void LD_L_HL() {
		Cpu.L = Ram.getByte(Cpu.getHL());
	}

//	LD L, A
	public static void LD_L_A() {
		Cpu.L = Cpu.A;
	}
	
//	LD (HL), B
	public static void LD_HL_B() {
		Ram.setByte(Cpu.getHL(), Cpu.B);
	}
	
//	LD (HL), C
	public static void LD_HL_C() {
		Ram.setByte(Cpu.getHL(), Cpu.C);
	}
	
//	LD (HL), D
	public static void LD_HL_D() {
		Ram.setByte(Cpu.getHL(), Cpu.D);
	}
	
//	LD (HL), E
	public static void LD_HL_E() {
		Ram.setByte(Cpu.getHL(), Cpu.E);
	}
	
//	LD (HL), H
	public static void LD_HL_H() {
		Ram.setByte(Cpu.getHL(), Cpu.H);
	}
	
//	LD (HL), L
	public static void LD_HL_L() {
		Ram.setByte(Cpu.getHL(), Cpu.L);
	}
	
//	HALT
	public static void HALT() {
		
	}
	
//	LD (HL), A
	public static void LD_HL_A() {
		Ram.setByte(Cpu.getHL(), Cpu.A);
	}

//	LD A, B
	public static void LD_A_B() {
		Cpu.A = Cpu.B;
	}
	
//	LD A, C
	public static void LD_A_C() {
		Cpu.A = Cpu.C;
	}
	
//	LD A, D
	public static void LD_A_D() {
		Cpu.A = Cpu.D;
	}
	
//	LD A, E
	public static void LD_A_E() {
		Cpu.A = Cpu.E;
	}
	
//	LD A, H
	public static void LD_A_H() {
		Cpu.A = Cpu.H;
	}
	
//	LD A, L
	public static void LD_A_L() {
		Cpu.A = Cpu.L;
	}

//	LD A, (HL)
	public static void LD_A_HL() {
		Cpu.A = Ram.getByte(Cpu.getHL());
	}
	
//	LD A, A
	public static void LD_A_A() {
		Cpu.A = Cpu.A;
	}

//	ADD A, B
//	Z 0 H C
	public static void ADD_A_B() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.B);
		Cpu.checkCarry8bit(Cpu.A + Cpu.B);
		Cpu.A = (Cpu.A + Cpu.B) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}
	
//	ADD A, C
//	Z 0 H C
	public static void ADD_A_C() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.C);
		Cpu.checkCarry8bit(Cpu.A + Cpu.C);
		Cpu.A = (Cpu.A + Cpu.C) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}
	
//	ADD A, D
//	Z 0 H C
	public static void ADD_A_D() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.D);
		Cpu.checkCarry8bit(Cpu.A + Cpu.D);
		Cpu.A = (Cpu.A + Cpu.D) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}
	
//	ADD A, E
//	Z 0 H C
	public static void ADD_A_E() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.E);
		Cpu.checkCarry8bit(Cpu.A + Cpu.E);
		Cpu.A = (Cpu.A + Cpu.E) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}
	
//	ADD A, H
//	Z 0 H C
	public static void ADD_A_H() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.H);
		Cpu.checkCarry8bit(Cpu.A + Cpu.H);
		Cpu.A = (Cpu.A + Cpu.H) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}
	
//	ADD A, L
//	Z 0 H C
	public static void ADD_A_L() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.L);
		Cpu.checkCarry8bit(Cpu.A + Cpu.L);
		Cpu.A = (Cpu.A + Cpu.L) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}
	
//	ADD A, (HL)
//	Z 0 H C
	public static void ADD_A_HL() {
		Cpu.checkHalfCarry8bit(Cpu.A, Ram.getByte(Cpu.getHL()));
		Cpu.checkCarry8bit(Cpu.A + Ram.getByte(Cpu.getHL()));
		Cpu.A = (Cpu.A + Ram.getByte(Cpu.getHL())) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		
		Cpu.resetFlagN();
	}

//	ADD A, A
//	Z 0 H C
	public static void ADD_A_A() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.A);
		Cpu.checkCarry8bit(Cpu.A + Cpu.A);
		Cpu.A = (Cpu.A + Cpu.A) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}
	
//	ADC A, B
//	Z 0 H C
	public static void ADC_A_B() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.B + Cpu.getFlagC());
		Cpu.checkCarry8bit(Cpu.A + Cpu.B + Cpu.getFlagC());
		Cpu.A = (Cpu.A + Cpu.B + Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}
	
//	ADC A, C
//	Z 0 H C
	public static void ADC_A_C() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.C + Cpu.getFlagC());
		Cpu.checkCarry8bit(Cpu.A + Cpu.C + Cpu.getFlagC());
		Cpu.A = (Cpu.A + Cpu.C + Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}
	
//	ADC A, D
//	Z 0 H C
	public static void ADC_A_D() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.D + Cpu.getFlagC());
		Cpu.checkCarry8bit(Cpu.A + Cpu.D + Cpu.getFlagC());
		Cpu.A = (Cpu.A + Cpu.D + Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}
	
//	ADC A, E
//	Z 0 H C
	public static void ADC_A_E() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.E + Cpu.getFlagC());
		Cpu.checkCarry8bit(Cpu.A + Cpu.E + Cpu.getFlagC());
		Cpu.A = (Cpu.A + Cpu.E + Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}
	
//	ADC A, H
//	Z 0 H C
	public static void ADC_A_H() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.H + Cpu.getFlagC());
		Cpu.checkCarry8bit(Cpu.A + Cpu.H + Cpu.getFlagC());
		Cpu.A = (Cpu.A + Cpu.H + Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}
	
//	ADC A, L
//	Z 0 H C
	public static void ADC_A_L() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.L + Cpu.getFlagC());
		Cpu.checkCarry8bit(Cpu.A + Cpu.L + Cpu.getFlagC());
		Cpu.A = (Cpu.A + Cpu.L + Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}
	
//	ADC A, (HL)
//	Z 0 H C
	public static void ADC_A_HL() {
		Cpu.checkHalfCarry8bit(Cpu.A, Ram.getByte(Cpu.getHL()) + Cpu.getFlagC());
		Cpu.checkCarry8bit(Cpu.A + Ram.getByte(Cpu.getHL()) + Cpu.getFlagC());
		Cpu.A = (Cpu.A + Ram.getByte(Cpu.getHL()) + Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}

//	ADC A, A
//	Z 0 H C
	public static void ADC_A_A() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.A + Cpu.getFlagC());
		Cpu.checkCarry8bit(Cpu.A + Cpu.A + Cpu.getFlagC());
		Cpu.A = (Cpu.A + Cpu.A + Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}
	
//	SUB B
//	Z 1 H C
	public static void SUB_A_B() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.B);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.B);
		Cpu.A = (Cpu.A - Cpu.B) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
	}
	
//	SUB C
//	Z 1 H C
	public static void SUB_A_C() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.C);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.C);
		Cpu.A = (Cpu.A - Cpu.C) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
	}
	
//	SUB D
//	Z 1 H C
	public static void SUB_A_D() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.D);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.D);
		Cpu.A = (Cpu.A - Cpu.D) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
	}
	
//	SUB E
//	Z 1 H C
	public static void SUB_A_E() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.E);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.E);
		Cpu.A = (Cpu.A - Cpu.E) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
	}
	
//	SUB H
//	Z 1 H C
	public static void SUB_A_H() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.H);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.H);
		Cpu.A = (Cpu.A - Cpu.H) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
	}
	
//	SUB L
//	Z 1 H C
	public static void SUB_A_L() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.L);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.L);
		Cpu.A = (Cpu.A - Cpu.L) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
	}
	
//	SUB (HL)
//	Z 1 H C
	public static void SUB_A_HL() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Ram.getByte(Cpu.getHL()));
		Cpu.checkCarry8bit(Cpu.A + Ram.getByte(Cpu.getHL()));
		Cpu.A = (Cpu.A - Ram.getByte(Cpu.getHL())) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
	}
	
//	SUB A
//	1 1 0 0
	public static void SUB_A_A() {
		Cpu.A = (Cpu.A - Cpu.A) & 0xff;
		Cpu.setFlagZ();
		Cpu.setFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		
	}
	
//	SBC A, B
//	Z 1 H C
	public static void SBC_A_B() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.B - Cpu.getFlagC());
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.B - Cpu.getFlagC());
		Cpu.A = (Cpu.A - Cpu.B - Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
	}
	
//	SBC A, C
//	Z 1 H C
	public static void SBC_A_C() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.C - Cpu.getFlagC());
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.C - Cpu.getFlagC());
		Cpu.A = (Cpu.A - Cpu.C - Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
	}
	
//	SBC A, D
//	Z 1 H C
	public static void SBC_A_D() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.D - Cpu.getFlagC());
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.D - Cpu.getFlagC());
		Cpu.A = (Cpu.A - Cpu.D - Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
	}
	
//	SBC A, E
//	Z 1 H C
	public static void SBC_A_E() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.E - Cpu.getFlagC());
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.E - Cpu.getFlagC());
		Cpu.A = (Cpu.A - Cpu.E - Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
	}
	
//	SBC A, H
//	Z 1 H C
	public static void SBC_A_H() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.H - Cpu.getFlagC());
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.H - Cpu.getFlagC());
		Cpu.A = (Cpu.A - Cpu.H - Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.H);
		Cpu.setFlagN();
	}
	
//	SBC A, L
//	Z 1 H C
	public static void SBC_A_L() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.L - Cpu.getFlagC());
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.L - Cpu.getFlagC());
		Cpu.A = (Cpu.A - Cpu.L - Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
	}
	
//	SBC A, (HL)
//	Z 1 H C
	public static void SBC_A_HL() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Ram.getByte(Cpu.getHL()) - Cpu.getFlagC());
		Cpu.checkCarry8bit(Cpu.A - Ram.getByte(Cpu.getHL()) - Cpu.getFlagC());
		Cpu.A = (Cpu.A - Ram.getByte(Cpu.getHL()) - Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
	}
	
//	SBC A, A
//	Z 1 H -
	public static void SBC_A_A() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.B - Cpu.getFlagC());
		Cpu.A = (Cpu.A - Cpu.B - Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
	}	
	
//	 AND B
//	 Z 0 1 0
	public static void AND_B() {
		Cpu.A = Cpu.A & Cpu.B;
		Cpu.F &= 0x2f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}	
	
//	 AND C
//	 Z 0 1 0
	public static void AND_C() {
		Cpu.A = Cpu.A & Cpu.C;
		Cpu.F &= 0x2f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	 AND D
//	 Z 0 1 0
	public static void AND_D() {
		Cpu.A = Cpu.A & Cpu.D;
		Cpu.F &= 0x2f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	AND E
//	Z 0 1 0
	public static void AND_E() {
		Cpu.A = Cpu.A & Cpu.E;
		Cpu.F &= 0x2f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	AND H
//	Z 0 1 0
	public static void AND_H() {
		Cpu.A = Cpu.A & Cpu.H;
		Cpu.F &= 0x2f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	AND L
//	Z 0 1 0
	public static void AND_L() {
		Cpu.A = Cpu.A & Cpu.L;
		Cpu.F &= 0x2f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	AND (HL)
//	Z 0 1 0
	public static void AND_HL() {
		Cpu.A = Cpu.A & Ram.getByte(Cpu.getHL());
		Cpu.F &= 0x2f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	AND A
//	Z 0 1 0
	public static void AND_A() {
		Cpu.A = Cpu.A & Cpu.A;
		Cpu.F &= 0x2f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	XOR B
//	Z 0 0 0
	public static void XOR_B() {
		Cpu.A = Cpu.A ^ Cpu.B;
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	XOR C
//	Z 0 0 0
	public static void XOR_C() {
		Cpu.A = Cpu.A ^ Cpu.C;
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}

//	XOR D
//	Z 0 0 0
	public static void XOR_D() {
		Cpu.A = Cpu.A ^ Cpu.D;
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	XOR E
//	Z 0 0 0
	public static void XOR_E() {
		Cpu.A = Cpu.A ^ Cpu.E;
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	XOR H
//	Z 0 0 0
	public static void XOR_H() {
		Cpu.A = Cpu.A ^ Cpu.H;
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	XOR L
//	Z 0 0 0
	public static void XOR_L() {
		Cpu.A = Cpu.A ^ Cpu.L;
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	XOR (HL)
//	Z 0 0 0
	public static void XOR_HL() {
		Cpu.A = Cpu.A ^ Ram.getByte(Cpu.getHL());
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	XOR A
//	1 0 0 0
	public static void XOR_A() {
		Cpu.A = Cpu.A ^ Cpu.A;
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	OR B
//	Z 0 0 0
	public static void OR_B() {
		Cpu.A = Cpu.A | Cpu.B;
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	OR C
//	Z 0 0 0
	public static void OR_C() {
		Cpu.A = Cpu.A | Cpu.C;
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	OR D
//	Z 0 0 0
	public static void OR_D() {
		Cpu.A = Cpu.A | Cpu.D;
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	OR E
//	Z 0 0 0
	public static void OR_E() {
		Cpu.A = Cpu.A | Cpu.E;
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	OR H
//	Z 0 0 0
	public static void OR_H() {
		Cpu.A = Cpu.A | Cpu.H;
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	OR L
//	Z 0 0 0
	public static void OR_L() {
		Cpu.A = Cpu.A | Cpu.L;
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	OR (HL)
//	Z 0 0 0
	public static void OR_HL() {
		Cpu.A = Cpu.A | Ram.getByte(Cpu.getHL());
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}
	
//	OR A
//	Z 0 0 0
	public static void OR_A() {
		Cpu.A = Cpu.A | Cpu.A;
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}

//	CP B
//	Z 1 H C
	public static void CP_B() {
		Cpu.checkZero8bit(Cpu.A - Cpu.B);
		Cpu.setFlagN();
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.B);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.B);
	}

//	CP C
//	Z 1 H C
	public static void CP_C() {
		Cpu.checkZero8bit(Cpu.A - Cpu.C);
		Cpu.setFlagN();
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.C);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.C);
	}
	
//	CP D
//	Z 1 H C
	public static void CP_D() {
		Cpu.checkZero8bit(Cpu.A - Cpu.D);
		Cpu.setFlagN();
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.D);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.D);
	}
	
//	CP E
//	Z 1 H C
	public static void CP_E() {
		Cpu.checkZero8bit(Cpu.A - Cpu.E);
		Cpu.setFlagN();
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.E);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.E);
	}
	
//	CP H
//	Z 1 H C
	public static void CP_H() {
		Cpu.checkZero8bit(Cpu.A - Cpu.H);
		Cpu.setFlagN();
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.H);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.H);
	}
	
//	CP L
//	Z 1 H C
	public static void CP_L() {
		Cpu.checkZero8bit(Cpu.A - Cpu.L);
		Cpu.setFlagN();
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.L);
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.L);
	}
	
//	CP (HL)
//	Z 1 H C
	public static void CP_HL() {
		Cpu.checkZero8bit(Cpu.A - Ram.getByte(Cpu.getHL()));
		Cpu.setFlagN();
		Cpu.checkHalfCarry8bitSub(Cpu.A, Ram.getByte(Cpu.getHL()));
		Cpu.checkCarry8bitSub(Cpu.A, Ram.getByte(Cpu.getHL()));
	}
	
//	CP A
//	1 1 0 0
	public static void CP_A() {
		Cpu.setFlagZ();
		Cpu.setFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
	}
	
//	RET NZ
	public static void RET_NZ() {
		if (Cpu.getFlagZ() == 0) {
			RET();
		}
	}
	
//	POP BC
	public static void POP_BC() {
		Cpu.C = Ram.getByte(Cpu.SP);
		Cpu.SP += 1;
		Cpu.B = Ram.getByte(Cpu.SP);
		Cpu.SP += 1;
	}
	
//	JP NZ, a16
	public static void JP_NZ_a16() {
		if (Cpu.getFlagZ() == 0) {
			JP_a16();
		}
	}

//	JP a16
	public static void JP_a16() {
		int l = Ram.getByte(Cpu.fetch());
		int h = Ram.getByte(Cpu.fetch());
		int res = (h << 8) + l;
		Cpu.PC = (Cpu.PC + res) & 0xffff;
	}
	
//	CALL NZ, a16
	public static void CALL_NZ_a16( ) {
		if (Cpu.getFlagZ() == 0) {
			CALL_a16();
		}
	}
	
//	PUSH BC
	public static void PUSH_BC() {
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, Cpu.B);
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, Cpu.C);
	}

//	ADD A, d8
//	Z 0 H C
	public static void ADD_A_d8() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.fetch());
		Cpu.checkCarry8bit(Cpu.A + Cpu.fetch());
		Cpu.A = (Cpu.A + Cpu.fetch()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}
	
//	RST 0
	public static void RST_0() {
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
		Cpu.PC = Ram.getByte(0x00);
	}
	
//	RET Z
	public static void RET_Z() {
		if (Cpu.getFlagZ() == 1) {
			RET();
		}
	}

//	RET
	public static void RET() {
		int l = Ram.getByte(Cpu.fetchSP());
		int h = Ram.getByte(Cpu.fetchSP());
		int res = (h << 8) + l;
		Cpu.PC = res & 0xffff;
	}

//	JP Z, a16
	public static void JP_Z_a16() {
		if (Cpu.getFlagZ() == 1) {
			JP_a16();
		}
	}
	
//	PREFIX
	public static void PREFIX() {
		// TO DO:
	}
	
//	CALL Z, a16
	public static void CALL_Z_a16( ) {
		if (Cpu.getFlagZ() == 1) {
			CALL_a16();
		}
	}
	
//	CALL a16
	public static void CALL_a16() {
		int l = Ram.getByte(Cpu.fetch());
		int h = Ram.getByte(Cpu.fetch());
		int res = (h << 8) + l;
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
		Cpu.PC = res & 0xffff;
	}
	
//	ADC A, d8
//	Z 0 H C
	public static void ADC_A_d8() {
		Cpu.checkHalfCarry8bit(Cpu.A, Cpu.fetch() + Cpu.getFlagC());
		Cpu.checkCarry8bit(Cpu.A + Cpu.fetch() + Cpu.getFlagC());
		Cpu.A = (Cpu.A + Cpu.fetch() + Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.resetFlagN();
	}
	
//	RST 1
	public static void RST_1() {
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
		Cpu.PC = Ram.getByte(0x08);
	}

//	RET NC
	public static void RET_NC() {
		if (Cpu.getFlagC() == 0) {
			RET();
		}
	}

//	POP DE
	public static void POP_DE() {
		Cpu.E = Ram.getByte(Cpu.SP);
		Cpu.SP += 1;
		Cpu.D = Ram.getByte(Cpu.SP);
		Cpu.SP += 1;
	}

//	JP NC, a16
	public static void JP_NC_a16() {
		if (Cpu.getFlagC() == 1) {
			JP_a16();
		}
	}

//	CALL NC, a16
	public static void CALL_NC_a16( ) {
		if (Cpu.getFlagC() == 0) {
			CALL_a16();
		}
	}

//	PUSH DE
	public static void PUSH_DE() {
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, Cpu.D);
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, Cpu.E);
	}

//	SUB d8
//	Z 1 H C
	public static void SUB_A_d8() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.fetch());
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.fetch());
		Cpu.A = (Cpu.A - Cpu.fetch()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
	}

//	RST 2
	public static void RST_2() {
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
		Cpu.PC = Ram.getByte(0x10);
	}

//	RET C
	public static void RET_C() {
		if (Cpu.getFlagC() == 1) {
			RET();
		}
	}

//	RETI
//	Entiendo que hace lo mismo que RET pero se usa en diferentes circunstancias
	public static void RETI() {
		int l = Ram.getByte(Cpu.fetchSP());
		int h = Ram.getByte(Cpu.fetchSP());
		int res = (h << 8) + l;
		Cpu.PC = res & 0xffff;
	}

//	JP C, a16
	public static void JP_C_a16() {
		if (Cpu.getFlagC() == 1) {
			JP_a16();
		}
	}

//	CALL C, a16
	public static void CALL_C_a16( ) {
		if (Cpu.getFlagC() == 1) {
			CALL_a16();
		}
	}

//	SBC A, d8
//	Z 1 H C
	public static void SBC_A_d8() {
		Cpu.checkHalfCarry8bitSub(Cpu.A, Cpu.fetch() - Cpu.getFlagC());
		Cpu.checkCarry8bitSub(Cpu.A, Cpu.fetch() - Cpu.getFlagC());
		Cpu.A = (Cpu.A - Cpu.fetch() - Cpu.getFlagC()) & 0xff;
		Cpu.checkZero8bit(Cpu.A);
		Cpu.setFlagN();
	}	

//	RST 3
	public static void RST_3() {
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
		Cpu.PC = Ram.getByte(0x18);
	}
	
//	LD (a8), A
	public static void LD_a8_A() {
		// TO DO: no entendí bien que hace esta instrucción
		// no se si a8 es un parámetro o es un espacio de memoria específico
		Ram.setByte(Cpu.fetch(), Cpu.A);
	}

//	POP HL
	public static void POP_HL() {
		Cpu.L = Ram.getByte(Cpu.SP);
		Cpu.SP += 1;
		Cpu.H = Ram.getByte(Cpu.SP);
		Cpu.SP += 1;
	}
	
//	LD (C), A
	public static void LD_Cmem_A() {
		Ram.setByte(Cpu.C, Cpu.A);
	}

//	PUSH HL
	public static void PUSH_HL() {
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, Cpu.H);
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, Cpu.L);
	}
	
//	AND d8
//	Z 0 1 0
	public static void AND_d8() {
		Cpu.A = Cpu.A & Cpu.fetch();
		Cpu.F &= 0x2f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}

//	RST 4
	public static void RST_4() {
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
		Cpu.PC = Ram.getByte(0x20);
	}
	
//	ADD SP, s8
//	0 0 16-bit 16-bit
	public static void ADD_SP_s8() {
//		TO DO: no entiendo que hace
	}
	
//	JP HL
	public static void JP_HL() {
		int res = (Cpu.H << 8) + Cpu.L;
		Cpu.PC = res & 0xffff;
	}
	
//	LD (a16), A
	public static void LD_a16_A() {
		int index = ((Cpu.fetch() << 8) + Cpu.fetch()) & 0xffff;
		Ram.setByte(index, Cpu.A);
	}
	
//	XOR d8
//	Z 0 0 0
	public static void XOR_d8() {
		Cpu.A = Cpu.A ^ Cpu.fetch();
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}

//	RST 5
	public static void RST_5() {
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
		Cpu.PC = Ram.getByte(0x28);
	}

//	LD A, (a8)
	public static void LD_A_a8() {
		Cpu.A = Ram.getByte(Cpu.fetch());
	}

//	POP AF
	public static void POP_AF() {
		Cpu.F = Ram.getByte(Cpu.SP);
		Cpu.SP += 1;
		Cpu.A = Ram.getByte(Cpu.SP);
		Cpu.SP += 1;
	}

//	LD A, (C)
	public static void LD_A_Cmem() {
		Cpu.A = Ram.getByte(Cpu.C);
	}

//	DI
	public static void DI() {
		Cpu.IME = false;
	}

//	PUSH AF
	public static void PUSH_AF() {
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, Cpu.A);
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, Cpu.F);
	}

//	OR d8
//	Z 0 0 0
	public static void OR_d8() {
		Cpu.A = Cpu.A | Cpu.fetch();
		Cpu.F &= 0x0f;
		if (Cpu.A == 0x00) {
			Cpu.F |= 0x10; // Set Carry flag to 1
		};
	}

//	RST 6
	public static void RST_6() {
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
		Cpu.PC = Ram.getByte(0x30);
	}
	
//	LD HL, SP+s8
//	0 0 16-bit 16-bit
	public static void LD_HL_SPplusS8() {
//		TO DO: no entiendo que hace
	}

//	LD SP, HL
	public static void LD_SP_HL() {
		int value = (Cpu.H << 8) + Cpu.L;
		Cpu.SP = value;
	}

//	LD A, (a16)
	public static void LD_A_a16() {
		int index = ((Cpu.fetch() << 8) + Cpu.fetch()) & 0xffff;
		Cpu.A = Ram.getByte(index);
	}

//	EI
	public static void EI() {
		Cpu.IME = true;
	}
	
//	CP d8
//	Z 1 8-bit 8-bit
	public static void CP_d8() {
		int d8 = Cpu.fetch();
		Cpu.checkZero8bit(Cpu.A - d8);
		Cpu.setFlagN();
		Cpu.checkHalfCarry8bitSub(Cpu.A, d8);
		Cpu.checkCarry8bitSub(Cpu.A, d8);
	}

//	RST 7
	public static void RST_7() {
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, (Cpu.PC & 0xff00) >> 8); // High byte of PC
		Cpu.SP -= 1;
		Ram.setByte(Cpu.SP, Cpu.PC & 0xff); // Low byte of PC
		Cpu.PC = Ram.getByte(0x38);
	}

// 	###############################################	
//	16-bit opcodes, where the first 8 bits are 0xCB
// 	###############################################	
	
//	RLC B
//	2  8
//	Z 0 0 C
	public static void RLC_B() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		Cpu.B = ((Cpu.B << 1) | (Cpu.B >> 7)) & 0xff; // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.B & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	RLC C
//	2  8
//	Z 0 0 C
	public static void RLC_C() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		Cpu.C = ((Cpu.C << 1) | (Cpu.C >> 7)) & 0xff; // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.C & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	RLC D
//	2  8
//	Z 0 0 C
	public static void RLC_D() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		Cpu.D = ((Cpu.D << 1) | (Cpu.D >> 7)) & 0xff; // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.D & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	RLC E
//	2  8
//	Z 0 0 C
	public static void RLC_E() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		Cpu.E = ((Cpu.E << 1) | (Cpu.E >> 7)) & 0xff; // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.E & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	RLC H
//	2  8
//	Z 0 0 C
	public static void RLC_H() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		Cpu.H = ((Cpu.H << 1) | (Cpu.H >> 7)) & 0xff; // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.H & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	RLC L
//	2  8
//	Z 0 0 C
	public static void RLC_L() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		Cpu.L = ((Cpu.L << 1) | (Cpu.L >> 7)) & 0xff; // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.L & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	RLC (HL)
//	2  16
//	Z 0 0 C
	public static void RLC_HL() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		Ram.setByte(Cpu.getHL(), ((Ram.getByte(Cpu.getHL()) << 1) | (Ram.getByte(Cpu.getHL()) >> 7)) & 0xff); // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Ram.getByte(Cpu.getHL()) & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	RLC A
//	2  8
//	Z 0 0 C
	public static void RLC_A() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		Cpu.A = ((Cpu.A << 1) | (Cpu.A >> 7)) & 0xff; // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.A & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	RRC B
//	2  8
//	Z 0 0 C
	public static void RRC_B() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.B = ((Cpu.B << 7) | (Cpu.B >> 1)) & 0xff; // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.B & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	RRC C
//	2  8
//	Z 0 0 C
	public static void RRC_C() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.C = ((Cpu.C << 7) | (Cpu.C >> 1)) & 0xff; // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.C & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	RRC D
//	2  8
//	Z 0 0 C
	public static void RRC_D() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.D = ((Cpu.D << 7) | (Cpu.D >> 1)) & 0xff; // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.D & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	RRC E
//	2  8
//	Z 0 0 C
	public static void RRC_E() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.E = ((Cpu.E << 7) | (Cpu.E >> 1)) & 0xff; // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.E & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	RRC H
//	2  8
//	Z 0 0 C
	public static void RRC_H() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.H = ((Cpu.H << 7) | (Cpu.H >> 1)) & 0xff; // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.H & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	RRC L
//	2  8
//	Z 0 0 C
	public static void RRC_L() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.L = ((Cpu.L << 7) | (Cpu.L >> 1)) & 0xff; // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.L & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	RRC (HL)
//	2  16
//	Z 0 0 C
	public static void RRC_HL() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Ram.setByte(Cpu.getHL(), ((Ram.getByte(Cpu.getHL()) << 7) | (Ram.getByte(Cpu.getHL()) >> 1)) & 0xff); // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Ram.getByte(Cpu.getHL()) & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	RRC A
//	2  8
//	Z 0 0 C
	public static void RRC_A() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.A = ((Cpu.A << 7) | (Cpu.A >> 1)) & 0xff; // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.A & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
	}
	
//	RL B
//	2  8
//	Z 0 0 C
	public static void RL_B() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Cpu.B = ((Cpu.B << 1) | (Cpu.B >> 7)) & 0xff; // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.B & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.B = Cpu.B & c;
	}
	
//	RL C
//	2  8
//	Z 0 0 C
	public static void RL_C() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Cpu.C = ((Cpu.C << 1) | (Cpu.C >> 7)) & 0xff; // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.C & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.C = Cpu.C & c;
	}
	
//	RL D
//	2  8
//	Z 0 0 C
	public static void RL_D() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Cpu.D = ((Cpu.D << 1) | (Cpu.D >> 7)) & 0xff; // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.D & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.D = Cpu.D & c;
	}
	
//	RL E
//	2  8
//	Z 0 0 C
	public static void RL_E() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Cpu.E = ((Cpu.E << 1) | (Cpu.E >> 7)) & 0xff; // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.E & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.E = Cpu.E & c;
	}
	
//	RL H
//	2  8
//	Z 0 0 C
	public static void RL_H() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Cpu.H = ((Cpu.H << 1) | (Cpu.H >> 7)) & 0xff; // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.H & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.H = Cpu.H & c;
	}
	
//	RL L
//	2  8
//	Z 0 0 C
	public static void RL_L() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Cpu.L = ((Cpu.L << 1) | (Cpu.L >> 7)) & 0xff; // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.L & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.L = Cpu.L & c;
	}
	
//	RL (HL)
//	2  16
//	Z 0 0 C
	public static void RL_HL() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Ram.setByte(Ram.getByte(Cpu.getHL()),  ((Ram.getByte(Cpu.getHL()) << 1) | (Ram.getByte(Cpu.getHL()) >> 7)) & 0xff); // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Ram.getByte(Cpu.getHL()) & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) & c);
	}
	
//	RL A
//	2  8
//	Z 0 0 C
	public static void RL_A() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Cpu.A = ((Cpu.A << 1) | (Cpu.A >> 7)) & 0xff; // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.A & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.A = Cpu.A & c;
	}
	
//	RR B
//	2  8
//	Z 0 0 C
	public static void RR_B() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Cpu.B = ((Cpu.B << 7) | (Cpu.B >> 1)) & 0xff; // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.B & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.B = Cpu.B & c;
	}
	
//	RR C
//	2  8
//	Z 0 0 C
	public static void RR_C() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Cpu.C = ((Cpu.C << 7) | (Cpu.C >> 1)) & 0xff; // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.C & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.C = Cpu.C & c;
	}
		
//	RR D
//	2  8
//	Z 0 0 C
	public static void RR_D() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Cpu.D = ((Cpu.D << 7) | (Cpu.D >> 1)) & 0xff; // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.D & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.D = Cpu.D & c;
	}
	
//	RR E
//	2  8
//	Z 0 0 C
	public static void RR_E() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Cpu.E = ((Cpu.E << 7) | (Cpu.E >> 1)) & 0xff; // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.E & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.E = Cpu.E & c;
	}
	
//	RR H
//	2  8
//	Z 0 0 C
	public static void RR_H() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Cpu.H = ((Cpu.H << 7) | (Cpu.H >> 1)) & 0xff; // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.H & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.H = Cpu.H & c;
	}
	
//	RR L
//	2  8
//	Z 0 0 C
	public static void RR_L() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Cpu.L = ((Cpu.L << 7) | (Cpu.L >> 1)) & 0xff; // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.L & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.L = Cpu.L & c;
	}
	
//	RR (HL)
//	2  16
//	Z 0 0 C
	public static void RR_HL() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Ram.setByte(Cpu.getHL(), ((Ram.getByte(Cpu.getHL()) << 7) | (Ram.getByte(Cpu.getHL()) >> 1)) & 0xff); // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Ram.getByte(Cpu.getHL()) & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) & c);
	}
	
//	RR A
//	2  8
//	Z 0 0 C
	public static void RR_A() {
		Cpu.resetFlagZ();
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int c = Cpu.getFlagC() | 0xfe; // esto lo hago para que quede en el formato 1111 111(Carry Flag) y luego poder setearlo en el bit 0 de A con un &
		
		Cpu.A = ((Cpu.A << 7) | (Cpu.A >> 1)) & 0xff; // Rotate to right
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Cpu.A & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.A = Cpu.A & c;
	}
	
//	SLA B
//	2  8
//	Z 0 0 C
	public static void SLA_B() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		int res  = Cpu.B << 1; // Shift to the left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((res & 0x100) == 0x100) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.B = res & 0xff;
	}
	
//	SLA C
//	2  8
//	Z 0 0 C
	public static void SLA_C() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		int res  = Cpu.C << 1; // Shift to the left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((res & 0x100) == 0x100) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.C = res & 0xff;
	}
	
//	SLA D
//	2  8
//	Z 0 0 C
	public static void SLA_D() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		int res  = Cpu.D << 1; // Shift to the left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((res & 0x100) == 0x100) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.D = res & 0xff;
	}
	
//	SLA E
//	2  8
//	Z 0 0 C
	public static void SLA_E() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		int res  = Cpu.E << 1; // Shift to the left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((res & 0x100) == 0x100) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.E = res & 0xff;
	}
	
//	SLA H
//	2  8
//	Z 0 0 C
	public static void SLA_H() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		int res  = Cpu.H << 1; // Shift to the left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((res & 0x100) == 0x100) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.H = res & 0xff;
	}
	
//	SLA L
//	2  8
//	Z 0 0 C
	public static void SLA_L() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		int res  = Cpu.L << 1; // Shift to the left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((res & 0x100) == 0x100) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.L = res & 0xff;
	}
	
//	SLA (HL)
//	2  16
//	Z 0 0 C
	public static void SLA_HL() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		int res = Ram.getByte(Cpu.getHL()) << 1;
		Ram.setByte(Cpu.getHL(), res); // Rotate to left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((res & 0x100) == 0x100) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Ram.setByte(Cpu.getHL(), res & 0xff);
	}
	
//	SLA A
//	2  8
//	Z 0 0 C
	public static void SLA_A() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		
		int res  = Cpu.A << 1; // Shift to the left
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((res & 0x100) == 0x100) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Cpu.A = res & 0xff;
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
		
	}
//	SRA (HL)
//	2  16
//	Z 0 0 C
	public static void SRA_HL() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int bit = Ram.getByte(Cpu.getHL()) & 0x80;
		int res = (Ram.getByte(Cpu.getHL()) >> 1) | bit; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Ram.getByte(Cpu.getHL()) & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Ram.setByte(Cpu.getHL(), res & 0xff);
		
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
	}
	
//	SWAP (HL)
//	2  16
//	Z 0 0 0
	public static void SWAP_HL() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		Cpu.resetFlagC();
		int l = Ram.getByte(Cpu.getHL()) & 0x0f;
		int h = Ram.getByte(Cpu.getHL()) & 0xf0;
		Ram.setByte(Cpu.getHL(), (l << 4) | (h >> 4));
		Cpu.checkZero8bit(Ram.getByte(Cpu.getHL()));
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
		
	}
	
//	SRL (HL)
//	2  16
//	Z 0 0 C
	public static void SRL_HL() {
		Cpu.resetFlagN();
		Cpu.resetFlagH();
		int res = Ram.getByte(Cpu.getHL()) >> 1; // Rotate to the right
		
//		The contents of bit 7 are placed in both the CY flag and bit 0 of register A.
		if ((Ram.getByte(Cpu.getHL()) & 0x01) == 0x01) {
			Cpu.setFlagC();
		} else {
			Cpu.resetFlagC();
		}
		
		Cpu.checkZero8bit(res);
		Ram.setByte(Cpu.getHL(), res & 0xff);
		
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
	}
	
//	BIT 0, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_0_HL() {
		if ((Ram.getByte(Cpu.getHL()) & 0x01) == 0x01) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
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
	}
	
//	BIT 1, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_1_HL() {
		if ((Ram.getByte(Cpu.getHL()) & 0x02) == 0x02) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
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
	}
	
//	BIT 2, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_2_HL() {
		if ((Ram.getByte(Cpu.getHL()) & 0x04) == 0x04) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
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
	}
	
//	BIT 3, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_3_HL() {
		if ((Ram.getByte(Cpu.getHL()) & 0x08) == 0x08) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
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
	}
	
//	BIT 4, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_4_HL() {
		if ((Ram.getByte(Cpu.getHL()) & 0x10) == 0x10) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
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
	}
	
//	BIT 5, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_5_HL() {
		if ((Ram.getByte(Cpu.getHL()) & 0x20) == 0x20) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
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
	}
	
//	BIT 6, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_6_HL() {
		if ((Ram.getByte(Cpu.getHL()) & 0x40) == 0x40) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
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
	}
	
//	BIT 7, (HL)
//	2  12
//	Z 0 1 -
	public static void BIT_7_HL() {
		if ((Ram.getByte(Cpu.getHL()) & 0x80) == 0x80) {
			Cpu.resetFlagZ();
		} else {
			Cpu.setFlagZ();
		}
		Cpu.resetFlagN();
		Cpu.setFlagH();
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
	}
	
//	RES 0, B
//	2  8
//	- - - -
	public static void RES_0_B() {
		Cpu.B &= 0xfe;
	}
	
//	RES 0, C
//	2  8
//	- - - -
	public static void RES_0_C() {
		Cpu.C &= 0xfe;
	}
	
//	RES 0, D
//	2  8
//	- - - -
	public static void RES_0_D() {
		Cpu.D &= 0xfe;
	}
		
//	RES 0, E
//	2  8
//	- - - -
	public static void RES_0_E() {
		Cpu.E &= 0xfe;
	}
	
//	RES 0, H
//	2  8
//	- - - -
	public static void RES_0_H() {
		Cpu.H &= 0xfe;
	}
	
//	RES 0, L
//	2  8
//	- - - -
	public static void RES_0_L() {
		Cpu.L &= 0xfe;
	}
	
//	RES 0, (HL)
//	2  16
//	- - - -
	public static void RES_0_HL() {
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) & 0xfe);
	}
	
//	RES 0, A
//	2  8
//	- - - -
	public static void RES_0_A() {
		Cpu.A &= 0xfe;
	}
	
//	RES 1, B
//	2  8
//	- - - -
	public static void RES_1_B() {
		Cpu.B &= 0xfd;
	}
		
//	RES 1, C
//	2  8
//	- - - -
	public static void RES_1_C() {
		Cpu.C &= 0xfd;
	}
	
//	RES 1, D
//	2  8
//	- - - -
	public static void RES_1_D() {
		Cpu.D &= 0xfd;
	}

//	RES 1, E
//	2  8
//	- - - -
	public static void RES_1_E() {
		Cpu.E &= 0xfd;
	}

//	RES 1, H
//	2  8
//	- - - -
	public static void RES_1_H() {
		Cpu.H &= 0xfd;
	}

//	RES 1, L
//	2  8
//	- - - -
	public static void RES_1_L() {
		Cpu.L &= 0xfd;
	}

//	RES 1, (HL)
//	2  16
//	- - - -
	public static void RES_1_HL() {
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) & 0xfd);
	}
	
//	RES 1, A
//	2  8
//	- - - -
	public static void RES_1_A() {
		Cpu.A &= 0xfd;
	}

//	RES 2, B
//	2  8
//	- - - -
	public static void RES_2_B() {
		Cpu.B &= 0xdf;
	}

//	RES 2, C
//	2  8
//	- - - -
	public static void RES_2_C() {
		Cpu.C &= 0xfb;
	}
	
//	RES 2, D
//	2  8
//	- - - -
	public static void RES_2_D() {
		Cpu.D &= 0xfb;
	}

//	RES 2, E
//	2  8
//	- - - -
	public static void RES_2_E() {
		Cpu.E &= 0xfb;
	}

//	RES 2, H
//	2  8
//	- - - -
	public static void RES_2_H() {
		Cpu.H &= 0xfb;
	}

//	RES 2, L
//	2  8
//	- - - -
	public static void RES_2_L() {
		Cpu.L &= 0xfb;
	}

//	RES 2, (HL)
//	2  16
//	- - - -
	public static void RES_2_HL() {
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) & 0xfb);
	}
	
//	RES 2, A
//	2  8
//	- - - -
	public static void RES_2_A() {
		Cpu.A &= 0xfb;
	}
	
//	RES 3, B
//	2  8
//	- - - -
	public static void RES_3_B() {
		Cpu.B &= 0xf7;
	}
		
//	RES 3, C
//	2  8
//	- - - -
	public static void RES_3_C() {
		Cpu.C &= 0xf7;
	}
		
//	RES 3, D
//	2  8
//	- - - -
	public static void RES_3_D() {
		Cpu.D &= 0xf7;
	}
	
//	RES 3, E
//	2  8
//	- - - -
	public static void RES_3_E() {
		Cpu.E &= 0xf7;
	}
	
//	RES 3, H
//	2  8
//	- - - -
	public static void RES_3_H() {
		Cpu.H &= 0xf7;
	}
	
//	RES 3, L
//	2  8
//	- - - -
	public static void RES_3_L() {
		Cpu.L &= 0xf7;
	}
	
//	RES 3, (HL)
//	2  16
//	- - - -
	public static void RES_3_HL() {
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) & 0xf7);
	}
	
//	RES 3, A
//	2  8
//	- - - -
	public static void RES_3_A() {
		Cpu.A &= 0xf7;
	}
	
//	RES 4, B
//	2  8
//	- - - -
	public static void RES_4_B() {
		Cpu.B &= 0xef;
	}
	
//	RES 4, C
//	2  8
//	- - - -
	public static void RES_4_C() {
		Cpu.C &= 0xef;
	}
	
//	RES 4, D
//	2  8
//	- - - -
	public static void RES_4_D() {
		Cpu.D &= 0xef;
	}
	
//	RES 4, E
//	2  8
//	- - - -
	public static void RES_4_E() {
		Cpu.E &= 0xef;
	}
	
//	RES 4, H
//	2  8
//	- - - -
	public static void RES_4_H() {
		Cpu.H &= 0xef;
	}
	
//	RES 4, L
//	2  8
//	- - - -
	public static void RES_4_L() {
		Cpu.L &= 0xef;
	}
	
//	RES 4, (HL)
//	2  16
//	- - - -
	public static void RES_4_HL() {
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) & 0xef);
	}
	
//	RES 4, A
//	2  8
//	- - - -
	public static void RES_4_A() {
		Cpu.A &= 0xef;
	}
	
//	RES 5, B
//	2  8
//	- - - -
	public static void RES_5_B() {
		Cpu.B &= 0xdf;
	}
		
//	RES 5, C
//	2  8
//	- - - -
	public static void RES_5_C() {
		Cpu.C &= 0xdf;
	}
	
//	RES 5, D
//	2  8
//	- - - -
	public static void RES_5_D() {
		Cpu.D &= 0xdf;
	}

//	RES 5, E
//	2  8
//	- - - -
	public static void RES_5_E() {
		Cpu.E &= 0xdf;
	}

//	RES 5, H
//	2  8
//	- - - -
	public static void RES_5_H() {
		Cpu.H &= 0xdf;
	}

//	RES 5, L
//	2  8
//	- - - -
	public static void RES_5_L() {
		Cpu.L &= 0xdf;
	}

//	RES 5, (HL)
//	2  16
//	- - - -
	public static void RES_5_HL() {
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) & 0xdf);
	}
	
//	RES 5, A
//	2  8
//	- - - -
	public static void RES_5_A() {
		Cpu.A &= 0xdf;
	}

//	RES 6, B
//	2  8
//	- - - -
	public static void RES_6_B() {
		Cpu.B &= 0xbf;
	}
	
//	RES 6, C
//	2  8
//	- - - -
	public static void RES_6_C() {
		Cpu.C &= 0xbf;
	}
		
//	RES 6, D
//	2  8
//	- - - -
	public static void RES_6_D() {
		Cpu.D &= 0xbf;
	}
	
//	RES 6, E
//	2  8
//	- - - -
	public static void RES_6_E() {
		Cpu.E &= 0xbf;
	}
	
//	RES 6, H
//	2  8
//	- - - -
	public static void RES_6_H() {
		Cpu.H &= 0xbf;
	}
	
//	RES 6, L
//	2  8
//	- - - -
	public static void RES_6_L() {
		Cpu.L &= 0xbf;
	}
	
//	RES 6, (HL)
//	2  16
//	- - - -
	public static void RES_6_HL() {
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) & 0xbf);
	}
	
//	RES 6, A
//	2  8
//	- - - -
	public static void RES_6_A() {
		Cpu.A &= 0xbf;
	}
	
//	RES 7, B
//	2  8
//	- - - -
	public static void RES_7_B() {
		Cpu.B &= 0x7f;
	}
	
//	RES 7, C
//	2  8
//	- - - -
	public static void RES_7_C() {
		Cpu.C &= 0x7f;
	}
		
//	RES 7, D
//	2  8
//	- - - -
	public static void RES_7_D() {
		Cpu.D &= 0x7f;
	}
	
//	RES 7, E
//	2  8
//	- - - -
	public static void RES_7_E() {
		Cpu.E &= 0x7f;
	}
	
//	RES 7, H
//	2  8
//	- - - -
	public static void RES_7_H() {
		Cpu.H &= 0x7f;
	}
	
//	RES 7, L
//	2  8
//	- - - -
	public static void RES_7_L() {
		Cpu.L &= 0x7f;
	}
	
//	RES 7, (HL)
//	2  16
//	- - - -
	public static void RES_7_HL() {
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) & 0x7f);
	}

//	RES 7, A
//	2  8
//	- - - -
	public static void RES_7_A() {
		Cpu.A &= 0x7f;
	}
	
//	SET 0, B
//	2  8
//	- - - -
	public static void SET_0_B() {
		Cpu.B |= 0x01;
	}
	
//	SET 0, C
//	2  8
//	- - - -
	public static void SET_0_C() {
		Cpu.C |= 0x01;
	}
		
//	SET 0, D
//	2  8
//	- - - -
	public static void SET_0_D() {
		Cpu.D |= 0x01;
	}
	
//	SET 0, E
//	2  8
//	- - - -
	public static void SET_0_E() {
		Cpu.E |= 0x01;
	}
	
//	SET 0, H
//	2  8
//	- - - -
	public static void SET_0_H() {
		Cpu.H |= 0x01;
	}
	
//	SET 0, L
//	2  8
//	- - - -
	public static void SET_0_L() {
		Cpu.L |= 0x01;
	}
	
//	SET 0, (HL)
//	2  16
//	- - - -
	public static void SET_0_HL() {
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) | 0x01);
	}
	
//	SET 0, A
//	2  8
//	- - - -
	public static void SET_0_A() {
		Cpu.A |= 0x01;
	}
	
//	SET 1, B
//	2  8
//	- - - -
	public static void SET_1_B() {
		Cpu.B |= 0x02;
	}
	
//	SET 1, C
//	2  8
//	- - - -
	public static void SET_1_C() {
		Cpu.C |= 0x02;
	}
	
//	SET 1, D
//	2  8
//	- - - -
	public static void SET_1_D() {
		Cpu.D |= 0x02;
	}
	
//	SET 1, E
//	2  8
//	- - - -
	public static void SET_1_E() {
		Cpu.E |= 0x02;
	}
	
//	SET 1, H
//	2  8
//	- - - -
	public static void SET_1_H() {
		Cpu.H |= 0x02;
	}
	
//	SET 1, L
//	2  8
//	- - - -
	public static void SET_1_L() {
		Cpu.L |= 0x02;
	}
	
//	SET 1, (HL)
//	2  16
//	- - - -
	public static void SET_1_HL() {
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) | 0x02);
	}
	
//	SET 1, A
//	2  8
//	- - - -
	public static void SET_1_A() {
		Cpu.A |= 0x02;
	}
	
//	SET 2, B
//	2  8
//	- - - -
	public static void SET_2_B() {
		Cpu.B |= 0x04;
	}
	
//	SET 2, C
//	2  8
//	- - - -
	public static void SET_2_C() {
		Cpu.C |= 0x04;
	}
		
//	SET 2, D
//	2  8
//	- - - -
	public static void SET_2_D() {
		Cpu.D |= 0x04;
	}
	
//	SET 2, E
//	2  8
//	- - - -
	public static void SET_2_E() {
		Cpu.E |= 0x04;
	}
	
//	SET 2, H
//	2  8
//	- - - -
	public static void SET_2_H() {
		Cpu.H |= 0x04;
	}
	
//	SET 2, L
//	2  8
//	- - - -
	public static void SET_2_L() {
		Cpu.L |= 0x04;
	}
	
//	SET 2, (HL)
//	2  16
//	- - - -
	public static void SET_2_HL() {
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) | 0x04);
	}
	
//	SET 2, A
//	2  8
//	- - - -
	public static void SET_2_A() {
		Cpu.A |= 0x04;
	}
	
//	SET 3, B
//	2  8
//	- - - -
	public static void SET_3_B() {
		Cpu.B |= 0x08;
	}
	
//	SET 3, C
//	2  8
//	- - - -
	public static void SET_3_C() {
		Cpu.C |= 0x08;
	}
		
//	SET 3, D
//	2  8
//	- - - -
	public static void SET_3_D() {
		Cpu.D |= 0x08;
	}
	
//	SET 3, E
//	2  8
//	- - - -
	public static void SET_3_E() {
		Cpu.E |= 0x08;
	}
	
//	SET 3, H
//	2  8
//	- - - -
	public static void SET_3_H() {
		Cpu.H |= 0x08;
	}
	
//	SET 3, L
//	2  8
//	- - - -
	public static void SET_3_L() {
		Cpu.L |= 0x08;
	}
	
//	SET 3, (HL)
//	2  16
//	- - - -
	public static void SET_3_HL() {
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) | 0x08);
	}
	
//	SET 3, A
//	2  8
//	- - - -
	public static void SET_3_A() {
		Cpu.A |= 0x08;
	}
	
//	SET 4, B
//	2  8
//	- - - -
	public static void SET_4_B() {
		Cpu.B |= 0x10;
	}
	
//	SET 4, C
//	2  8
//	- - - -
	public static void SET_4_C() {
		Cpu.C |= 0x10;
	}
	
//	SET 4, D
//	2  8
//	- - - -
	public static void SET_4_D() {
		Cpu.D |= 0x10;
	}
	
//	SET 4, E
//	2  8
//	- - - -
	public static void SET_4_E() {
		Cpu.E |= 0x10;
	}
	
//	SET 4, H
//	2  8
//	- - - -
	public static void SET_4_H() {
		Cpu.H |= 0x10;
	}
	
//	SET 4, L
//	2  8
//	- - - -
	public static void SET_4_L() {
		Cpu.L |= 0x10;
	}
	
//	SET 4, (HL)
//	2  16
//	- - - -
	public static void SET_4_HL() {
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) | 0x10);
	}

//	SET 4, A
//	2  8
//	- - - -
	public static void SET_4_A() {
		Cpu.A |= 0x10;
	}
	
//	SET 5, B
//	2  8
//	- - - -
	public static void SET_5_B() {
		Cpu.B |= 0x20;
	}
	
//	SET 5, C
//	2  8
//	- - - -
	public static void SET_5_C() {
		Cpu.C |= 0x20;
	}
	
//	SET 5, D
//	2  8
//	- - - -
	public static void SET_5_D() {
		Cpu.D |= 0x20;
	}

//	SET 5, E
//	2  8
//	- - - -
	public static void SET_5_E() {
		Cpu.E |= 0x20;
	}

//	SET 5, H
//	2  8
//	- - - -
	public static void SET_5_H() {
		Cpu.H |= 0x20;
	}

//	SET 5, L
//	2  8
//	- - - -
	public static void SET_5_L() {
		Cpu.L |= 0x20;
	}

//	SET 5, (HL)
//	2  16
//	- - - -
	public static void SET_5_HL() {
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) | 0x20);
	}
	
//	SET 5, A
//	2  8
//	- - - -
	public static void SET_5_A() {
		Cpu.A |= 0x20;
	}

//	SET 6, B
//	2  8
//	- - - -
	public static void SET_6_B() {
		Cpu.B |= 0x40;
	}

//	SET 6, C
//	2  8
//	- - - -
	public static void SET_6_C() {
		Cpu.C |= 0x40;
	}
	
//	SET 6, D
//	2  8
//	- - - -
	public static void SET_6_D() {
		Cpu.D |= 0x40;
	}

//	SET 6, E
//	2  8
//	- - - -
	public static void SET_6_E() {
		Cpu.E |= 0x40;
	}

//	SET 6, H
//	2  8
//	- - - -
	public static void SET_6_H() {
		Cpu.H |= 0x40;
	}

//	SET 6, L
//	2  8
//	- - - -
	public static void SET_6_L() {
		Cpu.L |= 0x40;
	}

//	SET 6, (HL)
//	2  16
//	- - - -
	public static void SET_6_HL() {
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) | 0x40);
	}
	
//	SET 6, A
//	2  8
//	- - - -
	public static void SET_6_A() {
		Cpu.A |= 0x40;
	}

//	SET 7, B
//	2  8
//	- - - -
	public static void SET_7_B() {
		Cpu.B |= 0x80;
	}

//	SET 7, C
//	2  8
//	- - - -
	public static void SET_7_C() {
		Cpu.C |= 0x80;
	}
	
//	SET 7, D
//	2  8
//	- - - -
	public static void SET_7_D() {
		Cpu.D |= 0x80;
	}

//	SET 7, E
//	2  8
//	- - - -
	public static void SET_7_E() {
		Cpu.E |= 0x80;
	}

//	SET 7, H
//	2  8
//	- - - -
	public static void SET_7_H() {
		Cpu.H |= 0x80;
	}

//	SET 7, L
//	2  8
//	- - - -
	public static void SET_7_L() {
		Cpu.L |= 0x80;
	}

//	SET 7, (HL)
//	2  16
//	- - - -
	public static void SET_7_HL() {
		Ram.setByte(Cpu.getHL(), Ram.getByte(Cpu.getHL()) | 0x80);
	}
	
//	SET 7, A
//	2  8
//	- - - -
	public static void SET_7_A() {
		Cpu.A |= 0x80;
	}

}
