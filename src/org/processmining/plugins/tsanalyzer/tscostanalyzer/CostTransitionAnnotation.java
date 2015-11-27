package org.processmining.plugins.tsanalyzer.tscostanalyzer;

import org.processmining.models.graphbased.directed.transitionsystem.Transition;
import org.processmining.plugins.tsanalyzer.StatisticsAnnotationProperty;
import org.processmining.plugins.tsanalyzer.annotation.TransitionAnnotation;

public class CostTransitionAnnotation extends TransitionAnnotation {
	private static final String COST = "cost";
	
	public CostTransitionAnnotation(Transition transition) {
		super(transition);
		addProperty(COST, new StatisticsAnnotationProperty());
	}
	
	public StatisticsAnnotationProperty getTotal(){
		return (StatisticsAnnotationProperty) getProperty(COST);
	}
}
