package protocol.http;

import framework.Invocation;
import org.apache.commons.io.IOUtils;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {

    public String send(String hostname, Integer port, Invocation invocation) {
        ObjectOutputStream oos = null;
        try {
            URL url = new URL("http", hostname, port, "/");
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            oos = new ObjectOutputStream(outputStream);

            oos.writeObject(invocation);
            oos.flush();

            String result = IOUtils.toString(httpURLConnection.getInputStream());
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                }catch (Exception e) {

                }
            }
        }
        return null;

    }
}
