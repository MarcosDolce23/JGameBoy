package gb.apu;

import gb.Main;
import gb.utils.BitOperations;

public class AudioProcessingUnit {
    private static AudioProcessingUnit instance;

    // Sound buffer configuration
    private float[] bufferQueues = new float[AudioOutput.BUFFER_SIZE];
    private static byte[] buffer = new byte[AudioOutput.BUFFER_SIZE];

    // Buffer queue
    private int bufferInd = 0;

    // =============== // Sound Controller //
    private int soundClocks = 0;
    private int soundInterval = Main.cpu.cyclesPerSec / 512; // 8192 cycles

    private int bufferClocks = 0;
    private int bufferInterval = (int) Math.ceil(Main.cpu.cyclesPerSec / AudioOutput.SAMPLE_RATE);

    private boolean lengthStep = false;
    public boolean soundOn = false;

    // Duty patterns
    private int[][] duty = {
            { -1, -1, -1, -1, -1, -1, -1, 1 }, // 12.5%
            { 1, -1, -1, -1, -1, -1, -1, 1 }, // 25%
            { 1, -1, -1, -1, -1, 1, 1, 1 }, // 50%
            { -1, 1, 1, 1, 1, 1, 1, -1 } // 75%
    };

    // ======== Channel 1 ========

    public boolean chan1On = false;

    // Frequency and settings
    public boolean chan1CounterSelect = false;
    public int chan1InitFreq = 0;
    public int chan1RawFreq = 0;

    // Frequency timer
    private int chan1FreqTimer = 0;

    // Length and pattern duty
    public int chan1Length = 0;
    public int chan1PatternDuty = 0;
    private int chan1DutyStep = 0;

    // Sweep register
    public boolean chan1SweepOn = false;
    public boolean chan1SweepDec = false;
    public int chan1SweepTime = 0;
    public int chan1SweepNum = 0;
    private int chan1SweepClocks = 0;

    // Channel envelope
    public boolean chan1EnvOn = false;
    public boolean chan1EnvInc = false;
    public float chan1EnvInit = 0;
    public int chan1EnvSweep = 0;
    public int chan1EnvClocks = 0;
    public float chan1EnvVol = 0;
    public float chan1EnvInterval = 0;

    // ======== End Channel 1 ========

    // ======== Channel 2 ========

    public boolean chan2On = false;

    // Frequency and settings
    public boolean chan2CounterSelect = false;
    public int chan2RawFreq = 0;

    // Frequency timer
    private int chan2FreqTimer = 0;

    // Length and pattern duty
    public int chan2Length = 0;
    public int chan2PatternDuty = 0;
    private int chan2DutyStep = 0;

    // Channel envelope
    public boolean chan2EnvOn = false;
    public boolean chan2EnvInc = false;
    public float chan2EnvInit = 0;
    public int chan2EnvSweep = 0;
    public int chan2EnvClocks = 0;
    public float chan2EnvVol = 0;
    public float chan2EnvInterval = 0;

    // ======== End Channel 2 ========

    // ======== Channel 3 ========

    public boolean chan3On = false;

    // Frequency and settings
    public boolean chan3CounterSelect = false;
    public int chan3RawFreq = 0;

    // Frequency timer
    private int chan3FreqTimer = 0;

    // Sample step
    public int chan3SampleStep = 0;

    // Volume shift
    public int chan3InitVolShift = 0;
    public int chan3VolShift = 4;

    // Playback enable
    public boolean chan3Playback = false;

    // Length
    public int chan3Length = 0;

    // ======== End Channel 3 ========

    // ======== Channel 4 ========

    public boolean chan4On = false;

    public int LFSR = 0x7fff;
    public boolean LFSRMode = false;

    private int nextBitLFSR() {
        boolean x = ((LFSR & 1) ^ ((LFSR & 2) >> 1)) != 0;
        LFSR = LFSR >> 1;
        LFSR = LFSR | (x ? (1 << 14) : 0);
        if (LFSRMode) {
            LFSR = LFSR | (x ? (1 << 6) : 0);
        }
        if ((1 & ~LFSR) == 0)
        	return -1;
        return 1;
    }

    public int chan4InitFreq = 0;
    public int chan4RawFreq = 0;
    public int chan4Freq = 0;

    // Frequency timer
    private int chan4FreqTimer = 0;

    // Length
    public int chan4Length = 0;

    // Frequency and settings
    public boolean chan4CounterSelect = false;
    public int chan4ClockShift = 0;
    public float chan4ClockDivider = 0.5f;

    // Channel envelope
    public boolean chan4EnvOn = false;
    public boolean chan4EnvInc = false;
    public float chan4EnvInit = 0;
    public int chan4EnvSweep = 0;
    public int chan4EnvClocks = 0;
    public float chan4EnvVol = 0;
    public float chan4EnvInterval = 0;

    private int calcSweep() {
        int preFreq = chan1RawFreq;
        int newFreq = preFreq >> chan1SweepNum;

        newFreq = preFreq + (chan1SweepDec ? -newFreq : newFreq);
        return newFreq;
    }

    private boolean soundOn() {
        return BitOperations.testBit(Main.mmu.getByte(0xff26), 7);
    }

    private void chan1Disable() {
        chan1On = false;
        chan1EnvVol = 0; // Mute
    }

    private void chan2Disable() {
        chan2On = false;
        chan2EnvVol = 0; // Mute
    }

    public void chan3Disable() {
        chan3On = false;
        chan3VolShift = 4;
    }

    private void chan4Disable() {
        chan4On = false;
        chan4EnvVol = 0; // Mute
    }

    private void chan1UpdateFreq(int cycles) {
        chan1FreqTimer -= cycles;
        if (chan1FreqTimer <= 0) {
            chan1FreqTimer += (2048 - chan1RawFreq) * 4;

            chan1DutyStep++;
            chan1DutyStep &= 7;
        }
    }

    private void chan2UpdateFreq(int cycles) {
        chan2FreqTimer -= cycles;
        if (chan2FreqTimer <= 0) {
            chan2FreqTimer += (2048 - chan2RawFreq) * 4;

            chan2DutyStep++;
            chan2DutyStep &= 7;
        }
    }

    private void chan3UpdateFreq(int cycles) {
        chan3FreqTimer -= cycles;
        if (chan3FreqTimer <= 0) {
            chan3FreqTimer += (2048 - chan3RawFreq) * 2;

            chan3SampleStep++;
            chan3SampleStep &= 0x1f;
        }
    }

    private void chan4UpdateFreq(int cycles) {
        chan4FreqTimer -= cycles;
        if (chan4FreqTimer <= 0) {
            chan4FreqTimer += chan4RawFreq;

            chan4Freq = nextBitLFSR();
        }
    }

    private void chan1UpdateEnvelope() {
        chan1EnvClocks++;

        if (chan1EnvClocks >= chan1EnvInterval) {
            chan1EnvClocks = 0;
            if (!chan1EnvOn)
                return;

            // Inc
            if (chan1EnvInc) {
                chan1EnvVol += 1 / 15f;
                if (chan1EnvVol > 1)
                    chan1EnvVol = 1;
            }
            // Dec
            else {
                chan1EnvVol -= (1 / 15f);
                if (chan1EnvVol < 0)
                    chan1EnvVol = 0;
            }
        }
    }

    private void chan2UpdateEnvelope() {
        chan2EnvClocks++;

        if (chan2EnvClocks >= chan2EnvInterval) {
            chan2EnvClocks = 0;
            if (!chan2EnvOn)
                return;

            // Inc
            if (chan2EnvInc) {
                chan2EnvVol += 1 / 15f;
                if (chan2EnvVol > 1)
                    chan2EnvVol = 1;
            }
            // Dec
            else {
                chan2EnvVol -= (1 / 15f);
                if (chan2EnvVol < 0)
                    chan2EnvVol = 0;
            }
        }
    }

    private void chan4UpdateEnvelope() {
        chan4EnvClocks++;

        if (chan4EnvClocks >= chan4EnvInterval) {
            chan4EnvClocks = 0;
            if (!chan4EnvOn)
                return;

            // Inc
            if (chan4EnvInc) {
                chan4EnvVol += 1 / 15f;
                if (chan4EnvVol > 1)
                    chan4EnvVol = 1;
            }
            // Dec
            else {
                chan4EnvVol -= (1 / 15f);
                if (chan4EnvVol < 0)
                    chan4EnvVol = 0;
            }
        }
    }

    private void chan1UpdateSweep() {
        if (--chan1SweepClocks > 0)
            return;

        // When clocks reaches 0 ...
        if (chan1SweepTime > 0) {
            chan1SweepClocks = chan1SweepTime;

            int newFreq = calcSweep();

            boolean overflow = newFreq > 2047;
            if (!overflow && (chan1SweepNum > 0)) {
                chan1RawFreq = newFreq;

                if (calcSweep() > 2047)
                    chan1Disable();
            } else if (overflow)
                chan1Disable();
        } else
            chan1SweepClocks = 8;
    }

    private void chan1UpdateLength() {
        if (lengthStep && chan1CounterSelect && (--chan1Length == 0))
            chan1Disable();
    }

    private void chan2UpdateLength() {
        if (lengthStep && chan2CounterSelect && (--chan2Length == 0))
            chan2Disable();
    }

    private void chan3UpdateLength() {
        if (lengthStep && chan3CounterSelect && (--chan3Length == 0))
            chan3Disable();
    }

    private void chan4UpdateLength() {
        if (lengthStep && chan4CounterSelect && (--chan4Length == 0))
            chan4Disable();
    }

    public void chan1Trigger() {
        chan1On = true;

        // Restart envelope
        chan1EnvVol = chan1EnvInit;
        chan1RawFreq = chan1InitFreq;

        // Restart length
        if (chan1Length == 0)
            chan1Length = 64; // Full

        // Restart sweep
        if (chan1SweepTime > 0)
            chan1SweepClocks = chan1SweepTime;
        else
            chan1SweepClocks = 8;

        if ((chan1SweepNum > 0) && (calcSweep() > 2047))
            chan1Disable();
    }

    public void chan2Trigger() {
        chan2On = true;

        // Restart envelope
        chan2EnvVol = chan2EnvInit;

        // Restart length
        if (chan2Length == 0)
            chan2Length = 64; // Full
    }

    public void chan3Trigger() {
        if (!chan3Playback)
            return;

        chan3On = true;

        chan3VolShift = chan3InitVolShift;

        // Restart length
        if (chan3Length == 0)
            chan3Length = 256; // Full
    }

    public void chan4Trigger() {
        chan4On = true;

        // Restart envelope
        chan4EnvVol = chan4EnvInit;
        LFSR = 0x7fff;

        // Restart length
        if (chan4Length == 0)
            chan4Length = 64; // Full
    }

    // Buffer methods
    private boolean stepBuffer() {
        float sample = getSample();
        bufferQueues[bufferInd++] = sample;

        return (bufferInd >= bufferQueues.length); // True when buffer(s) are full !
    }

    private float getSample() {
        // Channel 1
        float sample1 = duty[chan1PatternDuty][chan1DutyStep] * chan1EnvVol;

        // Channel 2
        float sample2 = duty[chan2PatternDuty][chan2DutyStep] * chan2EnvVol;

        // Channel 3
        float sample3;
        if (chan3VolShift == 4)
            sample3 = 0;
        else {
            int aux = 0;
            aux = chan3GetSample();
            aux = (aux + (aux - 0xf)) >> chan3VolShift;
            sample3 = aux / 15f;
        }

        float sample4 = chan4Freq * chan4EnvVol;

        // System.out.println("Sample 1: " + sample1);
        // System.out.println("Sample 2: " + sample2);
        // System.out.println("Sample 3: " + sample3);
        // if (sample4 != 0.0f)
        //     System.out.println("Sample 4: " + sample4);

        // Mix ...
        return (sample1 + sample2 + sample3 + sample4) / 4;
        // return (sample1 + sample2 + sample3) / 3;

    }

    private int chan3GetSample() {
        if ((chan3SampleStep & 1) == 0) {
            return (((Main.mmu.ram[0xff30 + (chan3SampleStep >> 1)] & 0xff) >> 4) & 0xf);
        }
        return (((Main.mmu.ram[0xff30 + (chan3SampleStep >> 1)] & 0xff) & 0xf));
    }

    private void copyBufferQueues() {
        for (int i = 0; i < AudioOutput.BUFFER_SIZE; i++)
            buffer[i] = (byte) (bufferQueues[i] * 127f);
    }

    public void handleSound(int cycles) {
        if (!soundOn())
            return;

        // Frequency Timmers
        chan1UpdateFreq(cycles);
        chan2UpdateFreq(cycles);
        chan3UpdateFreq(cycles);
        chan4UpdateFreq(cycles);

        // Every 512 hz ...
        soundClocks += cycles;
        if (soundClocks >= soundInterval) {
            // Channel 1
            if (chan1On) {
                chan1UpdateEnvelope();
                chan1UpdateSweep();
                chan1UpdateLength();
            }

            // Channel 2
            if (chan2On) {
                chan2UpdateEnvelope();
                chan2UpdateLength();
            }

            // Channel 3
            if (chan3On) {
                chan3UpdateLength();
            }

            // Channel 4
            if (chan4On) {
                chan4UpdateEnvelope();
                chan4UpdateLength();
            }

            lengthStep = !lengthStep;

            soundClocks -= soundInterval;
        }

        // Filling the buffer
        bufferClocks += cycles;
        if (bufferClocks >= bufferInterval) {
            // Step ...
            if (stepBuffer()) {
                bufferInd = 0;

                copyBufferQueues();

                // This action must be asynchronic
                Thread threadPlayBuffer = new Thread(new ThreadPlayBuffer());
                threadPlayBuffer.start();
            }

            bufferClocks -= bufferInterval;
        }
    }

    static class ThreadPlayBuffer implements Runnable {
        @Override
        public void run() {
            AudioOutput audioOutput = AudioOutput.getInstance(buffer);
            audioOutput.playBuffer();
        }
    }

    private AudioProcessingUnit() {
    }

    public static AudioProcessingUnit getInstance() {
        if (instance == null) {
            instance = new AudioProcessingUnit();
        }
        return instance;
    }
}
