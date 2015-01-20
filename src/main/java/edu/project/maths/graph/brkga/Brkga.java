package edu.project.maths.graph.brkga;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import javafx.util.Pair;

/**
 *
 * @author orak
 * @param <GraphDecoder>
 */
/*
 * BRKGA.h
 *
 * This class encapsulates a Biased Random-key Genetic Algorithm (for minimization problems) with K
 * independent Populations stored in two vectors of Population, current and previous. It supports
 * multi-threading via OpenMP, and implements the following key methods:
 *
 * - BRKGA() constructor: initializes the populations with parameters described below.
 * - evolve() operator: evolve each Population following the BRKGA methodology. This method
 *                      supports OpenMP to evolve up to K independent Populations in parallel.
 *                      Please note that double Decoder::decode(...) MUST be thread-safe.
 *
 * Required hyperparameters:
 * - n: number of genes in each chromosome
 * - p: number of elements in each population
 * - pe: pct of elite items into each population
 * - pm: pct of mutants introduced at each generation into the population
 * - rhoe: probability that an offspring inherits the allele of its elite parent
 *
 * Optional parameters:
 * - K: number of independent Populations
 * - MAX_THREADS: number of threads to perform parallel decoding -- WARNING: Decoder::decode() MUST
 *                be thread-safe!
 *
 *
 * Decoder: problem-specific decoder that implements any of the decode methods outlined below. When
 *          compiling and linking BRKGA with -fopenmp (i.e., with multithreading support via
 *          OpenMP), the method must be thread-safe.
 *     - double decode(const vector< double >& chromosome) const, if you don't want to change
 *       chromosomes inside the framework, or
 *     - double decode(vector< double >& chromosome) const, if you'd like to update a chromosome
 *
 */
public class Brkga {

    private int n;	// number of genes in the chromosome
    private int p;	// number of elements in the population
    private int pe;	// number of elite items in the population
    private int pm;	// number of mutants introduced at each generation into the population
    private double rhoe;	// probability that an offspring inherits the allele of its elite parent

    // Templates:
    //RNG& refRNG;				// reference to the random number generator
    private GraphDecoder decoder;	// reference to the problem-dependent Decoder

    // Parallel populations parameters:
    int K = 1;				// number of independent parallel populations
    int MAX_THREADS = 1;		// number of threads for parallel decoding

    // Data:
    ArrayList<Population> previous = new ArrayList<Population>();
    ArrayList<Population> current = new ArrayList<Population>();

    /*
     * Default constructor
     * Required hyperparameters:
     * - n: number of genes in each chromosome
     * - p: number of elements in each population
     * - pe: pct of elite items into each population
     * - pm: pct of mutants introduced at each generation into the population
     * - rhoe: probability that an offspring inherits the allele of its elite parent
     *
     * Optional parameters:
     * - K: number of independent Populations
     * - MAX_THREADS: number of threads to perform parallel decoding
     *                WARNING: Decoder::decode() MUST be thread-safe; safe if implemented as
     *                + double Decoder::decode(std::vector< double >& chromosome) const
     */

    public Brkga(int n, int p, double pe, double pm, double rhoe, GraphDecoder decoder, int K) {

        // Error check:
        //using std::range_error;
        if (n == 0) {
            throw new RuntimeException("Chromosome size equals zero.");
        }
        if (p == 0) {
            throw new RuntimeException("Population size equals zero.");
        }
        if (pe == 0) {
            throw new RuntimeException("Elite-set size equals zero.");
        }
        if (pe > p) {
            throw new RuntimeException("Elite-set size greater than population size (pe > p).");
        }
        if (pm > p) {
            throw new RuntimeException("Mutant-set size (pm) greater than population size (p).");
        }
        if (pe + pm > p) {
            throw new RuntimeException("elite + mutant sets greater than population size (p).");
        }
        if (K == 0) {
            throw new RuntimeException("Number of parallel populations cannot be zero.");
        }

        this.n = n;
        this.p = p;
        this.pe = (int) (pe * p);
        this.pm = (int) (pm * p);
        this.rhoe = rhoe;
        this.decoder = decoder;
        this.K = K;

        // Initialize and decode each chromosome of the current population, then copy to previous:
        for (int i = 0; i < K; ++i) {
            // Allocate:
            Population population = new Population(n, p);
//            current.set(i, population);
            current.add(population);

            // Initialize:
            initialize(population);

            // Then just copy to previous:
//            previous.set(i, new Population(current.get(i)));
            previous.add(population);
        }
        
        
    }

    /**
     * Resets all populations with brand new keys
     */
    void reset() {
        for (Population population: current)
            initialize(population);
    }

    private void initialize(Population population) {
//            Population population = current.get(i);
        Random randomValue = new Random((new Date()).getTime());

        for (int j = 0; j < p; ++j) {
            for (int k = 0; k < n; ++k) {
                population.setChromosomeAllele(j, k, randomValue.nextDouble());
                //(*current[i])(j, k) = refRNG.rand(); 
            }
        }

        for (int j = 0; j < p; ++j) {
            ArrayList<Double> chromosome = population.getithChromosome(j);
            double fitness = decoder.decode(chromosome);

            population.setFitness(j, fitness);

//		current[i]->setFitness(j, refDecoder.decode((*current[i])(j)) );
        }

        // Sort:
//        current.get(i).sortFitness();
        population.sortFitness();

    }

    /**
     * Returns the current population
     * @param k
     * @return 
     */
    public Population getPopulation(int k) {
        return (current.get(k));
    }

    /**
     * Returns the best fitness found so far among all populations
     * @return 
     */
    public double getBestFitness() {
        double best = current.get(0).getFitness(0); //double best = current[0]->fitness[0].first;

        for (int i = 1; i < K; ++i) {
            if (current.get(i).getFitness(0) < best) {
                best = current.get(i).getFitness(0);
            }
        }

        return best;
    }

    /**
     * Evolve the current populations following the guidelines of BRKGAs
     *
     * @param generations number of generations (must be even and nonzero)
     * @param J interval to exchange elite chromosomes (must be even; 0 ==> no
     * synchronization)
     * @param M number of elite chromosomes to select from each population in
     * order to exchange
     */
    public void evolve(int generations) {

        if (generations == 0) {
            throw new RuntimeException("Cannot evolve for 0 generations.");
        }

        for (int i = 0; i < generations; ++i) {
            for (int j = 0; j < K; ++j) {
                evolution(current.get(j), previous.get(j)); // First evolve the population (curr, next)

                Population pop = current.get(j);
                current.set(j, previous.get(i));
                previous.set(j, pop);
//                                    std::swap(current[j], previous[j]);		// Update (prev = curr; curr = prev == next)
            }
        }
    }

    /**
     * Exchange elite-solutions between the populations
     *
     * @param M number of elite chromosomes to select from each population
     */
    void exchangeElite(int M) {
        if (M == 0 || M >= p) {
            throw new RuntimeException("M cannot be zero or >= p.");
        }

        for (int i = 0; i < K; ++i) {
            // Population i will receive some elite members from each Population j below:
            int dest = p - 1;	// Last chromosome of i (will be updated below)
            for (int j = 0; j < K; ++j) {
                if (j == i) {
                    continue;
                }

                // Copy the M best of Population j into Population i:
                for (int m = 0; m < M; ++m) {
                    // Copy the m-th best of Population j into the 'dest'-th position of Population i:
//                        const std::vector < double > & bestOfJ = current[j]
//                        ->getChromosome(m);
                    ArrayList<Double> bestOfJ = current.get(j).getChromosome(m);

//                        std::copy(bestOfJ.begin(), bestOfJ.end(), current[i]->getChromosome(dest).begin());
                    ArrayList<Double> destChrom = current.get(i).getChromosome(dest);
                    for (int x = 0; x < bestOfJ.size(); x++) {
                        destChrom.set(x, bestOfJ.get(x));
                    }

//				current[i]->fitness[dest].first = current[j]->fitness[m].first;
                    current.get(i).setFitness(dest, current.get(j).getFitness(m));

                    --dest;
                }
            }
        }

        for (int j = 0; j < K; ++j) {
            current.get(j).sortFitness();
        }
    }

    /**
     * Returns the chromosome with best fitness so far among all populations
     * @return 
     */
    public ArrayList<Double> getBestChromosome() {

        int bestK = 0;

        for(int i = 1; i < K; ++i) {
		if( current.get(i).getBestFitness() < current.get(bestK).getBestFitness() ) 
                { bestK = i; }
	}


        return current.get(bestK).getChromosome(0);// The top one :-)
    }

    
    public void evolution(Population curr, Population next) {
	// We now will set every chromosome of 'current', iterating with 'i':
	int i = 0;	// Iterate chromosome by chromosome
	int j;          // Iterate allele by allele

	// 2. The 'pe' best chromosomes are maintained, so we just copy these into 'current':
	while(i < pe) {
		for(j = 0 ; j < n; ++j) {
                    // TODO: check this
                   next.setChromosomeAllele(i, j, curr.getChromosome(i).get(j)); //next(i,j) = curr(curr.fitness[i].second, j); }
                }

//                Pair<Double, Integer> p = new Pair<Double, Integer>(curr.getFitness(i), i);
                next.setFitness(i, curr.getFitness(i));
//		next.fitness[i].first = curr.fitness[i].first;
//		next.fitness[i].second = i;
		++i;
	}

        Random rand = new Random((new Date()).getTime());
        
	// 3. We'll mate 'p - pe - pm' pairs; initially, i = pe, so we need to iterate until i < p - pm:
	while(i < p - pm) {
		// Select an elite parent:
            if (pe < 1)
                System.out.println("OMG");
		int eliteParent = rand.nextInt(pe); //refRNG.randInt(pe - 1));

		// Select a non-elite parent:
		int noneliteParent = pe + rand.nextInt(p - pe); // (refRNG.randInt(p - pe - 1));

		// Mate:
		for(j = 0; j < n; ++j) {
//			int sourceParent = ((refRNG.rand() < rhoe) ? eliteParent : noneliteParent);
			int sourceParent = ((rand.nextDouble()< rhoe) ? eliteParent : noneliteParent);
                        next.setChromosomeAllele(i, j, curr.getChromosome(sourceParent).get(j));
//			next(i, j) = curr(curr.fitness[sourceParent].second, j);
		}

		++i;
	}

	// We'll introduce 'pm' mutants:
	while(i < p) {
		for(j = 0; j < n; ++j) {
//                    next(i, j) = refRNG.rand(); 
                    next.setChromosomeAllele(i, j, rand.nextDouble());
                }
		++i;
	}

	for(int x = pe; x < p; ++x) {
		next.setFitness(x, decoder.decode(next.population.get(x)) );
	}

	// Now we must sort 'current' by fitness, since things might have changed:
	next.sortFitness();
}

public int getN()  { return n; }
public int getP() { return p; }
public int getPe() { return pe; }
public int getPm() { return pm; }
public int getPo() { return p - pe - pm; }
public double getRhoe() { return rhoe; }
public int getK() { return K; }
public int getMAX_THREADS() { return MAX_THREADS; }

}



