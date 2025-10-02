import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;
import java.util.Iterator;
import javax.sound.sampled.*;
import java.io.IOException;

public class TronGame extends JPanel implements ActionListener, KeyListener {

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int UNIT = 12;
    private static final int DELAY = 40;

    private LinkedList<Point> player1Trail = new LinkedList<>();
    private LinkedList<Point> player2Trail = new LinkedList<>();

    private Point player1;
    private Point player2;

    private char dir1 = 'R';
    private char dir2 = 'L';

    private Timer timer;
    private boolean running = false;
    private String winner = null;

    private Color player1Color;
    private Color player2Color;

    private Image imgPlayer1;
    private Image imgPlayer2;
    private int motoSize = 55;

    // Variables para ajustar la posición de las imágenes
    private int imageOffsetX = -17;
    private int imageOffsetY = -17;

    // Variables para habilidades especiales
    private boolean player1AbilityUsed = false;
    private boolean player2AbilityUsed = false;

    // Habilidad Azul - Velocidad
    private boolean player1SpeedBoost = false;
    private boolean player2SpeedBoost = false;
    private int player1SpeedTimer = 0;
    private int player2SpeedTimer = 0;
    private Timer speedTimer1;
    private Timer speedTimer2;

    // Habilidad Amarillo - Invisibilidad
    private boolean player1Invisible = false;
    private boolean player2Invisible = false;
    private int player1InvisibleTimer = 0;
    private int player2InvisibleTimer = 0;
    private Timer invisTimer1;
    private Timer invisTimer2;

    // Habilidad Verde - Controles invertidos
    private boolean player1ControlsInverted = false;
    private boolean player2ControlsInverted = false;
    private int player1InvertTimer = 0;
    private int player2InvertTimer = 0;
    private Timer invertTimer1;
    private Timer invertTimer2;

    // Variables para sonidos del juego
    private Clip musicaJuego;
    private boolean musicaJuegoActiva = false;

    public TronGame(Color p1Color, Color p2Color) {
        this.player1Color = p1Color;
        this.player2Color = p2Color;

        updateImages();
        inicializarMusicaJuego();

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);
        startGame();
    }

    // Inicializar música del juego
    private void inicializarMusicaJuego() {
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(
                    getClass().getResource("/assets/musicaJuego.wav") // Tu archivo de música del juego
            );

            musicaJuego = AudioSystem.getClip();
            musicaJuego.open(audioInput);
            musicaJuego.loop(Clip.LOOP_CONTINUOUSLY);
            musicaJuegoActiva = true;

            // Ajustar volumen
            ajustarVolumen(musicaJuego, -15.0f);

        } catch (Exception e) {
            System.out.println("Error al cargar la música del juego: " + e.getMessage());
            musicaJuegoActiva = false;
        }
    }

    // Método para ajustar el volumen
    private void ajustarVolumen(Clip clip, float volumenDB) {
        try {
            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(volumenDB);
        } catch (Exception e) {
            System.out.println("No se pudo ajustar el volumen: " + e.getMessage());
        }
    }

    // Reproducir sonido de colisión/derrota
    private void reproducirSonidoDerrota() {
        reproducirSonidoEfecto("/assets/derrota.wav");
    }

    // Reproducir sonido de habilidad
    private void reproducirSonidoHabilidad(String tipoHabilidad) {
        String archivo = switch (tipoHabilidad) {
            case "velocidad" -> "/assets/velocidad.wav";
            case "invisibilidad" -> "/assets/invisibilidad.wav";
            case "explosion" -> "/assets/explosion.wav";
            case "confusion" -> "/assets/confusion.wav";
            default -> null; // No reproducir nada si no encuentra el tipo
        };

        if (archivo != null) {
            reproducirSonidoEfecto(archivo);
        }
    }

    // Método genérico para reproducir efectos de sonido
    private void reproducirSonidoEfecto(String rutaArchivo) {
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(
                    getClass().getResource(rutaArchivo)
            );
            Clip sonidoEfecto = AudioSystem.getClip();
            sonidoEfecto.open(audioInput);
            sonidoEfecto.start();

            // Cerrar el clip después de que termine
            sonidoEfecto.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    sonidoEfecto.close();
                }
            });

        } catch (Exception e) {
            System.out.println("Error al reproducir efecto de sonido " + rutaArchivo + ": " + e.getMessage());
        }
    }

    // Detener música del juego
    private void detenerMusicaJuego() {
        if (musicaJuego != null) {
            musicaJuego.stop();
            musicaJuego.close();
            musicaJuegoActiva = false;
        }
    }

    private void updateImages() {
        try {
            String imagePath1 = getImagePathForColor(player1Color);
            imgPlayer1 = new ImageIcon(getClass().getResource(imagePath1)).getImage();

            String imagePath2 = getImagePathForColor(player2Color);
            imgPlayer2 = new ImageIcon(getClass().getResource(imagePath2)).getImage();

        } catch (Exception e) {
            System.out.println("Error cargando imágenes: " + e.getMessage());
            try {
                imgPlayer1 = new ImageIcon(getClass().getResource("/assets/Red.png")).getImage();
                imgPlayer2 = new ImageIcon(getClass().getResource("/assets/Blue.png")).getImage();
            } catch (Exception e2) {
                System.out.println("Error cargando imágenes de fallback: " + e2.getMessage());
            }
        }
    }

    private String getImagePathForColor(Color color) {
        if (color.equals(Color.RED)) {
            return "/assets/Red.png";
        } else if (color.equals(Color.BLUE)) {
            return "/assets/Blue.png";
        } else if (color.equals(Color.YELLOW)) {
            return "/assets/Yellow.png";
        } else if (color.equals(Color.GREEN)) {
            return "/assets/Green.png";
        } else {
            return "/assets/Red.png";
        }
    }

    private double getRotationAngle(char direction) {
        switch (direction) {
            case 'U': return -Math.PI / 2;
            case 'D': return Math.PI / 2;
            case 'L': return Math.PI;
            case 'R': return 0;
            default: return 0;
        }
    }

    private void drawRotatedImage(Graphics2D g2d, Image image, int x, int y, int width, int height, double angle, float alpha) {
        AffineTransform oldTransform = g2d.getTransform();

        Composite oldComposite = g2d.getComposite();
        if (alpha < 1.0f) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }

        int centerX = x + width / 2;
        int centerY = y + height / 2;

        AffineTransform transform = new AffineTransform();
        transform.translate(centerX, centerY);
        transform.rotate(angle);
        transform.translate(-width / 2, -height / 2);

        g2d.setTransform(transform);
        g2d.drawImage(image, 0, 0, width, height, this);

        g2d.setTransform(oldTransform);
        g2d.setComposite(oldComposite);
    }

    public void startGame() {
        if (timer != null) {
            timer.stop();
        }
        stopAllAbilityTimers();

        player1 = new Point(WIDTH / 4, HEIGHT / 2);
        player2 = new Point(3 * WIDTH / 4, HEIGHT / 2);
        player1Trail.clear();
        player2Trail.clear();
        player1Trail.add(new Point(player1));
        player2Trail.add(new Point(player2));
        dir1 = 'R';
        dir2 = 'L';
        winner = null;
        running = true;

        resetAbilities();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void stopAllAbilityTimers() {
        if (speedTimer1 != null) speedTimer1.stop();
        if (speedTimer2 != null) speedTimer2.stop();
        if (invisTimer1 != null) invisTimer1.stop();
        if (invisTimer2 != null) invisTimer2.stop();
        if (invertTimer1 != null) invertTimer1.stop();
        if (invertTimer2 != null) invertTimer2.stop();
    }

    private void resetAbilities() {
        player1AbilityUsed = false;
        player2AbilityUsed = false;
        player1SpeedBoost = false;
        player2SpeedBoost = false;
        player1Invisible = false;
        player2Invisible = false;
        player1ControlsInverted = false;
        player2ControlsInverted = false;
        player1SpeedTimer = 0;
        player2SpeedTimer = 0;
        player1InvisibleTimer = 0;
        player2InvisibleTimer = 0;
        player1InvertTimer = 0;
        player2InvertTimer = 0;
    }

    private void activatePlayer1Ability() {
        if (player1AbilityUsed || !running) return;

        player1AbilityUsed = true;

        if (player1Color == Color.BLUE) {
            reproducirSonidoHabilidad("velocidad");
            player1SpeedBoost = true;
            player1SpeedTimer = 30;
            speedTimer1 = new Timer(DELAY, e -> {
                player1SpeedTimer--;
                if (player1SpeedTimer <= 0) {
                    player1SpeedBoost = false;
                    speedTimer1.stop();
                }
            });
            speedTimer1.start();

        } else if (player1Color == Color.YELLOW) {
            reproducirSonidoHabilidad("invisibilidad");
            player1Invisible = true;
            player1InvisibleTimer = 30;
            invisTimer1 = new Timer(DELAY, e -> {
                player1InvisibleTimer--;
                if (player1InvisibleTimer <= 0) {
                    player1Invisible = false;
                    invisTimer1.stop();
                }
            });
            invisTimer1.start();

        } else if (player1Color == Color.RED) {
            reproducirSonidoHabilidad("explosion");
            explodeTrails(player1, player1Trail);

        } else if (player1Color == Color.GREEN) {
            reproducirSonidoHabilidad("confusion");
            player2ControlsInverted = true;
            player2InvertTimer = 30;
            invertTimer2 = new Timer(DELAY, e -> {
                player2InvertTimer--;
                if (player2InvertTimer <= 0) {
                    player2ControlsInverted = false;
                    invertTimer2.stop();
                }
            });
            invertTimer2.start();
        }
    }

    private void activatePlayer2Ability() {
        if (player2AbilityUsed || !running) return;

        player2AbilityUsed = true;

        if (player2Color == Color.BLUE) {
            reproducirSonidoHabilidad("velocidad");
            player2SpeedBoost = true;
            player2SpeedTimer = 30;
            speedTimer2 = new Timer(DELAY, e -> {
                player2SpeedTimer--;
                if (player2SpeedTimer <= 0) {
                    player2SpeedBoost = false;
                    speedTimer2.stop();
                }
            });
            speedTimer2.start();

        } else if (player2Color == Color.YELLOW) {
            reproducirSonidoHabilidad("invisibilidad");
            player2Invisible = true;
            player2InvisibleTimer = 30;
            invisTimer2 = new Timer(DELAY, e -> {
                player2InvisibleTimer--;
                if (player2InvisibleTimer <= 0) {
                    player2Invisible = false;
                    invisTimer2.stop();
                }
            });
            invisTimer2.start();

        } else if (player2Color == Color.RED) {
            reproducirSonidoHabilidad("explosion");
            explodeTrails(player2, player2Trail);

        } else if (player2Color == Color.GREEN) {
            reproducirSonidoHabilidad("confusion");
            player1ControlsInverted = true;
            player1InvertTimer = 30;
            invertTimer1 = new Timer(DELAY, e -> {
                player1InvertTimer--;
                if (player1InvertTimer <= 0) {
                    player1ControlsInverted = false;
                    invertTimer1.stop();
                }
            });
            invertTimer1.start();
        }
    }

    private void explodeTrails(Point center, LinkedList<Point> excludeTrail) {
        int explosionRange = UNIT * 5;

        if (excludeTrail != player1Trail) {
            Iterator<Point> iter1 = player1Trail.iterator();
            while (iter1.hasNext()) {
                Point p = iter1.next();
                if (Math.abs(p.x - center.x) <= explosionRange &&
                        Math.abs(p.y - center.y) <= explosionRange) {
                    iter1.remove();
                }
            }
        }

        if (excludeTrail != player2Trail) {
            Iterator<Point> iter2 = player2Trail.iterator();
            while (iter2.hasNext()) {
                Point p = iter2.next();
                if (Math.abs(p.x - center.x) <= explosionRange &&
                        Math.abs(p.y - center.y) <= explosionRange) {
                    iter2.remove();
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (running) {
            if (!player1Invisible) {
                g2d.setColor(player1Color);
                for (Point p : player1Trail) {
                    g2d.fillRect(p.x, p.y, UNIT, UNIT);
                }
            }

            if (!player2Invisible) {
                g2d.setColor(player2Color);
                for (Point p : player2Trail) {
                    g2d.fillRect(p.x, p.y, UNIT, UNIT);
                }
            }

            float player1Alpha = player1Invisible ? 0.3f : 1.0f;
            float player2Alpha = player2Invisible ? 0.3f : 1.0f;

            if (imgPlayer1 != null) {
                double angle1 = getRotationAngle(dir1);
                drawRotatedImage(g2d, imgPlayer1,
                        player1.x + imageOffsetX,
                        player1.y + imageOffsetY,
                        motoSize, motoSize,
                        angle1, player1Alpha);
            } else {
                g2d.setColor(player1Color);
                if (player1Invisible) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                }
                g2d.fillRect(player1.x, player1.y, UNIT, UNIT);
            }

            if (imgPlayer2 != null) {
                double angle2 = getRotationAngle(dir2);
                drawRotatedImage(g2d, imgPlayer2,
                        player2.x + imageOffsetX,
                        player2.y + imageOffsetY,
                        motoSize, motoSize,
                        angle2, player2Alpha);
            } else {
                g2d.setColor(player2Color);
                if (player2Invisible) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                }
                g2d.fillRect(player2.x, player2.y, UNIT, UNIT);
            }

            drawAbilityStatus(g2d);

        } else {
            if (winner != null) {
                g2d.setColor(Color.white);
                g2d.setFont(new Font("Arial", Font.BOLD, 30));
                FontMetrics fm = getFontMetrics(g2d.getFont());
                String message = winner + " gana! Presiona R para reiniciar.";
                g2d.drawString(message, (WIDTH - fm.stringWidth(message)) / 2, HEIGHT / 2);
            }
        }

        g2d.dispose();
    }

    private void drawAbilityStatus(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 14));

        g.setColor(player1Color);
        String p1Status = "J1: ";
        if (player1AbilityUsed) {
            p1Status += "USADA";
        } else {
            p1Status += getAbilityName(player1Color) + " (C)";
        }
        g.drawString(p1Status, 10, 30);

        if (player1SpeedBoost) {
            g.drawString("VELOCIDAD: " + (player1SpeedTimer/10 + 1) + "s", 10, 50);
        }
        if (player1Invisible) {
            g.drawString("INVISIBLE: " + (player1InvisibleTimer/10 + 1) + "s", 10, 50);
        }
        if (player1ControlsInverted) {
            g.drawString("CONTROLES INVERTIDOS: " + (player1InvertTimer/10 + 1) + "s", 10, 50);
        }

        g.setColor(player2Color);
        String p2Status = "J2: ";
        if (player2AbilityUsed) {
            p2Status += "USADA";
        } else {
            p2Status += getAbilityName(player2Color) + " (M)";
        }
        g.drawString(p2Status, WIDTH - 200, 30);

        if (player2SpeedBoost) {
            g.drawString("VELOCIDAD: " + (player2SpeedTimer/10 + 1) + "s", WIDTH - 200, 50);
        }
        if (player2Invisible) {
            g.drawString("INVISIBLE: " + (player2InvisibleTimer/10 + 1) + "s", WIDTH - 200, 50);
        }
        if (player2ControlsInverted) {
            g.drawString("CONTROLES INVERTIDOS: " + (player2InvertTimer/10 + 1) + "s", WIDTH - 200, 50);
        }
    }

    private String getAbilityName(Color color) {
        if (color == Color.BLUE) return "VELOCIDAD";
        if (color == Color.YELLOW) return "INVISIBILIDAD";
        if (color == Color.RED) return "EXPLOSIÓN";
        if (color == Color.GREEN) return "CONFUSIÓN";
        return "DESCONOCIDA";
    }

    public void move() {
        if (!running) return;

        int moves1 = player1SpeedBoost ? 2 : 1;
        for (int i = 0; i < moves1; i++) {
            switch (dir1) {
                case 'U': player1.y -= UNIT; break;
                case 'D': player1.y += UNIT; break;
                case 'L': player1.x -= UNIT; break;
                case 'R': player1.x += UNIT; break;
            }
            player1Trail.add(new Point(player1));
        }

        int moves2 = player2SpeedBoost ? 2 : 1;
        for (int i = 0; i < moves2; i++) {
            switch (dir2) {
                case 'U': player2.y -= UNIT; break;
                case 'D': player2.y += UNIT; break;
                case 'L': player2.x -= UNIT; break;
                case 'R': player2.x += UNIT; break;
            }
            player2Trail.add(new Point(player2));
        }
    }

    public void checkCollisions() {
        // Colisiones con bordes
        if (player1.x < 0 || player1.x >= WIDTH || player1.y < 0 || player1.y >= HEIGHT) {
            running = false;
            winner = "Jugador 2";
        } else if (player2.x < 0 || player2.x >= WIDTH || player2.y < 0 || player2.y >= HEIGHT) {
            running = false;
            winner = "Jugador 1";
        }

        // Colisiones con estela propia
        if (!player1Invisible) {
            for (Point p : player1Trail) {
                if (p.equals(player1) && p != player1Trail.getLast()) {
                    running = false;
                    winner = "Jugador 2";
                    break;
                }
            }
        }

        if (!player2Invisible) {
            for (Point p : player2Trail) {
                if (p.equals(player2) && p != player2Trail.getLast()) {
                    running = false;
                    winner = "Jugador 1";
                    break;
                }
            }
        }

        // Colisiones entre jugadores
        if (!player1Invisible) {
            for (Point p : player2Trail) {
                if (p.equals(player1)) {
                    running = false;
                    winner = "Jugador 2";
                    break;
                }
            }
        }

        if (!player2Invisible) {
            for (Point p : player1Trail) {
                if (p.equals(player2)) {
                    running = false;
                    winner = "Jugador 1";
                    break;
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollisions();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            // Controles Jugador 1
            case KeyEvent.VK_W:
                if (player1ControlsInverted) {
                    if (dir1 != 'U') dir1 = 'D';
                } else {
                    if (dir1 != 'D') dir1 = 'U';
                }
                break;
            case KeyEvent.VK_S:
                if (player1ControlsInverted) {
                    if (dir1 != 'D') dir1 = 'U';
                } else {
                    if (dir1 != 'U') dir1 = 'D';
                }
                break;
            case KeyEvent.VK_A:
                if (player1ControlsInverted) {
                    if (dir1 != 'L') dir1 = 'R';
                } else {
                    if (dir1 != 'R') dir1 = 'L';
                }
                break;
            case KeyEvent.VK_D:
                if (player1ControlsInverted) {
                    if (dir1 != 'R') dir1 = 'L';
                } else {
                    if (dir1 != 'L') dir1 = 'R';
                }
                break;

            // Controles Jugador 2
            case KeyEvent.VK_UP:
                if (player2ControlsInverted) {
                    if (dir2 != 'U') dir2 = 'D';
                } else {
                    if (dir2 != 'D') dir2 = 'U';
                }
                break;
            case KeyEvent.VK_DOWN:
                if (player2ControlsInverted) {
                    if (dir2 != 'D') dir2 = 'U';
                } else {
                    if (dir2 != 'U') dir2 = 'D';
                }
                break;
            case KeyEvent.VK_LEFT:
                if (player2ControlsInverted) {
                    if (dir2 != 'L') dir2 = 'R';
                } else {
                    if (dir2 != 'R') dir2 = 'L';
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (player2ControlsInverted) {
                    if (dir2 != 'R') dir2 = 'L';
                } else {
                    if (dir2 != 'L') dir2 = 'R';
                }
                break;

            // Habilidades especiales
            case KeyEvent.VK_C:
                activatePlayer1Ability();
                break;
            case KeyEvent.VK_M:
                activatePlayer2Ability();
                break;

            case KeyEvent.VK_R:
                if (!running) startGame();
                updateImages();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    // Método para liberar recursos de audio al cerrar
    public void dispose() {
        detenerMusicaJuego();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Juego de Motos Tron - 2 Jugadores");
        TronGame game = new TronGame(Color.RED, Color.YELLOW);
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);

        // Asegurar limpieza al cerrar
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                game.dispose();
            }
        });

        frame.setVisible(true);
    }
}