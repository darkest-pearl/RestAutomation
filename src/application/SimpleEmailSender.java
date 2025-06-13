package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

/**
 * Sends formatted email reports using Gmail SMTP with a fixed recipient list.
 *
 * @author Musab
 */
public class SimpleEmailSender {
	
	List<String> recipientList = new ArrayList<>();
	
	public SimpleEmailSender() {
		recipientList.add("sururmb@gmail.com");
		recipientList.add("munirasaid1979@gmail.com");
	}

    public void sendEmail(String reportContent) {
        final String username = "munirasaid1979@gmail.com";
        final String password = "fzeegecxscpmofiv"; // Use Gmail App Password

        // Your formatted report

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
          new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
          });
        for (String email : recipientList) {
        	try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(email)
                );
                message.setSubject("Formatted Sales Report");
                message.setText(reportContent); // plain text, not HTML

                Transport.send(message);
                System.out.println("Email sent successfully");

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        
    }
}
