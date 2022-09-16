package main;

import javax.sound.midi.*;


import java.io.File;
import java.util.*;

public class Main {

	public static void main(String[] args) {
 
        try {
 
            // A static method of MidiSystem that returns
            // a sequencer instance.
        	
        	Sequencer sequencer = MidiSystem.getSequencer();
     //       Sequencer sequencer = MidiSystem.getMidiDeviceInfo()[0].;
            for (MidiDevice.Info i : MidiSystem.getMidiDeviceInfo()) {
				
            	System.out.println(i.toString() + i.getDescription() + i.getVersion());
			}
            System.out.println(sequencer.getDeviceInfo());
            sequencer.open();
 
            // Creating a sequence.
            //Sequence sequence = new Sequence(Sequence.PPQ, 4);
            Sequence sequence = MidiSystem.getSequence(new File("haydn_symphony_100_2.mid"));
 
            // PPQ(Pulse per ticks) is used to specify timing
            // type and 4 is the timing resolution.
 
            // Creating a track on our sequence upon which
            // MIDI events would be placed
            //Track track = sequence.createTrack();
            
 
                // Adding some events to the track
//                for (int i = 5; i < (4 * numOfNotes) + 5; i += 4)
//            {
// 
//                // Add Note On event
//                track.add(makeEvent(144, 1, i, 100, i));
// 
//                // Add Note Off event
//                track.add(makeEvent(128, 1, i, 100, i + 2));
//            }
 
            // Setting our sequence so that the sequencer can
            // run it on synthesizer
            sequencer.setSequence(sequence);
 
            // Specifies the beat rate in beats per minute.
            sequencer.setTempoInBPM(220);
 
            // Sequencer starts to play notes
            sequencer.start();
 
            

    		try {
    			while(sequencer.isRunning()) {
    				Thread.sleep(10);
    			}
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            sequencer.close();
        }
        catch (Exception ex) {
 
            ex.printStackTrace();
        }
    }
 
    static public MidiEvent makeEvent(int command, int channel,
                               int note, int velocity, int tick)
    {
 
        MidiEvent event = null;
 
        try {
 
            // ShortMessage stores a note as command type, channel,
            // instrument it has to be played on and its speed.
            ShortMessage a = new ShortMessage();
            a.setMessage(command, channel, note, velocity);
 
            // A midi event is comprised of a short message(representing
            // a note) and the tick at which that note has to be played
            event = new MidiEvent(a, tick);
        }
        catch (Exception ex) {
 
            ex.printStackTrace();
        }
        return event;
    }

}
