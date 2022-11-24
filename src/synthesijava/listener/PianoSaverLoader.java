package synthesijava.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import synthesijava.Piano;

public class PianoSaverLoader implements ActionListener {

	// forráskommentek ugyanúgy, mint a StartStopListenernél
	JFileChooser fileChooser = new JFileChooser();
	Piano piano;
	public PianoSaverLoader(Piano p) {
		fileChooser.setFileFilter(new FileNameExtensionFilter("Piano properties file (*.piano)", "piano"));
		piano = p;
	}

	@Override public void actionPerformed(ActionEvent event) {
		if ("Load piano settings".equals(event.getActionCommand())) {
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				try {
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileChooser.getSelectedFile()));
					Piano p = (Piano) ois.readObject();
					ois.close();
					piano.operatorEQ(p);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Error while loading file: " + e.getMessage() + ".", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (ClassNotFoundException | ClassCastException e) {
					JOptionPane.showMessageDialog(null, "Invalid file format: " + e.getMessage() + ".", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if ("Save piano settings".equals(event.getActionCommand())) {
			if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				try {
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileChooser.getSelectedFile()));
					oos.writeObject(piano);
					oos.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Error while saving file: " + e.getMessage() + ".", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	
	}

}
