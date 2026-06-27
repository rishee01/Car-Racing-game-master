import java.awt.Rectangle;

public class PowerUp {
    private int type; // 0: Nitro, 1: Shield, 2: Magnet, 3: SlowMo, 4: DoubleScore, 5: Repair
    private float x;
    private float y;
    private final int size = 24;
    private double hoverOffset = 0;

    public PowerUp(int type, float x, float y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.hoverOffset = Math.random() * Math.PI * 2; // Random starting phase
    }

    public void update(float roadSpeed, float slowMoFactor) {
        // Move down with the road
        y += roadSpeed * slowMoFactor;
        
        // Update float hover animation
        hoverOffset += 0.05;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) (y + getAnimOffset()), size, size);
    }

    public float getAnimOffset() {
        return (float) (Math.sin(hoverOffset) * 4.0);
    }

    public int getType() { return type; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getSize() { return size; }
}
