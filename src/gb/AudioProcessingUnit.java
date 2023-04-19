package gb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import gb.utils.BitOperations;

public class AudioProcessingUnit implements Runnable {
	
	// Buffer queue
	private int bufferInd = 0;
//    private byte[] bufferQueues = new byte[8192];
	
    // =============== //   Sound Controller //
    private int soundClocks = 0;
    private int soundInterval = Main.cpu.cyclespersec / 512; // 8192 cycles

    private int bufferClocks = 0;
//    private int bufferInterval = Math.ceil(Main.cpu.cyclespersec / this.buffer.sampleRate);
    private int bufferInterval = 96;

    public boolean soundOn = false;
    
    private boolean lengthStep = false;
    
    // Duty patterns
    private int[][] duty = {
        {-1, -1, -1, -1, -1, -1, -1, 1}, // 12.5%
        {1, -1, -1, -1, -1, -1, -1, 1}, // 25%
        {1, -1, -1, -1, -1, 1, 1, 1}, // 50%
        {-1, 1, 1, 1, 1, 1, 1, -1}  // 75%
    };
	
	// Frequency and settings
    public boolean chan1CounterSelect = false;
    public int chan1InitFreq = 0;
    public int chan1RawFreq = 0;
    
    public boolean chan2CounterSelect = false;
    public int chan2RawFreq = 0;
    
    public boolean chan3CounterSelect = false;
    public int chan3RawFreq = 0;
	
	// Frequency timer
    private int chan1FreqTimer = 0;
    private int chan1DutyStep = 0;
    
    private int chan2FreqTimer = 0;
    private int chan2DutyStep = 0;
    
    private int chan3FreqTimer = 0;
    public int chan3SampleStep = 0;
    
    // Length and pattern duty
    public int chan1PatternDuty = 0;
    public int chan1Length = 0;
    
    public int chan2PatternDuty = 0;
    public int chan2Length = 0;
    
    // Sweep register
    public int chan1SweepTime = 0;
    public boolean chan1SweepDec = false;
    public int chan1SweepNum = 0;

    public boolean chan1SweepOn = false;
    private int chan1SweepClocks = 0;
    
    public float chan1EnvInit = 0;
    public boolean chan1EnvInc = false;
    public int chan1EnvSweep = 0;
    public int chan1EnvClocks = 0;
    
    public float chan1EnvVol = 0; 
    public boolean chan1EnvOn = false;
    public float chan1EnvInterval = 0;
    
    public boolean chan1On = false;
    
    public float chan2EnvInit = 0;
    public boolean chan2EnvInc = false;
    public int chan2EnvSweep = 0;
    public int chan2EnvClocks = 0;

    public float chan2EnvVol = 0;
    public boolean chan2EnvOn = false;
    public float chan2EnvInterval = 0;
    
    public boolean chan2On = false;
    
    public boolean chan3On = false;
    
 // Volume shift
    public int chan3InitVolShift = 0;
    public int chan3VolShift = 0;
    
 // Playback enable
    public boolean chan3Playback = false;
    
 // Length
    public int chan3Length = 0;
	
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
	
    private void chan1UpdateEnvelope() {
        chan1EnvClocks++;

        if (chan1EnvClocks >= chan1EnvInterval) {
            chan1EnvClocks = 0;
            if (!chan1EnvOn)
                return;

            // Inc
            if (chan1EnvInc) {
                chan1EnvVol += 1/15f;
                if (chan1EnvVol > 1)
                    chan1EnvVol = 1;
            }
            // Dec
            else {
                chan1EnvVol -= (1/15f);
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
                chan2EnvVol += 1/15f;
                if (chan2EnvVol > 1)
                    chan2EnvVol = 1;
            }
            // Dec
            else {
                chan2EnvVol -= (1/15f);
                if (chan2EnvVol < 0)
                    chan2EnvVol = 0;
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

                if (calcSweep () > 2047)
                    chan1Disable();
            }
            else if (overflow)
                chan1Disable();
        }
        else
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

        // Mix ...
        return (sample1 + sample2 + sample3) / 3;
    }
    
    private int chan3GetSample() {
    	int aux1 = Main.mmu.ram[0xff30 + (chan3SampleStep >> 1)] & 0xff;
    	int aux2 = (~(chan3SampleStep & 1) * 4);
    	
    	if ((chan3SampleStep & 1) == 0) {
    		return (((Main.mmu.ram[0xff30 + (chan3SampleStep >> 1)] & 0xff) 
            		>> 4)
            		& 0xf);
    	}
    	
        return (((Main.mmu.ram[0xff30 + (chan3SampleStep >> 1)] & 0xff) & 0xf));
    }
    
    
    // test audio
    private static final int SAMPLE_RATE = 44000;

    private static final int BUFFER_SIZE = 8192;

    private static final AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
//    private static final AudioFormat FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, SAMPLE_RATE, 8, 1, 1, SAMPLE_RATE, true);

    private float[] bufferQueues = new float[BUFFER_SIZE];

    private byte[] buffer = new byte[BUFFER_SIZE];

    private void playBuffer() throws UnsupportedAudioFileException, IOException {

        SourceDataLine line;
        try {
            line = AudioSystem.getSourceDataLine(FORMAT);
            line.open(FORMAT);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        line.start();

        line.write(buffer, 0, buffer.length);

        line.drain();
        line.stop();
    }
    
    private void copyBufferQueues() {
        for (int i = 0; i < BUFFER_SIZE; i ++)
            buffer[i] = (byte) (bufferQueues[i] * 127f);
//    	buffer = floatToByte(bufferQueues);
//    	fromBufferToAudioBytes(buffer, bufferQueues);
    }
    
	public void handleSound(int cycles) {
		if (!soundOn())
			return;
		
		// Frequency Timmers
		chan1UpdateFreq(cycles);
		chan2UpdateFreq(cycles);
		chan3UpdateFreq(cycles);
		
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
//                try {
//					playBuffer();
//				} catch (UnsupportedAudioFileException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
                Thread t1 =new Thread(this);   // Using the constructor Thread(Runnable r)  
                t1.start();  
            }

            bufferClocks -= bufferInterval;
        }
	}
	
	private byte[] floatToByte(float[] input) {
//	    byte[] ret = new byte[input.length*4];
//	    for (int x = 0; x < input.length; x++) {
//	        ByteBuffer.wrap(ret, x*4, 4).putFloat(input[x]);
//	    }
//	    return ret;
		
//		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//		DataOutputStream dataStream = new DataOutputStream( byteStream );
//
//		// write each element of your double[] to dataStream
//		for (int i = 0; i < input.length; i++) {
//			try {
//				dataStream.writeFloat(input[i]);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		try {
//			dataStream.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			byteStream.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		byte[] audioBytes = byteStream.toByteArray();
//		return audioBytes;
		
	    byte[] byteArray = new byte[input.length * 4];
	    int byteArrayIndex = 0;
	    for (int i = 0; i < input.length; i++) {
	        int rawInt = Float.floatToRawIntBits(input[i]);
	        byteArray[byteArrayIndex] = (byte) (rawInt >>> 24);
	        byteArray[byteArrayIndex + 1] = (byte) (rawInt >>> 16);
	        byteArray[byteArrayIndex + 2] = (byte) (rawInt >>> 8);
	        byteArray[byteArrayIndex + 3] = (byte) (rawInt);
	        byteArrayIndex += 4;
	    }
	    return byteArray;
	}
	
	public static byte[] fromBufferToAudioBytes(byte[] audioBytes, float[] buffer)
	{
	    for (int i = 0, n = buffer.length; i < n; i++)
	    {
	        buffer[i] *= 32767;
	        audioBytes[i*2] = (byte) buffer[i];
	        audioBytes[i*2 + 1] = (byte)((int)buffer[i] >> 8 );
	    }
	    return audioBytes;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			playBuffer();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}