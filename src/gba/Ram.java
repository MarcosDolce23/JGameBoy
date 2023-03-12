package gba;

public class Ram {
	public static byte[] ram = new byte[0xFFFF + 1]; // No estoy seguro del " + 1" lo agregué porque si escribo en el registro FFFF me da error ya que llega hasta el FFFE. Necesito clarificar este asunto
	
	// Return an int representation of the value of the memory at the give index
	public static int getByte(int index) {
		return ram[index] & 0xFF;
	}
	
	// Set the position index of the memory with the byte representation of value
	public static void setByte(int index, int value) {
		ram[index] = (byte) value;
	}
}
