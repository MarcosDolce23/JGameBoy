package gb.apu;

import gb.Main;
import gb.utils.BitOperations;

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
		chanOn();
		
		if (!chanPlayback)
            return;

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
        chanOff();
        chanVolShift = 4; // Mute
    }
	
	private int chanGetSample() {
		if ((chanSampleStep & 1) == 0) {
            return (((Main.mmu.ram[0xff30 + (chanSampleStep >> 1)] & 0xff) >> 4) & 0xf);
        }
        return (((Main.mmu.ram[0xff30 + (chanSampleStep >> 1)] & 0xff) & 0xf));
	}
	
	@Override
	public void NRX0(int value) {
		if (!(chanPlayback = BitOperations.testBit(value, 7) ? true : false))
            chanDisable();
	}
	
	@Override
	public void NRX1(int value) {
		chanLength = 256 - value;
	}

	@Override
	public void NRX2(int value) {
		int volshift = (value & 0x60) >> 5;
        
        if (volshift != 0) {
        	chanInitVolShift = volshift - 1;
        	volshift = volshift - 1;
        } else {
        	chanInitVolShift = 4;
        	volshift = 4;
        }

        if (chanEnabled())
            chanVolShift = volshift;
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
        chanRawFreq |= (value & 0b0111) << 8;

        // Trigger event
        if (BitOperations.testBit(value, 7))
            chanTrigger();
	}

	@Override
	void resetChan() {
		chanSampleStep = 0;
		
		// Volume shift
	    chanInitVolShift = 0;
	    chanVolShift = 4;
	}

	@Override
	void chanOn() {
		Main.mmu.ram[0xff26] |= 0x4;
	}

	@Override
	void chanOff() {
		Main.mmu.ram[0xff26] &= 0xfb;
	}

	@Override
	boolean chanEnabled() {
		return BitOperations.testBit(Main.mmu.ram[0xff26], 2);
	}

	@Override
    void tickLength() {
		chanLength--;
        int lenght = Main.mmu.ram[0xff1b] & 0xff;
        lenght++;
        Main.mmu.ram[0xff1b] = (byte) (lenght & 0xff);
    }

}
