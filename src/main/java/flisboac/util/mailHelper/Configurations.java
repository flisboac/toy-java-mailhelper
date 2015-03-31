/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package flisboac.util.mailHelper;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

/**
 *
 * @author flavio.costa
 */
public class Configurations {
    
    public static final String OutwardEncoding = null;
    public static final String DefaultRfc822Encoding = null;
            
    public static String getDefaultJvmEncoding() {
    	OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());
    	String enc = writer.getEncoding();
    	return enc;
    }
    
    public static String getDefaultRfc822Encoding() {
        return DefaultRfc822Encoding;
    }
}
