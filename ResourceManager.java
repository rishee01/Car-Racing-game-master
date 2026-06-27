import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ResourceManager {
    private static BufferedImage playerCar;
    private static BufferedImage[] enemyCars = new BufferedImage[6];
    private static BufferedImage[] powerups = new BufferedImage[6]; // Nitro, Shield, Magnet, SlowMo, DoubleScore, Repair
    private static BufferedImage coin;
    private static BufferedImage[] obstacles = new BufferedImage[3]; // Oil Spill, Pothole, Speed Breaker
    private static BufferedImage roadBack1;
    private static BufferedImage roadBack2;
    private static BufferedImage startScreen;
    private static BufferedImage arrow;
    private static BufferedImage timerIcon;

    static {
        loadAssets();
    }

    private static void loadAssets() {
        // Try to load player car from file
        playerCar = loadImage("car_mask.png");
        if (playerCar == null) {
            playerCar = createPlayerCarSprite();
        }

        // Try to load road background frames
        roadBack1 = loadImage("race1.png");
        roadBack2 = loadImage("race2.png");
        if (roadBack1 == null) roadBack1 = createProceduralRoad(false);
        if (roadBack2 == null) roadBack2 = createProceduralRoad(true);

        startScreen = loadImage("startScreen.png");
        if (startScreen == null) startScreen = createProceduralStartScreen();

        arrow = loadImage("arrow.png");
        if (arrow == null) arrow = createProceduralArrow();

        timerIcon = loadImage("timer.png");
        if (timerIcon == null) timerIcon = createProceduralTimerIcon();

        // Load or create enemies
        for (int i = 0; i < enemyCars.length; i++) {
            enemyCars[i] = createEnemyCarSprite(i);
        }

        // Load or create power-ups
        for (int i = 0; i < powerups.length; i++) {
            powerups[i] = createPowerupSprite(i);
        }

        // Load or create coin
        coin = createCoinSprite();

        // Load or create obstacles
        for (int i = 0; i < obstacles.length; i++) {
            obstacles[i] = createObstacleSprite(i);
        }
    }

    private static BufferedImage loadImage(String path) {
        try {
            File file = new File(path);
            if (file.exists() && file.length() > 0) {
                return ImageIO.read(file);
            }
        } catch (Exception e) {
            System.err.println("Could not load image: " + path + " - " + e.getMessage());
        }
        return null;
    }

    public static BufferedImage getPlayerCar() { return playerCar; }
    public static BufferedImage getEnemyCar(int type) { return enemyCars[Math.abs(type) % enemyCars.length]; }
    public static BufferedImage getPowerUp(int type) { return powerups[Math.abs(type) % powerups.length]; }
    public static BufferedImage getCoin() { return coin; }
    public static BufferedImage getObstacle(int type) { return obstacles[Math.abs(type) % obstacles.length]; }
    public static BufferedImage getRoadBackground(boolean alt) { return alt ? roadBack2 : roadBack1; }
    public static BufferedImage getStartScreen() { return startScreen; }
    public static BufferedImage getArrow() { return arrow; }
    public static BufferedImage getTimerIcon() { return timerIcon; }

    // ==========================================
    // PROCEDURAL SPRITE GENERATION (GRAPHICS2D)
    // ==========================================

    private static BufferedImage createPlayerCarSprite() {
        BufferedImage img = new BufferedImage(36, 57, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRoundRect(2, 4, 32, 51, 8, 8);

        // Body (Sleek red sports car)
        g.setPaint(new GradientPaint(18, 0, new Color(220, 20, 60), 18, 57, new Color(139, 0, 0)));
        g.fillRoundRect(3, 2, 30, 53, 10, 10);

        // Spoiler
        g.setColor(new Color(40, 40, 40));
        g.fillRect(2, 50, 32, 4);
        g.fillRect(4, 46, 2, 4);
        g.fillRect(30, 46, 2, 4);

        // Windows (Glassmorphism dark look)
        g.setColor(new Color(30, 30, 30));
        g.fillRoundRect(6, 15, 24, 18, 5, 5);
        g.setColor(new Color(100, 200, 255, 120)); // reflection
        g.fillPolygon(new int[]{7, 18, 7}, new int[]{17, 17, 30}, 3);

        // Racing Stripes
        g.setColor(Color.WHITE);
        g.fillRect(12, 2, 3, 13);
        g.fillRect(21, 2, 3, 13);
        g.fillRect(12, 33, 3, 17);
        g.fillRect(21, 33, 3, 17);

        // Headlights
        g.setColor(Color.YELLOW);
        g.fillOval(5, 4, 6, 4);
        g.fillOval(25, 4, 6, 4);

        // Taillights
        g.setColor(Color.RED);
        g.fillRect(5, 52, 6, 2);
        g.fillRect(25, 52, 6, 2);

        // Wheels
        g.setColor(new Color(10, 10, 10));
        g.fillRect(1, 10, 3, 10);
        g.fillRect(32, 10, 3, 10);
        g.fillRect(1, 40, 3, 10);
        g.fillRect(32, 40, 3, 10);

        g.dispose();
        return img;
    }

    private static BufferedImage createEnemyCarSprite(int type) {
        BufferedImage img = new BufferedImage(36, 57, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow
        g.setColor(new Color(0, 0, 0, 80));
        g.fillRoundRect(2, 4, 32, 51, 8, 8);

        Color mainColor = Color.GRAY;
        Color detailColor = Color.DARK_GRAY;
        boolean isSpecial = false;

        switch (type) {
            case 0: // Sports (Blue)
                mainColor = new Color(30, 144, 255);
                detailColor = new Color(0, 0, 128);
                break;
            case 1: // Taxi (Yellow)
                mainColor = new Color(255, 215, 0);
                detailColor = Color.BLACK;
                break;
            case 2: // Truck (Green / Cargo)
                mainColor = new Color(46, 139, 87);
                detailColor = new Color(47, 79, 79);
                break;
            case 3: // Ambulance (White / Red Cross)
                mainColor = Color.WHITE;
                detailColor = new Color(220, 20, 60);
                isSpecial = true;
                break;
            case 4: // Police (Black and White)
                mainColor = new Color(20, 20, 20);
                detailColor = Color.WHITE;
                isSpecial = true;
                break;
            case 5: // Sedan (Violet)
                mainColor = new Color(138, 43, 226);
                detailColor = new Color(75, 0, 130);
                break;
        }

        // Draw body
        g.setPaint(new GradientPaint(18, 0, mainColor, 18, 57, mainColor.darker()));
        g.fillRoundRect(4, 3, 28, 51, 6, 6);

        // Wheels
        g.setColor(Color.BLACK);
        g.fillRect(1, 10, 4, 9);
        g.fillRect(31, 10, 4, 9);
        g.fillRect(1, 38, 4, 9);
        g.fillRect(31, 38, 4, 9);

        // Windshield and Windows
        g.setColor(new Color(40, 40, 40));
        g.fillRoundRect(7, 16, 22, 18, 4, 4);
        g.setColor(new Color(200, 220, 255, 100)); // Reflection
        g.fillRect(9, 18, 8, 14);

        if (type == 1) { // Taxi stripe
            g.setColor(Color.BLACK);
            g.fillRect(4, 25, 28, 6);
            g.setColor(Color.WHITE);
            for (int x = 6; x < 32; x += 6) {
                g.fillRect(x, 26, 3, 4);
            }
        } else if (type == 2) { // Truck Cabin separation
            g.setColor(new Color(30, 30, 30));
            g.fillRect(4, 18, 28, 3);
            g.setColor(new Color(100, 100, 100));
            g.fillRect(6, 20, 24, 32); // Cargo trunk
        } else if (type == 3) { // Ambulance Red Cross
            g.setColor(Color.RED);
            g.fillRect(16, 28, 4, 12);
            g.fillRect(12, 32, 12, 4);
            // Flashing Lights
            g.setColor(Color.BLUE);
            g.fillOval(9, 10, 5, 5);
            g.setColor(Color.RED);
            g.fillOval(22, 10, 5, 5);
        } else if (type == 4) { // Police pattern
            g.setColor(Color.WHITE);
            g.fillRect(4, 16, 4, 15);
            g.fillRect(28, 16, 4, 15);
            // Police light bar
            g.setColor(Color.RED);
            g.fillRect(10, 14, 8, 3);
            g.setColor(Color.BLUE);
            g.fillRect(18, 14, 8, 3);
        }

        // Headlights
        g.setColor(new Color(255, 250, 200));
        g.fillOval(6, 4, 5, 3);
        g.fillOval(25, 4, 5, 3);

        g.dispose();
        return img;
    }

    private static BufferedImage createPowerupSprite(int type) {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color color = Color.WHITE;
        switch (type) {
            case 0: color = Color.RED; break;       // Nitro
            case 1: color = Color.CYAN; break;      // Shield
            case 2: color = Color.MAGENTA; break;   // Magnet
            case 3: color = Color.BLUE; break;      // Slowmo
            case 4: color = Color.ORANGE; break;    // Double
            case 5: color = Color.GREEN; break;     // Repair
        }

        // Draw glowing background circle
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60));
        g.fillOval(2, 2, 28, 28);
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 180));
        g.setStroke(new BasicStroke(2));
        g.drawOval(4, 4, 24, 24);

        g.setColor(Color.WHITE);
        if (type == 0) { // Nitro: Fire flame
            g.fillPolygon(new int[]{16, 12, 14, 16, 18, 20}, new int[]{8, 18, 24, 22, 24, 18}, 6);
        } else if (type == 1) { // Shield: Cross/bubble
            g.setStroke(new BasicStroke(3));
            g.drawArc(10, 10, 12, 12, 0, 360);
            g.drawLine(16, 10, 16, 22);
            g.drawLine(10, 16, 22, 16);
        } else if (type == 2) { // Magnet: U-shape
            g.setStroke(new BasicStroke(4));
            g.drawArc(10, 10, 12, 12, 180, 180);
            g.drawLine(10, 16, 10, 22);
            g.drawLine(22, 16, 22, 22);
        } else if (type == 3) { // Slowmo: Hourglass
            g.fillPolygon(new int[]{10, 22, 10, 22}, new int[]{9, 9, 23, 23}, 4);
            g.fillRect(10, 8, 12, 2);
            g.fillRect(10, 22, 12, 2);
        } else if (type == 4) { // Double: '2X'
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("2X", 8, 21);
        } else if (type == 5) { // Repair: Wrench
            g.setStroke(new BasicStroke(3));
            g.drawLine(10, 22, 20, 12);
            g.fillOval(18, 9, 5, 5);
        }

        g.dispose();
        return img;
    }

    private static BufferedImage createCoinSprite() {
        BufferedImage img = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gold coin
        g.setPaint(new RadialGradientPaint(12, 12, 10, new float[]{0.0f, 1.0f}, new Color[]{new Color(255, 230, 100), new Color(204, 153, 0)}));
        g.fillOval(2, 2, 20, 20);

        g.setColor(new Color(255, 255, 255, 200));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(4, 4, 16, 16);

        // Coin dollar sign or star inside
        g.setColor(new Color(153, 102, 0));
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("$", 8, 17);

        g.dispose();
        return img;
    }

    private static BufferedImage createObstacleSprite(int type) {
        BufferedImage img = new BufferedImage(48, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (type == 0) { // Oil Spill (Black puddle)
            g.setColor(new Color(20, 20, 20, 220));
            g.fillOval(4, 4, 40, 16);
            g.fillOval(10, 2, 20, 12);
            g.setColor(new Color(60, 60, 60, 120)); // shiny reflection
            g.drawArc(8, 6, 32, 10, 150, 120);
        } else if (type == 1) { // Pothole (Crater)
            g.setColor(new Color(40, 30, 20, 240));
            g.fillOval(6, 5, 36, 14);
            g.setColor(new Color(10, 5, 0));
            g.fillOval(10, 8, 28, 9);
        } else { // Speed Breaker (Yellow/Black stripes)
            for (int i = 0; i < 48; i += 8) {
                g.setColor((i / 8) % 2 == 0 ? Color.YELLOW : Color.BLACK);
                g.fillRect(i, 4, 8, 16);
            }
        }

        g.dispose();
        return img;
    }

    private static BufferedImage createProceduralRoad(boolean alt) {
        BufferedImage img = new BufferedImage(740, 500, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Grass side shoulders (Green)
        g.setColor(new Color(34, 139, 34));
        g.fillRect(0, 0, 740, 500);

        // Road center (Asphalt dark grey)
        g.setColor(new Color(80, 80, 80));
        g.fillRect(170, 0, 400, 500);

        // Side lane markers (White solid lines)
        g.setColor(Color.WHITE);
        g.fillRect(170, 0, 5, 500);
        g.fillRect(565, 0, 5, 500);

        // Dash center markings (Yellow dotted lines)
        g.setColor(Color.YELLOW);
        int offset = alt ? 25 : 0;
        for (int y = -50 + offset; y < 500; y += 50) {
            g.fillRect(367, y, 6, 25);
        }

        g.dispose();
        return img;
    }

    private static BufferedImage createProceduralStartScreen() {
        BufferedImage img = new BufferedImage(740, 500, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Cool retro arcade gradient background
        g.setPaint(new GradientPaint(0, 0, new Color(20, 20, 40), 740, 500, new Color(10, 10, 15)));
        g.fillRect(0, 0, 740, 500);

        g.dispose();
        return img;
    }

    private static BufferedImage createProceduralArrow() {
        BufferedImage img = new BufferedImage(200, 150, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Transparent container
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRoundRect(0, 0, 200, 150, 15, 15);

        // Draw arrow-keys layout
        g.setColor(Color.WHITE);
        g.drawRoundRect(10, 10, 180, 130, 10, 10);

        g.dispose();
        return img;
    }

    private static BufferedImage createProceduralTimerIcon() {
        BufferedImage img = new BufferedImage(50, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Clock outline
        g.setColor(Color.WHITE);
        g.drawOval(2, 2, 16, 16);
        g.drawLine(10, 10, 10, 5);
        g.drawLine(10, 10, 14, 10);

        g.dispose();
        return img;
    }
}
