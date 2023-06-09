package gb.apu;

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
        chanOn = true;

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

}
