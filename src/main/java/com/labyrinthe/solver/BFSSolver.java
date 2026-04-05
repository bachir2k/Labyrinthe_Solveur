package com.labyrinthe.solver;

import com.labyrinthe.model.Maze;
import com.labyrinthe.model.Position;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Résolution par Breadth-First Search (Parcours en Largeur).
 *
 * <p>Principe : une file (FIFO) explore le labyrinthe niveau par niveau,
 * garantissant ainsi que le premier chemin trouvé est le plus court.</p>
 *
 * <p><b>Structure utilisée :</b> {@link ArrayDeque} en mode file (add/poll).</p>
 * <p><b>Avantage :</b> chemin optimal garanti (nombre minimal de cases).</p>
 * <p><b>Inconvénient :</b> utilise plus de mémoire que DFS sur les grands labyrinthes.</p>
 */
public class BFSSolver implements MazeSolver {

    @Override
    public SolverResult solve(Maze maze) {
        long startTime = System.nanoTime();

        Map<Position, Position> parent   = new HashMap<>();
        Queue<Position>         queue    = new ArrayDeque<>();
        int                     explored = 0;

        Position origin = maze.getStart();
        Position target = maze.getEnd();

        queue.add(origin);
        parent.put(origin, null);

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            explored++;

            if (current.equals(target)) {
                long elapsed = System.nanoTime() - startTime;
                return new SolverResult("BFS", buildPath(parent, target), explored, elapsed);
            }

            for (Position neighbor : maze.neighbors(current)) {
                if (!parent.containsKey(neighbor)) {
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        long elapsed = System.nanoTime() - startTime;
        return new SolverResult("BFS", Collections.emptyList(), explored, elapsed);
    }

    private List<Position> buildPath(Map<Position, Position> parent, Position target) {
        List<Position> path = new ArrayList<>();
        Position step = target;
        while (step != null) {
            path.add(step);
            step = parent.get(step);
        }
        Collections.reverse(path);
        return path;
    }
}
