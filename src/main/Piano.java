package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.swing.JPanel;

public class Piano extends JPanel implements Receiver, Transmitter {
	private int lowestNoteDisplayed = 0;
	private int highestNoteDisplayed = 128; // this-1 is actually the last one displayed

	private static final long serialVersionUID = 1L;
	// Transmitter implementációjához:
	private Receiver rec = null; // ennek kell meghívni a send-jét ha transzmittálni akarunk
	@Override public void setReceiver(Receiver receiver) { rec = receiver; }
	@Override public Receiver getReceiver() { return rec; }

	// Receiver implementációjához:
	@Override public void close() { }
	// Ezt fogja meghívni az összekötött transzmitter, ha küldeni akar nekünk valamit
	@Override public void send(MidiMessage message, long timeStamp) {
		// TODO Auto-generated method stub
	}

	public static boolean isBlackNote(int note) {
		return note % 12 == 1 || note % 12 == 3 || note % 12 == 6 || note % 12 == 8 || note % 12 == 10;
	}
	public static boolean isNoteCorF(int note) {
		return note % 12 == 0 || note % 12 == 5;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Dimension size = getSize();
	    g.setColor(Color.BLACK);
	    g.drawRect(0, 0, size.width - 1, size.height - 1);
	    g.setColor(Color.BLACK);
	    int noteCount = highestNoteDisplayed - lowestNoteDisplayed;
	    for (int absoluteNote = lowestNoteDisplayed; absoluteNote < highestNoteDisplayed; ++absoluteNote) {
	    	int relativeNote = absoluteNote - lowestNoteDisplayed;
	    	int beginPixel = ((size.width-1) * relativeNote) / noteCount;
	    	int endPixel = ((size.width-1) * (relativeNote + 1)) / noteCount;
	    	int middlePixel = ((size.width-1) * (relativeNote * 2 + 1)) / (noteCount * 2);
	    
	    	if (isBlackNote(absoluteNote)) {
	    		g.fillRect(beginPixel, 0, endPixel - beginPixel, (int)(size.height * 0.7));
    			g.drawLine(middlePixel, 0, middlePixel, size.height - 1);
	    	}
		    if (isNoteCorF(absoluteNote))
	    		g.drawLine(beginPixel, 0, beginPixel, size.height - 1);
	    }
	}
}
