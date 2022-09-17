package main;

//import java.awt.Canvas;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.JPanel;

public class Roll extends JPanel implements Receiver {

	private static final long serialVersionUID = 1L;
	static final int MAX_CHANNELS = 16;
	static final int MAX_PITCHES = 128;
	
	Set<Note> notes = new LinkedHashSet<Note>();
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
			notes.add(n);
			currentlyPressed[channel][pitch] = n;
		}
	}

	@Override
	public void close() {
	}
	
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
		for (Iterator<Note> it = notes.iterator(); it.hasNext();) {
			Note note = it.next();
			note.paint(g);
		}
	}

}
