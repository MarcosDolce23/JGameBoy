package gb.apu;

import gb.Main;
import gb.utils.BitOperations;

// Sound Channel 1 — Pulse with wavelength sweep

public class Channel1 extends Channel {
	
    public int chanInitFreq = 0;

	// Pattern duty
    public int chanPatternDuty = 0;
    private int chanDutyStep = 0;
    
    // Sweep register
    public boolean chanSweepOn = false;
    public boolean chanSweepDec = false;
    public int chanSweepTime = 0;
    public int chanSweepNum = 0;
    private int chanSweepClocks = 0;

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
        if (dacOn)
            chanOn();

        // Restart envelope
        chanEnvVol = chanEnvInit;
        chanRawFreq = chanInitFreq;

        // Restart length
        if (chanLength == 0)
            chanLength = 64; // Full

        // Restart sweep
        if (chanSweepTime > 0)
            chanSweepClocks = chanSweepTime;
        else
            chanSweepClocks = 8;

        if ((chanSweepNum > 0) && (calcSweep() > 2047))
            chanDisable();
	}

	@Override
	float getSample() {
		return duty[chanPatternDuty][chanDutyStep] * chanEnvVol;
	}
	
	private int calcSweep() {
        int preFreq = chanRawFreq;
        int newFreq = preFreq >> chanSweepNum;

        newFreq = preFreq + (chanSweepDec ? -newFreq : newFreq);
        return newFreq;
    }
	
	public void chanUpdateSweep() {
        if (--chanSweepClocks > 0)
            return;

        // When clocks reaches 0 ...
        if (chanSweepTime > 0) {
            chanSweepClocks = chanSweepTime;

            int newFreq = calcSweep();

            boolean overflow = newFreq > 2047;
            if (!overflow && (chanSweepNum > 0)) {
                chanRawFreq = newFreq;

                if (calcSweep() > 2047)
                    chanDisable();
            } else if (overflow)
                chanDisable();
        } else
            chanSweepClocks = 8;
    }
	
	@Override
	public void NRX0(int value) {
        chanSweepTime = 512 * ((value >> 4) / 128);
        chanSweepDec = BitOperations.testBit(value, 3) ? true : false;
        chanSweepNum = value & 0b0111;
	}

	@Override
	public void NRX1(int value) {
		chanPatternDuty = value >> 6;
        chanLength = 64 - (value & 0x3f);
	}

	@Override
	public void NRX2(int value) {
        if (!(dacOn = (value & 0xf8) == 0 ? false : true))
            chanDisable();

		chanEnvInit =	(value >> 4) / 15f;
        chanEnvVol = (value >> 4) / 15f;
        
        if(!chanEnabled())
        	chanEnvVol = 0;

        chanEnvInc = BitOperations.testBit(value, 3) ? true : false;
        int sweep = chanEnvSweep = value & 0b0111;

        chanEnvInterval = 512 * (sweep/64f);
        chanEnvOn = sweep > 0;
	}

	@Override
	public void NRX3(int value) {
        chanInitFreq &= 0x700; // Preserve top bits
        chanInitFreq |= value;
        chanRawFreq = chanInitFreq;
	}

	@Override
	public void NRX4(int value) {
        chanCounterSelect = (BitOperations.testBit(value, 6)) ? true : false; 

        chanInitFreq &= 0xff; // Preserve bottom bits
        chanInitFreq |= (value & 0x7) << 8;
        chanRawFreq = chanInitFreq;

        // Trigger event
        if (BitOperations.testBit(value, 7))
            chanTrigger();
	}

	@Override
	void resetChan() {
	    chanInitFreq = 0;

		// Pattern duty
	    chanPatternDuty = 0;
	    chanDutyStep = 0;
	    
	    // Sweep register
	    chanSweepOn = false;
	    chanSweepDec = false;
	    chanSweepTime = 0;
	    chanSweepNum = 0;
	    chanSweepClocks = 0;
	}

	@Override
	void chanOn() {
		Main.mmu.ram[0xff26] |= 0x1;
	}

	@Override
	void chanOff() {
		Main.mmu.ram[0xff26] &= 0xfe;
	}

	@Override
	boolean chanEnabled() {
		return BitOperations.testBit(Main.mmu.ram[0xff26], 0);
	}

    @Override
    void tickLength() {
        chanLength--;
        int lenght = Main.mmu.ram[0xff11] & 0x3f;
        lenght++;
        Main.mmu.ram[0xff11] = (byte) ((Main.mmu.ram[0xff11] & 0xc0) | (lenght & 0x3f));
    }

}
