package edu.usc.cps.graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by charith on 7/13/16.
 * Disclaimer: Modified version of source http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
 */
public class Graph {



    private Map<Vertex,HashSet<Edge>> adjList;

    private HashSet<Edge> edgeSet;

    private int numberOfEdges;

    private int numberOfVertices;

    public Graph(String fileName) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        adjList = new HashMap<Vertex, HashSet<Edge>>();
        edgeSet = new HashSet<>();

        String line = reader.readLine();
        //source, target, e-label, s-label, t-label

        int edge_count = 0;
        while (line != null) {
            String[] parts = line.split(",");

            Vertex source = new Vertex(parts[0],parts[3]);
            Vertex target = new Vertex(parts[1],parts[4]);

            Edge edge = new Edge(""  + edge_count, source, target, 1, parts[2]);

            if(adjList.containsKey(source)) {
                adjList.get(source).add(edge);
            } else {
                HashSet<Edge> edges = new HashSet<>();
                edges.add(edge);
                adjList.put(source,edges);
            }


            if(adjList.containsKey(target)) {
                adjList.get(target).add(edge);
            } else {
                HashSet<Edge> edges = new HashSet<>();
                edges.add(edge);
                adjList.put(target,edges);
            }

            edgeSet.add(edge);

            edge_count++;
            line = reader.readLine();
        }

        numberOfEdges = edge_count;
        numberOfVertices = adjList.keySet().size();
    }


    public Set<Edge> getIncidentEdges(Vertex vertex) {
        return adjList.get(vertex);
    }


    public Set<Vertex> getVertices() {
        return adjList.keySet();
    }

    public Set<Edge> getEdges() {
        return edgeSet;
    }


}
