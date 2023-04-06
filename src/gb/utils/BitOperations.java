package gb.utils;

public class BitOperations {
	
	public static boolean testBit(byte b, int bit) {
		return ((b & (1 << bit )) == (1 << bit));
	}
	
	public static byte bitReset(byte b, int bit) {
		return (byte) (b & ~(1 << bit));
	}
	
	public static byte bitSet(byte b, int bit) {
		return (byte) (b | (1 << bit));
	}

}