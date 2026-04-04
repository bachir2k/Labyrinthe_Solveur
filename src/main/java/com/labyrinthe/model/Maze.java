package com.labyrinthe.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Modèle principal du labyrinthe.
 * Stocke la grille sous forme de matrice 2D de {@link Cell},
 * et expose les opérations de navigation et de modification.
 */
public class Maze {

    private final Cell[][]  grid;
    private final int       rows;
    private final int       cols;
    private final Position  start;
    private final Position  end;

    public Maze(Cell[][] grid) {
        this.rows = grid.length;
        this.cols = grid[0].length;
        this.grid = deepCopy(grid);

        Position s = null, e = null;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == Cell.DEPART) s = new Position(r, c);
                else if (grid[r][c] == Cell.SORTIE) e = new Position(r, c);
            }
        }
        if (s == null || e == null) {
            throw new IllegalArgumentException(
                    "Le labyrinthe doit contenir un départ (S) et une sortie (E).");
        }
        this.start = s;
        this.end   = e;
    }

    // ---- Accesseurs ----

    public int getRows()        { return rows; }
    public int getCols()        { return cols; }
    public Position getStart()  { return start; }
    public Position getEnd()    { return end; }
    public Cell getCell(int r, int c) { return grid[r][c]; }
    public Cell getCell(Position p)   { return grid[p.getRow()][p.getCol()]; }

    public boolean isWalkable(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols && grid[r][c] != Cell.MUR;
    }

    public boolean isWalkable(Position p) {
        return isWalkable(p.getRow(), p.getCol());
    }

    /** Retourne les 4 voisins franchissables (haut, bas, gauche, droite). */
    public List<Position> neighbors(Position pos) {
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        List<Position> result = new ArrayList<>();
        for (int[] d : dirs) {
            int nr = pos.getRow() + d[0];
            int nc = pos.getCol() + d[1];
            if (isWalkable(nr, nc)) result.add(new Position(nr, nc));
        }
        return result;
    }

    /** Crée une copie de la grille pour l'affichage du chemin sans altérer l'original. */
    public Cell[][] copyGrid() { return deepCopy(grid); }

    private static Cell[][] deepCopy(Cell[][] src) {
        Cell[][] copy = new Cell[src.length][src[0].length];
        for (int r = 0; r < src.length; r++) {
            System.arraycopy(src[r], 0, copy[r], 0, src[r].length);
        }
        return copy;
    }
}
