package com.labyrinthe.solver;

import com.labyrinthe.model.Cell;
import com.labyrinthe.model.Maze;
import com.labyrinthe.model.Position;

/**
 * Tests unitaires pour {@link DFSSolver}.
 */
class DFSSolverTest {

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + " Expected: " + expected + ", but was: " + actual);
        }
    }

    /** Labyrinthe simple du sujet : solution directe ligne du haut. */
    private Maze simpleMaze() {
        Cell[][] grid = {
            {Cell.MUR,    Cell.MUR,    Cell.MUR,    Cell.MUR,    Cell.MUR,    Cell.MUR,    Cell.MUR},
            {Cell.MUR,    Cell.DEPART, Cell.PASSAGE, Cell.PASSAGE, Cell.PASSAGE, Cell.SORTIE, Cell.MUR},
            {Cell.MUR,    Cell.PASSAGE, Cell.MUR,   Cell.MUR,    Cell.MUR,    Cell.PASSAGE, Cell.MUR},
            {Cell.MUR,    Cell.PASSAGE, Cell.PASSAGE, Cell.PASSAGE, Cell.PASSAGE, Cell.PASSAGE, Cell.MUR},
            {Cell.MUR,    Cell.MUR,    Cell.MUR,    Cell.MUR,    Cell.MUR,    Cell.MUR,    Cell.MUR},
        };
        return new Maze(grid);
    }

    /** Labyrinthe sans solution (sortie murée). */
    private Maze unsolvableMaze() {
        Cell[][] grid = {
            {Cell.MUR,    Cell.MUR,    Cell.MUR},
            {Cell.MUR,    Cell.DEPART, Cell.MUR},
            {Cell.MUR,    Cell.MUR,    Cell.SORTIE},
        };
        return new Maze(grid);
    }

    void testDfsFindsSolution() {
        SolverResult result = new DFSSolver().solve(simpleMaze());
        assertTrue(result.hasSolution(), "DFS doit trouver une solution sur ce labyrinthe.");
    }

    void testDfsPathStartsAtS() {
        SolverResult result = new DFSSolver().solve(simpleMaze());
        assertTrue(result.hasSolution(), "Une solution est attendue.");
        Position first = result.getPath().get(0);
        assertEquals(new Position(1, 1), first, "Le chemin doit commencer au départ S (1,1).");
    }

    void testDfsPathEndsAtE() {
        SolverResult result = new DFSSolver().solve(simpleMaze());
        assertTrue(result.hasSolution(), "Une solution est attendue.");
        Position last = result.getPath().get(result.getPath().size() - 1);
        assertEquals(new Position(1, 5), last, "Le chemin doit se terminer à la sortie E (1,5).");
    }

    void testDfsNoSolutionWhenBlocked() {
        SolverResult result = new DFSSolver().solve(unsolvableMaze());
        assertFalse(result.hasSolution(), "DFS ne doit pas trouver de solution sur un labyrinthe bloqué.");
    }

    void testDfsExploredCountPositive() {
        SolverResult result = new DFSSolver().solve(simpleMaze());
        assertTrue(result.getNodesExplored() > 0, "DFS doit explorer au moins une cellule.");
    }

    void testDfsDurationPositive() {
        SolverResult result = new DFSSolver().solve(simpleMaze());
        assertTrue(result.getDurationNs() >= 0, "La durée ne peut pas être négative.");
    }
}
