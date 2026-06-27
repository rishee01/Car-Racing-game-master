import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class InputManager extends KeyAdapter {
    private final Set<Integer> activeKeys = new HashSet<>();
    private boolean pauseRequested = false;
    private boolean muteRequested = false;
    private boolean enterPressed = false;
    private boolean spacePressed = false;
    private boolean screenshotRequested = false;

    public synchronized void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        activeKeys.add(keyCode);

        if (keyCode == KeyEvent.VK_ESCAPE) {
            pauseRequested = true;
        }
        if (keyCode == KeyEvent.VK_M) {
            muteRequested = true;
        }
        if (keyCode == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }
        if (keyCode == KeyEvent.VK_SPACE) {
            spacePressed = true;
        }
        if (keyCode == KeyEvent.VK_F12) {
            screenshotRequested = true;
        }
    }

    public synchronized void keyReleased(KeyEvent e) {
        activeKeys.remove(e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            spacePressed = false;
        }
    }

    public synchronized boolean isKeyPressed(int keyCode) {
        return activeKeys.contains(keyCode);
    }

    public synchronized boolean isLeftPressed() {
        return activeKeys.contains(KeyEvent.VK_LEFT) || activeKeys.contains(KeyEvent.VK_A);
    }

    public synchronized boolean isRightPressed() {
        return activeKeys.contains(KeyEvent.VK_RIGHT) || activeKeys.contains(KeyEvent.VK_D);
    }

    public synchronized boolean isUpPressed() {
        return activeKeys.contains(KeyEvent.VK_UP) || activeKeys.contains(KeyEvent.VK_W);
    }

    public synchronized boolean isDownPressed() {
        return activeKeys.contains(KeyEvent.VK_DOWN) || activeKeys.contains(KeyEvent.VK_S);
    }

    public synchronized boolean isSpacePressed() {
        return spacePressed;
    }

    public synchronized boolean checkPauseRequested() {
        boolean req = pauseRequested;
        pauseRequested = false;
        return req;
    }

    public synchronized boolean checkMuteRequested() {
        boolean req = muteRequested;
        muteRequested = false;
        return req;
    }

    public synchronized boolean checkEnterPressed() {
        boolean req = enterPressed;
        enterPressed = false;
        return req;
    }

    public synchronized boolean checkScreenshotRequested() {
        boolean req = screenshotRequested;
        screenshotRequested = false;
        return req;
    }

    public synchronized void clear() {
        activeKeys.clear();
        pauseRequested = false;
        muteRequested = false;
        enterPressed = false;
        spacePressed = false;
        screenshotRequested = false;
    }
}
