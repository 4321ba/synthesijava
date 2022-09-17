package main;

//import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.JPanel;

public class Roll extends JPanel implements Receiver {

	private static final long serialVersionUID = 1L;
	Set<Note> notes = new HashSet<Note>();
	Note[][] currentlyPressed = new Note[16][128];
	
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
			Note n = new Note(pitch, volume);
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
		g.setColor(Color.BLUE);
		for (Iterator<Note> iterator = notes.iterator(); iterator.hasNext();) {
			iterator.next().paint(g);
		}
	}

}
