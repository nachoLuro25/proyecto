import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.io.IOException;

public class VentanaPrincipal extends JFrame {

    private Color colorJugador1 = Color.RED;
    private Color colorJugador2 = Color.BLUE;

    private Image fondoMenu;

    // Variables para el sonido
    private Clip musicaMenu;
    private boolean musicaActiva = false;

    public VentanaPrincipal() {
        setTitle("CYCLE WARS - Menú Principal");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Inicializar la música del menú
        inicializarMusicaMenu();

        fondoMenu = new ImageIcon(getClass().getResource("/assets/fondoMenu.png")).getImage();

        JPanel panelConFondo = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(fondoMenu, 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Botón INICIAR JUEGO
        JButton jugarBtn = crearBotonMenu("/assets/btnJugar.png", 220, 50);
        jugarBtn.setBounds(600, 300, 220, 50);
        jugarBtn.addActionListener(e -> abrirSeleccionJugadores());
        panelConFondo.add(jugarBtn);

        // Botón CONFIGURACIÓN
        JButton configBtn = crearBotonMenu("/assets/configuracion.png", 220, 50);
        configBtn.setBounds(600, 370, 220, 50);
        configBtn.addActionListener(e -> abrirConfiguracion());
        panelConFondo.add(configBtn);

        // Botón AYUDA
        JButton ayudaBtn = crearBotonMenu("/assets/btnAyuda.png", 220, 50);
        ayudaBtn.setBounds(600, 440, 220, 50);
        ayudaBtn.addActionListener(e -> mostrarAyuda());
        panelConFondo.add(ayudaBtn);

        // Botón SALIR
        JButton salirBtn = crearBotonMenu("/assets/salir.png", 220, 50);
        salirBtn.setBounds(600, 510, 220, 50);
        salirBtn.addActionListener(e -> {
            detenerMusicaMenu();
            System.exit(0);
        });
        panelConFondo.add(salirBtn);

        // Botón para pausar/reanudar música
        JButton musicaBtn = new JButton(musicaActiva ? "🔇 Silenciar" : "🔊 Música");
        musicaBtn.setBounds(50, 50, 120, 30);
        musicaBtn.setFont(new Font("Arial", Font.BOLD, 12));
        musicaBtn.addActionListener(e -> {
            if (musicaActiva) {
                pausarMusicaMenu();
                musicaBtn.setText("🔊 Música");
            } else {
                reanudarMusicaMenu();
                musicaBtn.setText("🔇 Silenciar");
            }
        });
        panelConFondo.add(musicaBtn);

        setContentPane(panelConFondo);
        setVisible(true);
    }

    // Método para inicializar la música del menú
    private void inicializarMusicaMenu() {
        try {
            // Cargar el archivo de música desde la carpeta assets
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(
                    getClass().getResource("/assets/musicaMenu.wav") // Cambia por tu archivo
            );

            musicaMenu = AudioSystem.getClip();
            musicaMenu.open(audioInput);

            // Reproducir en bucle infinito
            musicaMenu.loop(Clip.LOOP_CONTINUOUSLY);
            musicaActiva = true;

            // Ajustar el volumen (opcional)
            ajustarVolumen(musicaMenu, -10.0f); // Reducir 10 dB

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error al cargar la música del menú: " + e.getMessage());
            musicaActiva = false;
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

    // Método para pausar la música del menú
    private void pausarMusicaMenu() {
        if (musicaMenu != null && musicaMenu.isRunning()) {
            musicaMenu.stop();
            musicaActiva = false;
        }
    }

    // Método para reanudar la música del menú
    private void reanudarMusicaMenu() {
        if (musicaMenu != null && !musicaMenu.isRunning()) {
            musicaMenu.setFramePosition(0); // Empezar desde el inicio
            musicaMenu.loop(Clip.LOOP_CONTINUOUSLY);
            musicaActiva = true;
        }
    }

    // Método para detener completamente la música del menú
    private void detenerMusicaMenu() {
        if (musicaMenu != null) {
            musicaMenu.stop();
            musicaMenu.close();
            musicaActiva = false;
        }
    }

    // Método para reproducir sonido de click en botones - ELIMINADO

    // Método para crear botones con imagen en el menú
    private JButton crearBotonMenu(String rutaImagen, int ancho, int alto) {
        JButton btn;
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(rutaImagen));
            Image img = icon.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            btn = new JButton(new ImageIcon(img));
        } catch (Exception e) {
            btn = new JButton("BOTÓN"); // fallback si no encuentra la imagen
        }
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        return btn;
    }

    // Ventana de selección de jugadores
    private void abrirSeleccionJugadores() {
        JFrame frameSeleccion = new JFrame("Seleccionar Colores - Cycle Wars");
        frameSeleccion.setSize(1200, 550);
        frameSeleccion.setLocationRelativeTo(this);
        frameSeleccion.setLayout(new BorderLayout());

        JPanel panelPrincipal = new JPanel(new GridLayout(1, 2, 20, 20));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelPrincipal.setBackground(Color.BLACK);

        // Jugador 1
        JPanel panelJ1 = new JPanel(new BorderLayout());
        panelJ1.setBackground(Color.BLACK);

        JLabel labelJ1 = new JLabel("JUGADOR 1", SwingConstants.CENTER);
        labelJ1.setFont(new Font("Consolas", Font.BOLD, 26));
        labelJ1.setForeground(Color.CYAN);

        JPanel opcionesJ1 = new JPanel(new GridLayout(2, 2, 10, 10));
        opcionesJ1.setBackground(Color.BLACK);
        opcionesJ1.add(crearBotonColor("/assets/perfilRojo.png", Color.RED, true));
        opcionesJ1.add(crearBotonColor("/assets/perfilAzul.png", Color.BLUE, true));
        opcionesJ1.add(crearBotonColor("/assets/perfilAmarillo.png", Color.YELLOW, true));
        opcionesJ1.add(crearBotonColor("/assets/perfilVerde.png", Color.GREEN, true));

        panelJ1.add(labelJ1, BorderLayout.NORTH);
        panelJ1.add(opcionesJ1, BorderLayout.CENTER);

        // Jugador 2
        JPanel panelJ2 = new JPanel(new BorderLayout());
        panelJ2.setBackground(Color.BLACK);

        JLabel labelJ2 = new JLabel("JUGADOR 2", SwingConstants.CENTER);
        labelJ2.setFont(new Font("Consolas", Font.BOLD, 26));
        labelJ2.setForeground(Color.CYAN);

        JPanel opcionesJ2 = new JPanel(new GridLayout(2, 2, 10, 10));
        opcionesJ2.setBackground(Color.BLACK);
        opcionesJ2.add(crearBotonColor("/assets/perfilRojo.png", Color.RED, false));
        opcionesJ2.add(crearBotonColor("/assets/perfilAzul.png", Color.BLUE, false));
        opcionesJ2.add(crearBotonColor("/assets/perfilAmarillo.png", Color.YELLOW, false));
        opcionesJ2.add(crearBotonColor("/assets/perfilVerde.png", Color.GREEN, false));

        panelJ2.add(labelJ2, BorderLayout.NORTH);
        panelJ2.add(opcionesJ2, BorderLayout.CENTER);

        panelPrincipal.add(panelJ1);
        panelPrincipal.add(panelJ2);

        // Botón comenzar
        JButton comenzarBtn = new JButton("COMENZAR");
        comenzarBtn.setFont(new Font("Consolas", Font.BOLD, 20));
        comenzarBtn.addActionListener(e -> {
            // AQUÍ ES DONDE SE DETIENE LA MÚSICA DEL MENÚ
            detenerMusicaMenu();
            frameSeleccion.dispose();
            abrirJuego();
        });

        frameSeleccion.add(panelPrincipal, BorderLayout.CENTER);
        frameSeleccion.add(comenzarBtn, BorderLayout.SOUTH);
        frameSeleccion.getContentPane().setBackground(Color.BLACK);
        frameSeleccion.setVisible(true);
    }

    // Crea un botón con imagen escalada para selección de personajes
    private JButton crearBotonColor(String rutaImagen, Color color, boolean esJugador1) {
        JButton btn;
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(rutaImagen));
            Image img = icon.getImage().getScaledInstance(270, 250, Image.SCALE_SMOOTH);
            btn = new JButton(new ImageIcon(img));
        } catch (Exception e) {
            btn = new JButton();
            btn.setBackground(color);
        }
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);

        // Acción al hacer clic (selección de personaje)
        btn.addActionListener(e -> {
            if (esJugador1) {
                colorJugador1 = color;
            } else {
                colorJugador2 = color;
            }
        });

        return btn;
    }

    private void abrirConfiguracion() {
        JFrame frameConfig = new JFrame("Configuración - Cycle Wars");
        frameConfig.setSize(400, 200);
        frameConfig.setLocationRelativeTo(this);
        frameConfig.setLayout(new BorderLayout());

        JLabel lblBrillo = new JLabel("Brillo del juego", JLabel.CENTER);
        JSlider sliderBrillo = new JSlider(0, 100, 50);
        sliderBrillo.setMajorTickSpacing(25);
        sliderBrillo.setPaintTicks(true);
        sliderBrillo.setPaintLabels(true);

        frameConfig.add(lblBrillo, BorderLayout.NORTH);
        frameConfig.add(sliderBrillo, BorderLayout.CENTER);

        frameConfig.setVisible(true);
    }

    private void mostrarAyuda() {
        String mensaje = """
        📋 CONTROLES:
        • Jugador 1: WASD para moverse, C para habilidad
        • Jugador 2: Flechas para moverse, M para habilidad
        • R para reiniciar partida

        🚀 Objetivo:
        Encierra a tu rival con tu estela de luz.
        Usa tu habilidad especial en el momento justo.

        🟦 HABILIDADES ESPECIALES (una vez por partida):

        🔵 AZUL - VELOCIDAD SUPERIOR:
        • Duración: 5 segundos
        • Efecto: Velocidad doble
        • Estrategia: Ideal para escapar o alcanzar

        🟡 AMARILLO - INVISIBILIDAD:
        • Duración: 3 segundos
        • Efecto: Atravesar estelas
        • Estrategia: Perfecto en zonas congestionadas

        🔴 ROJO - EXPLOSIÓN:
        • Instantáneo
        • Efecto: Destruye estelas cercanas
        • Estrategia: Crea espacio libre

        🟢 VERDE - CONFUSIÓN:
        • Duración: 3 segundos
        • Efecto: Invierte controles del rival
        • Estrategia: Desorientar al enemigo
        """;

        JTextArea area = new JTextArea(mensaje);
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        area.setBackground(Color.BLACK);
        area.setForeground(Color.CYAN);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(600, 500));

        JOptionPane.showMessageDialog(this, scroll, "¿Cómo jugar?", JOptionPane.INFORMATION_MESSAGE);
    }

    private void abrirJuego() {
        JFrame frameJuego = new JFrame("Juego de Motos Tron - 2 Jugadores con Habilidades");
        frameJuego.add(new TronGame(colorJugador1, colorJugador2));
        frameJuego.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameJuego.pack();
        frameJuego.setLocationRelativeTo(null);
        frameJuego.setVisible(true);

        // Opcional: Si quieres que la música del menú se reanude cuando cierren el juego
        frameJuego.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                // Reanudar música del menú cuando se cierre el juego
                reanudarMusicaMenu();
            }
        });
    }

    // Método para liberar recursos al cerrar la ventana principal
    @Override
    public void dispose() {
        detenerMusicaMenu();
        super.dispose();
    }

    public static void main(String[] args) {
        new VentanaPrincipal();
    }
}