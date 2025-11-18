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

    // Game settings
    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private final int UNIT_SIZE = 25;
    private final int DELAY = 75; // Hard mode: fast
    private final int x[] = new int[(WIDTH*HEIGHT)/(UNIT_SIZE*UNIT_SIZE)];
    private final int y[] = new int[(WIDTH*HEIGHT)/(UNIT_SIZE*UNIT_SIZE)];
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
        random = new Random();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        // Load custom food image if exists
        File modsFolder = new File("mods");
        if (!modsFolder.exists()) {
            modsFolder.mkdir();
            System.out.println("Created mods folder.");
            File readme = new File(modsFolder, "README.md");
            try (FileWriter writer = new FileWriter(readme)) {
                writer.write("# Mods Folder\n\n");
                writer.write("This folder is used for modifying game elements.\n\n");
                writer.write("Currently, it supports custom food images:\n");
                writer.write("- Place an image named `food.png` here.\n");
                writer.write("- If the file exists, the game will use it as the snake's food.\n");
                writer.write("- If not, the game will use the default red square food.\n");
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

    public static void main(String[] args) {
        // Loading screen
        System.out.println("Loading Snake Game...");
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        // Start game
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
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (running) {
            // Draw apple
            if (foodImage != null) {
                g.drawImage(foodImage, appleX, appleY, UNIT_SIZE*2, UNIT_SIZE*2, null);
            } else {
                g.setColor(Color.RED);
                g.fillRect(appleX, appleY, UNIT_SIZE*2, UNIT_SIZE*2);
            }

            // Draw snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) g.setColor(Color.GREEN);
                else g.setColor(new Color(45,180,0));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // Score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + applesEaten, 10, 25);
        } else {
            gameOver(g);
        }
    }

    private void newApple() {
        appleX = random.nextInt((WIDTH/UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        switch(direction) {
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
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) running = false;
        }
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) running = false;
        if (!running) timer.stop();
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (WIDTH - metrics.stringWidth("Game Over"))/2, HEIGHT/2);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("Score: " + applesEaten, (WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, HEIGHT/2 + 50);

        // Restart message
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Press R to restart", (WIDTH - metrics.stringWidth("Press R to restart"))/2, HEIGHT/2 + 100);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT: if (direction != 'R') direction = 'L'; break;
                case KeyEvent.VK_RIGHT: if (direction != 'L') direction = 'R'; break;
                case KeyEvent.VK_UP: if (direction != 'D') direction = 'U'; break;
                case KeyEvent.VK_DOWN: if (direction != 'U') direction = 'D'; break;
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
