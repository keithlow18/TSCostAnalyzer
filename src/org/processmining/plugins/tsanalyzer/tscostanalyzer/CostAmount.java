package org.processmining.plugins.tsanalyzer.tscostanalyzer;

public class CostAmount {

	private final double amount;
	private final String currency;

	public CostAmount(final double amount,final String currency) {
		super();
		this.amount = amount;
		this.currency = currency;
	}

	public double getValue() {
		return amount;
	}

	public String getCurrency() {
		return currency;
	}

	public String getString() {
		return getCurrency() + Double.toString(getValue());	
	}

}
