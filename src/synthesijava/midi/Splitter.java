package synthesijava.midi;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public class Splitter {

	private List<Transmitter> transmitters = new ArrayList<Transmitter>();
	private List<Receiver> receivers = new ArrayList<Receiver>();
	
	
	public Transmitter newTransmitter() {
		Transmitter t = new InnerTransmitter();
		transmitters.add(t);
		return t;
	}

	public Receiver newReceiver() {
		Receiver r = new InnerReceiver(transmitters);
		receivers.add(r);
		return r;
	}
	
	static private class InnerTransmitter implements Transmitter {
		private Receiver r = null;
		@Override
		public void setReceiver(Receiver receiver) { r = receiver; }
		@Override
		public Receiver getReceiver() {	return r; }
		@Override
		public void close() {}
	}
	
	static private class InnerReceiver implements Receiver {
		private List<Transmitter> transmitters;
		public InnerReceiver(List<Transmitter> trs) {
			transmitters = trs;
		}
		// mások ezt fogják hívni, és mindent ami itt jön, akarjuk továbbítani az összes innertransmitter.r felé
		@Override
		public void send(MidiMessage message, long timeStamp) {
			synchronized (transmitters) {
				for (Transmitter transmitter : transmitters)
					transmitter.getReceiver().send(message, timeStamp);
			}
		}
		@Override
		public void close() {}
	}

}
