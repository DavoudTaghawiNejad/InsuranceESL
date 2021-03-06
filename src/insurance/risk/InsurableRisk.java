/**
 * Created by Torsten Heinrich
 */
package insurance.risk;

//ESL
import org.economicsl.inventory.Item;

//JDistLib from: http://jdistlib.sourceforge.net/javadoc/
import jdistlib.generic.GenericDistribution;
import jdistlib.Exponential;
//import jdistlib.Uniform;
//import jdistlib.Normal;
//import jdistlib.LogNormal;
//import jdistlib.Poisson;
import jdistlib.evd.GeneralizedPareto;

import jdistlib.rng.MersenneTwister;
import jdistlib.rng.RandomEngine;


//TODO: this class should extend org.economicsl.inventory.Good or org.economicsl.inventory.Item
//TODO: random number generator draws the same numbers for different agents/risks (apparently for all calls within a certain time window). How to solve this?
//TODO: ... so either give every org.economicsl.agent their own seed or lock the resource
public class InsurableRisk extends Item{
	
	private double value;
	private double runtime;
	private GenericDistribution eventDist;
	private GenericDistribution eventSizeDist;
	private RandomEngine randomE;
	
	//TODO: overload the constructor in a more generic way?
	public InsurableRisk(String name, GenericDistribution valueDist, double runtime, GenericDistribution eventDist, GenericDistribution eventSizeDist, long seed) {
		
		//TODO: A RandomEngine instance with new random seed must supplied to avoid duplicates. Unfortunately, the class level RE does not yet exist at this point.
		this("", valueDist.random(new MersenneTwister(System.nanoTime())), runtime, eventDist, eventSizeDist, seed);
		//this("", valueDist.random(), runtime, eventDist, eventSizeDist);
	}

	public InsurableRisk(String name, double value, double runtime, GenericDistribution eventDist, GenericDistribution eventSizeDist, long seed) {
		super(name);
		this.value = value;
		this.runtime = runtime;
		this.eventDist = eventDist;
		this.eventSizeDist = eventSizeDist;
		this.randomE = new MersenneTwister(seed);
	}
	
	public InsurableRisk(double runtime, long seed) {
		/**
		 * setting default distributions: 
		 *    Power Law with x_min = 10.
		 *                   alpha = 3.
		 *               => PDF(x) = 200 * x^(-3)
		 *    Exponential with lambda = .03
		 *               => PDF(x) = 0.03 * e^(-0.03*x) 
		 *    Power Law with x_min = 10.
		 *                   alpha = 3.
		 *               => PDF(x) = 200 * x^(-3)
		 */
		this("", new GeneralizedPareto(10., 10.*3., 1./3.), runtime, new Exponential(33.33), new GeneralizedPareto(10., 10.*3., 1./3.), seed);
	}

	public InsurableRisk(long seed) {
		/**
		 *  default to:
		 *   runtime = 100 periods
		 */
		this(100, seed);
	}
	
	public double getTimeToNextEvent() {
		return(this.eventDist.random(this.randomE));
	}
	
	public double getSizeOfEvent () {
		return(this.eventSizeDist.random(this.randomE));
	}
	
	public double getRuntime() {
		return this.runtime;
	}
	
	public double getValue() {
		return this.value;
	}

	/* //TODO: should we include this?
	public void addInsurance(InsuranceContract ic) {
		this.insurance = ic;
	}
	*/
	
}	
