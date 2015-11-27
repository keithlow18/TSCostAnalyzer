package org.processmining.plugins.tsanalyzer;

import org.processmining.models.graphbased.directed.transitionsystem.Transition;
import org.processmining.plugins.tsanalyzer.annotation.TransitionAnnotation;

public class TimeTransitionAnnotation extends TransitionAnnotation {
	private static final String DURATION = "duration";

	public TimeTransitionAnnotation(Transition transition) {
		super(transition);
		addProperty(DURATION, new StatisticsAnnotationProperty());
	}

	public StatisticsAnnotationProperty getDuration() {
		return (StatisticsAnnotationProperty) getProperty(DURATION);
	}
}
