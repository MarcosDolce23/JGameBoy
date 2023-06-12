package gb.apu;

import gb.Main;
import gb.utils.BitOperations;

// Sound Channel 4 — Noise

public class Channel4 extends Channel {
	
	public int chanFreq = 0;
	private int LFSR = 0x7fff;
	
	public int chanClockShift = 0;
	public float chanClockDivider = 0;
	public boolean LFSRWidth = false;
	
	@Override
	void chanUpdateFreq(int cycles) {
		chanFreqTimer -= cycles;
        if (chanFreqTimer <= 0) {
            chanFreqTimer += chanRawFreq;

            chanFreq = nextBitLFSR();
        }
	}

	@Override
	public void chanTrigger() {
        chanOn();

        // Restart envelope
        chanEnvVol = chanEnvInit;
        LFSR = 0x7fff;
        LFSRWidth = false;
        
        // Restart length
        if (chanLength == 0)
            chanLength = 64; // Full
	}

	@Override
	float getSample() {
		return chanFreq * chanEnvVol;
	}
	
	private int nextBitLFSR() {
        boolean x = ((LFSR & 1) ^ ((LFSR & 2) >> 1)) != 0;
        LFSR = LFSR >> 1;
        LFSR = LFSR | (x ? (1 << 14) : 0);
        if (LFSRWidth) {
            LFSR = LFSR | (x ? (1 << 6) : 0);
        }
        return 1 & ~LFSR;
    }

	@Override
	public void NRX1(int value) {
		chanLength = 64 - (value & 0x3f);
	}

	@Override
	public void NRX2(int value) {
		chanEnvInit =	(value >> 4) / 15f;
        chanEnvVol = (value >> 4) / 15f;
        
        if (!BitOperations.testBit(Main.mmu.ram[0xff26], 3))
        	chanEnvVol = 0;

        chanEnvInc = BitOperations.testBit(value, 3) ? true : false;
        int sweep = chanEnvSweep = value & 0b0111;

        chanEnvInterval = 512 * (sweep/64f);
        chanEnvOn = sweep > 0;
	}

	@Override
	public void NRX3(int value) {
		chanClockShift = (value & 0xf0) >> 4;
		
		chanClockDivider = value & 0x7;
		if (chanClockDivider == 0) {
			chanClockDivider = 0.5f;
		}

		LFSRWidth = BitOperations.testBit(value, 3);
		
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
        
        chanRawFreq = divisor << chanClockShift;
//		Main.apu.chan4RawFreq = (int) (Math.round(24288 / Main.apu.chan4ClockDivider / Math.pow(2, Main.apu.chan4ClockShift + 1)));
//		Main.apu.chan4RawFreq = (int) (Math.round((Main.apu.chan4ClockDivider * Math.pow(2, Main.apu.chan4ClockShift) * 262144)));
	}

	@Override
	public void NRX4(int value) {
		chanCounterSelect = (BitOperations.testBit(value, 6)) ? true : false; 

        // Trigger event
        if (BitOperations.testBit(value, 7))
            chanTrigger();

	}

	@Override
	public void NRX0(int value) {}

	@Override
	void resetChan() {
		chanFreq = 0;
		LFSR = 0x7fff;
		
		chanClockShift = 0;
		chanClockDivider = 0;
		LFSRWidth = false;
	}

	@Override
	void chanOn() {
		Main.mmu.ram[0xff26] |= 0x8;
	}

	@Override
	void chanOff() {
		Main.mmu.ram[0xff26] &= 0xf7;
	}

	@Override
	boolean chanEnabled() {
		// TODO Auto-generated method stub
		return BitOperations.testBit(Main.mmu.ram[0xff26], 3);
	}
}
