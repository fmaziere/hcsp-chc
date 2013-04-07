/**
 * MOCellStudy.java
 *
 * @author Bernabe Dorronsoro
 * @version 1.0
 */
package jmetal.experiments.scheduling;

import jmetal.experiments.*;

import java.util.logging.Logger;

import java.io.IOException;
import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import jmetal.base.Algorithm;
import jmetal.base.Problem;
import jmetal.experiments.settings.MOCell_Settings;
import jmetal.experiments.settings.MOCell_Settings_AG;
import jmetal.experiments.settings.MOCell_Settings_SRF;
//import jmetal.problems.MOBypassLinks;
import jmetal.util.JMException;
import jmetal.experiments.util.GeneratePareto;

import jmetal.experiments.settings.CEC10.*;

/**
 * @author Bernabe Dorronsoro
 * 
 *         This experiment class is configured to solve 100 instances of every
 *         scheduling problem class with NSGAII, MOCell, IBEA, and MOEA/D (100
 *         independent runs per algorithm/instance)
 */
public class MOCell_MKRR_Exp extends ExperimentNoPareto {

	// Number of independent runs per algorithm and problem
	private int independentRunsDf_ = 1;
	// Number of threads to use (= number of algorithms to run in parallel)
	private int numberOfThreadsDf_ = 1;
	// Number of instances to solve per problem
	private int numberOfInstancesDF_ = 1;

	/**
	 * Configures the algorithms in each independent run
	 * 
	 * @param problem
	 *            The problem to solve
	 * @param problemIndex
	 */
	public synchronized void algorithmSettings(Problem problem,
			int problemIndex, Algorithm[] algorithm) {
		try {
			int numberOfAlgorithms = algorithmNameList_.length;
			Properties[] parameters = new Properties[numberOfAlgorithms];

			for (int i = 0; i < numberOfAlgorithms; i++) {
				parameters[i] = new Properties();
				parameters[i].setProperty("POPULATION_SIZE", "225");
				parameters[i].setProperty("MAX_EVALUATIONS", "500000");

				parameters[i].setProperty("ARCHIVE_SIZE", "225");
				parameters[i].setProperty("FEEDBACK", "50");
				parameters[i].setProperty("SPECIAL_SOLUTION", "Min-min");

				parameters[i].setProperty("SELECTION", "BinaryTournament");
				// parameters[i].setProperty("SELECTION", "TournamentFour");

				// parameters[i].setProperty("RECOMBINATION", "DPX");
				parameters[i].setProperty("RECOMBINATION", "UniformCrossover");
				parameters[i].setProperty("CROSSOVER_PROBABILITY", "0.9");

				parameters[i].setProperty("MUTATION", "RebalanceMutation");
				// Estaba a 16!!!
				parameters[i].setProperty("MUTATION_ROUNDS", "25");
				parameters[i].setProperty("MUTATION_OVERLOAD_PERCENTAGE",
						"0.25");

				// parameters[i].setProperty("MUTATION_POLICY", "moderate");
				// parameters[i].setProperty("MUTATION_POLICY", "simple");
				parameters[i].setProperty("MUTATION_POLICY", "random");

				// parameters[i].setProperty("MUTATION_MODE", "strict");
				parameters[i].setProperty("MUTATION_MODE", "permissive");

				// parameters[i].setProperty("MUTATION_PROBABILITY", new
				// Double(1.0/problem.getNumberOfVariables()).toString());
				parameters[i].setProperty("MUTATION_PROBABILITY", "0.5");

				parameters[i].setProperty("LOCAL_SEARCH", "LMCTSLocalSearch");
			}

			if ((paretoFrontFile_ != null)
					&& !paretoFrontFile_[problemIndex].equals("")) {
				for (int i = 0; i < numberOfAlgorithms; i++)
					parameters[i].setProperty("PARETO_FRONT_FILE",
							paretoFrontFile_[problemIndex]);
			} // if

			algorithm[0] = new MOCell_Settings_CEC10(problem)
					.configure(parameters[0]);
			// algorithm[1] = new
			// NSGAII_Settings_CEC10(problem).configure(parameters[1]);
			// algorithm[2] = new
			// IBEA_Settings_CEC10(problem).configure(parameters[2]);
			// algorithm[3] = new
			// MOEAD_Settings_CEC10(problem).configure(parameters[3]);
		} catch (JMException ex) {
			Logger.getLogger(NSGAIIStudy.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	public static void main(String[] args) throws JMException, IOException {

		if (args.length != 0) {
			System.out.println("Error. Try: PaperCEC10Study");
			System.exit(-1);
		} // if

		// Integer[] params = new Integer[] {new Integer(args[0]).intValue(),
		// new Integer(args[1]).intValue(), new Integer(args[2]).intValue(), new
		// Integer(args[3]).intValue()};
		Object[] params = new String[] { "Int" };

		MOCell_MKRR_Exp exp = new MOCell_MKRR_Exp();

		exp.experimentName_ = "MO_Cell_MKRR";
		exp.timmingFileName_ = "TIMMINGS";

		exp.algorithmNameList_ = new String[] { "aMOCell4Sched" };
		// exp.algorithmNameList_ = new String[] {"aMOCell4Sched",
		// "NSGAIISched", "IBEASched", "MOEADSched"} ;

		exp.problemList_ = new String[] { "scheduling.u_i_hihi_MKRR" };
		exp.paretoFrontFile_ = new String[] { "MOCell_MKRR.u_i_hihi.pf" };
		exp.indicatorList_ = new String[] { "HV", "SPREAD", "IGD", "EPSILON" };

//		exp.experimentBaseDirectory_ = "./" + exp.experimentName_;
		exp.experimentBaseDirectory_ = "./Results/" + exp.experimentName_;
//		exp.paretoFrontDirectory_ = "./paretoFronts/scheduling";
		exp.paretoFrontDirectory_ = "./Results";

		exp.instances_ = exp.numberOfInstancesDF_;

		// create the Pareto front files
		for (int i = 0; i < exp.paretoFrontFile_.length; i++) {
			File file = null;
			if (exp.instances_ != 1) {
				for (int inst = 0; inst < exp.instances_; inst++) {
					file = new File(exp.paretoFrontDirectory_ + "/"
							+ exp.paretoFrontFile_[i] + "." + inst);
					try {
						file.createNewFile();
					} catch (IOException ioe) {
						System.out
								.println("Error while creating the empty file : "
										+ exp.paretoFrontDirectory_
										+ exp.paretoFrontFile_[i] + ioe);
					}
				}

			} else {
				file = new File(exp.paretoFrontDirectory_ + "/"
						+ exp.paretoFrontFile_[i]);
				try {
					file.createNewFile();
				} catch (IOException ioe) {
					System.out.println("Error while creating the empty file : "
							+ exp.paretoFrontDirectory_
							+ exp.paretoFrontFile_[i] + ioe);
				}
			}
		}

		int numberOfAlgorithms = exp.algorithmNameList_.length;
		exp.algorithmSettings_ = new Settings[numberOfAlgorithms];

		exp.independentRuns_ = exp.independentRunsDf_;

		// Run the experiments
		int numberOfThreads = exp.numberOfThreadsDf_;
		exp.runExperiment(numberOfThreads, params);

		// Since the true Pareto front is not known for this problem, we
		// generate it by merging all the obtained Pareto fronts in the
		// experimentation
		GeneratePareto paretoFront = new GeneratePareto(exp);
		paretoFront.run();
		paretoFront.computeQualityIndicators();

		// Generate latex tables (comment this sentence is not desired)
		exp.generateLatexTables();
	}
} // NSGAIIStudy

