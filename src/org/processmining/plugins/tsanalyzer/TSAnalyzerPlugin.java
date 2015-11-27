package org.processmining.plugins.tsanalyzer;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.transitionsystem.payload.event.EventPayloadTransitionSystem;
import org.processmining.plugins.tsanalyzer.tscostanalyzer.CostTransitionSystemAnnotation;
import org.processmining.plugins.tsanalyzer.tscostanalyzer.CostTransitionSystemAnnotationConnection;
import org.processmining.plugins.tsanalyzer.tscostanalyzer.TSCostAnalyzer;

@Plugin(name = "Transition system analyzer", parameterLabels = { "Event payload transition system", "Log" }, returnLabels = { "Transition system time annotation" }, returnTypes = { CostTransitionSystemAnnotation.class }, userAccessible = true)
public class TSAnalyzerPlugin {

	/**
	 * Shows a dialog where the user can select which conversions to use, then
	 * does the conversions.
	 * 
	 * @param context
	 *            The current plug-in context.
	 * @param ts
	 *            The transition system to convert.
	 * @return The converted transition system.
	 */
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "M. Pesic", email = "m.pesic@tue.nl", uiLabel = UITopiaVariant.USEPLUGIN)
	@PluginVariant(variantLabel = "Select conversions to use", requiredParameterLabels = { 0, 1 })
	public CostTransitionSystemAnnotation options(final UIPluginContext context, final EventPayloadTransitionSystem ts,
			final XLog log) {
		return simple(context, ts, log);
	}

	/**
	 * Converts the given transition system using the default conversion
	 * settings.
	 * 
	 * @param context
	 *            The current plug-in context.
	 * @param ts
	 *            The transition system to convert.
	 * @return The converted transition system.
	 */
	@PluginVariant(variantLabel = "Use default conversions", requiredParameterLabels = { 0, 1 })
	public CostTransitionSystemAnnotation simple(final PluginContext context, final EventPayloadTransitionSystem ts,
			final XLog log) {
		context.getFutureResult(0).setLabel(
				"Cost Annotated " + ts.getLabel() + " from log " + XConceptExtension.instance().extractName(log));
		TSCostAnalyzer analyzer = new TSCostAnalyzer(context, ts, log);
		CostTransitionSystemAnnotation annotation = analyzer.annotate();
		context.addConnection(new CostTransitionSystemAnnotationConnection(annotation, ts, log));
		return annotation;
	}
}