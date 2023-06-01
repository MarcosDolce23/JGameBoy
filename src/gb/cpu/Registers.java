package gb.cpu;

import gb.Main;
import gb.utils.BitOperations;

public class Registers {
	
	// CPU registers and flags
	public int A = 0x01; // Accumulator
	public int F = 0xb0; // Flags

	public int B = 0x00;
	public int C = 0x13;

	public int D = 0x00;
	public int E = 0xd8;

	public int H = 0x01;
	public int L = 0x4d;

	// Stack Pointer
	public int SP = 0xfffe;

	// Program Counter/Pointer
	public int PC = 0x0100;

	public int getBC() {
		return ((B << 8) + C);
	}

	public int getDE() {
		return ((D << 8) + E);
	}

	public int getHL() {
		return ((H << 8) + L);
	}

	public int getFlagZ() {
		return (F & 0x80) >> 7;
	}

	public int getFlagN() {
		return (F & 0x40) >> 6;
	}

	public int getFlagH() {
		return (F & 0x20) >> 5;
	}

	public int getFlagC() {
		return (F & 0x10) >> 4;
	}

	public void setFlagZ() {
		F = BitOperations.bitSet(F, 7); // Set flag Z to 1
	}

	public void setFlagN() {
		F = BitOperations.bitSet(F, 6); // Set flag N to 1
	}

	public void setFlagH() {
		F = BitOperations.bitSet(F, 5); // Set flag H to 1
	}

	public void setFlagC() {
		F = BitOperations.bitSet(F, 4); // Set flag C to 1
	}

	public void resetFlagZ() {
		F = BitOperations.bitReset(F, 7); // Set flag Z to 0
	}

	public void resetFlagN() {
		F = BitOperations.bitReset(F, 6); // Set flag N to 0
	}

	public void resetFlagH() {
		F = BitOperations.bitReset(F, 5); // Set flag H to 0
	}

	public void resetFlagC() {
		F = BitOperations.bitReset(F, 4); // Set flag C to 0
	}
	
	public int fetch() {
		int pc = PC;
		PC += 1;
		
		return Main.mmu.getByte(pc);
	}
	
	public int fetchSigned() {
		int pc = PC;
		PC += 1;
		return Main.mmu.getSignedByte(pc);
	}
	
	public int fetchSP() {
		int sp = SP;
		SP += 1;
		return Main.mmu.getByte(sp);
	}

	public void checkHalfCarry8bit(int value1, int value2) {
		if ((value1 & 0xf) + (value2 & 0xf) > 0xf) {
			setFlagH();
		} else {
			resetFlagH();
		}
	}

	public void checkCarry8bit(int value) {
		if (value > 0xff) {
			setFlagC();
		} else {
			resetFlagC();
		}
	}

	public void checkHalfCarry16bit(int value1, int value2) {
		if ((value1 & 0xfff) + (value2 & 0xfff) > 0xfff) {
			setFlagH();
		} else {
			resetFlagH();
		}
	}

	public void checkCarry16bit(int value) {
		if (value > 0xffff) {
			setFlagC();
		} else {
			resetFlagC();
		}
	}

	public void checkCarry8bitSub(int value1, int value2) {
		if (value1 < value2) {
			setFlagC();
		} else {
			resetFlagC();
		}
	}

	public void checkHalfCarry8bitSub(int value1, int value2) {
		if ((value1 & 0xf) < (value2 & 0xf)) {
			setFlagH();
		} else {
			resetFlagH();
		}
	}

	public void checkHalfCarry16bitSub(int value1, int value2) {
		if ((value1 & 0xff) < (value2 & 0xff)) {
			setFlagH();
		} else {
			resetFlagH();
		}
	}

	public void checkCarry16bitSub(int value1, int value2) {
		if (value1 < value2) {
			setFlagC();
		} else {
			resetFlagC();
		}
	}

	public void checkZero8bit(int value) {
		if ((value & 0xff) == 0x00) {
			setFlagZ();
		} else {
			resetFlagZ();
		}
	}

	public void checkZero16bit(int value) {
		if ((value & 0xffff) == 0x0000) {
			setFlagZ();
		} else {
			resetFlagZ();
		}
	}
}
