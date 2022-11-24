package synthesijava.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.Sequencer;

public class StartStopListener implements ActionListener {

	Sequencer sequencer;
	public StartStopListener(Sequencer s) {
		sequencer = s;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (sequencer.getSequence() != null) {
			if (sequencer.isRunning())
				sequencer.stop();
			else
				sequencer.start();
		}
	}

}
