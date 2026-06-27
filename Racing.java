import javax.swing.*;
import java.awt.event.*;

public class Racing extends JFrame {
    private final InputManager inputManager;
    private final GamePanel gamePanel;

    public Racing() {
        super("Arcade Racer 2D");
        
        // Setup Window size and properties
        setSize(756, 539); // Adjusted slightly to yield exactly 740x500 client drawing size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        // Instantiate core managers
        inputManager = new InputManager();
        gamePanel = new GamePanel(inputManager);

        // Add drawing panel
        add(gamePanel);

        // Attach inputs
        addKeyListener(inputManager);
        gamePanel.addKeyListener(inputManager);

        // Intercept typing for name input in leaderboard
        KeyAdapter typingAdapter = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                gamePanel.handleTyping(e);
            }
            @Override
            public void keyPressed(KeyEvent e) {
                gamePanel.handleTyping(e);
            }
        };
        addKeyListener(typingAdapter);
        gamePanel.addKeyListener(typingAdapter);

        setVisible(true);
        
        // Ensure focus
        gamePanel.requestFocus();
        gamePanel.requestFocusInWindow();
    }

    public static void main(String[] args) {
        // Run UI construction on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(Racing::new);
    }
}
