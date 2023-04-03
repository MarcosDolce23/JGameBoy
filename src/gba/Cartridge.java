package gba;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Cartridge {
	
	public String title;
	public int SCBFlag;
	public static int cartridgeType;
	public int ROMSize;
	public int RAMSize;
	public int destinationCode;
	public int oldLicenseeCode;
	public int maskROMVersionNumber;
	
	private Memory mem;
	
	private static Cartridge instance;
	
	public void load(String src) throws IOException {
		File rom = new File(src);
		FileInputStream fl = new FileInputStream(rom);
		mem = Memory.getInstance();

		fl.read(mem.ram);
		fl.close();
		
		title = getTitle();
		SCBFlag = mem.getByte(0x0146);
		cartridgeType = mem.getByte(0x0147);
		ROMSize = mem.getByte(0x0148);
		RAMSize = mem.getByte(0x0149);
		destinationCode = mem.getByte(0x014a);
		oldLicenseeCode = mem.getByte(0x014b);
		maskROMVersionNumber = mem.getByte(0x014c);
		
	}
	
	private String getTitle() {
		String title = "";
		for (int i = 0; i < 16; i++) {
			title = title + String.valueOf((char) mem.getByte(0x0134 + i));
		}
		return title;
	}
	
	public Cartridge() {
		instance = this;
	}
	
	public static Cartridge getInstance() {
		return instance;
	}
	
}
