package org.processmining.plugins.tsanalyzer;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.transitionsystem.payload.event.EventPayloadTransitionSystem;
import org.processmining.plugins.tsanalyzer.annotation.TransitionSystemAnnotationConnection;

public class TimeTransitionSystemAnnotationConnection extends
		TransitionSystemAnnotationConnection<TimeTransitionSystemAnnotation, EventPayloadTransitionSystem> {

	public TimeTransitionSystemAnnotationConnection(TimeTransitionSystemAnnotation ann,
			EventPayloadTransitionSystem ts, XLog log) {
		super(ann, ts, log);
	}

}
