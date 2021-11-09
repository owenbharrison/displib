package io.github.owenbharrison.displib.noise;

import java.io.InputStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;

public class Sound {
	public Clip clip;
	
	public Sound(InputStream stream) {
		try {
			this.clip = AudioSystem.getClip();
			AudioInputStream ais = AudioSystem.getAudioInputStream(stream);
			this.clip.open(ais);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void play() {
		this.clip.start();
	}
	
	public void rewind() {
		this.clip.setFramePosition(0);
	}
	
	public void pause() {
		this.clip.stop();
	}
}
