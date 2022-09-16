package main;

import java.awt.Graphics;

public class Note {
	int pitch; // = note
	int volume; // = velocity
	long begin;
	long end;
	Note(int p, int v) {
		pitch = p;
		volume = v;
		begin = System.currentTimeMillis();
		end = -1l;
	}
	void setEnd() {
		end = System.currentTimeMillis();
	}
	void paint(Graphics g) {
		long currt = System.currentTimeMillis();
		long newend = end == -1 ? currt : end;
		g.fillRect(10*pitch, (int)((currt-newend)/10), 10, (int)((newend-begin) / 10));
		//System.out.printf("%d %d %d %d\n", 10*pitch, (int)((currt-begin)/30), 10, (int)((newend-begin) / 30));
	}
}
