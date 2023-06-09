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
	public void chanTrigger() {
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
        if (chanVolShift == 4)
            return 0;
        else {
            int aux = 0;
            aux = chanGetSample();
            aux = (aux + (aux - 0xf)) >> chanVolShift;
            return aux / 15f;
        }
	}
	
	@Override
	public void chanDisable() {
        chanOn = false;
        chanVolShift = 4; // Mute
    }
	
	private int chanGetSample() {
		if ((chanSampleStep & 1) == 0) {
            return (((Main.mmu.ram[0xff30 + (chanSampleStep >> 1)] & 0xff) >> 4) & 0xf);
        }
        return (((Main.mmu.ram[0xff30 + (chanSampleStep >> 1)] & 0xff) & 0xf));
	}

}
