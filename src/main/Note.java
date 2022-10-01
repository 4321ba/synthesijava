package main;

import java.awt.Color;
import java.awt.Graphics;

public class Note {
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
		Color c = new Color(Color.HSBtoRGB(channel/16.0f, 1.0f, 1.0f));
		Color calphaval = new Color(c.getRed(), c.getGreen(), c.getBlue(), volume + 128);
		return calphaval;
	}
	
	void paint(Graphics g, int width) {
		g.setColor(getNoteColor(pitch, volume, channel));
		
		long currt = System.currentTimeMillis();
		long newend = end == -1 ? currt : end;
		g.fillRect(width*pitch/128, (int)((currt-newend)/10), width/128, (int)((newend-begin) / 10));
		//System.out.printf("%d %d %d %d\n", 10*pitch, (int)((currt-begin)/30), 10, (int)((newend-begin) / 30));
	}
}
