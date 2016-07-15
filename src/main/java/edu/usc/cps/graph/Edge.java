package edu.usc.cps.graph;

/**
 * Disclaimer: Modified version of source http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
 */

public class Edge  {
    private final String id;
    private Vertex source;
    private Vertex destination;
    private int weight;
    private String label;

    public Edge(String id, Vertex source, Vertex destination, int weight) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public Edge(String id, Vertex source, Vertex destination, int weight, String label) {
        this(id,source,destination,weight);
        this.label = label;
    }

    public String getId() {
        return id;
    }
    public Vertex getDestination() {
        return destination;
    }

    public Vertex getSource() {
        return source;
    }
    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return source + " " + destination;
    }



}