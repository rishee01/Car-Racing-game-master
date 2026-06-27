import javax.sound.sampled.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoundManager {
    private static boolean muted = false;
    private static float volume = 0.5f; // 0.0 to 1.0
    private static final ExecutorService soundExecutor = Executors.newCachedThreadPool();
    private static SourceDataLine bgmLine = null;
    private static boolean bgmRunning = false;
    private static SourceDataLine engineLine = null;
    private static boolean engineRunning = false;
    private static float latestEngineSpeedPercent = 0.0f;

    public static synchronized void toggleMute() {
        muted = !muted;
        if (muted) {
            stopBGM();
            stopEngineHum();
        } else {
            startBGM();
            startEngineHum();
        }
    }

    public static synchronized boolean isMuted() {
        return muted;
    }

    public static synchronized void setVolume(float vol) {
        volume = Math.max(0.0f, Math.min(1.0f, vol));
    }

    public static synchronized float getVolume() {
        return volume;
    }

    // Play a short UI click sound
    public static void playClick() {
        if (muted) return;
        soundExecutor.execute(() -> playTone(800, 50, volume * 0.5f));
    }

    // Play a countdown beep
    public static void playBeep(boolean isGo) {
        if (muted) return;
        soundExecutor.execute(() -> {
            if (isGo) {
                playTone(1200, 300, volume * 0.8f);
            } else {
                playTone(600, 150, volume * 0.8f);
            }
        });
    }

    // Play nitro boost sound (upward frequency sweep + noise)
    public static void playNitro() {
        if (muted) return;
        soundExecutor.execute(() -> {
            try {
                AudioFormat format = new AudioFormat(22050, 8, 1, true, false);
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format);
                line.start();

                byte[] buffer = new byte[4410]; // 200 ms
                for (int i = 0; i < buffer.length; i++) {
                    double t = (double) i / 22050.0;
                    double freq = 200.0 + 800.0 * (t / 0.2); // Sweep from 200Hz to 1000Hz
                    double val = Math.sin(2.0 * Math.PI * freq * t);
                    // Add noise
                    double noise = Math.random() * 2.0 - 1.0;
                    buffer[i] = (byte) ((val * 0.5 + noise * 0.5) * 127.0 * volume);
                }
                line.write(buffer, 0, buffer.length);
                line.drain();
                line.close();
            } catch (Exception ignored) {}
        });
    }

    // Play crash sound (explosive white noise with decay)
    public static void playCrash() {
        if (muted) return;
        soundExecutor.execute(() -> {
            try {
                AudioFormat format = new AudioFormat(22050, 8, 1, true, false);
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format);
                line.start();

                byte[] buffer = new byte[11025]; // 500 ms
                for (int i = 0; i < buffer.length; i++) {
                    double decay = 1.0 - ((double) i / buffer.length);
                    double noise = Math.random() * 2.0 - 1.0;
                    // Low pass filtered look
                    buffer[i] = (byte) (noise * decay * 127.0 * volume);
                }
                line.write(buffer, 0, buffer.length);
                line.drain();
                line.close();
            } catch (Exception ignored) {}
        });
    }

    // Generate engine sound (continuous pitch varying by speed)
    public static synchronized void startEngineHum() {
        if (engineRunning || muted) return;
        engineRunning = true;
        soundExecutor.execute(() -> {
            try {
                AudioFormat format = new AudioFormat(11025, 8, 1, true, false);
                engineLine = AudioSystem.getSourceDataLine(format);
                engineLine.open(format);
                engineLine.start();

                byte[] buffer = new byte[551]; // 50 ms
                while (engineRunning) {
                    if (muted) {
                        Thread.sleep(100);
                        continue;
                    }
                    float speedPercent = latestEngineSpeedPercent;
                    double baseFreq = 50.0 + 100.0 * speedPercent; // 50Hz to 150Hz
                    for (int i = 0; i < buffer.length; i++) {
                        double t = (double) i / 11025.0;
                        double period = 1.0 / baseFreq;
                        double phase = (t % period) / period;
                        double val = 2.0 * phase - 1.0;
                        buffer[i] = (byte) (val * 0.12 * 127.0 * volume);
                    }
                    engineLine.write(buffer, 0, buffer.length);
                    Thread.sleep(20);
                }
            } catch (Exception e) {
                engineRunning = false;
            }
        });
    }

    public static synchronized void setEngineSpeed(float speedPercent) {
        latestEngineSpeedPercent = speedPercent;
    }

    public static synchronized void stopEngineHum() {
        engineRunning = false;
        if (engineLine != null) {
            try {
                engineLine.stop();
                engineLine.close();
            } catch (Exception ignored) {}
            engineLine = null;
        }
    }

    // Start background music loop (simple retro synth tune)
    public static synchronized void startBGM() {
        if (bgmRunning || muted) return;
        bgmRunning = true;
        soundExecutor.execute(() -> {
            try {
                AudioFormat format = new AudioFormat(22050, 8, 1, true, false);
                bgmLine = AudioSystem.getSourceDataLine(format);
                bgmLine.open(format);
                bgmLine.start();

                // Simple 4-chord progression chiptune loop
                int[] melody = {60, 64, 67, 72, 62, 65, 69, 74, 57, 60, 64, 69, 58, 62, 65, 70};
                int noteIndex = 0;
                byte[] buffer = new byte[2205]; // 100ms per note component

                while (bgmRunning) {
                    if (muted) {
                        Thread.sleep(100);
                        continue;
                    }
                    int midiNote = melody[noteIndex % melody.length];
                    double freq = Math.pow(2.0, (midiNote - 69.0) / 12.0) * 440.0;
                    
                    for (int i = 0; i < buffer.length; i++) {
                        double t = (double) i / 22050.0;
                        // A nice soft square/triangle wave hybrid
                        double val = Math.sin(2.0 * Math.PI * freq * t) > 0 ? 0.3 : -0.3;
                        buffer[i] = (byte) (val * 127.0 * (volume * 0.3f));
                    }
                    bgmLine.write(buffer, 0, buffer.length);
                    noteIndex++;
                    Thread.sleep(150); // Pause between beats
                }
            } catch (Exception e) {
                bgmRunning = false;
            }
        });
    }

    // Stop background music
    public static synchronized void stopBGM() {
        bgmRunning = false;
        if (bgmLine != null) {
            try {
                bgmLine.stop();
                bgmLine.close();
            } catch (Exception ignored) {}
            bgmLine = null;
        }
    }

    // Play custom tone helper
    private static void playTone(int frequency, int durationMs, float gain) {
        try {
            AudioFormat format = new AudioFormat(22050, 8, 1, true, false);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();

            int length = (int) (22050.0 * (durationMs / 1000.0));
            byte[] buffer = new byte[length];
            for (int i = 0; i < length; i++) {
                double t = (double) i / 22050.0;
                double val = Math.sin(2.0 * Math.PI * frequency * t);
                buffer[i] = (byte) (val * 127.0 * gain);
            }
            line.write(buffer, 0, length);
            line.drain();
            line.close();
        } catch (Exception ignored) {}
    }
}
