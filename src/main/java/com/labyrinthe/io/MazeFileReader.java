package com.labyrinthe.io;

import com.labyrinthe.model.Cell;
import com.labyrinthe.model.Maze;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Lecture d'un labyrinthe depuis un fichier texte (.txt).
 *
 * <p><b>Formats supportés :</b></p>
 * <ul>
 *   <li><b>Format simple</b> : chaque ligne = une rangée, caractères # = # S E</li>
 *   <li><b>Format ASCII graphique</b> : format avec frontières +---+ | | (lignes paires = cellules)</li>
 * </ul>
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

        // Détecter le format
        if (isAsciiGraphicFormat(lines.get(0))) {
            return parseAsciiGraphicFormat(lines);
        } else {
            return parseSimpleFormat(lines);
        }
    }

    /**
     * Détecte si le format est ASCII graphique.
     * Les lignes commencent par + et contiennent des -.
     */
    private static boolean isAsciiGraphicFormat(String firstLine) {
        return firstLine.startsWith("+") && firstLine.contains("-");
    }

    /**
     * Parse le format ASCII graphique.
     * Les lignes paires (0, 2, 4...) contiennent les frontières.
     * Les lignes impaires (1, 3, 5...) contiennent les cellules entre les |.
     */
    private static Maze parseAsciiGraphicFormat(List<String> lines) {
        List<String> cellLines = new ArrayList<>();

        // Extraire les lignes contenant les cellules (lignes impaires : 1, 3, 5...)
        for (int i = 1; i < lines.size(); i += 2) {
            String line = lines.get(i);
            // Extraire les cellules entre les |
            List<Character> cells = extractCellsFromGraphicLine(line);
            StringBuilder row = new StringBuilder();
            for (char c : cells) {
                row.append(c);
            }
            cellLines.add(row.toString());
        }

        return parseSimpleFormat(cellLines);
    }

    /**
     * Extrait les caractères des cellules à partir d'une ligne du format ASCII.
     * Par exemple : "| S | = | E |" → ['S', '=', 'E']
     * Chaque cellule est séparée par | et peut contenir plusieurs caractères.
     */
    private static List<Character> extractCellsFromGraphicLine(String line) {
        List<Character> cells = new ArrayList<>();
        
        // Diviser par | pour obtenir les cellules
        String[] parts = line.split("\\|");
        
        for (int i = 1; i < parts.length; i++) {
            String cell = parts[i];
            
            // Chercher un caractère significatif dans cette cellule
            char cellChar = ' ';
            for (char c : cell.toCharArray()) {
                if (c != ' ') {
                    cellChar = c;
                    break;
                }
            }
            
            // Si aucun caractère significatif, c'est un passage par défaut
            if (cellChar == ' ') {
                cellChar = '=';
            }
            
            cells.add(cellChar);
        }
        
        return cells;
    }

    /**
     * Parse le format simple (format originel).
     */
    private static Maze parseSimpleFormat(List<String> lines) {
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
