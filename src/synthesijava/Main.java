package synthesijava;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import synthesijava.listener.DirectionChangeListener;
import synthesijava.listener.ExternalDeviceConnector;
import synthesijava.listener.MidiFileChooser;
import synthesijava.listener.PianoSaverLoader;
import synthesijava.listener.StartStopListener;
import synthesijava.midi.Splitter;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

	static JMenuBar createMenuBar(Sequencer sequencer, Piano piano, DirectionChangeListener dCL, ExternalDeviceConnector eDC) {
        // https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html

		JMenuBar menuBar = new JMenuBar();

		{ // File menu
			JMenu fileMenu = new JMenu("File");
			fileMenu.setMnemonic(KeyEvent.VK_F);
			menuBar.add(fileMenu);

			JMenuItem openMidiButton = new JMenuItem("Open MIDI", KeyEvent.VK_O);
			openMidiButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
			openMidiButton.addActionListener(new MidiFileChooser(sequencer));
			fileMenu.add(openMidiButton);
			
			fileMenu.addSeparator();

			PianoSaverLoader pianoSaverLoader = new PianoSaverLoader(piano);
			JMenuItem loadPianoButton = new JMenuItem("Load piano settings", KeyEvent.VK_L);
			loadPianoButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
			loadPianoButton.addActionListener(pianoSaverLoader);
			fileMenu.add(loadPianoButton);
			JMenuItem savePianoButton = new JMenuItem("Save piano settings", KeyEvent.VK_S);
			savePianoButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			savePianoButton.addActionListener(pianoSaverLoader);
			fileMenu.add(savePianoButton);
		}
		
		{ // Playback menu
			JMenu playbackMenu = new JMenu("Playback");
			playbackMenu.setMnemonic(KeyEvent.VK_B);
			menuBar.add(playbackMenu);
			
			JMenuItem startStopButton = new JMenuItem("Start / Stop", KeyEvent.VK_S);
			startStopButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
			startStopButton.addActionListener(new StartStopListener(sequencer));
			playbackMenu.add(startStopButton);
			
			playbackMenu.addSeparator();

			JMenuItem externalDeviceButton = new JMenuItem("Connect / Disconnect external MIDI device", KeyEvent.VK_C);
			externalDeviceButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
			externalDeviceButton.addActionListener(eDC);
			playbackMenu.add(externalDeviceButton);
		}
		
		{ // Piano menu
			JMenu pianoMenu = new JMenu("Piano");
			pianoMenu.setMnemonic(KeyEvent.VK_P);
			menuBar.add(pianoMenu);
			
			JMenuItem addLeftButton = new JMenuItem("Add a key to the left side", KeyEvent.VK_L);
			addLeftButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0));
			addLeftButton.addActionListener(piano);
			pianoMenu.add(addLeftButton);
			JMenuItem removeLeftButton = new JMenuItem("Remove a key from the left side", KeyEvent.VK_E);
			removeLeftButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.SHIFT_MASK));
			removeLeftButton.addActionListener(piano);
			pianoMenu.add(removeLeftButton);
			JMenuItem addRightButton = new JMenuItem("Add a key to the right side", KeyEvent.VK_L);
			addRightButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
			addRightButton.addActionListener(piano);
			pianoMenu.add(addRightButton);
			JMenuItem removeRightButton = new JMenuItem("Remove a key from the right side", KeyEvent.VK_E);
			removeRightButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, ActionEvent.SHIFT_MASK));
			removeRightButton.addActionListener(piano);
			pianoMenu.add(removeRightButton);
		}

		{ // Mode menu
			JMenu modeMenu = new JMenu("Mode");
			modeMenu.setMnemonic(KeyEvent.VK_M);
			menuBar.add(modeMenu);
			
			ButtonGroup directionButtonGroup = new ButtonGroup();
			JRadioButtonMenuItem upwardsDirectionButton = new JRadioButtonMenuItem("Notes fly upwards");
			upwardsDirectionButton.setMnemonic(KeyEvent.VK_U);
			upwardsDirectionButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
			upwardsDirectionButton.addActionListener(dCL);
			upwardsDirectionButton.setSelected(true);
			directionButtonGroup.add(upwardsDirectionButton);
			modeMenu.add(upwardsDirectionButton);
			JRadioButtonMenuItem downwardsDirectionButton = new JRadioButtonMenuItem("Notes fall downwards");
			downwardsDirectionButton.setMnemonic(KeyEvent.VK_D);
			downwardsDirectionButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
			downwardsDirectionButton.addActionListener(dCL);
			directionButtonGroup.add(downwardsDirectionButton);
			modeMenu.add(downwardsDirectionButton);
			
			downwardsDirectionButton.doClick(); // hogy a listener beállítsa a dolgokat
			
		}
		
		{ // About menu
			JMenu aboutMenu = new JMenu("About");
			aboutMenu.setMnemonic(KeyEvent.VK_A);
			menuBar.add(aboutMenu);
			
			JMenuItem aboutButton = new JMenuItem("About Synthesijava", KeyEvent.VK_A);
			aboutMenu.add(aboutButton);
			aboutButton.addActionListener((event) -> {
					// https://stackoverflow.com/questions/9119481/how-to-present-a-simple-alert-message-in-java TODO devversion
					JOptionPane.showMessageDialog(null, "Dev version.\nMade by 1234ab for the university subject Programming 3 at BME.\nUses the Java standard library and the Swing toolkit.", "About", JOptionPane.INFORMATION_MESSAGE);
				});
			JMenuItem sourceCodeButton = new JMenuItem("Source code", KeyEvent.VK_S);
			sourceCodeButton.addActionListener((event) -> {
					try {
						// https://stackoverflow.com/questions/748895/how-do-you-open-web-pages-in-java
						java.awt.Desktop.getDesktop().browse(new java.net.URI("https://github.com/4321ba/synthesijava"));
					} catch (IOException | URISyntaxException e) {
						JOptionPane.showMessageDialog(null, "Could not open the webpage https://github.com/4321ba/synthesijava: " + e.getMessage() + ".", "Error", JOptionPane.ERROR_MESSAGE);
					}
				});
			aboutMenu.add(sourceCodeButton);
		}
		
		return menuBar;
	}
	
	static void createAndShowGUI(Sequencer sequencer, Piano piano, Roll roll, DirectionChangeListener dCL, ExternalDeviceConnector eDC, WindowListener closingAction) {
		JFrame frame = new JFrame("Synthesijava");
        frame.setSize(1280, 720);
        frame.setMinimumSize(new Dimension(320, 240));
        frame.setJMenuBar(createMenuBar(sequencer, piano, dCL, eDC));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(closingAction);

        // https://docs.oracle.com/javase/tutorial/uiswing/layout/box.html
        // https://docs.oracle.com/javase/tutorial/uiswing/examples/layout/BoxLayoutDemoProject/src/layout/BoxLayoutDemo.java
        // https://stackoverflow.com/questions/19745559/java-swing-boxlayout-having-panels-of-different-sizes-ratio-to-each-other
        // https://stackoverflow.com/questions/2432839/what-is-the-relation-between-contentpane-and-jpanel
        // https://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html
        Container pane = frame.getContentPane();
        pane.setLayout(new GridBagLayout());
        
        GridBagConstraints rollConstraint = new GridBagConstraints();
        rollConstraint.weightx = 1.0;
        rollConstraint.weighty = 7.0;
        rollConstraint.fill = GridBagConstraints.BOTH;
        rollConstraint.gridx = 0;
        rollConstraint.gridy = 0;
        pane.add(roll, rollConstraint);
        GridBagConstraints pianoConstraint = new GridBagConstraints();
        pianoConstraint.weightx = 1.0;
        pianoConstraint.weighty = 1.0;
        pianoConstraint.fill = GridBagConstraints.BOTH;
        pianoConstraint.gridx = 0;
        pianoConstraint.gridy = 1;
        pane.add(piano, pianoConstraint);

        // https://stackoverflow.com/questions/57948299/why-does-my-custom-swing-component-repaint-faster-when-i-move-the-mouse-java
        /* Update the scene every 17 milliseconds. */
        Timer timer = new Timer(17, (e) -> pane.repaint());
        timer.start();
        
        frame.setVisible(true);
        piano.grabFocus(); // hogy lehessen rajta játszani
	}
	
	
	public static void main(String[] args) {
		// bekapcsolni a hardware accelerationt, mert linuxon valamiért alapértelmezetten nincs
		// https://stackoverflow.com/questions/41001623/java-animation-programs-running-jerky-in-linux/41002553#41002553https://stackoverflow.com/questions/41001623/java-animation-programs-running-jerky-in-linux/41002553#41002553
	    System.setProperty("sun.java2d.opengl", "true");
 
        try {
        	// https://docs.oracle.com/javase/tutorial/sound/overview-MIDI.html
        	
        	// összekötögetés pontosan a dokumentációban
        	
        	// Sequencer
        	Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();
            for (Transmitter transmitter : sequencer.getTransmitters())
            	transmitter.close(); // alapból össze van kötve a szintetizátorral, de mi ezt nem szeretnénk
            Transmitter sequencerTransmitter = sequencer.getTransmitter();
            
            // Piano
            Piano piano = new Piano();
            
            // Roll
    		Roll roll = new Roll(piano::getXCoordsForNote);
    		
            // Synthesizer
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
    		
    		// KeyboardMIDIInput
            KeyboardMIDIInput keyboardMIDIInput = new KeyboardMIDIInput();
            piano.addKeyListener(keyboardMIDIInput);

            // az a Splitter, amelyik az ábrán a bal oldalon van
            Splitter leftSplitter = new Splitter();
            
            // az a Splitter, amelyik az ábrán a jobb oldalon van
            Splitter rightSplitter = new Splitter();
            
            // összekötések
            
            sequencerTransmitter.setReceiver(leftSplitter.newReceiver());
            keyboardMIDIInput.setReceiver(leftSplitter.newReceiver());
            // a Default Transmittert majd a lentebb készített ExternalDeviceConnector fogja csatlakoztatni
            
            leftSplitter.newTransmitter().setReceiver(roll);
            // a Delayert majd a lentebb készített DirectionChangeListener fogja ki-be rakosgatni

            rightSplitter.newTransmitter().setReceiver(synthesizer.getReceiver());
            rightSplitter.newTransmitter().setReceiver(piano);
    		
            //synthesizer.loadAllInstruments(MidiSystem.getSoundbank(new File("...-.sf2")));

			DirectionChangeListener dCL = new DirectionChangeListener(roll, leftSplitter, rightSplitter);
			ExternalDeviceConnector eDC = new ExternalDeviceConnector(leftSplitter.newReceiver());
            
            // https://stackoverflow.com/questions/5824049/running-a-method-when-closing-the-program
            // tudom hogy csúnya, de ezért a 3 sorért nem szeretnék egy új fájlt létrehozni, és ez logikailag ide illik
            // a megnyitások után
            WindowListener closingAction = new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                	sequencer.close();
                	synthesizer.close();
                	dCL.close();
                	eDC.close();
                }
            };

            SwingUtilities.invokeLater(() -> Main.createAndShowGUI(sequencer, piano, roll, dCL, eDC, closingAction));
        }
        catch (MidiUnavailableException ex) {
            ex.printStackTrace();
        }
    }

}
