package com.zensar.zenlabs.zeva.voiceid;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.eclipse.swt.widgets.Shell;

public class SoundRecorder extends Thread {
	private final File wavFile;
	private TargetDataLine line;
	private final Shell shell;
	public SoundRecorder(String filePath,Shell shell) {
		this.shell = shell;
		wavFile = new File(filePath);
		if (!wavFile.exists()) {
			if (!wavFile.getParentFile().exists())
				wavFile.getParentFile().mkdirs();

		}
	}

	


	/**
	 * Captures the sound and record into a WAV file
	 */
	public void run() {
		try {
			AudioFormat format = new AudioFormat(16000, 16, 1, true, true);
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			if (!AudioSystem.isLineSupported(info)) {
				UIHelper.handelErrors(shell, "Line not supported");
				System.exit(0);
			}
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start(); // start capturing
			AudioInputStream ais = new AudioInputStream(line);
			AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);


		} catch (LineUnavailableException | IOException ex) {
			UIHelper.handelException(shell, ex);
		}
	}

	/**
	 * Closes the target data line to finish capturing and recording
	 */
	void finish() {
		line.stop();
		line.close();
	}

}
