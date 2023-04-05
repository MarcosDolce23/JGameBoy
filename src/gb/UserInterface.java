package gb;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class UserInterface implements ActionListener, KeyListener {
	
	public static JFrame frame;
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem i1;
	String rom;
	
	public UserInterface() {
        frame = new JFrame();
        menuBar= new JMenuBar();
        menu = new JMenu("File");  
        i1 = new JMenuItem("ROM");
        
        frame.addKeyListener(this);
        i1.addActionListener(this);
        
        menu.add(i1);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
		frame.setSize(PixelProcessingUnit.gbwidth * 3, PixelProcessingUnit.gbheight * 3);
		
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == i1) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			int result = fileChooser.showOpenDialog(fileChooser);
			if (result == JFileChooser.APPROVE_OPTION) {
			    rom = fileChooser.getSelectedFile().getAbsolutePath();
			    frame.add(Main.ppu);
			    frame.setVisible(true);
			    try {
					Main.cartridge.load(rom);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Main.start();
			}
//			waitingLoop = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Main.joypad.pressButton(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Main.joypad.releaseButton(e.getKeyCode());
	}
}
