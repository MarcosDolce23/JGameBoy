package gb.apu;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class AudioOutput {
    private static volatile AudioOutput instance;

    // Sound buffer configuration
    public static final int SAMPLE_RATE = 44100;
    public static final int BUFFER_SIZE = 8192;
    private static final AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
    private byte[] buffer = new byte[BUFFER_SIZE];

    private AudioOutput(byte[] buffer) {
        this.buffer = buffer;
    }

    public static AudioOutput getInstance(byte[] buffer) {
        AudioOutput result = instance;
        if (result != null) {
            return result;
        }
        synchronized (AudioOutput.class) {
            if (instance == null) {
                instance = new AudioOutput(buffer);
            }
            return instance;
        }
    }

    public void playBuffer() {
        SourceDataLine line;
        try {
            line = AudioSystem.getSourceDataLine(FORMAT);
            line.open(FORMAT);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

        setVolume(line, 0.25f);

        line.start();
        line.write(buffer, 0, buffer.length);
        line.drain();
        line.stop();
    }

    private void setVolume(SourceDataLine line, float volume) {
        if (volume < 0f || volume > 1f)
            throw new IllegalArgumentException("Volume not valid: " + volume);
        FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }
}
