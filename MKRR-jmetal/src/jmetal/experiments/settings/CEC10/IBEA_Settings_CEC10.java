/**
 * IBEA_Settings.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * IBEA_Settings class of algorithm IBEA
 */
package jmetal.experiments.settings.CEC10;

import jmetal.metaheuristics.ibea.IBEASched;

import jmetal.metaheuristics.nsgaII.*;
import java.util.Properties;
import jmetal.base.Algorithm;
import jmetal.base.Operator;
import jmetal.base.Problem;
import jmetal.base.operator.comparator.FitnessComparator;
import jmetal.base.operator.crossover.CrossoverFactory;
import jmetal.base.operator.localSearch.LocalSearchFactory;
import jmetal.base.operator.mutation.MutationFactory;
import jmetal.base.operator.selection.BinaryTournament;
import jmetal.base.operator.selection.SelectionFactory;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.ibea.IBEA;
import jmetal.problems.ProblemFactory;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;

/**
 * @author Antonio J. Nebro
 */
public class IBEA_Settings_CEC10 extends Settings{
  
	 // Default settings
//  int populationSize_ = 100    ;
  int archiveSize_    = 100    ;
  int feedback_       = 20     ;
 
  //double mutationProbability_  = 1.0/problem_.getNumberOfVariables() ;
//  double crossoverProbability_ = 0.9 ;
  
  double  distributionIndexForMutation_ = 20    ;
  double  distributionIndexForCrossover_ = 20    ;

  	private String SELECTION             = "BinaryTournament"  ; ///< Selection operator
	private String CROSSOVER             = "UniformCrossover"  ; ///< Crossover operator
	private double crossoverProbability_ = 1.0                 ; ///< Crossover probability
	private String LOCAL_SEARCH          = "LMCTSLocalSearch"  ; ///< Local search operator
	private String MUTATION              = "RebalanceMutation" ; ///< Mutation operator
	private double mutationProbability_  = 0.2                 ; ///< Mutation probability
	private String M_POLICY              = "moderate"          ; ///< Mutation policy
	private String M_MODE                = "strict"            ; ///< Mutation policy mode
	private double M_OVERLOAD_PER        = 0.25                ; ///< Mutation: Percentage of machines overloaded and underloaded
	private int    M_ROUNDS              = 16                  ; ///< Mutation rounds
	private int    maxEvaluations_       = 500000              ; ///< Number of evaluations
	private int    populationSize_       = 100                 ; ///< Size of the population by default
	private String P_SPECIAL_SOLUTION    = "Min-min"           ; ///< Algorithm in order to setup the initial population

  
  String paretoFrontFile_ = "" ;
  
  /**
   * Constructor
   */
  public IBEA_Settings_CEC10(Problem problem) {
    super(problem) ;
  } // IBEA_Settings
  
  /**
   * Configure NSGAII with user-defined parameter settings
   * @return A NSGAII algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
	    Algorithm algorithm ;
	    Operator  selection   = null ; // Selection operator
		Operator  crossover   = null ; // Crossover operator
		Operator  mutation    = null ; // Mutation operator
		Operator  localsearch = null ; // LocalSearch operator
	    
	    QualityIndicator indicators ;
	    
	    // Creating the problem
	    algorithm = new IBEASched(problem_) ;

	    // Algorithm parameters
	    algorithm.setInputParameter("populationSize", populationSize_);
	    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
	    algorithm.setInputParameter("archiveSize",archiveSize_ );
	    algorithm.setInputParameter("feedBack",feedback_);
	    
		algorithm.setInputParameter( "specialSolution"  , P_SPECIAL_SOLUTION );

	    // Mutation and Crossover for Real codification 
		crossover = CrossoverFactory.getCrossoverOperator( CROSSOVER );
	    //crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover");
	    crossover.setParameter("probability", crossoverProbability_);                   
	    crossover.setParameter("distributionIndex",distributionIndexForCrossover_);

		mutation = MutationFactory.getMutationOperator( MUTATION );
	    //mutation = MutationFactory.getMutationOperator("PolynomialMutation"); 
	    mutation.setParameter("probability", mutationProbability_);
	    mutation.setParameter( "rounds"             , (Integer) M_ROUNDS );
		mutation.setParameter( "Problem"            , problem_           );
		mutation.setParameter( "overloadPercentage" , M_OVERLOAD_PER     );
		mutation.setParameter( "Policy"             , M_POLICY           );
		mutation.setParameter( "Mode"               , M_MODE             );

		selection = SelectionFactory.getSelectionOperator( SELECTION );

		localsearch = LocalSearchFactory.getLocalSearchOperator( LOCAL_SEARCH );
		localsearch.setParameter( "Problem" , problem_ );

	    
	    // Add the operators to the algorithm
		algorithm.addOperator( "localsearch" , localsearch );
		algorithm.addOperator("crossover",crossover);
	    algorithm.addOperator("mutation",mutation);
	    algorithm.addOperator("selection",selection);
	    
	//   // Creating the indicator object
	//   if (! paretoFrontFile_.equals("")) {
//	      indicators = new QualityIndicator(problem_, paretoFrontFile_);
//	      algorithm.setInputParameter("indicators", indicators) ;  
	//   } // if
	    return algorithm ;
	  }
  
  /**
   * Configure IBEA with user-defined parameter settings
   * @param settings
   * @return An IBEA algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure(Properties settings) throws JMException {
	  
	  if (settings != null) {
        populationSize_  = Integer.parseInt(settings.getProperty("POPULATION_SIZE", ""+populationSize_)) ;
        maxEvaluations_  = Integer.parseInt(settings.getProperty("MAX_EVALUATIONS", ""+maxEvaluations_)) ;
        archiveSize_     = Integer.parseInt(settings.getProperty("ARCHIVE_SIZE", ""+archiveSize_)) ;
        feedback_        = Integer.parseInt(settings.getProperty("FEEDBACK", ""+feedback_)) ;

        P_SPECIAL_SOLUTION = settings.getProperty("SPECIAL_SOLUTION", P_SPECIAL_SOLUTION);
        
    	  CROSSOVER = settings.getProperty("RECOMBINATION", CROSSOVER);
        
        crossoverProbability_ = Double.parseDouble(settings.getProperty("CROSSOVER_PROBABILITY", 
                                                      ""+crossoverProbability_)) ;
    	  
        MUTATION = settings.getProperty("MUTATION", MUTATION);
        M_ROUNDS = Integer.parseInt(settings.getProperty("MUTATION_ROUNDS", ""+M_ROUNDS));
        M_OVERLOAD_PER = Double.parseDouble(settings.getProperty("MUTATION_OVERLOAD_PERCENTAGE", ""+M_OVERLOAD_PER));
        M_POLICY = settings.getProperty("MUTATION_POLICY", M_POLICY);
        M_MODE = settings.getProperty("MUTATION_MODE", M_MODE);
        
        
        mutationProbability_ = Double.parseDouble(settings.getProperty("MUTATION_PROBABILITY", 
                                                      ""+mutationProbability_)) ;
        distributionIndexForMutation_ = 
              Double.parseDouble(settings.getProperty("DISTRIBUTION_INDEX_FOR_MUTATION", 
                                                      ""+distributionIndexForMutation_)) ;
        distributionIndexForCrossover_ = 
              Double.parseDouble(settings.getProperty("DISTRIBUTION_INDEX_FOR_CROSSOVER", 
                                                      ""+distributionIndexForCrossover_)) ;
        
        SELECTION = settings.getProperty("SELECTION", SELECTION);
        
        LOCAL_SEARCH = settings.getProperty("LOCAL_SEARCH", LOCAL_SEARCH);
        
        paretoFrontFile_ = settings.getProperty("PARETO_FRONT_FILE", "") ;
      }
    
    return configure() ;
  }
} // IBEA_Settings
