module RestAutomation {
	requires javafx.controls;
	requires java.sql;
	requires jakarta.mail;
	requires jakarta.activation;
	requires org.joda.time;
	 // (If you are using javax.mail instead)

	
	
	opens application to javafx.graphics, javafx.fxml;
}
