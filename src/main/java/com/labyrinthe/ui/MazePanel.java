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

    private static final Color C_WALL     = new Color(45,  45,  45);
    private static final Color C_PASSAGE  = new Color(240, 240, 235);
    private static final Color C_START    = new Color(39,  174, 96);
    private static final Color C_END      = new Color(192, 57,  43);
    private static final Color C_PATH     = new Color(52,  152, 219);
    private static final Color C_EXPLORED = new Color(174, 214, 241);
    private static final Color C_BG       = new Color(28,  28,  28);

    private static final int CELL_PX = 26; // pixels par cellule

    private Maze         maze;
    private Set<Position> solutionPath = new HashSet<>();
    private Set<Position> exploredSet  = new HashSet<>();

    public MazePanel() {
        setBackground(C_BG);
    }

    

    public void setMaze(Maze maze) {
        this.maze        = maze;
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

    //Leigui niou xol loumouy dioxé fiiii

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (maze == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int offX = 8, offY = 8;

        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                Position pos  = new Position(r, c);
                Cell     cell = maze.getCell(r, c);
                int      x    = offX + c * CELL_PX;
                int      y    = offY + r * CELL_PX;

                //Lii moy dernière partie cellule bi 
                g2.setColor(pickBackground(cell, pos));
                g2.fillRoundRect(x, y, CELL_PX - 2, CELL_PX - 2, 5, 5);

                // Label S / E
                if (cell == Cell.DEPART || cell == Cell.SORTIE) {
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("SansSerif", Font.BOLD, CELL_PX / 2 + 1));
                    FontMetrics fm = g2.getFontMetrics();
                    String lbl = String.valueOf(cell.getSymbol());
                    g2.drawString(lbl,
                            x + (CELL_PX - 2 - fm.stringWidth(lbl)) / 2,
                            y + (CELL_PX - 2 + fm.getAscent() - fm.getDescent()) / 2);
                }
            }
        }
    }

    private Color pickBackground(Cell cell, Position pos) {
        if (cell == Cell.MUR)    return C_WALL;
        if (cell == Cell.DEPART) return C_START;
        if (cell == Cell.SORTIE) return C_END;
        if (solutionPath.contains(pos)) return C_PATH;
        if (exploredSet.contains(pos))  return C_EXPLORED;
        return C_PASSAGE;
    }

    private void refreshSize() {
        if (maze == null) return;
        int w = maze.getCols() * CELL_PX + 16;
        int h = maze.getRows() * CELL_PX + 16;
        setPreferredSize(new Dimension(w, h));
        revalidate();
    }
}
