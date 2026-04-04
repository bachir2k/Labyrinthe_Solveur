package com.labyrinthe.generator;

import com.labyrinthe.model.Cell;
import com.labyrinthe.model.Maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Génération aléatoire de labyrinthes parfaits par l'algorithme
 * de <b>Recursive Backtracking</b> (backtracking récursif).
 *
 * <p>Principe :</p>
 * <ol>
 *   <li>On initialise une grille entièrement remplie de murs.</li>
 *   <li>On part d'une cellule de départ et on creuse des passages
 *       vers des voisins non visités (distants de 2 cases).</li>
 *   <li>On mélange les directions aléatoirement à chaque étape.</li>
 *   <li>On place {@code S} en haut-gauche et {@code E} en bas-droite.</li>
 * </ol>
 *
 * <p>Résultat : un labyrinthe <em>parfait</em> — sans cycles,
 * avec exactement un chemin entre chaque paire de cases.</p>
 */
public class MazeGenerator {

    private MazeGenerator() { /* classe utilitaire, pas d'instanciation */ }

    /**
     * Génère un labyrinthe de taille {@code rows × cols} avec une graine reproductible.
     * Les dimensions sont ajustées pour être impaires si nécessaire (minimum 5).
     *
     * @param rows nombre de lignes souhaité
     * @param cols nombre de colonnes souhaité
     * @param seed graine aléatoire (pour reproductibilité)
     * @return un {@link Maze} prêt à l'emploi
     */
    public static Maze generate(int rows, int cols, long seed) {
        if (rows % 2 == 0) rows++;
        if (cols % 2 == 0) cols++;
        rows = Math.max(rows, 5);
        cols = Math.max(cols, 5);

        Cell[][] grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = Cell.MUR;
            }
        }

        carve(grid, 1, 1, new Random(seed));

        grid[1][1]           = Cell.DEPART;
        grid[rows - 2][cols - 2] = Cell.SORTIE;

        return new Maze(grid);
    }

    /** Surcharge avec graine aléatoire non déterministe. */
    public static Maze generate(int rows, int cols) {
        return generate(rows, cols, System.nanoTime());
    }

    // -------------------------------------------------------------------------

    private static void carve(Cell[][] grid, int row, int col, Random rng) {
        grid[row][col] = Cell.PASSAGE;

        List<int[]> dirs = new ArrayList<>(List.of(
                new int[]{-2, 0},
                new int[]{ 2, 0},
                new int[]{ 0,-2},
                new int[]{ 0, 2}
        ));
        Collections.shuffle(dirs, rng);

        for (int[] d : dirs) {
            int nextRow = row + d[0];
            int nextCol = col + d[1];
            int wallRow = row + d[0] / 2;
            int wallCol = col + d[1] / 2;

            if (inBounds(grid, nextRow, nextCol) && grid[nextRow][nextCol] == Cell.MUR) {
                grid[wallRow][wallCol] = Cell.PASSAGE;
                carve(grid, nextRow, nextCol, rng);
            }
        }
    }

    private static boolean inBounds(Cell[][] grid, int r, int c) {
        return r > 0 && r < grid.length - 1 && c > 0 && c < grid[0].length - 1;
    }
}
