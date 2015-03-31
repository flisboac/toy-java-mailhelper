package flisboac.util.mailHelper;

import java.io.*;
import javax.mail.internet.MimeUtility;

public class MailData {
    
    private DataType dataType = DataType.Text;
    private String encoding = null;
    private String originalEncoding = null;
    private String mimeType = null;
    private String data = null;
    private String name = null;
    private File file = null;

    public MailData() {}

    public MailData(String encoding) {
        this.encoding = encoding;
    }

    public static MailData textFile(File file, String... args) {
        MailData ret = new MailData();
        ret.setFile(file);
        ret.setDataType(DataType.Text);
        ret.setMimeType("text/plain");
        if (args.length > 0) {
            ret.setEncoding(args[0]);
        }
        if (args.length > 1) {
            ret.setOriginalEncoding(args[1]);
        }
        if (args.length > 2) {
            ret.setMimeType(args[2]);
        }
        return ret;
    }
    
    public static MailData binaryFile(File file, String... args) {
        MailData ret = new MailData();
        ret.setFile(file);
        ret.setEncoding("");
        ret.setDataType(DataType.Binary);
        ret.setMimeType("application/octet-stream");
        if (args.length > 0) {
            ret.setEncoding(args[0]);
        }
        if (args.length > 1) {
            ret.setMimeType(args[1]);
        }
        return ret;
    }
    
    public static MailData field(String value, String... args) {
        MailData ret = new MailData();
        ret.setData(value);
        ret.setEncoding("");
        ret.setDataType(DataType.Text);
        ret.setMimeType(null);
        if (args.length > 0) {
            ret.setEncoding(args[0]);
        }
        if (args.length > 1) {
            ret.setMimeType(args[1]);
        }
        return ret;
    }
    
    public static MailData textContent(String value, String... args) {
        MailData ret = new MailData();
        ret.setData(value);
        ret.setDataType(DataType.Text);
        ret.setEncoding("");
        ret.setMimeType("text/plain");
        if (args.length > 0) {
            ret.setEncoding(args[0]);
        }
        if (args.length > 1) {
            ret.setMimeType(args[1]);
        }
        return ret;
    }
    
    public static MailData htmlContent(String value, String... args) {
        MailData ret = new MailData();
        ret.setData(value);
        ret.setDataType(DataType.Text);
        ret.setMimeType("text/html");
        if (args.length > 0) {
            ret.setEncoding(args[0]);
        }
        if (args.length > 1) {
            ret.setMimeType(args[1]);
        }
        return ret;
    }
    
    public boolean isFile() {
        return file != null;
    }
    
    public boolean isField() {
        return data != null;
    }
    
    public boolean isValid() {
        return isFile() || isField();
    }
    
    public String generateMimeType() {
        return mimeType != null ? (mimeType + (encoding != null ? "; charset=" + encoding : "")) : "application/octet-stream";
    }

    public String generateEncoding() {
        return encoding == null ? Configurations.getDefaultJvmEncoding() : encoding.isEmpty() ? Configurations.OutwardEncoding : encoding;
    }
    
    public String generateFieldData() throws UnsupportedEncodingException, FileNotFoundException, IOException {
        if (data != null) {
            if (encoding == null) {
                return MimeUtility.encodeText(data);
            } else if (encoding.isEmpty()) {
                return MimeUtility.encodeText(data, Configurations.getDefaultJvmEncoding(), Configurations.getDefaultRfc822Encoding());
            } else {
                return MimeUtility.encodeText(data, encoding, null);
            }
        } else if (file != null) {
            String content;
            String readEncoding = originalEncoding == null ? encoding : originalEncoding;
            if (readEncoding == null) {
                content = Util.readTextFile(file);
            } else if (readEncoding.isEmpty()) {
                content = Util.readTextFile(file, Configurations.getDefaultJvmEncoding());
            } else {
                content = Util.readTextFile(file, readEncoding);
            }
            if (encoding == null) {
                return MimeUtility.encodeText(content);
            } else if (encoding.isEmpty()) {
                return MimeUtility.encodeText(content, Configurations.getDefaultJvmEncoding(), Configurations.getDefaultRfc822Encoding());
            } else {
                return MimeUtility.encodeText(content, encoding, null);
            }
        }
        return null;
    }

    public String generateContentData() throws FileNotFoundException, IOException {
        if (this.data != null) {
            return this.data;
        }
        if (file != null) {
            String content;
            String readEncoding = originalEncoding == null ? encoding : originalEncoding;
            if (readEncoding == null) {
                content = Util.readTextFile(file);
            } else if (readEncoding.isEmpty()) {
                content = Util.readTextFile(file, Configurations.getDefaultJvmEncoding());
            } else {
                content = Util.readTextFile(file, readEncoding);
            }
            return content;
        }
        return null;
    }
    
    public byte[] generateBinaryData() throws FileNotFoundException, IOException {
        byte[] array = null;
        String outputEncoding = generateEncoding();
        if (this.data != null) {
            array = this.data.getBytes(outputEncoding);
        } else if (this.file != null) {
            array = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(array);
            fis.close();
            if (this.originalEncoding != null) {
                array = new String(array, this.originalEncoding).getBytes(outputEncoding);
            }
        }
        return array;
    }
    
    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getOriginalEncoding() {
        return originalEncoding;
    }

    public void setOriginalEncoding(String originalEncoding) {
        this.originalEncoding = originalEncoding;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "EmailData{" + "dataType=" + dataType + ", encoding=" + encoding + ", originalEncoding=" + originalEncoding + ", mimeType=" + mimeType + ", data=" + data + ", name=" + name + ", file=" + file + '}';
    }
    
}
