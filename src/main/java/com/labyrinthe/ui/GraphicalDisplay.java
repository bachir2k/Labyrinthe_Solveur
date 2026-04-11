package com.labyrinthe.ui;

import com.labyrinthe.generator.MazeGenerator;
import com.labyrinthe.io.MazeFileReader;
import com.labyrinthe.io.MazeFileWriter;
import com.labyrinthe.model.Maze;
import com.labyrinthe.model.Position;
import com.labyrinthe.solver.BFSSolver;
import com.labyrinthe.solver.DFSSolver;
import com.labyrinthe.solver.MazeSolver;
import com.labyrinthe.solver.SolverResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Boy comment vous faites pour utiliser VScode , j'deteste , moi Intellij reik wala sunlime text lay use 
public class GraphicalDisplay extends JFrame {


    private final MazePanel   mazePanel  = new MazePanel();
    private final JTextArea   statsArea  = new JTextArea(20, 63);
    private final JComboBox<String> algoBox =
            new JComboBox<>(new String[]{"DFS", "BFS", "DFS + BFS (comparaison)"});
    private final JSpinner rowsSpin = new JSpinner(new SpinnerNumberModel(15, 5, 99, 2));
    private final JSpinner colsSpin = new JSpinner(new SpinnerNumberModel(15, 5, 99, 2));
    private final JSlider  speedSlider = new JSlider(JSlider.HORIZONTAL, 10, 200, 60);
    private final JButton  btnSolve    = darkButton("▶  Résoudre");
    private final JButton  btnStop     = darkButton("⏹  Arrêter");
    private final JLabel   statusLabel = new JLabel("Prêt.");

    // ---- État ----
    private Maze            currentMaze;
    private volatile boolean animating = false;
    private Thread          animThread;

    // ---- Couleurs UI ----
    private static final Color BG_DARK   = new Color(15, 25, 45); // marine un peu dark
    private static final Color PANEL_BG  = new Color(0, 0, 0);
    private static final Color TEXT_FG   = new Color(220, 220, 220);
    private static final Color ACCENT    = new Color(15, 25, 45);

    // -------------------------------------------------------------------------

    public GraphicalDisplay() {
        super("Résolution de Labyrinthe — ESP Dakar M1");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
        buildUI();
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    // Voilà nak , leigui fi moy interface biiiii

    private void buildUI() {
        setLayout(new BorderLayout(6, 6));

        add(buildToolbar(),    BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildStatusBar(),  BorderLayout.SOUTH);
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        toolbar.setBackground(PANEL_BG);
        toolbar.setBorder(new EmptyBorder(4, 6, 4, 6));
//On génére nak
        toolbar.add(darkLabel("Lignes:"));
        toolbar.add(styleSpinner(rowsSpin));
        toolbar.add(darkLabel("Colonnes:"));
        toolbar.add(styleSpinner(colsSpin));

        JButton btnGen  = darkButton("🎲 Générer");
        JButton btnLoad = darkButton("📂 Charger");
        JButton btnSave = darkButton("💾 Sauvegarder");
        toolbar.add(btnGen);
        toolbar.add(btnLoad);
        toolbar.add(btnSave);

        toolbar.add(new JSeparator(SwingConstants.VERTICAL));

        
        toolbar.add(darkLabel("Algo:"));
        styleCombo(algoBox);
        toolbar.add(algoBox);

        styleButton(btnSolve, ACCENT);
        styleButton(btnStop, new Color(192, 57, 43));
        btnStop.setEnabled(false);
        toolbar.add(btnSolve);
        toolbar.add(btnStop);

        JButton btnClear = darkButton("⟳ Reset");
        toolbar.add(btnClear);

        // Écouteurs
        btnGen.addActionListener(e  -> generateMaze());
        btnLoad.addActionListener(e -> loadFromFile());
        btnSave.addActionListener(e -> saveToFile());
        btnSolve.addActionListener(e -> startSolve());
        btnStop.addActionListener(e  -> stopAnimation());
        btnClear.addActionListener(e -> {
            if (currentMaze != null) {
                mazePanel.clear();
                statsArea.setText("");
                setStatus("Réinitialisé.");
            }
        });

        return toolbar;
    }

    private JPanel buildCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(6, 6));
        center.setBackground(BG_DARK);
        center.setBorder(new EmptyBorder(0, 8, 0, 8));

        JScrollPane mazeScroll = new JScrollPane(mazePanel);
        mazeScroll.setBackground(BG_DARK);
        mazeScroll.getViewport().setBackground(BG_DARK);
        mazeScroll.setPreferredSize(new Dimension(560, 480));
        center.add(mazeScroll, BorderLayout.CENTER);

        statsArea.setEditable(false);
        statsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        statsArea.setBackground(new Color(20, 20, 20));
        statsArea.setForeground(new Color(160, 210, 160));
        statsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(60, 60, 60)), "Statistiques",
                        0, 0, null, TEXT_FG),
                new EmptyBorder(4, 6, 4, 6)));
        center.add(new JScrollPane(statsArea), BorderLayout.EAST);

        return center;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.setBackground(PANEL_BG);
        statusLabel.setForeground(new Color(180, 180, 180));
        bar.add(statusLabel);
        return bar;
    }

    // ---- Actions ----

    private void generateMaze() {
        int rows = (int) rowsSpin.getValue();
        int cols = (int) colsSpin.getValue();
        currentMaze = MazeGenerator.generate(rows, cols);
        mazePanel.setMaze(currentMaze);
        statsArea.setText("");
        setStatus("Labyrinthe généré : " + rows + " × " + cols);
        pack();
    }

    private void loadFromFile() {
        JFileChooser fc = new JFileChooser(".");
        fc.setFileFilter(new FileNameExtensionFilter("Fichiers texte (*.txt)", "txt"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try {
                currentMaze = MazeFileReader.read(f.toPath());
                mazePanel.setMaze(currentMaze);
                statsArea.setText("");
                setStatus("Chargé : " + f.getName()
                        + " (" + currentMaze.getRows() + " × " + currentMaze.getCols() + ")");
                pack();
            } catch (IOException | IllegalArgumentException ex) {
                showError("Erreur : " + ex.getMessage());
            }
        }
    }

    private void saveToFile() {
        if (currentMaze == null) { showError("Aucun labyrinthe à sauvegarder."); return; }
        JFileChooser fc = new JFileChooser(".");
        fc.setFileFilter(new FileNameExtensionFilter("Fichiers texte (*.txt)", "txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().endsWith(".txt")) f = new File(f.getPath() + ".txt");
            try {
                MazeFileWriter.write(currentMaze, f.toPath());
                setStatus("Sauvegardé : " + f.getName());
            } catch (IOException ex) {
                showError("Erreur d'écriture : " + ex.getMessage());
            }
        }
    }

    private void startSolve() {
        if (currentMaze == null) { showError("Veuillez d'abord charger ou générer un labyrinthe."); return; }
        stopAnimation();

        String algo = (String) algoBox.getSelectedItem();
        btnSolve.setEnabled(false);
        btnStop.setEnabled(true);
        animating = true;

        animThread = new Thread(() -> {
            try {
                runAnimation(algo);
            } finally {
                SwingUtilities.invokeLater(() -> {
                    btnSolve.setEnabled(true);
                    btnStop.setEnabled(false);
                    animating = false;
                });
            }
        }, "anim-thread");
        animThread.setDaemon(true);
        animThread.start();
    }

    private void stopAnimation() {
        animating = false;
        if (animThread != null) {
            animThread.interrupt();
        }
    }

    private void runAnimation(String algo) {
        StringBuilder stats = new StringBuilder();

        if ("DFS".equals(algo) || "DFS + BFS (comparaison)".equals(algo)) {
            SolverResult dfs = new DFSSolver().solve(currentMaze);
            if ("DFS".equals(algo)) {
                animateSolution(dfs);
            }
            stats.append(dfs.summary()).append("\n");

            if ("DFS + BFS (comparaison)".equals(algo)) {
                SolverResult bfs = new BFSSolver().solve(currentMaze);
                stats.append(bfs.summary()).append("\n");
                stats.append(buildComparisonText(dfs, bfs));
                animateSolution(bfs); // on anime BFS (chemin optimal)
            }
        } else {
            SolverResult bfs = new BFSSolver().solve(currentMaze);
            animateSolution(bfs);
            stats.append(bfs.summary()).append("\n");
        }

        String finalStats = stats.toString();
        SwingUtilities.invokeLater(() -> statsArea.setText(finalStats));
    }

    /**
     * Anime la résolution pas-à-pas :
     * 1) montre les cellules explorées une à une (bleu pâle)
     * 2) puis affiche le chemin final (bleu vif)
     */
    private void animateSolution(SolverResult result) {
        if (!result.hasSolution()) {
            SwingUtilities.invokeLater(() -> setStatus(result.getAlgorithmName() + " : aucune solution."));
            return;
        }

        List<Position> path     = result.getPath();
        int            delay    = speedSlider.getValue();
        List<Position> explored = new ArrayList<>(path); // on anime le chemin comme exploration simplifiée

        // Étape 1 : animation de l'exploration
        for (int i = 0; i < explored.size() && animating; i++) {
            final List<Position> current = new ArrayList<>(explored.subList(0, i + 1));
            SwingUtilities.invokeLater(() -> {
                mazePanel.setExplored(current);
                mazePanel.setSolutionPath(null);
                setStatus(result.getAlgorithmName() + " — exploration en cours...");
            });
            sleep(delay);
        }

        if (!animating) return;

        // Étape 2 : afficher le chemin final
        SwingUtilities.invokeLater(() -> {
            mazePanel.setExplored(null);
            mazePanel.setSolutionPath(path);
            setStatus(result.getAlgorithmName() + " — chemin trouvé : "
                    + result.getPathLength() + " cases | "
                    + result.getNodesExplored() + " nœuds explorés | "
                    + String.format("%.3f ms", result.getDurationMs()));
        });
    }

    private String buildComparisonText(SolverResult dfs, SolverResult bfs) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n── Analyse ──\n");
        if (dfs.hasSolution() && bfs.hasSolution()) {
            int diff = dfs.getPathLength() - bfs.getPathLength();
            sb.append(diff == 0
                    ? "• Même longueur de chemin pour les deux algorithmes.\n"
                    : "• BFS : chemin " + diff + " case(s) plus court.\n");
        }
        sb.append("• BFS garantit l'optimalité du chemin.\n");
        sb.append("• DFS : mémoire réduite, sans garantie d'optimalité.\n");
        sb.append("• Le chemin affiché est celui du BFS.\n");
        return sb.toString();
    }

    // ---- Helpers ----

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private void setStatus(String msg) {
        statusLabel.setText("  " + msg);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    // ---- Style ----

    private JLabel darkLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_FG);
        return lbl;
    }

    private JButton darkButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        return btn;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD));
    }

    private JSpinner styleSpinner(JSpinner sp) {
        sp.setBackground(PANEL_BG);
        sp.getEditor().getComponent(0).setBackground(new Color(55, 55, 55));
        ((JSpinner.DefaultEditor) sp.getEditor()).getTextField().setForeground(TEXT_FG);
        sp.setPreferredSize(new Dimension(60, 26));
        return sp;
    }

    private void styleCombo(JComboBox<String> box) {
        box.setBackground(new Color(0,0,0));
        box.setForeground(Color.black);
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                setBackground(sel ? ACCENT : new Color(55, 55, 55));
                setForeground(Color.WHITE);
                return this;
            }
        });
    }
}
