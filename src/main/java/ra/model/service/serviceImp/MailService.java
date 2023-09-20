package ra.model.service.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private JavaMailSender javaMailSender;

/*    public void sendEmail(String to, String subject, String html) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.addCc("dglong1994@gmail.com");
            mimeMessageHelper.setSubject(subject);

            mimeMessageHelper.setText(html, true);
            for (MultipartFile f: file
            ) {

                ByteArrayResource attachment = new ByteArrayResource(f.getBytes());
                mimeMessageHelper.addAttachment(f.getOriginalFilename(), attachment);
            }

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        javaMailSender.send(mimeMessage);
    }*/
   public void sendMail(String to,String subject, String text){
       SimpleMailMessage message = new SimpleMailMessage();
       message.setTo(to);
       message.setSubject(subject);
       message.setText(text);
       javaMailSender.send(message);
   }
}