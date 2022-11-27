package synthesijava;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.function.Function;
/**
 * egy lefele eső tégladarabot testesít meg, hangmagassággal, erővel, kezdő- és végtimestamppel
 * otthont ad egy rakás ide tartozó utility static függvénynek is, amit azért nem teszek nem staticcá,
 * mert csupán ezeknek a használatához felesleges és káros, hogy plusz egy heap-allokációt el kelljen végezni,
 * amikor ezek sima, egyszerű, matematikai transzformációk, és nincs igazából túl nagy közük a lefele eső tégladarabhoz
 */
public class Note implements Comparable<Note> {
	private int pitch; // = note, hangmagasság (0..127)
	private int volume; // = velocity, hangerő (0..127)
	private int channel; // = csatorna (0..15)
	private long begin; // kezdeti időpillanat/timestamp
	private long end; // végső időpillanat/timestamp
	/**
	 * létrehoz egy éppen elkezdődő téglát
	 * @param p pitch 0..127
	 * @param v volume 0..127
	 * @param c channel 0..15
	 */
	Note(int p, int v, int c) {
		pitch = p;
		volume = v;
		channel = c;
		begin = System.currentTimeMillis();
		end = -1l;
	}
	/**
	 * beállítja a téglának, hogy éppen most lett vége
	 */
	void setEnd() {
		end = System.currentTimeMillis();
	}

	/**
	 * visszaadja, hogy az intként megadott pitch/note/hangmagasság-hoz tartozó zongorabillentyű fekete-e
	 * @param note pitch/note/hangmagasság
	 * @return fekete-e
	 */
	static boolean isBlackNote(int note) {
		return note % 12 == 1 || note % 12 == 3 || note % 12 == 6 || note % 12 == 8 || note % 12 == 10;
	}
	/**
	 * visszaadja a megadott pitch/note/hangmagasság nevét, bé/kereszt közül keresztet ad
	 * @param note pitch/note/hangmagasság
	 * @return neve (pl C, C#, D, D#, ...)
	 */
	static String getNoteName(int note) {
		final String[] names = "C,C#,D,D#,E,F,F#,G,G#,A,A#,B".split(",");
		return names[note % 12];
	}
	/**
	 * visszaadja a jellemzők által generált színt, lehetne nem statikus is, de így a Piano-nak nem kell létrehoznia
	 * egy Note objektumot csak azért, hogy ezt a függvényt meghívja
	 * @param pitch pitch/note/hangmagasság 0..127
	 * @param volume volume/velocity/hangerő 0..127
	 * @param channel csatorna 0..15
	 * @return
	 */
	public static Color getNoteColor(int pitch, int volume, int channel) {
		//long currt = System.currentTimeMillis();
		//Color c = new Color(Color.HSBtoRGB((channel/16.0f + (currt%1000)/1000.0f)-(long)(channel/16.0f + (currt%1000)/1000.0f), 1.0f, 1.0f));
		Color c = new Color(Color.HSBtoRGB(channel/16.0f, 1.0f, isBlackNote(pitch) ? 0.7f : 1.0f));
		Color calphaval = new Color(c.getRed(), c.getGreen(), c.getBlue(), volume + 128);
		return calphaval;
	}
	/**
	 * segédfüggvény, egy inverzlerp és egy lerp kombinálása: amilyen módon value megoszlik input_min és input_max között,
	 * olyan módon megoszló értéket ad vissza output_min és output_max között
	 * példák vannak a tesztesetben
	 * value lehet nem input_min és input_max közötti érték is
	 * @param input_min alsó viszonyítási pont a value-hoz
	 * @param input_max felső viszonyítási pont a value-hoz
	 * @param value érték
	 * @param output_min alsó viszonyítási pont a visszatérési értékhez
	 * @param output_max felső viszonyítási pont a visszatérési értékhez
	 * @return az ilerp-lerpelt érték
	 */
	static int remap(long input_min, long input_max, long value, int output_min, int output_max) {
		//inverse lerp and lerp from
        //https://www.gamedev.net/tutorials/programming/general-and-gameplay-programming/inverse-lerp-a-super-useful-yet-often-overlooked-function-r5230/
        double ratio = (value - input_min) / (double)(input_max - input_min);
        return (int) Math.round((1.0 - ratio) * output_min + ratio * output_max);
	}
	
	/**
	 * kirajzolja a téglát g-re
	 * @param g graphics context vagy micsoda
	 * @param rollSize a roll mérete, amire rajzol
	 * @param getXCoordsForNote egy olyan függvénypointer, amit meghívva a pitch/note/hangmagassággal,
	 * megkapjuk, hogy a téglát melyik x koordinátától meddig kell kirajzolni (ezt a piano tudja igazából, mert neki lehet állítgatni a billentyűit)
	 * @param upperTimeStamp a roll tetejének az időbélyege, hogy el lehessen dönteni az y koordinátát a tégla időbélyegéből
	 * @param lowerTimeStamp a roll aljának az időbélyege, hasonló ok miatt
	 * @return hogy ki kellett-e egyáltalán rajzolni, vagy már nincs is a képernyőn
	 */
	boolean paint(Graphics g, Dimension rollSize, Function<Integer, int[]> getXCoordsForNote, long upperTimeStamp, long lowerTimeStamp) {
		g.setColor(getNoteColor(pitch, volume, channel));
		
		long newEnd = end == -1 ? Math.max(upperTimeStamp, lowerTimeStamp) : end;
		int[] xCoords = getXCoordsForNote.apply(pitch);
		int xBegin = xCoords[0];
		int xWidth = xCoords[1] - xCoords[0];
		int y1 = remap(upperTimeStamp, lowerTimeStamp, begin, 0, rollSize.height);
		int y2 = remap(upperTimeStamp, lowerTimeStamp, newEnd, 0, rollSize.height);
		int yBegin = Math.min(y1, y2);
		int yHeight = Math.abs(y1 - y2);
		g.fillRoundRect(xBegin, yBegin, xWidth, yHeight, xWidth / 2, xWidth / 2);
		g.setColor(Color.BLACK);
		g.drawRoundRect(xBegin, yBegin, xWidth, yHeight, xWidth / 2, xWidth / 2);
		// ha a képernyőszélek közül a régebbi timestampű régebbi, mint a frissebbik vége a hangnak, akkor még rajta van a képernyőn
		return Math.min(upperTimeStamp, lowerTimeStamp) <= Math.max(begin, newEnd);
	}
	/**
	 * rendezést valósít meg a hangok között, lényegében azt, hogy a kirajzolás milyen sorrendben történjen
	 */
	@Override
	public int compareTo(Note o) {
		// kirajzolás növekvő sorrendben történjen
		// mivel a fekete hangok keskenyebbek és lehet, hogy teljesen ki lesznek takarva, a fehér viszont mindig kilátszik a fekete alól,
		// ezért a fekete kerül mindig előbbre (későbbi kirajzolásra), azaz ha this fekete, akkor this>o <=> return>0
		if (isBlackNote(pitch) != isBlackNote(o.pitch))
			return isBlackNote(pitch) ? 1 : -1;
		// this < o (azaz return < 0) ha this korábban kezdődik, mint o (ergo kirajzoláskor this legyen hátrébb)
		if (begin != o.begin)
			return (int) (begin - o.begin);
		// this legyen hátrébb rajzolva (azaz this<o) akkor is, ha egyszerre kezdődnek, de this tart tovább
		if (end != o.end)
			return (int) (o.end - end);
		// this legyen előrébb rajzolva (this>o <=> return>0), ha kisebb csatornán van
		if (channel != o.channel)
			return o.channel - channel;
		return pitch - o.pitch;
		// olyant nem engedünk meg, hogy ugyanaz a hangmagasság ugyanazon a csatornán egyszerre többször is szóljon
		// így ezek a dolgok egy hangot egyértelműen azonosítanak
	}
}
