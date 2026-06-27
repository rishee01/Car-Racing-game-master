import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class Particle {
    public enum ParticleType {
        SMOKE,
        SPARK,
        FLAME,
        RAIN,
        SPEED_LINE,
        TEXT
    }

    private ParticleType type;
    private float x, y;
    private float vx, vy;
    private float life; // 0.0 to 1.0 (1.0 is new, 0.0 is dead)
    private float decay;
    private Color color;
    private float size;
    private String text; // For TEXT type

    public Particle(ParticleType type, float x, float y, float vx, float vy, float decay, Color color, float size) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.decay = decay;
        this.color = color;
        this.size = size;
        this.life = 1.0f;
    }

    public Particle(String text, float x, float y, float vy, float decay, Color color, int fontSize) {
        this.type = ParticleType.TEXT;
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = vy;
        this.decay = decay;
        this.color = color;
        this.size = fontSize;
        this.text = text;
        this.life = 1.0f;
    }

    public boolean update(float slowMoFactor) {
        x += vx * slowMoFactor;
        y += vy * slowMoFactor;
        life -= decay * slowMoFactor;
        return life > 0;
    }

    public void render(Graphics2D g) {
        int alpha = Math.max(0, Math.min(255, (int) (life * 255)));
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));

        switch (type) {
            case SMOKE:
                g.fillOval((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
                break;
            case SPARK:
                g.fillRect((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
                break;
            case FLAME:
                // Blue flame glow
                g.fillOval((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
                break;
            case RAIN:
                g.setStroke(new java.awt.BasicStroke(1));
                g.drawLine((int) x, (int) y, (int) (x - vx * 2), (int) (y + size));
                break;
            case SPEED_LINE:
                g.fillRect((int) x, (int) y, 2, (int) size);
                break;
            case TEXT:
                g.setFont(new Font("Arial", Font.BOLD, (int) size));
                // Draw drop shadow
                g.setColor(new Color(0, 0, 0, alpha));
                g.drawString(text, (int) x + 1, (int) y + 1);
                // Draw text
                g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                g.drawString(text, (int) x, (int) y);
                break;
        }
    }
}
