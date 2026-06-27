import java.awt.Rectangle;

public class Player {
    private float x;
    private float y;
    private final int width = 36;
    private final int height = 57;

    // Movement speeds
    private float speed = 0.0f;
    private float maxSpeed = 8.0f;
    private float acceleration = 0.15f;
    private float deceleration = 0.1f;
    private float steerSpeed = 4.0f;

    // Upgrade Levels (1 to 5)
    private int engineLevel = 1;
    private int handlingLevel = 1;
    private int armorLevel = 1;

    // Visual attributes
    private double tilt = 0.0; // steering angle (degrees)
    
    // Stats & Coins
    private int coins = 0;
    private float fuel = 100.0f; // 0 to 100
    private float nitroVal = 50.0f; // 0 to 100
    private boolean nitroActive = false;

    // Power-up durations (in frames or ms; let's use frames at 60fps)
    private int shieldFrames = 0;
    private int magnetFrames = 0;
    private int doubleScoreFrames = 0;
    private int slowMoFrames = 0;

    public Player(float startX, float startY) {
        this.x = startX;
        this.y = startY;
        applyUpgrades();
    }

    public void applyUpgrades() {
        // Engine upgrade improves max speed and acceleration
        this.maxSpeed = 7.0f + (engineLevel - 1) * 1.5f;
        this.acceleration = 0.12f + (engineLevel - 1) * 0.04f;

        // Handling upgrade improves steering speed and braking deceleration
        this.steerSpeed = 3.5f + (handlingLevel - 1) * 0.8f;
        this.deceleration = 0.08f + (handlingLevel - 1) * 0.03f;
    }

    public void update(InputManager input, boolean isCountdown) {
        // Decrease powerups
        if (shieldFrames > 0) shieldFrames--;
        if (magnetFrames > 0) magnetFrames--;
        if (doubleScoreFrames > 0) doubleScoreFrames--;
        if (slowMoFrames > 0) slowMoFrames--;

        // Upgrade applied variables
        float currentMax = maxSpeed;
        if (nitroActive) {
            currentMax = maxSpeed * 1.6f;
        }

        if (isCountdown) {
            // Decelerate or keep static during countdown
            speed = Math.max(0.0f, speed - deceleration);
            tilt = tilt * 0.9;
            return;
        }

        // Handle fuel
        if (fuel > 0) {
            fuel -= 0.015f * (speed / maxSpeed);
        } else {
            fuel = 0;
            currentMax = maxSpeed * 0.2f; // Barely moving on empty tank
        }

        // Steer left/right
        boolean steering = false;
        if (input.isLeftPressed()) {
            x = Math.max(185, x - steerSpeed);
            tilt = Math.max(-12.0, tilt - 2.0);
            steering = true;
        }
        if (input.isRightPressed()) {
            x = Math.min(520, x + steerSpeed);
            tilt = Math.min(12.0, tilt + 2.0);
            steering = true;
        }

        // Natural tilt recovery
        if (!steering) {
            tilt = tilt * 0.8;
            
            // Subtle lane centering / snapping assistance
            // If they are near center of lanes, slide them in slightly
            int[] lanes = {200, 300, 400, 500};
            for (int lane : lanes) {
                if (Math.abs(x - lane) < 15) {
                    x += (lane - x) * 0.1f;
                    break;
                }
            }
        }

        // Speed increase/decrease
        if (input.isUpPressed()) {
            speed = Math.min(currentMax, speed + acceleration);
        } else if (input.isDownPressed()) {
            speed = Math.max(-1.5f, speed - (deceleration * 2.0f)); // reverse/brake
        } else {
            speed = speed > 0 ? Math.max(0.0f, speed - deceleration) : Math.min(0.0f, speed + deceleration);
        }

        // Handle Nitro
        if (input.isSpacePressed() && nitroVal > 0 && fuel > 0) {
            if (!nitroActive) {
                nitroActive = true;
                SoundManager.playNitro();
            }
            nitroVal -= 0.4f;
            if (nitroVal <= 0) {
                nitroVal = 0;
                nitroActive = false;
            }
        } else {
            nitroActive = false;
            // slowly recharge nitro
            if (nitroVal < 100.0f) {
                nitroVal = Math.min(100.0f, nitroVal + 0.05f);
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    // Upgrades GUI helpers
    public boolean upgradeEngine(int cost) {
        if (engineLevel < 5) {
            engineLevel++;
            applyUpgrades();
            return true;
        }
        return false;
    }

    public boolean upgradeHandling(int cost) {
        if (handlingLevel < 5) {
            handlingLevel++;
            applyUpgrades();
            return true;
        }
        return false;
    }

    public boolean upgradeArmor(int cost) {
        if (armorLevel < 5) {
            armorLevel++;
            applyUpgrades();
            return true;
        }
        return false;
    }

    // Getters and Setters
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }
    public float getMaxSpeed() { return maxSpeed; }
    public double getTilt() { return tilt; }
    public int getCoins() { return coins; }
    public void addCoins(int amt) { this.coins += amt; }
    public void setCoins(int coins) { this.coins = coins; }
    public float getFuel() { return fuel; }
    public void setFuel(float fuel) { this.fuel = Math.min(100.0f, fuel); }
    public float getNitroVal() { return nitroVal; }
    public void setNitroVal(float nitroVal) { this.nitroVal = nitroVal; }
    public boolean isNitroActive() { return nitroActive; }

    public int getEngineLevel() { return engineLevel; }
    public int getHandlingLevel() { return handlingLevel; }
    public int getArmorLevel() { return armorLevel; }
    public void setEngineLevel(int l) { this.engineLevel = l; applyUpgrades(); }
    public void setHandlingLevel(int l) { this.handlingLevel = l; applyUpgrades(); }
    public void setArmorLevel(int l) { this.armorLevel = l; }

    public boolean hasShield() { return shieldFrames > 0; }
    public void activateShield(int duration) { this.shieldFrames = duration; }
    public int getShieldFrames() { return shieldFrames; }

    public boolean hasMagnet() { return magnetFrames > 0; }
    public void activateMagnet(int duration) { this.magnetFrames = duration; }
    public int getMagnetFrames() { return magnetFrames; }

    public boolean hasDoubleScore() { return doubleScoreFrames > 0; }
    public void activateDoubleScore(int duration) { this.doubleScoreFrames = duration; }
    public int getDoubleScoreFrames() { return doubleScoreFrames; }

    public boolean hasSlowMo() { return slowMoFrames > 0; }
    public void activateSlowMo(int duration) { this.slowMoFrames = duration; }
    public int getSlowMoFrames() { return slowMoFrames; }
}
