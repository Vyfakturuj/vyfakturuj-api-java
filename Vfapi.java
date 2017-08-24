/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vfapi;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 *
 * @author vecernik
 */
public class Vfapi {

    private static final String BASEURL = "https://api.vyfakturuj.cz/2.0/"; // zakladni adresa API

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // nacteni existujiciho dokladu
        System.out.println(getInvoice(66627));
        // vytvoreni noveho dokladu
        System.out.println(createInvoice("{"
                + "\"customer_name\":\"Franta Novak\","
                + "\"items\":["
                + "{\"text\":\"rohlik\",\"unit_price\":\"1.5\",\"quantity\":10,\"unit\":\"ks\",\"vat_rate\":\"15\"},"
                + "{\"text\":\"ryze\",\"unit_price\":\"29.9\",\"quantity\":3,\"unit\":\"kg\",\"vat_rate\":\"21\"}"
                + "]}"));
    }

    public static String getInvoice(Integer vfiid) throws IOException {
        return httpGet("invoice/" + vfiid.toString());
    }

    public static String createInvoice(String json) throws IOException {
        return httpPost("invoice/", json);
    }

    private static String httpGet(String addr) throws IOException {
        HttpURLConnection connection = createConnection(addr);
        connection.setRequestMethod("GET");
        return connection2String(connection);
    }

    private static String httpPost(String addr, String json) throws IOException {
        HttpURLConnection connection = createConnection(addr);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        byte[] out = json.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        connection.setFixedLengthStreamingMode(length);
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.connect();
        try (OutputStream os = connection.getOutputStream()) {
            os.write(out);
        }

        return connection2String(connection);
    }

    private static HttpURLConnection createConnection(String addr) throws IOException {
        URL url = new URL(BASEURL + addr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        String userpass = LOGIN + ":" + KEY;
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(userpass.getBytes()));
        // following headers are for debugging/development purpose
//        connection.setRequestProperty("X-Authorization", "Basic " + Base64.getEncoder().encodeToString(userpass.getBytes())); // failsafe authorization in the case if usual header get cut off
//        connection.setRequestProperty("X-Debug", "1"); // enable debug return

        return connection;
    }

    private static String connection2String(HttpURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine + "\r\n");
        }
        in.close();
        return response.toString();
    }

    //private static byte[] convertPostData
}
