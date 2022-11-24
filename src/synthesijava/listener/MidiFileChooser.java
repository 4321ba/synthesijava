package synthesijava.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MidiFileChooser implements ActionListener {

	// https://www.javatpoint.com/instance-initializer-block
	JFileChooser fileChooser = new JFileChooser();
	Sequencer sequencer;
	public MidiFileChooser(Sequencer s) {
		fileChooser.setFileFilter(new FileNameExtensionFilter("Standard MIDI File (*.mid)", "mid"));
		sequencer = s;
	}

	// https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
	// https://stackoverflow.com/questions/891380/java-anonymous-class-that-implements-actionlistener
	// https://docs.oracle.com/javase/tutorial/uiswing/events/actionlistener.html
	@Override public void actionPerformed(ActionEvent event) {
		// a file chooser megjelenítése:
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        	try {
				sequencer.setSequence(MidiSystem.getSequence(fileChooser.getSelectedFile()));
	            sequencer.start();
            	//JOptionPane.showMessageDialog(null, "File successfully loaded.", "Success", JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e) {
				// https://www.baeldung.com/java-concat-null-string
				JOptionPane.showMessageDialog(null, "Error while loading file: " + e.getMessage() + ".", "Error", JOptionPane.ERROR_MESSAGE);
			} catch (InvalidMidiDataException e) {
				JOptionPane.showMessageDialog(null, "Invalid MIDI file: " + e.getMessage() + ".", "Error", JOptionPane.ERROR_MESSAGE);
			}
        }
	}

}
