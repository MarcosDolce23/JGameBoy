package gba;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		File rom = new File("C:/Users/marco/Downloads/Tetris (Japan) (En)/Tetris (Japan) (En).gb");
//		File rom = new File("C:/Users/marco/Downloads/Tetris (Japan) (En)/cpu_instrs.gb"); // Failed
//		File rom = new File("C:/Users/marco/Downloads/Tetris (Japan) (En)/01-special.gb"); // Passed
		File rom = new File("C:/Users/marco/Downloads/Tetris (Japan) (En)/02-interrupts.gb"); // Passed
//		File rom = new File("C:/Users/marco/Downloads/Tetris (Japan) (En)/03-op sp,hl.gb"); // Passed
//		File rom = new File("C:/Users/marco/Downloads/Tetris (Japan) (En)/04-op r,imm.gb"); // Passed
//		File rom = new File("C:/Users/marco/Downloads/Tetris (Japan) (En)/05-op rp.gb"); // Passed
//		File rom = new File("C:/Users/marco/Downloads/Tetris (Japan) (En)/06-ld r,r.gb"); // Passed
//		File rom = new File("C:/Users/marco/Downloads/Tetris (Japan) (En)/07-jr,jp,call,ret,rst.gb"); // Passed
//		File rom = new File("C:/Users/marco/Downloads/Tetris (Japan) (En)/08-misc instrs.gb"); // Passed
//		File rom = new File("C:/Users/marco/Downloads/Tetris (Japan) (En)/09-op r,r.gb"); // Passed
//		File rom = new File("C:/Users/marco/Downloads/Tetris (Japan) (En)/10-bit ops.gb"); // Passed
//		File rom = new File("C:/Users/marco/Downloads/Tetris (Japan) (En)/11-op a,(hl).gb"); // Passed

		
		FileInputStream fl = new FileInputStream(rom);
		
		Memory mem = new Memory();
				
//		int a = 0x00AB;
//		System.out.println("Low: " + (a & 0xFF)); // Leer low 8 bits
//		System.out.println("High: " + ((a >>8) & 0xff)); // Leer high 8 bits
		
//		byte[] array = new byte[(int)rom.length()];
		
		fl.read(mem.ram);
		
		fl.close();
		
		
		Cpu cpu = new Cpu();
		
		cpu.loop();
		
//		System.out.println(String.format("0x%04X", Cpu.getHL()));
//		System.out.println(String.format("0x%02X", Ram.getByte(10)));
//		
//		InstructionSet.LD_HLdec_A();
//		
//		System.out.println(Cpu.getHL());
//		System.out.println(String.format("0x%04X", Cpu.getHL()));
		
//		System.out.println(Arrays.toString(Ram.ram));
	}

}
