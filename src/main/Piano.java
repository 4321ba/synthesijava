package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;
import javax.swing.JPanel;

public class Piano extends JPanel implements Receiver {
	
	public static final int  MAXNOTES = 128;
	
	
	private int lowestNoteDisplayed = 0;
	private int highestNoteDisplayed = MAXNOTES; // this-1 is actually the last one displayed

	private static final long serialVersionUID = 1L;
	
	// Ezt fogja meghívni az összekötött transzmitter, ha küldeni akar nekünk valamit
	@Override public void send(MidiMessage message, long timeStamp) {
		// TODO Auto-generated method stub
	}
	@Override public void close() { }

	public static boolean isBlackNote(int note) {
		return note % 12 == 1 || note % 12 == 3 || note % 12 == 6 || note % 12 == 8 || note % 12 == 10;
	}
	public static String getNoteName(int note) {
		final String[] names = "C,C#,D,D#,E,F,F#,G,G#,A,A#,B".split(",");
		return names[note % 12];
	}
	
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Dimension size = getSize();
	    g.setColor(Color.BLACK);
	    g.drawRect(0, 0, size.width - 1, size.height - 1);
	    int noteCount = highestNoteDisplayed - lowestNoteDisplayed;
	    for (int absoluteNote = lowestNoteDisplayed; absoluteNote < highestNoteDisplayed; ++absoluteNote) {
	    	String noteName = getNoteName(absoluteNote);
	    	int relativeNote = absoluteNote - lowestNoteDisplayed;
	    	int beginPixel = ((size.width-1) * relativeNote) / noteCount;
	    	int endPixel = ((size.width-1) * (relativeNote + 1)) / noteCount;
//	    	int middlePixel = ((size.width-1) * (relativeNote * 2 + 1)) / (noteCount * 2);

		    g.setColor(Color.BLACK);
	    	if (noteName.equals("C") || noteName.equals("F"))
	    		g.drawLine(beginPixel, 0, beginPixel, size.height - 1);
	    	if (isBlackNote(absoluteNote)) {
	    		double lerpWeight = 1/2.0;
	    		if (noteName.equals("C#"))
	    			lerpWeight = 1/3.0;
	    		if (noteName.equals("D#"))
	    			lerpWeight = 2/3.0;
	    		if (noteName.equals("F#"))
	    			lerpWeight = 1/4.0;
	    		if (noteName.equals("G#"))
	    			lerpWeight = 1/2.0;
	    		if (noteName.equals("A#"))
	    			lerpWeight = 3/4.0;
	    		int middlePixel = (int)(lerpWeight * beginPixel + (1 - lerpWeight) * endPixel + 0.5);
	    		g.fillRect(beginPixel, 0, endPixel - beginPixel, (int)(size.height * 0.7));
    			g.drawLine(middlePixel, 0, middlePixel, size.height - 1);
    		    g.setColor(Color.WHITE);
	    	}
		    g.drawString(""+KeyboardMIDIInput.noteToKey[absoluteNote], beginPixel, size.height / 2);
	    }
	}

	
	
	
}
