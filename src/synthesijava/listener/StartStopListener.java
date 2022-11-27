package synthesijava.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.Sequencer;

/**
 * best effort szolgáltatás: megállítja és elindítja a sequencert a gui kérésére
 */
public class StartStopListener implements ActionListener {

	private Sequencer sequencer;
	/**
	 * @param s a sequencer amit megállítunk/elindítunk
	 */
	public StartStopListener(Sequencer s) {
		sequencer = s;
	}
	
	/**
	 * elvégzi a negálást, figyelve, nehogy illegalstateexceptiont kapjunk, ha még nincs egyáltalán mit játszani
	 */
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
