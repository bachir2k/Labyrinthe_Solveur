package com.labyrinthe.io;

import com.labyrinthe.model.Maze;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Sauvegarde d'un labyrinthe dans un fichier texte (.txt).
 * Utile pour exporter un labyrinthe généré aléatoirement afin de le réutiliser.
 */
public class MazeFileWriter {

    private MazeFileWriter() { }

    /**
     * Écrit le labyrinthe dans le fichier spécifié.
     *
     * @param maze le labyrinthe à sauvegarder
     * @param path chemin de destination
     * @throws IOException en cas d'erreur d'écriture
     */
    public static void write(Maze maze, Path path) throws IOException {
        try (PrintWriter writer = new PrintWriter(path.toFile())) {
            for (int r = 0; r < maze.getRows(); r++) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < maze.getCols(); c++) {
                    sb.append(maze.getCell(r, c).getSymbol());
                }
                writer.println(sb);
            }
        }
    }
}
