/*
 * Population.h
 *
 * Encapsulates a population of chromosomes represented by a vector of doubles. We don't decode
 * nor deal with random numbers here; instead, we provide private support methods to set the
 * fitness of a specific chromosome as well as access methods to each allele. Note that the BRKGA
 * class must have access to such methods and thus is a friend.
 */
package edu.project.maths.graph.brkga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javafx.util.Pair;

/**
 *
 * @author orak
 */
public class Population {

    ArrayList<Pair<Double, Integer>> fitness;
    ArrayList<ArrayList<Double>> population;

    // These methods REQUIRE fitness to be sorted, and thus a call to sortFitness() beforehand
    // (this is done by BRKGA, so rest assured: everything will work just fine with BRKGA).
    
    // Returns the best fitness in this population
    public double getBestFitness() {
        return getFitness(0);
    }

    // Returns the fitness of chromosome i
    public double getFitness(int i) {
        Pair<Double, Integer> item = fitness.get(i);
        return item.getKey();
    }

    // Returns i-th best chromosome
    ArrayList<Double> getChromosome(int i) {
        int chromosomeKey = fitness.get(i).getValue();
        return population.get(chromosomeKey);
    }

    public Population(Population other) {
        population = other.population;
        fitness = other.fitness;
    }

    public Population(int n, int p) {
        if (p == 0) {
            throw new RuntimeException("Population size p cannot be zero.");
        }
        if (n == 0) {
            throw new RuntimeException("Chromosome size n cannot be zero.");
        }

//		population(p, std::vector< double >(n, 0.0)), fitness(p) 
        population = new ArrayList<ArrayList<Double>>();

        for (int i = 0; i < p; i++) {

            ArrayList<Double> chromosomes = new ArrayList<Double>();

            for (int j = 0; j < n; j++) {
                chromosomes.add(0.0);
            }

            population.add(chromosomes);
        }
        
        fitness = new ArrayList<Pair<Double, Integer>>();
        
        for (int j=0; j<p; j++)
        {
            Pair pair = new Pair(0.0, j);
            fitness.add(pair);
        }

    }

    // Sorts 'fitness' by its first parameter
    public void sortFitness() {
        Collections.sort(fitness, new Comparator<Pair<Double, Integer>>() {
            @Override
            public int compare(Pair<Double, Integer> x, Pair<Double, Integer> y) {
                double res = y.getKey() - x.getKey();
//                return (int) res;
                int resInt;

                if (res > 0.0) {
                    resInt = 1;
                } else if (res == 0.0) {
                    resInt = 0;
                } else {
                    resInt = -1;
                }

                return resInt;

            }
        });
    }

    // Sets the fitness of chromosome i
    public void setFitness(int i, double f) {
        Pair<Double, Integer> p = new Pair<Double, Integer>(f, i);
        fitness.set(i, p);

    }

    public int getN() {
        return population.get(0).size();
    }

    public int getP() {
        return population.size();
    }

    void setChromosomeAllele(int j, int k, Double randomValue) {
//        	return population[chromosome][allele];
        population.get(j).set(k, randomValue);
    }
    
    ArrayList<Double> getithChromosome(int i){
        return population.get(i);
    }
}
