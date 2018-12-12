package soa5.controller;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

@Controller
public class FileLoadController {
    private static String UPLOADED_FOLDER = "D:\\distr\\";

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @RequestMapping(value = "/load",method = RequestMethod.POST)
    @ResponseBody()
    public String singleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("subject") String subject,
    @RequestParam("textLine") String text,@RequestParam("user") String user, @RequestParam("password") String password ) {

            ArrayList<String> addressArray= new ArrayList<>();
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
                Files.write(path, bytes);
                FileReader fileReader = new FileReader(UPLOADED_FOLDER + file.getOriginalFilename());
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    addressArray.add(line);
                }
                    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
                    mailSender.setHost("smtp.gmail.com");
                    mailSender.setPort(587);
                    mailSender.setUsername(user);
                    mailSender.setPassword(password);
                for (int i = 0; i < addressArray.size(); i++) {
                    Properties props = mailSender.getJavaMailProperties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.host", "smtp.gmail.com");
                    props.put("mail.smtp.port", "587");
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(addressArray.get(i));
                    message.setSubject(subject);
                    message.setText(text);
                    mailSender.send(message);
                }
            }catch (Exception ex){
                return ex.getMessage();
            }
        return "success";
    }
}
