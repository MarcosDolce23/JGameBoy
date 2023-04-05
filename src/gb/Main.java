package gb;

import java.io.IOException;

public class Main {
	
	public static int fps = 60;
	public static Cpu cpu = new Cpu();
	public static MemoryManagementUnit mmu = new MemoryManagementUnit();
	public static PixelProcessingUnit ppu = new PixelProcessingUnit();
	public static Cartridge cartridge = new Cartridge();
	public static Joypad joypad = new Joypad();
	private static boolean waitingLoop = true;
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		new UserInterface();
		
		while (waitingLoop) {
			System.out.println("Waiting ROM...");
		}
		
		cpu.loopExe(0);
	}
	
    public static void setFPS() {
        cpu.cyclesperframe = cpu.cyclespersec / fps;
        cpu.interval = 1000 / fps;
    }
	
	public static void start() {
		setFPS();
		waitingLoop = false;
	}

}
