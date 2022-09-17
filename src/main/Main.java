package main;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;
import javax.swing.JFrame;
import javax.swing.Timer;

import java.io.File;

public class Main {

	public static void main(String[] args) {
		// bekapcsolni a hardware accelerationt, mert linuxon valamiért alapértelmezetten nincs
		// https://stackoverflow.com/questions/41001623/java-animation-programs-running-jerky-in-linux/41002553#41002553https://stackoverflow.com/questions/41001623/java-animation-programs-running-jerky-in-linux/41002553#41002553
	    System.setProperty("sun.java2d.opengl", "true");
 
        try {
        	
        	Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();
            Sequence sequence = MidiSystem.getSequence(new File("haydn_symphony_100_2.mid"));
            sequencer.setSequence(sequence);
            Transmitter tr = sequencer.getTransmitter();
    		Roll r = new Roll();
    		tr.setReceiver(r);
            sequencer.start();
 

            JFrame frame = new JFrame("Synthesijava");
            frame.add(r);
            frame.setSize(1280, 720);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            

            /* Update the scene every 40 milliseconds. */
            Timer timer = new Timer(16, (e) -> r.repaint());
            timer.start();
            
//			while(sequencer.isRunning()) {
//				frame.getContentPane().repaint();
//				//r.repaint();
//			}
//    		tr.close();
//    		
//            sequencer.close();
        }
        catch (Exception ex) {
        	
            ex.printStackTrace();
        }
    }

}
