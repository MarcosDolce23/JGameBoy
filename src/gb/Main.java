package gb;

import java.io.IOException;

import gb.apu.AudioProcessingUnit;
import gb.cpu.CentralProcessingUnit;
import gb.mmu.Cartridge;
import gb.mmu.MemoryManagementUnit;
import gb.ppu.PixelProcessingUnit;
import gb.ui.Joypad;
import gb.ui.UserInterface;

public class Main {
	
	public static int fps = 60;
	public static UserInterface ui;
	public static CentralProcessingUnit cpu = new CentralProcessingUnit();
	public static MemoryManagementUnit mmu = new MemoryManagementUnit();
	public static PixelProcessingUnit ppu = new PixelProcessingUnit();
	public static AudioProcessingUnit apu = new AudioProcessingUnit();
	public static Cartridge cartridge = new Cartridge();
	public static Joypad joypad = new Joypad();
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		ui = new UserInterface();
		
	}
	
    public static void setFPS() {
        cpu.cyclesPerFrame = cpu.cyclesPerSec / fps;
        cpu.interval = 1000 / fps;
    }
	
	public static void start() {
		setFPS();
		try {
			cpu.loopExe(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
