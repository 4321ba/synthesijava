package synthesijava.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;

import synthesijava.Roll;
import synthesijava.midi.Delayer;
import synthesijava.midi.Splitter;

public class DirectionChangeListener implements ActionListener {

	Delayer delayer;
	Transmitter leftSplitterTransmitter;
	Receiver rightSplitterReceiver;
	
	Roll roll;
	Splitter leftSplitter, rightSplitter;
	public DirectionChangeListener(Roll r, Splitter leftS, Splitter rightS) {
		roll = r;
		leftSplitter = leftS;
		rightSplitter = rightS;
		leftSplitterTransmitter = leftSplitter.newTransmitter();
		rightSplitterReceiver = rightSplitter.newReceiver();
	}
	// ezt kell meghívni bezárás előtt, hogy leállítsa a Delayer threadjét
	public void close() {
		if (delayer != null)
			delayer.close();
		delayer = null;
	}
	@Override public void actionPerformed(ActionEvent event) {
		close();
		if ("Notes fly upwards".equals(event.getActionCommand())) {
            leftSplitterTransmitter.setReceiver(rightSplitterReceiver);
            roll.setGoingDownwards(false);
		} else if ("Notes fall downwards".equals(event.getActionCommand())) {
            delayer = new Delayer();
            leftSplitterTransmitter.setReceiver(delayer);
            delayer.setReceiver(rightSplitterReceiver);
            delayer.start();
            roll.setGoingDownwards(true);
		}
	}

}