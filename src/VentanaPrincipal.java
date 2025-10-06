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

        inicializarMusicaMenu();

        fondoMenu = new ImageIcon(getClass().getResource("/assets/fondoMenu.png")).getImage();

        JPanel panelConFondo = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(fondoMenu, 0, 0, getWidth(), getHeight(), this);
            }
        };

// Calcular posición centrada horizontalmente
        int anchoVentana = 1400;
        int anchoBoton = 300;
        int altoBoton = 80;
        int xCentrado = (anchoVentana - anchoBoton) / 2;

        // Posición vertical más baja (más abajo en la pantalla)
        int yInicial = 430;
        int espaciado = 90;

        // Botón INICIAR JUEGO
        JButton jugarBtn = crearBotonMenu("/assets/jugar.png", 300, 80);
        jugarBtn.setBounds(xCentrado, yInicial, anchoBoton, altoBoton);
        jugarBtn.addActionListener(e -> abrirSeleccionJugadores());
        panelConFondo.add(jugarBtn);

        // Botón CONFIGURACIÓN
        JButton configBtn = crearBotonMenu("/assets/configuracion.png", 300, 80);
        configBtn.setBounds(xCentrado, yInicial + espaciado, anchoBoton, altoBoton);
        configBtn.addActionListener(e -> abrirConfiguracion());
        panelConFondo.add(configBtn);

        // Botón AYUDA
        JButton ayudaBtn = crearBotonMenu("/assets/comojugar.png", 300, 80);
        ayudaBtn.setBounds(xCentrado, yInicial + (espaciado * 2), anchoBoton, altoBoton);
        ayudaBtn.addActionListener(e -> mostrarAyuda());
        panelConFondo.add(ayudaBtn);

        // Botón SALIR
        JButton salirBtn = crearBotonMenu("/assets/salir.png", 300, 80);
        salirBtn.setBounds(xCentrado, yInicial + (espaciado * 3), anchoBoton, altoBoton);
        salirBtn.addActionListener(e -> {
            detenerMusicaJuego();
            System.exit(0);
        });
        panelConFondo.add(salirBtn);

        setContentPane(panelConFondo);
        setVisible(true);
    }

    private void inicializarMusicaMenu() {
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(
                    getClass().getResource("/assets/musicaMenu.wav")
            );

            musicaMenu = AudioSystem.getClip();
            musicaMenu.open(audioInput);
            musicaMenu.loop(Clip.LOOP_CONTINUOUSLY);
            musicaActiva = true;

            ajustarVolumen(musicaMenu, -10.0f);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error al cargar la música del menú: " + e.getMessage());
            musicaActiva = false;
        }
    }

    private void ajustarVolumen(Clip clip, float volumenDB) {
        try {
            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(volumenDB);
        } catch (Exception e) {
            System.out.println("No se pudo ajustar el volumen: " + e.getMessage());
        }
    }

    private void pausarMusicaMenu() {
        if (musicaMenu != null && musicaMenu.isRunning()) {
            musicaMenu.stop();
            musicaActiva = false;
        }
    }

    private void reanudarMusicaMenu() {
        if (musicaMenu != null && !musicaMenu.isRunning()) {
            musicaMenu.setFramePosition(0);
            musicaMenu.loop(Clip.LOOP_CONTINUOUSLY);
            musicaActiva = true;
        }
    }

    private void detenerMusicaJuego() {
        if (musicaMenu != null) {
            musicaMenu.stop();
            musicaMenu.close();
            musicaActiva = false;
        }
    }

    private JButton crearBotonMenu(String rutaImagen, int ancho, int alto) {
        JButton btn;
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(rutaImagen));
            Image img = icon.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            btn = new JButton(new ImageIcon(img));
        } catch (Exception e) {
            btn = new JButton("BOTÓN");
        }
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        return btn;
    }

    // Ventana de selección de jugadores MEJORADA
    private void abrirSeleccionJugadores() {
        JFrame frameSeleccion = new JFrame("Seleccionar Motos - Cycle Wars");
        frameSeleccion.setSize(1200, 650);
        frameSeleccion.setLocationRelativeTo(this);
        frameSeleccion.setLayout(new BorderLayout());

        JPanel panelPrincipal = new JPanel(new GridLayout(1, 2, 30, 20));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));
        panelPrincipal.setBackground(Color.BLACK);

        // Variables locales para tracking de selección
        final JButton[] botonSelJ1 = new JButton[1]; // Array para poder modificar desde lambda
        final JButton[] botonSelJ2 = new JButton[1];

        // Jugador 1
        JPanel panelJ1 = new JPanel(new BorderLayout(10, 10));
        panelJ1.setBackground(Color.BLACK);
        panelJ1.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));

        JLabel labelJ1 = new JLabel("⚡ JUGADOR 1 ⚡", SwingConstants.CENTER);
        labelJ1.setFont(new Font("Consolas", Font.BOLD, 28));
        labelJ1.setForeground(Color.CYAN);
        labelJ1.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JPanel opcionesJ1 = new JPanel(new GridLayout(2, 2, 15, 15));
        opcionesJ1.setBackground(Color.BLACK);
        opcionesJ1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Label para mostrar selección actual
        JLabel seleccionJ1 = new JLabel("Color: ROJO", SwingConstants.CENTER);
        seleccionJ1.setFont(new Font("Consolas", Font.BOLD, 18));
        seleccionJ1.setForeground(Color.RED);
        seleccionJ1.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton btnRojoJ1 = crearBotonColorMejorado("/assets/perfilRojo.png", Color.RED, "ROJO",
                botonSelJ1, Color.CYAN, seleccionJ1, true);
        JButton btnAzulJ1 = crearBotonColorMejorado("/assets/perfilAzul.png", Color.BLUE, "AZUL",
                botonSelJ1, Color.CYAN, seleccionJ1, true);
        JButton btnAmarilloJ1 = crearBotonColorMejorado("/assets/perfilAmarillo.png", Color.YELLOW, "AMARILLO",
                botonSelJ1, Color.CYAN, seleccionJ1, true);
        JButton btnVerdeJ1 = crearBotonColorMejorado("/assets/perfilVerde.png", Color.GREEN, "VERDE",
                botonSelJ1, Color.CYAN, seleccionJ1, true);

        opcionesJ1.add(btnRojoJ1);
        opcionesJ1.add(btnAzulJ1);
        opcionesJ1.add(btnAmarilloJ1);
        opcionesJ1.add(btnVerdeJ1);

        panelJ1.add(labelJ1, BorderLayout.NORTH);
        panelJ1.add(opcionesJ1, BorderLayout.CENTER);
        panelJ1.add(seleccionJ1, BorderLayout.SOUTH);

        // Jugador 2
        JPanel panelJ2 = new JPanel(new BorderLayout(10, 10));
        panelJ2.setBackground(Color.BLACK);
        panelJ2.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 3));

        JLabel labelJ2 = new JLabel("⚡ JUGADOR 2 ⚡", SwingConstants.CENTER);
        labelJ2.setFont(new Font("Consolas", Font.BOLD, 28));
        labelJ2.setForeground(Color.MAGENTA);
        labelJ2.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JPanel opcionesJ2 = new JPanel(new GridLayout(2, 2, 15, 15));
        opcionesJ2.setBackground(Color.BLACK);
        opcionesJ2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Label para mostrar selección actual
        JLabel seleccionJ2 = new JLabel("Color: AZUL", SwingConstants.CENTER);
        seleccionJ2.setFont(new Font("Consolas", Font.BOLD, 18));
        seleccionJ2.setForeground(Color.BLUE);
        seleccionJ2.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton btnRojoJ2 = crearBotonColorMejorado("/assets/perfilRojo.png", Color.RED, "ROJO",
                botonSelJ2, Color.MAGENTA, seleccionJ2, false);
        JButton btnAzulJ2 = crearBotonColorMejorado("/assets/perfilAzul.png", Color.BLUE, "AZUL",
                botonSelJ2, Color.MAGENTA, seleccionJ2, false);
        JButton btnAmarilloJ2 = crearBotonColorMejorado("/assets/perfilAmarillo.png", Color.YELLOW, "AMARILLO",
                botonSelJ2, Color.MAGENTA, seleccionJ2, false);
        JButton btnVerdeJ2 = crearBotonColorMejorado("/assets/perfilVerde.png", Color.GREEN, "VERDE",
                botonSelJ2, Color.MAGENTA, seleccionJ2, false);

        opcionesJ2.add(btnRojoJ2);
        opcionesJ2.add(btnAzulJ2);
        opcionesJ2.add(btnAmarilloJ2);
        opcionesJ2.add(btnVerdeJ2);

        panelJ2.add(labelJ2, BorderLayout.NORTH);
        panelJ2.add(opcionesJ2, BorderLayout.CENTER);
        panelJ2.add(seleccionJ2, BorderLayout.SOUTH);

        panelPrincipal.add(panelJ1);
        panelPrincipal.add(panelJ2);

        // Panel inferior con botones
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelInferior.setBackground(Color.BLACK);

        JButton volverBtn = new JButton("⬅ VOLVER");
        volverBtn.setFont(new Font("Consolas", Font.BOLD, 18));
        volverBtn.setBackground(Color.DARK_GRAY);
        volverBtn.setForeground(Color.WHITE);
        volverBtn.setPreferredSize(new Dimension(150, 45));
        volverBtn.addActionListener(e -> frameSeleccion.dispose());

        JButton comenzarBtn = new JButton("▶ COMENZAR");
        comenzarBtn.setFont(new Font("Consolas", Font.BOLD, 20));
        comenzarBtn.setBackground(new Color(0, 200, 0));
        comenzarBtn.setForeground(Color.WHITE);
        comenzarBtn.setPreferredSize(new Dimension(200, 50));
        comenzarBtn.addActionListener(e -> {
            if (colorJugador1.equals(colorJugador2)) {
                JOptionPane.showMessageDialog(frameSeleccion,
                        "⚠ Los jugadores no pueden elegir el mismo color",
                        "Error de selección",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            detenerMusicaJuego();
            frameSeleccion.dispose();
            abrirJuego();
        });

        panelInferior.add(volverBtn);
        panelInferior.add(comenzarBtn);

        frameSeleccion.add(panelPrincipal, BorderLayout.CENTER);
        frameSeleccion.add(panelInferior, BorderLayout.SOUTH);
        frameSeleccion.getContentPane().setBackground(Color.BLACK);
        frameSeleccion.setVisible(true);

        // Marcar selección inicial
        botonSelJ1[0] = btnRojoJ1;
        botonSelJ2[0] = btnAzulJ2;
        btnRojoJ1.setBorder(BorderFactory.createLineBorder(Color.CYAN, 4));
        btnAzulJ2.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 4));
    }

    // Crea un botón con imagen escalada y efecto de selección
    private JButton crearBotonColorMejorado(String rutaImagen, Color color, String nombreColor,
                                            JButton[] botonSeleccionado, Color colorBorde,
                                            JLabel labelSeleccion, boolean esJugador1) {
        JButton btn;
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(rutaImagen));
            Image img = icon.getImage().getScaledInstance(240, 220, Image.SCALE_SMOOTH);
            btn = new JButton(new ImageIcon(img));
        } catch (Exception e) {
            btn = new JButton(nombreColor);
            btn.setBackground(color);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Consolas", Font.BOLD, 16));
        }

        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        // Efecto hover
        JButton finalBtn1 = btn;
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (finalBtn1 != botonSeleccionado[0]) {
                    finalBtn1.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (finalBtn1 != botonSeleccionado[0]) {
                    finalBtn1.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                }
            }
        });

        // Acción al hacer clic
        JButton finalBtn = btn;
        btn.addActionListener(e -> {
            // Quitar borde del botón anterior
            if (botonSeleccionado[0] != null) {
                botonSeleccionado[0].setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            }

            // Actualizar color del jugador
            if (esJugador1) {
                colorJugador1 = color;
            } else {
                colorJugador2 = color;
            }

            // Actualizar label de selección
            labelSeleccion.setText("Color: " + nombreColor);
            labelSeleccion.setForeground(color);

            // Marcar nuevo botón seleccionado
            botonSeleccionado[0] = finalBtn;
            finalBtn.setBorder(BorderFactory.createLineBorder(colorBorde, 4));
        });

        return btn;
    }

    private void abrirConfiguracion() {
        JFrame frameConfig = new JFrame("Configuración - Cycle Wars");
        frameConfig.setSize(400, 200);
        frameConfig.setLocationRelativeTo(this);
        frameConfig.setLayout(new BorderLayout(10, 10));

        JPanel panelVolumen = new JPanel(new BorderLayout(5, 5));
        panelVolumen.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblVolumen = new JLabel("🔊 Volumen", JLabel.CENTER);
        lblVolumen.setFont(new Font("Arial", Font.BOLD, 16));

        JSlider sliderVolumen = new JSlider(-30, 6, -10);
        sliderVolumen.setPaintTicks(false);
        sliderVolumen.setPaintLabels(false);

        // Label para mostrar el porcentaje
        JLabel lblPorcentaje = new JLabel("56%", JLabel.CENTER);
        lblPorcentaje.setFont(new Font("Arial", Font.BOLD, 14));

        sliderVolumen.addChangeListener(e -> {
            // Convertir rango -30 a 6 en porcentaje 0% a 100%
            int valorDB = sliderVolumen.getValue();
            int porcentaje = (int) (((valorDB + 30) / 36.0) * 100);
            lblPorcentaje.setText(porcentaje + "%");

            if (!sliderVolumen.getValueIsAdjusting()) {
                ajustarVolumen(musicaMenu, (float) valorDB);
            }
        });

        panelVolumen.add(lblVolumen, BorderLayout.NORTH);
        panelVolumen.add(sliderVolumen, BorderLayout.CENTER);
        panelVolumen.add(lblPorcentaje, BorderLayout.SOUTH);

        frameConfig.add(panelVolumen, BorderLayout.CENTER);
        frameConfig.setVisible(true);
    }

    private void mostrarAyuda() {
        JFrame frameAyuda = new JFrame("⚡ CYCLE WARS - Guía de Juego");
        frameAyuda.setSize(900, 700);
        frameAyuda.setLocationRelativeTo(this);
        frameAyuda.setLayout(new BorderLayout());
        frameAyuda.getContentPane().setBackground(Color.BLACK);

        // Panel principal con scroll
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(Color.BLACK);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Título principal
        JLabel titulo = new JLabel("⚡ GUÍA DE BATALLA ⚡");
        titulo.setFont(new Font("Consolas", Font.BOLD, 36));
        titulo.setForeground(Color.CYAN);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(titulo);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 30)));

        // Sección OBJETIVO
        panelPrincipal.add(crearSeccionAyuda(
                "🎯 OBJETIVO DEL JUEGO",
                "Sobrevive más tiempo que tu oponente. Tu moto deja un rastro de energía mortal.\n" +
                        "Si tocas cualquier estela (incluida la tuya), ¡GAME OVER!\n" +
                        "Usa estrategia, velocidad y habilidades para encerrar a tu rival.",
                new Color(0, 255, 255)
        ));

        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 25)));

        // Sección CONTROLES
        JPanel seccionControles = new JPanel();
        seccionControles.setLayout(new BoxLayout(seccionControles, BoxLayout.Y_AXIS));
        seccionControles.setBackground(Color.BLACK);
        seccionControles.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tituloControles = new JLabel("🎮 CONTROLES");
        tituloControles.setFont(new Font("Consolas", Font.BOLD, 24));
        tituloControles.setForeground(new Color(255, 100, 255));
        seccionControles.add(tituloControles);
        seccionControles.add(Box.createRigidArea(new Dimension(0, 10)));

        // Jugador 1
        JPanel panelJ1 = crearPanelJugador(
                "JUGADOR 1",
                "W A S D - Movimiento",
                "C - Habilidad Especial",
                Color.CYAN
        );
        seccionControles.add(panelJ1);
        seccionControles.add(Box.createRigidArea(new Dimension(0, 10)));

        // Jugador 2
        JPanel panelJ2 = crearPanelJugador(
                "JUGADOR 2",
                "↑ ↓ ← → - Movimiento",
                "M - Habilidad Especial",
                new Color(255, 100, 255)
        );
        seccionControles.add(panelJ2);
        seccionControles.add(Box.createRigidArea(new Dimension(0, 10)));

        // Controles generales
        JLabel lblGeneral = new JLabel("⌨ TECLA R - Reiniciar Partida");
        lblGeneral.setFont(new Font("Consolas", Font.BOLD, 16));
        lblGeneral.setForeground(Color.WHITE);
        seccionControles.add(lblGeneral);

        panelPrincipal.add(seccionControles);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 25)));

        // Sección HABILIDADES ESPECIALES
        JLabel tituloHabilidades = new JLabel("✨ HABILIDADES ESPECIALES");
        tituloHabilidades.setFont(new Font("Consolas", Font.BOLD, 24));
        tituloHabilidades.setForeground(new Color(255, 215, 0));
        tituloHabilidades.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelPrincipal.add(tituloHabilidades);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel subtituloHab = new JLabel("Cada jugador puede usar su habilidad UNA VEZ por partida. ¡Úsala sabiamente!");
        subtituloHab.setFont(new Font("Consolas", Font.ITALIC, 13));
        subtituloHab.setForeground(new Color(200, 200, 200));
        subtituloHab.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelPrincipal.add(subtituloHab);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 15)));

        // Habilidad AZUL
        panelPrincipal.add(crearHabilidad(
                "🔵 AZUL - TURBO BOOST",
                "Duración: 5 segundos",
                "Tu moto alcanza el DOBLE de velocidad",
                "Perfecta para escapar de situaciones peligrosas o sorprender al rival",
                new Color(0, 150, 255)
        ));

        // Habilidad AMARILLA
        panelPrincipal.add(crearHabilidad(
                "🟡 AMARILLO - MODO FANTASMA",
                "Duración: 3 segundos",
                "Atraviesa estelas sin morir",
                "Ideal para escapar cuando estás rodeado o crear jugadas arriesgadas",
                new Color(255, 215, 0)
        ));

        // Habilidad ROJA
        panelPrincipal.add(crearHabilidad(
                "🔴 ROJO - PULSO EMP",
                "Efecto: Instantáneo",
                "Destruye todas las estelas en un radio cercano",
                "Crea espacio libre cuando el mapa está saturado",
                new Color(255, 50, 50)
        ));

        // Habilidad VERDE
        panelPrincipal.add(crearHabilidad(
                "🟢 VERDE - HACK NEURONAL",
                "Duración: 3 segundos",
                "Invierte los controles del enemigo",
                "Causa confusión total. ¡El rival no sabrá qué le pasó!",
                new Color(50, 255, 50)
        ));

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(panelPrincipal);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(Color.BLACK);

        // Botón cerrar
        JButton btnCerrar = new JButton("✖ CERRAR");
        btnCerrar.setFont(new Font("Consolas", Font.BOLD, 18));
        btnCerrar.setBackground(new Color(50, 50, 50));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setPreferredSize(new Dimension(150, 45));
        btnCerrar.addActionListener(e -> frameAyuda.dispose());

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setBackground(Color.BLACK);
        panelBoton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelBoton.add(btnCerrar);

        frameAyuda.add(scrollPane, BorderLayout.CENTER);
        frameAyuda.add(panelBoton, BorderLayout.SOUTH);
        frameAyuda.setVisible(true);
    }

    private JPanel crearSeccionAyuda(String titulo, String contenido, Color colorTitulo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Consolas", Font.BOLD, 24));
        lblTitulo.setForeground(colorTitulo);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea txtContenido = new JTextArea(contenido);
        txtContenido.setFont(new Font("Consolas", Font.PLAIN, 15));
        txtContenido.setForeground(Color.WHITE);
        txtContenido.setBackground(new Color(20, 20, 20));
        txtContenido.setEditable(false);
        txtContenido.setLineWrap(true);
        txtContenido.setWrapStyleWord(true);
        txtContenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorTitulo, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        panel.add(lblTitulo);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(txtContenido);

        return panel;
    }

    private JPanel crearPanelJugador(String nombre, String movimiento, String habilidad, Color colorBorde) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(15, 15, 15));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorBorde, 3),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNombre = new JLabel("⚡ " + nombre);
        lblNombre.setFont(new Font("Consolas", Font.BOLD, 18));
        lblNombre.setForeground(colorBorde);
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblMov = new JLabel("  • " + movimiento);
        lblMov.setFont(new Font("Consolas", Font.PLAIN, 15));
        lblMov.setForeground(Color.WHITE);
        lblMov.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHab = new JLabel("  • " + habilidad);
        lblHab.setFont(new Font("Consolas", Font.PLAIN, 15));
        lblHab.setForeground(Color.WHITE);
        lblHab.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblNombre);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(lblMov);
        panel.add(lblHab);

        return panel;
    }

    private JPanel crearHabilidad(String nombre, String duracion, String efecto, String estrategia, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(15, 15, 15));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 3),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(850, 150));

        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(new Font("Consolas", Font.BOLD, 18));
        lblNombre.setForeground(color);
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDuracion = new JLabel("⏱ " + duracion);
        lblDuracion.setFont(new Font("Consolas", Font.PLAIN, 14));
        lblDuracion.setForeground(new Color(200, 200, 200));
        lblDuracion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblEfecto = new JLabel("⚡ " + efecto);
        lblEfecto.setFont(new Font("Consolas", Font.BOLD, 15));
        lblEfecto.setForeground(Color.WHITE);
        lblEfecto.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblEstrategia = new JLabel("💡 " + estrategia);
        lblEstrategia.setFont(new Font("Consolas", Font.ITALIC, 13));
        lblEstrategia.setForeground(new Color(180, 180, 180));
        lblEstrategia.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblNombre);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(lblDuracion);
        panel.add(lblEfecto);
        panel.add(Box.createRigidArea(new Dimension(0, 3)));
        panel.add(lblEstrategia);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        return panel;
    }

    private void abrirJuego() {
        // Detener la música del menú antes de cerrar
        detenerMusicaJuego();

        JFrame frameJuego = new JFrame("Juego de Motos Tron - 2 Jugadores con Habilidades");
        TronGame tronGame = new TronGame(colorJugador1, colorJugador2);
        frameJuego.add(tronGame);
        frameJuego.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameJuego.pack();
        frameJuego.setLocationRelativeTo(null);
        frameJuego.setResizable(false);
        frameJuego.setVisible(true);

        // Cerrar la ventana principal
        this.dispose();

        // Opcional: Si quieres que vuelva al menú al cerrar el juego
        frameJuego.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                tronGame.dispose();
                // Opcional: Reabrir el menú principal
                // new VentanaPrincipal();
            }
        });
    }



    @Override
    public void dispose() {
        detenerMusicaJuego();
        super.dispose();
    }

    public static void main(String[] args) {
        new VentanaPrincipal();
    }
}