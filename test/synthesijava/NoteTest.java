package synthesijava;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;

import org.junit.Test;

/**
 * teszteli a Note egyes függvényeit, és a Piano.getLerpWeight-et is
 *
 */
public class NoteTest {
	/**
	 * teszteli azt, hogy a zongorabillentyű tényleg fekete-e
	 */
	@Test
	public void blackNote() {
		assertFalse(Note.isBlackNote(0)); // C
		assertFalse(Note.isBlackNote(60)); // C
		assertTrue(Note.isBlackNote(61)); // C#
		assertFalse(Note.isBlackNote(69)); // A, 440hz (A4)
		assertTrue(Note.isBlackNote(70)); // A#/Bé
	}
	/**
	 * teszteli, hogy megfelelő név tartozik a hanghoz
	 */
	@Test
	public void noteName() {
		assertEquals("C", Note.getNoteName(0));
		assertEquals("C", Note.getNoteName(60));
		assertEquals("C#", Note.getNoteName(61));
		assertEquals("A", Note.getNoteName(69));
		assertEquals("A#", Note.getNoteName(70));
	}
	/**
	 * teszteli, hogy a különböző hangok különböző színt kapnak-e
	 */
	@Test
	public void noteColor() {
		Color c1 = Note.getNoteColor(30, 40, 10);
		Color c2 = Note.getNoteColor(31, 40, 10);
		Color c3 = Note.getNoteColor(30, 60, 10);
		Color c4 = Note.getNoteColor(30, 40, 1);
		assertNotEquals(c1.getComponents(null), c2.getComponents(null));
		assertNotEquals(c1.getComponents(null), c3.getComponents(null));
		assertNotEquals(c1.getComponents(null), c4.getComponents(null));
		assertNotEquals(c2.getComponents(null), c3.getComponents(null));
		assertNotEquals(c2.getComponents(null), c4.getComponents(null));
		assertNotEquals(c3.getComponents(null), c4.getComponents(null));
	}
	/**
	 * teszteli, hogy a remap függvény megfelelő értéket ad-e vissza
	 */
	@Test
	public void remap() {
		assertEquals(-11, Note.remap(10, 20, 12, -12, -7));
		assertEquals(-5, Note.remap(10, 20, 5, -10, -20));
		assertEquals(48, Note.remap(30, 10, 15, 40, 51));
	}
	/**
	 * teszteli, hogy a Note természetes rendezése megfelelő-e
	 */
	@Test
	public void noteCompare() {
		// A.compareTo(B) < 0 <=> A-B < 0 <=> A < B <=> B rajzolódik feljebb/később
		// azt várjuk, hogy B feljebb rajzolódik, ha fekete (mivel a fekete a kisebb, és nem látszana ki a fehér alól
		//   korábban (alá) rajzolódjon:      később (fölé) rajzolódjon:
		assertTrue(new Note(60, 10, 1).compareTo(new Note(61, 10, 1)) < 0);
		assertTrue(new Note(60, 10, 1).compareTo(new Note(61, 10, 10)) < 0);
		// a kisebb csatornájú később rajzolódjon, ha a fentebbi dolgok megegyeznek
		assertTrue(new Note(62, 10, 10).compareTo(new Note(60, 10, 1)) < 0);
		// a nagyobb hangmagasságú rajzolódjon később, ha egyébként minden más egyforma
		assertTrue(new Note(60, 10, 1).compareTo(new Note(62, 10, 1)) < 0);
		// a hangmagasságot nem hasonlítjuk:
		assertEquals(new Note(60, 20, 1).compareTo(new Note(60, 10, 1)), 0);
	}
	/**
	 * teszteli, hogy a fekete zongorabillentyű alatti fekete csík x koordinátájának súlya jól van-e számolva
	 */
	@Test
	public void pianoKeyLerpWeight() {
		// C# alatt a C-D elválasztó fekete csík 2:1 arányban osztja a fekete billentyű hosszát
		assertEquals(2/3.0, Piano.getLerpWeight(61), 0); // C#
		assertEquals(1/3.0, Piano.getLerpWeight(63), 0); // D#
		assertEquals(3/4.0, Piano.getLerpWeight(66), 0); // F#
		assertEquals(1/2.0, Piano.getLerpWeight(68), 0); // G#
		assertEquals(1/4.0, Piano.getLerpWeight(70), 0); // A#
	}
	/**
	 * teszteli, hogy a fekete zongorabillentyű alatti fekete csík x koordinátájának súlyának lekérdezése
	 * nem fekete hang esetén exceptiont dob
	 */
	@Test(expected=IllegalArgumentException.class)
	public void pianoKeyLerpIllegal() {
		Piano.getLerpWeight(60); // C
	}
}
