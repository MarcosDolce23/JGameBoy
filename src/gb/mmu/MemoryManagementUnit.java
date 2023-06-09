package gb.mmu;

import gb.Main;
import gb.utils.BitOperations;

public class MemoryManagementUnit {
	
	private static MemoryManagementUnit instance;

	public byte[] ram;
	
	private boolean enableRam = false;
	private boolean romBanking = true;
	
	// Return an int representation of the value of the memory at the give index
	public int getByte(int index) {
		
		// Cartridge has memory controller?
		if (Main.cartridge.mbc1 || Main.cartridge.mbc2 || Main.cartridge.mbc3) {
			
			// Reading from Rom bank
			if ((index >= 0x4000) && (index < 0x8000)) {
				int newIndex = (index - 0x4000) + (Main.cartridge.romBankNumber * 0x4000);
				return Main.cartridge.cartridgeMemory[newIndex] & 0xff;
			}
			
			// Reading from Ram memory bank
			if (enableRam && (index >= 0xA000) && (index <= 0xBFFF)) {
				int newIndex = (index - 0xA000) + (Main.cartridge.ramBankNumber * 0x2000);
				return Main.cartridge.ramBanks[newIndex] & 0xff;
			}
		}
		
		if (index == 0xff00) return Main.joypad.getJoypadState();
		
		// Wave pattern samples
		if ((index >= 0xff30) && (index <= 0xff3f)) {
			if (Main.apu.ch3.chanPlayback)
				return ram[0xff30 + (Main.apu.ch3.chanSampleStep >> 1)] & 0xff;
			else
				return ram[index] & 0xff;
		}
		
		return ram[index] & 0xff;
	
	}
	
	public int getSignedByte(int index) {
		// Cartridge has memory controller?
		if (Main.cartridge.mbc1 || Main.cartridge.mbc2 || Main.cartridge.mbc3) {
			
			// Reading from Rom bank
			if ((index >= 0x4000) && (index < 0x8000)) {
				int newIndex = (index - 0x4000) + (Main.cartridge.romBankNumber * 0x4000);
				return Main.cartridge.cartridgeMemory[newIndex];
			}
			
			// Reading from Ram memory bank
			if (enableRam && (index >= 0xA000) && (index <= 0xBFFF)) {
				int newIndex = (index - 0xA000) + (Main.cartridge.ramBankNumber * 0x2000);
				return Main.cartridge.ramBanks[newIndex];
			}
		}
		
		// Wave pattern samples
		if ((index >= 0xff30) && (index <= 0xff3f)) {
			if (Main.apu.ch3.chanPlayback)
				return ram[0xff30 + (Main.apu.ch3.chanSampleStep >> 1)];
			else
				return ram[index];
		}
		
		return ram[index];
	}
	
	// Set the position index of the memory with the byte representation of value
	public void setByte(int index, int value) {
		
		// Read Only Memory for Rom
		if (index < 0x8000) {
			handleBanking(index, value);
		}

		else if ((index >= 0xa000) && (index < 0xc000)) {
			if (enableRam) {
		       int newIndex = index - 0xa000;
		       Main.cartridge.ramBanks[newIndex + (Main.cartridge.ramBankNumber * 0x2000)] = (byte) value;
		     }
		}
		
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
				ram[index] = (byte) value;
				return;
			}
			
			if (index == 0xff07) {
				ram[index] = (byte) (value | 0xf8);
				return;
			}
			
			if (index == 0xff0f) {
				ram[index] = (byte) (value | 0xe0);
				return;
			}
			
            // ----- SQUARE CHANEL 1 ----- //
            // NR10 - sweep reg
			if (index == 0xff10) {
                Main.apu.ch1.chanSweepTime = 512 * ((value >> 4) / 128);

                Main.apu.ch1.chanSweepDec = BitOperations.testBit(value, 3) ? true : false;
                Main.apu.ch1.chanSweepNum = value & 0b0111;
				
				ram[index] = (byte) value;
				return;
			}
			
			// NR11 - length and pattern duty
			if (index == 0xff11) {
				Main.apu.ch1.chanPatternDuty = value >> 6;
                Main.apu.ch1.chanLength = 64 - (value & 0x3f);
				
				ram[index] = (byte) (value | 0x3f);
				return;
			}
			
			// NR12 - volume envelope
			if (index == 0xff12) {
                Main.apu.ch1.chanEnvInit =	(value >> 4) / 15f;
                Main.apu.ch1.chanEnvVol = (value >> 4) / 15f;
                
                if (!Main.apu.ch1.chanOn)
                	Main.apu.ch1.chanEnvVol = 0;

                Main.apu.ch1.chanEnvInc = BitOperations.testBit(value, 3) ? true : false;
                int sweep = Main.apu.ch1.chanEnvSweep = value & 0b0111;

                Main.apu.ch1.chanEnvInterval = 512 * (sweep/64f);
                Main.apu.ch1.chanEnvOn = sweep > 0;
				
				ram[index] = (byte) value;
				return;
			}
			
			// NR13 - lower 8 bits of frequency
			if (index == 0xff13) {
                Main.apu.ch1.chanInitFreq &= 0x700; // Preserve top bits
                Main.apu.ch1.chanInitFreq |= value;
                Main.apu.ch1.chanRawFreq = Main.apu.ch1.chanInitFreq;
				ram[index] = (byte) value;
                return;
			}
			
			// NR14 - higher 3 bits of frequency
			if (index == 0xff14) {
                Main.apu.ch1.chanCounterSelect = (BitOperations.testBit(value, 6)) ? true : false; 

                Main.apu.ch1.chanInitFreq &= 0xff; // Preserve bottom bits
                Main.apu.ch1.chanInitFreq |= (value & 0x7) << 8;
                Main.apu.ch1.chanRawFreq = Main.apu.ch1.chanInitFreq;

                // Trigger event
                if (BitOperations.testBit(value, 7))
                    Main.apu.ch1.chanTrigger();

				ram[index] = (byte) (value | 0xbf);
				return;
			}
			
			// ---- SQUARE CHANNEL 2 ---- //
            // NR21 - length and pattern duty
			if (index == 0xff16) {
				Main.apu.ch2.chanPatternDuty = value >> 6;
                Main.apu.ch2.chanLength = 64 - (value & 0x3f);
                
				ram[index] = (byte) (value | 0x3f);
				return;
			}
			
			// NR22 - volume envelope
			if (index == 0xff17) {
				Main.apu.ch2.chanEnvInit = (value >> 4) / 15f;
		        Main.apu.ch2.chanEnvVol = (value >> 4) / 15f;

		        Main.apu.ch2.chanEnvInc = BitOperations.testBit(value, 3) ? true : false;
		        int sweep = Main.apu.ch2.chanEnvSweep = value & 0x7;

		        Main.apu.ch2.chanEnvInterval = 512 * (sweep/64f);
		        Main.apu.ch2.chanEnvOn = sweep > 0;
		                
				ram[index] = (byte) value;
				return;
			}
			
			// NR23 - lower 8 bits of frequency
			if (index == 0xff18) {
				Main.apu.ch2.chanRawFreq &= 0x700; // Preserve top bits
				Main.apu.ch2.chanRawFreq |= value;
                return;
			}
			
			// NR24 - higher 3 bits of frequency
			if (index == 0xff19) {
				Main.apu.ch2.chanCounterSelect = BitOperations.testBit(value, 6) ? true : false; 

                Main.apu.ch2.chanRawFreq &= 0xff; // Preserve bottom bits
                Main.apu.ch2.chanRawFreq |= (value & 0x7) << 8;

                // Trigger event
                if (BitOperations.testBit(value, 7))
                    Main.apu.ch2.chanTrigger();
				
				ram[index] = (byte) (value | 0xbf);
				return;
			}
			
			// ---- WAVE CHANNEL 3 ---- //
            // NR30 - playback enable
			if (index == 0xff1a) {
				if (!(Main.apu.ch3.chanPlayback = BitOperations.testBit(value, 7) ? true : false))
                    Main.apu.ch3.chanDisable();
				
				ram[index] = (byte) (value | 0x7f);
				return;
			}
			
			// NR31 - length
			if (index == 0xff1b) {
				Main.apu.ch3.chanLength = 256 - value;
                return;
			}
			
			// NR32 - volume shift
			if (index == 0xff1c) {
				int volshift = (value & 0x60) >> 5;
                    
                if (volshift != 0) {
                	Main.apu.ch3.chanInitVolShift = volshift - 1;
                	volshift = volshift - 1;
                } else {
                	Main.apu.ch3.chanInitVolShift = 4;
                	volshift = 4;
                }

                if (Main.apu.ch3.chanOn)
                    Main.apu.ch3.chanVolShift = volshift;
				
				ram[index] = (byte) (value | 0x9f);
				return;
			}
			
			// NR33 - lower 8 bits of frequency
			if (index == 0xff1d) {
				Main.apu.ch3.chanRawFreq &= 0x700; // Preserve top bits
                Main.apu.ch3.chanRawFreq |= value;
				ram[index] = (byte) (value | 0xbf);
                return;
			}
			
			// NR34 - higher 3 bits of frequency
			if (index == 0xff1e) {
				Main.apu.ch3.chanCounterSelect = BitOperations.testBit(value, 6) ? true : false; 

                Main.apu.ch3.chanRawFreq &= 0xff; // Preserve bottom bits
                Main.apu.ch3.chanRawFreq |= (value & 0b0111) << 8;

                // Trigger event
                if (BitOperations.testBit(value, 7))
                    Main.apu.ch3.chanTrigger();
				
				ram[index] = (byte) (value | 0xbf);
				return;
			}

			// NR41 - Channel 4 length timer [write-only]
			if (index == 0xff20) {
				Main.apu.ch4.chanLength = 64 - (value & 0x3f);
				ram[index] = (byte) (value | 0x3f);
				return;
			}
			
			// NR42 - Channel 4 volume & envelope
			if (index == 0xff21) {
				Main.apu.ch4.chanEnvInit =	(value >> 4) / 15f;
                Main.apu.ch4.chanEnvVol = (value >> 4) / 15f;
                
                if (!Main.apu.ch4.chanOn)
                	Main.apu.ch4.chanEnvVol = 0;

                Main.apu.ch4.chanEnvInc = BitOperations.testBit(value, 3) ? true : false;
                int sweep = Main.apu.ch4.chanEnvSweep = value & 0b0111;

                Main.apu.ch4.chanEnvInterval = 512 * (sweep/64f);
                Main.apu.ch4.chanEnvOn = sweep > 0;
				
				ram[index] = (byte) value;
				return;
			}

			if (index == 0xff22) {
				Main.apu.ch4.chanClockShift = (value & 0xf0) >> 4;
				
				Main.apu.ch4.chanClockDivider = value & 0x7;
				if (Main.apu.ch4.chanClockDivider == 0) {
					Main.apu.ch4.chanClockDivider = 0.5f;
				}

				Main.apu.ch4.LFSRWidth = BitOperations.testBit(value, 3);
				
				int divisor;
		        switch (value & 0b111) {
		            case 0:
		                divisor = 8;
		                break;

		            case 1:
		                divisor = 16;
		                break;

		            case 2:
		                divisor = 32;
		                break;

		            case 3:
		                divisor = 48;
		                break;

		            case 4:
		                divisor = 64;
		                break;

		            case 5:
		                divisor = 80;
		                break;

		            case 6:
		                divisor = 96;
		                break;

		            case 7:
		                divisor = 112;
		                break;

		            default:
		                throw new IllegalStateException();
		        }
		        
		        Main.apu.ch4.chanRawFreq = divisor << Main.apu.ch4.chanClockShift;
//				Main.apu.chan4RawFreq = (int) (Math.round(24288 / Main.apu.chan4ClockDivider / Math.pow(2, Main.apu.chan4ClockShift + 1)));
//				Main.apu.chan4RawFreq = (int) (Math.round((Main.apu.chan4ClockDivider * Math.pow(2, Main.apu.chan4ClockShift) * 262144)));
				ram[index] = (byte) value;
				return;
			}

			if (index == 0xff23) {
				Main.apu.ch4.chanCounterSelect = (BitOperations.testBit(value, 6)) ? true : false; 

                // Trigger event
                if (BitOperations.testBit(value, 7))
                    Main.apu.ch4.chanTrigger();

				ram[index] = (byte) (value | 0xbf);
				return;
			}

			// Wave pattern samples
			if ((index >= 0xff30) && (index <= 0xff3f)) {
                if (Main.apu.ch3.chanPlayback)
                    ram[0xff30 + (Main.apu.ch3.chanSampleStep >> 1)] = (byte) value;
                else
                    ram[index] = (byte) value;
                return;
			}
			
			// ---- AUDIO SETTINGS ---- //
            // NR50 - i need this so pokemon blue dont freeze
			if (index == 0xff24) {
				ram[index] = (byte) value;
				return;
			}
			
			// NR52 - Sound enable / status
			if (index == 0xff26) {
				Main.apu.soundOn = BitOperations.testBit(value, 7) ? true : false;

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
				int preStat = ram[index] & 0x78;

				// Update stat signal on a change
                if ((value & 0x78) != preStat)
                	Main.ppu.updateStatSignal();
                
				ram[index] &= 0x07;
				ram[index] |= (value & 0xf8) | 0x80;
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
			
			if (index == 0xff44) {
				ram[index] = 0;
				return;
			}
			
			if (index == 0xff45) {
				ram[index] = (byte) value;
				Main.ppu.checkCoincidence();
				Main.ppu.updateStatSignal();
				return;
			}
			
	        // DMA transfer
			if (index == 0xff46) {
				int dest = value * 256;
				
				for (int i = 0; i < 0xa0; i++) {
					int val = getByte(dest | i);
					setByte(0xfe00 | i, val);
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
	
	private void setBootValues() {
		// Hardware register after boot for DMG / MGB
//		ram[0xff00] = (byte) 0xcf;
		ram[0xff02] = (byte) 0x7e;
		ram[0xff04] = (byte) 0xab;
		ram[0xff07] = (byte) 0xf8;
		ram[0xff0f] = (byte) 0xe1;
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
		ram[0xff41] = (byte) 0x86;
		ram[0xff46] = (byte) 0xff;
		ram[0xff47] = (byte) 0xfc;
		ram[0xff48] = (byte) 0xff;
		ram[0xff49] = (byte) 0xff;
		ram[0xff4d] = (byte) 0xff;
		ram[0xff4f] = (byte) 0xff;
		ram[0xff50] = (byte) 0x01;
		ram[0xff51] = (byte) 0xff;
		ram[0xff52] = (byte) 0xff;
		ram[0xff53] = (byte) 0xff;
		ram[0xff54] = (byte) 0xff;
		ram[0xff55] = (byte) 0xff;
		ram[0xff56] = (byte) 0xff;
		ram[0xff68] = (byte) 0xff;
		ram[0xff69] = (byte) 0xff;
		ram[0xff6a] = (byte) 0xff;
		ram[0xff6b] = (byte) 0xff;
		ram[0xff70] = (byte) 0xff;
	}
	
	private MemoryManagementUnit() {
		ram = new byte[0x10000]; // No estoy seguro del " + 1" lo agregué porque si escribo en el registro FFFF me da error ya que llega hasta el FFFE. Necesito clarificar este asunto
		setBootValues();
	}
	
	public static MemoryManagementUnit getInstance() {
		if (instance == null) {
			instance = new MemoryManagementUnit();
		}
		return instance;
	}
	
	private void handleBanking(int index, int value) {
		value &= 0xff;
		// do RAM enabling
		if (index < 0x2000) {
			if (Main.cartridge.mbc1 || Main.cartridge.mbc2 || Main.cartridge.mbc3) {
				doRamBankEnable(index,value);
			}
		}
	
		// Select ROM bank number
		else if ((index >= 0x2000) && (index < 0x4000)) {
			if (Main.cartridge.mbc1 || Main.cartridge.mbc2 || Main.cartridge.mbc3) {
				doChangeLoRomBank(value) ;
			}
		}
	
		// do ROM or RAM bank change
		else if ((index >= 0x4000) && (index < 0x6000)) {
			// There is no rambank in mbc2 so always use rambank 0
			if (Main.cartridge.mbc1) {
					if(romBanking) {
						doChangeHiRomBank(value);
					}
					else {
						doRamBankChange(value);
					}
			}
			
			if (Main.cartridge.mbc3) {
				if (value < 0x04) {
					Main.cartridge.ramBankNumber = value;
				} else if ((value > 0x07) && (value < 0x0d)) {
					Main.cartridge.rtg = value;
				}
			}
		}
	
		// this will change whether we are doing ROM banking
		// or RAM banking with the above if statement
		else if ((index >= 0x6000) && (index < 0x8000)) {
			if (Main.cartridge.mbc1) {
				doChangeRomRamMode(value);
			}
		}
	}
	
	private void doRamBankEnable(int index, int value) {
		if (Main.cartridge.mbc2) {
			if (BitOperations.testBit(value, 4))
				return;
		}
		
		if ((value & 0x0f) == 0x0a)
			enableRam = true;
		else
			enableRam = false;
	}
	
	private void doChangeLoRomBank(int value) {
		if (Main.cartridge.mbc1) {
			Main.cartridge.romBankNumber = value & 0x1f;
			if ((value == 0x00) || (value == 0x20) || (value == 0x40) || (value == 0x60))
				Main.cartridge.romBankNumber++;
		} else if (Main.cartridge.mbc2) {
			Main.cartridge.romBankNumber = value & 0xf;
			if (Main.cartridge.romBankNumber == 0)
				Main.cartridge.romBankNumber++;
		} else if (Main.cartridge.mbc3) {
			Main.cartridge.romBankNumber = value & 0x1f;
			if (value == 0x00) {
				Main.cartridge.romBankNumber++;
			}
		}
	}
	
	private void doChangeHiRomBank(int value) {
		Main.cartridge.romBankNumber |= (value & 3) << 5;
	}
	
	private void doRamBankChange(int value) {
		Main.cartridge.ramBankNumber = value & 0x3;
	}
	
	private void doChangeRomRamMode(int value) {
		int newValue = value & 0x1;
		romBanking = (newValue == 0) ? true : false;
		if (romBanking)
		   Main.cartridge.ramBankNumber = 0 ;
	}
}
