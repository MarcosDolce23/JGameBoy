package gba;

public class Cpu {
	
	//CPU registers and flags
	
	// Registers
	
	// A: Accumulators, F: Flags
	public static int A;
	public static int F;
	
	public static int B;
	public static int C;
	
	public static int D;
	public static int E;
	
	public static int H;
	public static int L;
	
	// Stack Pointer
	public static int SP;
	
	// Program Counter/Pointer
	public static int PC;
	
	public static boolean IME = false;
	
	public static int getBC() {
		return ((B << 8) + C);
	}
	
	public static int getDE() {
		return ((D << 8) + E);
	}
	
	public static int getHL() {
		return ((H << 8) + L);
	}
	
//	public static int getSP() {
//		return ((S << 8) + P);
//	}
	
	public static int getFlagZ() {
		return (Cpu.F & 0x80) >> 7;
	}
	
	public static int getFlagC() {
		return (Cpu.F & 0x10) >> 4;
	}
	public static void setFlagZ() {
		F |= 0x80; // Set flag Z to 1
	}
	
	public static void setFlagN() {
		F |= 0x40; // Set flag N to 1
	}
	
	public static void setFlagH() {
		F |= 0x20; // Set flag H to 1
	}
	
	public static void setFlagC() {
		F |= 0x10; // Set flag C to 1
	}

	public static void resetFlagZ() {
		F &= 0x7f; // Set flag Z to 0
	}
	
	public static void resetFlagN() {
		F &= 0xbf; // Set flag N to 0
	}

	public static void resetFlagH() {
		F &= 0xdf; // Set flag H to 0
	}

	public static void resetFlagC() {
		F &= 0xef; // Set flag C to 0
	}	
	
	public static void checkHalfCarry8bit(int value1, int value2) {
		if ((value1 & 0xf) + (value2 & 0xf) > 0xf) {
			setFlagH();
		} else {
			resetFlagH();
		}
	}
	
	public static void checkHalfCarry16bit(int value1, int value2) {
		if ((value1 & 0xff) + (value2 & 0xff) > 0xff) {
			setFlagH();
		} else {
			resetFlagH();
		}
	}
	
	public static void checkHalfCarry8bitSub(int value1, int value2) {
		if ((value1 & 0xf) < (value2 & 0xf)) {
			setFlagH();
		} else {
			resetFlagH();
		}
	}
	
	public static void checkHalfCarry16bitSub(int value1, int value2) {
		if ((value1 & 0xff) < (value2 & 0xff)) {
			setFlagH();
		} else {
			resetFlagH();
		}
	}
	
	public static void checkCarry8bit(int value) {
		if (value > 0xff) {
			setFlagH();
		} else {
			resetFlagH();
		}
	}
	
	public static void checkCarry16bit(int value) {
		if (value > 0xffff) {
			setFlagH();
		} else {
			resetFlagH();
		}
	}
	
	public static void checkCarry8bitSub(int value1, int value2) {
		if (value1 < value2) {
			setFlagH();
		} else {
			resetFlagH();
		}
	}
	
	public static void checkCarry16bitSub(int value1, int value2) {
		if (value1 < value2) {
			setFlagH();
		} else {
			resetFlagH();
		}
	}
	
	public static void checkZero8bit(int value) {
		if (value == 0x00) {
			setFlagZ();
		} else {
			resetFlagZ();
		}
	}
	
	public static void checkZero16bit(int value) {
		if (value == 0x0000) {
			setFlagZ();
		} else {
			resetFlagZ();
		}
	}
	
	// Función para realizar un desplazamiento circular a la izquierda o un desplazamiento circular a la derecha
	// en posiciones de entero `n` por `k` basadas en el indicador `isLeftShift`
	public static int circularShift(int n, int k, boolean isLeftShift)
	{
	    // desplazamiento a la izquierda por `k`
	    if (isLeftShift) {
	        return ((n << k) | (n >> (8 - k))) & 0xff;
	    }
	 
	    // desplazamiento a la derecha por `k`
	    return ((n >> k) | (n << (8 - k)))  & 0xff;
	}
	
	public static int fetch() {
		int pc = PC;
		PC += 1;
		return Ram.getByte(pc) & 0xff;
	}
	
	public static int fetchSP() {
		int sp = SP;
		SP += 1;
		return Ram.getByte(sp) & 0xff;
		
	}
	
	public static int cycles = 0;
	
	public Cpu() {
		A = 0x00;
		F = 0x00;
		
		B = 0x00;
		C = 0x00;
		
		D = 0x00;
		E = 0x00;
		
		H = 0x00;
		L = 0x00;
		
		SP = 0x0000;
		
		PC = 0x0000;
	}
}
