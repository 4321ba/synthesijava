package synthesijava;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Transmitter;

import org.junit.Test;

import synthesijava.midi.Splitter;

public class SplitterTest {
	
	static private class TestReceiver implements Receiver {
		public List<MidiMessage> ml = new ArrayList<>();
		public List<Long> tl = new ArrayList<>();

		@Override
		public void send(MidiMessage message, long timeStamp) {
			ml.add(message);
			tl.add(timeStamp);
		}

		@Override
		public void close() { }
	}
	
	@Test
	public void splitter() throws InvalidMidiDataException {
		Splitter s = new Splitter();
		Receiver r1 = s.newReceiver(); // 1. tesztelt függvény
		Receiver r2 = s.newReceiver();
		Transmitter t1 = s.newTransmitter(); // 2. tesztelt függvény
		Transmitter t2 = s.newTransmitter();
		TestReceiver tr1 = new TestReceiver();
		TestReceiver tr2 = new TestReceiver();
		t1.setReceiver(tr1); // 3. tesztelt függvény
		t2.setReceiver(tr2);
		assertEquals(t1.getReceiver(), tr1); // 4. tesztelt függvény
		assertEquals(t2.getReceiver(), tr2);

		List<MidiMessage> expMl = new ArrayList<>();
		expMl.add(new ShortMessage(ShortMessage.NOTE_ON, 36, 46));
		expMl.add(new SysexMessage(new byte[] {(byte) SysexMessage.SYSTEM_EXCLUSIVE, 4, 5}, 3));
		expMl.add(new ShortMessage(ShortMessage.NOTE_OFF, 42, 49));
		
		r1.send(expMl.get(0), 345); // 5. tesztelt függvény
		r2.send(expMl.get(1), 543);
		r1.send(expMl.get(2), 678);
		
		List<Long> expTl = new ArrayList<>();
		expTl.add(345L);
		expTl.add(543L);
		expTl.add(678L);
		
		assertEquals(expMl, tr1.ml); // összehasonlítás referencia alapján, mert dereferencia alapján nincs implementálva
		assertEquals(expTl, tr1.tl);
		assertEquals(expMl, tr2.ml);
		assertEquals(expTl, tr2.tl);
	}
}
