import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    // Game Loop Timer (16ms ≈ 60 FPS)
    private final Timer gameTimer;
    private GameState state = GameState.MENU;

    // Core references
    private final Player player;
    private final Road road;
    private final ScoreManager scoreManager;
    private final HUD hud;
    private final InputManager inputManager;

    // Entity Lists
    private final List<EnemyCar> enemies = new ArrayList<>();
    private final List<PowerUp> powerups = new ArrayList<>();
    private final List<Point> coins = new ArrayList<>(); // x, y points
    private final List<Particle> particles = new ArrayList<>();

    // Spawning timers
    private int enemySpawnTimer = 0;
    private int powerupSpawnTimer = 0;
    private int obstacleSpawnTimer = 0;
    private int coinSpawnTimer = 0;

    // Gameplay timers & difficulty variables
    private int playTimeFrames = 0;
    private int difficultyLevel = 1;
    private int difficultyTimer = 0; // seconds
    private float roadSpeed = 5.0f;
    private float trafficSpeedBase = 2.0f;

    // Countdown state
    private int countdownVal = 3;
    private int countdownFrames = 0;

    // Screen Shake & Camera Zoom
    private float screenShakeIntensity = 0.0f;
    private float cameraZoom = 1.0f;

    // Rain & Weather
    private int rainSpawnCooldown = 0;
    private boolean nightMode = false;
    private boolean rainMode = false;

    // Slide effect (Oil spill)
    private int slideFrames = 0;
    private float slideDir = 0;

    // Menu selections and UI hover tracking
    private Rectangle[] menuButtons;
    private Rectangle[] pauseButtons;
    private Rectangle[] gameOverButtons;
    private Rectangle[] settingsButtons;
    private Rectangle[] highscoreButtons;
    private Rectangle[] garageButtons;
    private Rectangle[] carSelectionButtons;
    private int hoveredButtonIndex = -1;

    // Name input for high scores
    private String playerNameInput = "Player";
    private boolean typingActive = false;

    // Player Car Skins
    private int activeCarSkin = 0; // 0: Red, 1: Yellow, 2: Blue

    public GamePanel(InputManager input) {
        this.inputManager = input;
        this.player = new Player(350, 400);
        this.road = new Road();
        this.scoreManager = new ScoreManager();
        this.hud = new HUD();

        setPreferredSize(new Dimension(740, 500));
        setBackground(Color.DARK_GRAY);
        setDoubleBuffered(true);

        addMouseListener(this);
        addMouseMotionListener(this);

        // Define Menu Button Rectangles
        initButtons();

        // Start 60 FPS Game Loop
        this.gameTimer = new Timer(16, this);
        this.gameTimer.start();

        // Start Background Music
        SoundManager.startBGM();
    }

    private void initButtons() {
        // MENU: Play (0), Selection (1), Garage (2), High Scores (3), Settings (4), Exit (5)
        menuButtons = new Rectangle[6];
        for (int i = 0; i < 6; i++) {
            menuButtons[i] = new Rectangle(270, 180 + i * 45, 200, 35);
        }

        // PAUSED: Resume (0), Restart (1), Main Menu (2)
        pauseButtons = new Rectangle[3];
        for (int i = 0; i < 3; i++) {
            pauseButtons[i] = new Rectangle(270, 200 + i * 50, 200, 35);
        }

        // GAME OVER: Submit & Restart (0), Main Menu (1)
        gameOverButtons = new Rectangle[2];
        gameOverButtons[0] = new Rectangle(180, 380, 170, 40);
        gameOverButtons[1] = new Rectangle(390, 380, 170, 40);

        // SETTINGS: Toggle Music (0), Toggle SFX (1), Back (2)
        settingsButtons = new Rectangle[3];
        settingsButtons[0] = new Rectangle(220, 200, 300, 40);
        settingsButtons[1] = new Rectangle(220, 260, 300, 40);
        settingsButtons[2] = new Rectangle(270, 380, 200, 40);

        // HIGH SCORES: Back (0)
        highscoreButtons = new Rectangle[1];
        highscoreButtons[0] = new Rectangle(270, 420, 200, 40);

        // GARAGE: Upgrade Engine (0), Upgrade Handling (1), Upgrade Armor (2), Back (3)
        garageButtons = new Rectangle[4];
        garageButtons[0] = new Rectangle(80, 350, 160, 40);
        garageButtons[1] = new Rectangle(290, 350, 160, 40);
        garageButtons[2] = new Rectangle(500, 350, 160, 40);
        garageButtons[3] = new Rectangle(270, 430, 200, 40);

        // CAR SELECTION: Red Car (0), Yellow Car (1), Blue Car (2), Select & Back (3)
        carSelectionButtons = new Rectangle[4];
        carSelectionButtons[0] = new Rectangle(120, 250, 100, 40);
        carSelectionButtons[1] = new Rectangle(320, 250, 100, 40);
        carSelectionButtons[2] = new Rectangle(520, 250, 100, 40);
        carSelectionButtons[3] = new Rectangle(270, 380, 200, 40);
    }

    private void restartGame() {
        SoundManager.stopEngineHum();
        player.setX(350);
        player.setY(400);
        player.setSpeed(0);
        player.setFuel(100);
        player.setNitroVal(50);
        player.activateShield(0);
        player.activateMagnet(0);
        player.activateDoubleScore(0);
        player.activateSlowMo(0);

        enemies.clear();
        powerups.clear();
        coins.clear();
        particles.clear();
        road.getObstacles().clear();

        playTimeFrames = 0;
        difficultyLevel = 1;
        difficultyTimer = 0;
        roadSpeed = 5.0f;
        trafficSpeedBase = 2.0f;

        scoreManager.addScore(-scoreManager.getScore()); // Reset score
        scoreManager.addCoins(-scoreManager.getCoinsCollected()); // Reset collected coins

        state = GameState.COUNTDOWN;
        countdownVal = 3;
        countdownFrames = 0;
        SoundManager.playBeep(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    private void update() {
        // Decay screen shake
        if (screenShakeIntensity > 0.0f) {
            screenShakeIntensity = Math.max(0.0f, screenShakeIntensity - 0.3f);
        }
        // Toggle Mute globally via keyboard check
        if (inputManager.checkMuteRequested()) {
            SoundManager.toggleMute();
        }

        // Toggle Screen Capture (Screenshot)
        if (inputManager.checkScreenshotRequested()) {
            triggerScreenshot();
        }

        switch (state) {
            case MENU:
                updateMenuBackground();
                break;
            case COUNTDOWN:
                updateCountdown();
                break;
            case PLAYING:
                updateGameplay();
                break;
            case PAUSED:
                if (inputManager.checkPauseRequested()) {
                    state = GameState.PLAYING;
                    SoundManager.startEngineHum();
                }
                break;
            case GAME_OVER:
                updateExplosionsOnly();
                break;
            case SETTINGS:
            case HIGH_SCORE:
            case GARAGE:
            case CAR_SELECTION:
                break;
        }
    }

    private void updateMenuBackground() {
        // Scroll road background slowly
        road.update(1.5f, 1.0f);
        updateParticles(1.0f);
    }

    private void updateCountdown() {
        countdownFrames++;
        if (countdownFrames >= 60) {
            countdownFrames = 0;
            countdownVal--;
            if (countdownVal > 0) {
                SoundManager.playBeep(false);
            } else if (countdownVal == 0) {
                SoundManager.playBeep(true);
            } else {
                state = GameState.PLAYING;
                SoundManager.startEngineHum();
            }
        }
        road.update(2.0f, 1.0f);
        player.update(inputManager, true);
        updateParticles(1.0f);
    }

    private void updateGameplay() {
        // Handle ESC key to pause
        if (inputManager.checkPauseRequested()) {
            state = GameState.PAUSED;
            SoundManager.stopEngineHum();
            return;
        }

        // Slow motion factor
        float slowMoFactor = player.hasSlowMo() ? 0.4f : 1.0f;

        // Upgrades modifications
        int armorLevel = player.getArmorLevel();


        // Calculate dynamic zoom based on player speed and nitro
        if (player.isNitroActive()) {
            cameraZoom = cameraZoom + (0.95f - cameraZoom) * 0.05f; // Zoom out slightly
        } else {
            cameraZoom = cameraZoom + (1.00f - cameraZoom) * 0.05f;
        }

        // Apply slide physics (if oil spill triggered)
        if (slideFrames > 0) {
            slideFrames--;
            player.setX(Math.max(185, Math.min(520, player.getX() + slideDir)));
        }

        // Update player
        player.update(inputManager, false);

        // Engine sound frequency update
        SoundManager.setEngineSpeed(player.getSpeed() / player.getMaxSpeed());

        // Scrolling speed scales with player's velocity
        roadSpeed = player.getSpeed();
        road.update(roadSpeed, slowMoFactor);

        // Increment time
        playTimeFrames++;
        if (playTimeFrames % 60 == 0) {
            difficultyTimer++;
            scoreManager.update(player.getSpeed());

            // Difficulty progression every 20 seconds
            if (difficultyTimer % 20 == 0 && difficultyLevel < 15) {
                difficultyLevel++;
                trafficSpeedBase += 0.5f;
                particles.add(new Particle("DIFFICULTY UP!", 270, 200, -2.0f, 0.02f, Color.RED, 24));
            }
        }

        // Spawning Routines
        handleSpawning(slowMoFactor);

        // Update traffic enemies
        Iterator<EnemyCar> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            EnemyCar enemy = enemyIt.next();
            enemy.update(roadSpeed, slowMoFactor, enemies);

            // Clean up off-screen
            if (enemy.getY() > 600 || enemy.getY() < -150) {
                enemyIt.remove();
                continue;
            }

            // Overtake scoring check
            if (enemy.getY() > player.getY() + 10 && enemy.getY() < player.getY() + 15) {
                scoreManager.triggerOvertake();
                particles.add(new Particle("OVERTAKE +50", player.getX(), player.getY() - 30, -2.0f, 0.03f, Color.GREEN, 14));
            }

            // Near Miss check
            if (Math.abs(enemy.getX() - player.getX()) < 65 && Math.abs(enemy.getY() - player.getY()) < 50) {
                // If it passes close but hasn't intersected, count as near miss
                if (!enemy.getBounds().intersects(player.getBounds()) && playTimeFrames % 30 == 0) {
                    scoreManager.triggerNearMiss();
                    particles.add(new Particle("NEAR MISS x" + scoreManager.getComboMultiplier(), player.getX() - 10, player.getY() - 40, -3.0f, 0.02f, Color.ORANGE, 16));
                }
            }
        }

        // Update Power-ups
        Iterator<PowerUp> puIt = powerups.iterator();
        while (puIt.hasNext()) {
            PowerUp pu = puIt.next();
            pu.update(roadSpeed, slowMoFactor);
            if (pu.getY() > 600) {
                puIt.remove();
            }
        }

        // Update Coins
        Iterator<Point> coinIt = coins.iterator();
        while (coinIt.hasNext()) {
            Point coinPt = coinIt.next();
            coinPt.y += (int) (roadSpeed * slowMoFactor);

            // Magnet Attraction Physics
            if (player.hasMagnet()) {
                double dist = Math.hypot(player.getX() + 18 - coinPt.x, player.getY() + 28 - coinPt.y);
                if (dist < 150) {
                    coinPt.x += (int) (((player.getX() + 18 - coinPt.x) / dist) * 8.0);
                    coinPt.y += (int) (((player.getY() + 28 - coinPt.y) / dist) * 8.0);
                }
            }

            if (coinPt.y > 600) {
                coinIt.remove();
            }
        }

        // Collisions Engine
        checkCollisions(armorLevel);

        // Exhaust smoke particles & Nitro flames
        if (roadSpeed > 1.0f && playTimeFrames % 3 == 0) {
            particles.add(new Particle(Particle.ParticleType.SMOKE, player.getX() + 8, player.getY() + 55, -0.5f + (float) Math.random(), 3.0f, 0.05f, Color.GRAY, 8));
            particles.add(new Particle(Particle.ParticleType.SMOKE, player.getX() + 28, player.getY() + 55, -0.5f + (float) Math.random(), 3.0f, 0.05f, Color.GRAY, 8));

            if (player.isNitroActive()) {
                // Add glowing blue exhaust flames
                particles.add(new Particle(Particle.ParticleType.FLAME, player.getX() + 8, player.getY() + 58, 0, 5.0f, 0.08f, Color.CYAN, 12));
                particles.add(new Particle(Particle.ParticleType.FLAME, player.getX() + 28, player.getY() + 58, 0, 5.0f, 0.08f, Color.CYAN, 12));
                // Add speed lines at borders
                particles.add(new Particle(Particle.ParticleType.SPEED_LINE, (float) Math.random() * 740, 0, 0, 18.0f, 0.03f, new Color(255, 255, 255, 100), 40 + (float) Math.random() * 50));
            }
        }

        // Update Weather visuals
        updateWeatherVisuals(slowMoFactor);

        // Update particles
        updateParticles(slowMoFactor);
    }

    private void updateExplosionsOnly() {
        // Even when game over, let particles finish playing
        updateParticles(1.0f);
    }

    private void updateParticles(float slowMoFactor) {
        Iterator<Particle> pIt = particles.iterator();
        while (pIt.hasNext()) {
            Particle p = pIt.next();
            if (!p.update(slowMoFactor)) {
                pIt.remove();
            }
        }
    }

    private void updateWeatherVisuals(float slowMo) {
        if (rainMode) {
            rainSpawnCooldown--;
            if (rainSpawnCooldown <= 0) {
                rainSpawnCooldown = 3;
                // Add rain droplet particles
                for (int i = 0; i < 4; i++) {
                    particles.add(new Particle(Particle.ParticleType.RAIN, (float) Math.random() * 740, 0, -2.0f, 15.0f, 0.02f, new Color(150, 200, 255, 150), 15));
                }
            }
        }
    }

    private void handleSpawning(float slowMo) {
        // Spawning intervals progression based on difficulty
        int spawnInterval = Math.max(25, 75 - (difficultyLevel * 3)); // frames; 1.25s scaling to 0.4s

        // 1. Spawns enemy traffic
        enemySpawnTimer++;
        if (enemySpawnTimer > spawnInterval) {
            enemySpawnTimer = 0;

            int lane = (int) (Math.random() * 4);
            int type = (int) (Math.random() * 6);

            // Avoid spawning enemy car on top of another
            boolean laneBlocked = false;
            for (EnemyCar other : enemies) {
                if (other.getX() == EnemyCar.getLaneX(lane) && other.getY() < 80) {
                    laneBlocked = true;
                    break;
                }
            }

            if (!laneBlocked) {
                enemies.add(new EnemyCar(type, lane, -100, trafficSpeedBase));
            }
        }

        // 2. Spawns power-ups
        powerupSpawnTimer++;
        if (powerupSpawnTimer > 500) { // ~8 seconds
            powerupSpawnTimer = 0;
            int type = (int) (Math.random() * 6);
            float px = 200 + (float) Math.random() * 320;
            powerups.add(new PowerUp(type, px, -50));
        }

        // 3. Spawns coins in columns
        coinSpawnTimer++;
        if (coinSpawnTimer > 180) { // ~3 seconds
            coinSpawnTimer = 0;
            int lane = (int) (Math.random() * 4);
            int startY = -150;
            float px = EnemyCar.getLaneX(lane);
            // Spawn a column of 4 coins
            for (int i = 0; i < 4; i++) {
                coins.add(new Point((int) px, startY + i * 35));
            }
        }

        // 4. Spawns obstacles (Oil spill, speed breaker, pothole)
        obstacleSpawnTimer++;
        if (obstacleSpawnTimer > 350) { // ~6 seconds
            obstacleSpawnTimer = 0;
            int type = (int) (Math.random() * 3);
            int lane = (int) (Math.random() * 4);
            road.spawnObstacle(type, lane);
        }
    }

    private void checkCollisions(int armorLevel) {
        Rectangle pRect = player.getBounds();

        // 1. Player with Enemy cars
        Iterator<EnemyCar> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            EnemyCar enemy = enemyIt.next();
            if (pRect.intersects(enemy.getBounds())) {
                if (player.hasShield()) {
                    // Shield protects and destroys enemy
                    enemyIt.remove();
                    player.activateShield(0); // Consume shield
                    triggerExplosionSparks(enemy.getX() + 18, enemy.getY() + 28, Color.CYAN);
                    screenShakeIntensity = 6.0f;
                    scoreManager.addScore(200);
                    particles.add(new Particle("SHIELD COLLISION! +200", player.getX() - 30, player.getY() - 40, -2.0f, 0.02f, Color.CYAN, 14));
                    SoundManager.playCrash();
                } else {
                    // Critical Crash!
                    triggerCrashGameOver();
                }
                return;
            }
        }

        // 2. Player with Obstacles
        for (Road.Obstacle o : road.getObstacles()) {
            if (o.active && pRect.intersects(o.getBounds())) {
                o.active = false;
                if (o.type == 0) { // Oil Spill: Slide out
                    slideFrames = 40;
                    slideDir = Math.random() > 0.5f ? 4.0f : -4.0f;
                    particles.add(new Particle("SLIDING!", player.getX(), player.getY() - 30, -2.0f, 0.03f, Color.YELLOW, 14));
                } else if (o.type == 1) { // Pothole: Speed hit + shake
                    player.setSpeed(player.getSpeed() * 0.4f);
                    screenShakeIntensity = 8.0f;
                    particles.add(new Particle("POTHOLE DAMAGE", player.getX(), player.getY() - 30, -2.0f, 0.03f, Color.RED, 14));
                    SoundManager.playCrash();
                } else if (o.type == 2) { // Speed Breaker: Heavy brake
                    player.setSpeed(player.getSpeed() * 0.25f);
                    particles.add(new Particle("SPEED BREAKER", player.getX(), player.getY() - 30, -2.0f, 0.03f, Color.ORANGE, 14));
                }
            }
        }

        // 3. Player with Power-ups
        Iterator<PowerUp> puIt = powerups.iterator();
        while (puIt.hasNext()) {
            PowerUp pu = puIt.next();
            if (pRect.intersects(pu.getBounds())) {
                activatePowerupEffect(pu.getType());
                puIt.remove();
                SoundManager.playClick();
            }
        }

        // 4. Player with Coins
        Iterator<Point> coinIt = coins.iterator();
        while (coinIt.hasNext()) {
            Point coinPt = coinIt.next();
            Rectangle cRect = new Rectangle(coinPt.x - 12, coinPt.y - 12, 24, 24);
            if (pRect.intersects(cRect)) {
                int coinVal = player.hasDoubleScore() ? 2 : 1;
                scoreManager.addCoins(coinVal);
                scoreManager.addScore(50 * coinVal);
                particles.add(new Particle(String.format("+$%d", coinVal), coinPt.x, coinPt.y, -3.0f, 0.04f, Color.YELLOW, 14));
                coinIt.remove();
                SoundManager.playClick();
            }
        }
    }

    private void activatePowerupEffect(int type) {
        switch (type) {
            case 0: // Nitro
                player.setNitroVal(Math.min(100.0f, player.getNitroVal() + 40.0f));
                particles.add(new Particle("NITRO REFILL!", player.getX(), player.getY() - 30, -2.0f, 0.02f, Color.RED, 14));
                break;
            case 1: // Shield
                player.activateShield(300); // 5 seconds
                particles.add(new Particle("SHIELD ACTIVE", player.getX(), player.getY() - 30, -2.0f, 0.02f, Color.CYAN, 14));
                break;
            case 2: // Magnet
                player.activateMagnet(400); // 6.6 seconds
                particles.add(new Particle("COIN MAGNET", player.getX(), player.getY() - 30, -2.0f, 0.02f, Color.MAGENTA, 14));
                break;
            case 3: // SlowMo
                player.activateSlowMo(400);
                particles.add(new Particle("SLOW MOTION", player.getX(), player.getY() - 30, -2.0f, 0.02f, Color.BLUE, 14));
                break;
            case 4: // Double Score
                player.activateDoubleScore(300);
                particles.add(new Particle("DOUBLE SCORE", player.getX(), player.getY() - 30, -2.0f, 0.02f, Color.ORANGE, 14));
                break;
            case 5: // Repair
                player.setFuel(100.0f);
                particles.add(new Particle("FUEL RESTORED", player.getX(), player.getY() - 30, -2.0f, 0.02f, Color.GREEN, 14));
                break;
        }
    }

    private void triggerExplosionSparks(float cx, float cy, Color col) {
        for (int i = 0; i < 35; i++) {
            float vx = -6.0f + (float) Math.random() * 12.0f;
            float vy = -6.0f + (float) Math.random() * 12.0f;
            particles.add(new Particle(Particle.ParticleType.SPARK, cx, cy, vx, vy, 0.02f + (float) Math.random() * 0.03f, col, 4 + (float) Math.random() * 5));
        }
    }

    private void triggerCrashGameOver() {
        state = GameState.GAME_OVER;
        SoundManager.stopEngineHum();
        screenShakeIntensity = 18.0f;
        SoundManager.playCrash();

        // Massive fireballs
        triggerExplosionSparks(player.getX() + 18, player.getY() + 28, Color.RED);
        triggerExplosionSparks(player.getX() + 18, player.getY() + 28, Color.ORANGE);
        triggerExplosionSparks(player.getX() + 18, player.getY() + 28, Color.YELLOW);

        // Store persistent score immediately
        ScoreManager.submitScore(scoreManager.getScore(), scoreManager.getDistance(), scoreManager.getCoinsCollected(), playTimeFrames / 60);

        // Retrieve earned coins and save to player state
        player.addCoins(scoreManager.getCoinsCollected());
    }

    // Paint core routine
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Apply Screen Shake transform if active
        if (screenShakeIntensity > 0.0f) {
            int dx = (int) (-screenShakeIntensity + Math.random() * (screenShakeIntensity * 2));
            int dy = (int) (-screenShakeIntensity + Math.random() * (screenShakeIntensity * 2));
            g2.translate(dx, dy);
        }

        // Apply Camera zoom relative to car center
        if (cameraZoom != 1.0f) {
            double cx = player.getX() + 18;
            double cy = player.getY() + 28;
            g2.translate(cx, cy);
            g2.scale(cameraZoom, cameraZoom);
            g2.translate(-cx, -cy);
        }

        switch (state) {
            case MENU:
                road.draw(g2, getWidth(), getHeight());
                drawParticles(g2);
                drawMenuOverlay(g2);
                break;
            case COUNTDOWN:
                road.draw(g2, getWidth(), getHeight());
                drawPlayerCar(g2);
                drawParticles(g2);
                drawCountdownOverlay(g2);
                break;
            case PLAYING:
            case PAUSED:
                road.draw(g2, getWidth(), getHeight());
                drawCoins(g2);
                drawPowerups(g2);
                drawEnemies(g2);
                drawPlayerCar(g2);
                drawParticles(g2);
                hud.draw(g2, player, scoreManager, playTimeFrames / 60);

                if (state == GameState.PAUSED) {
                    drawPauseOverlay(g2);
                }
                break;
            case GAME_OVER:
                road.draw(g2, getWidth(), getHeight());
                drawEnemies(g2);
                drawParticles(g2);
                drawGameOverOverlay(g2);
                break;
            case SETTINGS:
                drawSettingsScreen(g2);
                break;
            case HIGH_SCORE:
                drawHighscoreScreen(g2);
                break;
            case GARAGE:
                drawGarageScreen(g2);
                break;
            case CAR_SELECTION:
                drawCarSelectionScreen(g2);
                break;
        }
    }

    private void drawPlayerCar(Graphics2D g) {
        g.translate(player.getX() + 18, player.getY() + 28);
        g.rotate(Math.toRadians(player.getTilt()));

        BufferedImage skin = ResourceManager.getPlayerCar();
        if (skin != null) {
            g.drawImage(skin, -18, -28, null);
        }

        // Renders Shield bubble if active
        if (player.hasShield()) {
            g.setColor(new Color(0, 255, 255, 80));
            g.fillOval(-25, -35, 50, 70);
            g.setColor(Color.CYAN);
            g.setStroke(new BasicStroke(2));
            g.drawOval(-25, -35, 50, 70);
        }

        g.rotate(-Math.toRadians(player.getTilt()));
        g.translate(-(player.getX() + 18), -(player.getY() + 28));
    }

    private void drawEnemies(Graphics2D g) {
        for (EnemyCar enemy : enemies) {
            BufferedImage img = ResourceManager.getEnemyCar(enemy.getType());
            if (img != null) {
                g.drawImage(img, (int) enemy.getX(), (int) enemy.getY(), null);
            }
        }
    }

    private void drawPowerups(Graphics2D g) {
        for (PowerUp pu : powerups) {
            BufferedImage img = ResourceManager.getPowerUp(pu.getType());
            if (img != null) {
                g.drawImage(img, (int) pu.getX(), (int) (pu.getY() + pu.getAnimOffset()), null);
            }
        }
    }

    private void drawCoins(Graphics2D g) {
        BufferedImage coinImg = ResourceManager.getCoin();
        for (Point pt : coins) {
            g.drawImage(coinImg, pt.x - 12, pt.y - 12, null);
        }
    }

    private void drawParticles(Graphics2D g) {
        for (Particle p : particles) {
            p.render(g);
        }
    }

    // ==========================================
    // OVERLAYS AND PAGES DRAWING METHODS
    // ==========================================

    private void drawMenuOverlay(Graphics2D g) {
        // Dark translucent panel
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Neon blue outline frame
        g.setColor(new Color(0, 150, 255));
        g.setStroke(new BasicStroke(4));
        g.drawRoundRect(20, 20, getWidth() - 40, getHeight() - 40, 20, 20);

        // Game Title
        g.setFont(new Font("Impact", Font.ITALIC, 54));
        g.setColor(Color.RED);
        g.drawString("ARCADE RACER", 225, 95);
        g.setFont(new Font("Impact", Font.ITALIC, 54));
        g.setColor(Color.WHITE);
        g.drawString("ARCADE RACER", 222, 92);

        g.setFont(new Font("Consolas", Font.PLAIN, 16));
        g.setColor(Color.YELLOW);
        g.drawString("SPEEDWAY TRAFFIC MODE", 280, 130);

        // Buttons
        String[] labels = {"PLAY GAME", "SELECT CAR", "GARAGE", "LEADERBOARD", "SETTINGS", "EXIT"};
        drawButtonsList(g, menuButtons, labels);
    }

    private void drawCountdownOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Impact", Font.BOLD, 100));
        String text = countdownVal == 0 ? "GO!" : String.valueOf(countdownVal);
        
        // Glow effect
        g.setColor(new Color(255, 69, 0, 150));
        g.drawString(text, 370 - g.getFontMetrics().stringWidth(text) / 2 + 5, 295);

        g.setColor(Color.YELLOW);
        g.drawString(text, 370 - g.getFontMetrics().stringWidth(text) / 2, 290);
    }

    private void drawPauseOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Impact", Font.BOLD, 48));
        g.setColor(Color.WHITE);
        g.drawString("GAME PAUSED", 250, 130);

        String[] labels = {"RESUME", "RESTART RUN", "QUIT TO MENU"};
        drawButtonsList(g, pauseButtons, labels);
    }

    private void drawGameOverOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Impact", Font.BOLD, 64));
        g.setColor(Color.RED);
        g.drawString("GAME OVER", 230, 95);

        // Draw Stats Box
        g.setColor(new Color(30, 30, 30, 240));
        g.fillRoundRect(180, 130, 380, 230, 15, 15);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(180, 130, 380, 230, 15, 15);

        g.setFont(new Font("Consolas", Font.BOLD, 18));
        g.setColor(Color.YELLOW);
        g.drawString("CRASH REPORT", 310, 160);

        g.setFont(new Font("Consolas", Font.PLAIN, 16));
        g.setColor(Color.WHITE);
        g.drawString(String.format("SCORE:     %d", scoreManager.getScore()), 220, 200);
        g.drawString(String.format("DISTANCE:  %.1f km", scoreManager.getDistance() / 100.0f), 220, 230);
        g.drawString(String.format("COINS:     $%d", scoreManager.getCoinsCollected()), 220, 260);
        g.drawString(String.format("HIGH SCORE: %d", ScoreManager.getHighScore()), 220, 290);

        // Name input trigger
        g.drawString("ENTER NAME: ", 220, 330);
        g.setColor(Color.YELLOW);
        g.drawString(playerNameInput + (typingActive && (playTimeFrames % 30 < 15) ? "|" : ""), 330, 330);

        // Buttons
        g.setFont(new Font("Consolas", Font.BOLD, 14));
        drawButton(g, gameOverButtons[0], "SAVE & REPLAY", 0);
        drawButton(g, gameOverButtons[1], "MAIN MENU", 1);
    }

    private void drawSettingsScreen(Graphics2D g) {
        g.setColor(new Color(20, 20, 30));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Impact", Font.BOLD, 36));
        g.setColor(Color.WHITE);
        g.drawString("SETTINGS", 300, 75);

        // Sub items
        String musicLabel = SoundManager.isMuted() ? "MUSIC: MUTED" : "MUSIC: ON";
        String sfxLabel = rainMode ? "RAIN WEATHER: ON" : "RAIN WEATHER: OFF";

        g.setFont(new Font("Consolas", Font.BOLD, 16));
        drawButton(g, settingsButtons[0], musicLabel, 0);
        drawButton(g, settingsButtons[1], sfxLabel, 1);
        drawButton(g, settingsButtons[2], "BACK", 2);
    }

    private void drawHighscoreScreen(Graphics2D g) {
        g.setColor(new Color(15, 15, 20));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Impact", Font.BOLD, 36));
        g.setColor(Color.YELLOW);
        g.drawString("ARCADE LEADERBOARD", 230, 60);

        // Draw Table Headers
        g.setFont(new Font("Consolas", Font.BOLD, 14));
        g.setColor(Color.CYAN);
        g.drawString("RANK  SCORE   DISTANCE   COINS   DATE", 130, 110);
        g.drawLine(120, 120, 620, 120);

        g.setColor(Color.WHITE);
        List<ScoreManager.ScoreEntry> boards = ScoreManager.getLeaderboard();
        int y = 145;
        for (int i = 0; i < Math.min(8, boards.size()); i++) {
            ScoreManager.ScoreEntry e = boards.get(i);
            String row = String.format(" #%2d  %5d   %5.1f km   $%4d   %s", 
                                       i+1, e.score, e.distance/100.0f, e.coins, e.dateStr);
            g.drawString(row, 120, y);
            y += 30;
        }

        drawButton(g, highscoreButtons[0], "BACK", 0);
    }

    private void drawGarageScreen(Graphics2D g) {
        g.setColor(new Color(25, 25, 35));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Header
        g.setFont(new Font("Impact", Font.BOLD, 36));
        g.setColor(Color.ORANGE);
        g.drawString("GARAGE SHOP", 290, 60);

        g.setFont(new Font("Consolas", Font.BOLD, 18));
        g.setColor(Color.YELLOW);
        g.drawString(String.format("GARAGE BANK: $%d COINS", player.getCoins()), 230, 100);

        // Displays 3 cards: Engine, Handling, Armor
        g.setFont(new Font("Consolas", Font.PLAIN, 14));
        
        // Card 1: Engine Upgrade
        drawUpgradeCard(g, 60, 140, "ENGINE UPGRADE", player.getEngineLevel(), "Max Speed + Acceleration", 0);

        // Card 2: Handling Upgrade
        drawUpgradeCard(g, 270, 140, "HANDLING UPGRADE", player.getHandlingLevel(), "Steer Precision + Brake", 1);

        // Card 3: Armor Upgrade
        drawUpgradeCard(g, 480, 140, "ARMOR UPGRADE", player.getArmorLevel(), "Collision Invulnerability", 2);

        // Back button
        drawButton(g, garageButtons[3], "BACK TO MENU", 3);
    }

    private void drawUpgradeCard(Graphics2D g, int cx, int cy, String title, int currentLevel, String desc, int index) {
        g.setColor(new Color(40, 40, 50));
        g.fillRoundRect(cx, cy, 200, 250, 10, 10);
        g.setColor(Color.WHITE);
        g.drawRoundRect(cx, cy, 200, 250, 10, 10);

        // Title
        g.setFont(new Font("Consolas", Font.BOLD, 12));
        g.setColor(Color.YELLOW);
        g.drawString(title, cx + 15, cy + 30);

        // Desc
        g.setFont(new Font("Consolas", Font.PLAIN, 10));
        g.setColor(Color.LIGHT_GRAY);
        g.drawString(desc, cx + 15, cy + 60);

        // Render progress bars
        g.setFont(new Font("Consolas", Font.BOLD, 14));
        g.setColor(Color.WHITE);
        g.drawString(String.format("LEVEL: %d/5", currentLevel), cx + 15, cy + 120);

        for (int i = 0; i < 5; i++) {
            g.setColor(i < currentLevel ? Color.GREEN : Color.DARK_GRAY);
            g.fillRect(cx + 15 + i * 34, cy + 140, 30, 10);
        }

        // Price
        int price = currentLevel * 100;
        if (currentLevel >= 5) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawString("MAX LEVEL REACHED", cx + 15, cy + 190);
            drawButton(g, garageButtons[index], "MAXED", index);
        } else {
            g.setColor(Color.YELLOW);
            g.drawString(String.format("PRICE: $%d", price), cx + 15, cy + 190);
            String btnText = player.getCoins() >= price ? "UPGRADE" : "NO FUNDS";
            drawButton(g, garageButtons[index], btnText, index);
        }
    }

    private void drawCarSelectionScreen(Graphics2D g) {
        g.setColor(new Color(30, 20, 40));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Impact", Font.BOLD, 36));
        g.setColor(Color.CYAN);
        g.drawString("SELECT VEHICLE SKIN", 240, 60);

        // Draw three cars side by side
        String[] names = {"RED SPEEDER", "YELLOW LIGHTNING", "BLUE PHANTOM"};
        for (int i = 0; i < 3; i++) {
            int cx = 100 + i * 200;
            int cy = 130;

            // Frame
            g.setColor(activeCarSkin == i ? new Color(0, 255, 255, 60) : new Color(30, 30, 30));
            g.fillRoundRect(cx, cy, 140, 180, 10, 10);
            g.setColor(activeCarSkin == i ? Color.CYAN : Color.WHITE);
            g.drawRoundRect(cx, cy, 140, 180, 10, 10);

            // Draw car preview using procedural render helper
            BufferedImage skin = ResourceManager.getEnemyCar(i == 0 ? 5 : (i == 1 ? 1 : 0));
            if (skin != null) {
                g.drawImage(skin, cx + 52, cy + 40, null);
            }

            g.setFont(new Font("Consolas", Font.BOLD, 12));
            g.setColor(Color.WHITE);
            g.drawString(names[i], cx + 25, cy + 130);

            String buttonText = activeCarSkin == i ? "EQUIPPED" : "EQUIP";
            drawButton(g, carSelectionButtons[i], buttonText, i);
        }

        drawButton(g, carSelectionButtons[3], "BACK", 3);
    }

    private void drawButtonsList(Graphics2D g, Rectangle[] buttons, String[] labels) {
        g.setFont(new Font("Consolas", Font.BOLD, 16));
        for (int i = 0; i < buttons.length; i++) {
            drawButton(g, buttons[i], labels[i], i);
        }
    }

    private void drawButton(Graphics2D g, Rectangle rect, String text, int index) {
        boolean hovered = (hoveredButtonIndex == index);
        g.setColor(hovered ? new Color(255, 255, 255, 40) : new Color(0, 0, 0, 120));
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 8, 8);

        g.setColor(hovered ? Color.YELLOW : Color.WHITE);
        g.setStroke(new BasicStroke(hovered ? 2 : 1));
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 8, 8);

        g.drawString(text, rect.x + (rect.width - g.getFontMetrics().stringWidth(text)) / 2, rect.y + 22);
    }

    // ==========================================
    // MOUSE AND KEYBOARD TYPING HANDLERS
    // ==========================================

    @Override
    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();
        int newHover = -1;

        Rectangle[] activeButtons = getActiveButtonsList();
        if (activeButtons != null) {
            for (int i = 0; i < activeButtons.length; i++) {
                if (activeButtons[i].contains(p)) {
                    newHover = i;
                    break;
                }
            }
        }

        if (newHover != hoveredButtonIndex) {
            hoveredButtonIndex = newHover;
            if (hoveredButtonIndex != -1) {
                SoundManager.playClick();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        Rectangle[] activeButtons = getActiveButtonsList();
        if (activeButtons == null || hoveredButtonIndex == -1) return;

        SoundManager.playClick();

        switch (state) {
            case MENU:
                if (hoveredButtonIndex == 0) restartGame();
                else if (hoveredButtonIndex == 1) state = GameState.CAR_SELECTION;
                else if (hoveredButtonIndex == 2) state = GameState.GARAGE;
                else if (hoveredButtonIndex == 3) state = GameState.HIGH_SCORE;
                else if (hoveredButtonIndex == 4) state = GameState.SETTINGS;
                else if (hoveredButtonIndex == 5) System.exit(0);
                break;
            case PAUSED:
                if (hoveredButtonIndex == 0) state = GameState.PLAYING;
                else if (hoveredButtonIndex == 1) restartGame();
                else if (hoveredButtonIndex == 2) state = GameState.MENU;
                break;
            case GAME_OVER:
                if (hoveredButtonIndex == 0) {
                    // Score already submitted on crash; restart run
                    restartGame();
                } else if (hoveredButtonIndex == 1) {
                    state = GameState.MENU;
                }
                break;
            case SETTINGS:
                if (hoveredButtonIndex == 0) SoundManager.toggleMute();
                else if (hoveredButtonIndex == 1) {
                    rainMode = !rainMode;
                    road.setRain(rainMode);
                } else if (hoveredButtonIndex == 2) state = GameState.MENU;
                break;
            case HIGH_SCORE:
                if (hoveredButtonIndex == 0) state = GameState.MENU;
                break;
            case GARAGE:
                if (hoveredButtonIndex == 3) {
                    state = GameState.MENU;
                } else {
                    int upgradeIndex = hoveredButtonIndex; // 0: Engine, 1: Handling, 2: Armor
                    int currentLevel = (upgradeIndex == 0) ? player.getEngineLevel() : 
                                       ((upgradeIndex == 1) ? player.getHandlingLevel() : player.getArmorLevel());
                    int price = currentLevel * 100;
                    if (currentLevel < 5 && player.getCoins() >= price) {
                        player.addCoins(-price); // spend coins
                        if (upgradeIndex == 0) player.upgradeEngine(price);
                        else if (upgradeIndex == 1) player.upgradeHandling(price);
                        else if (upgradeIndex == 2) player.upgradeArmor(price);
                    }
                }
                break;
            case CAR_SELECTION:
                if (hoveredButtonIndex == 3) {
                    state = GameState.MENU;
                } else if (hoveredButtonIndex >= 0 && hoveredButtonIndex < 3) {
                    activeCarSkin = hoveredButtonIndex;
                }
                break;
            default:
                break;
        }

        hoveredButtonIndex = -1; // reset after action
    }

    private Rectangle[] getActiveButtonsList() {
        switch (state) {
            case MENU: return menuButtons;
            case PAUSED: return pauseButtons;
            case GAME_OVER: return gameOverButtons;
            case SETTINGS: return settingsButtons;
            case HIGH_SCORE: return highscoreButtons;
            case GARAGE: return garageButtons;
            case CAR_SELECTION: return carSelectionButtons;
            default: return null;
        }
    }

    // Handles keyboard character entry during game-over leaderboard typing
    public void handleTyping(KeyEvent e) {
        if (state == GameState.GAME_OVER) {
            typingActive = true;
            char c = e.getKeyChar();
            if (Character.isLetterOrDigit(c) && playerNameInput.length() < 12) {
                playerNameInput += c;
            } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && playerNameInput.length() > 0) {
                playerNameInput = playerNameInput.substring(0, playerNameInput.length() - 1);
            }
        }
    }

    private void triggerScreenshot() {
        try {
            BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            paintAll(g);
            g.dispose();
            javax.imageio.ImageIO.write(image, "PNG", new java.io.File("screenshot.png"));
            particles.add(new Particle("SCREENSHOT CAPTURED!", 250, 200, -2.0f, 0.02f, Color.CYAN, 16));
        } catch (Exception ignored) {}
    }

    // Interface overrides
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}

    public GameState getGameState() { return state; }
}
