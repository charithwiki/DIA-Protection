package edu.usc.cps.alg;

import com.google.common.collect.Sets;
import gurobi.*;
import org.graphstream.graph.Edge;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by Charith Wickramaarachchi
 * min cT x
 * (i) x(δ(W)) ≥ 1, for all W ⊂ V,W ∩ T = ∅,
 * (V \ W) ∩ T = ∅,
 * (ii) 0 ≤ xe ≤ 1, for all e ∈ E,
 * (iii) x integer,
 * Assume an undirected graph
 */
public class GRBSteinerTree {


    private Map<Integer, Set<Integer>> adjList;

    private Set<Integer> terminals;

    private Set<Integer> vertices;

    private Map<String, Integer> edgeIndex;

    private Map<Integer, String> indexEdge;

    private Set<String> steinerTreeEdges;


    double[] x_t_1;

    private int num_edges = 0;


    /**
     *
     * @param edgeList edge list file
     * @param terminals set of terminal node ids
     * @param split seperator used in edge list file
     * @throws Exception
     */
    public GRBSteinerTree(String edgeList, Set<Integer> terminals, String split) throws Exception{
        adjList = new HashMap<Integer, Set<Integer>>();
        this.terminals = terminals;
        vertices = new HashSet<Integer>();
        edgeIndex = new HashMap<String, Integer>();
        indexEdge = new HashMap<Integer, String>();
        steinerTreeEdges = new HashSet<String>();

        BufferedReader graphReader = new BufferedReader(new FileReader(edgeList));
        String line = graphReader.readLine();

        int idx = 0;
        while (line != null) {

            String[] parts = line.split(split);

            int source = Integer.parseInt(parts[0]);
            int target = Integer.parseInt(parts[1]);

            vertices.add(source);
            vertices.add(target);

            if (adjList.containsKey(source)) {
                adjList.get(source).add(target);
            } else {
                HashSet<Integer> targets = new HashSet<Integer>();
                targets.add(target);
                adjList.put(source, targets);
            }

            if (adjList.containsKey(target)) {
                adjList.get(target).add(source);
            } else {
                HashSet<Integer> targets = new HashSet<Integer>();
                targets.add(source);
                adjList.put(target, targets);
            }



            String edge = source > target ? "" + target + "," + source : "" + source + "," + target;

            if (!edgeIndex.containsKey(edge)) {
               // System.out.println("Edge: " + edge + ": idx: " + idx);
                indexEdge.put(idx,edge);
                edgeIndex.put(edge, idx++);
            }


            line = graphReader.readLine();
            num_edges++;
        }


        graphReader.close();

      //  System.out.println("init done");
        x_t_1 = new double[num_edges];
        Arrays.fill(x_t_1,0.0);


    }

    /**
     *
     * @param edgeList edge list file
     * @param terminalFile set of terminal node ids
     * @param split seperator used in edge list file
     * @throws Exception
     */
    public GRBSteinerTree(String edgeList, String terminalFile, String split) throws Exception {


        this(edgeList, new HashSet<Integer>(), split);


        BufferedReader terminalReader = new BufferedReader(new FileReader(terminalFile));
        String line = terminalReader.readLine();


        while (line != null) {
            int t = Integer.parseInt(line);
            terminals.add(t);
            line = terminalReader.readLine();
        }


    }

    /**
     *  Calculate the Steiner Min Tree (SMT)
     * @return cost of SMT
     * @throws Exception
     */
    public double steinerTree() throws Exception{

        GRBEnv env = new GRBEnv();
        GRBModel model = new GRBModel(env);
        model.set(GRB.StringAttr.ModelName, "SteinerTree");




        GRBVar[] x = new GRBVar[num_edges];
        for (int p = 0; p < num_edges; ++p) {
            x[p] = model.addVar(0, 1, 0, GRB.BINARY, "x" + p);
        }



        model.update();

        /**
         * Set objective
         */
        GRBLinExpr expr = new GRBLinExpr();
        for (int p = 0; p < num_edges; ++p) {
            expr.addTerm(1, x[p]);
        }

        model.setObjective(expr, GRB.MINIMIZE);

        addConstrains(model, x);
      //  System.out.println("starting");
        model.optimize();
        double cost = model.get(GRB.DoubleAttr.ObjVal);
//        System.out.println("\nTOTAL COSTS: " + cost);
//        System.out.println("SOLUTION:");

        for(int p = 0; p < num_edges; ++p) {
            if (x[p].get(GRB.DoubleAttr.X) == 1.0) {
                //System.out.println("x" + p + ": " + indexEdge.get(p));
                x_t_1[p] = 1;
                steinerTreeEdges.add(indexEdge.get(p));
            }
        }
        env.dispose();
        return cost;
    }



    private void addConstrains(GRBModel model,GRBVar[] x) throws Exception{
        int idx=0;
        for(Set<Integer> S: powerSet(vertices)) {
            if(!S.isEmpty() && isValidSubset(S)) {

                GRBLinExpr numCuts = new GRBLinExpr();
                int cuts[] = getCutVector(S);
                for(int i=0; i < num_edges; i++) {
                    numCuts.addTerm(cuts[i],x[i]);
                }

                model.addConstr(numCuts,GRB.GREATER_EQUAL,1, "c" + idx++);

            }
        }
    }


    private boolean isValidSubset(Set<Integer> vertexSet) {

        Sets.SetView<Integer> setView = Sets.intersection(vertexSet, terminals);
        return setView.size() > 0 && setView.size() < terminals.size();
    }


    private static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
        Set<Set<T>> sets = new HashSet<Set<T>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<T>());
            return sets;
        }
        List<T> list = new ArrayList<T>(originalSet);
        T head = list.get(0);
        Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
        for (Set<T> set : powerSet(rest)) {
            Set<T> newSet = new HashSet<T>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

    private int[] getCutVector(Set<Integer> S) {
        int []cuts = new int[num_edges];
        Arrays.fill(cuts,0);

        for(int vertex: S) {

            Set<Integer> neighbours = adjList.get(vertex);
            Sets.SetView<Integer> setView = Sets.difference(neighbours,S);

            for(int cutV: setView) {
                String edge = vertex > cutV ? "" + cutV + "," + vertex : "" + vertex + "," + cutV;
                int idx = edgeIndex.get(edge);
                cuts[idx] = 1;
            }

        }
        return cuts;
    }


    public Set<String> getSteinerTreeEdges() {
        return steinerTreeEdges;
    }




}
