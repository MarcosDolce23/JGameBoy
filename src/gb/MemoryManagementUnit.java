package gb;

public class MemoryManagementUnit {
	
	public byte[] ram;
	
	// Return an int representation of the value of the memory at the give index
	public int getByte(int index) {
		if (index == 0xff00) return Main.joypad.getJoypadState();
		return ram[index] & 0xFF;
	}
	
	public int getSignedByte(int index) {
		return ram[index];
	}
	
	// Set the position index of the memory with the byte representation of value
	public void setByte(int index, int value) {
		
		// Read Only Memory for Rom
		if (index < 0x8000) {}
		
		// Writing to ECHO Ram also writes in RAM
		else if ((index >= 0xe000 ) && (index < 0xfe00)) {
			ram[index] = (byte) value;
			setByte(index - 0x2000, value);
		}
		
		// This area is restricted
		else if ((index >= 0xfea0) && (index < 0xfeff)) {}
		
		// Almost no control is needed over this area so write to memory
		else {
			
			if (index == 0xff00) {
//				Main.joypad.actionButtons = (value & 0x20) != 0x20 ? true : false;
//                Main.joypad.directionButtons = (value & 010) != 0x10 ? true : false;
//
//                Main.joypad.CheckJoypad();

                ram[index] &= 0x0f;
				ram[index] |= (value & 0xf0) | 0xc0;
				return;
			}
			
			if (index == 0xff01) {
				ram[index] = (byte) value;
				return;
			}
			
			if (index == 0xff02) {
				ram[index] = (byte) value;
				return;
			}
			
			if (index == 0xff04) {
				ram[index] = 0;
				return;
			}
			
			if (index == 0xff05) {
				ram[index] = (byte) value;
				return;
			}
			
			if (index == 0xff06) {
				ram[index] = (byte) (value | 0xf8);
				return;
			}
			
			if (index == 0xff0f) {
				ram[index] = (byte) (value | 0xe0);
				return;
			}
			
			if (index == 0xff10) {
				ram[index] = (byte) value;
				return;
			}
			
			if (index == 0xff11) {
				ram[index] = (byte) (value | 0x3f);
				return;
			}
			
			if (index == 0xff012) {
				ram[index] = (byte) value;
				return;
			}
			
			if (index == 0xff14) {
				ram[index] = (byte) (value | 0xbf);
				return;
			}
			
			if (index == 0xff16) {
				ram[index] = (byte) (value | 0x3f);
				return;
			}
			
			if (index == 0xff17) {
				ram[index] = (byte) (value | 0x3f);
				return;
			}
			
			if (index == 0xff19) {
				ram[index] = (byte) (value | 0xbf);
				return;
			}
			
			if (index == 0xff1a) {
				ram[index] = (byte) (value | 0x7f);
				return;
			}
			
			if (index == 0xff1c) {
				ram[index] = (byte) (value | 0x9f);
				return;
			}
			
			if (index == 0xff1e) {
				ram[index] = (byte) (value | 0xbf);
				return;
			}
			
			if (index == 0xff3f) {
				ram[index] = (byte) value;
				return;
			}
			
			if (index == 0xff24) {
				ram[index] = (byte) value;
				return;
			}
			
			if (index == 0xff26) {
				ram[index] = (byte) (value | 0x7f);
				return;
			}
			
			if (index == 0xff40) {
				boolean lcdWasOn = Main.ppu.lcdEnable();
				ram[index] = (byte) value;

				if (lcdWasOn != Main.ppu.lcdEnable()) {
                    if (Main.ppu.lcdEnable())
                        Main.ppu.turnLcdOn ();
                    else
                        Main.ppu.turnLcdOff ();
                }
				return;
			}
			
			if (index == 0xff41) {
				// bits 0-2 areonly lecture
				PixelProcessingUnit ppu = Main.ppu;
				int preStat = ram[index] & 0b01111000;

				// Update stat signal on a change
                if ((value & 0b01111000) != preStat)
                    ppu.updateStatSignal();
                
				ram[index] &= 0x07;
				ram[index] |= (value & 0xf8) | 0x80;
//                ram[index] = (byte) value;
				return;
			}
			
			if (index == 0xff42) {
				ram[index] = (byte) value;
				return;
			}
			
			if (index == 0xff43) {
				ram[index] = (byte) value;
				return;
			}
			
			if (index == 0xff45) {
				PixelProcessingUnit ppu = Main.ppu;
				ppu.checkCoincidence();
				ppu.updateStatSignal();
				ram[index] = (byte) value;
				return;
			}
			
	        // DMA transfer
			if (index == 0xff46) {
				int dest = value * 256;
				
				for (int i = 0; i < 0xa0; i++) {
					int data = getByte(dest | i);
					setByte(0xfe00 | i, data);
				}
			}
			
			if (index == 0xff47) {
				ram[index] = (byte) value;
				return;
			}
			
			if (index == 0xff48) {
				ram[index] = (byte) value;
				return;
			}
			
			if (index == 0xff49) {
				ram[index] = (byte) value;
				return;
			}
			
			if (index == 0xff4a) {
				ram[index] = (byte) value;
				return;
			}
			
			if (index == 0xff4b) {
				ram[index] = (byte) value;
				return;
			}
			
			if (index == 0xff50) {
				ram[index] = (byte) (value | 0xfe);
				return;
			}
			
			ram[index] = (byte) value;
		}
		
	}
//			[0xff00] - joypad input
//		if (index == 0xff00) {
//			ram[index] = readInput(value);
//			return;
//		}

//			[0xff50] - lock bootrom
//		else if (index == 0xff50 && value == 1) {
//			lockBootROM();
//			ram[index] = value;
//		}

//			[0xff46] - oam dma transfer
//		else if (index == 0xff46) {
//			dmaOAMtransfer();
//			ram[index] = val;
//		}

//			[0xff11] - SC1 trigger
//		else if (index == 0xff14 && (val >> 7)) {
//			ram[index] = val;
//			resetSC1length(readFromMem(0xff11) & 0x3f);
//			return;
//		}
//			[0xff21] - SC2 trigger
//		else if (index == 0xff19 && (val >> 7)) {
//			ram[index] = val;
//			resetSC2length(readFromMem(0xff16) & 0x3f);
//			return;
//		}
////			[0xff31] - SC3 trigger
//		else if (index == 0xff1e && (val >> 7)) {
//			ram[index] = val;
//			resetSC3length(readFromMem(0xff1b));
//			return;
//		}
////			[0xff41] - SC4 trigger
//		else if (index == 0xff23 && (val >> 7)) {
//			ram[index] = val;
//			resetSC4length(readFromMem(0xff20) & 0x3f);
//			return;
//		}
	
	
	private void setBootValues() {
		// Hardware register after boot for DMG / MGB
//		ram[0xff00] = (byte) 0xcf;
//		ram[0xff02] = (byte) 0x7e;
//		ram[0xff04] = (byte) 0xab;
//		ram[0xff07] = (byte) 0xf8;
//		ram[0xff0f] = (byte) 0xe1;
//		ram[0xff10] = (byte) 0x80;
//		ram[0xff11] = (byte) 0xbf;
//		ram[0xff12] = (byte) 0xf3;
//		ram[0xff13] = (byte) 0xff;
//		ram[0xff14] = (byte) 0xbf;
//		ram[0xff16] = (byte) 0x3f;
//		ram[0xff18] = (byte) 0xff;
//		ram[0xff19] = (byte) 0xbf;
//		ram[0xff1a] = (byte) 0x7f;
//		ram[0xff1b] = (byte) 0xff;
//		ram[0xff1c] = (byte) 0x9f;
//		ram[0xff1d] = (byte) 0xff;
//		ram[0xff1e] = (byte) 0xbf;
//		ram[0xff20] = (byte) 0xff;
//		ram[0xff23] = (byte) 0xbf;
//		ram[0xff24] = (byte) 0x77;
//		ram[0xff25] = (byte) 0xf3;
//		ram[0xff26] = (byte) 0xf1;
		ram[0xff40] = (byte) 0x91;
//		ram[0xff41] = (byte) 0x86;
//		ram[0xff46] = (byte) 0xff;
		ram[0xff47] = (byte) 0xfc;
		ram[0xff48] = (byte) 0xff;
		ram[0xff49] = (byte) 0xff;
//		ram[0xff4d] = (byte) 0xff;
//		ram[0xff4f] = (byte) 0xff;
		ram[0xff50] = (byte) 0x01;
//		ram[0xff51] = (byte) 0xff;
//		ram[0xff52] = (byte) 0xff;
//		ram[0xff53] = (byte) 0xff;
//		ram[0xff54] = (byte) 0xff;
//		ram[0xff55] = (byte) 0xff;
//		ram[0xff56] = (byte) 0xff;
//		ram[0xff68] = (byte) 0xff;
//		ram[0xff69] = (byte) 0xff;
//		ram[0xff6a] = (byte) 0xff;
//		ram[0xff6b] = (byte) 0xff;
//		ram[0xff70] = (byte) 0xff;
	}
	
	public MemoryManagementUnit() {
		ram = new byte[0x10000]; // No estoy seguro del " + 1" lo agregué porque si escribo en el registro FFFF me da error ya que llega hasta el FFFE. Necesito clarificar este asunto
		setBootValues();
	}
}
