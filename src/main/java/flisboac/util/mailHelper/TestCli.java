/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package flisboac.util.mailHelper;

import java.io.File;
import java.util.Properties;
import javax.mail.Session;

/**
 *
 * @author flavio.costa
 */
public class TestCli {
    public static void main(String[] args) throws MailHelperException {
        Properties propriedadesSmtp = System.getProperties();
        propriedadesSmtp.put("mail.transport.protocol", "smtp");
        propriedadesSmtp.put("mail.smtp.host", "yoursmtpserver.com");
        propriedadesSmtp.put("mail.smtp.auth", "true");
//        propriedadesSmtp.put("mail.smtp.starttls.enable", "true");
//        propriedadesSmtp.put("mail.smtp.ssl.enable", "true");
        propriedadesSmtp.put("mail.smtp.port", "25");
        Session session = Session.getInstance(propriedadesSmtp, new SimpleAuthenticator("user", "passwd"));
        new Mail()
            .addFrom("flisboa.costa@gmail.com")
            .addTo("flisboa.costa@gmail.com")
            //.addCc("flisboa.costa@gmail.com")
            //.addCopia("flisboa.costa@gmail.com")
            //.addCopia("you.can.add@ad.infinitum")
            .addSubject("E-mail test with subject in UTF-8 ʘʤ", "UTF-8")
            //.addTextFileAttachment(new File("file_with_specific_encoding.txt"), "ISO-8859-1")
            //.addTextFileAttachment(new File("convert-file-contents-on-the-fly.txt"), "ISO-8859-1", "UTF-8")
            .addHtmlMessage("<h1>Hello, this is a test</h1><p>Body in ISO-8859-1.</p>", "ISO-8859-1")
            //.addAnexoTexto("You can also add arbitrary text as attachments!", "UTF-8")
            //.addAnexoArquivoTexto(new File("error.log"))
            //.addTextFileAttachment(new File("package.zip"))
            //.addBinaryFileAttachment(new File("image.jpg"))
            .send(session)
            ;
    }
}
