package com.labyrinthe.solver;

import com.labyrinthe.model.Cell;
import com.labyrinthe.model.Maze;
import com.labyrinthe.model.Position;

/**
 * Tests unitaires pour {@link BFSSolver}.
 */
class BFSSolverTest {

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError();
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError();
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected + ", but was: " + actual);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + " Expected: " + expected + ", but was: " + actual);
        }
    }

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

    private Maze unsolvableMaze() {
        Cell[][] grid = {
            {Cell.MUR,    Cell.MUR,    Cell.MUR},
            {Cell.MUR,    Cell.DEPART, Cell.MUR},
            {Cell.MUR,    Cell.MUR,    Cell.SORTIE},
        };
        return new Maze(grid);
    }

    void testBfsFindsSolution() {
        SolverResult result = new BFSSolver().solve(simpleMaze());
        assertTrue(result.hasSolution(), "BFS doit trouver une solution.");
    }

    void testBfsPathIsOptimal() {
        // Sur ce labyrinthe, le chemin le plus court passe par la ligne du haut : 5 cases (S inclus + E)
        SolverResult result = new BFSSolver().solve(simpleMaze());
        assertTrue(result.hasSolution());
        assertEquals(5, result.getPathLength(),
                "BFS doit trouver le chemin optimal de 5 cases.");
    }

    void testBfsPathStartsAtS() {
        SolverResult result = new BFSSolver().solve(simpleMaze());
        assertTrue(result.hasSolution());
        assertEquals(new Position(1, 1), result.getPath().getFirst());
    }

    void testBfsPathEndsAtE() {
        SolverResult result = new BFSSolver().solve(simpleMaze());
        assertTrue(result.hasSolution());
        assertEquals(new Position(1, 5), result.getPath().getLast());
    }

    void testBfsNoSolutionWhenBlocked() {
        SolverResult result = new BFSSolver().solve(unsolvableMaze());
        assertFalse(result.hasSolution());
    }

    void testBfsOptimalVsDfs() {
        Maze maze = simpleMaze();
        SolverResult bfs = new BFSSolver().solve(maze);
        SolverResult dfs = new DFSSolver().solve(maze);
        // BFS ne peut pas donner un chemin PLUS LONG que DFS sur ce labyrinthe
        assertTrue(bfs.getPathLength() <= dfs.getPathLength(),
                "BFS doit donner un chemin au moins aussi court que DFS.");
    }
}
