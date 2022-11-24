package synthesijava.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.swing.JOptionPane;


public class ExternalDeviceConnector implements ActionListener {

	Receiver leftSplitterReceiver;
	boolean isConnected = false;
	Transmitter defaultTransmitter = null;
	public ExternalDeviceConnector(Receiver leftSplitterReceiver) {
		this.leftSplitterReceiver = leftSplitterReceiver;
	}
	// ezt kell meghívni bezárás előtt, hogy bezárja az esetleg megnyitott defaultTransmittert
	public void close() {
		if (defaultTransmitter != null)
			defaultTransmitter.close();
		defaultTransmitter = null;
	}
	
	@Override public void actionPerformed(ActionEvent event) {
		try {
			if (!isConnected) {
				defaultTransmitter = MidiSystem.getTransmitter();
				defaultTransmitter.setReceiver(leftSplitterReceiver);
				isConnected = true;
            	JOptionPane.showMessageDialog(null, "External device successfully opened.", "Success", JOptionPane.INFORMATION_MESSAGE);
			} else {
				defaultTransmitter.close();
				defaultTransmitter = null;
				isConnected = false;
            	JOptionPane.showMessageDialog(null, "External device successfully closed.", "Success", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (MidiUnavailableException e) {
			JOptionPane.showMessageDialog(null, "Error while opening external device, it is unavailable: " + e.getMessage() + ".", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}
