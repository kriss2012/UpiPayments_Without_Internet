package com.demo.upimesh.config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;

/**
 * A lightweight Java Swing application wrapper that acts as the GUI control panel
 * for the UPI Offline Mesh Server.
 */
public class DesktopConsole {
    private JFrame frame;
    private JTextArea logTextArea;
    private JButton btnLaunch;
    private JButton btnShutdown;
    private JLabel lblStatus;

    public DesktopConsole() {
        createUI();
        redirectSystemStreams();
    }

    private void createUI() {
        // Set System Look & Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        frame = new JFrame("yono SBI — Offline UPI Control Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 550);
        frame.setLocationRelativeTo(null);

        // SBI branding colors
        Color sbiNavy = new Color(41, 32, 117);
        Color sbiCerulean = new Color(0, 181, 239);
        Color sbiBg = new Color(240, 244, 248);

        frame.getContentPane().setBackground(sbiBg);
        frame.setLayout(new BorderLayout(10, 10));

        // Header Panel (YONO SBI styling)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(sbiNavy);
        headerPanel.setLayout(new BorderLayout(5, 5));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("yono SBI");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JLabel lblSubtitle = new JLabel("OFFLINE UPI MESH SIMULATOR CONSOLE");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSubtitle.setForeground(sbiCerulean);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);

        frame.add(headerPanel, BorderLayout.NORTH);

        // Center Log Console Panel
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(sbiBg);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JLabel lblLogsTitle = new JLabel("System Console Logs");
        lblLogsTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblLogsTitle.setForeground(sbiNavy);
        centerPanel.add(lblLogsTitle, BorderLayout.NORTH);

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        logTextArea.setBackground(new Color(15, 23, 42)); // slate-900 background
        logTextArea.setForeground(new Color(14, 165, 233)); // sky-500 text
        logTextArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(logTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        frame.add(centerPanel, BorderLayout.CENTER);

        // Bottom Operations Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(sbiBg);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        lblStatus = new JLabel("Server Status: Booting Spring Boot...");
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblStatus.setForeground(new Color(217, 119, 6)); // Amber color
        bottomPanel.add(lblStatus);

        btnLaunch = new JButton("Open YONO SBI Dashboard");
        btnLaunch.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLaunch.setBackground(sbiCerulean);
        btnLaunch.setForeground(Color.WHITE);
        btnLaunch.setFocusPainted(false);
        btnLaunch.addActionListener((ActionEvent e) -> {
            try {
                Desktop.getDesktop().browse(new URI("http://localhost:8080"));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Unable to launch web browser: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        bottomPanel.add(btnLaunch);

        btnShutdown = new JButton("Stop Server");
        btnShutdown.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnShutdown.setBackground(new Color(220, 38, 38)); // Red color
        btnShutdown.setForeground(Color.WHITE);
        btnShutdown.setFocusPainted(false);
        btnShutdown.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        bottomPanel.add(btnShutdown);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Display Frame
        frame.setVisible(true);
    }

    /**
     * Mark the server as running and update status label.
     */
    public void setRunning(int port) {
        SwingUtilities.invokeLater(() -> {
            lblStatus.setText("Server Status: RUNNING on port " + port);
            lblStatus.setForeground(new Color(22, 163, 74)); // Green color
        });
    }

    /**
     * Captures and redirects stdout and stderr streams to the console text area.
     */
    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            private final StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) {
                appendChar((char) b);
            }

            @Override
            public void write(byte[] b, int off, int len) {
                appendString(new String(b, off, len));
            }

            private void appendChar(char c) {
                buffer.append(c);
                if (c == '\n') {
                    flushBuffer();
                }
            }

            private void appendString(String s) {
                buffer.append(s);
                if (s.contains("\n")) {
                    flushBuffer();
                }
            }

            private void flushBuffer() {
                String text = buffer.toString();
                buffer.setLength(0);
                SwingUtilities.invokeLater(() -> {
                    logTextArea.append(text);
                    // auto-scroll
                    logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
                });
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }
}
