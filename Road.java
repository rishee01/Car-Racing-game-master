import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Road {
    public static class Decoration {
        float x, y;
        boolean isTree; // true = tree, false = street light
        float scale;

        public Decoration(float x, float y, boolean isTree) {
            this.x = x;
            this.y = y;
            this.isTree = isTree;
            this.scale = 0.8f + (float) Math.random() * 0.4f;
        }

        public void update(float roadSpeed, float slowMoFactor) {
            y += roadSpeed * slowMoFactor;
        }
    }

    public static class Obstacle {
        int type; // 0: Oil Spill, 1: Pothole, 2: Speed Breaker
        float x, y;
        int width = 48;
        int height = 24;
        boolean active = true;

        public Obstacle(int type, float x, float y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }

        public void update(float roadSpeed, float slowMoFactor) {
            y += roadSpeed * slowMoFactor;
        }

        public Rectangle getBounds() {
            return new Rectangle((int) x, (int) y, width, height);
        }
    }

    private float scrollY = 0;
    private final List<Decoration> leftDecor = new ArrayList<>();
    private final List<Decoration> rightDecor = new ArrayList<>();
    private final List<Obstacle> obstacles = new ArrayList<>();

    // Day/Night and Weather Settings
    private float dayCycleTime = 0.0f; // 0 to 1 (day -> sunset -> night -> sunrise -> day)
    private boolean rainActive = false;
    private boolean fogActive = false;
    private int decorationSpawnTimer = 0;

    public Road() {
        // Pre-populate decorations
        for (int y = 0; y < 600; y += 80) {
            leftDecor.add(new Decoration(30 + (float) Math.random() * 40, y, Math.random() > 0.3));
            rightDecor.add(new Decoration(650 + (float) Math.random() * 40, y, Math.random() > 0.3));
        }
    }

    public void update(float roadSpeed, float slowMoFactor) {
        // Scroll markings
        scrollY = (scrollY + roadSpeed * slowMoFactor) % 50;

        // Update decorations
        for (Decoration d : leftDecor) d.update(roadSpeed, slowMoFactor);
        for (Decoration d : rightDecor) d.update(roadSpeed, slowMoFactor);

        // Update obstacles
        Iterator<Obstacle> obsIt = obstacles.iterator();
        while (obsIt.hasNext()) {
            Obstacle o = obsIt.next();
            o.update(roadSpeed, slowMoFactor);
            if (o.y > 600) {
                obsIt.remove();
            }
        }

        // Clean up out of bounds decorations and recycle
        leftDecor.removeIf(d -> d.y > 600);
        rightDecor.removeIf(d -> d.y > 600);

        decorationSpawnTimer++;
        if (decorationSpawnTimer > 35) { // Spawn at intervals
            decorationSpawnTimer = 0;
            leftDecor.add(new Decoration(30 + (float) Math.random() * 40, -100, Math.random() > 0.3));
            rightDecor.add(new Decoration(650 + (float) Math.random() * 40, -100, Math.random() > 0.3));
        }

        // Progress day/night cycle
        dayCycleTime = (dayCycleTime + 0.0005f) % 1.0f;
    }

    public void spawnObstacle(int type, int lane) {
        float x = EnemyCar.getLaneX(lane) - 24; // center the 48-pixel width obstacle in lane
        obstacles.add(new Obstacle(type, x, -50));
    }

    public void draw(Graphics2D g, int width, int height) {
        // 1. Draw Grass Shoulders (Greenery)
        g.setColor(new Color(34, 139, 34));
        g.fillRect(0, 0, width, height);

        // 2. Draw Main Road Asphalt
        g.setColor(new Color(60, 60, 60));
        g.fillRect(170, 0, 400, height);

        // 3. Draw Side Guardrails / Barriers
        g.setColor(new Color(180, 180, 180));
        g.fillRect(160, 0, 10, height);
        g.fillRect(570, 0, 10, height);

        // Red/white striped hazard markings on barriers
        g.setColor(Color.RED);
        for (int y = (int) scrollY - 50; y < height; y += 40) {
            g.fillRect(160, y, 10, 20);
            g.fillRect(570, y, 10, 20);
        }
        g.setColor(Color.WHITE);
        for (int y = (int) scrollY - 30; y < height; y += 40) {
            g.fillRect(160, y, 10, 20);
            g.fillRect(570, y, 10, 20);
        }

        // 4. Draw Lanes Markings (3 center lines separating 4 lanes)
        g.setColor(new Color(255, 255, 255, 180));
        // Lane dividers at X coordinates: 270, 370, 470
        for (int y = (int) scrollY - 50; y < height; y += 50) {
            g.fillRect(268, y, 4, 25);
            g.fillRect(368, y, 4, 25);
            g.fillRect(468, y, 4, 25);
        }

        // 5. Renders Obstacles (Oil spill, speed breaker, pothole)
        for (Obstacle o : obstacles) {
            BufferedImage obsImg = ResourceManager.getObstacle(o.type);
            if (obsImg != null) {
                g.drawImage(obsImg, (int) o.x, (int) o.y, null);
            }
        }

        // 6. Draw side decorations (Trees and Streetlights)
        for (Decoration d : leftDecor) {
            drawDecoration(g, d);
        }
        for (Decoration d : rightDecor) {
            drawDecoration(g, d);
        }

        // 7. Apply Day/Night Cycle Shader Overlay
        drawAmbientLighting(g, width, height);

        // 8. Apply Weather Overlay (Fog / Rain)
        if (fogActive) {
            g.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 80), 0, height, new Color(255, 255, 255, 20)));
            g.fillRect(0, 0, width, height);
        }
    }

    private void drawDecoration(Graphics2D g, Decoration d) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (d.isTree) {
            // Shadow
            g.setColor(new Color(0, 0, 0, 60));
            g.fillOval((int) d.x - (int) (15 * d.scale) + 5, (int) d.y + (int) (30 * d.scale) - 5, (int) (30 * d.scale), (int) (15 * d.scale));

            // Trunk
            g.setColor(new Color(139, 69, 19));
            g.fillRect((int) d.x - (int) (4 * d.scale), (int) d.y + (int) (10 * d.scale), (int) (8 * d.scale), (int) (25 * d.scale));

            // Foilage (Layered circles)
            g.setPaint(new RadialGradientPaint(d.x, d.y - (5 * d.scale), 25 * d.scale, new float[]{0.0f, 1.0f}, new Color[]{new Color(50, 200, 50), new Color(10, 80, 10)}));
            g.fillOval((int) d.x - (int) (20 * d.scale), (int) d.y - (int) (25 * d.scale), (int) (40 * d.scale), (int) (40 * d.scale));
            g.fillOval((int) d.x - (int) (15 * d.scale), (int) d.y - (int) (35 * d.scale), (int) (30 * d.scale), (int) (30 * d.scale));
        } else {
            // Street Light Pole
            g.setColor(new Color(100, 100, 100));
            g.setStroke(new BasicStroke(3));
            g.drawLine((int) d.x, (int) d.y + 40, (int) d.x, (int) d.y - 10); // vertical pole
            g.drawLine((int) d.x, (int) d.y - 10, (int) d.x + (d.x < 370 ? 15 : -15), (int) d.y - 10); // horizontal arm

            // Lamp head
            g.setColor(new Color(50, 50, 50));
            g.fillRect((int) d.x + (d.x < 370 ? 10 : -20), (int) d.y - 13, 10, 5);

            // Light cone if Sunset/Night
            float darkness = getNightFactor();
            if (darkness > 0.2f) {
                int glowAlpha = (int) (darkness * 120);
                g.setPaint(new RadialGradientPaint((int) d.x + (d.x < 370 ? 15 : -15), (int) d.y - 8, 40, new float[]{0.0f, 1.0f}, new Color[]{new Color(255, 255, 180, glowAlpha), new Color(255, 255, 200, 0)}));
                g.fillOval((int) d.x + (d.x < 370 ? -15 : -45), (int) d.y - 8, 60, 120);
            }
        }
    }

    private void drawAmbientLighting(Graphics2D g, int width, int height) {
        float factor = getNightFactor();
        if (factor > 0.05f) {
            int alpha = (int) (factor * 180); // max darkness alpha
            g.setColor(new Color(5, 5, 25, alpha)); // Dark blue night overlay
            g.fillRect(0, 0, width, height);
        } else {
            float sunsetFactor = getSunsetFactor();
            if (sunsetFactor > 0.05f) {
                int alpha = (int) (sunsetFactor * 90);
                g.setColor(new Color(230, 80, 10, alpha)); // Orange-ish sunset tint
                g.fillRect(0, 0, width, height);
            }
        }
    }

    public float getNightFactor() {
        // Night is cycle between 0.4 and 0.8
        if (dayCycleTime > 0.45f && dayCycleTime < 0.85f) {
            if (dayCycleTime < 0.55f) { // sunset -> night transition
                return (dayCycleTime - 0.45f) / 0.10f;
            } else if (dayCycleTime > 0.75f) { // night -> sunrise transition
                return (0.85f - dayCycleTime) / 0.10f;
            }
            return 1.0f;
        }
        return 0.0f;
    }

    public float getSunsetFactor() {
        // Sunset is active between 0.35 and 0.50
        if (dayCycleTime > 0.35f && dayCycleTime < 0.50f) {
            if (dayCycleTime < 0.40f) {
                return (dayCycleTime - 0.35f) / 0.05f;
            } else if (dayCycleTime > 0.45f) {
                return (0.50f - dayCycleTime) / 0.05f;
            }
            return 1.0f;
        }
        return 0.0f;
    }

    public void setRain(boolean active) { this.rainActive = active; }
    public boolean isRainActive() { return rainActive; }
    public void setFog(boolean active) { this.fogActive = active; }
    public boolean isFogActive() { return fogActive; }
    public List<Obstacle> getObstacles() { return obstacles; }
}
