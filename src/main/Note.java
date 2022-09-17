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
	void paint(Graphics g) {
		Color c = new Color(Color.HSBtoRGB(channel/16.0f, 1.0f, 1.0f));
		Color calphaval = new Color(c.getRed(), c.getGreen(), c.getBlue(), volume + 128);
		g.setColor(calphaval);
		
		long currt = System.currentTimeMillis();
		long newend = end == -1 ? currt : end;
		g.fillRect(10*pitch, (int)((currt-newend)/10), 10, (int)((newend-begin) / 10));
		//System.out.printf("%d %d %d %d\n", 10*pitch, (int)((currt-begin)/30), 10, (int)((newend-begin) / 30));
	}
}
