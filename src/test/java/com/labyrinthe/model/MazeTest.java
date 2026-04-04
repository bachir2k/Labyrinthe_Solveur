package com.labyrinthe.model;

import com.labyrinthe.generator.MazeGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour {@link Maze} et {@link MazeGenerator}.
 */
class MazeTest {

    private Maze buildSimple() {
        Cell[][] grid = {
            {Cell.MUR,    Cell.MUR,    Cell.MUR,    Cell.MUR,    Cell.MUR},
            {Cell.MUR,    Cell.DEPART, Cell.PASSAGE, Cell.SORTIE, Cell.MUR},
            {Cell.MUR,    Cell.MUR,    Cell.MUR,    Cell.MUR,    Cell.MUR},
        };
        return new Maze(grid);
    }

    @Test
    void testStartPositionDetected() {
        Maze maze = buildSimple();
        assertEquals(new Position(1, 1), maze.getStart());
    }

    @Test
    void testEndPositionDetected() {
        Maze maze = buildSimple();
        assertEquals(new Position(1, 3), maze.getEnd());
    }

    @Test
    void testWallNotWalkable() {
        Maze maze = buildSimple();
        assertFalse(maze.isWalkable(0, 0), "Un mur ne doit pas être franchissable.");
    }

    @Test
    void testPassageIsWalkable() {
        Maze maze = buildSimple();
        assertTrue(maze.isWalkable(1, 2), "Un passage doit être franchissable.");
    }

    @Test
    void testOutOfBoundsNotWalkable() {
        Maze maze = buildSimple();
        assertFalse(maze.isWalkable(-1, 0));
        assertFalse(maze.isWalkable(99, 99));
    }

    @Test
    void testNeighborsCount() {
        Maze maze = buildSimple();
        List<Position> neighbors = maze.neighbors(new Position(1, 2));
        // Voisins de (1,2) : (1,1)=DEPART et (1,3)=SORTIE — les deux sont franchissables
        assertEquals(2, neighbors.size());
    }

    @Test
    void testMazeWithoutStartThrows() {
        Cell[][] bad = {
            {Cell.MUR, Cell.MUR},
            {Cell.MUR, Cell.SORTIE},
        };
        assertThrows(IllegalArgumentException.class, () -> new Maze(bad));
    }

    @Test
    void testGeneratedMazeHasStartAndEnd() {
        Maze maze = MazeGenerator.generate(11, 11, 123L);
        assertNotNull(maze.getStart());
        assertNotNull(maze.getEnd());
    }

    @Test
    void testGeneratedMazeDimensions() {
        Maze maze = MazeGenerator.generate(10, 10, 0L); // doit ajuster à 11×11
        assertEquals(11, maze.getRows());
        assertEquals(11, maze.getCols());
    }
}
