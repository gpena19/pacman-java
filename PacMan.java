import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x, y, width, height;
        Image image;
        int startX, startY;
        char direction = 'U';
        int velocityX = 0, velocityY = 0;
        long spawnDelay;
        

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }
    
        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            switch (this.direction) {
                case 'U' -> { this.velocityX = 0; this.velocityY = -tileSize/4; }
                case 'D' -> { this.velocityX = 0; this.velocityY = tileSize/4; }
                case 'L' -> { this.velocityX = -tileSize/4; this.velocityY = 0; }
                case 'R' -> { this.velocityX = tileSize/4; this.velocityY = 0; }
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    
    private boolean ghostScatterMode = true;
    private long scatterCooldownEndTime = 0;
    private long scatterEndTime = 0;
    private boolean poweredUp = false;
    private long powerUpEndTime = 0;
    private int statusBarHeight = 40;
    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize + statusBarHeight;

    private Image wallImage, blueGhostImage, orangeGhostImage, pinkGhostImage, redGhostImage;
    private Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;
    private Image powerFoodImage;

    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X        *        X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O  *    bpo    *  O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls, foods, ghosts;
    Block pacman;

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score = 0, lives = 3;
    boolean gameOver = false;

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();
        powerFoodImage = new ImageIcon(getClass().getResource("./powerFood.png")).getImage();

        loadMap();
        for (Block ghost : ghosts) {
            ghost.updateDirection(directions[random.nextInt(4)]);
        }

        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();


        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char tileMapChar = tileMap[r].charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;

                switch (tileMapChar) {
                    case 'X' -> walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                    case 'b' -> {
                        Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                        ghost.spawnDelay = System.currentTimeMillis() + 2000 + random.nextInt(3000);
                        ghosts.add(ghost);
                    }
                    case 'o' -> {
                        Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                        ghost.spawnDelay = System.currentTimeMillis() + 2000 + random.nextInt(3000);
                        ghosts.add(ghost);
                    }
                    case 'p' -> {
                        Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                        ghost.spawnDelay = System.currentTimeMillis() + 2000 + random.nextInt(3000);
                        ghosts.add(ghost);
                    }
                    case 'r' -> {
                        Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                        ghost.spawnDelay = System.currentTimeMillis() + 2000 + random.nextInt(3000);
                        ghosts.add(ghost);
                    }
                    case 'P' -> pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                    case ' ' -> foods.add(new Block(null, x + 14, y + 14, 4, 4));
                    case '*' -> foods.add(new Block(powerFoodImage, x + 10, y + 10, 12, 12));
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, boardWidth, statusBarHeight);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString(gameOver ? "Game Over | Final Score: " + score : "Lives: x" + lives + "   Score: " + score, 10, 25);

        g.drawImage(pacman.image, pacman.x, pacman.y + statusBarHeight, pacman.width, pacman.height, null);
        for (Block ghost : ghosts)
            g.drawImage(ghost.image, ghost.x, ghost.y + statusBarHeight, ghost.width, ghost.height, null);
        for (Block wall : walls)
            g.drawImage(wall.image, wall.x, wall.y + statusBarHeight, wall.width, wall.height, null);
        g.setColor(Color.WHITE);
        for (Block food : foods)
            g.fillRect(food.x, food.y + statusBarHeight, food.width, food.height);
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        long currentTime = System.currentTimeMillis();

        if (ghostScatterMode && currentTime > scatterEndTime) {
    ghostScatterMode = false;
    scatterCooldownEndTime = currentTime + 5000; // chase for 7 seconds
}
            if (!ghostScatterMode && currentTime > scatterCooldownEndTime) {
    ghostScatterMode = true;
    scatterEndTime = currentTime + 4000; // scatter for 4 seconds
}
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
                if (food.image == powerFoodImage) {
                    poweredUp = true;
                    powerUpEndTime = System.currentTimeMillis() + 7000;
                    for (Block ghost : ghosts)
                        ghost.image = new ImageIcon(getClass().getResource("./scaredGhost.png")).getImage();
                }
            }
        }
        foods.remove(foodEaten);

        if (poweredUp && System.currentTimeMillis() > powerUpEndTime) {
            poweredUp = false;
            for (Block ghost : ghosts) {
                char symbol = tileMap[ghost.startY / tileSize].charAt(ghost.startX / tileSize);
                ghost.image = switch (symbol) {
                    case 'b' -> blueGhostImage;
                    case 'o' -> orangeGhostImage;
                    case 'p' -> pinkGhostImage;
                    case 'r' -> redGhostImage;
                    default -> ghost.image;
                };
        // Toggle scatter every 10 seconds
        if (!ghostScatterMode && System.currentTimeMillis() % 10000 < 100) {
             ghostScatterMode = true;
            scatterEndTime = System.currentTimeMillis() + 3000; // scatter for 3 sec
    }

        if (ghostScatterMode && System.currentTimeMillis() > scatterEndTime) {
        ghostScatterMode = false;
        if (System.currentTimeMillis() < ghost.spawnDelay) continue;
}
            }
        }

        HashSet<Point> ghostOccupiedTiles = new HashSet<>();

        for (Block ghost : ghosts) {
            // Store the tile this ghost is currently on
            Point ghostTile = new Point(ghost.x / tileSize, ghost.y / tileSize);
            ghostOccupiedTiles.add(ghostTile);
        
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
        
            // Only evaluate new direction at tile-aligned intersections
            if (ghost.x % tileSize == 0 && ghost.y % tileSize == 0) {
                int dx = pacman.x - ghost.x;
                int dy = pacman.y - ghost.y;
        
                char[] tryDirs = new char[4];
                int i = 0;
        
                if (Math.abs(dx) > Math.abs(dy)) {
                    tryDirs[i++] = dx < 0 ? 'L' : 'R';
                    tryDirs[i++] = dy < 0 ? 'U' : 'D';
                    tryDirs[i++] = dy < 0 ? 'D' : 'U';
                    tryDirs[i++] = dx < 0 ? 'R' : 'L';
                } else {
                    tryDirs[i++] = dy < 0 ? 'U' : 'D';
                    tryDirs[i++] = dx < 0 ? 'L' : 'R';
                    tryDirs[i++] = dx < 0 ? 'R' : 'L';
                    tryDirs[i++] = dy < 0 ? 'D' : 'U';
                }
        
                for (char dir : tryDirs) {
                    int testX = ghost.x;
                    int testY = ghost.y;
        
                    switch (dir) {
                        case 'U' -> testY -= tileSize;
                        case 'D' -> testY += tileSize;
                        case 'L' -> testX -= tileSize;
                        case 'R' -> testX += tileSize;
                    }
        
                    Point testTile = new Point(testX / tileSize, testY / tileSize);
                    if (ghostOccupiedTiles.contains(testTile)) continue; // another ghost is already there
        
                    Block testBlock = new Block(null, testX, testY, tileSize, tileSize);
                    boolean blocked = false;
                    for (Block wall : walls) {
                        if (collision(testBlock, wall)) {
                            blocked = true;
                            break;
                        }
                    }
        
                    if (!blocked) {
                        ghost.updateDirection(dir);
                        break;
                    }
                    if (ghostScatterMode) {
                        ghost.updateDirection(directions[random.nextInt(4)]);
                        continue; // skip pathfinding
                    }
                }
            }
        
            // Check for collision with Pac-Man
            if (collision(ghost, pacman)) {
                if (poweredUp) {
                    score += 200;
                    ghost.reset();
                } else {
                    lives--;
                    if (lives == 0) {
                        gameOver = true;
                        return;
                    }
                    resetPositions();
                    break;
                }
            }
        
        
            // Check for collision with Pac-Man
            if (collision(ghost, pacman)) {
                if (poweredUp) {
                    score += 200;
                    ghost.reset();
                } else {
                    lives--;
                    if (lives == 0) {
                        gameOver = true;
                        return;
                    }
                    resetPositions();
                    break;
                }
            }
        }

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            int dx = pacman.x - ghost.x;
int dy = pacman.y - ghost.y;

char[] preferredDirs = new char[4];
int index = 0;

// Prioritize directions that reduce the distance to Pac-Man
if (Math.abs(dx) > Math.abs(dy)) {
    preferredDirs[index++] = dx < 0 ? 'L' : 'R';
    preferredDirs[index++] = dy < 0 ? 'U' : 'D';
    preferredDirs[index++] = dy < 0 ? 'D' : 'U';
    preferredDirs[index++] = dx < 0 ? 'R' : 'L';
} else {
    preferredDirs[index++] = dy < 0 ? 'U' : 'D';
    preferredDirs[index++] = dx < 0 ? 'L' : 'R';
    preferredDirs[index++] = dx < 0 ? 'R' : 'L';
    preferredDirs[index++] = dy < 0 ? 'D' : 'U';
}

// Try the directions in order until one is walkable
for (char dir : preferredDirs) {
    ghost.updateDirection(dir);
    ghost.x += ghost.velocityX;
    ghost.y += ghost.velocityY;

    boolean blocked = false;
    for (Block wall : walls) {
        if (collision(ghost, wall)) {
            blocked = true;
            break;
        }
    }

    ghost.x -= ghost.velocityX;
    ghost.y -= ghost.velocityY;

    if (!blocked) break;
}
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) gameLoop.stop();
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> pacman.updateDirection('U');
            case KeyEvent.VK_DOWN -> pacman.updateDirection('D');
            case KeyEvent.VK_LEFT -> pacman.updateDirection('L');
            case KeyEvent.VK_RIGHT -> pacman.updateDirection('R');
        }

        pacman.image = switch (pacman.direction) {
            case 'U' -> pacmanUpImage;
            case 'D' -> pacmanDownImage;
            case 'L' -> pacmanLeftImage;
            case 'R' -> pacmanRightImage;
            default -> pacman.image;
        };
    }
}
