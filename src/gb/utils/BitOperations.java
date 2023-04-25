package gb.utils;

public class BitOperations {
	
	public static boolean testBit(int b, int bit) {
		return ((b & (1 << bit )) == (1 << bit));
	}
	
	public static int bitReset(int b, int bit) {
		return (b & ~(1 << bit));
	}
	
	public static int bitSet(int b, int bit) {
		return (b | (1 << bit));
	}
	
	public static int rotateLeft(int bt) {
		return ((bt << 1) | (bt >> 7)) & 0xff;
	}
	
	public static int rotateRight(int bt) {
		return ((bt << 7) | (bt >> 1)) & 0xff;
	}

}