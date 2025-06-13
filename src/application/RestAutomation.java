package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Handles user interaction with the RestAutomation POS system.
 * Manages UI updates, report generation, and order processing.
 * 
 * @author Musab
 */
public class RestAutomation extends Application {
	/** List of all available menu items. */
    private final List<MenuItem> menuItems = new ArrayList<>();
    
    /** Font for displaying labels in Geez script. */
    Font geezFont = Font.loadFont("file:resources/fonts/AbyssinicaSIL-Regular.ttf", 16);
    
    /** VBox that displays the list of selected sale items. */
    private final VBox salesBox = new VBox(5);
    
    /** Label that shows the total price of the current order. */
    private final Label orderTotalPriceLabel = new Label("Total Order Price: Birr0.00");
    
    /** The main center panel of the UI that displays view content. */
    private final VBox centerPanel = new VBox(); 

    /** List of today's orders loaded from the database. */
    private List<Order> todaysOrders = new ArrayList<>();
    
    /** DAO for handling order operations. */
    private OrderDAO orderDAO = new OrderDAO();
    
    /** DAO for menu item operations. */
    private MenuItemDAO menuItemDAO = new MenuItemDAO();
    
    /** DAO for cash operations. */
    private CashDAO cashDAO = new CashDAO();
    
    /** DAO for logging user actions. */
    private final LogDAO logDAO = new LogDAO();
    
    /** Items in the current order being placed. */
    private List<MenuItem> orderItems = new ArrayList<>();
    
    /** Total price of the current order. */
    private double orderTotalPrice;
    
    
    /**
     * Launches the JavaFX application window and sets up the UI layout.
     *
     * @param primaryStage the primary stage for the JavaFX app
     */
    @Override
    public void start(Stage primaryStage) {
    	// JavaFX layout and scene setup code...
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        initializeMenuItems();

        VBox menuManagement = createMenuManagementPanel();
        Label firstLabel = new Label("𝖂𝖊𝖑𝖈𝖔𝖒𝖊 𝕿𝖔 𝕽𝖊𝖘𝖙𝕬𝖚𝖙𝖔𝖒𝖆𝖙𝖎𝖔𝖓 𝕬𝖕𝖕");
        firstLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: darkblue; -fx-font-weight: bold;");
        firstLabel.setFont(Font.font(27));
        firstLabel.setAlignment(Pos.TOP_CENTER);
        firstLabel.setPadding(new Insets(100));
        firstLabel.setMaxWidth(Double.MAX_VALUE);
        
        centerPanel.getChildren().add(firstLabel);
        
        VBox salesPanel = createSalesPanel();
        
        root.setLeft(menuManagement);
        root.setCenter(centerPanel);
        root.setRight(salesPanel);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("RestAutomation App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Initializes the menu items from the database.
     */
	private void initializeMenuItems() {
    	List<MenuItem> items = menuItemDAO.getAllItems();
		for (MenuItem item : items) {
			menuItems.add(item);
		}
		todaysOrders = orderDAO.getTodayOrders();
    }

    private VBox createMenuManagementPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 1;");
        
        Button addOrderBtn = new Button("Add Order");
        addOrderBtn.setPrefWidth(200);
        addOrderBtn.setOnAction(_ -> showAddOrderView());

        Button viewOrdersBtn = new Button("View Orders");
        viewOrdersBtn.setPrefWidth(200);
        viewOrdersBtn.setOnAction(_ -> showViewOrdersView());
        
        Button annulOrdersBtn = new Button("Annul Orders");
        annulOrdersBtn.setPrefWidth(200);
        annulOrdersBtn.setOnAction(_ -> showAnnulOrdersView());
        
        Button viewReportBtn = new Button("View Reports");
        viewReportBtn.setPrefWidth(200);
        viewReportBtn.setOnAction(_ -> showViewReportView());;
        
        Button sendReportBtn = new Button("Send Report");
        sendReportBtn.setPrefWidth(200);
        sendReportBtn.setOnAction(_ -> sendReports());
        
        Button exportReportBtn = new Button("Export Report");
        exportReportBtn.setPrefWidth(200);
        exportReportBtn.setOnAction(_ -> exportReportsToFile());
        
        Button addCashBtn = new Button("Add Cash");
        addCashBtn.setPrefWidth(200);
        addCashBtn.setOnAction(_ -> showAddCashView());
        
        Button editCashBtn = new Button("Edit Cash");
        editCashBtn.setPrefWidth(200);
        editCashBtn.setOnAction(_ -> showEditCashView());
        Label title = new Label("RestAutomation App");
        
        Button previousOrdersBtn = new Button("Previous Orders");
        previousOrdersBtn.setPrefWidth(200);
        previousOrdersBtn.setOnAction(_ -> showPreviousOrdersView());

        Button viewLogsBtn = new Button("View Logs");
        viewLogsBtn.setPrefWidth(200);
        viewLogsBtn.setOnAction(_ -> showLogsView());

        Button undoBtn = new Button("Undo Last Sale");
        undoBtn.setPrefWidth(200);
        undoBtn.setOnAction(_ -> {
            if (!orderItems.isEmpty()) {
                // Remove the last item from the actual list
                MenuItem lastItem = orderItems.remove(orderItems.size() - 1);

                // Update quantity in salesBox
                int remaining = Collections.frequency(orderItems, lastItem);

                // Find the corresponding label
                for (int i = 0; i < salesBox.getChildren().size(); i++) {
                    if (salesBox.getChildren().get(i) instanceof Label label &&
                        label.getText().startsWith(lastItem.getName())) {

                        if (remaining == 0) {
                            salesBox.getChildren().remove(i);
                        } else {
                            label.setText(lastItem.getName() + " x" + remaining + " - Birr" + String.format("%.2f", lastItem.getPrice() * remaining));
                            label.setFont(geezFont);
                        }
                        break;
                    }
                }

                orderTotalPrice -= lastItem.getPrice();
                updateOrderLabels();
            }
        });
        
        orderTotalPriceLabel.setStyle("-fx-font-weight: bold;");

        
        panel.getChildren().addAll(title, addOrderBtn, viewOrdersBtn, annulOrdersBtn, viewReportBtn, 
        		sendReportBtn, exportReportBtn, addCashBtn, editCashBtn, previousOrdersBtn, viewLogsBtn, undoBtn, orderTotalPriceLabel);

        return panel;
    }
    
    private void showAddOrderView() {
        TabPane tabPane = new TabPane();
        
        Tab foodTab = new Tab("Food", createMenuTabContent("food"));
        Tab juiceTab = new Tab("Juice", createMenuTabContent("juice"));
        Tab softDrinkTab = new Tab("Soft Drinks", createMenuTabContent("Soft Drink"));
        Tab hotDrinkTab = new Tab("Hot Drinks", createMenuTabContent("Hot Drink"));

        tabPane.getTabs().addAll(foodTab, juiceTab, softDrinkTab, hotDrinkTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Button placeOrderButton = new Button("Place Order");
        placeOrderButton.setPrefHeight(30);
        placeOrderButton.setMaxWidth(150); // full width if needed
        
        placeOrderButton.setOnAction((_ -> showPlaceOrderConfirmationDialog()));

        // Style and alignment (optional)
        VBox bottomBox = new VBox(placeOrderButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));
        bottomBox.setStyle("-fx-background-color: #f0f0f0;");

        centerPanel.getChildren().setAll(tabPane, bottomBox);
    }

    private void showViewOrdersView() {
        VBox ordersBox = new VBox(10);
        ordersBox.setPadding(new Insets(10));
        Label ordersListLabel = new Label("Orders made today:");
        ordersListLabel.setStyle("-fx-font-weight: bold;");
        ordersBox.getChildren().add(ordersListLabel);

        for (Order order : todaysOrders) {
        	Label lbl = new Label(order.toString() + "\n");
        	lbl.setFont(geezFont);
            ordersBox.getChildren().add(lbl);
        }

        ScrollPane scrollPane = new ScrollPane(ordersBox);
        scrollPane.setFitToWidth(true);
        centerPanel.getChildren().setAll(scrollPane); // Replace center panel content
    }

    private void showEditCashView() {
    	
    	HBox hbox = new HBox(10);
    	hbox.setPadding(new Insets(10));
    	VBox vbox = new VBox(10);
    	vbox.setPadding(new Insets(100));
    	
    	Label cashTitle = new Label();
    	cashTitle.setText("Edit Cash");
    	cashTitle.setAlignment(Pos.TOP_CENTER);
    	cashTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
    	cashTitle.setTextAlignment(TextAlignment.CENTER);
    	hbox.getChildren().add(cashTitle);
    	
    	
    	Label currentCash = new Label();
    	currentCash.setText("Current cash: 20000");
    	currentCash.setAlignment(Pos.TOP_CENTER);
    	
		TextField cashField = new TextField();
        cashField.setPromptText("Total Received Cash");
        cashField.setMaxWidth(250);
        cashField.setAlignment(Pos.TOP_CENTER);
        
        Button addItemBtn = new Button("Commit Changes");
        currentCash.setText("Current cash: " + cashDAO.getTodayCash());

        addItemBtn.setMaxWidth(150);
        addItemBtn.setCenterShape(true);
        addItemBtn.setAlignment(Pos.TOP_CENTER);
        addItemBtn.setOnAction(_ -> {
            try {
                double amount = Double.parseDouble(cashField.getText());
                cashDAO.updateTodayCash(amount);
                showAlert(Alert.AlertType.INFORMATION, "Cash Updated", "Today's cash has been updated.");
                currentCash.setText("Current cash: " + cashDAO.getTodayCash());
                logAction("User edited cash: " + amount);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number.");
                logAction("User editing cash FAILED!");
            }
        });
        
        vbox.setMaxWidth(Double.MAX_VALUE);
        vbox.getChildren().add(cashTitle);
        vbox.getChildren().add(new Separator());
        vbox.getChildren().add(currentCash);
        vbox.getChildren().add(new Separator());
        vbox.getChildren().add(cashField);
        vbox.getChildren().add(addItemBtn);
        vbox.setAlignment(Pos.BASELINE_CENTER);
        
        centerPanel.getChildren().setAll(vbox);
        centerPanel.setAlignment(Pos.TOP_CENTER);
	}

	private void showAddCashView() {
		HBox hbox = new HBox(10);
    	hbox.setPadding(new Insets(10));
    	VBox vbox = new VBox(10);
    	vbox.setPadding(new Insets(100));
    	
    	Label cashTitle = new Label();
    	cashTitle.setText("Add Cash");
    	cashTitle.setAlignment(Pos.TOP_CENTER);
    	cashTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
    	cashTitle.setTextAlignment(TextAlignment.CENTER);
    	hbox.getChildren().add(cashTitle);
    	
		TextField cashField = new TextField();
        cashField.setPromptText("Total Received Cash");
        cashField.setMaxWidth(250);
        cashField.setAlignment(Pos.TOP_CENTER);
        
        Button addCashBtn = new Button("Add Cash");
        addCashBtn.setMaxWidth(150);
        addCashBtn.setCenterShape(true);
        addCashBtn.setAlignment(Pos.TOP_CENTER);
        addCashBtn.setOnAction(_ -> {
            try {
                double amount = Double.parseDouble(cashField.getText());
                cashDAO.addCash(amount);
                showAlert(Alert.AlertType.INFORMATION, "Cash Added", "Today's cash has been recorded.");
                logAction("User added cash: " + amount);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number.");
                logAction("User adding cash FAILED! ");
            }
        });

        vbox.setMaxWidth(Double.MAX_VALUE);
        vbox.getChildren().add(cashTitle);
        vbox.getChildren().add(new Separator());
        vbox.getChildren().add(cashField);
        vbox.getChildren().add(addCashBtn);
        vbox.setAlignment(Pos.BASELINE_CENTER);
        
        centerPanel.getChildren().setAll(vbox);
        centerPanel.setAlignment(Pos.TOP_CENTER);

	}

	private void sendReports() {
	    String reportContent = generateFormattedReport();

	    try {
	    	SimpleEmailSender emailSender = new SimpleEmailSender();
	    	emailSender.sendEmail(reportContent);
	    	String emailList = """
	    			Email sent to: 1. 
	    				sururmb@gmail.com
	    				munirasaid1979@gmail.com""";
	    	showAlert(Alert.AlertType.INFORMATION, "Email Sent Successful", emailList);
	    	logAction("User sent reports via email");
	    } catch (Exception e) {
	        e.printStackTrace();
	        logAction("User sending reports via email FAILED!\n" + e.getMessage());
	    }
	}

	private void exportReportsToFile() {
	    String reportContent = generateFormattedReport();

	    try {
	        String fileName = "Daily_Sales_Report_" + LocalDate.now() + ".txt";
	        FileWriter writer = new FileWriter(fileName);
	        writer.write(reportContent);
	        writer.close();

	        showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Report exported to " + fileName);
	        logAction("Exported report to file.");
	    } catch (IOException e) {
	        showAlert(Alert.AlertType.ERROR, "Export Failed", "Failed to export report: " + e.getMessage());
	        logAction("Export report to file FAILED!");
	    }
	}

	private void showViewReportView() {
	    String reportContent = generateFormattedReport();

	    TextArea reportArea = new TextArea(reportContent);
	    reportArea.setEditable(false);
	    reportArea.setStyle("-fx-font-family: 'monospaced'; -fx-font-size: 14px;");
	    reportArea.setPrefHeight(500);

	    centerPanel.getChildren().setAll(new ScrollPane(reportArea));
	    logAction("User viewed reports");
	}
	
	/**
     * Logs a user action into the database.
     *
     * @param action the action description
     */
    private void logAction(String action) {
        logDAO.insertLog(action);
    }

	private String generateFormattedReport() {
	    LocalDate today = LocalDate.now();
	    org.joda.time.LocalDate ethiopianDate = EthiopianDateUtil.toEthiopianDate(today);

	    double foodSales = calculateCategorySales("food");
	    double juiceSales = calculateCategorySales("juice");
	    double hotDrinkSales = calculateCategorySales("Hot Drink");
	    double softDrinkSales = calculateCategorySales("Soft Drink");
	    
	    double taxed = getTaxedAmount();    // stub or DB value
	    double tot = getTotalTax();              // stub or DB value
	    double hidden = getHiddenAmount();  // stub or DB value

	    double total = foodSales + juiceSales + hotDrinkSales + softDrinkSales;
	    double total1 = taxed + hidden;
	    double cash = getCurrentCash(); // fetch actual cash received
	    double difference = cash - total;
	    
	    System.out.println(EthiopianDateUtil.formatEthiopianDate(ethiopianDate));
	    
	    return String.format("""
	        Gregorian Date: %s
	        Ethiopian Date : %s

	        Food Sales:                 %.2f
	        Juice Sales:                %.2f
	        HDS (hot drinks sales):     %.2f
	        SDS (soft drinks sales):    %.2f
	        -----------------------------------------
	        Total:                      %.2f
	        Total:                      %.2f
	        Cash:                       %.2f
	        Difference:                 %.2f

	        Bank 1 (Taxed):             %.2f
	        TOT :                       %.2f
	        Bank 2 (Hidden):            %.2f
	        """,
	        today.format(DateTimeFormatter.ofPattern("dd - MM - yyyy")),
	        EthiopianDateUtil.formatEthiopianDate(ethiopianDate),
	        foodSales, juiceSales, hotDrinkSales, softDrinkSales,
	        total, total1, cash, difference,
	        taxed, tot, hidden
	    );
	    
	}

	private double calculateCategorySales(String category) {
	    return todaysOrders.stream()
	        .flatMap(order -> order.getOrdersList().stream())
	        .filter(item -> item.getType().equalsIgnoreCase(category))
	        .mapToDouble(MenuItem::getPrice)
	        .sum();
	}

	private double getCurrentCash() {
	    return cashDAO.getTodayCash(); // Now pulls from DB
	}

	private double getTaxedAmount() {
		double taxedAmount = 0;
		List<MenuItem> orderMenuItems = new ArrayList<>();
	    for (Order order : todaysOrders) {
	    	
	    	if (order.getIsTaxed()) {
	    		orderMenuItems = order.getOrdersList();
	    		for (MenuItem item : orderMenuItems) {
	    			taxedAmount += item.getPrice();
	    		}
	    	}
	    }
	    return taxedAmount;
	}

	private double getTotalTax() {
		double taxedAmount = getTaxedAmount();
		double  companyShare = taxedAmount / 1.1;
		double tot = taxedAmount - companyShare;
	    return tot;
	}

	private double getHiddenAmount() {
		double totalAmount = 0.0;
		for (Order order : todaysOrders) {
			for (MenuItem item : order.getOrdersList()) {
				totalAmount += item.getPrice();
			}
		}
		return totalAmount - getTaxedAmount();
	}

	private void showAlert(Alert.AlertType type, String title, String content) {
	    Alert alert = new Alert(type);
	    alert.setTitle(title);
	    alert.setHeaderText(null);
	    alert.setContentText(content);
	    alert.showAndWait();
	}

	private void showAnnulOrdersView() {
		VBox annulOrdersView = new VBox(10);
		annulOrdersView.setPadding(new Insets(20));
		annulOrdersView.setAlignment(Pos.TOP_CENTER);
		
		for (Order order : todaysOrders) {
		    HBox orderRow = new HBox(10);
		    orderRow.setAlignment(Pos.CENTER_LEFT);

		    Label orderLabel = new Label(order.toString()); // customize display
		    orderLabel.setFont(geezFont);
		    Button annulBtn = new Button("❌");
		    annulBtn.setStyle("-fx-text-fill: red;");

		    annulBtn.setOnAction(_ -> showAnnulConfirmationDialog(order));

		    orderRow.getChildren().addAll(orderLabel, annulBtn);
		    annulOrdersView.getChildren().add(orderRow);
		}
		
		ScrollPane scrollPane = new ScrollPane(annulOrdersView);
        scrollPane.setFitToWidth(true);
        centerPanel.getChildren().setAll(scrollPane); // Replace center panel content
		centerPanel.setAlignment(Pos.CENTER);
	}
	
	private void showAnnulConfirmationDialog(Order order) {
	    Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
	    confirmDialog.setTitle("Annul Order");
	    confirmDialog.setHeaderText("Are you sure you want to annul this order?");

	    // Create VBox to hold itemized content
	    VBox itemBox = new VBox(5);
	    itemBox.setPadding(new Insets(10));

	    for (MenuItem item : order.getOrdersList()) { // assuming getItems() returns a List<MenuItem>
	        Label itemLabel = new Label(item.getName() + " - Birr" + item.getPrice());
	        itemLabel.setFont(geezFont);
	        itemBox.getChildren().add(itemLabel);
	    }

	    // Wrap items in scrollable container
	    ScrollPane scrollPane = new ScrollPane(itemBox);
	    scrollPane.setFitToWidth(true);
	    scrollPane.setPrefViewportHeight(150); // adjustable height to prevent dialog from stretching
	    scrollPane.setStyle("-fx-background-color: transparent;");

	    VBox content = new VBox(10, new Label("Order Details:"), scrollPane);
	    content.setPadding(new Insets(10));
	    confirmDialog.getDialogPane().setContent(content);

	    ButtonType yesButton = new ButtonType("Yes, Annul", ButtonBar.ButtonData.YES);
	    ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
	    confirmDialog.getButtonTypes().setAll(yesButton, noButton);

	    Optional<ButtonType> result = confirmDialog.showAndWait();
	    if (result.isPresent() && result.get() == yesButton) {
	        annulOrder(order);
	        refreshAnnulOrdersView();
	    }
	}

	private void annulOrder(Order order) {
	    todaysOrders.remove(order);
	    logAction("User Annulled Order: " + order.getId());
	}

	private void refreshAnnulOrdersView() {
	    showAnnulOrdersView(); // or recreate the VBox and update centerPanel
	}





    private VBox createSalesPanel() {
        VBox panel = new VBox(10);
        panel.setMinWidth(300);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ccc; -fx-border-width: 1;");

        Label title = new Label("Current Order Summary");
        
        
        
        ScrollPane salesScrollPane = new ScrollPane(salesBox);
        salesScrollPane.setFitToWidth(true);
        salesScrollPane.setPrefHeight(400); // or whatever height you prefer
        salesScrollPane.setStyle("-fx-background: #ffffff;");

        panel.getChildren().addAll(title, new Separator(), salesScrollPane);

        return panel;
    }
    
    private ScrollPane createMenuTabContent(String type) {
        VBox box = new VBox(10);
        GridPane foodGrid = new GridPane();
        box.setPadding(new Insets(10));
        ScrollPane scrollPane;
        
        if (!type.equalsIgnoreCase("food")) {
        	
        	for (MenuItem item : menuItems) {
                if (item.getType().equalsIgnoreCase(type)) {
                    Button btn = new Button(item.getName() + " - Birr" + item.getPrice());
                    btn.setFont(geezFont);
                    btn.setMaxWidth(Double.MAX_VALUE);
                    btn.setOnAction(_ -> handleItemClick(item));
                    box.getChildren().add(btn);
                }
        	}
        	scrollPane = new ScrollPane(box);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #fff; -fx-border-color: #ccc;");
        } else {
        	
        	foodGrid.setHgap(10);
        	foodGrid.setVgap(10);
        	foodGrid.setPadding(new Insets(10));
        	
        	int col = 0;
        	int row = 0;
        	
        	for (MenuItem item : menuItems) {
        		if (item.getType().equalsIgnoreCase("food")) {
        			Button btn = new Button(item.getName() + " - Birr" + item.getPrice());
        			btn.setFont(geezFont);
        			btn.setPrefWidth(300);
        			btn.setOnAction(_ -> handleItemClick(item));
        			foodGrid.add(btn, col, row);
        			
        			col++;
        			if (col == 3) {
        				col = 0;
        				row++;
        			}
        		}
        	}
        	scrollPane = new ScrollPane(foodGrid);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #fff; -fx-border-color: #ccc;");
        }
        
        return scrollPane;
    }


    private void handleItemClick(MenuItem item) {
        orderItems.add(item);
        int quantity = Collections.frequency(orderItems, item);

        // Check if the item already exists in salesBox
        Optional<Label> existingLabel = salesBox.getChildren().stream()
            .filter(node -> node instanceof Label)
            .map(node -> (Label) node)
            .filter(label -> label.getText().contains(item.getName()))
            .findFirst();

        if (existingLabel.isPresent()) {
            existingLabel.get().setText(item.getName() + " x" + quantity + " - Birr" + String.format("%.2f", item.getPrice() * quantity));
            existingLabel.get().setFont(geezFont);
        } else {
            Label itemLabel = new Label(item.getName() + " x1 - Birr" + String.format("%.2f", item.getPrice()));
            itemLabel.setFont(geezFont);
            salesBox.getChildren().add(itemLabel);
        }

        // Update total order price
        orderTotalPrice += item.getPrice();
        orderTotalPriceLabel.setText("Total Order Price: Birr" + String.format("%.2f", orderTotalPrice));
    }

    private void showPlaceOrderConfirmationDialog() {
        Dialog<Boolean> confirmDialog = new Dialog<>();
        confirmDialog.setTitle("Placing Order");
        confirmDialog.setHeaderText("Is the Order Taxed?");

        // Create custom dialog buttons
        ButtonType placeOrderButtonType = new ButtonType("Place Order", ButtonBar.ButtonData.OK_DONE);
        confirmDialog.getDialogPane().getButtonTypes().addAll(placeOrderButtonType, ButtonType.CANCEL);

        // Aggregate order items
        Map<MenuItem, Integer> itemQuantities = new HashMap<>();
        for (MenuItem item : orderItems) {
            itemQuantities.put(item, itemQuantities.getOrDefault(item, 0) + 1);
        }

        // VBox to hold order items
        VBox itemList = new VBox(5);
        
        for (Map.Entry<MenuItem, Integer> entry : itemQuantities.entrySet()) {
            MenuItem item = entry.getKey();
            int quantity = entry.getValue();
            Label itemLabel = new Label(item.getName() + " x" + quantity + " - Birr" + String.format("%.2f", item.getPrice() * quantity));
            itemLabel.setFont(geezFont);
            itemList.getChildren().add(itemLabel);
            orderTotalPrice += item.getPrice() * quantity;
        }
        Label totalLabel = new Label("Total Price: " + orderTotalPrice);
        totalLabel.setFont(geezFont);
        itemList.getChildren().add(totalLabel);

        // Wrap itemList in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(itemList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(200); // limit height so dialog doesn't grow too tall
        scrollPane.setStyle("-fx-background-color:transparent;");

        // Tax checkbox
        CheckBox taxCheckBox = new CheckBox("This order is taxed");

        // Layout
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.getChildren().addAll(new Label("Order Items:"), scrollPane, taxCheckBox);

        confirmDialog.getDialogPane().setContent(content);

        // Result converter
        confirmDialog.setResultConverter(dialogButton -> {
            if (dialogButton == placeOrderButtonType) {
                boolean isTaxed = taxCheckBox.isSelected();
                if (isTaxed) {
                	showTOTBreakdownDialog();
                }
                placeOrder(isTaxed);
                
                //showOrderSummaryDialog(orderTotalPrice, isTaxed);
                printOrderReceipt(orderTotalPrice);
                
                 // Your own method to handle saving the order
                return true;
            }
            return null;
        });
        confirmDialog.showAndWait();
    }
    
    private void showTOTBreakdownDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("TOT Breakdown");

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        double totalCompanyShare = 0.0;
        double totalTOT = 0.0;
        double totalOriginal = 0.0;

        for (MenuItem item : orderItems) {
            double originalPrice = item.getPrice();
            double companyShare = originalPrice / 1.1;
            double tot = originalPrice - companyShare;

            totalCompanyShare += companyShare;
            totalTOT += tot;
            totalOriginal += originalPrice;

            Label itemBreakdown = new Label(
                    item.getName() + " | " +
                    "Company: " + String.format("%.2f", companyShare) + " Br | " +
                    "TOT: " + String.format("%.2f", tot) + " Br | " +
                    "Total: " + String.format("%.2f", originalPrice) + " Br"
            );
            itemBreakdown.setFont(geezFont);
            content.getChildren().add(itemBreakdown);
        }

        Separator separator = new Separator();
        Label totalBreakdown = new Label("Total Company Share: " + String.format("%.2f", totalCompanyShare) +
                " Br\nTotal TOT: " + String.format("%.2f", totalTOT) + " Br\nTotal Original Price: " + String.format("%.2f", totalOriginal));
        totalBreakdown.setStyle("-fx-font-weight: bold;");

        content.getChildren().addAll(separator, totalBreakdown);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
    
    private void showPreviousOrdersView() {
        List<Order> allOrders = orderDAO.getAllOrders(); // Assumes this is implemented
        Map<LocalDate, List<Order>> groupedByDate = new HashMap<>();

        // Group orders by the date portion of the timestamp
        for (Order order : allOrders) {
            LocalDate date = order.getTimestamp().toLocalDate(); // Extract date only
            groupedByDate.computeIfAbsent(date, _ -> new ArrayList<>()).add(order);
        }

        VBox orderGroups = new VBox(15);
        orderGroups.setPadding(new Insets(10));

        TextField searchBox = new TextField();
        searchBox.setPromptText("Search date (e.g. 2024-12-31)");
        searchBox.setMaxWidth(300);

        for (Map.Entry<LocalDate, List<Order>> entry : groupedByDate.entrySet().stream()
                .sorted(Map.Entry.<LocalDate, List<Order>>comparingByKey().reversed()).toList()) {

            LocalDate date = entry.getKey();
            List<Order> orders = entry.getValue();

            VBox section = new VBox(5);
            section.setStyle("-fx-border-color: #ccc; -fx-border-width: 0 0 1 0; -fx-background-color: #f8f8f8; -fx-padding: 10;");

            Label dateLabel = new Label("📅 Date: " + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            dateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: darkblue;");
            section.getChildren().add(dateLabel);

            for (Order o : orders) {
                Label orderLabel = new Label(o.toString());
                orderLabel.setFont(geezFont);
                section.getChildren().add(orderLabel);
            }

            orderGroups.getChildren().add(section);
        }

        ScrollPane scrollPane = new ScrollPane(orderGroups);
        scrollPane.setFitToWidth(true);

        VBox wrapper = new VBox(10, searchBox, scrollPane);
        wrapper.setPadding(new Insets(10));
        centerPanel.getChildren().setAll(wrapper);

        // 🔍 Add search functionality
        searchBox.textProperty().addListener((_, _, newVal) -> {
            orderGroups.getChildren().clear();

            groupedByDate.entrySet().stream()
                .filter(e -> e.getKey().toString().contains(newVal.trim()))
                .sorted(Map.Entry.<LocalDate, List<Order>>comparingByKey().reversed())
                .forEach(entry -> {
                    VBox section = new VBox(5);
                    section.setStyle("-fx-border-color: #ccc; -fx-border-width: 0 0 1 0; -fx-background-color: #f8f8f8; -fx-padding: 10;");
                    Label dateLabel = new Label("📅 Date: " + entry.getKey().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    dateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: darkblue;");
                    section.getChildren().add(dateLabel);

                    for (Order o : entry.getValue()) {
                        Label orderLabel = new Label(o.toString());
                        orderLabel.setFont(geezFont);
                        section.getChildren().add(orderLabel);
                    }
                    orderGroups.getChildren().add(section);
                });
        });
    }
    
    private void showLogsView() {
        List<LogEntry> allLogs = logDAO.getAllLogs();
        Map<LocalDate, List<LogEntry>> logsByDate = new HashMap<>();

        for (LogEntry log : allLogs) {
            LocalDate date = log.getTimeStamp().toLocalDate();
            logsByDate.computeIfAbsent(date, _ -> new ArrayList<>()).add(log);
        }

        VBox logsContainer = new VBox(15);
        logsContainer.setPadding(new Insets(10));

        TextField searchBox = new TextField();
        searchBox.setPromptText("Search date (e.g. 2024-12-31)");
        searchBox.setMaxWidth(300);

        for (Map.Entry<LocalDate, List<LogEntry>> entry : logsByDate.entrySet().stream()
                .sorted(Map.Entry.<LocalDate, List<LogEntry>>comparingByKey().reversed()).toList()) {

            VBox dayBox = new VBox(5);
            dayBox.setStyle("-fx-background-color: #f2f2f2; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0; -fx-padding: 10;");

            Label dateLabel = new Label("🗓️ Date: " + entry.getKey());
            dateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #444;");
            dayBox.getChildren().add(dateLabel);

            for (LogEntry log : entry.getValue()) {
                Label logLabel = new Label(log.getTimeStamp().toLocalTime() + " — " + log.getAction());
                logLabel.setFont(geezFont);
                dayBox.getChildren().add(logLabel);
            }

            logsContainer.getChildren().add(dayBox);
        }

        ScrollPane scrollPane = new ScrollPane(logsContainer);
        scrollPane.setFitToWidth(true);

        VBox wrapper = new VBox(10, searchBox, scrollPane);
        wrapper.setPadding(new Insets(10));
        centerPanel.getChildren().setAll(wrapper);
        logAction("User viewed logs");
        // 🔍 Filter logs by date
        searchBox.textProperty().addListener((_, _, newVal) -> {
            logsContainer.getChildren().clear();
            logsByDate.entrySet().stream()
                .filter(e -> e.getKey().toString().contains(newVal.trim()))
                .sorted(Map.Entry.<LocalDate, List<LogEntry>>comparingByKey().reversed())
                .forEach(entry -> {
                    VBox dayBox = new VBox(5);
                    dayBox.setStyle("-fx-background-color: #f2f2f2; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0; -fx-padding: 10;");
                    Label dateLabel = new Label("🗓️ Date: " + entry.getKey());
                    dateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #444;");
                    dayBox.getChildren().add(dateLabel);
                    for (LogEntry log : entry.getValue()) {
                        Label logLabel = new Label(log.getTimeStamp().toLocalTime() + " — " + log.getAction());
                        logLabel.setFont(geezFont);
                        dayBox.getChildren().add(logLabel);
                    }
                    logsContainer.getChildren().add(dayBox);
                });
        });
    }


    private void printOrderReceipt(double totalPrice) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(null)) {
            VBox printContent = new VBox(5);
            printContent.setPadding(new Insets(10));

            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd - MM - yyyy");
            Label dateLabel = new Label("Date: " + LocalDate.now().format(dateFormat));
            printContent.getChildren().add(dateLabel);
            
            Map<MenuItem, Integer> itemQuantities = new HashMap<>();
            for (MenuItem item : orderItems) {
                itemQuantities.put(item, itemQuantities.getOrDefault(item, 0) + 1);
            }

            // VBox to hold order items
            
            VBox content = new VBox(10);
            content.setPadding(new Insets(10));
            
            for (Map.Entry<MenuItem, Integer> entry : itemQuantities.entrySet()) {
                MenuItem item = entry.getKey();
                int quantity = entry.getValue();
                Label line = new Label(item.getName() + " | x" + quantity +
                        " | " + String.format("%.2f", item.getPrice()) +
                        " x " + quantity + " = Br" + item.getPrice() * quantity);
                line.setFont(geezFont);
                printContent.getChildren().add(line);
            }

            Label totalLabel = new Label("Total Price: " + String.format("%.2f", totalPrice) + " Br");
            totalLabel.setStyle("-fx-font-weight: bold;");
            printContent.getChildren().add(totalLabel);

            boolean printed = job.printPage(printContent);
            if (printed) {
                job.endJob();
            }
        }
    }
    
    /**
     * Places an order and updates the UI and database.
     *
     * @param taxed true if the order is taxed
     */
    private void placeOrder(boolean taxed) {
    	orderDAO.saveOrder(orderItems, taxed);
    	orderItems.clear();
    	orderTotalPrice = 0;
    	salesBox.getChildren().clear();
    	updateOrderLabels();
    	todaysOrders = orderDAO.getTodayOrders();
    	logAction((taxed ? ("Placed a new taxed order."): ("Placed a new untaxed order.")));
    	
    }
    
    /**
     * Updates the label displaying the total order price.
     */
    private void updateOrderLabels() {
    	orderTotalPriceLabel.setText("Total Order Price: Birr" + String.format("%.2f", orderTotalPrice));
    }
    
    /**
     * Entry point of the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
    	RestAutomation app = new RestAutomation();
        app.logAction("User started app");
    	launch(args);
        System.out.println("App Started!");
        app.logAction("User closed app!");
        
    }
}



