import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/*
 * @author: Horia-George Dună
 * @id: 1949284
 * @author: Radu-Cristian Sarău
 * @id: 1939149 
 */

/**
 * This class deals with the game's music.
 */
public class SnakeMusic {
    private Clip clip;

    /**
     * This method stops the music.
     */
    public void muteMusic() {
        if (clip != null) {
            FloatControl gainControl = (FloatControl) 
                clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(gainControl.getMinimum());
        }
    }

    /**
     * This method plays the music.
     * @param musicLocation is used to find the music's location in the computer.
     */
    void playMusic(String musicLocation) {
        try {
            File musicPath = new File(musicLocation);

            if (musicPath.exists()) {
                if (clip != null) {
                    clip.close();
                }
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                System.out.println("Cannot find file.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method changes the music.
     * @param musicLocation is used to find the music's location in the compuiter.
     */
    public static void changeMusic(String musicLocation) {
        SnakeMusic musicObject = new SnakeMusic();
        musicObject.playMusic(musicLocation);
    }
}