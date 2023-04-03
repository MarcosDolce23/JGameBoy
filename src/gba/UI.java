package gba;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class UI implements ActionListener {
	
	JFrame f;
	JMenuBar mb;
	JMenu menu;
	JMenuItem i1;
	Ppu ppu;
	Memory mem;
	File rom;
	
	int fps = 60;
	
	public UI() {
		mem = new Memory();
		
        f = new JFrame();
        mb = new JMenuBar();
        menu = new JMenu("File");  
        i1 = new JMenuItem("ROM");
        ppu =  new Ppu();
        
        i1.addActionListener(this);
        menu.add(i1);
        mb.add(menu);
        f.setJMenuBar(mb);
		f.add(ppu);
		f.setSize(Ppu.gbwidth * 3, Ppu.gbheight * 3);
		
        f.setVisible(true);
        f.setLayout(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == i1) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			int result = fileChooser.showOpenDialog(fileChooser);
			if (result == JFileChooser.APPROVE_OPTION) {
			    rom = fileChooser.getSelectedFile();
			    System.out.println("Selected file: " + rom.getAbsolutePath());
			    try {
					start();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return;
	}
	
	private void start() throws IOException, InterruptedException {
//		File rom = new File("C:/Users/marco/Downloads/Tetris (Japan) (En)/Dr. Mario (World).gb");
		FileInputStream fl = new FileInputStream(rom);
		Cpu cpu = new Cpu(ppu);
		
		fl.read(mem.ram);
		fl.close();
		
		cpu.cyclesperframe = cpu.cyclespersec / fps;
        cpu.interval = 1000 / fps;
		
		cpu.loopExe(0);
		
	}
	
}
