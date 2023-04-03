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

public class Main {
	
	public static int fps = 60;
	public static Cpu cpu;
	private static boolean waitingLoop = true;
	private static String rom;
	
	static JFrame f;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		Memory mem = new Memory();
//		Ppu ppu =  new Ppu();
        f = new JFrame();  
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("File");  
        JMenuItem i1 = new JMenuItem("ROM");
        
        i1.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			int result = fileChooser.showOpenDialog(fileChooser);
			if (result == JFileChooser.APPROVE_OPTION) {
			    rom = fileChooser.getSelectedFile().getAbsolutePath();
//			    System.out.println("Selected file: " + rom.getAbsolutePath());
			    try {
					start(mem, rom);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			waitingLoop = false;
        });
        
        menu.add(i1);
        mb.add(menu);
        f.setJMenuBar(mb);
        f.setSize(Ppu.gbwidth * 3, Ppu.gbheight * 3);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//		cpu = new Cpu(ppu);
//		setFPS();
//		cpu.loopExe(0);
		
//		new UI();
        
        // Este loop lo hago porque si pongo el estart en el actionListener al entrar en el loop del cpu nunca corta. Debería encontrar una mejor manera de solucionar esto, algo como un callback
        while(waitingLoop) {
        	System.out.println("Waiting for rom...");
//        	continue;
        }
		
		setFPS();
		cpu.loopExe(0);
		 
	}
	
    public static void setFPS() {
        cpu.cyclesperframe = cpu.cyclespersec / fps;
        cpu.interval = 1000 / fps;
    }
	
	private static void start(Memory mem, String src) throws IOException, InterruptedException {
		Cartridge cart = new Cartridge();
		Ppu ppu =  new Ppu();
		f.add(ppu);  
		f.setVisible(true);
		cart.load(src);
		cpu = new Cpu(ppu);
	}

}
