package com.google.gwt.sample.stockwatcher.client;

public class StockPrice {
	
	private String symbols;
	private double price;
	private double change;
	
	public StockPrice() {}
	
	public StockPrice(String symbols, double price, double change) {
		this.symbols = symbols;
		this.price = price;
		this.change = change;
	}

	public double getChange() {
		return change;
	}

	public void setChange(double change) {
		this.change = change;
	}
	
	public double getChangePercent() {
		return 100.0 * this.change / this.price;
	}

	public String getSymbols() {
		return symbols;
	}

	public void setSymbols(String symbols) {
		this.symbols = symbols;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
