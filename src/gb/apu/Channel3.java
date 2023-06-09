package gb.apu;

import gb.Main;

// Sound Channel 3 — Wave output

public class Channel3 extends Channel {
	
	public int chanSampleStep = 0;
	
	// Volume shift
    public int chanInitVolShift = 0;
    public int chanVolShift = 4;
    
    public boolean chanPlayback = false;
    
	@Override
	void chanUpdateFreq(int cycles) {
		chanFreqTimer -= cycles;
        if (chanFreqTimer <= 0) {
            chanFreqTimer += (2048 - chanRawFreq) * 2;

            chanSampleStep++;
            chanSampleStep &= 0x1f;
        }
	}

	@Override
	void chanTrigger() {
        if (!chanPlayback)
            return;

        chanOn = true;

        chanVolShift = chanInitVolShift;

        // Restart length
        if (chanLength == 0)
            chanLength = 256; // Full
	}

	@Override
	float getSample() {
		if ((chanSampleStep & 1) == 0) {
            return (((Main.mmu.ram[0xff30 + (chanSampleStep >> 1)] & 0xff) >> 4) & 0xf);
        }
        return (((Main.mmu.ram[0xff30 + (chanSampleStep >> 1)] & 0xff) & 0xf));
	}

}
