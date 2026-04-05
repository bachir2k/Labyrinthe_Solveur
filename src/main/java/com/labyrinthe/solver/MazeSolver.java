package com.labyrinthe.solver;

import com.labyrinthe.model.Maze;

/**
 * Interface commune à tous les algorithmes de résolution de labyrinthe.
 * Chaque implémentation résout le labyrinthe et renvoie un {@link SolverResult}
 * contenant le chemin et les statistiques associées.
 */
public interface MazeSolver {

    /**
     * Résout le labyrinthe donné.
     *
     * @param maze le labyrinthe à résoudre (non modifié)
     * @return le résultat de la résolution
     */
    SolverResult solve(Maze maze);
}
