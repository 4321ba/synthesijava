package synthesijava;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.JPanel;
/**
 * a lefele eső téglákat menedzseli
 */
public class Roll extends JPanel implements Receiver {

	private static final long serialVersionUID = 1L;
	static final int MAX_CHANNELS = 16;
	static final int MAX_PITCHES = 128;
	
	/**
	 * if false, it goes upwards
	 */
	private boolean isGoingDownwards = true;
	public void setGoingDownwards(boolean isGoingDownwards) {
		this.isGoingDownwards = isGoingDownwards;
	}
	/**
	 *  the delay between the note appearing at the top and arriving at the bottom
	 */
	public static final long DELAYMS = 3000;
	/**
	 * olyan függvény, amit meghívva megkapjuk az adott x koordinátáit (kezdeti és vég) a hangnak
	 * 
	 * muszáj tudni a Piano-nak erről a függvényéről legalább, mert ő tudja, hogy bal/jobb oldalról mennyi billentyű van levéve/hozzáadva
	 * és annak függvényében kell a lefele eső hangok x koordinátáit is kirajzolni
	 * emiatt az egyetlen függvény miatt nem akartam, hogy függőség alakuljon ki az egész Piano-ra, szóval csak függvénypointert kapunk
	 */
	private Function<Integer, int[]> getXCoordsForNote;
	/**
	 * konstuktor a getXCoordsForNote változó miatt
	 */
	public Roll(Function<Integer, int[]> getXCoordsForNote) {
		this.getXCoordsForNote = getXCoordsForNote;
	}
	
	/**
	 * eltárolja a képernyőn levő hangokat, rendezve: hogy rendezetten lehessen kirajzolni őket
	 * (pl fekete legyen mindig feljebb)
	 */
	private SortedSet<Note> notes = new TreeSet<Note>();
	/**
	 * eltárolja azokat a hangokat, amikhez (nyilván) érkezett note on, de note off még nem
	 * így tudjuk majd egyszerűen lezárni őket, és nem kell a notes-ban keresgélni
	 */
	private Note[][] currentlyPressed = new Note[MAX_CHANNELS][MAX_PITCHES];
	
	/**
	 * itt kapjuk a midi üzeneteket, csak a note on és note off érdekel minket, ezen belül pedig a fentebbi két tagváltozó
	 * megfelelő tagjait átírja
	 */
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
		// lezárni az előző hangot, ha van (note off esetén ennyit elég csinálni)
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
	/**
	 * kirajzolja a hangokat/téglákat, pontosabban megkéri a hangokat/téglákat, hogy rajzolják ki magukat
	 */
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
    			boolean isDrawn = note.paint(g, size, getXCoordsForNote, upperTimeStamp, lowerTimeStamp);
    			if (!isDrawn)
    				it.remove();
    		}
		}
	}

}
