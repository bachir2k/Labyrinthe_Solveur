package com.labyrinthe.solver;

import com.labyrinthe.model.Position;

import java.util.List;

/**
 * Encapsule le résultat d'un algorithme de résolution.
 * Contient le chemin solution, le nombre de nœuds explorés et le temps d'exécution.
 */
public class SolverResult {

    private final String        algorithmName;
    private final List<Position> path;
    private final int           nodesExplored;
    private final long          durationNs;

    public SolverResult(String algorithmName, List<Position> path,
                        int nodesExplored, long durationNs) {
        this.algorithmName = algorithmName;
        this.path          = path;
        this.nodesExplored = nodesExplored;
        this.durationNs    = durationNs;
    }

    public boolean         hasSolution()      { return path != null && !path.isEmpty(); }
    public String          getAlgorithmName() { return algorithmName; }
    public List<Position>  getPath()          { return path; }
    public int             getNodesExplored() { return nodesExplored; }
    public long            getDurationNs()    { return durationNs; }
    public double          getDurationMs()    { return durationNs / 1_000_000.0; }
    public int             getPathLength()    { return hasSolution() ? path.size() : 0; }

    /** Résumé formaté pour affichage console. */
    public String summary() {
        if (!hasSolution()) {
            return String.format("[%s] Aucune solution | Explorés : %d | Temps : %.3f ms",
                    algorithmName, nodesExplored, getDurationMs());
        }
        return String.format("[%s] Chemin : %d cases | Explorés : %d | Temps : %.3f ms",
                algorithmName, getPathLength(), nodesExplored, getDurationMs());
    }
}
