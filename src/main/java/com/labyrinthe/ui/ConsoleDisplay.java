package com.labyrinthe.ui;

import com.labyrinthe.model.Cell;
import com.labyrinthe.model.Maze;
import com.labyrinthe.model.Position;
import com.labyrinthe.solver.SolverResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Affichage textuel du labyrinthe dans le terminal avec codes couleur ANSI.
 *
 * <p>Palette :</p>
 * <ul>
 *   <li>Murs {@code #} — gris foncé</li>
 *   <li>Passages {@code =} — blanc</li>
 *   <li>Départ {@code S} — jaune</li>
 *   <li>Sortie {@code E} — rouge</li>
 *   <li>Chemin solution {@code +} — vert</li>
 *   <li>Cases explorées {@code .} — bleu (affichage de l'exploration)</li>
 * </ul>
 */
public class ConsoleDisplay {

    private static final String RESET = "\u001B[0m";
    private static final String BOLD  = "\u001B[1m";

    private ConsoleDisplay() { }

    // -------------------------------------------------------------------------
    // Affichage du labyrinthe
    // -------------------------------------------------------------------------

    /** Affiche le labyrinthe brut sans solution. */
    public static void printMaze(Maze maze) {
        printMazeWithHighlight(maze, null, null);
    }

    /** Affiche le labyrinthe avec le chemin solution marqué. */
    public static void printSolution(Maze maze, SolverResult result) {
        if (!result.hasSolution()) {
            printColored("  Aucune solution trouvée pour " + result.getAlgorithmName() + ".\n",
                    "\u001B[31m");
            return;
        }
        printMazeWithHighlight(maze, result.getPath(), null);
    }

    /** Affiche le labyrinthe avec chemin et cellules explorées (pour visualisation pédagogique). */
    public static void printExploration(Maze maze, List<Position> explored, List<Position> path) {
        printMazeWithHighlight(maze, path, explored);
    }

    private static void printMazeWithHighlight(Maze maze,
                                               List<Position> path,
                                               List<Position> explored) {
        Set<Position> pathSet     = path     != null ? new HashSet<>(path)     : Set.of();
        Set<Position> exploredSet = explored != null ? new HashSet<>(explored) : Set.of();

        System.out.println();
        for (int r = 0; r < maze.getRows(); r++) {
            System.out.print("  ");
            for (int c = 0; c < maze.getCols(); c++) {
                Position pos  = new Position(r, c);
                Cell     cell = maze.getCell(r, c);
                char     sym  = cell.getSymbol();
                String   color;

                if (cell == Cell.DEPART || cell == Cell.SORTIE) {
                    color = BOLD + cell.getAnsiColor();
                } else if (pathSet.contains(pos)) {
                    color = BOLD + Cell.CHEMIN.getAnsiColor();
                    sym   = Cell.CHEMIN.getSymbol();
                } else if (exploredSet.contains(pos)) {
                    color = Cell.EXPLORE.getAnsiColor();
                    sym   = Cell.EXPLORE.getSymbol();
                } else {
                    color = cell.getAnsiColor();
                }

                System.out.print(color + sym + RESET);
            }
            System.out.println();
        }
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Tableau comparatif
    // -------------------------------------------------------------------------

    /** Affiche un tableau comparatif des performances DFS vs BFS. */
    public static void printComparison(SolverResult dfs, SolverResult bfs) {
        String sep = "  " + "-".repeat(68);
        System.out.println();
        printHeader("  === COMPARAISON DFS vs BFS ===");
        System.out.println(sep);
        System.out.printf("  %-12s %-18s %-22s %-14s%n",
                "Algorithme", "Longueur chemin", "Nœuds explorés", "Temps (ms)");
        System.out.println(sep);
        printResultRow(dfs);
        printResultRow(bfs);
        System.out.println(sep);
        System.out.println();
        printAnalysis(dfs, bfs);
    }

    private static void printResultRow(SolverResult r) {
        String pathLen = r.hasSolution() ? String.valueOf(r.getPathLength()) : "N/A";
        System.out.printf("  %-12s %-18s %-22d %-14.3f%n",
                r.getAlgorithmName(), pathLen, r.getNodesExplored(), r.getDurationMs());
    }

    private static void printAnalysis(SolverResult dfs, SolverResult bfs) {
        System.out.println("  Analyse :");
        if (dfs.hasSolution() && bfs.hasSolution()) {
            int diff = dfs.getPathLength() - bfs.getPathLength();
            if (diff == 0) {
                System.out.println("  • Les deux algorithmes ont trouvé un chemin de même longueur.");
            } else {
                System.out.println("  • BFS a trouvé un chemin " + diff + " case(s) plus court que DFS.");
            }
            if (dfs.getDurationMs() < bfs.getDurationMs()) {
                System.out.println("  • DFS a été plus rapide en temps d'exécution.");
            } else {
                System.out.println("  • BFS a été plus rapide en temps d'exécution.");
            }
        }
        System.out.println("  • BFS garantit toujours le chemin le plus court (optimalité).");
        System.out.println("  • DFS utilise moins de mémoire mais ne garantit pas l'optimalité.");
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    public static void printHeader(String text) {
        System.out.println(BOLD + "\u001B[36m" + text + RESET);
    }

    public static void printSuccess(String text) {
        printColored("  ✔ " + text + "\n", "\u001B[32m");
    }

    public static void printError(String text) {
        printColored("  ✘ " + text + "\n", "\u001B[31m");
    }

    public static void printInfo(String text) {
        System.out.println("  " + text);
    }

    private static void printColored(String text, String color) {
        System.out.print(color + text + RESET);
    }
}
