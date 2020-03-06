package mightyelemental.opensharez;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {

	String path = "";
	Clip   clip;

	private Sound(String path) {
		this.path = path;
		try {
			clip = AudioSystem.getClip();
//			clip.addLineListener( e -> {
//				if (e.getType() == LineEvent.Type.STOP) { Sound.isSoundPlaying.set( false ); }
//			} );
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public static Sound soundFromFile(String path) throws URISyntaxException {
		path = path.replace( ".", "/" );
		BufferedInputStream bis = getAudio( path );
		if (bis != null) {
			return new Sound( path );
		} else {
			System.err.println( "Could not find sound file " + path );
		}
		return null;
	}

	private static BufferedInputStream getAudio(String path) {
		return new BufferedInputStream( Sound.class.getClassLoader()
				.getResourceAsStream( "mightyelemental/opensharez/sounds/" + path + ".wav" ) );
	}

	public void play() {
		clip.stop();
		clip.close();
		clip.setMicrosecondPosition( 0 );
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream( getAudio( path ) );
			clip.open( ais );
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}

	}

}
