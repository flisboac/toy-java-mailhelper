package flisboac.util.mailHelper;

import java.io.*;

public class Util {
    
    public static String readTextFile(File file) throws FileNotFoundException, IOException {
        return readTextFile(file, null);
    }
    
    public static String readTextFile(File file, String encoding) throws FileNotFoundException, IOException {
        BufferedReader br;
        if (encoding == null) {
            br = new BufferedReader(new FileReader(file));
        } else if (!encoding.isEmpty()) {
            br = new BufferedReader(new FileReader(file));
        } else {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
        }
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(String.format("%s%n", line));
                line = br.readLine();
            }
            return sb.toString();
            
        } finally {
            br.close();
        }
    }
}
