package com.labyrinthe.solver;

import com.labyrinthe.model.Maze;
import com.labyrinthe.model.Position;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Résolution par Depth-First Search (Parcours en Profondeur).
 *
 * <p>Principe : une pile (LIFO) maintient les positions à visiter.
 * On plonge le plus loin possible avant de revenir en arrière (backtracking).</p>
 *
 * <p><b>Structure utilisée :</b> {@link ArrayDeque} en mode pile (push/pop).</p>
 * <p><b>Avantage :</b> faible consommation mémoire.</p>
 * <p><b>Inconvénient :</b> le chemin trouvé n'est pas nécessairement le plus court.</p>
 */
public class DFSSolver implements MazeSolver {

    @Override
    public SolverResult solve(Maze maze) {
        long startTime = System.nanoTime();

        Map<Position, Position> parent  = new HashMap<>();
        Deque<Position>         stack   = new ArrayDeque<>();
        int                     explored = 0;

        Position origin = maze.getStart();
        Position target = maze.getEnd();

        stack.push(origin);
        parent.put(origin, null);

        while (!stack.isEmpty()) {
            Position current = stack.pop();
            explored++;

            if (current.equals(target)) {
                long elapsed = System.nanoTime() - startTime;
                return new SolverResult("DFS", buildPath(parent, target), explored, elapsed);
            }

            for (Position neighbor : maze.neighbors(current)) {
                if (!parent.containsKey(neighbor)) {
                    parent.put(neighbor, current);
                    stack.push(neighbor);
                }
            }
        }

        long elapsed = System.nanoTime() - startTime;
        return new SolverResult("DFS", Collections.emptyList(), explored, elapsed);
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
