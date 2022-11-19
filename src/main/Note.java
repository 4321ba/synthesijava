package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class Note implements Comparable<Note> {
	int pitch; // = note
	int volume; // = velocity
	int channel;
	long begin;
	long end;
	Note(int p, int v, int c) {
		pitch = p;
		volume = v;
		channel = c;
		begin = System.currentTimeMillis();
		end = -1l;
	}
	void setEnd() {
		end = System.currentTimeMillis();
	}
	
	public static Color getNoteColor(int pitch, int volume, int channel) {
		//long currt = System.currentTimeMillis();
		//Color c = new Color(Color.HSBtoRGB((channel/16.0f + (currt%1000)/1000.0f)-(long)(channel/16.0f + (currt%1000)/1000.0f), 1.0f, 1.0f));
		Color c = new Color(Color.HSBtoRGB(channel/16.0f, 1.0f, Piano.isBlackNote(pitch) ? 0.7f : 1.0f));
		Color calphaval = new Color(c.getRed(), c.getGreen(), c.getBlue(), volume + 128);
		return calphaval;
	}
	
	static int remap(long input_min, long input_max, long value, int output_min, int output_max) {
		//inverse lerp and lerp from
        //https://www.gamedev.net/tutorials/programming/general-and-gameplay-programming/inverse-lerp-a-super-useful-yet-often-overlooked-function-r5230/
        double ratio = (value - input_min) / (double)(input_max - input_min);
        return (int) ((1.0 - ratio) * output_min + ratio * output_max + 0.5);
	}
	
	// visszaadja, hogy ki kellett-e egyáltalán rajzolni, vagy már nincs is a képernyőn
	boolean paint(Graphics g, Dimension rollSize, Piano piano, long upperTimeStamp, long lowerTimeStamp) {
		g.setColor(getNoteColor(pitch, volume, channel));
		
		long newEnd = end == -1 ? Math.max(upperTimeStamp, lowerTimeStamp) : end;
		int[] xCoords = piano.getXCoordsForNote(pitch);
		int xBegin = xCoords[0];
		int xWidth = xCoords[1] - xCoords[0];
		int y1 = remap(upperTimeStamp, lowerTimeStamp, begin, 0, rollSize.height);
		int y2 = remap(upperTimeStamp, lowerTimeStamp, newEnd, 0, rollSize.height);
		int yBegin = Math.min(y1, y2);
		int yHeight = Math.abs(y1 - y2);
		g.fillRect(xBegin, yBegin, xWidth, yHeight);
		g.setColor(Color.BLACK);
		g.drawRect(xBegin, yBegin, xWidth, yHeight);
		// ha a képernyőszélek közül a régebbi timestampű régebbi, mint a frissebbik vége a hangnak, akkor még rajta van a képernyőn
		return Math.min(upperTimeStamp, lowerTimeStamp) <= Math.max(begin, newEnd);
	}
	@Override
	public int compareTo(Note o) {
		// kirajzolás növekvő sorrendben történjen
		// mivel a fekete hangok keskenyebbek és lehet, hogy teljesen ki lesznek takarva, a fehér viszont mindig kilátszik a fekete alól,
		// ezért a fekete kerül mindig előbbre (későbbi kirajzolásra), azaz ha this fekete, akkor this>o <=> return>0
		if (Piano.isBlackNote(pitch) != Piano.isBlackNote(o.pitch))
			return Piano.isBlackNote(pitch) ? 1 : -1;
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
