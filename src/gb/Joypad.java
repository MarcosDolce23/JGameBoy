package gb;

import gb.utils.*;

public class Joypad {
	
//	FF00 — P1/JOYP: Joypad
//	The eight Game Boy action/direction buttons are arranged as a 2x4 matrix. Select either action or direction buttons by writing to this register, then read out the bits 0-3.
//
//	Bit 7 - Not used
//	Bit 6 - Not used
//	Bit 5 - P15 Select Action buttons    (0=Select)
//	Bit 4 - P14 Select Direction buttons (0=Select)
//	Bit 3 - P13 Input: Down  or Start    (0=Pressed) (Read Only)
//	Bit 2 - P12 Input: Up    or Select   (0=Pressed) (Read Only)
//	Bit 1 - P11 Input: Left  or B        (0=Pressed) (Read Only)
//	Bit 0 - P10 Input: Right or A        (0=Pressed) (Read Only)
	
	private int key;
	private byte joypadState = (byte) 0xff;

	public void pressButton(int keyCode) {
		switch(keyCode) {
			// Right Button
			case 39:
				key = 0;
			break;
			// Left Button
			case 37:
				key = 1;
			break;
			// Up Button
			case 38:
				key = 2;
			break;
			// Down Button
			case 40:
				key = 3;
			break;
			// A Button
			case 90:
				key = 4;
			break;
			// B Button
			case 88:
				key = 5;
			break;
			// Select Button
			case 16:
				key = 6;
			break;
			// Start Button
			case 10:
				key = 7;
			break;
		}
		
		boolean previouslyUnset = false;
		
		// if setting from 1 to 0 we may have to request an interupt
		   if (BitOperations.testBit(joypadState, key) == false)
		     previouslyUnset = true ;

		   // remember if a keypressed its bit is 0 not 1
		   joypadState = BitOperations.bitReset(joypadState, key);

		   // button pressed
		   boolean button = true;

		   // is this a standard button or a directional button?
		   if (key > 3)
		     button = true;
		   else // directional button pressed
		     button = false;

		   byte keyReq = Main.mmu.ram[0xff00];
		   boolean requestInterupt = false;

		   // only request interupt if the button just pressed is
		   // the style of button the game is interested in
		   if (button && !BitOperations.testBit(keyReq, 5))
		     requestInterupt = true;

		   // same as above but for directional button
		   else if (!button && !BitOperations.testBit(keyReq, 4))
		     requestInterupt = true;

		   // request interupt
		   if (requestInterupt && !previouslyUnset)
		     Main.mmu.ram[0xff0f] |= 0x08;
	}
	
	public byte getJoypadState() {
	   byte res = Main.mmu.ram[0xff00];
	   // flip all the bits
	   res ^= 0xff ;

	   // are we interested in the standard buttons?
	   if (!BitOperations.testBit(res, 4))
	   {
	     byte topJoypad = (byte) (joypadState >> 4);
	     topJoypad |= 0xF0 ; // turn the top 4 bits on
	     res &= topJoypad ; // show what buttons are pressed
	   }
	   else if (!BitOperations.testBit(res,5))//directional buttons
	   {
	     byte bottomJoypad = (byte) (joypadState & 0xf);
	     bottomJoypad |= 0xF0 ;
	     res &= bottomJoypad ;
	   }
	   return res ;
	}
	
	public void releaseButton(int keyCode) {
		switch(keyCode) {
		// Right Button
		case 39:
			key = 0;
		break;
		// Left Button
		case 37:
			key = 1;
		break;
		// Up Button
		case 38:
			key = 2;
		break;
		// Down Button
		case 40:
			key = 3;
		break;
		// A Button
		case 90:
			key = 4;
		break;
		// B Button
		case 88:
			key = 5;
		break;
		// Select Button
		case 16:
			key = 6;
		break;
		// Start Button
		case 10:
			key = 7;
		break;
	}
		joypadState = BitOperations.bitSet(joypadState, key) ;
	}
	
}
