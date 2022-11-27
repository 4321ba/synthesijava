package synthesijava.midi;

import java.util.LinkedList;
import java.util.Queue;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import synthesijava.Roll;

/**
 * külön szálon futó késleltető FIFO implementáció
 * a receiver interfészén kapott midi üzeneteket visszaadja Roll.DELAYMS késleltetés múlva a transzmitter interfészén
 */
public class Delayer extends Thread implements Receiver, Transmitter {

	/**
	 * nem létezett pair a java-ban szóval muszáj voltam írni egyet
	 * @param <K> kulcs
	 * @param <V> érték
	 */
	static private class Pair<K, V> {
		K k;
		V v;
		Pair(K k, V v) { this.k = k; this.v = v; }
	}
	
	/**
	 * a sor, ez az adatstruktúra, mivel el kell tárolni az időbélyeget (Long), meg az üzenetet is
	 * de sorrendet megőrizve
	 * viszont mindig csak az egyik felébe teszünk be és a másikból veszünk ki
	 * Map típusú tároló meg nem jöhet szóba, mert lehet többször is ugyanaz az időbélyeg
	 */
	private Queue<Pair<Long, MidiMessage>> messages = new LinkedList<>();
	
	// transzmitter implementáció
	private Receiver receiver;
	@Override
	public void setReceiver(Receiver receiver) { this.receiver = receiver; }
	@Override
	public Receiver getReceiver() { return receiver; }

	/**
	 * ezt fogja meghívni az összekötött transzmitter, ha küldeni akar nekünk valamit
	 * mi betesszük a sorba
	 */
	@Override
	public void send(MidiMessage message, long timeStamp) {
		synchronized(messages) {
			messages.add(new Pair<Long, MidiMessage>(System.currentTimeMillis(), message));
			messages.notify(); // csak egyetlenegy hely várakozhat
		}
	}

	/**
	 * bezáráskor le kell állítanunk a threadet, ezt interrapttal jelezzük ((Benes emlékére))
	 */
	@Override
	public void close() {
		interrupt();
	}

	/**
	 * mindig határozatlan ideig várunk (ha nincs semmi a sorban), vagy addig, amíg a sor elején levő üzenetet ki kell bocsátanunk
	 * mivel az üzenetek érkezési (és így kibocsátási) sorrendben vannak a sorban
	 * bezárás interruptedexceptionnel
	 */
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
