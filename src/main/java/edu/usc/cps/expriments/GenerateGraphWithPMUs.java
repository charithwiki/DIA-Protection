package edu.usc.cps.expriments;

import java.io.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.List;

/**
 * Created by charith on 7/13/16.
 */
public class GenerateGraphWithPMUs {


    public static void main(String[] args) throws Exception {


        int N = 14;

        String inPath = "/home/charith/Smart-grid-security/DIA-Protection/IEEE-14-bus.txt";
        String outPath = "/home/charith/Smart-grid-security/DIA-Protection//data/IEEE-14-bus-";



        for(int i = 0; i <= N; i++) {




            BufferedReader reader = new BufferedReader(new FileReader(inPath));
            String out = outPath + i + ".txt";
            PrintWriter writer = new PrintWriter(new FileWriter(out));
            String line = reader.readLine();




            while (line != null) {
                writer.println(line);
                line = reader.readLine();
            }


            if(i !=0 ) {

                Set<Integer> set = getRandomPMUList(i,N);

                for(int s : set) {
                    writer.println("" + s + "," + (N+1));
                }

            }

            reader.close();
            writer.flush();
            writer.close();
        }
    }


    private static Set<Integer> getRandomPMUList(int count, int N) {
        Set<Integer> set = new HashSet<>();

        Random random = new Random();
        while (set.size() != count) {

            int v = random.nextInt(N)  + 1;
            set.add(v);

        }

        return set;
    }
}



