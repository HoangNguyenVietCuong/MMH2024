package entity;


import java.io.File;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import entity.Email;

public class EmailUtils {
	public static void send(Email email) 
			throws Exception {
		Properties prop = new Properties();
		
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");
		
		Session session = Session.getInstance(prop, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(email.getFrom(), email.getFromPassword());
			}
		});
		try {
			Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("your-email@example.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getTo()));
            message.setSubject(email.getSubject());

            // Email body with attachment
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText(email.getContent(), "utf-8", "html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);

            // Check if an attachment path is provided
            if (email.getAttachmentPath() != null && !email.getAttachmentPath().isEmpty()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(new File(email.getAttachmentPath()));
                multipart.addBodyPart(attachmentPart);
            }

            message.setContent(multipart);
			
			Transport.send(message);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
			throw e;
		}
	}
}
