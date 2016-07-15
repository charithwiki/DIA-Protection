package edu.usc.cps.utils;

import scala.Int;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by charith on 7/13/16.
 */
public class Undirector {


    /**
     * Remove Duplicate edges and make the graph undirected
     * @param args arg[0] input csv edge list file, arg[1] output csv file
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(args[0]));
        PrintWriter writer = new PrintWriter(new FileWriter(args[1]));
        String line = reader.readLine();


        HashSet<String> edges = new HashSet<String>();
        HashSet<Integer> vertices = new HashSet<>();
        HashMap<Integer,Integer> vertexMap = new HashMap<>();

        int vid =1;

        while (line != null) {

            System.out.println(line);
            String[] parts = line.split(",");

            int source = (int) Double.parseDouble(parts[0]);
            int target = (int) Double.parseDouble(parts[1]);

            int s =0;
            int t =0;

            if(vertexMap.containsKey(source)) {
                s = vertexMap.get(source);
            } else {
                s = vid++;
                vertexMap.put(source,s);
            }


            if(vertexMap.containsKey(target)) {
                t = vertexMap.get(target);
            } else {
                t = vid++;
                vertexMap.put(target,t);
            }


            String edge = s < t? ""  + s + "," + t: "" + t + "," + s;

            if(!edges.contains(edge)) {
                writer.println(edge);
                edges.add(edge);
            }

            line = reader.readLine();
        }
        writer.flush();
    }
}
