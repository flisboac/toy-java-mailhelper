package flisboac.util.mailHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class Mail {

    private String defaultEncoding = Configurations.OutwardEncoding;
    private MailData from = new MailData(defaultEncoding);
    private MailData to = new MailData(defaultEncoding);
    private MailData subject = new MailData(defaultEncoding);
    private MailData message = new MailData(defaultEncoding);
    private List<MailData> cc = new ArrayList<MailData>();
    private List<MailData> attachments = new ArrayList<MailData>();

    public Mail() {
    }

    public Mail(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
        this.from = new MailData(defaultEncoding);
        this.to = new MailData(defaultEncoding);
        this.subject = new MailData(defaultEncoding);
        this.message = new MailData(defaultEncoding);
    }

    public Mail addCc(String email, String... args) {
        MailData data = MailData.field(email, args);
        if (args.length == 0) {
            data.setEncoding(defaultEncoding);
        }
        cc.add(data);
        return this;
    }
    
    public Mail addFrom(String email, String... args) {
        MailData data = MailData.field(email, args);
        if (args.length == 0) {
            data.setEncoding(defaultEncoding);
        }
        this.from = data;
        return this;
    }
    
    public Mail addTo(String email, String... args) {
        MailData data = MailData.field(email, args);
        if (args.length == 0) {
            data.setEncoding(defaultEncoding);
        }
        this.to = data;
        return this;
    }
    
    public Mail addSubject(String str, String... args) {
        MailData data = MailData.field(str, args);
        if (args.length == 0) {
            data.setEncoding(defaultEncoding);
        }
        this.subject = data;
        return this;
    }
    
    public Mail addTextMessage(String msg, String... args) {
        MailData data = MailData.textContent(msg, args);
        if (args.length == 0) {
            data.setEncoding(defaultEncoding);
        }
        this.message = data;
        return this;
    }
    
    public Mail addHtmlMessage(String msg, String... args) {
        MailData data = MailData.htmlContent(msg, args);
        if (args.length == 0) {
            data.setEncoding(defaultEncoding);
        }
        this.message = data;
        return this;
    }
    
    public Mail addTextFileAttachment(File file, String... args) {
        MailData data = MailData.textFile(file, args);
        if (args.length == 0) {
            data.setEncoding(defaultEncoding);
        }
        attachments.add(data);
        return this;
    }
    
    public Mail addBinaryFileAttachment(File file, String... args) {
        MailData data = MailData.binaryFile(file, args);
        if (args.length == 0) {
            data.setEncoding(defaultEncoding);
        }
        attachments.add(data);
        return this;
    }
    
    public Mail addTextAttachment(String content, String... args) {
        MailData data = MailData.textContent(content, args);
        if (args.length == 0) {
            data.setEncoding(defaultEncoding);
        }
        attachments.add(data);
        return this;
    }
    
    public Mail addHtmlAttachment(String content, String... args) {
        MailData data = MailData.htmlContent(content, args);
        if (args.length == 0) {
            data.setEncoding(defaultEncoding);
        }
        attachments.add(data);
        return this;
    }
    
    public void send() throws MailHelperException {
        Properties properties = new Properties();
        send(properties);
    }

    public void send(Properties properties) throws MailHelperException {
        Session session = Session.getDefaultInstance(properties);
        send(session);
    }

    public void send(Session session) throws MailHelperException {
        send(new MimeMessage(session));
    }

    public void send(Message msg) throws MailHelperException {
        prepare(msg);
        try {
            Transport.send(msg);
            
        } catch (MessagingException ex) {
            throw new MailHelperException("Error while sending e-mail..", ex);
        }
    }

    public void prepare(Message msg) throws MailHelperException {
        if (!isValid()) {
            throw new MailHelperException("Invalid e-mail data.");
        }
        
        try {
            msg.setFrom(new InternetAddress(this.from.generateFieldData()));

            if (this.to != null) {
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(this.to.generateFieldData()));
            }

            if (this.subject != null) {
                String data = this.subject.generateFieldData();
                msg.setSubject(data);
            }

            if (!cc.isEmpty()) {
                Address[] copied = new Address[cc.size()];
                int i = 0;
                for (MailData copy : cc) {
                    copied[i] = new InternetAddress(copy.generateFieldData());
                    i++;
                }
                msg.addRecipients(Message.RecipientType.CC, copied);
            }
            
            if (getEnvelopeType() == EnvelopeType.Multipart) {
                int index = 0;
                Multipart mps = new MimeMultipart();
                
                for (MailData attachment : attachments) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    String mimeType = attachment.generateMimeType();
                    String generatedEncoding = attachment.generateEncoding();
                    
                    if (attachment.isFile()) {
                        if (attachment.getEncoding() != null) {
                            byte[] attachmentData = attachment.generateBinaryData();
                            ByteArrayDataSource attachmentDataSource = new ByteArrayDataSource(attachmentData, mimeType);
                            attachmentPart.setDataHandler(new DataHandler(attachmentDataSource));
                            attachmentPart.setDisposition(Part.ATTACHMENT);
                            attachmentPart.setFileName(attachment.getFile().getName());
                            mps.addBodyPart(attachmentPart);
                            
                        } else {
                            FileDataSource fds = new FileDataSource(attachment.getFile());
                            attachmentPart.setDataHandler(new DataHandler(fds));
                            attachmentPart.setFileName(fds.getName());
                            mps.addBodyPart(attachmentPart, index++);
                        }

                    } else {
                        if (attachment.getDataType() == DataType.Binary) {
                            byte[] attachmentData = attachment.generateBinaryData();
                            String attachmentName=  attachment.getName() != null ? attachment.getName() : String.format("Attachment-%s", index);
                            ByteArrayDataSource fds = new ByteArrayDataSource(attachmentData, mimeType);
                            attachmentPart.setDataHandler(new DataHandler(fds));
                            attachmentPart.setFileName(attachmentName);
                            mps.addBodyPart(attachmentPart, index++);
                            
                        } else {
                            String attachmentContent = attachment.generateContentData();
                            //String mimeType = anexo.gerarMimeType();
                            File attachmentTempFile = File.createTempFile("Attachment-", ".txt");
                            Writer output = new OutputStreamWriter(new FileOutputStream(attachmentTempFile), generatedEncoding);
                            output.append(attachmentContent);
                            output.close();
                            FileDataSource fds = new FileDataSource(attachmentTempFile);
                            attachmentPart.setDataHandler(new DataHandler(fds));
                            attachmentPart.setFileName(attachmentTempFile.getName());
                            mps.addBodyPart(attachmentPart, index++);
                        }
                    }
                }

                MimeBodyPart textPart = new MimeBodyPart();
                String mimeType = message.generateMimeType();
                if (message.getDataType() == DataType.Binary) {
                    textPart.setContent(message.generateBinaryData(), mimeType);
                } else {
                    textPart.setContent(message.generateContentData(), mimeType);
                }
                mps.addBodyPart(textPart);
                msg.setContent(mps);
                
            } else {
                String mimeType = message.generateMimeType();
                if (message.getDataType() == DataType.Binary) {
                    msg.setContent(message.generateBinaryData(), mimeType);
                } else {
                    msg.setContent(message.generateContentData(), mimeType);
                }
            }
            
        } catch (MessagingException ex) {
            throw new MailHelperException("Message error while preparing message to be sent.", ex);
            
        } catch (UnsupportedEncodingException ex) {
            throw new MailHelperException("Encoding error while preparing message to be sent.", ex);
            
        } catch (FileNotFoundException ex) {
            throw new MailHelperException("File not found while preparing e-mail to be sent.", ex);
            
        } catch (IOException ex) {
            throw new MailHelperException("I/O Error while preparing e-mail to be sent.", ex);
        }
    }

    public boolean isValid() {
        boolean valid = true;
        valid &= from != null && from.isValid();
        valid &= to != null && to.isValid();
        valid &= subject == null || subject.isField();
        valid &= message != null && message.isValid();
        valid &= cc != null && attachments != null;
        if (valid) {
            for (MailData data : this.cc) {
                valid &= data.isField();
                if (!valid) {
                    break;
                }
            }
        }
        if (valid) {
            for (MailData data : this.attachments) {
                valid &= data.isValid();
                if (!valid) {
                    break;
                }
            }
        }
        return valid;
    }

    public EnvelopeType getEnvelopeType() {
        return attachments.isEmpty() ? EnvelopeType.Simple : EnvelopeType.Multipart;
    }

    public List<MailData> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MailData> attachments) {
        this.attachments = attachments;
    }

    public MailData getSubject() {
        return subject;
    }

    public void setSubject(MailData subject) {
        this.subject = subject;
    }

    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    public List<MailData> getCc() {
        return cc;
    }

    public void setCc(List<MailData> copies) {
        this.cc = copies;
    }

    public MailData getTo() {
        return to;
    }

    public void setTo(MailData to) {
        this.to = to;
    }

    public MailData getMessage() {
        return message;
    }

    public void setMessage(MailData message) {
        this.message = message;
    }

    public MailData getFrom() {
        return from;
    }

    public void setFrom(MailData from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "Email{" + "defaultEncoding=" + defaultEncoding + ", from=" + from + ", to=" + to + ", subject=" + subject + ", message=" + message + ", cc=" + cc + ", attachments=" + attachments + '}';
    }

}
