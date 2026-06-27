import java.awt.Rectangle;

public class EnemyCar {
    private int type; // 0: Sports, 1: Taxi, 2: Truck, 3: Ambulance, 4: Police, 5: Sedan
    private float x;
    private float y;
    private int width;
    private int height;
    private float speed;
    private int targetLane;
    private int laneChangeCooldown = 0;

    public EnemyCar(int type, int lane, float startY, float roadSpeed) {
        this.type = type;
        this.targetLane = lane;
        this.x = getLaneX(lane);
        this.y = startY;

        // Dimensions
        if (type == 2) { // Truck
            this.width = 38;
            this.height = 75;
            this.speed = roadSpeed * 0.4f + (float) Math.random() * 1.0f;
        } else if (type == 3) { // Ambulance
            this.width = 36;
            this.height = 62;
            this.speed = roadSpeed * 0.8f + (float) Math.random() * 1.5f;
        } else if (type == 0) { // Sports Car
            this.width = 36;
            this.height = 57;
            this.speed = roadSpeed * 0.9f + (float) Math.random() * 2.0f;
        } else { // Others
            this.width = 36;
            this.height = 57;
            this.speed = roadSpeed * 0.6f + (float) Math.random() * 1.2f;
        }
    }

    public void update(float roadSpeed, float slowMoFactor, java.util.List<EnemyCar> others) {
        float actualSpeed = speed * slowMoFactor;
        float actualRoadSpeed = roadSpeed * slowMoFactor;

        // Move relative to road speed (enemy moves down since player moves up)
        y += (actualRoadSpeed - actualSpeed);

        // Specific AI Behaviors
        if (type == 3) { // Ambulance lane change logic
            if (laneChangeCooldown > 0) {
                laneChangeCooldown--;
            } else {
                // If there's a car closely in front, try to change lane
                boolean carAhead = false;
                for (EnemyCar other : others) {
                    if (other != this && other.targetLane == this.targetLane && other.y > this.y && (other.y - this.y) < 120) {
                        carAhead = true;
                        break;
                    }
                }
                if (carAhead) {
                    // Choose adjacent lane
                    int dir = Math.random() > 0.5 ? 1 : -1;
                    int newLane = Math.max(0, Math.min(3, targetLane + dir));
                    if (newLane != targetLane) {
                        // Check if new lane is clear
                        boolean laneClear = true;
                        float newX = getLaneX(newLane);
                        for (EnemyCar other : others) {
                            if (other != this && Math.abs(other.x - newX) < 30 && Math.abs(other.y - this.y) < 100) {
                                laneClear = false;
                                break;
                            }
                        }
                        if (laneClear) {
                            targetLane = newLane;
                            laneChangeCooldown = 120; // 2 seconds cooldown
                        }
                    }
                }
            }
        }

        // Interpolate x position towards target lane center
        float targetX = getLaneX(targetLane);
        x += (targetX - x) * 0.08f;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public static float getLaneX(int lane) {
        // Map 0-3 lanes to exact X coordinates
        // Lane 0: 200, Lane 1: 300, Lane 2: 400, Lane 3: 500
        return 200 + lane * 100 - 18; // Subtract 18 (half of width) to center car in lane
    }

    public int getType() { return type; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public float getSpeed() { return speed; }
}
