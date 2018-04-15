import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;


public class Spinner {

    private HttpGet getSpin;
    private HttpGet getConfirmation;
    private CloseableHttpResponse spinResponse;
    private CloseableHttpResponse confirmationResponse;
    private CloseableHttpClient httpclient;
    private URI confirmationUri;
    private URI spinUri;
    private String sessid;
    private String wagerid;
    private String wonamount;

    public Spinner(CloseableHttpClient httpclient, String sessid) throws URISyntaxException {
        this.httpclient = httpclient;
        this.sessid = sessid;
    }

    public CloseableHttpResponse executeSpin() throws URISyntaxException, IOException {

        spinUri = new URIBuilder()
                .setScheme("https")
                .setHost("pff.yggdrasilgaming.com")
                .setPath("game.web/service")
                .setParameter("fn", "play")
                .setParameter("currency", "EUR")
                .setParameter("gameid", "7316")
                .setParameter("sessid", this.sessid)
                .setParameter("log", null)
                .setParameter("gameHistorySessionId", "session")
                .setParameter("gameHistoryTicketId", "ticket")
                .setParameter("amount", "1.25")
                .setParameter("lines", "1111111111111111111111111")
                .setParameter("coin", "0.05")
                .setParameter("clientinfo", "1510201247470900004")
                .setParameter("channelID", null)
                .setParameter("crid", "fe689ca3-689b-436e-9eab-a3e58725d4c7")
                .setParameter("csid", "c20b83da-8107-42cf-8c67-6dd99d95ec56")
                .build();

        getSpin = new HttpGet(spinUri);
        spinResponse = httpclient.execute(getSpin);

        if (spinResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return spinResponse;
        }
        return null;
    }

    public boolean isSpinWinnig() throws IOException {
//        reading a response
        BufferedReader br2 = new BufferedReader(
                new InputStreamReader((spinResponse.getEntity().getContent())));
        String output2 = br2.readLine();

//       parsing a response
        JSONObject ob = new JSONObject(output2);
        JSONObject data2 = ob.getJSONObject("data");
        JSONObject wager = data2.getJSONObject("wager");
        JSONArray bets = wager.getJSONArray("bets");
        JSONObject eventdata = bets.getJSONObject(0).getJSONObject("eventdata");
//        gettig wagerid to use it in confirmation reuest
        this.wagerid = wager.get("wagerid").toString();

//        getting accwa
        float accwa = Float.parseFloat(eventdata.get("accWa").toString());

//        close current response
        spinResponse.close();

//        checking spin result
        if (accwa > 0) {
            return true; //it is a winnig spin
        }

        return false; //it is not a winnig spin


    }

    public String getWonAmount() throws IOException, URISyntaxException {

        confirmationUri = new URIBuilder()
                .setScheme("https")
                .setHost("pff.yggdrasilgaming.com")
                .setPath("game.web/service")
                .setParameter("fn", "play")
                .setParameter("currency", "EUR")
                .setParameter("gameid", "7316")
                .setParameter("sessid", this.sessid)
                .setParameter("log", null)
                .setParameter("gameHistorySessionId", "session")
                .setParameter("gameHistoryTicketId", "ticket")
                .setParameter("amount", "0")
                .setParameter("wagerid", this.wagerid)
                .setParameter("betid", "1")
                .setParameter("step", "2")
                .setParameter("cmd", "C")
                .setParameter("channelID", null)
                .setParameter("crid", "fe689ca3-689b-436e-9eab-a3e58725d4c7")
                .setParameter("csid", "c20b83da-8107-42cf-8c67-6dd99d95ec56")
                .build();

//        creating and executing confirmation request
        getConfirmation = new HttpGet(confirmationUri);
        confirmationResponse = httpclient.execute(getConfirmation);

//        reading a response
        BufferedReader br2 = new BufferedReader(
                new InputStreamReader((confirmationResponse.getEntity().getContent())));
        String output = br2.readLine();

//        parsing a response, ROOT.data.wager.bets[0].wonamount
        JSONObject ob = new JSONObject(output);
        JSONObject data2 = ob.getJSONObject("data");
        JSONObject wager = data2.getJSONObject("wager");
        JSONArray bets = wager.getJSONArray("bets");
        wonamount = bets.getJSONObject(0).get("wonamount").toString();

//        checking response code
        if (confirmationResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return wonamount;
        }
        return null;
    }
}
