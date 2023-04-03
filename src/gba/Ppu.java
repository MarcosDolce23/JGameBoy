package gba;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JPanel;

public class Ppu extends JPanel {
	
	public static Ppu instance;

  Memory mem = Memory.getInstance();

  Color[] pallete = {
    Color.WHITE,
    Color.LIGHT_GRAY,
    Color.GRAY,
    Color.BLACK
  };

  // Palletes
  private int getPalShade(int index) {
    if (index == 0)
      return mem.getByte(0xff47) & 0x03;
    if (index == 1)
      return (mem.getByte(0xff47) & 0x0c) >> 2;
    if (index == 2)
      return (mem.getByte(0xff47) & 0x30) >> 4;
    return (mem.getByte(0xff47) & 0xc0) >> 6;
  }

  int[][] objshades = {
    {
      3,
      3,
      3
    },
    {
      3,
      3,
      3
    }
  };

  //    Screen Elements

  public static int gbwidth = 160; // 160
  public static int gbheight = 144; // 144

  BufferedImage img = new BufferedImage(gbwidth, gbheight, BufferedImage.TYPE_4BYTE_ABGR);

  byte[] pxMap = new byte[gbwidth * gbheight]; // Used to decide what pixel data was drawn

  long interval = (long)(1000 / 59.7); // GB refresh rate

  // =============== //   Scanlines //

  int ppuclocks = 0;

  boolean statsignal = false;

  // Mode lengths in t-cycles
  int oamlength = 80;
  int drawlength = 172;
  int hblanklength = 204;
  int scanlinelength = 456;

  ArrayList < Sprite > acceptedSprites = new ArrayList < Sprite > (); // The good boys which fit draw conditions go here :)

  int maxSpritesScan = 10;

  // Scanline positions
  int lx = 0;
  int ly = 0;
  
  int wilc = 0; // Window internal line counter
  boolean winOnThisFrame = false;


//  int wx = 0;
  private int wx() {
	  return (mem.getByte(0xff4b) - 7);
  }
  
//  int wy = 0;
  private int wy() {
	  return mem.getByte(0xff4a);
  }
  
  // BG scroll positions
  //    int scrollx = 0;
  //    int scrolly = 0;

  private int scrollY() {
    return mem.getByte(0xff42);
  }

  private int scrollX() {
    return mem.getByte(0xff43);
  }

  int sub_ly = 0; // Used to decide which row of tile to draw

  private int lyc() {
	  return mem.getByte(0xff45);
  }

  private int putPixel(int x, int y, int color) {
    int ind = (y * gbwidth + x);
    //        int pind = ind * 4;

    Color pal = pallete[color];
    img.setRGB(x, y, pal.getRGB());

    return ind; // Don't let it go to waste <3
  };

  // Flag setting methods
  private void setCoincidence() {
	  // Set bit 2
	  mem.ram[0xff41] |= 0x4;
  }

  private void clearCoincidence() {
	  // Clear bit 2
	  mem.ram[0xff41] &= ~0x4;
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
    return ((mem.getByte(0xff40) & 0x01) == 0x01);
  }

  private boolean spritesEnable() {
    return ((mem.getByte(0xff40) & 0x02) == 0x02);
  }

  private boolean tallSprites() {
    return ((mem.getByte(0xff40) & 0x04) == 0x04);
  }

  private boolean bgTileMap() {
    return ((mem.getByte(0xff40) & 0x08) == 0x08);
  }

  private boolean signedAddressing() {
    return ((mem.getByte(0xff40) & 0x10) == 0x10);
  }

  private boolean windowEnable() {
    return ((mem.getByte(0xff40) & 0x20) == 0x20);
  }

  private boolean windowTileMap() {
    return ((mem.getByte(0xff40) & 0x40) == 0x040);
  }

  private boolean lcdEnable() {
    return ((mem.getByte(0xff40) & 0x80) == 0x80);
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

  private boolean coinIrqOn() {
    return ((mem.getByte(0xff41) & 0x40) == 0x40);
  }

  private boolean mode2IrqOn() {
    return ((mem.getByte(0xff41) & 0x20) == 0x20);
  }

  private boolean mode1IrqOn() {
    return ((mem.getByte(0xff41) & 0x10) == 0x10);
  }

  private boolean mode0IrqOn() {
    return ((mem.getByte(0xff41) & 0x08) == 0x08);
  }

  private boolean coincidence() {
    return ((mem.getByte(0xff41) & 0x04) == 0x04);
  }

  private int mode() {
    return (mem.getByte(0xff41) & 0x03);
  }

  public boolean handleScan(int cycled) {
	  
    // Do nothing if LCD is off
    if (!lcdEnable())
      return true;

    ppuclocks += cycled;

    switch (mode()) {

      // ---- OAM MODE 2 ---- //
    case 2:
      if (ppuclocks >= oamlength) {
        // Mode 2 is over ...
        writeMode(3);
        searchOam();

        ppuclocks -= oamlength;
      }
//      break;
      // ---- DRAW MODE 3 ---- //
    case 3:
      // ... we're just imaginary plotting pixels
      if (ppuclocks >= drawlength) {
        // Mode 3 is over ...
        writeMode(0);
        renderScan(); // Finally render on hblank :D

        ppuclocks -= drawlength;
      }
//      break;
      // ---- H-BLANK MODE 0 ---- //
    case 0:
      // We're relaxin here ...
      if (ppuclocks >= hblanklength) {
        // Advance LY
        ly++;

        checkCoincidence();
        mem.ram[0xff44] = (byte) this.ly;

        // When entering vblank period ...
        if (ly == gbheight) {
//          int bt = mem.getByte(0xff0f);
//          mem.setByte(0xff0f, bt | 0x01); // Request vblank irq !
        	mem.ram[0xff0f] |= 0b00000001; // Set bit 0
        	writeMode(1);

          renderImage(); // Draw picture ! (in v-sync uwu)
        } else
          writeMode(2); // Reset

        ppuclocks -= hblanklength;
      }
//      break;
      // ---- V-BLANK MODE 1 ---- //
    case 1:
      if (ppuclocks >= scanlinelength) {
        // Advance LY
        ly++;

        // Check if out of vblank period ..
        if (ly == 154) {
          ly = 0;
          wilc = 0;
          winOnThisFrame = false;

          checkCoincidence();

          writeMode(2); // Reset
        } else {
          checkCoincidence();
          updateStatSignal();
        }

        mem.ram[0xff44] = (byte) ly;

        ppuclocks -= scanlinelength;
      }
//      break;
    }

    return true;
  }

  private void writeMode(int mode) {
    //        stat.mode = mode;
    updateStatSignal();

    // Write mode to bits 1 - 0
    mem.ram[0xff41] &= 0b11111100; // Clear last 2 bits, ready for setting
    mem.ram[0xff41] |= mode; // Write mode to last 2 bits
  }

  // Update signal state
  public void updateStatSignal() {
    boolean presignal = statsignal;

    statsignal = (coinIrqOn() && coincidence()) || (mode2IrqOn() && mode() == 2) || (mode0IrqOn() && mode() == 0) || (mode1IrqOn() && mode() == 1);

    if (!presignal && statsignal) {
      int res = mem.getByte(0xff0f) | 0x02; // Set bit 1
      mem.setByte(0xff0f, res);
    }
  }

  private void searchOam() {
	  Sprite[] spritePool = new Sprite[40]; // An object pool with 40 blank sprites
	  
	  for (var i = 0; i < spritePool.length; i++)
		  spritePool[i] = new Sprite();
	  
	  acceptedSprites.clear(); // Clear buffer
    
    // Load the spritePool with Sprite attributes
	  for (int i = 0xfe00; i < 0xfea0; i += 4 ) {
		  Sprite bs = new Sprite();
		  bs.setY(mem.getByte(i));
		  bs.setX(mem.getByte(i + 1));
		  bs.setTile(mem.getByte(i + 2));
		  bs.setFlags(mem.getByte(i + 3));
		  spritePool[(i - 0xfe00) / 4] = bs;
    }
    
    // Search sprite pool
    for (int i = 0; i < spritePool.length; i++) {

      Sprite sprite = spritePool[i];

      int ly = this.ly + 16;
      int spriteHeight = tallSprites() ? 16 : 8;

      if (
        sprite.x > 0 // X > 0
        &&
        ly >= sprite.y // LY + 16 >= Y
        &&
        ly < sprite.y + spriteHeight // LY + 16 < Y + height
      ) {
        acceptedSprites.add(sprite);
        int accepted = acceptedSprites.size();

        if (accepted == maxSpritesScan)
          break;
      }
      
    }
    
    Collections.sort(acceptedSprites, (a, b) -> b.x - a.x);
  }

  public void checkCoincidence() {
    // Yes !
    if (ly == lyc())
      setCoincidence();
    // No !
    else
      clearCoincidence();
  }

  public void renderScan() {

    // Ready up some stuff
    lx = 0;

    int x = scrollX();
    int y = (ly + scrollY()) & 0xff;
    
//    this.wy = mem.getByte(0xff4a);
//    this.wx = mem.getByte(0xff4b) - 7; // 7px of offset
   
    int wx = 0;

    sub_ly = y & 7;
    int sub_wy = (wilc & 7) << 1; // (wilc % 8 * 2) 2 bits per pixel

    // Calculate tile data and map bases
    int tiledatabase = signedAddressing() ? 0x8000 : 0x9000;

    int bgmapbase = bgTileMap() ? 0x9c00 : 0x9800;
    int winmapbase = windowTileMap() ? 0x9c00 : 0x9800;

    int mapindy = bgmapbase + (y >> 3) * 32; // (y / 8 * 32) Beginning of background tile map
    int winindy = winmapbase + (wilc >> 3) * 32;
    
    winOnThisFrame = (ly != 0) ? true : false;
    boolean inWindowRn = windowEnable() && winOnThisFrame && (wx() < gbwidth);

    while (lx < gbwidth) {
      // ----- WINDOW ----- //
      if (inWindowRn && (lx >= wx())) {

        int mapind = winindy + (wx >> 3); // (x / 8) Background tile map
        int patind = mem.getByte(mapind); // Get tile index at map

        // Calculate tile data address

        if (!signedAddressing())
//          patind = mem.getSignedByte(mapind);
        	patind = patind << 24 >> 24; 

        int addr = tiledatabase + (patind << 4) + (sub_wy); // (tile index * 16) Each tile is 16 bytes, (sub_ly * 2) Each line of a tile is 2 bytes

        // Get tile line data
        int lobyte = mem.getByte(addr++);
        int hibyte = mem.getByte(addr);

        // Mix and draw current tile line pixel
        int bitmask = 1 << ((wx ^ 7) & 7);
        int nib = ((hibyte & bitmask) != 0 ? 2 : 0) | ((lobyte & bitmask) != 0 ? 1 : 0);
        
//        System.out.println("Put: 3");
        int pxind = putPixel(lx, ly, getPalShade(nib));

        pxMap[pxind] = (byte) nib;

        wx++;

      }

      // ----- BACKGROUND ----- //
      else if (bgEnable()) {

        int mapind = mapindy + (x >> 3); // (x / 8) Background tile map
        int patind = mem.getByte(mapind); // Get tile index at map

        // Calculate tile data address

        if (!signedAddressing())
          patind = patind << 24 >> 24; // Complement tile index in 0x8800 mode
//          patind = mem.getSignedByte(mapind);

        int addr = tiledatabase + (patind << 4) + (sub_ly << 1); // (tile index * 16) Each tile is 16 bytes, (sub_ly * 2) Each line of a tile is 2 bytes

        // Get tile line data
        int lobyte = mem.getByte(addr++);
        int hibyte = mem.getByte(addr);

        // Mix and draw current tile line pixel
        int bitmask = 1 << ((x ^ 7) & 7);
        int nib = ((hibyte & bitmask) != 0 ? 2 : 0) | ((lobyte & bitmask) != 0 ? 1 : 0);
        
//        System.out.println("Put: 4");
        int pxind = putPixel(lx, ly, getPalShade(nib));

        pxMap[pxind] = (byte) nib;

        x++;
        x &= 0xff;

      } else {
//    	  System.out.println("Put: 1");
        int pxind = putPixel(lx, ly, 0);
        pxMap[pxind] = 0;

      }

      // Next !
      lx++;
    }

    // For every scan we draw a window, inc WILC
    wilc = inWindowRn ? wilc++ : wilc;

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
          // (0x8000 + [sprite tile index * 16])
          // (sprite row * 2) we account for yflip as well
        addr += 0x8000;
        
        int pxind_y = realY * gbwidth;

        // Get tile data
        int lobyte = mem.getByte(addr++);
        int hibyte = mem.getByte(addr);

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
            || (sprite.behind && pxMap[pxind_y + sx] > 0)
          )
            continue;

          // Mix and draw !
          int px = nib;
//          System.out.println("Put: 2");
          putPixel(sx, realY, px);

        }
        // Next sprite pls !
      }

    }

    // Fin !
  }

  public void paint(Graphics g) {
    setBackground(Color.BLACK);
    Graphics2D g2d = (Graphics2D) g;
    //		g2d.scale(1.0, 1.0);
    g2d.drawImage(img, 0, 0, getWidth(), getHeight(), this);
  }

  public void renderImage() {
    repaint();
  }
  
  public Ppu() {
	  instance = this;
  }
  
  //    public void paint(Graphics g) {  
  //    	
  //    	setBackground(Color.WHITE);
  //    	
  //    	int xDraw = 0;
  //    	int yDraw = 0;
  //    	int tileNum = 0;
  //    	
  //    	int addr = 0x8000;
  //    	    	
  //    	for (int y = 0; y < 24; y++) {
  //    		for (int x = 0; x < 16; x++) {
  //    			displayTile(g, addr, tileNum, xDraw + (x * scale), yDraw + (y * scale));
  //    			xDraw += (8 * scale);
  //    			tileNum++;
  //    		}
  //    		
  //    		yDraw += (8 * scale);
  //    		xDraw = 0;
  //    	}
  //    	          
  //    }

  //	public void displayTile(Graphics g, int addr, int tileNum, int x, int y) {
  //	for (int tileY = 0; tileY < 16; tileY += 2) {
  //		int b1 = mem.getByte(addr + (tileNum * 16) + tileY);
  //		int b2 = mem.getByte(addr + (tileNum * 16) + tileY + 1);
  //		
  //		for (int bit = 7; bit >= 0; bit--) {
  //			int hi = (b1 & (1 << bit)) >> (bit - 1);
  //			int lo = (b2 & (1 << bit)) >> bit;
  //			
  //			int color = hi | lo;
  //			
  //			g.setColor(pallete[color]);
  //			g.fillRect(x + (7 - bit) * scale, y + (tileY / 2 * scale), scale, scale);
  //
  //		}
  //	}
  //}
}