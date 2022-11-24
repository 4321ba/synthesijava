package synthesijava.midi;

import java.util.LinkedList;
import java.util.Queue;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import synthesijava.Roll;

public class Delayer extends Thread implements Receiver, Transmitter {

	static private class Pair<K, V> {
		K k;
		V v;
		Pair(K k, V v) { this.k = k; this.v = v; }
	}
	
	private Queue<Pair<Long, MidiMessage>> messages = new LinkedList<>();
	
	private Receiver receiver;
	@Override
	public void setReceiver(Receiver receiver) { this.receiver = receiver; }
	@Override
	public Receiver getReceiver() { return receiver; }

	// Ezt fogja meghívni az összekötött transzmitter, ha küldeni akar nekünk valamit
	@Override
	public void send(MidiMessage message, long timeStamp) {
		synchronized(messages) {
			messages.add(new Pair<Long, MidiMessage>(System.currentTimeMillis(), message));
			messages.notify(); // csak egyetlenegy hely várakozhat
		}
	}

	@Override
	public void close() {
		interrupt();
	}

	// bezárás interruptedexceptionnel
	@Override
	public void run() {
		try {
			while (!interrupted()) {
				Pair<Long, MidiMessage> top;
				synchronized(messages) {
					top = messages.peek();
					if (top == null)
						messages.wait(); // ezt csak a betevő dolog oldhatja fel
					top = messages.peek(); // itt már kell legyen benne valami
				}
				long currentTime = System.currentTimeMillis();
				if (top.k + Roll.DELAYMS > currentTime)
					sleep(top.k + Roll.DELAYMS - currentTime);
				receiver.send(top.v, -1);
				synchronized(messages) {
					messages.remove();
				}
			}
		} catch (InterruptedException e) {
			// intended behaviour hogy ezzel lesz vége a szálnak
		}
	}

}
