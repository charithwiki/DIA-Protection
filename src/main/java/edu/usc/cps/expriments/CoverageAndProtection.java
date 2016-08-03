package edu.usc.cps.expriments;

import edu.usc.cps.alg.GRBSteinerTree;
import edu.usc.cps.graph.Graph;
import edu.usc.cps.utils.Utils;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.DefaultGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by charith on 8/3/16.
 */
public class CoverageAndProtection {


    public static void main(String[] args) throws Exception{
        int N = 14;


        String inpath = "/home/charith/Smart-grid-security/DIA-Protection/data/IEEE-14-bus-";
        String outPath = "/home/charith/Smart-grid-security/DIA-Protection/results/coverage.csv";
        String outPath2 = "/home/charith/Smart-grid-security/DIA-Protection/results/cost.csv";

        PrintWriter writer = new PrintWriter(new FileWriter(outPath));
        PrintWriter writer2 = new PrintWriter(new FileWriter(outPath2));
        writer.println("#PMU,#S_atck,Type,Coverage, time(ms)");
        writer2.println("#PMU,#S_atck,Type,Cost, time(ms)");


        //PMUs

        int pmu[]  = {3,7,10};

        for(int i : pmu) {

            String in = inpath + i + ".txt";



            for(int j=1; j < N; j++) {


                Set<Integer> satck = Utils.getRandomSet(j,N);
                satck.add(N+1);

                //find O
                long startT = System.currentTimeMillis();
                GRBSteinerTree steinerTree = new GRBSteinerTree(in,satck,",");
                steinerTree.steinerTree();
                Set<String> treeEdges = steinerTree.getSteinerTreeEdges();
                long endT = System.currentTimeMillis();
                Set<Integer> cover = new HashSet<>();
                for(String e: treeEdges) {
                    String []parts = e.split(",");
                    int s = Integer.parseInt(parts[0]);
                    int t = Integer.parseInt(parts[1]);

                    cover.add(s);
                    cover.add(t);



                }
                writer.println("" + i + "," + (satck.size() -1) + ",O," + (cover.size() -1) + "," + (endT - startT));
                writer2.println("" + i + "," + (satck.size() -1) + ",O," + treeEdges.size() + "," + (endT - startT));


                //find D

                BufferedReader reader = new BufferedReader(new FileReader(in));

                String line = reader.readLine();
                DefaultGraph graph = new DefaultGraph("Grid");
                graph.setStrict(false);
                graph.setAutoCreate(true);
                while (line != null) {
                    String []parts = line.split(",");
                    Edge e = graph.addEdge("" + line, "" + Integer.parseInt(parts[0]), "" + Integer.parseInt(parts[1]));
                    e.addAttribute("weight", new Integer(1));
                    line = reader.readLine();
                }

                for(int v : satck) {
                    if(v != N + 1) {
                        Edge e = graph.addEdge("" + v + "," + (N + 2) , "" + v, "" + (N + 2));
                        e.addAttribute("weight", new Integer(1));
                    }

                }

                long startTime = System.currentTimeMillis();
                Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "weight");
                dijkstra.init(graph);
                dijkstra.setSource(graph.getNode("" + (N+2)));
                dijkstra.compute();

                int coverage = dijkstra.getPath(graph.getNode("" + (N + 1))).getNodeCount() -2;
                int cost = dijkstra.getPath(graph.getNode("" + (N+1))).getEdgeCount() -1;

                long endTime = System.currentTimeMillis();

                writer.println("" + i + "," + (satck.size() -1) + ",D," + coverage + "," + (endTime - startTime));
                writer2.println("" + i + "," + (satck.size() -1) + ",D," + cost + "," + (endTime - startTime));


                reader.close();
            }

            writer.flush();
            writer2.flush();

        }


        writer.flush();
        writer2.flush();
        writer.close();
        writer2.close();

    }

}
