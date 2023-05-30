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
	
	/* I made these classes as a Singleton although it is not necessary since I'm accessing them in a static way.
	 * I'm trying to learn good practices and design patterns and this is my first approach.
	 * It may change in the future.
	 */
	public static CentralProcessingUnit cpu = CentralProcessingUnit.getInstance();
	public static MemoryManagementUnit mmu = MemoryManagementUnit.getInstance();
	public static PixelProcessingUnit ppu = PixelProcessingUnit.getInstance();
	public static AudioProcessingUnit apu = AudioProcessingUnit.getInstance();
	public static Cartridge cartridge = Cartridge.getInstance();
	public static Joypad joypad = Joypad.getInstance();
	
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
