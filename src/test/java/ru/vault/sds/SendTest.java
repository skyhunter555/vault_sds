package ru.syntez.vault.sds;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@Ignore
@SpringBootTest
public class SendTest {

    @Test
    public void sendToSlack() throws Exception {
        String query = "https://hooks.slack.com/services/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";
        String json = "{\"text\":\"Hello, World!\"}";
        URL url = new URL(query);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        OutputStream os = conn.getOutputStream();
        os.write(json.getBytes("UTF-8"));
        InputStream inputStream = new BufferedInputStream(conn.getInputStream());
        String result = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
        os.close();
        conn.disconnect();
    }

}
