package com.inn.cafe.com.inn.cafe.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.UserTransaction;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmailUtils {

    private final JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text, List<String> list){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hientdhe163020@fpt.edu.vn");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        if(list.isEmpty() && list.size() > 0) {
            message.setCc(getCcArray(list));
        }
        emailSender.send(message);
    }
    public void forgetPasswordMail(String to, String subject, String password) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        helper.setFrom("hientdhe163020@fpt.edu.vn");
        helper.setTo(to);
        helper.setSubject(subject);

        String htmlMsg = "<p><b>Your Login details for Cafe Management System</b><br><b>Email: </b> " + to +
                " <br><b>Password: </b> " + password + "<br><a href=\"http://localhost:4200/\">Click here to login</a></p>";
        message.setContent(htmlMsg,"text/html");
        emailSender.send(message);
    }

    private String[] getCcArray(List<String> list){
        String[] cc = new String[list.size()];
        for(int i = 0; i<list.size();i++){
            cc[i] = list.get(i);
        }
        return cc;
    }
}
