package org.processmining.plugins.tsanalyzer.tscostanalyzer;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;

public class CostStatistics {
	
	protected String currencytype;
	protected HashMap<State, StateStatistics> states;
	protected HashMap<Transition, TransitionStatistics> transitions;
	
	public CostStatistics()	{
		super();
		states = new HashMap<State, StateStatistics>();
		transitions = new HashMap<Transition, TransitionStatistics>();
		currencytype = "";
	}
	
	public Iterable<Entry<State, StateStatistics>> getStates(){
		return states.entrySet();
	}
	
	public Iterable<Entry<Transition, TransitionStatistics>> getTransitions(){
		return transitions.entrySet();
	}

	public String getCurrencyType()
	{
		return currencytype;
	}
	
	public void SetCurrencyType(String currencytype)
	{
		this.currencytype = currencytype;
	}
	
	public StateStatistics getStatistics(State state){
		StateStatistics statistics = states.get(state);
		if (statistics == null){
			statistics = new StateStatistics();
			states.put(state, statistics);
		}
		return statistics;
	}
	
	public TransitionStatistics getStatistics(Transition transition){
		TransitionStatistics statistics = transitions.get(transition);
		if (statistics == null){
			statistics = new TransitionStatistics();
			transitions.put(transition, statistics);
		}
		return statistics;
	}
	
	class StateStatistics{
		
	    private DescriptiveStatistics remaining;
		private DescriptiveStatistics elapsed;
		
		public StateStatistics() {
			super();
			remaining = new  DescriptiveStatistics();
			elapsed = new  DescriptiveStatistics();
		}	

		
		public DescriptiveStatistics getRemaining(){
			return remaining;
		}
		
		public DescriptiveStatistics getElapsed(){
			return elapsed;
		}
	}
	
	class TransitionStatistics{
		private DescriptiveStatistics total;
		
		public TransitionStatistics() {
			super();
			total = new DescriptiveStatistics();
		}
		
		public DescriptiveStatistics getTotal(){
			return total;
		}
	}
}
