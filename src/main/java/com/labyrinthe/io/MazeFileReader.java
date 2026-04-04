package com.labyrinthe.io;

import com.labyrinthe.model.Cell;
import com.labyrinthe.model.Maze;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Lecture d'un labyrinthe depuis un fichier texte (.txt).
 *
 * <p>Format attendu : chaque ligne représente une rangée du labyrinthe.
 * Les caractères reconnus sont {@code #}, {@code =}, {@code S}, {@code E}.</p>
 */
public class MazeFileReader {

    private MazeFileReader() { }

    /**
     * Charge un labyrinthe depuis le fichier spécifié.
     *
     * @param path chemin vers le fichier texte
     * @return le labyrinthe chargé
     * @throws IOException              en cas d'erreur de lecture
     * @throws IllegalArgumentException si le fichier est vide ou mal formé
     */
    public static Maze read(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);

        // Supprimer les lignes vides en fin de fichier
        while (!lines.isEmpty() && lines.getLast().isBlank()) {
            lines.removeLast();
        }
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide : " + path);
        }

        int cols = lines.stream().mapToInt(String::length).max().orElse(0);
        Cell[][] grid = new Cell[lines.size()][cols];

        for (int r = 0; r < lines.size(); r++) {
            String line = lines.get(r);
            for (int c = 0; c < cols; c++) {
                char ch = c < line.length() ? line.charAt(c) : '#';
                grid[r][c] = Cell.fromChar(ch);
            }
        }

        return new Maze(grid);
    }
}
