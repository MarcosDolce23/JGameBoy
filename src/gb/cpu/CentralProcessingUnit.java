package gb.cpu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import gb.Main;
import gb.utils.BitOperations;

public final class CentralProcessingUnit {
	private static CentralProcessingUnit instance;

	Registers regs = new Registers();

	// CPU Timing
	public int cyclesPerSec = 4194304;
	public int cyclesPerFrame;
	public int interval = 0;
	public int cycles = 0;
	private int cycled = 0;
	private int frequency;
	private Timer timer;

	// CPU Flags
	public boolean IME = false;
	public boolean HALT = false;
	public boolean haltBugAtm = false;

	private int divClocksum = 0;
	private int timerClocksum = 0;

//	FFFF — IE: Interrupt enable
//	
//	Bit 0: VBlank   Interrupt Enable  (INT $40)  (1=Enable)
//	Bit 1: LCD STAT Interrupt Enable  (INT $48)  (1=Enable)
//	Bit 2: Timer    Interrupt Enable  (INT $50)  (1=Enable)
//	Bit 3: Serial   Interrupt Enable  (INT $58)  (1=Enable)
//	Bit 4: Joypad   Interrupt Enable  (INT $60)  (1=Enable)

	private boolean getIEVBlank() {
		return BitOperations.testBit(Main.mmu.getByte(0xffff), 0);
	}

	private boolean getIELCDSTAT() {
		return BitOperations.testBit(Main.mmu.getByte(0xffff), 1);
	}

	private boolean getIETimer() {
		return BitOperations.testBit(Main.mmu.getByte(0xffff), 2);
	}

	private boolean getIESerial() {
		return BitOperations.testBit(Main.mmu.getByte(0xffff), 3);
	}

	private boolean getIEJoypad() {
		return BitOperations.testBit(Main.mmu.getByte(0xffff), 4);
	}

//	FF0F — IF: Interrupt flag
//	
//	Bit 0: VBlank   Interrupt Request (INT $40)  (1=Request)
//	Bit 1: LCD STAT Interrupt Request (INT $48)  (1=Request)
//	Bit 2: Timer    Interrupt Request (INT $50)  (1=Request)
//	Bit 3: Serial   Interrupt Request (INT $58)  (1=Request)
//	Bit 4: Joypad   Interrupt Request (INT $60)  (1=Request)

	private boolean getIFVBlank() {
		return BitOperations.testBit(Main.mmu.getByte(0xff0f), 0);
	}

	private boolean getIFLCDSTAT() {
		return BitOperations.testBit(Main.mmu.getByte(0xff0f), 1);
	}

	private boolean getIFTimer() {
		return BitOperations.testBit(Main.mmu.getByte(0xff0f), 2);
	}

	private boolean getIFSerial() {
		return BitOperations.testBit(Main.mmu.getByte(0xff0f), 3);
	}

	private boolean getIFJoypad() {
		return BitOperations.testBit(Main.mmu.getByte(0xff0f), 4);
	}

	private void resetIFVBlank() {
		Main.mmu.setByte(0xff0f, BitOperations.bitReset(Main.mmu.getByte(0xff0f), 0));
	}

	private void resetIFLCDSTAT() {
		Main.mmu.setByte(0xff0f, BitOperations.bitReset(Main.mmu.getByte(0xff0f), 1));
	}

	private void resetIFTimer() {
		Main.mmu.setByte(0xff0f, BitOperations.bitReset(Main.mmu.getByte(0xff0f), 2));
	}

	private void resetIFSerial() {
		Main.mmu.setByte(0xff0f, BitOperations.bitReset(Main.mmu.getByte(0xff0f), 3));
	}

	private void resetIFJoypad() {
		Main.mmu.setByte(0xff0f, BitOperations.bitReset(Main.mmu.getByte(0xff0f), 4));
	}

	private CentralProcessingUnit() {
	}

	public static CentralProcessingUnit getInstance() {
		if (instance == null) {
			instance = new CentralProcessingUnit();
		}
		return instance;
	}

	ActionListener taskPerformer = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			// ...Perform a task...

			Long msBefore = System.currentTimeMillis();

			try {
				cycled = runFrame(cycled);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Long msAfter = System.currentTimeMillis();
			Long msSpent = msAfter - msBefore;

			int aux = (interval - msSpent) > 0 ? (int) (interval - msSpent) : 0;

			timer.setInitialDelay(aux);
		}
	};

	public void loopExe(int extractCycles) throws InterruptedException {
		System.out.println("Running...");

		Long msBefore = System.currentTimeMillis();

		cycled = runFrame(extractCycles);

		Long msAfter = System.currentTimeMillis();
		Long msSpent = msAfter - msBefore;

		// Adjust the time of the CPU
		timer = new Timer((int) (interval - msSpent), taskPerformer);
		timer.setRepeats(true);
		timer.start();
	}

	private int runFrame(int extractCycles) throws InterruptedException {
		int cycles = cyclesPerFrame - extractCycles;

		while (cycles > 0) {
			cycles -= step();
		}

		return cycles;
	}

	private int step() throws InterruptedException {
		int opcode;
		cycles = 0;
		if (haltBugAtm) {
			haltBugAtm = false;
			opcode = Main.mmu.getByte(regs.PC);
		} else
			opcode = regs.fetch();
		decode(opcode);
		handleInterrupts();

		handleTimer();
		Main.ppu.handleScan(cycles);
		Main.apu.handleSound(cycles);

		return cycles;
	}

	private void manageInterrupt(int counter) {
		regs.SP -= 1;
		Main.mmu.setByte(regs.SP, (regs.PC & 0xff00) >> 8); // High byte of PC
		regs.SP -= 1;
		Main.mmu.setByte(regs.SP, regs.PC & 0xff); // Low byte of PC
		regs.PC = counter;
		InstructionSet.DI();
		cycles += 16; // Add the other cycles beside DI()
	}

	private void handleInterrupts() {
		if (IME) {
			// v-blank interrupt
			if (getIEVBlank() & getIFVBlank()) {
				manageInterrupt(0x40);
				resetIFVBlank();
			}
			// LCD STAT interrupt
			if (getIELCDSTAT() & getIFLCDSTAT()) {
				manageInterrupt(0x48);
				resetIFLCDSTAT();
			}
			// Timer interrupt
			if (getIETimer() & getIFTimer()) {
				manageInterrupt(0x50);
				resetIFTimer();
			}
			// Serial interrupt
			if (getIESerial() & getIFSerial()) {
				manageInterrupt(0x58);
				resetIFSerial();
			}
			// Joypad interrupt
			if (getIEJoypad() & getIFJoypad()) {
				manageInterrupt(0x60);
				resetIFJoypad();
			}
		}
	}
	
	private void setDivider() {
		divClocksum += cycles;
		if (divClocksum >= 256) {
			Main.mmu.ram[0xff04]++;
			divClocksum -= 256;
		}
	}
	
	private void setFrequency() {
		frequency = 4096; // Hz
		if ((Main.mmu.getByte(0xff07) & 0x03) == 0x01) // mask last 2 bits
			frequency = 262144;
		else if ((Main.mmu.getByte(0xff07) & 0x03) == 0x02) // mask last 2 bits
			frequency = 65536;
		else if ((Main.mmu.getByte(0xff07) & 0x03) == 0x03) // mask last 2 bits
			frequency = 16384;
	}
	
	private void increaseTIMA() {
		int aux = Main.mmu.getByte(0xff05); // I set this auxiliar because TIMA will never be greater than 255 beacause will be set to 0 before.
		Main.mmu.ram[0xff05]++;
		aux++;
//		check TIMA for overflow
		if (aux > 0xff) {
//			set timer interrupt request
			Main.mmu.setByte(0xff0f, Main.mmu.getByte(0xff0f) | 0x04);
//			reset timer to timer modulo
			Main.mmu.setByte(0xff05, Main.mmu.getByte(0xff06));
		}

		timerClocksum -= (4194304 / frequency);
	}

	private void handleTimer() {
		setDivider();

//		check if timer is on
		if (BitOperations.testBit(Main.mmu.getByte(0xff07), 2)) {
			setFrequency();
//			increase helper counter
			timerClocksum += cycles;
//			Increment the timer according to the frequency (synched to the processed opcodes)
			while (timerClocksum >= (4194304 / frequency)) {
				increaseTIMA();
			}
		}
	}

	private void decode(int opcode) {
		// Prefixed (0xCB 0xxx)
		if (opcode == 0xCB) {
			opcode = regs.fetch();
			switch (opcode) {
			case 0:
				InstructionSet.RLC_B();
				break;
			case 1:
				InstructionSet.RLC_C();
				break;
			case 2:
				InstructionSet.RLC_D();
				break;
			case 3:
				InstructionSet.RLC_E();
				break;
			case 4:
				InstructionSet.RLC_H();
				break;
			case 5:
				InstructionSet.RLC_L();
				break;
			case 6:
				InstructionSet.RLC_HL();
				break;
			case 7:
				InstructionSet.RLC_A();
				break;
			case 8:
				InstructionSet.RRC_B();
				break;
			case 9:
				InstructionSet.RRC_C();
				break;
			case 10:
				InstructionSet.RRC_D();
				break;
			case 11:
				InstructionSet.RRC_E();
				break;
			case 12:
				InstructionSet.RRC_H();
				break;
			case 13:
				InstructionSet.RRC_L();
				break;
			case 14:
				InstructionSet.RRC_HL();
				break;
			case 15:
				InstructionSet.RRC_A();
				break;
			case 16:
				InstructionSet.RL_B();
				break;
			case 17:
				InstructionSet.RL_C();
				break;
			case 18:
				InstructionSet.RL_D();
				break;
			case 19:
				InstructionSet.RL_E();
				break;
			case 20:
				InstructionSet.RL_H();
				break;
			case 21:
				InstructionSet.RL_L();
				break;
			case 22:
				InstructionSet.RL_HL();
				break;
			case 23:
				InstructionSet.RL_A();
				break;
			case 24:
				InstructionSet.RR_B();
				break;
			case 25:
				InstructionSet.RR_C();
				break;
			case 26:
				InstructionSet.RR_D();
				break;
			case 27:
				InstructionSet.RR_E();
				break;
			case 28:
				InstructionSet.RR_H();
				break;
			case 29:
				InstructionSet.RR_L();
				break;
			case 30:
				InstructionSet.RR_HL();
				break;
			case 31:
				InstructionSet.RR_A();
				break;
			case 32:
				InstructionSet.SLA_B();
				break;
			case 33:
				InstructionSet.SLA_C();
				break;
			case 34:
				InstructionSet.SLA_D();
				break;
			case 35:
				InstructionSet.SLA_E();
				break;
			case 36:
				InstructionSet.SLA_H();
				break;
			case 37:
				InstructionSet.SLA_L();
				break;
			case 38:
				InstructionSet.SLA_HL();
				break;
			case 39:
				InstructionSet.SLA_A();
				break;
			case 40:
				InstructionSet.SRA_B();
				break;
			case 41:
				InstructionSet.SRA_C();
				break;
			case 42:
				InstructionSet.SRA_D();
				break;
			case 43:
				InstructionSet.SRA_E();
				break;
			case 44:
				InstructionSet.SRA_H();
				break;
			case 45:
				InstructionSet.SRA_L();
				break;
			case 46:
				InstructionSet.SRA_HL();
				break;
			case 47:
				InstructionSet.SRA_A();
				break;
			case 48:
				InstructionSet.SWAP_B();
				break;
			case 49:
				InstructionSet.SWAP_C();
				break;
			case 50:
				InstructionSet.SWAP_D();
				break;
			case 51:
				InstructionSet.SWAP_E();
				break;
			case 52:
				InstructionSet.SWAP_H();
				break;
			case 53:
				InstructionSet.SWAP_L();
				break;
			case 54:
				InstructionSet.SWAP_HL();
				break;
			case 55:
				InstructionSet.SWAP_A();
				break;
			case 56:
				InstructionSet.SRL_B();
				break;
			case 57:
				InstructionSet.SRL_C();
				break;
			case 58:
				InstructionSet.SRL_D();
				break;
			case 59:
				InstructionSet.SRL_E();
				break;
			case 60:
				InstructionSet.SRL_H();
				break;
			case 61:
				InstructionSet.SRL_L();
				break;
			case 62:
				InstructionSet.SRL_HL();
				break;
			case 63:
				InstructionSet.SRL_A();
				break;
			case 64:
				InstructionSet.BIT_0_B();
				break;
			case 65:
				InstructionSet.BIT_0_C();
				break;
			case 66:
				InstructionSet.BIT_0_D();
				break;
			case 67:
				InstructionSet.BIT_0_E();
				break;
			case 68:
				InstructionSet.BIT_0_H();
				break;
			case 69:
				InstructionSet.BIT_0_L();
				break;
			case 70:
				InstructionSet.BIT_0_HL();
				break;
			case 71:
				InstructionSet.BIT_0_A();
				break;
			case 72:
				InstructionSet.BIT_1_B();
				break;
			case 73:
				InstructionSet.BIT_1_C();
				break;
			case 74:
				InstructionSet.BIT_1_D();
				break;
			case 75:
				InstructionSet.BIT_1_E();
				break;
			case 76:
				InstructionSet.BIT_1_H();
				break;
			case 77:
				InstructionSet.BIT_1_L();
				break;
			case 78:
				InstructionSet.BIT_1_HL();
				break;
			case 79:
				InstructionSet.BIT_1_A();
				break;
			case 80:
				InstructionSet.BIT_2_B();
				break;
			case 81:
				InstructionSet.BIT_2_C();
				break;
			case 82:
				InstructionSet.BIT_2_D();
				break;
			case 83:
				InstructionSet.BIT_2_E();
				break;
			case 84:
				InstructionSet.BIT_2_H();
				break;
			case 85:
				InstructionSet.BIT_2_L();
				break;
			case 86:
				InstructionSet.BIT_2_HL();
				break;
			case 87:
				InstructionSet.BIT_2_A();
				break;
			case 88:
				InstructionSet.BIT_3_B();
				break;
			case 89:
				InstructionSet.BIT_3_C();
				break;
			case 90:
				InstructionSet.BIT_3_D();
				break;
			case 91:
				InstructionSet.BIT_3_E();
				break;
			case 92:
				InstructionSet.BIT_3_H();
				break;
			case 93:
				InstructionSet.BIT_3_L();
				break;
			case 94:
				InstructionSet.BIT_3_HL();
				break;
			case 95:
				InstructionSet.BIT_3_A();
				break;
			case 96:
				InstructionSet.BIT_4_B();
				break;
			case 97:
				InstructionSet.BIT_4_C();
				break;
			case 98:
				InstructionSet.BIT_4_D();
				break;
			case 99:
				InstructionSet.BIT_4_E();
				break;
			case 100:
				InstructionSet.BIT_4_H();
				break;
			case 101:
				InstructionSet.BIT_4_L();
				break;
			case 102:
				InstructionSet.BIT_4_HL();
				break;
			case 103:
				InstructionSet.BIT_4_A();
				break;
			case 104:
				InstructionSet.BIT_5_B();
				break;
			case 105:
				InstructionSet.BIT_5_C();
				break;
			case 106:
				InstructionSet.BIT_5_D();
				break;
			case 107:
				InstructionSet.BIT_5_E();
				break;
			case 108:
				InstructionSet.BIT_5_H();
				break;
			case 109:
				InstructionSet.BIT_5_L();
				break;
			case 110:
				InstructionSet.BIT_5_HL();
				break;
			case 111:
				InstructionSet.BIT_5_A();
				break;
			case 112:
				InstructionSet.BIT_6_B();
				break;
			case 113:
				InstructionSet.BIT_6_C();
				break;
			case 114:
				InstructionSet.BIT_6_D();
				break;
			case 115:
				InstructionSet.BIT_6_E();
				break;
			case 116:
				InstructionSet.BIT_6_H();
				break;
			case 117:
				InstructionSet.BIT_6_L();
				break;
			case 118:
				InstructionSet.BIT_6_HL();
				break;
			case 119:
				InstructionSet.BIT_6_A();
				break;
			case 120:
				InstructionSet.BIT_7_B();
				break;
			case 121:
				InstructionSet.BIT_7_C();
				break;
			case 122:
				InstructionSet.BIT_7_D();
				break;
			case 123:
				InstructionSet.BIT_7_E();
				break;
			case 124:
				InstructionSet.BIT_7_H();
				break;
			case 125:
				InstructionSet.BIT_7_L();
				break;
			case 126:
				InstructionSet.BIT_7_HL();
				break;
			case 127:
				InstructionSet.BIT_7_A();
				break;
			case 128:
				InstructionSet.RES_0_B();
				break;
			case 129:
				InstructionSet.RES_0_C();
				break;
			case 130:
				InstructionSet.RES_0_D();
				break;
			case 131:
				InstructionSet.RES_0_E();
				break;
			case 132:
				InstructionSet.RES_0_H();
				break;
			case 133:
				InstructionSet.RES_0_L();
				break;
			case 134:
				InstructionSet.RES_0_HL();
				break;
			case 135:
				InstructionSet.RES_0_A();
				break;
			case 136:
				InstructionSet.RES_1_B();
				break;
			case 137:
				InstructionSet.RES_1_C();
				break;
			case 138:
				InstructionSet.RES_1_D();
				break;
			case 139:
				InstructionSet.RES_1_E();
				break;
			case 140:
				InstructionSet.RES_1_H();
				break;
			case 141:
				InstructionSet.RES_1_L();
				break;
			case 142:
				InstructionSet.RES_1_HL();
				break;
			case 143:
				InstructionSet.RES_1_A();
				break;
			case 144:
				InstructionSet.RES_2_B();
				break;
			case 145:
				InstructionSet.RES_2_C();
				break;
			case 146:
				InstructionSet.RES_2_D();
				break;
			case 147:
				InstructionSet.RES_2_E();
				break;
			case 148:
				InstructionSet.RES_2_H();
				break;
			case 149:
				InstructionSet.RES_2_L();
				break;
			case 150:
				InstructionSet.RES_2_HL();
				break;
			case 151:
				InstructionSet.RES_2_A();
				break;
			case 152:
				InstructionSet.RES_3_B();
				break;
			case 153:
				InstructionSet.RES_3_C();
				break;
			case 154:
				InstructionSet.RES_3_D();
				break;
			case 155:
				InstructionSet.RES_3_E();
				break;
			case 156:
				InstructionSet.RES_3_H();
				break;
			case 157:
				InstructionSet.RES_3_L();
				break;
			case 158:
				InstructionSet.RES_3_HL();
				break;
			case 159:
				InstructionSet.RES_3_A();
				break;
			case 160:
				InstructionSet.RES_4_B();
				break;
			case 161:
				InstructionSet.RES_4_C();
				break;
			case 162:
				InstructionSet.RES_4_D();
				break;
			case 163:
				InstructionSet.RES_4_E();
				break;
			case 164:
				InstructionSet.RES_4_H();
				break;
			case 165:
				InstructionSet.RES_4_L();
				break;
			case 166:
				InstructionSet.RES_4_HL();
				break;
			case 167:
				InstructionSet.RES_4_A();
				break;
			case 168:
				InstructionSet.RES_5_B();
				break;
			case 169:
				InstructionSet.RES_5_C();
				break;
			case 170:
				InstructionSet.RES_5_D();
				break;
			case 171:
				InstructionSet.RES_5_E();
				break;
			case 172:
				InstructionSet.RES_5_H();
				break;
			case 173:
				InstructionSet.RES_5_L();
				break;
			case 174:
				InstructionSet.RES_5_HL();
				break;
			case 175:
				InstructionSet.RES_5_A();
				break;
			case 176:
				InstructionSet.RES_6_B();
				break;
			case 177:
				InstructionSet.RES_6_C();
				break;
			case 178:
				InstructionSet.RES_6_D();
				break;
			case 179:
				InstructionSet.RES_6_E();
				break;
			case 180:
				InstructionSet.RES_6_H();
				break;
			case 181:
				InstructionSet.RES_6_L();
				break;
			case 182:
				InstructionSet.RES_6_HL();
				break;
			case 183:
				InstructionSet.RES_6_A();
				break;
			case 184:
				InstructionSet.RES_7_B();
				break;
			case 185:
				InstructionSet.RES_7_C();
				break;
			case 186:
				InstructionSet.RES_7_D();
				break;
			case 187:
				InstructionSet.RES_7_E();
				break;
			case 188:
				InstructionSet.RES_7_H();
				break;
			case 189:
				InstructionSet.RES_7_L();
				break;
			case 190:
				InstructionSet.RES_7_HL();
				break;
			case 191:
				InstructionSet.RES_7_A();
				break;
			case 192:
				InstructionSet.SET_0_B();
				break;
			case 193:
				InstructionSet.SET_0_C();
				break;
			case 194:
				InstructionSet.SET_0_D();
				break;
			case 195:
				InstructionSet.SET_0_E();
				break;
			case 196:
				InstructionSet.SET_0_H();
				break;
			case 197:
				InstructionSet.SET_0_L();
				break;
			case 198:
				InstructionSet.SET_0_HL();
				break;
			case 199:
				InstructionSet.SET_0_A();
				break;
			case 200:
				InstructionSet.SET_1_B();
				break;
			case 201:
				InstructionSet.SET_1_C();
				break;
			case 202:
				InstructionSet.SET_1_D();
				break;
			case 203:
				InstructionSet.SET_1_E();
				break;
			case 204:
				InstructionSet.SET_1_H();
				break;
			case 205:
				InstructionSet.SET_1_L();
				break;
			case 206:
				InstructionSet.SET_1_HL();
				break;
			case 207:
				InstructionSet.SET_1_A();
				break;
			case 208:
				InstructionSet.SET_2_B();
				break;
			case 209:
				InstructionSet.SET_2_C();
				break;
			case 210:
				InstructionSet.SET_2_D();
				break;
			case 211:
				InstructionSet.SET_2_E();
				break;
			case 212:
				InstructionSet.SET_2_H();
				break;
			case 213:
				InstructionSet.SET_2_L();
				break;
			case 214:
				InstructionSet.SET_2_HL();
				break;
			case 215:
				InstructionSet.SET_2_A();
				break;
			case 216:
				InstructionSet.SET_3_B();
				break;
			case 217:
				InstructionSet.SET_3_C();
				break;
			case 218:
				InstructionSet.SET_3_D();
				break;
			case 219:
				InstructionSet.SET_3_E();
				break;
			case 220:
				InstructionSet.SET_3_H();
				break;
			case 221:
				InstructionSet.SET_3_L();
				break;
			case 222:
				InstructionSet.SET_3_HL();
				break;
			case 223:
				InstructionSet.SET_3_A();
				break;
			case 224:
				InstructionSet.SET_4_B();
				break;
			case 225:
				InstructionSet.SET_4_C();
				break;
			case 226:
				InstructionSet.SET_4_D();
				break;
			case 227:
				InstructionSet.SET_4_E();
				break;
			case 228:
				InstructionSet.SET_4_H();
				break;
			case 229:
				InstructionSet.SET_4_L();
				break;
			case 230:
				InstructionSet.SET_4_HL();
				break;
			case 231:
				InstructionSet.SET_4_A();
				break;
			case 232:
				InstructionSet.SET_5_B();
				break;
			case 233:
				InstructionSet.SET_5_C();
				break;
			case 234:
				InstructionSet.SET_5_D();
				break;
			case 235:
				InstructionSet.SET_5_E();
				break;
			case 236:
				InstructionSet.SET_5_H();
				break;
			case 237:
				InstructionSet.SET_5_L();
				break;
			case 238:
				InstructionSet.SET_5_HL();
				break;
			case 239:
				InstructionSet.SET_5_A();
				break;
			case 240:
				InstructionSet.SET_6_B();
				break;
			case 241:
				InstructionSet.SET_6_C();
				break;
			case 242:
				InstructionSet.SET_6_D();
				break;
			case 243:
				InstructionSet.SET_6_E();
				break;
			case 244:
				InstructionSet.SET_6_H();
				break;
			case 245:
				InstructionSet.SET_6_L();
				break;
			case 246:
				InstructionSet.SET_6_HL();
				break;
			case 247:
				InstructionSet.SET_6_A();
				break;
			case 248:
				InstructionSet.SET_7_B();
				break;
			case 249:
				InstructionSet.SET_7_C();
				break;
			case 250:
				InstructionSet.SET_7_D();
				break;
			case 251:
				InstructionSet.SET_7_E();
				break;
			case 252:
				InstructionSet.SET_7_H();
				break;
			case 253:
				InstructionSet.SET_7_L();
				break;
			case 254:
				InstructionSet.SET_7_HL();
				break;
			case 255:
				InstructionSet.SET_7_A();
				break;
			default:
				System.out.println("Invalid opcode: " + (opcode & 0xff));
			}
		} else {
			switch (opcode) {
			case 0:
				InstructionSet.NOP();
				break;
			case 1:
				InstructionSet.LD_BC_d16();
				break;
			case 2:
				InstructionSet.LD_BC_A();
				break;
			case 3:
				InstructionSet.INC_BC();
				break;
			case 4:
				InstructionSet.INC_B();
				break;
			case 5:
				InstructionSet.DEC_B();
				break;
			case 6:
				InstructionSet.LD_B_d8();
				break;
			case 7:
				InstructionSet.RLCA();
				break;
			case 8:
				InstructionSet.LD_a16_SP();
				break;
			case 9:
				InstructionSet.ADD_HL_BC();
				break;
			case 10:
				InstructionSet.LD_A_BC();
				break;
			case 11:
				InstructionSet.DEC_BC();
				break;
			case 12:
				InstructionSet.INC_C();
				break;
			case 13:
				InstructionSet.DEC_C();
				break;
			case 14:
				InstructionSet.LD_C_d8();
				break;
			case 15:
				InstructionSet.RRCA();
				break;
			case 16:
				InstructionSet.STOP();
				break;
			case 17:
				InstructionSet.LD_DE_d16();
				break;
			case 18:
				InstructionSet.LD_DE_A();
				break;
			case 19:
				InstructionSet.INC_DE();
				break;
			case 20:
				InstructionSet.INC_D();
				break;
			case 21:
				InstructionSet.DEC_D();
				break;
			case 22:
				InstructionSet.LD_D_d8();
				break;
			case 23:
				InstructionSet.RLA();
				break;
			case 24:
				InstructionSet.JR_s8();
				break;
			case 25:
				InstructionSet.ADD_HL_DE();
				break;
			case 26:
				InstructionSet.LD_A_DE();
				break;
			case 27:
				InstructionSet.DEC_DE();
				break;
			case 28:
				InstructionSet.INC_E();
				break;
			case 29:
				InstructionSet.DEC_E();
				break;
			case 30:
				InstructionSet.LD_E_d8();
				break;
			case 31:
				InstructionSet.RRA();
				break;
			case 32:
				InstructionSet.JR_NZ_s8();
				break;
			case 33:
				InstructionSet.LD_HL_d16();
				break;
			case 34:
				InstructionSet.LD_HLinc_A();
				break;
			case 35:
				InstructionSet.INC_HL();
				break;
			case 36:
				InstructionSet.INC_H();
				break;
			case 37:
				InstructionSet.DEC_H();
				break;
			case 38:
				InstructionSet.LD_H_d8();
				break;
			case 39:
				InstructionSet.DAA();
				break;
			case 40:
				InstructionSet.JR_Z_s8();
				break;
			case 41:
				InstructionSet.ADD_HL_HL();
				break;
			case 42:
				InstructionSet.LD_A_HLinc();
				break;
			case 43:
				InstructionSet.DEC_HL();
				break;
			case 44:
				InstructionSet.INC_L();
				break;
			case 45:
				InstructionSet.DEC_L();
				break;
			case 46:
				InstructionSet.LD_L_d8();
				break;
			case 47:
				InstructionSet.CPL();
				break;
			case 48:
				InstructionSet.JR_NC_s8();
				break;
			case 49:
				InstructionSet.LD_SP_d16();
				break;
			case 50:
				InstructionSet.LD_HLdec_A();
				break;
			case 51:
				InstructionSet.INC_SP();
				break;
			case 52:
				InstructionSet.INC_HLmem();
				break;
			case 53:
				InstructionSet.DEC_HLmem();
				break;
			case 54:
				InstructionSet.LD_HL_d8();
				break;
			case 55:
				InstructionSet.SCF();
				break;
			case 56:
				InstructionSet.JR_C_s8();
				break;
			case 57:
				InstructionSet.ADD_HL_SP();
				break;
			case 58:
				InstructionSet.LD_A_HLdec();
				break;
			case 59:
				InstructionSet.DEC_SP();
				break;
			case 60:
				InstructionSet.INC_A();
				break;
			case 61:
				InstructionSet.DEC_A();
				break;
			case 62:
				InstructionSet.LD_A_d8();
				break;
			case 63:
				InstructionSet.CFF();
				break;
			case 64:
				InstructionSet.LD_B_B();
				break;
			case 65:
				InstructionSet.LD_B_C();
				break;
			case 66:
				InstructionSet.LD_B_D();
				break;
			case 67:
				InstructionSet.LD_B_E();
				break;
			case 68:
				InstructionSet.LD_B_H();
				break;
			case 69:
				InstructionSet.LD_B_L();
				break;
			case 70:
				InstructionSet.LD_B_HL();
				break;
			case 71:
				InstructionSet.LD_B_A();
				break;
			case 72:
				InstructionSet.LD_C_B();
				break;
			case 73:
				InstructionSet.LD_C_C();
				break;
			case 74:
				InstructionSet.LD_C_D();
				break;
			case 75:
				InstructionSet.LD_C_E();
				break;
			case 76:
				InstructionSet.LD_C_H();
				break;
			case 77:
				InstructionSet.LD_C_L();
				break;
			case 78:
				InstructionSet.LD_C_HL();
				break;
			case 79:
				InstructionSet.LD_C_A();
				break;
			case 80:
				InstructionSet.LD_D_B();
				break;
			case 81:
				InstructionSet.LD_D_C();
				break;
			case 82:
				InstructionSet.LD_D_D();
				break;
			case 83:
				InstructionSet.LD_D_E();
				break;
			case 84:
				InstructionSet.LD_D_H();
				break;
			case 85:
				InstructionSet.LD_D_L();
				break;
			case 86:
				InstructionSet.LD_D_HL();
				break;
			case 87:
				InstructionSet.LD_D_A();
				break;
			case 88:
				InstructionSet.LD_E_B();
				break;
			case 89:
				InstructionSet.LD_E_C();
				break;
			case 90:
				InstructionSet.LD_E_D();
				break;
			case 91:
				InstructionSet.LD_E_E();
				break;
			case 92:
				InstructionSet.LD_E_H();
				break;
			case 93:
				InstructionSet.LD_E_L();
				break;
			case 94:
				InstructionSet.LD_E_HL();
				break;
			case 95:
				InstructionSet.LD_E_A();
				break;
			case 96:
				InstructionSet.LD_H_B();
				break;
			case 97:
				InstructionSet.LD_H_C();
				break;
			case 98:
				InstructionSet.LD_H_D();
				break;
			case 99:
				InstructionSet.LD_H_E();
				break;
			case 100:
				InstructionSet.LD_H_H();
				break;
			case 101:
				InstructionSet.LD_H_L();
				break;
			case 102:
				InstructionSet.LD_H_HL();
				break;
			case 103:
				InstructionSet.LD_H_A();
				break;
			case 104:
				InstructionSet.LD_L_B();
				break;
			case 105:
				InstructionSet.LD_L_C();
				break;
			case 106:
				InstructionSet.LD_L_D();
				break;
			case 107:
				InstructionSet.LD_L_E();
				break;
			case 108:
				InstructionSet.LD_L_H();
				break;
			case 109:
				InstructionSet.LD_L_L();
				break;
			case 110:
				InstructionSet.LD_L_HL();
				break;
			case 111:
				InstructionSet.LD_L_A();
				break;
			case 112:
				InstructionSet.LD_HL_B();
				break;
			case 113:
				InstructionSet.LD_HL_C();
				break;
			case 114:
				InstructionSet.LD_HL_D();
				break;
			case 115:
				InstructionSet.LD_HL_E();
				break;
			case 116:
				InstructionSet.LD_HL_H();
				break;
			case 117:
				InstructionSet.LD_HL_L();
				break;
			case 118:
				InstructionSet.HALT();
				break;
			case 119:
				InstructionSet.LD_HL_A();
				break;
			case 120:
				InstructionSet.LD_A_B();
				break;
			case 121:
				InstructionSet.LD_A_C();
				break;
			case 122:
				InstructionSet.LD_A_D();
				break;
			case 123:
				InstructionSet.LD_A_E();
				break;
			case 124:
				InstructionSet.LD_A_H();
				break;
			case 125:
				InstructionSet.LD_A_L();
				break;
			case 126:
				InstructionSet.LD_A_HL();
				break;
			case 127:
				InstructionSet.LD_A_A();
				break;
			case 128:
				InstructionSet.ADD_A_B();
				break;
			case 129:
				InstructionSet.ADD_A_C();
				break;
			case 130:
				InstructionSet.ADD_A_D();
				break;
			case 131:
				InstructionSet.ADD_A_E();
				break;
			case 132:
				InstructionSet.ADD_A_H();
				break;
			case 133:
				InstructionSet.ADD_A_L();
				break;
			case 134:
				InstructionSet.ADD_A_HL();
				break;
			case 135:
				InstructionSet.ADD_A_A();
				break;
			case 136:
				InstructionSet.ADC_A_B();
				break;
			case 137:
				InstructionSet.ADC_A_C();
				break;
			case 138:
				InstructionSet.ADC_A_D();
				break;
			case 139:
				InstructionSet.ADC_A_E();
				break;
			case 140:
				InstructionSet.ADC_A_H();
				break;
			case 141:
				InstructionSet.ADC_A_L();
				break;
			case 142:
				InstructionSet.ADC_A_HL();
				break;
			case 143:
				InstructionSet.ADC_A_A();
				break;
			case 144:
				InstructionSet.SUB_A_B();
				break;
			case 145:
				InstructionSet.SUB_A_C();
				break;
			case 146:
				InstructionSet.SUB_A_D();
				break;
			case 147:
				InstructionSet.SUB_A_E();
				break;
			case 148:
				InstructionSet.SUB_A_H();
				break;
			case 149:
				InstructionSet.SUB_A_L();
				break;
			case 150:
				InstructionSet.SUB_A_HL();
				break;
			case 151:
				InstructionSet.SUB_A_A();
				break;
			case 152:
				InstructionSet.SBC_A_B();
				break;
			case 153:
				InstructionSet.SBC_A_C();
				break;
			case 154:
				InstructionSet.SBC_A_D();
				break;
			case 155:
				InstructionSet.SBC_A_E();
				break;
			case 156:
				InstructionSet.SBC_A_H();
				break;
			case 157:
				InstructionSet.SBC_A_L();
				break;
			case 158:
				InstructionSet.SBC_A_HL();
				break;
			case 159:
				InstructionSet.SBC_A_A();
				break;
			case 160:
				InstructionSet.AND_B();
				break;
			case 161:
				InstructionSet.AND_C();
				break;
			case 162:
				InstructionSet.AND_D();
				break;
			case 163:
				InstructionSet.AND_E();
				break;
			case 164:
				InstructionSet.AND_H();
				break;
			case 165:
				InstructionSet.AND_L();
				break;
			case 166:
				InstructionSet.AND_HL();
				break;
			case 167:
				InstructionSet.AND_A();
				break;
			case 168:
				InstructionSet.XOR_B();
				break;
			case 169:
				InstructionSet.XOR_C();
				break;
			case 170:
				InstructionSet.XOR_D();
				break;
			case 171:
				InstructionSet.XOR_E();
				break;
			case 172:
				InstructionSet.XOR_H();
				break;
			case 173:
				InstructionSet.XOR_L();
				break;
			case 174:
				InstructionSet.XOR_HL();
				break;
			case 175:
				InstructionSet.XOR_A();
				break;
			case 176:
				InstructionSet.OR_B();
				break;
			case 177:
				InstructionSet.OR_C();
				break;
			case 178:
				InstructionSet.OR_D();
				break;
			case 179:
				InstructionSet.OR_E();
				break;
			case 180:
				InstructionSet.OR_H();
				break;
			case 181:
				InstructionSet.OR_L();
				break;
			case 182:
				InstructionSet.OR_HL();
				break;
			case 183:
				InstructionSet.OR_A();
				break;
			case 184:
				InstructionSet.CP_B();
				break;
			case 185:
				InstructionSet.CP_C();
				break;
			case 186:
				InstructionSet.CP_D();
				break;
			case 187:
				InstructionSet.CP_E();
				break;
			case 188:
				InstructionSet.CP_H();
				break;
			case 189:
				InstructionSet.CP_L();
				break;
			case 190:
				InstructionSet.CP_HL();
				break;
			case 191:
				InstructionSet.CP_A();
				break;
			case 192:
				InstructionSet.RET_NZ();
				break;
			case 193:
				InstructionSet.POP_BC();
				break;
			case 194:
				InstructionSet.JP_NZ_a16();
				break;
			case 195:
				InstructionSet.JP_a16();
				break;
			case 196:
				InstructionSet.CALL_NZ_a16();
				break;
			case 197:
				InstructionSet.PUSH_BC();
				break;
			case 198:
				InstructionSet.ADD_A_d8();
				break;
			case 199:
				InstructionSet.RST_0();
				break;
			case 200:
				InstructionSet.RET_Z();
				break;
			case 201:
				InstructionSet.RET();
				break;
			case 202:
				InstructionSet.JP_Z_a16();
				break;
			case 204:
				InstructionSet.CALL_Z_a16();
				break;
			case 205:
				InstructionSet.CALL_a16();
				break;
			case 206:
				InstructionSet.ADC_A_d8();
				break;
			case 207:
				InstructionSet.RST_1();
				break;
			case 208:
				InstructionSet.RET_NC();
				break;
			case 209:
				InstructionSet.POP_DE();
				break;
			case 210:
				InstructionSet.JP_NC_a16();
				break;
			case 211:
				// Do nothing
				break;
			case 212:
				InstructionSet.CALL_NC_a16();
				break;
			case 213:
				InstructionSet.PUSH_DE();
				break;
			case 214:
				InstructionSet.SUB_A_d8();
				break;
			case 215:
				InstructionSet.RST_2();
				break;
			case 216:
				InstructionSet.RET_C();
				break;
			case 217:
				InstructionSet.RETI();
				break;
			case 218:
				InstructionSet.JP_C_a16();
				break;
			case 219:
				// Do nothing
				break;
			case 220:
				InstructionSet.CALL_C_a16();
				break;
			case 221:
				// Do nothing
				break;
			case 222:
				InstructionSet.SBC_A_d8();
				break;
			case 223:
				InstructionSet.RST_3();
				break;
			case 224:
				InstructionSet.LD_a8_A();
				break;
			case 225:
				InstructionSet.POP_HL();
				break;
			case 226:
				InstructionSet.LD_Cmem_A();
				break;
			case 227:
				// Do nothing
				break;
			case 228:
				// Do nothing
				break;
			case 229:
				InstructionSet.PUSH_HL();
				break;
			case 230:
				InstructionSet.AND_d8();
				break;
			case 231:
				InstructionSet.RST_4();
				break;
			case 232:
				InstructionSet.ADD_SP_s8();
				break;
			case 233:
				InstructionSet.JP_HL();
				break;
			case 234:
				InstructionSet.LD_a16_A();
				break;
			case 235:
				// Do nothing
				break;
			case 236:
				// Do nothing
				break;
			case 237:
				// Do nothing
				break;
			case 238:
				InstructionSet.XOR_d8();
				break;
			case 239:
				InstructionSet.RST_5();
				break;
			case 240:
				InstructionSet.LD_A_a8();
				break;
			case 241:
				InstructionSet.POP_AF();
				break;
			case 242:
				InstructionSet.LD_A_Cmem();
				break;
			case 243:
				InstructionSet.DI();
				break;
			case 244:
				// Do nothing
				break;
			case 245:
				InstructionSet.PUSH_AF();
				break;
			case 246:
				InstructionSet.OR_d8();
				break;
			case 247:
				InstructionSet.RST_6();
				break;
			case 248:
				InstructionSet.LD_HL_SPplusS8();
				break;
			case 249:
				InstructionSet.LD_SP_HL();
				break;
			case 250:
				InstructionSet.LD_A_a16();
				break;
			case 251:
				InstructionSet.EI();
				break;
			case 252:
				// Do nothing
				break;
			case 253:
				// Do nothing
				break;
			case 254:
				InstructionSet.CP_d8();
				break;
			case 255:
				InstructionSet.RST_7();
				break;
			default:
				System.out.println("Invalid opcode: " + (opcode & 0xff));
			}
		}
	}

}
