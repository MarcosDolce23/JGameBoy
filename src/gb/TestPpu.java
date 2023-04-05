package gb;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestPpu extends JPanel {
   @Override
   public void paint(Graphics g) {
      Graphics2D graphic2d = (Graphics2D) g;
      graphic2d.setColor(Color.BLUE);
      graphic2d.fillRect(100, 50, 60, 80);
   }
   public static void main(String[] args) {
      JFrame frame = new JFrame("Demo");
      TestPpu ppu = new TestPpu();
      frame.add(ppu);
      frame.setSize(550, 250);
      frame.setVisible(true);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      Graphics g = ppu.getGraphics();
      g.setColor(Color.RED);
      g.fillRect(200, 1000, 60, 80);
      ppu.paint(g);
      
   }
}