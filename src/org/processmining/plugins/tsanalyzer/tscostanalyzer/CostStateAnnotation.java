package org.processmining.plugins.tsanalyzer.tscostanalyzer;

import java.util.ArrayList;
import java.util.Collection;

import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.plugins.tsanalyzer.StatisticsAnnotationProperty;
import org.processmining.plugins.tsanalyzer.annotation.StateAnnotation;

public class CostStateAnnotation extends StateAnnotation{
	private static final String ELAPSED = "elapsed";
	private static final String REMAINING = "remaining";
	
	public CostStateAnnotation(State state) {
		super(state);
		addProperty(ELAPSED, new StatisticsAnnotationProperty());
		addProperty(REMAINING, new StatisticsAnnotationProperty());
	}
	
	private StatisticsAnnotationProperty getCostAnnotationProperty(String name){
		return (StatisticsAnnotationProperty) getProperty(name);
	}
	
	public StatisticsAnnotationProperty getElapsed(){
		return getCostAnnotationProperty(ELAPSED);
	}
	
	public StatisticsAnnotationProperty getRemaining(){
		return getCostAnnotationProperty(REMAINING);
	}	
	
	public static Iterable<String> getNamesOfProperties(){
		Collection<String> temp = new ArrayList<String>();
		temp.add(ELAPSED);
		temp.add(REMAINING);
		return temp;
	}
}
