package com.labyrinthe.model;

/**
 * Représente le type d'une case du labyrinthe.
 * Chaque type est associé à son caractère textuel et à sa couleur ANSI pour l'affichage console.
 */
public enum Cell {

    MUR     ('#', "\u001B[90m"),   // gris foncé
    PASSAGE ('=', "\u001B[37m"),   // blanc
    DEPART  ('S', "\u001B[33m"),   // jaune
    SORTIE  ('E', "\u001B[31m"),   // rouge
    CHEMIN  ('+', "\u001B[32m"),   // vert
    EXPLORE ('.', "\u001B[34m");   // bleu (exploration en cours)

    private final char symbol;
    private final String ansiColor;

    Cell(char symbol, String ansiColor) {
        this.symbol    = symbol;
        this.ansiColor = ansiColor;
    }

    public char getSymbol()      { return symbol; }
    public String getAnsiColor() { return ansiColor; }

    /**
     * Convertit un caractère en type de cellule.
     * @throws IllegalArgumentException si le caractère est inconnu
     */
    public static Cell fromChar(char c) {
        for (Cell cell : values()) {
            if (cell.symbol == c) return cell;
        }
        throw new IllegalArgumentException("Caractère de labyrinthe inconnu : '" + c + "'");
    }
}
