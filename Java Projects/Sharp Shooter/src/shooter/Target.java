package shooter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Target extends JPanel {

    public static final Color BACKGROUND_COLOR = Color.DARK_GRAY;

    public static final int TARGET_CENTER_X = 350;
    public static final int TARGET_CENTER_Y = 350;

    private static final int BULLET_HOLE_RADIUS = 5;
    private static final Color BULLET_HOLE_COLOR = new Color(120, 144, 156);
    private static final Color BULLET_HOLE_GRADIENT = Color.BLACK;

    private static final Color GUN_SIGHT_COLOR = Color.RED;
    private static final int GUN_SIGHT_RADIUS = 40;

    final SharpShooter game;

    private final java.util.List<int[]> bulletHoles = new java.util.ArrayList<>();
    private final int[] gunSightLocation;

    private int bulletsLeft = 12;
    private int score = 0;

    static final int[] ACCELERATION = new int[]{1, 2, 4, 2, 1};
    int currentAcceleration = 4;

    public Target(SharpShooter game) {
        super();
        this.game = game;
        setName("Canvas");
        setFocusable(true);
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(800, 800));
        gunSightLocation = new int[]{TARGET_CENTER_X, TARGET_CENTER_Y};
        addKeyListener(new TargetKeyListener(this));
    }

    void addBulletHole(int x, int y) {
        bulletHoles.add(new int[]{x, y});
    }

    void drawTarget(Graphics g) {
        for (int i = 9; i >= 0; i--) {
            int radius = 30 * (i + 1);
            if (i > 0 && i < 4) {
                g.setColor(Color.WHITE);
                g.fillOval(TARGET_CENTER_X - radius, TARGET_CENTER_Y - radius, 2 * radius, 2 * radius);
                g.setColor(Color.BLACK);
                g.drawOval(TARGET_CENTER_X - radius, TARGET_CENTER_Y - radius, 2 * radius, 2 * radius);
            } else {
                g.setColor(Color.BLACK);
                g.fillOval(TARGET_CENTER_X - radius, TARGET_CENTER_Y - radius, 2 * radius, 2 * radius);
                g.setColor(Color.WHITE);
                g.drawOval(TARGET_CENTER_X - radius, TARGET_CENTER_Y - radius, 2 * radius, 2 * radius);
            }
            if (i > 0) {
                g.setColor(Color.GRAY);
                g.setFont(new Font("Arial Black", Font.BOLD, 20));
                int val = Math.abs(i - 10);
                g.drawString(String.valueOf(val), TARGET_CENTER_X - 5, TARGET_CENTER_Y + radius - 10);
                g.drawString(String.valueOf(val), TARGET_CENTER_X - 5, TARGET_CENTER_Y - radius + 25);
                g.drawString(String.valueOf(val), TARGET_CENTER_X + radius - 20, TARGET_CENTER_Y + 5);
                g.drawString(String.valueOf(val), TARGET_CENTER_X - radius + 10, TARGET_CENTER_Y + 5);
            }
        }
    }

    void drawGunSight(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setStroke(new BasicStroke(5));
        g.setColor(GUN_SIGHT_COLOR);
        g.drawOval(
                gunSightLocation[0] - GUN_SIGHT_RADIUS,
                gunSightLocation[1] - GUN_SIGHT_RADIUS,
                GUN_SIGHT_RADIUS * 2,
                GUN_SIGHT_RADIUS * 2
        );
        g.setStroke(new BasicStroke(1));
        g.drawLine(
                gunSightLocation[0] - GUN_SIGHT_RADIUS + 2,
                gunSightLocation[1],
                gunSightLocation[0] - 7,
                gunSightLocation[1]
        );
        g.drawLine(
                gunSightLocation[0] + GUN_SIGHT_RADIUS - 2,
                gunSightLocation[1],
                gunSightLocation[0] + 7,
                gunSightLocation[1]
        );
        g.drawLine(
                gunSightLocation[0],
                gunSightLocation[1] - GUN_SIGHT_RADIUS + 2,
                gunSightLocation[0],
                gunSightLocation[1] - 7
        );
        g.drawLine(
                gunSightLocation[0],
                gunSightLocation[1] + GUN_SIGHT_RADIUS - 2,
                gunSightLocation[0],
                gunSightLocation[1] + 7
        );
    }

    public void drawBulletHoles(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (int[] bh : bulletHoles) {
            GradientPaint gradient = new GradientPaint(
                    bh[0] - BULLET_HOLE_RADIUS,
                    bh[1] - BULLET_HOLE_RADIUS,
                    BULLET_HOLE_COLOR,
                    bh[0] + BULLET_HOLE_RADIUS * 2,
                    bh[1] + BULLET_HOLE_RADIUS * 2,
                    BULLET_HOLE_GRADIENT
            );
            g2d.setPaint(gradient);
            g2d.fillOval(
                    bh[0] - BULLET_HOLE_RADIUS,
                    bh[1] - BULLET_HOLE_RADIUS,
                    BULLET_HOLE_RADIUS * 2,
                    BULLET_HOLE_RADIUS * 2
            );
        }
    }

    void gameOver() {
        game.statusbar.setText(String.format("Game over, your score: %d", score));
    }

    void updateStatus() {
        game.statusbar.setText(
                String.format("Bullets left: %d, your score: %d(%d). Use: \u2192\u2190\u2191\u2193 SPACE buttons.",
                        bulletsLeft, score, 12 - bulletsLeft));
    }

    void calculateScore(int x, int y) {
        int dist = (int) (Math.sqrt(Math.pow(TARGET_CENTER_X - x, 2) + Math.pow(TARGET_CENTER_Y - y, 2)));
        int point = 10 - (dist / 30);
        score += Math.max(point, 0);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawTarget(g);
        drawBulletHoles(g);
        drawGunSight(g);
    }

    static class TargetKeyListener implements KeyListener {

        private final Target target;

        public TargetKeyListener(Target target) {
            this.target = target;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (target.bulletsLeft > 0) {
                if (target.game.gameStarted && e.getKeyCode() != KeyEvent.VK_SPACE) {
                    target.currentAcceleration = (target.currentAcceleration + 1) % 5;
                    int rate = Target.ACCELERATION[target.currentAcceleration] * 10;
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        target.gunSightLocation[1] += rate;
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        target.gunSightLocation[1] -= rate;
                    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        target.gunSightLocation[0] -= rate;
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        target.gunSightLocation[0] += rate;
                    }
                } else {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        if (target.game.gameStarted) {
                            target.addBulletHole(target.gunSightLocation[0], target.gunSightLocation[1]);
                            target.bulletsLeft -= 1;
                            target.calculateScore(target.gunSightLocation[0], target.gunSightLocation[1]);
                            if (target.bulletsLeft == 0) {
                                target.gameOver();
                                target.game.gameStarted = false;
                            }
                        } else {
                            target.game.gameStarted = true;
                        }
                    }
                }
                target.gunSightLocation[0] = Math.max(target.gunSightLocation[0], 40);
                target.gunSightLocation[0] = Math.min(target.gunSightLocation[0], 660);
                target.gunSightLocation[1] = Math.max(target.gunSightLocation[1], 40);
                target.gunSightLocation[1] = Math.min(target.gunSightLocation[1], 660);
                if (target.game.gameStarted) {
                    target.updateStatus();
                }
            } else {
                target.gameOver();
            }
            target.repaint();
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }
}
