package com.labyrinthe.generator;

import com.labyrinthe.model.Cell;
import com.labyrinthe.model.Maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Génération aléatoire de labyrinthes par l'algorithme de
 * <b>Recursive Backtracking</b> (backtracking récursif),
 * suivi d'une phase d'élargissement pour créer des passages larges.
 *
 * <p>Principe :</p>
 * <ol>
 *   <li>Grille initialisée en murs.</li>
 *   <li>Recursive Backtracking creuse des passages 1-cellule-large.</li>
 *   <li>Une passe de "brise-murs" retire des murs intérieurs sélectionnés
 *       pour créer des couloirs 2×2 et des zones ouvertes (rooms).</li>
 *   <li>Place {@code S} en haut-gauche et {@code E} en bas-droite.</li>
 * </ol>
 */
public class MazeGenerator {

    private MazeGenerator() { /* classe utilitaire */ }

    /**
     * Génère un labyrinthe de taille {@code rows × cols}.
     * Les dimensions sont ajustées pour être impaires si nécessaire (minimum 5).
     *
     * @param rows  nombre de lignes souhaité
     * @param cols  nombre de colonnes souhaité
     * @param seed  graine aléatoire (pour reproductibilité)
     * @return un {@link Maze} prêt à l'emploi
     */
    public static Maze generate(int rows, int cols, long seed) {
        if (rows % 2 == 0) rows++;
        if (cols % 2 == 0) cols++;
        rows = Math.max(rows, 5);
        cols = Math.max(cols, 5);

        Cell[][] grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                grid[r][c] = Cell.MUR;

        Random rng = new Random(seed);

        // Étape 1 : creuser le labyrinthe parfait (passages 1-cellule)
        carve(grid, 1, 1, rng);

        // Étape 2 : élargir les passages (crée des zones larges comme dans l'image)
        widenPaths(grid, new Random(seed ^ 0xDEADBEEFL), 0.30);

        // Départ et sortie
        grid[1][1]               = Cell.DEPART;
        grid[rows - 2][cols - 2] = Cell.SORTIE;

        return new Maze(grid);
    }

    /** Surcharge avec graine aléatoire non déterministe. */
    public static Maze generate(int rows, int cols) {
        return generate(rows, cols, System.nanoTime());
    }

    // -------------------------------------------------------------------------
    // Algorithme principal : Recursive Backtracking
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

    // -------------------------------------------------------------------------
    // Post-traitement : élargissement des chemins
    // -------------------------------------------------------------------------

    /**
     * Retire aléatoirement une fraction des murs intérieurs qui séparent
     * deux passages déjà ouverts. Cela crée des boucles et des zones plus larges
     * (couloirs 2-cellules et "rooms"), reproduisant le style de labyrinthe
     * visible dans l'image de référence.
     *
     * @param grid     la grille à modifier
     * @param rng      générateur de nombres aléatoires
     * @param fraction proportion de murs éligibles à supprimer (ex. 0.30 = 30%)
     */
    private static void widenPaths(Cell[][] grid, Random rng, double fraction) {
        int rows = grid.length;
        int cols = grid[0].length;

        for (int r = 2; r < rows - 2; r++) {
            for (int c = 2; c < cols - 2; c++) {
                if (grid[r][c] != Cell.MUR) continue;

                // Mur horizontal : entre deux cellules aux positions (r-1,c) et (r+1,c)
                boolean hWall = (r % 2 == 0) && (c % 2 == 1)
                        && grid[r - 1][c] == Cell.PASSAGE
                        && grid[r + 1][c] == Cell.PASSAGE;

                // Mur vertical : entre deux cellules aux positions (r,c-1) et (r,c+1)
                boolean vWall = (r % 2 == 1) && (c % 2 == 0)
                        && grid[r][c - 1] == Cell.PASSAGE
                        && grid[r][c + 1] == Cell.PASSAGE;

                // Mur coin : à l'intersection de deux couloirs, entre 4 passages
                boolean cornerWall = (r % 2 == 0) && (c % 2 == 0)
                        && r > 1 && c > 1 && r < rows - 2 && c < cols - 2
                        && grid[r - 1][c] == Cell.PASSAGE
                        && grid[r + 1][c] == Cell.PASSAGE
                        && grid[r][c - 1] == Cell.PASSAGE
                        && grid[r][c + 1] == Cell.PASSAGE;

                if ((hWall || vWall || cornerWall) && rng.nextDouble() < fraction) {
                    grid[r][c] = Cell.PASSAGE;
                }
            }
        }
    }

    // -------------------------------------------------------------------------

    private static boolean inBounds(Cell[][] grid, int r, int c) {
        return r > 0 && r < grid.length - 1 && c > 0 && c < grid[0].length - 1;
    }
}
