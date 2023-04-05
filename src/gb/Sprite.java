package gb;

public class Sprite {

    int x;
    int y;
    int tile;

    // Flags
    boolean behind;
    boolean yflip;
    boolean xflip;
    int pallete;

    Sprite() {
      x = 0;
      y = 0;
      tile = 0;

      behind = false;
      yflip = false;
      xflip = false;
      pallete = 0;
    }

    // Byte write methods
    public void setY(int val) {
      y = val;
    }

    public void setX(int val) {
      x = val;
    }

    public void setTile(int val) {
      tile = val;
    }

    public void setFlags(int val) {
      behind = ((val & 0b10000000) == 0b10000000) ? true : false;
      yflip = ((val & 0b01000000) == 0b01000000) ? true : false;
      xflip = ((val & 0b00100000) == 0b00100000) ? true : false;
      pallete = ((val & 0b00010000) == 0b00010000) ? 1 : 0;
    }
  }
