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

    Channel1 ch1 = new Channel1();
    Channel2 ch2 = new Channel2();
    Channel3 ch3 = new Channel3();
    Channel4 ch4 = new Channel4();
    
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
        // Channel 1
        float sample1 = ch1.getSample();

        // Channel 2
        float sample2 = ch2.getSample();

        // Channel 3
        float sample3 = ch3.getSample();

        float sample4 = ch4.getSample();

        // Mix ...
        return (sample1 + sample2 + sample3 + sample4) / 4;

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
            // Channel 1
            if (ch1.chanOn) {
                ch1.chanUpdateEnvelope();
                ch1.chanUpdateSweep();
                ch1.chanUpdateLength();
            }

            // Channel 2
            if (ch2.chanOn) {
                ch2.chanUpdateEnvelope();
                ch2.chanUpdateLength();
            }

            // Channel 3
            if (ch3.chanOn) {
                ch3.chanUpdateLength();
            }

            // Channel 4
            if (ch4.chanOn) {
                ch4.chanUpdateEnvelope();
                ch4.chanUpdateLength();
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
