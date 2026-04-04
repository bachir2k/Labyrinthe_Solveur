package com.labyrinthe;

import com.labyrinthe.generator.MazeGenerator;
import com.labyrinthe.io.MazeFileReader;
import com.labyrinthe.io.MazeFileWriter;
import com.labyrinthe.model.Maze;
import com.labyrinthe.solver.BFSSolver;
import com.labyrinthe.solver.DFSSolver;
import com.labyrinthe.solver.SolverResult;
import com.labyrinthe.ui.ConsoleDisplay;
import com.labyrinthe.ui.GraphicalDisplay;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;


 *
public class Main {

    public static void main(String[] args) {
        if (args.length > 0 && "console".equalsIgnoreCase(args[0])) {
            runConsoleMenu();
        } else {
            runGuiMode();
        }
    }

   

    private static void runConsoleMenu() {
        Scanner scanner = new Scanner(System.in);
        Maze    maze    = null;

        System.out.println();
        ConsoleDisplay.printHeader("╔══════════════════════════════════════╗");
        ConsoleDisplay.printHeader("║   Résolution de Labyrinthe — ESP     ║");
        ConsoleDisplay.printHeader("║   Master 1 GLSI/SRT | Dr M. DIOP     ║");
        ConsoleDisplay.printHeader("╚══════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            printMenu(maze);
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> maze = handleLoadFile(scanner);
                case "2" -> maze = handleGenerate(scanner);
                case "3" -> {
                    if (maze == null) { ConsoleDisplay.printError("Aucun labyrinthe chargé."); break; }
                    ConsoleDisplay.printHeader("\n  === Labyrinthe ===");
                    ConsoleDisplay.printMaze(maze);
                }
                case "4" -> {
                    if (maze == null) { ConsoleDisplay.printError("Aucun labyrinthe chargé."); break; }
                    SolverResult result = new DFSSolver().solve(maze);
                    ConsoleDisplay.printHeader("\n  === Résolution DFS ===");
                    ConsoleDisplay.printInfo(result.summary());
                    ConsoleDisplay.printSolution(maze, result);
                }
                case "5" -> {
                    if (maze == null) { ConsoleDisplay.printError("Aucun labyrinthe chargé."); break; }
                    SolverResult result = new BFSSolver().solve(maze);
                    ConsoleDisplay.printHeader("\n  === Résolution BFS ===");
                    ConsoleDisplay.printInfo(result.summary());
                    ConsoleDisplay.printSolution(maze, result);
                }
                case "6" -> {
                    if (maze == null) { ConsoleDisplay.printError("Aucun labyrinthe chargé."); break; }
                    SolverResult dfs = new DFSSolver().solve(maze);
                    SolverResult bfs = new BFSSolver().solve(maze);
                    ConsoleDisplay.printComparison(dfs, bfs);
                }
                case "7" -> {
                    if (maze == null) { ConsoleDisplay.printError("Aucun labyrinthe chargé."); break; }
                    handleSaveFile(scanner, maze);
                }
                case "0" -> {
                    ConsoleDisplay.printInfo("Au revoir !");
                    running = false;
                }
                default  -> ConsoleDisplay.printError("Option invalide, réessayez.");
            }
        }
        scanner.close();
    }

    private static void printMenu(Maze maze) {
        String mazeStatus = maze == null
                ? "\u001B[31m[aucun labyrinthe chargé]\u001B[0m"
                : "\u001B[32m[" + maze.getRows() + " × " + maze.getCols() + "]\u001B[0m";
        System.out.println();
        ConsoleDisplay.printHeader("  ─── Menu principal " + mazeStatus + " ───");
        ConsoleDisplay.printInfo("  1. Charger un labyrinthe depuis un fichier");
        ConsoleDisplay.printInfo("  2. Générer un labyrinthe aléatoire");
        ConsoleDisplay.printInfo("  3. Afficher le labyrinthe");
        ConsoleDisplay.printInfo("  4. Résoudre avec DFS");
        ConsoleDisplay.printInfo("  5. Résoudre avec BFS");
        ConsoleDisplay.printInfo("  6. Comparer DFS vs BFS");
        ConsoleDisplay.printInfo("  7. Sauvegarder le labyrinthe dans un fichier");
        ConsoleDisplay.printInfo("  0. Quitter");
        System.out.print("  Votre choix : ");
    }

    private static Maze handleLoadFile(Scanner scanner) {
        System.out.print("  Chemin du fichier (.txt) : ");
        String path = scanner.nextLine().trim();
        try {
            Maze m = MazeFileReader.read(Path.of(path));
            ConsoleDisplay.printSuccess("Labyrinthe chargé depuis : " + path);
            return m;
        } catch (IOException | IllegalArgumentException e) {
            ConsoleDisplay.printError("Impossible de charger : " + e.getMessage());
            return null;
        }
    }

    private static Maze handleGenerate(Scanner scanner) {
        System.out.print("  Nombre de lignes (impair, ex: 15) : ");
        int rows = parseIntSafe(scanner.nextLine(), 15);
        System.out.print("  Nombre de colonnes (impair, ex: 15) : ");
        int cols = parseIntSafe(scanner.nextLine(), 15);
        Maze m = MazeGenerator.generate(rows, cols);
        ConsoleDisplay.printSuccess("Labyrinthe généré : " + m.getRows() + " × " + m.getCols());
        return m;
    }

    private static void handleSaveFile(Scanner scanner, Maze maze) {
        System.out.print("  Nom du fichier de sortie (.txt) : ");
        String path = scanner.nextLine().trim();
        if (!path.endsWith(".txt")) path += ".txt";
        try {
            MazeFileWriter.write(maze, Path.of(path));
            ConsoleDisplay.printSuccess("Sauvegardé dans : " + path);
        } catch (IOException e) {
            ConsoleDisplay.printError("Erreur d'écriture : " + e.getMessage());
        }
    }

    private static int parseIntSafe(String input, int defaultValue) {
        try { return Integer.parseInt(input.trim()); }
        catch (NumberFormatException e) { return defaultValue; }
    }
//Oh lala meitina deih boy ah  
    

    private static void runGuiMode() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }
            new GraphicalDisplay().setVisible(true);
        });
    }
}

//Laylaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
