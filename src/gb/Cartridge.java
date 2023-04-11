package gb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Cartridge {
	
	public String title;
	public int SCBFlag;
	public int cartridgeType;
	public int ROMSize;
	public int RAMSize;
	public int destinationCode;
	public int oldLicenseeCode;
	public int maskROMVersionNumber;
	
	public byte[] cartridgeMemory;
	
	public boolean mbc1;
	public boolean mbc2;
	public boolean mbc3;
	
	public int romBankNumber = 1;
	
	public int rtg = 0;
	
//	Cartridge memory address 0x148 tells how much RAM banks the game has.
//	The maximum is 4. The size of 1 RAM bank is 0x2000 bytes
//	so if we have an array of size 0x8000 this is enough space for all the RAM banks
	public byte[] ramBanks = new byte[0x8000];
	public int ramBankNumber = 0;
	
	public void load(String src) throws IOException {
		File rom = new File(src);
		FileInputStream fl = new FileInputStream(rom);
		
		cartridgeMemory = new byte[(int) rom.length()];
		fl.read(cartridgeMemory);
		
//		fl.read(Main.mmu.ram);
		fl.close();
		
		// Copy cartridge to gameboy rom
		for (int i = 0; i < 0x8000; i++) {
			Main.mmu.ram[i] = cartridgeMemory[i];
		}
		
		title = getTitle();
		SCBFlag = Main.mmu.getByte(0x0146);
		cartridgeType = Main.mmu.getByte(0x0147);
		ROMSize = Main.mmu.getByte(0x0148);
		RAMSize = Main.mmu.getByte(0x0149);
		destinationCode = Main.mmu.getByte(0x014a);
		oldLicenseeCode = Main.mmu.getByte(0x014b);
		maskROMVersionNumber = Main.mmu.getByte(0x014c);
		
		switch (cartridgeType) {
		   case 1 : mbc1 = true; break;
		   case 2 : mbc1 = true; break;
		   case 3 : mbc1 = true; break;
		   case 5 : mbc2 = true; break;
		   case 6 : mbc2 = true; break;
		   case 15: mbc3 = true; break;
		   case 16: mbc3 = true; break;
		   case 17: mbc3 = true; break;
		   case 18: mbc3 = true; break;
		   case 19: mbc3 = true; break;
		   default : break ;
		}
		
	}
	
	private String getTitle() {
		String title = "";
		for (int i = 0; i < 16; i++) {
			title = title + String.valueOf((char) Main.mmu.getByte(0x0134 + i));
		}
		return title;
	}
	
}
