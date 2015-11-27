package org.processmining.plugins.tsanalyzer.gui;

import java.util.Map.Entry;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.processmining.plugins.tsanalyzer.Duration;
import org.processmining.plugins.tsanalyzer.StatisticsAnnotationProperty;
import org.processmining.plugins.tsanalyzer.annotation.Annotation;
import org.processmining.plugins.tsanalyzer.annotation.AnnotationProperty;

public class StatisticsAnnotationTable extends JTable {

	private static final long serialVersionUID = -8287319052256738522L;

	public StatisticsAnnotationTable() {
		super();
		setModel(new StatisticsAnnotationTableModel());
	}

	public void setAnnotation(Annotation annotation) {
		TableModel model = getModel();
		if (model instanceof StatisticsAnnotationTableModel) {
			((StatisticsAnnotationTableModel) model).setAnnotation(annotation);
		}
	}

	public void clear() {
		TableModel model = getModel();
		if (model instanceof StatisticsAnnotationTableModel) {
			((StatisticsAnnotationTableModel) model).clear();
		}
	}

	private class StatisticsAnnotationTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 6799371841545528012L;

		StatisticsAnnotationTableModel() {
			super(new Object[] { "name", "value (average)", "st. dev.", "min", "max", "frequency" }, 0);
		}

		void setAnnotation(Annotation annotation) {
			clear();
			if (annotation != null) {
				for (Entry<String, AnnotationProperty<?>> entry : annotation.getProperties()) {
					AnnotationProperty<?> property = entry.getValue();
					if (property instanceof StatisticsAnnotationProperty) {
						addRow(entry.getKey(), (StatisticsAnnotationProperty) property);
					}
				}
			}
		}

		void clear() {
			setRowCount(0);
		}

		private void addRow(String name, StatisticsAnnotationProperty data) {
			String value = Double.toString(data.getValue());
			String stdev = Double.toString(data.getStandardDeviation());
			String min = Double.toString(data.getMin());
			String max = Double.toString(data.getMax());
			String frequencey = Long.toString(data.getFrequencey());
			addRow(new Object[] { name, value, stdev, min, max, frequencey });
		}
	}
}
