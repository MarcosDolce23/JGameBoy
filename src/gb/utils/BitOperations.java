package gb.utils;

public class BitOperations {
	
	public static boolean testBit(int b, int bit) {
		return ((b & (1 << bit )) == (1 << bit));
	}
	
	public static byte bitReset(int b, int bit) {
		return (byte) (b & ~(1 << bit));
	}
	
	public static byte bitSet(int b, int bit) {
		return (byte) (b | (1 << bit));
	}

}