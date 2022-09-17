package main;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;
import javax.swing.JFrame;
import javax.swing.Timer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class Main {

	public static void main(String[] args) {
		// bekapcsolni a hardware accelerationt, mert linuxon valamiért alapértelmezetten nincs
		// https://stackoverflow.com/questions/41001623/java-animation-programs-running-jerky-in-linux/41002553#41002553https://stackoverflow.com/questions/41001623/java-animation-programs-running-jerky-in-linux/41002553#41002553
	    System.setProperty("sun.java2d.opengl", "true");
 
        try {
        	// https://docs.oracle.com/javase/tutorial/sound/overview-MIDI.html
        	Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();
            Sequence sequence = MidiSystem.getSequence(new File("haydn_symphony_100_2.mid"));
            sequencer.setSequence(sequence);
            Transmitter transmitter = sequencer.getTransmitter();
    		Roll roll = new Roll();
    		transmitter.setReceiver(roll);
            sequencer.start();
 

            JFrame frame = new JFrame("Synthesijava");
            frame.setSize(1280, 720);
            frame.add(roll);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);


            // https://stackoverflow.com/questions/5824049/running-a-method-when-closing-the-program
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                	//System.out.println("záródunk");
            		transmitter.close();
                	sequencer.close();
                }
            });
            
            // https://stackoverflow.com/questions/57948299/why-does-my-custom-swing-component-repaint-faster-when-i-move-the-mouse-java
            /* Update the scene every 40 milliseconds. */
            Timer timer = new Timer(10, (e) -> roll.repaint());
            timer.start();
            
            //while (true)
            //	roll.repaint();
            
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
