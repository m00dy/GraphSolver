/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.project.maths.graph.brkga;

import edu.project.maths.graph.graphsolver.NetworkGraph;
import edu.project.maths.graph.graphsolver.NetworkLink;
import edu.project.maths.graph.graphsolver.Solution;
import edu.project.maths.graph.graphsolver.Transfer;
import java.util.ArrayList;

/**
 *
 * @author orak
 */
public class BrkgaSolver {

    private static int n;		// size of chromosomes = no of transfers
    private static final int p = 30;	// size of population
    private static final double pe = 0.20;		// fraction of population to be the elite-set
    private static final double pm = 0.10;		// fraction of population to be replaced by mutants
    private static final double rhoe = 0.70;	// probability that offspring inherit an allele from elite parent
    private static final int k = 3;		// number of independent populations
//	private static int MAXT = 2;	// number of threads for parallel decoding

    public static Solution solve(NetworkGraph<String, NetworkLink> graph, ArrayList<Transfer> transferList,
            Transfer demandTransfer) {
        n = transferList.size();
        Solution solution = new Solution();
        GraphDecoder decoder = new GraphDecoder(graph, transferList, demandTransfer);		// initialize the decoder

        // initialize the BRKGA-based heuristic
        Brkga algorithm = new Brkga(n, p, pe, pm, rhoe, decoder, k);

        int generation = 0;		// current generation
        final int X_INTVL = 2;	// exchange best individuals at every X_INTVL generations
        final int X_NUMBER = 2;	// exchange top 2 best
        final int MAX_GENS = 5;	// run for MAX_GENS gens

        do {
            algorithm.evolve(1);	// evolve the population for one generation

            if ((++generation) % X_INTVL == 0) {
                algorithm.exchangeElite(X_NUMBER);	// exchange top individuals
            }
        } while (generation < MAX_GENS);

        System.out.println("Best solution found has objective value = " + algorithm.getBestFitness());
        decoder.printResult = true;
        decoder.decode(algorithm.getBestChromosome());

        return solution;
    }

}
