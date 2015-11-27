package org.processmining.plugins.tsanalyzer;

import org.processmining.plugins.tsanalyzer.annotation.AnnotationProperty;

public class StatisticsAnnotationProperty extends AnnotationProperty<Double> {

	private static final String AVERAGE = "average";
	private static final String STANDARD_DEVIATION = "st.dev.";
	private static final String MIN = "min";
	private static final String MAX = "max";
	private static final String SUM = "sum";
	private static final String VARIANCE = "var";
	private static final String FREQUENCY = "freq";
	private static final String MEDIAN = "median";

	//private Double mean;

	public StatisticsAnnotationProperty() {
		super();
		//mean = new Double(0.0);
		setValue(new Double(0.0));
		setMeasurement(AVERAGE, null);
		setMeasurement(STANDARD_DEVIATION, null);
		setMeasurement(MIN, null);
		setMeasurement(MAX, null);
		setMeasurement(SUM, null);
		setMeasurement(VARIANCE, null);
		setMeasurement(FREQUENCY, null);
		setMeasurement(MEDIAN, null);
	}

	/*
	 * private double getValue(String measurement){ return
	 * ((Double)getMeasurement(measurement)).doubleValue(); }
	 * 
	 * private void setValue(String measurement, double value){
	 * setMeasurement(measurement, new Double(value)); }
	 */

	public double getMin() {
		return ((Double) getMeasurement(MIN)).doubleValue();
	}

	public void setMin(double value) {
		setMeasurement(MIN, value);
	}

	public double getAverage() {
		return ((Double) getMeasurement(AVERAGE)).doubleValue();
	}

	public void setAverage(double value) {
		setMeasurement(AVERAGE, value);
	}

	public double getStandardDeviation() {
		return ((Double) getMeasurement(STANDARD_DEVIATION)).doubleValue();
	}

	public void setStandardDeviation(double value) {
		setMeasurement(STANDARD_DEVIATION, value);
	}

	public double getMax() {
		return ((Double) getMeasurement(MAX)).doubleValue();
	}

	public void setMax(double value) {
		setMeasurement(MAX, value);
	}

	public double getSum() {
		return ((Double) getMeasurement(SUM));
	}

	public void setSum(double value) {
		setMeasurement(SUM, value);
	}

	public double getVariance() {
		return ((Double) getMeasurement(VARIANCE)).doubleValue();
	}

	public void setVariance(double value) {
		setMeasurement(VARIANCE, value);
	}

	public long getFrequencey() {
		return ((Long) getMeasurement(FREQUENCY)).longValue();
	}

	public void setFrequencey(long value) {
		setMeasurement(FREQUENCY, value);
	}

	public double getMedian() {
		return ((Double) getMeasurement(MEDIAN)).doubleValue();
	}

	public void setMedian(double value) {
		setMeasurement(MEDIAN, value);
	}
}
