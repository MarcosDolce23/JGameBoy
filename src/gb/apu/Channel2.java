package gb.apu;

import gb.utils.BitOperations;

// Sound Channel 2 — Pulse

public class Channel2 extends Channel {
	
	// Pattern duty
    public int chanPatternDuty = 0;
    private int chanDutyStep = 0;
    
	@Override
	void chanUpdateFreq(int cycles) {
		chanFreqTimer -= cycles;
        if (chanFreqTimer <= 0) {
            chanFreqTimer += (2048 - chanRawFreq) * 4;

            chanDutyStep++;
            chanDutyStep &= 7;
        }
	}

	@Override
	public void chanTrigger() {
        chanOn = true;

        // Restart envelope
        chanEnvVol = chanEnvInit;

        // Restart length
        if (chanLength == 0)
            chanLength = 64; // Full
	}

	@Override
	float getSample() {
		return duty[chanPatternDuty][chanDutyStep] * chanEnvVol;
	}

	@Override
	public void NRX1(int value) {
		chanPatternDuty = value >> 6;
        chanLength = 64 - (value & 0x3f);
	}

	@Override
	public void NRX2(int value) {
		chanEnvInit = (value >> 4) / 15f;
        chanEnvVol = (value >> 4) / 15f;

        chanEnvInc = BitOperations.testBit(value, 3) ? true : false;
        int sweep = chanEnvSweep = value & 0x7;

        chanEnvInterval = 512 * (sweep/64f);
        chanEnvOn = sweep > 0;
	}

	@Override
	public void NRX3(int value) {
		chanRawFreq &= 0x700; // Preserve top bits
		chanRawFreq |= value;
	}

	@Override
	public void NRX4(int value) {
		chanCounterSelect = BitOperations.testBit(value, 6) ? true : false; 

        chanRawFreq &= 0xff; // Preserve bottom bits
        chanRawFreq |= (value & 0x7) << 8;

        // Trigger event
        if (BitOperations.testBit(value, 7))
            chanTrigger();
	}

	@Override
	public void NRX0(int value) {}

}
