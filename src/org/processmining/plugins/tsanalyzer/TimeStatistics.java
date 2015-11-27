package org.processmining.plugins.tsanalyzer;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;

public class TimeStatistics {

	protected HashMap<State, StateStatistics> states;
	protected HashMap<Transition, TransitionStatistics> transitions;

	public TimeStatistics() {
		super();
		states = new HashMap<State, StateStatistics>();
		transitions = new HashMap<Transition, TransitionStatistics>();
	}

	public Iterable<Entry<State, StateStatistics>> getStates() {
		return states.entrySet();
	}

	public Iterable<Entry<Transition, TransitionStatistics>> getTransitions() {
		return transitions.entrySet();
	}

	public StateStatistics getStatistics(State state) {
		StateStatistics statistics = states.get(state);
		if (statistics == null) {
			statistics = new StateStatistics();
			states.put(state, statistics);
		}
		return statistics;
	}

	public TransitionStatistics getStatistics(Transition transition) {
		TransitionStatistics statistics = transitions.get(transition);
		if (statistics == null) {
			statistics = new TransitionStatistics();
			transitions.put(transition, statistics);
		}
		return statistics;
	}

	class StateStatistics {

		private final DescriptiveStatistics soujourn;
		private final DescriptiveStatistics remaining;
		private final DescriptiveStatistics elapsed;

		public StateStatistics() {
			super();
			soujourn = new DescriptiveStatistics();
			remaining = new DescriptiveStatistics();
			elapsed = new DescriptiveStatistics();
		}

		public DescriptiveStatistics getSoujourn() {
			return soujourn;
		}

		public DescriptiveStatistics getRemaining() {
			return remaining;
		}

		public DescriptiveStatistics getElapsed() {
			return elapsed;
		}
	}

	class TransitionStatistics {
		private final DescriptiveStatistics duration;

		public TransitionStatistics() {
			super();
			duration = new DescriptiveStatistics();
		}

		public DescriptiveStatistics getDuration() {
			return duration;
		}
	}
}
