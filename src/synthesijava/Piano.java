package synthesijava;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.JPanel;

public class Piano extends JPanel implements Receiver, ActionListener {
	
	private int lowestNoteDisplayed = 0;
	private int highestNoteDisplayed = Roll.MAX_PITCHES; // this-1 is actually the last one displayed

	// operator= cpp-ból, beolvasáshoz
	public Piano operatorEQ(Piano other) {
		this.lowestNoteDisplayed = other.lowestNoteDisplayed;
		this.highestNoteDisplayed = other.highestNoteDisplayed;
		return this;
	}
	
	private static final long serialVersionUID = 1L;
	
	// ez ilyen félig-csúnya megoldás, mert ha egy csatornán még szól hang miközben egy másikon épp befejeződött, azt így nem fogja figyelembe venni
	private transient Color[] keyColors = new Color[Roll.MAX_PITCHES];
	// Ezt fogja meghívni az összekötött transzmitter, ha küldeni akar nekünk valamit
	@Override public void send(MidiMessage message, long timeStamp) {
		if (!(message instanceof ShortMessage))
			return;
		ShortMessage sm = (ShortMessage) message;
		// control change 123 means toggle all notes off: this is emitted by the synthesizer when stopping the playback
		// https://www.whippedcreamsounds.com/midi-cc-list/
		if (sm.getCommand() == ShortMessage.CONTROL_CHANGE && sm.getData1()  == 123 && sm.getChannel() == 0)
			keyColors = new Color[Roll.MAX_PITCHES];
		if (sm.getCommand() != ShortMessage.NOTE_ON && sm.getCommand() != ShortMessage.NOTE_OFF)
			return;
		int channel = sm.getChannel();
		int pitch = sm.getData1();
		int volume = sm.getData2();
		keyColors[pitch] = null; // if note off
		if (sm.getCommand() == ShortMessage.NOTE_ON && volume != 0) { // <-if note on (note on 0-s hangerővel igazából note offnak számít)
			keyColors[pitch] = Note.getNoteColor(pitch, volume, channel);
		}
	}
	@Override public void close() { }

	
	static double getLerpWeight(int blackNote) {
		switch (Note.getNoteName(blackNote)) {
			case "C#": return  2/3.0;
			case "D#": return  1/3.0;
			case "F#": return  3/4.0;
			case "G#": return  2/4.0;
			case "A#": return  1/4.0;
		}
		throw new IllegalArgumentException("BlackNote is not black.");
	}
	int getBlackLineXCoord(int blackNote) {
	    Dimension size = getSize();
	    int noteCount = highestNoteDisplayed - lowestNoteDisplayed;
    	int relativeNote = blackNote - lowestNoteDisplayed;
    	int beginPixel = ((size.width-1) * relativeNote) / noteCount; // begin x coord for the note
    	int endPixel = ((size.width-1) * (relativeNote + 1)) / noteCount; // end x coord for note
		double lerpWeight = getLerpWeight(blackNote);
		return (int)((1 - lerpWeight) * beginPixel + lerpWeight * endPixel + 0.5); // +0.5 for rounding
	}
	
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Dimension size = getSize();
	    // először a fehér billentyűk háttérszínét, majd arra fogjuk rárajzolni a feketéket meg a csíkokat
	    for (int absoluteNote = lowestNoteDisplayed; absoluteNote < highestNoteDisplayed; ++absoluteNote) {
	    	int[] xCoords = getXCoordsForNote(absoluteNote);
	    	int beginPixel = xCoords[0];
	    	int endPixel = xCoords[1];
	    	if (!Note.isBlackNote(absoluteNote)) {
    		    g.setColor(Color.WHITE);
	    		g.setColor(keyColors[absoluteNote]); // null arg is silently ignored
	    		g.fillRect(beginPixel, 0, endPixel - beginPixel, size.height - 1);
	    	}
	    }
	    for (int absoluteNote = lowestNoteDisplayed; absoluteNote < highestNoteDisplayed; ++absoluteNote) {
	    	int[] xCoords = getXCoordsForNote(absoluteNote);
	    	int beginPixel = xCoords[0];
	    	int endPixel = xCoords[1];
		    g.setColor(Color.BLACK);
		    String noteName = Note.getNoteName(absoluteNote);
	    	if (noteName.equals("C") || noteName.equals("F"))
	    		g.drawLine(beginPixel, 0, beginPixel, size.height - 1);
	    	if (Note.isBlackNote(absoluteNote)) {
	    		int blackLineXCoord = getBlackLineXCoord(absoluteNote);
	    		g.drawLine(blackLineXCoord, 0, blackLineXCoord, size.height - 1);
	    		g.fillRect(beginPixel, 0, endPixel - beginPixel, (int)(size.height * 0.7));
	    		g.setColor(keyColors[absoluteNote]);
	    		g.fillRect(beginPixel, 0, endPixel - beginPixel, (int)(size.height * 0.7));
    		    g.setColor(Color.BLACK);
	    		g.drawRect(beginPixel, 0, endPixel - beginPixel, (int)(size.height * 0.7));
    		    g.setColor(Color.WHITE);
	    	}
		    g.drawString(""+KeyboardMIDIInput.noteToKey[absoluteNote], 
		    		((size.width-1) * (2 * (absoluteNote - lowestNoteDisplayed) + 1)) / (highestNoteDisplayed - lowestNoteDisplayed) / 2 - 3,
		    		size.height / 2);
	    }
	    g.setColor(Color.BLACK);
	    g.drawRect(0, 0, size.width - 1, size.height - 1);
	}
	
	public int[] getXCoordsForNote(int note) {
	    Dimension size = getSize();
	    int noteCount = highestNoteDisplayed - lowestNoteDisplayed;
    	int relativeNote = note - lowestNoteDisplayed;
    	int beginPixel = ((size.width-1) * relativeNote) / noteCount; // begin x coord for the note
    	int endPixel = ((size.width-1) * (relativeNote + 1)) / noteCount; // end x coord for note
    	if (Note.isBlackNote(note - 1))
    		beginPixel = getBlackLineXCoord(note - 1);
    	if (Note.isBlackNote(note + 1))
    		endPixel = getBlackLineXCoord(note + 1);
		return new int[] {beginPixel, endPixel};
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// https://www.javatpoint.com/java-switch-with-string
        switch(e.getActionCommand()) {
	        case "Add a key to the left side":
	        	lowestNoteDisplayed = Math.max(lowestNoteDisplayed - 1, 0);
	            break;
	        case "Remove a key from the left side":
	        	lowestNoteDisplayed = Math.min(lowestNoteDisplayed + 1, highestNoteDisplayed - 1);
	            break;
	        case "Add a key to the right side":
	        	highestNoteDisplayed = Math.min(highestNoteDisplayed + 1, Roll.MAX_PITCHES);
	            break;
	        case "Remove a key from the right side":
	        	highestNoteDisplayed = Math.max(highestNoteDisplayed - 1, lowestNoteDisplayed + 1);
	            break;
	        default:
	        	throw new RuntimeException("Piano got an unknown action " + e.getActionCommand());
        }
        repaint();
    }
	
}
