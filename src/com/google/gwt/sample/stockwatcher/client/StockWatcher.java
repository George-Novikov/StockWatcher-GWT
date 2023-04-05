package com.google.gwt.sample.stockwatcher.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class StockWatcher implements EntryPoint{
  private VerticalPanel mainPanel = new VerticalPanel();
  private FlexTable stocksFlexPanel = new FlexTable();
  private HorizontalPanel addPanel = new HorizontalPanel();
  private TextBox newSymbolTextbox = new TextBox();
  private Button addStockButton = new Button("Add");
  private Label lastUpdatedLabel = new Label();
  
  private ArrayList<String> stocks = new ArrayList<String>();
  
  private static final int REFRESH_RATE = 5000;

  public void onModuleLoad(){
	  
	//Resources.INSTANCE.css().ensureInjected();
	//Window.alert(GWT.getModuleBaseURL());

    stocksFlexPanel.setText(0, 0, "ID");
    stocksFlexPanel.setText(0, 1, "Price");
    stocksFlexPanel.setText(0, 2, "Change");
    stocksFlexPanel.setText(0, 3, "Remove");
    
    stocksFlexPanel.setCellPadding(5);
    
    stocksFlexPanel.getRowFormatter().addStyleName(0, "watchListHeader");
    stocksFlexPanel.addStyleName("watchList");
    stocksFlexPanel.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
    stocksFlexPanel.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
    stocksFlexPanel.getCellFormatter().addStyleName(0, 3, "watchListRemoveColumn");

    addPanel.add(newSymbolTextbox);
    addPanel.add(addStockButton);
    
    addPanel.addStyleName("addPanel");

    mainPanel.add(stocksFlexPanel);
    mainPanel.add(addPanel);
    mainPanel.add(lastUpdatedLabel);

    RootPanel.get("stockList").add(mainPanel);

    newSymbolTextbox.setFocus(true);

    addStockButton.addClickHandler(new ClickHandler(){
      public void onClick(ClickEvent event) {
        addStock();
      }
    });

    newSymbolTextbox.addKeyDownHandler(new KeyDownHandler(){
      public void onKeyDown(KeyDownEvent event){
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
          addStock();
        }
      }
    });

  }

  private void addStock(){
	  final String symbols = newSymbolTextbox.getText().toUpperCase().trim();
	  
	  if (!symbols.matches("^[0-9A-Z\\.]{1,10}$")) {
		  Window.alert("\"" + symbols + "\" is not a valid symbol");
		  newSymbolTextbox.selectAll();
		  return;
	  }
	  
	  newSymbolTextbox.setText("");
	  
	  if (stocks.contains(symbols)) {
		  return;
	  }
	  
	  int rowCount = stocksFlexPanel.getRowCount();
	  stocks.add(symbols);
	  stocksFlexPanel.setText(rowCount, 0, symbols);
	  stocksFlexPanel.setWidget(rowCount, 2, new Label());
	  
	  stocksFlexPanel.getCellFormatter().addStyleName(rowCount, 1, "watchListNumericColumn");
	  stocksFlexPanel.getCellFormatter().addStyleName(rowCount, 2, "watchListNumericColumn");
	  stocksFlexPanel.getCellFormatter().addStyleName(rowCount, 3, "watchListRemoveColumn");
	  
	  Button removeStockButton = new Button("x");
	  removeStockButton.addStyleDependentName("remove");
	  removeStockButton.addClickHandler(new ClickHandler() {
		  public void onClick(ClickEvent event) {
			  int removedIndex = stocks.indexOf(symbols);
			  stocks.remove(removedIndex);
			  stocksFlexPanel.removeRow(removedIndex + 1);
		  }			  
	  });
	  stocksFlexPanel.setWidget(rowCount, 3, removeStockButton);
	  
	  newSymbolTextbox.setFocus(true);
	  
	  Timer refreshTimer = new Timer() {
		  @Override
		  public void run() {
			  refreshWatchList();
		  }
	  };
	  refreshTimer.scheduleRepeating(REFRESH_RATE);
	  
	  refreshWatchList();
  }
  
  private void refreshWatchList() {
	  final double MAX_PRICE = 100.0;
	  final double MAX_PRICE_CHANGE = 0.02;
	  
	  StockPrice[] prices = new StockPrice[stocks.size()];
	  
	  for (int i = 0; i < stocks.size(); i++){
		  double price = Random.nextDouble() * MAX_PRICE;
		  double change = price * MAX_PRICE_CHANGE * (Random.nextDouble()*2.0 - 1.0);
		  
		  prices[i] = new StockPrice(stocks.get(i), price, change);
	  }
	  
	  updateTable(prices);
  }
  
  private void updateTable(StockPrice[] prices) {
	  for (int i = 0; i < prices.length; i++) {
		  renderTable(prices[i]);
	  }
	  
	  DateTimeFormat dateFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
	  lastUpdatedLabel.setText("Last update: " + dateFormat.format(new Date()));
  }
  
  private void renderTable(StockPrice price) {
	  if (!stocks.contains(price.getSymbols())) {
		  return;
	  }
	  
	  int row = stocks.indexOf(price.getSymbols()) + 1;
	  
	  String priceText = NumberFormat.getFormat("#,##0.00").format(price.getPrice());
	  
	  NumberFormat changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
	  String changeText = changeFormat.format(price.getChange());
	  String changePercentText = changeFormat.format(price.getChangePercent());
	  
	  stocksFlexPanel.setText(row, 1, priceText);
	  //stocksFlexPanel.setText(row, 2, changeText + " (" + changePercentText + "%)");
	  Label changeWidget = (Label)stocksFlexPanel.getWidget(row, 2);
	  changeWidget.setText(changeText + " (" + changePercentText + "%)");
	  
	  String changeStyleName = "noChange";
	  if (price.getChangePercent() < -0.1f) {
		  changeStyleName = "risingPrice";
	  } else if (price.getChangePercent() > 0.1f) {
		  changeStyleName = "loweringPrice";
	  }
	  
	  changeWidget.setStyleName(changeStyleName);
  }
}

