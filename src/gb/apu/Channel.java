package gb.apu;

public abstract class Channel {
	
    public static boolean lengthStep = false;

    // Frequency and settings
    public boolean chanCounterSelect = false;
    public int chanRawFreq = 0;

    public int chanFreqTimer = 0;
    public int chanLength = 0;

    // Channel envelope
    public boolean chanEnvOn = false;
    public boolean chanEnvInc = false;
    public float chanEnvInit = 0;
    public float chanEnvVol = 0;
    public float chanEnvInterval = 0;
    public int chanEnvSweep = 0;
    public int chanEnvClocks = 0;
    
 // Duty patterns
    protected int[][] duty = {
            { -1, -1, -1, -1, -1, -1, -1, 1 }, // 12.5%
            { 1, -1, -1, -1, -1, -1, -1, 1 }, // 25%
            { 1, -1, -1, -1, -1, 1, 1, 1 }, // 50%
            { -1, 1, 1, 1, 1, 1, 1, -1 } // 75%
    };
    
    public void chanDisable() {
        chanOff();
        lengthStep = false;

        // Frequency and settings
        chanCounterSelect = false;
        chanRawFreq = 0;

        chanFreqTimer = 0;
        chanLength = 0;

        // Channel envelope
        chanEnvOn = false;
        chanEnvInc = false;
        chanEnvInit = 0;
        chanEnvVol = 0;
        chanEnvInterval = 0;
        chanEnvSweep = 0;
        chanEnvClocks = 0;
        resetChan();
    }
    
    public void chanUpdateEnvelope() {
        chanEnvClocks++;

        if (chanEnvClocks >= chanEnvInterval) {
            chanEnvClocks = 0;
            if (!chanEnvOn)
                return;

            // Inc
            if (chanEnvInc) {
                chanEnvVol += 1 / 15f;
                if (chanEnvVol > 1)
                    chanEnvVol = 1;
            }
            // Dec
            else {
                chanEnvVol -= (1 / 15f);
                if (chanEnvVol < 0)
                    chanEnvVol = 0;
            }
        }
    }
    
    public void chanUpdateLength() {
        if (lengthStep && chanCounterSelect && (--chanLength == 0))
            chanDisable();
    }
    
    abstract void chanOn();
    abstract void chanOff();
    abstract boolean chanEnabled();
    abstract void resetChan();
    abstract void chanUpdateFreq(int cycles);
    public abstract void chanTrigger();
    abstract float getSample();
    public abstract void NRX0(int value);
    public abstract void NRX1(int value);
    public abstract void NRX2(int value);
    public abstract void NRX3(int value);
    public abstract void NRX4(int value);
}
