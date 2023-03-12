package gba;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File rom = new File("C:/Users/marco/Downloads/Tetris (Japan) (En)/Tetris (Japan) (En).gb");
		FileInputStream fl = new FileInputStream(rom);
				
//		int a = 0x00AB;
//		System.out.println("Low: " + (a & 0xFF)); // Leer low 8 bits
//		System.out.println("High: " + ((a >>8) & 0xff)); // Leer high 8 bits
		
//		byte[] array = new byte[(int)rom.length()];
		
		fl.read(Ram.ram);
		
		fl.close();
		
		
		
		Cpu.B = 0xff;
		
		System.out.println(Cpu.B);

		System.out.println(Cpu.B & (1 << 7));
		
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
