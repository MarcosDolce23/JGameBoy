package gb.apu;

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
	void chanTrigger() {
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

}
