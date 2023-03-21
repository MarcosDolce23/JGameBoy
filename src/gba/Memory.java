package gba;

public class Memory {
	
	public static Memory instance;
	
	public static Memory getInstance() {
		return instance;
	}
	
	public byte[] ram;
	
	// Return an int representation of the value of the memory at the give index
	public int getByte(int index) {
		return ram[index] & 0xFF;
	}
	
	public int getSignedByte(int index) {
		return ram[index];
	}
	
	// Set the position index of the memory with the byte representation of value
	public void setByte(int index, int value) {
		ram[index] = (byte) value;
	}
	
	
	private void setBootValues() {
		// Hardware register after boot for DMG / MGB
		ram[0xff00] = (byte) 0xcf;
		ram[0xff02] = (byte) 0x7e;
		ram[0xff04] = (byte) 0xab;
		ram[0xff07] = (byte) 0xf8;
		ram[0xff0f] = (byte) 0xe1;
		ram[0xff10] = (byte) 0x80;
		ram[0xff11] = (byte) 0xbf;
		ram[0xff12] = (byte) 0xf3;
		ram[0xff13] = (byte) 0xff;
		ram[0xff14] = (byte) 0xbf;
		ram[0xff16] = (byte) 0x3f;
		ram[0xff18] = (byte) 0xff;
		ram[0xff19] = (byte) 0xbf;
		ram[0xff1a] = (byte) 0x7f;
		ram[0xff1b] = (byte) 0xff;
		ram[0xff1c] = (byte) 0x9f;
		ram[0xff1d] = (byte) 0xff;
		ram[0xff1e] = (byte) 0xbf;
		ram[0xff20] = (byte) 0xff;
		ram[0xff23] = (byte) 0xbf;
		ram[0xff24] = (byte) 0x77;
		ram[0xff25] = (byte) 0xf3;
		ram[0xff26] = (byte) 0xf1;
		ram[0xff40] = (byte) 0x91;
		ram[0xff41] = (byte) 0x85;
		ram[0xff46] = (byte) 0xff;
		ram[0xff47] = (byte) 0xfc;
		ram[0xff4d] = (byte) 0xff;
		ram[0xff4f] = (byte) 0xff;
		ram[0xff51] = (byte) 0xff;
		ram[0xff52] = (byte) 0xff;
		ram[0xff53] = (byte) 0xff;
		ram[0xff54] = (byte) 0xff;
		ram[0xff55] = (byte) 0xff;
		ram[0xff56] = (byte) 0xff;
		ram[0xff68] = (byte) 0xff;
		ram[0xff69] = (byte) 0xff;
		ram[0xff6a] = (byte) 0xff;
		ram[0xff6b] = (byte) 0xff;
		ram[0xff70] = (byte) 0xff;
	}
	
	public Memory() {
		ram = new byte[0xFFFF + 1]; // No estoy seguro del " + 1" lo agregué porque si escribo en el registro FFFF me da error ya que llega hasta el FFFE. Necesito clarificar este asunto
		setBootValues();
		instance = this;
	}
}
