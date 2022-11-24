package synthesijava;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

public class KeyboardMIDIInput implements Transmitter, KeyListener {
	static final char[] noteToKey = new char[Roll.MAX_PITCHES];
	static {
		noteToKey[43] = '0'; // G
		noteToKey[44] = '1'; // gisz
		noteToKey[45] = 'q';
		noteToKey[46] = '2';
		noteToKey[47] = 'w';
		noteToKey[48] = 'e';
		noteToKey[49] = '4';
		noteToKey[50] = 'r';
		noteToKey[51] = '5';
		noteToKey[52] = 't';
		noteToKey[53] = 'z';
		noteToKey[54] = '7';
		noteToKey[55] = 'u';
		noteToKey[56] = '8';
		noteToKey[57] = 'i';
		noteToKey[58] = '9';
		noteToKey[59] = 'o';
		noteToKey[60] = 'p';
		noteToKey[61] = 'ü';
		noteToKey[62] = 'ő';
		noteToKey[63] = 'ó';
		noteToKey[64] = 'ú';
		noteToKey[65] = 'í';
		noteToKey[66] = 'a';
		noteToKey[67] = 'y';
		noteToKey[68] = 's';
		noteToKey[69] = 'x';
		noteToKey[70] = 'd';
		noteToKey[71] = 'c';
		noteToKey[72] = 'v';
		noteToKey[73] = 'g';
		noteToKey[74] = 'b';
		noteToKey[75] = 'h';
		noteToKey[76] = 'n';
		noteToKey[77] = 'm';
		noteToKey[78] = 'k';
		noteToKey[79] = ',';
		noteToKey[80] = 'l';
		noteToKey[81] = '.';
		noteToKey[82] = 'é';
		noteToKey[83] = '-';
		noteToKey[84] = 'á';
		noteToKey[85] = 'ű';
	}
	static final Map<Character, Integer> keyToNote = new HashMap<>();
	static {
		for (int note = 0; note < Roll.MAX_PITCHES; ++note)
			if (noteToKey[note] != '\0')
				keyToNote.put(noteToKey[note], note);
	}
	//TODO többi osztálynál is privát tagok
	private boolean[] isNotePressed = new boolean[Roll.MAX_PITCHES];
	

	// KeyListener implementációjához:
	@Override
	public void keyTyped(KeyEvent e) { }

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getModifiersEx() != 0)
			return;
		// Sajnos ez csak magyar layouttal működik, de az enkapszuláció örömteli beteljesedése miatt
		// nem kaphatom meg a nyers billentyűkódot, engem ugyanis nem a karakter érdekelne, hanem a billentyű pozíciója a billentyűzeten.
		// Ezt viszont elrejtik előlem (mondjuk platformfüggő is valszeg). See: KeyEvent.rawCode
		char actual = Character.toLowerCase((char)e.getExtendedKeyCode());
		Integer note = keyToNote.get(actual);
		if (note != null && !isNotePressed[note]) { // az echo ellen, ha már nem tudtam máshogy megkülönböztetni
			try {
				receiver.send(new ShortMessage(ShortMessage.NOTE_ON, 0, note, 127), -1l);
				isNotePressed[note] = true;
			} catch (InvalidMidiDataException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		char actual = Character.toLowerCase((char)e.getExtendedKeyCode());
		Integer note = keyToNote.get(actual);
		if (note != null && isNotePressed[note]) {
			try {
				receiver.send(new ShortMessage(ShortMessage.NOTE_OFF, 0, note, 0), -1l);
				isNotePressed[note] = false;
			} catch (InvalidMidiDataException e1) {
				e1.printStackTrace();
			}
		}
		
	}

	// Transmitter implementációjához:
	private Receiver receiver = null; // ennek kell meghívni a send-jét ha transzmittálni akarunk
	@Override public void setReceiver(Receiver receiver) { this.receiver = receiver; }
	@Override public Receiver getReceiver() { return receiver; }
	@Override public void close() { }


}
