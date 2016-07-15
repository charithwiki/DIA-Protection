package edu.usc.cps.alg;

import edu.usc.cps.graph.Edge;
import edu.usc.cps.graph.Graph;
import edu.usc.cps.graph.Vertex;

import java.util.*;

/**
 * Created by charith on 7/13/16.
 * Disclaimer:Optimized and Modified version of the SSSP implementation in http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
 */
public class DijkstraAlgorithm {

    private final Set<Vertex> nodes;
    private final Set<Edge> edges;
    private Set<Vertex> settledNodes;
    private Set<Vertex> unSettledNodes;
    private Map<Vertex, Vertex> predecessors;
    private Map<Vertex, Integer> distance;
    private Graph graph;


    /**
     *
     * @param graph Graph
     */
    public DijkstraAlgorithm(Graph graph) {
        // create a copy of the array so that we can operate on this array
        this.nodes = new HashSet<Vertex>(graph.getVertices());
        this.edges = new HashSet<Edge>(graph.getEdges());
        this.graph = graph;
    }

    /**
     * Calculate SSSP from a given source vertex to all other  vertices in graph
     * @param source vertex
     */
    public void execute(Vertex source) {
        settledNodes = new HashSet<Vertex>();
        unSettledNodes = new HashSet<Vertex>();
        distance = new HashMap<Vertex, Integer>();
        predecessors = new HashMap<Vertex, Vertex>();
        distance.put(source, 0);
        unSettledNodes.add(source);
        while (unSettledNodes.size() > 0) {
            Vertex node = getMinimum(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    private void findMinimalDistances(Vertex node) {
        Set<Vertex> adjacentNodes = getNeighbors(node);
        for (Vertex target : adjacentNodes) {
            if (getShortestDistance(target) > getShortestDistance(node)
                    + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node)
                        + getDistance(node, target));
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
        }

    }

    private int getDistance(Vertex node, Vertex target) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(node)
                    && edge.getDestination().equals(target)) {
                return edge.getWeight();
            } else if (edge.getDestination().equals(node) && edge.getSource().equals(target)) {
                return edge.getWeight();
            }
        }
        throw new RuntimeException("Should not happen");
    }

    private Set<Vertex> getNeighbors(Vertex node) {
        Set<Vertex> neighbors = new HashSet<>();
        Set<Edge> incidentEdges = graph.getIncidentEdges(node);
        for (Edge edge : incidentEdges) {


            if (edge.getSource().equals(node)) {
                if(!isSettled(edge.getDestination())) {
                    neighbors.add(edge.getDestination());
                }

            } else if (edge.getDestination().equals(node)) {
                if(!isSettled(edge.getSource())) {
                    neighbors.add(edge.getSource());
                }
            }

        }

        return neighbors;
    }

    private Vertex getMinimum(Set<Vertex> vertexes) {
        Vertex minimum = null;
        for (Vertex vertex : vertexes) {
            if (minimum == null) {
                minimum = vertex;
            } else {
                if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
                    minimum = vertex;
                }
            }
        }
        return minimum;
    }

    private boolean isSettled(Vertex vertex) {
        return settledNodes.contains(vertex);
    }

    private int getShortestDistance(Vertex destination) {
        Integer d = distance.get(destination);
        if (d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }


    /**
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     * @param target target vertex
     * @return shortest path from source to target
     */
    public LinkedList<Vertex> getPath(Vertex target) {
        LinkedList<Vertex> path = new LinkedList<Vertex>();
        Vertex step = target;
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }

}

