package main;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;
import javax.swing.JFrame;

import java.io.File;

public class Main {

	public static void main(String[] args) {
 
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

    		try {
    			while(sequencer.isRunning()) {
    				Thread.sleep(10);
    				frame.getContentPane().repaint();
    				//r.repaint();
    			}
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		tr.close();
    		
            sequencer.close();
        }
        catch (Exception ex) {
 
            ex.printStackTrace();
        }
    }

}
