package com.labyrinthe.ui;

import com.labyrinthe.model.Cell;
import com.labyrinthe.model.Maze;
import com.labyrinthe.model.Position;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Boy barki yalla douma leine wakh dara , nieuwleine door li niou pareih , le montage sur la vidéo prend du temps boy
public class MazePanel extends JPanel {

    // Couleurs
    private static final Color C_WALL     = new Color(35,  35,  35);   // mur : très sombre
    private static final Color C_PASSAGE  = new Color(230, 230, 225);  // passage : gris clair
    private static final Color C_START    = new Color(39,  174, 96);   // S : vert
    private static final Color C_END      = new Color(192, 57,  43);   // E : rouge
    private static final Color C_PATH     = new Color(52,  152, 219);  // chemin solution : bleu
    private static final Color C_EXPLORED = new Color(174, 214, 241);  // exploration : bleu clair

    /**
     * Taille totale d'une cellule en pixels.
     * Les murs occupent tout cet espace ; les passages ont un padding interne.
     */
    private static final int CELL_PX = 22;

    /** Padding interne pour les cellules passage (crée l'effet "mur épais"). */
    private static final int PAD = 2;

    /** Rayon des coins arrondis des passages. */
    private static final int ARC = 5;

    private Maze         maze;
    private Set<Position> solutionPath = new HashSet<>();
    private Set<Position> exploredSet  = new HashSet<>();

    public MazePanel() {
        setOpaque(false); // fond transparent → le viewport BG_DARK montre en dessous
    }

    public void setMaze(Maze maze) {
        this.maze         = maze;
        this.solutionPath = new HashSet<>();
        this.exploredSet  = new HashSet<>();
        refreshSize();
        repaint();
    }

    public void setSolutionPath(List<Position> path) {
        this.solutionPath = path != null ? new HashSet<>(path) : new HashSet<>();
        repaint();
    }

    public void setExplored(List<Position> explored) {
        this.exploredSet = explored != null ? new HashSet<>(explored) : new HashSet<>();
        repaint();
    }

    public void clear() {
        this.solutionPath = new HashSet<>();
        this.exploredSet  = new HashSet<>();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (maze == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int offX = 4, offY = 4;

        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                Position pos  = new Position(r, c);
                Cell     cell = maze.getCell(r, c);
                int      x    = offX + c * CELL_PX;
                int      y    = offY + r * CELL_PX;

                if (cell == Cell.MUR) {
                    // Les murs remplissent TOUT l'espace → murs épais visuellement
                    g2.setColor(C_WALL);
                    g2.fillRect(x, y, CELL_PX, CELL_PX);
                } else {
                    // Le fond de la cellule reste C_BG (fond sombre = "jointure" entre passages)
                    // On dessine le passage avec un padding → crée l'illusion de murs épais
                    Color fill = pickPassageColor(cell, pos);
                    g2.setColor(fill);
                    g2.fillRoundRect(x + PAD, y + PAD, CELL_PX - PAD * 2, CELL_PX - PAD * 2, ARC, ARC);

                    // Label S / E
                    if (cell == Cell.DEPART || cell == Cell.SORTIE) {
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("SansSerif", Font.BOLD, CELL_PX / 2));
                        FontMetrics fm = g2.getFontMetrics();
                        String lbl = String.valueOf(cell.getSymbol());
                        int cellW = CELL_PX - PAD * 2;
                        int cellH = CELL_PX - PAD * 2;
                        g2.drawString(lbl,
                                x + PAD + (cellW - fm.stringWidth(lbl)) / 2,
                                y + PAD + (cellH + fm.getAscent() - fm.getDescent()) / 2);
                    }
                }
            }
        }
    }

    private Color pickPassageColor(Cell cell, Position pos) {
        if (cell == Cell.DEPART) return C_START;
        if (cell == Cell.SORTIE) return C_END;
        if (solutionPath.contains(pos)) return C_PATH;
        if (exploredSet.contains(pos))  return C_EXPLORED;
        return C_PASSAGE;
    }

    private void refreshSize() {
        if (maze == null) return;
        int w = maze.getCols() * CELL_PX + 8;
        int h = maze.getRows() * CELL_PX + 8;
        setPreferredSize(new Dimension(w, h));
        revalidate();
    }
}
