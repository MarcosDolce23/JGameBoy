package gb.apu;

public abstract class Channel {
	
    public boolean chanOn = false;
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
    
    protected void chanDisable() {
        chanOn = false;
        chanEnvVol = 0; // Mute
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
    
    abstract void chanUpdateFreq(int cycles);
    abstract void chanTrigger();
    abstract float getSample();

}
