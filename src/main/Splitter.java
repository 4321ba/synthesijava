package main;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public class Splitter implements Receiver {

	List<Transmitter> tr = new ArrayList<Transmitter>();
	
	@Override
	public void send(MidiMessage message, long timeStamp) {
		for (Transmitter transmitter : tr) {
			transmitter.getReceiver().send(message, timeStamp);
		}
	}
	
	public Transmitter newTransmitter() {
		Transmitter t = new InnerTransmitter();
		tr.add(t);
		return t;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
	class InnerTransmitter implements Transmitter {

		Receiver r = null;
		
		@Override
		public void setReceiver(Receiver receiver) {
			r = receiver;
		}

		@Override
		public Receiver getReceiver() {
			return r;
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}
		
	}

}
