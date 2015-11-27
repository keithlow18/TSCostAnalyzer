package org.processmining.plugins.tsanalyzer.tscostanalyzer;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.transitionsystem.payload.event.EventPayloadTransitionSystem;
import org.processmining.plugins.tsanalyzer.annotation.TransitionSystemAnnotationConnection;

public class CostTransitionSystemAnnotationConnection extends TransitionSystemAnnotationConnection<CostTransitionSystemAnnotation, EventPayloadTransitionSystem> {

	public CostTransitionSystemAnnotationConnection(CostTransitionSystemAnnotation ann,
			EventPayloadTransitionSystem ts, XLog log) {
		super(ann, ts, log);
	}

}
