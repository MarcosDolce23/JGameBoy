package gb.apu;

// Sound Channel 4 — Noise

public class Channel4 extends Channel {
	
	public int chanFreq = 0;
	private int LFSR = 0x7fff;
	private boolean LFSRWidth = false;
	
	@Override
	void chanUpdateFreq(int cycles) {
		chanFreqTimer -= cycles;
        if (chanFreqTimer <= 0) {
            chanFreqTimer += chanRawFreq;

            chanFreq = nextBitLFSR();
        }
	}

	@Override
	void chanTrigger() {
        chanOn = true;

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
        if ((1 & ~LFSR) == 0)
        	return -1;
        return 1;
    }
}
