package synthesijava.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import synthesijava.Roll;
import synthesijava.midi.Delayer;
import synthesijava.midi.Splitter;
/**
 * megoldja a felfele ill lefele történő kirajzolás közötti váltást:
 * beteszi / kiveszi a Delayert, illetve megkéri a Roll-t, hogy forduljon fel
 */
public class DirectionChangeListener implements ActionListener {

	private Delayer delayer;
	private Transmitter leftSplitterTransmitter;
	private Receiver rightSplitterReceiver;
	
	private Roll roll;
	/**
	 * ctor, a rollról kell tudnunk mert fel kell fordítani, továbbá a leftSplitterről illetve a rightSplitterről
	 * (ábra a dokumentációban) elég respectively tudnunk az egyik transmitteréről illetve receiveréről
	 * azután ezeket tudjuk egymással összekötni (felfele), vagy a kettő közé bekötjük a delayert (lefele)
	 */
	public DirectionChangeListener(Roll r, Splitter leftS, Splitter rightS) {
		roll = r;
		leftSplitterTransmitter = leftS.newTransmitter();
		rightSplitterReceiver = rightS.newReceiver();
	}
	/**
	 *  ezt kell meghívni bezárás előtt, hogy leállítsa a Delayer threadjét
	 */
	public void close() {
		if (delayer != null)
			delayer.close();
		delayer = null;
	}
	/**
	 * mivel a delayer külön thread, ezért inkább leállítjuk, amikor nincs szükség rá, viszont így minden alkalommal
	 * újat kell létrehozni (threadet nem lehet újraindítani)
	 * egyébként ez a függvény érzékeli a guiban a változást, és elvégzi a szükséges változtatásokat
	 */
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