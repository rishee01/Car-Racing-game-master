import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScoreManager {
    public static class ScoreEntry implements Comparable<ScoreEntry> {
        public int score;
        public float distance;
        public int coins;
        public String timeStr;
        public String dateStr;

        public ScoreEntry(int score, float distance, int coins, String timeStr, String dateStr) {
            this.score = score;
            this.distance = distance;
            this.coins = coins;
            this.timeStr = timeStr;
            this.dateStr = dateStr;
        }

        @Override
        public int compareTo(ScoreEntry o) {
            // Descending order of scores
            return Integer.compare(o.score, this.score);
        }
    }

    private int score = 0;
    private float distance = 0.0f;
    private int coinsCollected = 0;
    private float comboMultiplier = 1.0f;
    private int comboCooldown = 0;

    private static final String HIGHSCORE_FILE = "highscore.txt";
    private static final List<ScoreEntry> leaderboard = new ArrayList<>();

    static {
        loadLeaderboard();
    }

    public void update(float playerSpeed) {
        if (playerSpeed > 0) {
            // Earn distance based on speed
            distance += (playerSpeed * 0.05f);
            
            // Score grows over time with speed and multiplier
            score += Math.round(playerSpeed * comboMultiplier * 0.1f);
        }

        // Combo decay
        if (comboCooldown > 0) {
            comboCooldown--;
            if (comboCooldown == 0) {
                comboMultiplier = 1.0f;
            }
        }
    }

    public void triggerNearMiss() {
        comboMultiplier = Math.min(5.0f, comboMultiplier + 0.5f);
        comboCooldown = 150; // 2.5 seconds at 60 FPS
        score += 150 * comboMultiplier;
    }

    public void triggerOvertake() {
        comboMultiplier = Math.min(5.0f, comboMultiplier + 0.2f);
        comboCooldown = 150;
        score += 50 * comboMultiplier;
    }

    public void addScore(int amt) {
        score += amt * comboMultiplier;
    }

    public void addCoins(int count) {
        coinsCollected += count;
    }

    public int getScore() { return score; }
    public float getDistance() { return distance; }
    public int getCoinsCollected() { return coinsCollected; }
    public float getComboMultiplier() { return comboMultiplier; }
    public int getComboCooldown() { return comboCooldown; }

    public static List<ScoreEntry> getLeaderboard() {
        return leaderboard;
    }

    public static int getHighScore() {
        if (leaderboard.isEmpty()) return 0;
        return leaderboard.get(0).score;
    }

    public static void submitScore(int score, float distance, int coins, int playTimeSeconds) {
        int mins = playTimeSeconds / 60;
        int secs = playTimeSeconds % 60;
        String timeStr = String.format("%02d:%02d", mins, secs);
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());

        ScoreEntry entry = new ScoreEntry(score, distance, coins, timeStr, dateStr);
        leaderboard.add(entry);
        Collections.sort(leaderboard);

        // Keep top 10
        if (leaderboard.size() > 10) {
            leaderboard.subList(10, leaderboard.size()).clear();
        }

        saveLeaderboard();
    }

    private static void loadLeaderboard() {
        leaderboard.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(HIGHSCORE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 5) {
                    try {
                        int s = Integer.parseInt(tokens[0]);
                        float d = Float.parseFloat(tokens[1]);
                        int c = Integer.parseInt(tokens[2]);
                        String t = tokens[3];
                        String dt = tokens[4];
                        leaderboard.add(new ScoreEntry(s, d, c, t, dt));
                    } catch (NumberFormatException ignored) {}
                }
            }
            Collections.sort(leaderboard);
        } catch (FileNotFoundException e) {
            // File does not exist yet; populate with default arcade placeholders
            populateDefaults();
            saveLeaderboard();
        } catch (IOException e) {
            System.err.println("Error loading highscores: " + e.getMessage());
        }
    }

    private static void saveLeaderboard() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(HIGHSCORE_FILE))) {
            for (ScoreEntry s : leaderboard) {
                pw.println(s.score + "," + s.distance + "," + s.coins + "," + s.timeStr + "," + s.dateStr);
            }
        } catch (IOException e) {
            System.err.println("Error saving highscores: " + e.getMessage());
        }
    }

    private static void populateDefaults() {
        leaderboard.add(new ScoreEntry(10000, 500.0f, 150, "03:45", "2026-01-01 12:00"));
        leaderboard.add(new ScoreEntry(5000, 250.0f, 75, "02:10", "2026-01-01 12:00"));
        leaderboard.add(new ScoreEntry(2000, 100.0f, 30, "01:05", "2026-01-01 12:00"));
        Collections.sort(leaderboard);
    }
}
