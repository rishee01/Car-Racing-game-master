import java.awt.*;

public class HUD {
    public void draw(Graphics2D g, Player player, ScoreManager score, int playTimeSeconds) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. TOP-CENTER SCORE & MULTIPLIER PANEL
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(270, 10, 200, 50, 10, 10);
        g.setColor(new Color(0, 200, 255));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(270, 10, 200, 50, 10, 10);

        g.setFont(new Font("Consolas", Font.BOLD, 18));
        g.setColor(Color.WHITE);
        String scoreStr = String.format("SCORE: %06d", score.getScore());
        g.drawString(scoreStr, 290, 32);

        // Multiplier display
        float mult = score.getComboMultiplier();
        if (mult > 1.0f) {
            g.setFont(new Font("Consolas", Font.BOLD, 14));
            g.setColor(Color.ORANGE);
            g.drawString(String.format("MULTIPLIER: x%.1f", mult), 290, 50);
            
            // Draw combo decay bar
            int cooldownWidth = (int) ((score.getComboCooldown() / 150.0f) * 180);
            g.setColor(new Color(255, 140, 0, 180));
            g.fillRect(280, 54, cooldownWidth, 3);
        }

        // 2. TOP-LEFT PANEL: SPEEDOMETER & FUEL
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(15, 10, 210, 75, 10, 10);
        g.setColor(new Color(0, 255, 128));
        g.drawRoundRect(15, 10, 210, 75, 10, 10);

        g.setFont(new Font("Consolas", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        int currentSpeedKph = Math.round(player.getSpeed() * 18.0f); // Conversion factor for scale
        g.drawString(String.format("SPEED: %d km/h", currentSpeedKph), 25, 30);

        // Fuel Bar
        g.setFont(new Font("Consolas", Font.PLAIN, 12));
        g.drawString("FUEL", 25, 48);
        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(65, 39, 140, 10, 4, 4);
        float fuel = player.getFuel();
        if (fuel > 30.0f) {
            g.setColor(Color.GREEN);
        } else {
            g.setColor(Color.RED);
        }
        g.fillRoundRect(65, 39, (int) (140 * (fuel / 100.0f)), 10, 4, 4);

        // Nitro Bar
        g.setColor(Color.WHITE);
        g.drawString("NITRO", 25, 68);
        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(65, 59, 140, 10, 4, 4);
        g.setColor(new Color(0, 200, 255));
        g.fillRoundRect(65, 59, (int) (140 * (player.getNitroVal() / 100.0f)), 10, 4, 4);

        // 3. TOP-RIGHT PANEL: CLOCK & DISTANCE
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(515, 10, 210, 55, 10, 10);
        g.setColor(new Color(255, 215, 0));
        g.drawRoundRect(515, 10, 210, 55, 10, 10);

        g.setFont(new Font("Consolas", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        int mins = playTimeSeconds / 60;
        int secs = playTimeSeconds % 60;
        g.drawString(String.format("TIME: %02d:%02d", mins, secs), 530, 30);
        g.drawString(String.format("DISTANCE: %.1f km", score.getDistance() / 100.0f), 530, 50);

        // 4. BOTTOM-LEFT PANEL: COINS & UPGRADES
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(15, 410, 200, 75, 10, 10);
        g.setColor(Color.WHITE);
        g.drawRoundRect(15, 410, 200, 75, 10, 10);

        g.setFont(new Font("Consolas", Font.BOLD, 14));
        g.setColor(Color.YELLOW);
        g.drawString(String.format("COINS: $%d", score.getCoinsCollected() + player.getCoins()), 25, 430);

        // Active Power-ups display
        g.setFont(new Font("Consolas", Font.PLAIN, 11));
        g.setColor(Color.WHITE);
        int py = 445;
        if (player.hasShield()) {
            drawPowerupBar(g, "SHIELD", player.getShieldFrames(), 300, py, Color.CYAN);
            py += 15;
        }
        if (player.hasMagnet()) {
            drawPowerupBar(g, "MAGNET", player.getMagnetFrames(), 300, py, Color.MAGENTA);
            py += 15;
        }
        if (player.hasDoubleScore()) {
            drawPowerupBar(g, "DOUBLE", player.getDoubleScoreFrames(), 300, py, Color.ORANGE);
            py += 15;
        }
        if (player.hasSlowMo()) {
            drawPowerupBar(g, "SLOW-MO", player.getSlowMoFrames(), 300, py, Color.BLUE);
        }

        // 5. BOTTOM-RIGHT PANEL: DYNAMIC SPEEEDOMETER DIAL
        drawAnalogSpeedometer(g, 630, 400, currentSpeedKph, player.getMaxSpeed() * 18.0f);
    }

    private void drawPowerupBar(Graphics2D g, String label, int frames, int maxFrames, int y, Color color) {
        g.drawString(label, 25, y + 8);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(80, y, 120, 8);
        g.setColor(color);
        int w = (int) ((frames / (float) maxFrames) * 120);
        g.fillRect(80, y, w, 8);
    }

    private void drawAnalogSpeedometer(Graphics2D g, int cx, int cy, int currentSpeed, float maxSpeed) {
        int r = 40;
        // Background
        g.setColor(new Color(0, 0, 0, 180));
        g.fillOval(cx, cy, r * 2, r * 2);
        g.setColor(new Color(0, 255, 128, 150));
        g.setStroke(new BasicStroke(2));
        g.drawOval(cx, cy, r * 2, r * 2);

        // Draw ticks
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.WHITE);
        for (int angle = 135; angle <= 405; angle += 30) {
            double rad = Math.toRadians(angle);
            int x1 = (int) (cx + r + Math.cos(rad) * (r - 5));
            int y1 = (int) (cy + r + Math.sin(rad) * (r - 5));
            int x2 = (int) (cx + r + Math.cos(rad) * r);
            int y2 = (int) (cy + r + Math.sin(rad) * r);
            g.drawLine(x1, y1, x2, y2);
        }

        // Draw Needle
        float ratio = Math.min(1.0f, currentSpeed / maxSpeed);
        double needleAngle = 135 + ratio * 270;
        double rad = Math.toRadians(needleAngle);
        int needleX = (int) (cx + r + Math.cos(rad) * (r - 10));
        int needleY = (int) (cy + r + Math.sin(rad) * (r - 10));

        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(cx + r, cy + r, needleX, needleY);

        g.fillOval(cx + r - 4, cy + r - 4, 8, 8);
    }
}
