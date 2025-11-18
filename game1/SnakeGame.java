import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class SnakeGame extends JPanel implements ActionListener {

    // Window settings
    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private final int UNIT_SIZE = 25;

    // Config values (default)
    private int configGameSpeed = 75;
    private boolean configAutoSpeed = false;
    private boolean configGodmode = false;
    private boolean configExitOnDeath = false;

    // Game variables
    private int DELAY = 75; // overwritten by config
    private final int x[] = new int[(WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE)];
    private final int y[] = new int[(WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE)];
    private int bodyParts = 6;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private Random random;
    private BufferedImage foodImage;

    public SnakeGame() {

        loadConfig(); // <<< Load or create config.emr09 at startup
        this.DELAY = configGameSpeed;

        random = new Random();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        // Load custom food image
        File modsFolder = new File("mods");
        if (!modsFolder.exists()) {
            modsFolder.mkdir();
            System.out.println("Created mods folder.");
            File readme = new File(modsFolder, "README.md");
            try (FileWriter writer = new FileWriter(readme)) {
                writer.write("# Mods Folder\n\n");
                writer.write("Place a file named food.png here to replace the apple graphic.\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File foodFile = new File(modsFolder, "food.png");
        if (foodFile.exists()) {
            try {
                foodImage = ImageIO.read(foodFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ---------------------------------------------------------------
    // CONFIG SYSTEM
    // ---------------------------------------------------------------

    private void loadConfig() {
        File configFile = new File("config.emr09");

        if (!configFile.exists()) {
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write("game_speed=75\n");
                writer.write("auto_speed=false\n");
                writer.write("godmode=false\n");
                writer.write("exit_on_death=false\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Created default config.emr09");
        } else {
            try {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(configFile.toPath());
                for (String line : lines) {
                    if (line.startsWith("game_speed="))
                        configGameSpeed = Integer.parseInt(line.split("=")[1].trim());

                    if (line.startsWith("auto_speed="))
                        configAutoSpeed = Boolean.parseBoolean(line.split("=")[1].trim());

                    if (line.startsWith("godmode="))
                        configGodmode = Boolean.parseBoolean(line.split("=")[1].trim());

                    if (line.startsWith("exit_on_death="))
                        configExitOnDeath = Boolean.parseBoolean(line.split("=")[1].trim());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Loaded config.emr09");
        }
    }

    // ---------------------------------------------------------------
    // MAIN & GAME INITIALIZATION
    // ---------------------------------------------------------------

    public static void main(String[] args) {
        System.out.println("Loading Snake Game...");
        try { Thread.sleep(1000); } catch (InterruptedException e) { }

        JFrame frame = new JFrame("Snake Game - Hard Mode");
        SnakeGame gamePanel = new SnakeGame();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        gamePanel.startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(configGameSpeed, this);
        timer.start();
    }

    // ---------------------------------------------------------------
    // GAME LOOP & RENDERING
    // ---------------------------------------------------------------

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (running) {
            // apple
            if (foodImage != null) {
                g.drawImage(foodImage, appleX, appleY, UNIT_SIZE * 2, UNIT_SIZE * 2, null);
            } else {
                g.setColor(Color.RED);
                g.fillRect(appleX, appleY, UNIT_SIZE * 2, UNIT_SIZE * 2);
            }

            // snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) g.setColor(Color.GREEN);
                else g.setColor(new Color(45, 180, 0));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + applesEaten, 10, 25);
        }
        else {
            gameOver(g);
        }
    }

    // ---------------------------------------------------------------
    // GAME MECHANICS
    // ---------------------------------------------------------------

    private void newApple() {
        appleX = random.nextInt((WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U': y[0] -= UNIT_SIZE; break;
            case 'D': y[0] += UNIT_SIZE; break;
            case 'L': x[0] -= UNIT_SIZE; break;
            case 'R': x[0] += UNIT_SIZE; break;
        }
    }

    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    private void checkCollisions() {

        if (!configGodmode) {
            // snake hits itself
            for (int i = bodyParts; i > 0; i--) {
                if (x[0] == x[i] && y[0] == y[i])
                    running = false;
            }

            // wall collision
            if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT)
                running = false;
        }

        if (!running) {
            timer.stop();
            if (configExitOnDeath) System.exit(0);
        }
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over",
                (WIDTH - metrics.stringWidth("Game Over")) / 2,
                HEIGHT / 2
        );

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("Score: " + applesEaten,
                (WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2,
                HEIGHT / 2 + 50
        );

        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Press R to restart",
                (WIDTH - metrics.stringWidth("Press R to restart")) / 2,
                HEIGHT / 2 + 100
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();

            // Auto-speed function
            if (configAutoSpeed && applesEaten > 0 && applesEaten % 5 == 0) {
                int newDelay = Math.max(20, configGameSpeed - applesEaten);
                timer.setDelay(newDelay);
            }
        }
        repaint();
    }

    // ---------------------------------------------------------------
    // INPUT
    // ---------------------------------------------------------------

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') direction = 'L';
                    break;

                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') direction = 'R';
                    break;

                case KeyEvent.VK_UP:
                    if (direction != 'D') direction = 'U';
                    break;

                case KeyEvent.VK_DOWN:
                    if (direction != 'U') direction = 'D';
                    break;

                case KeyEvent.VK_R:
                    if (!running) restartGame();
                    break;
            }
        }
    }

    private void restartGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        Arrays.fill(x, 0);
        Arrays.fill(y, 0);
        startGame();
    }
}
