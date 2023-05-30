package gb.ppu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JPanel;

import gb.Main;
import gb.utils.BitOperations;

public class PixelProcessingUnit extends JPanel {
	
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//    Screen Elements
	public static int gbwidth = 160; // 160
	public static int gbheight = 144; // 144

	private static PixelProcessingUnit instance;

	private BufferedImage img = new BufferedImage(gbwidth, gbheight, BufferedImage.TYPE_4BYTE_ABGR);
	private byte[] pxMap = new byte[gbwidth * gbheight]; // Used to decide what pixel data was drawn

	//	Scanlines
	private int ppuclocks = 0;
	private boolean statsignal = false;

	// Mode lengths in t-cycles
	private int oamLength = 80;
	private int drawLength = 172;
	private int hblankLength = 204;
	private int scanlineLength = 456;
	
	// The sprites which fit draw conditions are added here
	private ArrayList <Sprite> acceptedSprites = new ArrayList < Sprite > ();
	private Sprite[] spritePool = new Sprite[40]; // An object pool with 40 blank sprites

	private int maxSpritesScan = 10;

	// Scanline positions
	private int lx = 0;
	private int ly = 0;
	  
	private int wilc = 0; // Window internal line counter
	private int subly = 0; // Used to decide which row of tile to draw
	private boolean winOnThisFrame = false;

	private Color[] pallete = {
				Color.WHITE,
				Color.LIGHT_GRAY,
				Color.GRAY,
				Color.BLACK
		};

	// Palletes
	private int getPalShade(int index) {
		if (index == 0)
			return Main.mmu.getByte(0xff47) & 0x03;
		if (index == 1)
			return (Main.mmu.getByte(0xff47) & 0x0c) >> 2;
		if (index == 2)
			return (Main.mmu.getByte(0xff47) & 0x30) >> 4;
		return (Main.mmu.getByte(0xff47) & 0xc0) >> 6;
	}
	
	private int getObjShades(int pallete, int nib) {
		if (pallete == 0 ) {
			if (nib == 0)
				return Main.mmu.getByte(0xff48) & 0x03;
			if (nib == 1)
				return (Main.mmu.getByte(0xff48) & 0x0c) >> 2;
			if (nib == 2)
				return (Main.mmu.getByte(0xff48) & 0x30) >> 4;
			return (Main.mmu.getByte(0xff48) & 0xc0) >> 6;
		}
		
		if (pallete == 1) {
			if (nib == 0)
				return Main.mmu.getByte(0xff49) & 0x03;
			if (nib == 1)
				return (Main.mmu.getByte(0xff49) & 0x0c) >> 2;
			if (nib == 2)
				return (Main.mmu.getByte(0xff49) & 0x30) >> 4;
			return (Main.mmu.getByte(0xff49) & 0xc0) >> 6;
		}
		
		return 0;
	}

	private int wy() {
		return Main.mmu.getByte(0xff4a);
	}
	  
	private int wx() {
		return (Main.mmu.getByte(0xff4b) - 7) & 0xff;
	}
  
	private int scrollY() {
		return Main.mmu.getByte(0xff42);
	}

	private int scrollX() {
		return Main.mmu.getByte(0xff43);
	}

	private int lyc() {
		return Main.mmu.getByte(0xff45);
	}

	private int putPixel(int x, int y, int color) {
		int ind = (y * gbwidth + x);
		Color pal = pallete[color];
		
		img.setRGB(x, y, pal.getRGB());

		return ind;
	}

	// Flag setting methods
	private void setCoincidence() {
		// Set bit 2
		BitOperations.bitSet(Main.mmu.ram[0xff41], 2);
	}

	private void clearCoincidence() {
		// Clear bit 2
		BitOperations.bitReset(Main.mmu.ram[0xff41], 2);
	}

	//    FF40 — LCDC: LCD control
	//    LCDC is the main LCD Control register. Its bits toggle what elements are displayed on the screen, and how.

	//    Bit	Name	Usage notes
	//    7	LCD and PPU enable	0=Off, 1=On
	//    6	Window tile map area	0=9800-9BFF, 1=9C00-9FFF
	//    5	Window enable	0=Off, 1=On
	//    4	BG and Window tile data area	0=8800-97FF, 1=8000-8FFF
	//    3	BG tile map area	0=9800-9BFF, 1=9C00-9FFF
	//    2	OBJ size	0=8x8, 1=8x16
	//    1	OBJ enable	0=Off, 1=On
	//    0	BG and Window enable/priority	0=Off, 1=On

	private boolean bgEnable() {
		return BitOperations.testBit(Main.mmu.getByte(0xff40), 0);
	}

	private boolean spritesEnable() {
		return BitOperations.testBit(Main.mmu.getByte(0xff40), 1);
	}

	private boolean tallSprites() {
		return BitOperations.testBit(Main.mmu.getByte(0xff40), 2);
	}

	private boolean bgTileMap() {
		return BitOperations.testBit(Main.mmu.getByte(0xff40), 3);
	}

	private boolean signedAddressing() {
		return BitOperations.testBit(Main.mmu.getByte(0xff40), 4);
	}

	private boolean windowEnable() {
		return BitOperations.testBit(Main.mmu.getByte(0xff40), 5);
	}

	private boolean windowTileMap() {
		return BitOperations.testBit(Main.mmu.getByte(0xff40), 6);
	}

	public boolean lcdEnable() {
		return BitOperations.testBit(Main.mmu.getByte(0xff40), 7);
	}

	//    FF41 — STAT: LCD status
	//    Bit 6 - LYC=LY STAT Interrupt source         (1=Enable) (Read/Write)
	//    Bit 5 - Mode 2 OAM STAT Interrupt source     (1=Enable) (Read/Write)
	//    Bit 4 - Mode 1 VBlank STAT Interrupt source  (1=Enable) (Read/Write)
	//    Bit 3 - Mode 0 HBlank STAT Interrupt source  (1=Enable) (Read/Write)
	//    Bit 2 - LYC=LY Flag                          (0=Different, 1=Equal) (Read Only)
	//    Bit 1-0 - Mode Flag                          (Mode 0-3, see below) (Read Only)
	//              0: HBlank
	//              1: VBlank
	//              2: Searching OAM
	//              3: Transferring Data to LCD Controller
	//    The two lower STAT bits show the current status of the PPU.
	//
	//    Bit 2 is set when LY contains the same value as LYC. It is constantly updated.
	//
	//    Bits 3-6 select which sources are used for the STAT interrupt.

	private boolean coincidence() {
		return BitOperations.testBit(Main.mmu.getByte(0xff41), 2);
	}
	
	private boolean mode0IrqOn() {
		return BitOperations.testBit(Main.mmu.getByte(0xff41), 3);
	}
	
	private boolean mode1IrqOn() {
		return BitOperations.testBit(Main.mmu.getByte(0xff41), 4);
	}
	
	private boolean mode2IrqOn() {
		return BitOperations.testBit(Main.mmu.getByte(0xff41), 5);
	}
	
	private boolean coinIrqOn() {
		return BitOperations.testBit(Main.mmu.getByte(0xff41), 6);
	}

	private int mode() {
		return (Main.mmu.getByte(0xff41) & 0x03);
	}
  
	// LCD enable methods
	public void turnLcdOff() {
		ppuclocks = 0;
		statsignal = false;
		Main.mmu.ram[0xff44] = 0;

		writeMode(0); // When LCD disabled, stat mode is 0

		img.flush();
		renderImage();
	}

	public void turnLcdOn() {
		// Reset LY (and WILC) to 0
		ly = 0;
		wilc = 0;
		winOnThisFrame = false;
		checkCoincidence();
      
		writeMode(2); // When LCD enabled again, mode 2
		img.flush();
	}
  
	private void writeMode(int mode) {
		// Write mode to bits 1 - 0
		Main.mmu.ram[0xff41] &= 0xfc; // Clear last 2 bits, ready for setting
		Main.mmu.ram[0xff41] |= mode; // Write mode to last 2 bits
		updateStatSignal();
	}

	// Update signal state
	public void updateStatSignal() {
		boolean presignal = statsignal;

		statsignal = 
    		(coinIrqOn() && coincidence()) 
    		|| ((mode0IrqOn() && (mode() == 0)))
    		|| ((mode1IrqOn() && (mode() == 1)))
    		|| ((mode2IrqOn() && (mode() == 2)));

		if (!presignal && statsignal)
			BitOperations.bitSet(Main.mmu.ram[0xff0f], 1);
	}

	private void searchOam() {
		acceptedSprites.clear(); // Clear buffer
    
		for (var i = 0; i < spritePool.length; i++)
			spritePool[i] = new Sprite();
    
		// Load the spritePool with Sprite attributes from the oam
		for (int i = 0xfe00; i < 0xfea0; i += 4 ) {
			Sprite bs = new Sprite();
			bs.setY(Main.mmu.getByte(i));
			bs.setX(Main.mmu.getByte(i + 1));
			bs.setTile(Main.mmu.getByte(i + 2));
			bs.setFlags(Main.mmu.getByte(i + 3));
			spritePool[(i - 0xfe00) / 4] = bs;
		}
    
		// Search sprite pool
		for (int i = 0; i < spritePool.length; i++) {
			Sprite sprite = spritePool[i];

			int ly = this.ly + 16;
			int spriteHeight = tallSprites() ? 16 : 8;

			if ((sprite.x > 0) && (ly >= sprite.y) && (ly < sprite.y + spriteHeight)) {
				acceptedSprites.add(sprite);
				int accepted = acceptedSprites.size();

				if (accepted == maxSpritesScan)
					break;
			}
      
		}
    
		Collections.sort(acceptedSprites, (a, b) -> { if (b.x == a.x) return -1; return b.x - a.x;});
	}

	public void checkCoincidence() {
		if (ly == lyc())
			setCoincidence();
		else
			clearCoincidence();
	}
  
	public void handleScan(int cycled) {
		// Do nothing if LCD is off
		if (!lcdEnable())
			return;
	    
		ppuclocks += cycled;
	  
		switch (mode()) {
	   		// ---- OAM MODE 2 ---- //
	  		case 2:
	  			if (ppuclocks >= oamLength) {
	  				// Mode 2 is over ...
	  				writeMode(3);
	  				searchOam();
	  				ppuclocks -= oamLength;
	  			}
	  			break;
	  		// ---- DRAW MODE 3 ---- //
	  		case 3:
	  			if (ppuclocks >= drawLength) {
	  				// Mode 3 is over ...
	  				writeMode(0);
	  				renderScan();
	  				ppuclocks -= drawLength;
	  			}
	  			break;
	  		// ---- H-BLANK MODE 0 ---- //
	  		case 0:
	  			if (ppuclocks >= hblankLength) {
	  				// Advance LY
	  				ly++;
	  				checkCoincidence();
	  				Main.mmu.ram[0xff44] = (byte) ly;
	    		
	  				// When entering vblank period ...
	  				if (ly == gbheight) {
	  					Main.mmu.ram[0xff0f] |= 0x01; // Request interrupt
	  					writeMode(1);
	  					renderImage(); // Draw picture
	  				} else
	  					writeMode(2); // Reset

	  				ppuclocks -= hblankLength;
	  			}
	  			break;
	  		// ---- V-BLANK MODE 1 ---- //
	  		case 1:
	  			if (ppuclocks >= scanlineLength) {
	  				// Advance LY
	  				ly++;
	    		
	  				// Check if out of vblank period ..
	  				if (ly > 153) {
	  					ly = 0;
	  					wilc = 0;
	  					winOnThisFrame = false;
	  					checkCoincidence();
	  					writeMode(2); // Reset
	  				} else {
	  					checkCoincidence();
	  					updateStatSignal();
	  				}
	  				Main.mmu.ram[0xff44] = (byte) ly;
	  				ppuclocks -= scanlineLength;
	  			}
	  			break;
	    	}
	}
  
	private void renderScan() {
		// Initialize values
		lx = 0;

		int x = scrollX();
		int y = (ly + scrollY()) & 0xff;
   
		int wx = 0;

		subly = y & 7;
		int subwy = (wilc & 7) << 1; // (wilc % 8 * 2) 2 bits per pixel

		// Calculate tile data and map bases
		int tiledatabase = signedAddressing() ? 0x8000 : 0x9000;

		int bgmapbase = bgTileMap() ? 0x9c00 : 0x9800;
		int winmapbase = windowTileMap() ? 0x9c00 : 0x9800;

		int mapindy = bgmapbase + (y >> 3) * 32; // (y / 8 * 32) Beginning of background tile map
		int winindy = winmapbase + ((wilc >> 3) * 32);
    
		if (!winOnThisFrame)
			winOnThisFrame = (ly == wy());
    
		boolean inWindowRn = windowEnable() && winOnThisFrame && (wx() < gbwidth);

		while (lx < gbwidth) {
			// ----- WINDOW ----- //
			if (inWindowRn && (lx >= wx())) {
				int mapind = winindy + (wx >> 3); // (x / 8) Background tile map
				int patind = Main.mmu.getByte(mapind); // Get tile index at map

				// Calculate tile data address
				if (!signedAddressing())
					patind = patind << 24 >> 24;
        	
				int addr = tiledatabase + (patind << 4) + (subwy); // (tile index * 16) Each tile is 16 bytes, (sub_ly * 2) Each line of a tile is 2 bytes

				// Get tile line data
				int lobyte = Main.mmu.getByte(addr++);
				int hibyte = Main.mmu.getByte(addr);

				// Mix and draw current tile line pixel
				int bitmask = 1 << ((wx ^ 7) & 7);
				int nib = ((hibyte & bitmask) != 0 ? 2 : 0) | ((lobyte & bitmask) != 0 ? 1 : 0);
        
				int pxind = putPixel(lx, ly, getPalShade(nib));

				pxMap[pxind] = (byte) nib;

				wx++;

			}

			// ----- BACKGROUND ----- //
			else if (bgEnable()) {
				int mapind = mapindy + (x >> 3); // (x / 8) Background tile map
				int patind = Main.mmu.getByte(mapind); // Get tile index at map

				// Calculate tile data address
				if (!signedAddressing())
					patind = patind << 24 >> 24; // Complement tile index in 0x8800 mode

				int addr = tiledatabase + (patind << 4) + (subly << 1); // (tile index * 16) Each tile is 16 bytes, (sub_ly * 2) Each line of a tile is 2 bytes

				// Get tile line data
				int lobyte = Main.mmu.getByte(addr++);
				int hibyte = Main.mmu.getByte(addr);

				// Mix and draw current tile line pixel
				int bitmask = 1 << ((x ^ 7) & 7);
				int nib = ((hibyte & bitmask) != 0 ? 2 : 0) | ((lobyte & bitmask) != 0 ? 1 : 0);
        
				int pxind = putPixel(lx, ly, getPalShade(nib));

				pxMap[pxind] = (byte) nib;

				x++;
				x &= 0xff;

			} else {
				int pxind = putPixel(lx, ly, 0);
				pxMap[pxind] = 0;

			}

			lx++;
		}

		// For every scan we draw a window, inc WILC
		if (inWindowRn)
			wilc++;

		// ----- SPRITES ----- //
		if (spritesEnable()) {

			for (int i = 0; i < acceptedSprites.size(); i++) {
				Sprite sprite = acceptedSprites.get(i);

				int row = ly + 16 - sprite.y;

				int realY = sprite.y - 16 + row;
				int realX = sprite.x - 8;

				// Don't draw offscreen sprites
				if (realY >= gbheight)
					continue;

				int tile = sprite.tile;
				int height;

				// If dubby sprites on set tall tile lsb to 0
				if (tallSprites()) {
					tile &= 0xfe;
					height = 15;
				} else
					height = 7;

				// Calculate address
				int addr = (tile << 4) + (sprite.yflip ? ((row ^ height) & height) << 1 : row << 1);
				addr += 0x8000;
        
				int pxindy = realY * gbwidth;

				// Get tile data
				int lobyte = Main.mmu.getByte(addr++);
				int hibyte = Main.mmu.getByte(addr);

				// Mix and draw all 8 pixels
				for (int ii = 0; ii < 8; ii++) {
					// Check for horizontal flip
					int bitmask = sprite.xflip ? 1 << (ii & 7) : 1 << ((ii ^ 7) & 7);

					// Get pixel data
					int nib = ((hibyte & bitmask) != 0 ? 2 : 0) | ((lobyte & bitmask) != 0 ? 1 : 0);

					var sx = realX + ii;
					if (
							nib == 0 // pixels are transparent
							// Don't draw offscreen pixels
							|| sx >= gbwidth 
							|| sx < 0 
							|| (sprite.behind && (pxMap[pxindy + sx] > 0))
							)
						continue;

					// Mix and draw !
					int px = getObjShades(sprite.pallete, nib);
					putPixel(sx, realY, px);

				}
			}
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, 0, getWidth(), getHeight(), this);

	}

	private void renderImage() {
		repaint();
	}
	
	private PixelProcessingUnit() {
		setDoubleBuffered(true);
	}
	
	public static PixelProcessingUnit getInstance() {
		if (instance == null) {
			instance = new PixelProcessingUnit();
		}
		return instance;
	}
	
}