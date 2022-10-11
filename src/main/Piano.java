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

public class Piano extends JPanel implements Receiver, Transmitter, KeyEventDispatcher, KeyListener {
	

	private static Map<Character, Integer> mapKeysAndNotes = new HashMap<Character, Integer>();
	static {
		mapKeysAndNotes.put('0', 43); // G
		mapKeysAndNotes.put('1', 44); // gisz
		mapKeysAndNotes.put('q', 45);
		mapKeysAndNotes.put('2', 46);
		mapKeysAndNotes.put('w', 47);
		mapKeysAndNotes.put('e', 48);
		mapKeysAndNotes.put('4', 49);
		mapKeysAndNotes.put('r', 50);
		mapKeysAndNotes.put('5', 51);
		mapKeysAndNotes.put('t', 52);
		mapKeysAndNotes.put('z', 53);
		mapKeysAndNotes.put('7', 54);
		mapKeysAndNotes.put('u', 55);
		mapKeysAndNotes.put('8', 56);
		mapKeysAndNotes.put('i', 57);
		mapKeysAndNotes.put('9', 58);
		mapKeysAndNotes.put('o', 59);
		mapKeysAndNotes.put('p', 60);
		mapKeysAndNotes.put('ü', 61);
		mapKeysAndNotes.put('ő', 62);
		mapKeysAndNotes.put('ó', 63);
		mapKeysAndNotes.put('ú', 64);
		mapKeysAndNotes.put('í', 65);
		mapKeysAndNotes.put('a', 66);
		mapKeysAndNotes.put('y', 67);
		mapKeysAndNotes.put('s', 68);
		mapKeysAndNotes.put('x', 69);
		mapKeysAndNotes.put('d', 70);
		mapKeysAndNotes.put('c', 71);
		mapKeysAndNotes.put('v', 72);
		mapKeysAndNotes.put('g', 73);
		mapKeysAndNotes.put('b', 74);
		mapKeysAndNotes.put('h', 75);
		mapKeysAndNotes.put('n', 76);
		mapKeysAndNotes.put('m', 77);
		mapKeysAndNotes.put('k', 78);
		mapKeysAndNotes.put(',', 79);
		mapKeysAndNotes.put('l', 80);
		mapKeysAndNotes.put('.', 81);
		mapKeysAndNotes.put('é', 82);
		mapKeysAndNotes.put('-', 83);
		mapKeysAndNotes.put('á', 84);
		mapKeysAndNotes.put('ű', 85);
	}
	static char[] inverseMap = new char[128];//TODO const
	static {
		for (Map.Entry<Character, Integer> entry : mapKeysAndNotes.entrySet()) {
			Character key = entry.getKey();
			Integer val = entry.getValue();
			inverseMap[val] = key;
			
		}
	}
	
	
	private int lowestNoteDisplayed = 0;
	private int highestNoteDisplayed = 128; // this-1 is actually the last one displayed

	private static final long serialVersionUID = 1L;
	
	public Piano() {
		// https://stackoverflow.com/questions/4780910/jpanel-keylistener
		addKeyListener(this);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
	}
	
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
		    g.drawString(""+inverseMap[absoluteNote], beginPixel, size.height / 2);
	    }
	}
	
	
	
	// key listener:
	@Override public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Pressed "+e.getKeyChar());
		Integer note = mapKeysAndNotes.get(e.getKeyChar());
		if (note != null) {
			try { // TODO npe???
				rec.send(new ShortMessage(ShortMessage.NOTE_ON, 0, note, 127), -1l);
			} catch (InvalidMidiDataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Released "+e.getKeyChar());
		Integer note = mapKeysAndNotes.get(e.getKeyChar());
		if (note != null) {
			try { // TODO npe???
				rec.send(new ShortMessage(ShortMessage.NOTE_OFF, 0, note, 127), -1l);
			} catch (InvalidMidiDataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}
