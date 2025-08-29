package main.java;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

class CanvasApp extends JFrame implements MouseListener {

    private List<Point> originalPoints = new ArrayList<>(); // points fixes
    private List<Point> animPoints = new ArrayList<>();     // points animés
    private Canvas c;
    private Timer timer;

    public CanvasApp() {
        super("Canvas Chaikin");

        c = new Canvas() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);

                // Fond noir dd
                g.setColor(Color.white);
                g.fillRect(0, 0, getWidth(), getHeight());

                // Lignes entre points originaux
                if (!originalPoints.isEmpty() && (timer == null || !timer.isRunning())) {
                    g.setColor(Color.black);
                    for (int i = 0; i < originalPoints.size() - 1; i++) {
                        Point p1 = originalPoints.get(i);
                        Point p2 = originalPoints.get(i + 1);
                        g.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }

                // Points originaux
                g.setColor(Color.BLUE);
                for (Point p : originalPoints) {
                    g.fillOval(p.x - 3, p.y - 3, 6, 6);
                }

                // Animation Chaikin
                if (!animPoints.isEmpty() && timer != null && timer.isRunning()) {
                    g.setColor(Color.RED);
                    for (int i = 0; i < animPoints.size() - 1; i++) {
                        Point p1 = animPoints.get(i);
                        Point p2 = animPoints.get(i + 1);
                        g.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
            }
        };

        // c.setBackground(Color.BLACK);
        c.addMouseListener(this);
        c.setFocusable(true);
        c.requestFocusInWindow();
        c.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (originalPoints.size() < 2) {
                        System.out.println("Dessinez au moins 2 points pour lancer l'animation.");
                        c.repaint();
                    } else {
                        startAnimation();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
            }
        });

        add(c);
        setSize(1800, 900);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // ---------------- MouseListener ----------------
    @Override
    public void mouseClicked(MouseEvent e) {
        if (timer == null || !timer.isRunning()) {
            Point p = new Point(e.getX(), e.getY());
            originalPoints.add(p);
            animPoints = new ArrayList<>(originalPoints);
            c.repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    // ---------------- Chaikin ----------------
    private void startAnimation() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        animPoints = new ArrayList<>(originalPoints);
        final int[] step = {0};
        final int steps = 10;

        timer = new Timer(300, _ -> {
            if (step[0] >= steps) {
                step[0] = 0;
                animPoints = new ArrayList<>(originalPoints); // repart de l'état initial
            } else {
                animPoints = chaikinStep(animPoints);
                step[0]++;
            }
            c.repaint();
        });

        timer.start();
    }

    private List<Point> chaikinStep(List<Point> input) {
        List<Point> result = new ArrayList<>();

        if (input.size() < 2) {
            return input;
        }

        result.add(input.get(0)); // garder premier point

        for (int i = 0; i < input.size() - 1; i++) {
            Point p0 = input.get(i);
            Point p1 = input.get(i + 1);
            int qx = (int) (0.75 * p0.x + 0.25 * p1.x);
            int qy = (int) (0.75 * p0.y + 0.25 * p1.y);
            int rx = (int) (0.25 * p0.x + 0.75 * p1.x);
            int ry = (int) (0.25 * p0.y + 0.75 * p1.y);
            result.add(new Point(qx, qy));
            result.add(new Point(rx, ry));
        }
        result.add(input.get(input.size() - 1)); // garder dernier point
        return result;
    }

}
