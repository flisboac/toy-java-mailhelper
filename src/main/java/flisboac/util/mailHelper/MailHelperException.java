package flisboac.util.mailHelper;

public class MailHelperException extends Exception {
    
    public MailHelperException() {
        
    }
    
    public MailHelperException(String msg) {
        super(msg);
    }
    
    public MailHelperException(Throwable e) {
        super(e);
    }
    
    public MailHelperException(String msg, Throwable e) {
        super(msg, e);
    }
}
