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

    public boolean soundOn = false;

    // Instantiate channels 
    public Channel1 ch1 = new Channel1();
    public Channel2 ch2 = new Channel2();
    public Channel3 ch3 = new Channel3();
    public Channel4 ch4 = new Channel4();
    
    private boolean soundOn() {
        return BitOperations.testBit(Main.mmu.getByte(0xff26), 7);
    }

    // Buffer methods
    private boolean stepBuffer() {
        float sample = mixSamples();
        bufferQueues[bufferInd++] = sample;

        return (bufferInd >= bufferQueues.length); // True when buffer(s) are full !
    }

    private float mixSamples() {
        return (ch1.getSample() + ch2.getSample() + ch3.getSample() + ch4.getSample()) / 4;
    }

    private void copyBufferQueues() {
        for (int i = 0; i < AudioOutput.BUFFER_SIZE; i++)
            buffer[i] = (byte) (bufferQueues[i] * 127f);
    }

    public void handleSound(int cycles) {
        if (!soundOn())
            return;

        // Frequency Timmers
        ch1.chanUpdateFreq(cycles);
        ch2.chanUpdateFreq(cycles);
        ch3.chanUpdateFreq(cycles);
        ch4.chanUpdateFreq(cycles);

        // Every 512 hz ...
        soundClocks += cycles;
        if (soundClocks >= soundInterval) {
            ch1.chanUpdateLength();
            ch2.chanUpdateLength();
            ch3.chanUpdateLength();
            ch4.chanUpdateLength();
            
            // Channel 1
            if (ch1.chanEnabled()) {
                ch1.chanUpdateEnvelope();
                ch1.chanUpdateSweep();
            }

            // Channel 2
            if (ch2.chanEnabled()) {
                ch2.chanUpdateEnvelope();
            }

            // Channel 3
            if (ch3.chanEnabled()) {
            }

            // Channel 4
            if (ch4.chanEnabled()) {
                ch4.chanUpdateEnvelope();
            }

            Channel.lengthStep = !Channel.lengthStep;

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
