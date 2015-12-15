package me.oliverdunk.jb2;

import me.oliverdunk.jb2.exceptions.B2APIException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class B2API {

    //User-Agent field sent with all HTTP requests.
    private static final String USER_AGENT = "JB2/1.0";
    private static final String API_URL = "https://api.backblaze.com/b2api/v1";

    /**
     * Sends a request to the B2 API using the specified headers.
     * @param method A string representing the B2 method which should be called.
     * @param authorization A string representing the Authorization header.
     * @param body A HashMap of key-value pairs which should be sent .
     * @return A parsed JSONObject from the B2 server.
     * @throws B2APIException Thrown to represent an error returned by the B2 API.
     */
    private static JSONObject call(String method, String authorization, JSONObject body) throws B2APIException {
        try {
            URL url = new URL(API_URL + "/" + method);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", USER_AGENT);

            byte[] authorizationBytes = authorization.getBytes(StandardCharsets.UTF_8);
            String encodedAuthorization = Base64.getEncoder().encodeToString(authorizationBytes);
            connection.setRequestProperty("Authorization", "Basic " + encodedAuthorization);

            connection.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(body.toString());
            outputStream.flush();
            outputStream.close();

            JSONObject requestResult;

            if(connection.getResponseCode() < 400){
                InputStream inputStream =  connection.getInputStream();
                requestResult = inputToJSON(inputStream);
            }else{
                InputStream errorStream =  connection.getErrorStream();
                requestResult = inputToJSON(errorStream);

                B2APIException exception = new B2APIException(requestResult.getString("message"));
                exception.setStatusCode(requestResult.getInt("status"));
                exception.setIdentifier(requestResult.getString("code"));
                throw exception;
            }

            return requestResult;

        }catch(Exception ex){
            if(ex instanceof B2APIException) throw (B2APIException) ex;
            return new JSONObject();
        }
    }

    /**
     * Reads the data from an InputStream and returns the string parsed into a JSONObject.
     * @param inputStream InputStream which will be read to retrieve the data.
     * @return JSONObject representing the data inside the InputStream.
     * @throws IOException Thrown if an error occurs while reading data from the InputStream.
     */
    private static JSONObject inputToJSON(InputStream inputStream) throws IOException {
        StringBuilder JSON = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        while(reader.ready()) JSON.append(reader.readLine());
        return new JSONObject(JSON.toString().trim());
    }

    /**
     * Authorizes an account with the B2 API, using the b2_authorize_account method.
     * @param accountID Your B2 API account ID.
     * @param applicationKey Your B2 API application key.
     */
    public static void authorizeAccount(String accountID, String applicationKey){
        System.out.println(call("b2_authorize_account", accountID + ":" + applicationKey, new JSONObject()).toString());
    }

}