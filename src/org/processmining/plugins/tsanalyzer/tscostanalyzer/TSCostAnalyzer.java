package org.processmining.plugins.tsanalyzer.tscostanalyzer;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.deckfour.xes.extension.std.XExtendedEvent;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;
import org.processmining.models.graphbased.directed.transitionsystem.payload.event.EventPayloadTransitionSystem;
import org.processmining.plugins.transitionsystem.miner.util.TSEventCache;
import org.processmining.plugins.tsanalyzer.StatisticsAnnotationProperty;


public class TSCostAnalyzer {

	/**
	 * The context of this miner.
	 */
	private PluginContext context;

	/**
	 * the transition system to annotate
	 */
	private EventPayloadTransitionSystem transitionSystem;

	/**
	 * eventCache is used for efficient access to events in XTrace
	 */
	private TSEventCache eventCache;

	/**
	 * statistics for storing necessary cost values
	 */
	private CostStatistics statistics;

	
	/**
	 * the log to be used to annotate the transition system
	 */
	private XLog log;

	/**
	 * transition system annotation
	 */
	private CostTransitionSystemAnnotation annotation;
	
	/**
	 * 
	 * the currency used for cost
	 */
	private String CostCurrency; 
	/**
	 * Key for the cost amount attribute.
	 */
	public static final String KEY_COSTELEMENT = "cost:element";

	/**
	 * Key for the cost currency attribute.
	 */
	public static final String KEY_COSTCURRENCY = "cost:currency";

	/**
	 * Key for the cost amount attribute.
	 */
	public static final String KEY_COSTAMOUNT = "cost:amount";

	/**
	 * Partial Key for the cost type attribute.
	 */
	public static final String KEY_COSTTYPE = "cost:type";

	public TSCostAnalyzer(final PluginContext context, final EventPayloadTransitionSystem ts, final XLog log) {
		super();
		this.context = context;
		this.transitionSystem = ts;
		this.log = log;
		eventCache = new TSEventCache();
		statistics = new CostStatistics();
		annotation = new CostTransitionSystemAnnotation();
	}

	/**
	 * This method creates the annotation for
	 * 
	 * @param log
	 * @return
	 */
	public CostTransitionSystemAnnotation annotate() {

		context.getProgress().setMinimum(0);
		context.getProgress().setMaximum(log.size());
		/**
		 * For every trace a tick on the progress bar, and an extra tick for the
		 * modification phase.
		 */
		context.getProgress().setCaption("Annotating transition system with cost");
		context.getProgress().setIndeterminate(false);
		
		/***
		 * Get the currency type given in the log.
		 * 
		 */
/*		XAttribute currencyAttribute = log.getAttributes().get(KEY_COSTCURRENCY);
		if (currencyAttribute != null)
			
		{
			CostCurrency = ((XAttributeLiteral) currencyAttribute).getValue();
			statistics.currencytype = CostCurrency;
		}*/
	    	    
		for (XTrace pi : log) {
			/**
			 * get the total cost of this instance (trace)
			 */
					
			Double instanceCost = getProcessInstanceCost(pi);
			
			if (instanceCost != -1) {
				/**
				 * process one instance (trace)
				 */
				for (int i = 0; i < pi.size(); i++) {
					double currentCost = 0;
					currentCost = processEvent(pi, i, instanceCost, currentCost);
				}
				/**
				 * increase the progress bar
				 */
				context.getProgress().inc();
			}
		}
		/**
		 * create annotations from collected statistics
		 */
		createAnnotationsFromStatistics();
		return annotation;
	}

	/**
	 * This method processes one event from a trace by collecting all necessary
	 * cost data.
	 * 
	 * @param pi
	 *            the trace to which the event belongs
	 * @param i
	 *            the index of the event to be processed
	 * @param instanceCost
	 *            the overall cost of this trace
	 * @param currentCost
	 *            the current cost
	 * @return the current cost so far of the trace  
	 * 			         
	 */
	private double processEvent(final XTrace pi, final int i, final double instanceCost, double currentCost) {
		/**
		 * Get the cost of the event we are processing
		 */
		Double activityCost = getEventCost(pi,i);
		if (activityCost != null)
		{
			currentCost = currentCost + activityCost;
			/**
			 * Get the transition that corresponds to this event. Also get the
			 * source and the target for this transition.
			 */
			Transition transition = transitionSystem.getTransition(pi, i);
			if (transition != null) {
				State source = transition.getSource();
				State target = transition.getTarget();

				/**
				 * create statistics for the transition and target
				 */
				CostStatistics.TransitionStatistics transitionStatistics = statistics.getStatistics(transition);
				CostStatistics.StateStatistics targetStatistics = statistics.getStatistics(target);

				/**
				 * annotate the source only for the first event in the trace
				 */
				if (i == 0) {
					CostStatistics.StateStatistics sourceStatistics = statistics.getStatistics(source);
					sourceStatistics.getRemaining().addValue(instanceCost);
					sourceStatistics.getElapsed().addValue(0.0);
				}
				
                 /**
				 * annotate target with elapsed and remaining time
				 */
				targetStatistics.getElapsed().addValue(currentCost);
				targetStatistics.getRemaining().addValue(instanceCost - currentCost);
		
				/**
				 * annotate the transition with total cost
				 */
				if (i == 0)
				{
					transitionStatistics.getTotal().addValue(0.0);
				}
				else
				{
					transitionStatistics.getTotal().addValue(activityCost);
				}
			}
		}
		return currentCost;
}
	
	private void printValues(String name, DescriptiveStatistics st){

//		for (double d: st.getValues()){
//			Duration dur = new Duration((long)d);
//		}
		System.out.println();
	}

	/**
	 * Goes through all gathered statistics for each state and transition and
	 * creates annotations from the statistics.
	 * 
	 * @return the annotation of the transition system
	 */
	private void createAnnotationsFromStatistics() {
		
		statistics.getCurrencyType();
		/**
		 * create annotation for each state
		 */
		for (Entry<State, CostStatistics.StateStatistics> entry : statistics.getStates()) {
			CostStateAnnotation stateAnnotation = getStateAnnotation(entry.getKey());
			annotateState(stateAnnotation, entry.getValue());
		}
		/**
		 * create annotation for each transition
		 */
		for (Entry<Transition, CostStatistics.TransitionStatistics> entry : statistics.getTransitions()) {
			CostTransitionAnnotation transitionAnnotation = getTransitionAnnotation(entry.getKey());
			annotateTransition(transitionAnnotation, entry.getValue());
		}
	}

	/**
	 * Create state annotation from its statistics (i.e., elapsed, and remaining cost).
	 * 
	 * @param state
	 *            to be annotated
	 * @param statistics
	 *            collected for this state
	 */
	private void annotateState(CostStateAnnotation stateAnnotation, CostStatistics.StateStatistics statistics) {
		annotateStatisticsProperty(stateAnnotation.getRemaining(), statistics.getRemaining());
		annotateStatisticsProperty(stateAnnotation.getElapsed(), statistics.getElapsed());
	}

	/**
	 * Create transition annotation from its statistics.
	 * 
	 * @param transitionAnnotation
	 * @param statistics
	 */
	private void annotateTransition(CostTransitionAnnotation transitionAnnotation,
			CostStatistics.TransitionStatistics statistics) {
		annotateStatisticsProperty(transitionAnnotation.getTotal(), statistics.getTotal());
	}

	/**
	 * Creates annotation form one statistics property (i.e, average, standard
	 * deviation, variance, etc.)
	 * 
	 * @param prop
	 *            annotation to be created
	 * @param stat
	 *            statistics with time values
	 */
	private void annotateStatisticsProperty(StatisticsAnnotationProperty prop, DescriptiveStatistics stat) {
		prop.setValue(new Double(stat.getMean()));
		prop.setAverage(stat.getMean());
		prop.setStandardDeviation(stat.getStandardDeviation());
		prop.setMin(stat.getMin());
		prop.setMax(stat.getMax());
		prop.setSum(stat.getSum());
		prop.setVariance(stat.getVariance());
		prop.setFrequencey(stat.getN());
		prop.setMedian(stat.getPercentile(50));
	}

	/**
	 * Returns the annotation object for the given state. If the annotation
	 * object for the state does not exist, a new annotation object is created
	 * for the state and returned.
	 * 
	 * @param state
	 *            for which to find the annotation
	 * @return time annotation for the state
	 */
	private CostStateAnnotation getStateAnnotation(State state) {
		CostStateAnnotation stateAnnotation = annotation.getStateAnnotation(state);
		if (stateAnnotation == null) {
			stateAnnotation = new CostStateAnnotation(state);
			annotation.addStateAnnotation(stateAnnotation);
		}
		return stateAnnotation;
	}

	/**
	 * Returns the annotation object for the given transition. If the annotation
	 * object for the transition does not exist, a new annotation object is
	 * created for the transition and returned.
	 * 
	 * @param transition
	 *            for which to find the annotation object
	 * @return cost annotation for the transition object
	 */
	private CostTransitionAnnotation getTransitionAnnotation(Transition transition) {
		CostTransitionAnnotation transitionAnnotation = annotation.getTransitionAnnotation(transition);
		if (transitionAnnotation == null) {
			transitionAnnotation = new CostTransitionAnnotation(transition);
			annotation.addTransitionAnnotation(transitionAnnotation);
		}
		return transitionAnnotation;
	}
	
	/**
	 * Gets the total cost of a trace.
	 * 
	 * @param pi
	 *            the trace
	 * @return the cost of the trace
	 */
	private double getProcessInstanceCost(XTrace pi) {
		try {

			XAttribute costAttribute = pi.getAttributes().get(KEY_COSTELEMENT);
			if (costAttribute != null)
			{	Double cost = calculateTotalCostFromAttribute(costAttribute);
				return new Double(cost);

			}
		}
		catch (Exception ce) {
			System.out.println(ce.getMessage());
		}
		return -1;
	}

	/**
	 * Gets the total cost of an event.
	 * 
	 * @param pi
	 *            the trace
	 *  @param int 
	 *		    the index of the event
	 * @return the cost of the event
	 */

	private Double getEventCost(XTrace pi, int index) {
		try {

			XEvent event = pi.get(index);
			XAttribute costAttribute = event.getAttributes().get(KEY_COSTELEMENT);
			if (costAttribute != null)
			{
				double cost = calculateTotalCostFromAttribute(costAttribute);
				return new Double(cost);
			}
		}
		catch (Exception ce) {
			System.out.println(ce.toString());
		}
		return null;
	}

	/**
	 * Gets the total cost from a cost attribute.
	 * 
	 * @param costAttribute
	 *            
	 * @return the total cost
	 */

	private double calculateTotalCostFromAttribute(XAttribute costAttribute)
	{
		double totalCost = 0.0;
		HashMap <String,Double> costMap = getCostData(costAttribute);
		if (costMap != null)
		{ 	for(Double d: costMap.values())
			{
				totalCost += totalCost + d.doubleValue();
			}
		}
		return totalCost;

	}

	/**
	 * Gets the cost data from an attribute.
	 * 
	 * @param att
	 *            the attribute
	 * @return all the cost data (cost type, cost amount) of an attribute in the form of a hashmap or null
	 */
	
	private HashMap<String,Double> getCostData(XAttribute att)
	{ 
		HashMap<String, Double> costDataMap = new HashMap<String,Double>();
		if (att != null)
		{  
			XAttributeMap costAttributeMap = att.getAttributes();
			for (XAttribute x: costAttributeMap.values())
			{	if (x.getKey().startsWith(KEY_COSTTYPE))
				{
					String costType = ((XAttributeLiteral) x).getValue();
					XAttribute costAmtAttribute = (XAttribute) x.getAttributes().get(KEY_COSTAMOUNT);
					String costString = ((XAttributeLiteral) costAmtAttribute).getValue();
					Double costAmt = Double.parseDouble(costString);
				//	System.out.println("mappings" + costType + " --- " + costString);
					costDataMap.put(costType,costAmt);
				}
			}
			return costDataMap;
		}
		//System.out.println("getCostData returns null");
		return null;
	}
}

