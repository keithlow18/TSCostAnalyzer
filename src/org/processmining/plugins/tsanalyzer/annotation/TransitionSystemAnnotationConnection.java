package org.processmining.plugins.tsanalyzer.annotation;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.transitionsystem.payload.PayloadTransitionSystem;

public class TransitionSystemAnnotationConnection<A extends TransitionSystemAnnotation<?, ?>, P extends PayloadTransitionSystem<?>>
		extends AbstractConnection {

	public static final String TS = "TS";
	public static final String ANNOTATION = "ANNOTATION";
	public static final String LOG = "LOG";

	public TransitionSystemAnnotationConnection(A ann, P ts, XLog log) {
		super("Annotation TS =" + ts.getLabel() + " Log=" + XConceptExtension.instance().extractName(log));
		put(ANNOTATION, ann);
		put(TS, ts);
		put(LOG, log);
	}

}
