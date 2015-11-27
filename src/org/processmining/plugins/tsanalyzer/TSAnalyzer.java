package org.processmining.plugins.tsanalyzer;

import java.util.Date;
import java.util.Map.Entry;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.deckfour.xes.extension.std.XExtendedEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;
import org.processmining.models.graphbased.directed.transitionsystem.payload.event.EventPayloadTransitionSystem;
import org.processmining.plugins.transitionsystem.miner.util.TSEventCache;

public class TSAnalyzer {

	/**
	 * The context of this miner.
	 */
	private final PluginContext context;

	/**
	 * the transition system to annotate
	 */
	private final EventPayloadTransitionSystem transitionSystem;

	/**
	 * eventCache is used for efficient access to events in XTrace
	 */
	private final TSEventCache eventCache;

	/**
	 * statistics for storing necessary time values
	 */
	private final TimeStatistics statistics;

	/**
	 * the log to be used to annotate the transition system
	 */
	private final XLog log;

	/**
	 * transition system annotation
	 */
	private final TimeTransitionSystemAnnotation annotation;

	public TSAnalyzer(final PluginContext context, final EventPayloadTransitionSystem ts, final XLog log) {
		super();
		this.context = context;
		transitionSystem = ts;
		this.log = log;
		eventCache = new TSEventCache();
		statistics = new TimeStatistics();
		annotation = new TimeTransitionSystemAnnotation();
	}

	/**
	 * This method creates the annotation for
	 * 
	 * @param log
	 * @return
	 */
	public TimeTransitionSystemAnnotation annotate() {

		context.getProgress().setMinimum(0);
		context.getProgress().setMaximum(log.size());
		/**
		 * For every trace a tick on the progress bar, and an extra tick for the
		 * modification phase.
		 */
		context.getProgress().setCaption("Annotating transition system with time");
		context.getProgress().setIndeterminate(false);

		// And now for the real thing.
		for (XTrace pi : log) {
			/**
			 * get the start and end time of this instance (trace)
			 */
			long startTime = getStartTime(pi);
			long endTime = getEndTime(pi);

			if ((startTime != -1) && (endTime != -1)) {
				/**
				 * process one instance (trace)
				 */
				for (int i = 0; i < pi.size(); i++) {
					processEvent(pi, i, startTime, endTime);
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
	 * statistical time (i.e., elapsed, remaining, etc. times) data.
	 * 
	 * @param pi
	 *            the trace to which the event belongs
	 * @param i
	 *            the index of the event to be processed
	 * @param startTime
	 *            the starting time of this trace
	 * @param endTime
	 *            the ending time of this trace
	 */
	private void processEvent(final XTrace pi, final int i, final long startTime, final long endTime) {
		/**
		 * Get the timestamp of the event we are processing
		 */
		Date currentTimestamp = XExtendedEvent.wrap(eventCache.get(pi, i)).getTimestamp();
		if (currentTimestamp != null) {

			long currentTime = currentTimestamp.getTime();

			/**
			 * Get the transition that corresponds to this event. Also get the
			 * source and the target for this transition.
			 */
			Transition transition = transitionSystem.getTransition(pi, i);
			if (transition != null) {
				State source = transition.getSource();
				State target = transition.getTarget();

				/**
				 * create statistics for the transition, source and target
				 */
				TimeStatistics.TransitionStatistics transitionStatistics = statistics.getStatistics(transition);
				TimeStatistics.StateStatistics sourceStatistics = statistics.getStatistics(source);
				TimeStatistics.StateStatistics targetStatistics = statistics.getStatistics(target);

				/**
				 * annotate the source only for the first event in the trace
				 */
				if (i == 0) {
					sourceStatistics.getRemaining().addValue(endTime - currentTime);
					sourceStatistics.getElapsed().addValue(0);
					sourceStatistics.getSoujourn().addValue(0);
				}

				/**
				 * annotate target with elapsed and remaining time
				 */
				targetStatistics.getElapsed().addValue(currentTime - startTime);
				targetStatistics.getRemaining().addValue(endTime - currentTime);

				/**
				 * process soujourn time
				 */
				if ((i != pi.size() - 1)) {
					Date nextTimestamp = getExtendedEvent(pi, i + 1).getTimestamp();
					if (nextTimestamp != null) {
						double soujourn = nextTimestamp.getTime() - currentTime;
						targetStatistics.getSoujourn().addValue(soujourn);
					}
				} else {
					targetStatistics.getSoujourn().addValue(0);
				}

				/**
				 * annotate the transition with the duration time
				 */
				if (i == 0) {
					transitionStatistics.getDuration().addValue(0.0);
				} else {
					Date previousTimestamp = getExtendedEvent(pi, i - 1).getTimestamp();
					if (previousTimestamp != null) {
						transitionStatistics.getDuration().addValue(currentTime - previousTimestamp.getTime());
					}
				}
			}
		}
	}

	private void printValues(String name, DescriptiveStatistics st) {

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
		/**
		 * create annotation for each state
		 */
		for (Entry<State, TimeStatistics.StateStatistics> entry : statistics.getStates()) {
			printValues("elapsed", entry.getValue().getElapsed());
			printValues("remainian", entry.getValue().getRemaining());
			printValues("soujourn", entry.getValue().getSoujourn());
			TimeStateAnnotation stateAnnotation = getStateAnnotation(entry.getKey());
			annotateState(stateAnnotation, entry.getValue());
		}
		/**
		 * create annotation for each transition
		 */
		for (Entry<Transition, TimeStatistics.TransitionStatistics> entry : statistics.getTransitions()) {
			TimeTransitionAnnotation transitionAnnotation = getTransitionAnnotation(entry.getKey());
			annotateTransition(transitionAnnotation, entry.getValue());
		}
	}

	/**
	 * Create state annotation from its statistics (i.e., elapsed, remaining and
	 * soujourn time).
	 * 
	 * @param state
	 *            to be annotated
	 * @param statistics
	 *            collected for this state
	 */
	private void annotateState(TimeStateAnnotation stateAnnotation, TimeStatistics.StateStatistics statistics) {
		annotateStatisticsProperty(stateAnnotation.getSoujourn(), statistics.getSoujourn());
		annotateStatisticsProperty(stateAnnotation.getRemaining(), statistics.getRemaining());
		annotateStatisticsProperty(stateAnnotation.getElapsed(), statistics.getElapsed());
	}

	/**
	 * Create transition annotation from its statistics (i.e., duration time).
	 * 
	 * @param transitionAnnotation
	 * @param statistics
	 */
	private void annotateTransition(TimeTransitionAnnotation transitionAnnotation,
			TimeStatistics.TransitionStatistics statistics) {
		annotateStatisticsProperty(transitionAnnotation.getDuration(), statistics.getDuration());
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
	private TimeStateAnnotation getStateAnnotation(State state) {
		TimeStateAnnotation stateAnnotation = annotation.getStateAnnotation(state);
		if (stateAnnotation == null) {
			stateAnnotation = new TimeStateAnnotation(state);
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
	 * @return time annotation for the transition object
	 */
	private TimeTransitionAnnotation getTransitionAnnotation(Transition transition) {
		TimeTransitionAnnotation transitionAnnotation = annotation.getTransitionAnnotation(transition);
		if (transitionAnnotation == null) {
			transitionAnnotation = new TimeTransitionAnnotation(transition);
			annotation.addTransitionAnnotation(transitionAnnotation);
		}
		return transitionAnnotation;
	}

	/**
	 * Gets the time stamp of the first event in a trace.
	 * 
	 * @param pi
	 *            the trace
	 * @return the timestamp of the first event in the trace
	 */
	private long getStartTime(XTrace pi) {
		try {
			for (int i = 0; i < pi.size(); i++) {
				Date timestamp = getExtendedEvent(pi, i).getTimestamp();
				if (timestamp != null) {
					return timestamp.getTime();
				}
			}
		} catch (Exception ce) {
		}
		return -1;
	}

	/**
	 * Gets the time stamp of the last event in a trace.
	 * 
	 * @param pi
	 *            the trace
	 * @return the timestamp of the last event in the trace
	 */
	private long getEndTime(XTrace pi) {
		try {
			for (int i = 0; i < pi.size(); i++) {
				Date timestamp = getExtendedEvent(pi, pi.size() - i - 1).getTimestamp();
				if (timestamp != null) {
					return timestamp.getTime();
				}
			}
		} catch (Exception ce) {
		}
		return -1;
	}

	/**
	 * Converts the i-the element of a trace into XExtendedEvent.
	 * 
	 * @param pi
	 *            the trace
	 * @return it-th element of the trace as XExtendedEvent
	 */
	private XExtendedEvent getExtendedEvent(XTrace trace, int index) {
		return XExtendedEvent.wrap(eventCache.get(trace, index));
	}

}
