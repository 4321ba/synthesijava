package main;

import java.awt.Dimension;
//import java.awt.Canvas;
import java.awt.Graphics;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.JPanel;

public class Roll extends JPanel implements Receiver {

	private static final long serialVersionUID = 1L;
	static final int MAX_CHANNELS = 16;
	static final int MAX_PITCHES = 128;
	
	// if false, it goes upwards
	private boolean isGoingDownwards = true;
	// the delay between the note appearing at the top and arriving at the bottom
	public static final long DELAYMS = 2000;
	
	// muszáj tudni a Piano-ról, mert ő tudja, hogy bal/jobb oldalról mennyi billentyű van levéve/hozzáadva
	// és annak függvényében kell kirajzolni
	private Piano piano;
	public Roll(Piano p) {
		piano = p;
	}
	
	SortedSet<Note> notes = new TreeSet<Note>();
	Note[][] currentlyPressed = new Note[MAX_CHANNELS][MAX_PITCHES];
	
	@Override
	public void send(MidiMessage message, long timeStamp) {
		if (!(message instanceof ShortMessage))
			return;
		ShortMessage sm = (ShortMessage) message;
		if (sm.getCommand() != ShortMessage.NOTE_ON && sm.getCommand() != ShortMessage.NOTE_OFF)
			return;
		int channel = sm.getChannel();
		int pitch = sm.getData1();
		int volume = sm.getData2();
		// lezárni az előző hangot, ha van
		if (currentlyPressed[channel][pitch] != null) {
			currentlyPressed[channel][pitch].setEnd();
			currentlyPressed[channel][pitch] = null;
		}
		if (sm.getCommand() == ShortMessage.NOTE_ON && volume != 0) { // note on 0-s hangerővel igazából note offnak számít
			Note n = new Note(pitch, volume, channel);
			synchronized (notes) {
				notes.add(n);
			}
			currentlyPressed[channel][pitch] = n;
		}
	}

	@Override
	public void close() {
	}
	
	// https://www.oracle.com/java/technologies/painting.html
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Dimension size = getSize();
	    long current = System.currentTimeMillis();
	    long upperTimeStamp = isGoingDownwards ? current : current - DELAYMS;
	    long lowerTimeStamp = isGoingDownwards ? current - DELAYMS : current;
    	synchronized (notes) {
    		for (Iterator<Note> it = notes.iterator(); it.hasNext();) {
    			Note note = it.next();
    			boolean isDrawn = note.paint(g, size, piano, upperTimeStamp, lowerTimeStamp);
    			if (!isDrawn)
    				it.remove();
    		}
		}
	}

}
