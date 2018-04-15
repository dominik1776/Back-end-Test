import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class Authentication {

    private CloseableHttpClient httpclient;
    private URI url;

    public Authentication(CloseableHttpClient httpclient) {
        this.httpclient =  httpclient;
    }

    public String getSesid() throws IOException, URISyntaxException {
        String sesid = null;

        url = new URIBuilder().
                setScheme("https")
                .setHost("pff.yggdrasilgaming.com")
                .setPath("game.web/service")
                .setParameter("fn", "authenticate")
                .setParameter("org", "Demo")
                .setParameter("lang", "en")
                .setParameter("gameid", "7316")
                .setParameter("channel", "mobile")
                .setParameter("currency", "EUR")
                .setParameter("userName", null)
                .setParameter("crid", "fe689ca3-689b-436e-9eab-a3e58725d4c7")
                .setParameter("csid", "c20b83da-8107-42cf-8c67-6dd99d95ec56")
                .build();

//        creating and executing confirmation request
        HttpGet getAuthentication = new HttpGet(url);
        CloseableHttpResponse authResponse = httpclient.execute(getAuthentication);

//        reading a response
        BufferedReader br = new BufferedReader(
                new InputStreamReader((authResponse.getEntity().getContent())));
        String output = br.readLine();

//        parsing a response, ROOT.data.sessid
        JSONObject o = new JSONObject(output);
        JSONObject data = (JSONObject) o.get("data");
        sesid = (String) data.get("sessid");

//        close current response
        authResponse.close();

//        checking response code
        if (authResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return sesid;
        }

        return null;
    }
}
