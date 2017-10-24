package framework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author wensong
 * 2010-9-4 下午03:34:58
 */
public class IpLocationUtils{

    public static String getWebIp() {
        try {

            URL url = new URL("http://city.ip138.com/ip2city.asp");

            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

            String s = "";

            StringBuffer sb = new StringBuffer("");

            String webContent = "";

            while ((s = br.readLine()) != null) {
                sb.append(s + "\r\n");

            }

            br.close();
            webContent = sb.toString();
            int start = webContent.indexOf("[")+1;
            int end = webContent.indexOf("]");
            webContent = webContent.substring(start,end);

            return webContent;

        } catch (Exception e) {
            e.printStackTrace();
            return "error";

        }
    }
}
