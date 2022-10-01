package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MidiFileChooser implements ActionListener {

	@Override public void actionPerformed(ActionEvent e) {
		System.out.println("Hehehe" + e.getActionCommand());
	}

}
