import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.java2d.pipe.SpanClipRenderer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class GameTester {

    private static int count = 0;
    private static Boolean prizeWinningSpin;
    private static String wonamount;
    private static String sessid;
    private static CloseableHttpClient httpclient;
    private static Authentication authenticator;
    private static Spinner spinner;

    public static void main(String[] args) throws IOException, URISyntaxException {
//      client initialization
        httpclient =  HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig
                        .custom()
                        .setCookieSpec(CookieSpecs.STANDARD)
                        .build())
                .build();

//      getting ssid to authenticate the session
        authenticator = new Authentication(httpclient);
        sessid = authenticator.getSesid();

//      requesting until prize-winning spin is achieved
        prizeWinningSpin = false;
        while (!prizeWinningSpin){
            spinner = new Spinner(httpclient,sessid);
            spinner.executeSpin();
            prizeWinningSpin = spinner.isSpinWinnig();
            if (prizeWinningSpin){
                wonamount = spinner.getWonAmount();
            }
            count++;
        }

        System.out.println("Number of spin command: "+ count);
        System.out.println("The wonamount is: " + wonamount);

    }
}
