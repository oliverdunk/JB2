package me.oliverdunk.jb2.api;

import me.oliverdunk.jb2.exceptions.B2APIException;
import me.oliverdunk.jb2.models.*;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Class used for accessing the B2 API using an HTTP connection.
 */
public class B2API {

    //User-Agent field sent with all HTTP requests.
    private static final String USER_AGENT = "JB2/1.0";
    private static final String API_URL = "https://api.backblaze.com";

    private static JSONObject call(String URL, String method, String authorization, JSONObject body) throws B2APIException {
        try {
            URL url = new URL(URL + "/b2api/v1/" + method);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Authorization", authorization);

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

            connection.disconnect();
            return requestResult;
        }catch(Exception ex){
            if(ex instanceof B2APIException) throw (B2APIException) ex;
            return new JSONObject();
        }
    }

    private static JSONObject uploadFile(File file, String name, B2UploadRequest upload) throws B2APIException {
        try {
            URL url = new URL(upload.getUploadURL());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Authorization", upload.getAuthorizationToken());
            connection.setRequestProperty("Content-Type", "b2/x-auto");
            connection.setRequestProperty("X-Bz-File-Name", name);
            connection.setRequestProperty("X-Bz-Content-Sha1", getFileHash(file));

            connection.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(Files.readAllBytes(Paths.get(file.getPath())));
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

            connection.disconnect();
            return requestResult;
        }catch(Exception ex){
            if(ex instanceof B2APIException) throw (B2APIException) ex;
            return new JSONObject();
        }
    }

    private static void downloadFile(String URL, String authorization, B2File file, File destination) throws B2APIException {
        try {
            URL url = new URL(URL + "/b2api/v1/b2_download_file_by_id");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Authorization", authorization);

            connection.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(new JSONObject().put("fileId", file.getID()).toString());
            outputStream.flush();
            outputStream.close();

            if(connection.getResponseCode() < 400){
                InputStream inputStream =  connection.getInputStream();
                OutputStream fileOutputStream = new FileOutputStream(destination);

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, read);
                }
                fileOutputStream.close();
                connection.disconnect();
            }else{
                InputStream errorStream =  connection.getErrorStream();
                JSONObject requestResult = inputToJSON(errorStream);

                B2APIException exception = new B2APIException(requestResult.getString("message"));
                exception.setStatusCode(requestResult.getInt("status"));
                exception.setIdentifier(requestResult.getString("code"));
                throw exception;
            }

        }catch(Exception ex) {
            if (ex instanceof B2APIException) throw (B2APIException) ex;
        }
    }

    /**
     * Reads the data from an InputStream and returns the string parsed into a JSONObject.
     *
     * @param inputStream InputStream which will be read to retrieve the data
     * @return JSONObject representing the data inside the InputStream
     * @throws IOException Thrown if an error occurs while reading data from the InputStream
     */
    private static JSONObject inputToJSON(InputStream inputStream) throws IOException {
        StringBuilder JSON = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        while(reader.ready()) JSON.append(reader.readLine());
        return new JSONObject(JSON.toString().trim());
    }

    /**
     * Authorizes an account with the B2 API, using the b2_authorize_account method.
     *
     * @param accountID Your B2 API account ID
     * @param applicationKey Your B2 API application key
     * @return A B2Session instance representing the session created by this request
     */
    public static B2Session authorizeAccount(String accountID, String applicationKey){
        String encodedAuth = encodeAuthorization(accountID + ":" + applicationKey);
        JSONObject requestResult = call(API_URL, "b2_authorize_account", encodedAuth, new JSONObject());

        String authorizationToken = requestResult.getString("authorizationToken");
        String apiURL = requestResult.getString("apiUrl");
        String downloadURL = requestResult.getString("downloadUrl");
        return new B2Session(authorizationToken, accountID, apiURL, downloadURL);
    }

    /**
     * Encodes an authentication input into Base64, and formats for the Authorization field.
     * Used for the authorizeAccount, which does not have the usual authorization token.
     *
     * @param input Account ID and application key in the format accountID:applicationKey
     * @return Encoded Base64 String, with the Basic prefix
     */
    private static String encodeAuthorization(String input){
        byte[] authorizationBytes = input.getBytes(StandardCharsets.UTF_8);
        String encodedAuthorization = Base64.getEncoder().encodeToString(authorizationBytes);
        return "Basic " + encodedAuthorization;
    }

    /**
     * Gets the SHA1 hash of a file.
     *
     * @param file The file for which the hash should be generated
     * @return The SHA1 hash of the specified file
     */
    private static String getFileHash(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        FileInputStream fis = new FileInputStream(file);
        byte[] dataBytes = new byte[1024];
        int nread = 0;

        while ((nread = fis.read(dataBytes)) != -1) md.update(dataBytes, 0, nread);

        byte[] mdbytes = md.digest();
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mdbytes.length; i++) sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));

        return sb.toString();
    }

    /**
     * Creates a new B2Bucket using the API.
     *
     * @param session Session authenticated with the API, which will be used as Authorization
     * @param bucketName A name for the bucket, which is at least six characters and does not start with "b2-"
     * @param bucketType The privacy level of the bucket which is being created
     * @return String which is the ID of the bucket
     */
    public static B2Bucket createBucket(B2Session session, String bucketName, BucketType bucketType){
        JSONObject parameters = new JSONObject();
        parameters.put("accountId", session.getAccountID());
        parameters.put("bucketName", bucketName);
        parameters.put("bucketType", bucketType.getIdentifier());
        JSONObject requestResult = call(session.getAPIURL(), "b2_create_bucket", session.getAuthToken(), parameters);
        return new B2Bucket(bucketName, requestResult.getString("bucketId"), bucketType);
    }

    /**
     * Deletes a B2Bucket using the API, but only if the bucket contains no versions of any files.
     *
     * @param session Session authenticated with the API, which will be used as Authorization
     * @param bucket The B2Bucket instance which should be deleted
     */
    public static void deleteBucket(B2Session session, B2Bucket bucket){
        JSONObject parameters = new JSONObject();
        parameters.put("accountId", session.getAccountID());
        parameters.put("bucketId", bucket.getID());
        call(session.getAPIURL(), "b2_delete_bucket", session.getAuthToken(), parameters);
    }

    /**
     * Lists all buckets using the API, but only if the bucket contains no versions of any files.
     *
     * @param session Session authenticated with the API, which will be used as Authorization
     */
    public static List<B2Bucket> listBuckets(B2Session session){
        JSONObject parameters = new JSONObject();
        parameters.put("accountId", session.getAccountID());
        JSONObject response = call(session.getAPIURL(), "b2_list_buckets", session.getAuthToken(), parameters);

        List<B2Bucket> buckets = new ArrayList<B2Bucket>();
        for(int i = 0; i < response.getJSONArray("buckets").length(); i++){
            JSONObject bucket = response.getJSONArray("buckets").getJSONObject(i);
            buckets.add(new B2Bucket(
                    bucket.getString("bucketName"),
                    bucket.getString("bucketId"),
                    BucketType.getByIdentifier(bucket.getString("bucketType")))
            );
        }
        return buckets;
    }

    /**
     * Syncs a B2Bucket instance with the API.
     *
     * @param session Session authenticated with the API, which will be used as Authorization
     * @param bucket The B2Bucket instance which should be synced
     */
    public static void updateBucket(B2Session session, B2Bucket bucket){
        JSONObject parameters = new JSONObject();
        parameters.put("accountId", session.getAccountID());
        parameters.put("bucketId", bucket.getID());
        parameters.put("bucketType", bucket.getType().getIdentifier());
        call(session.getAPIURL(), "b2_update_bucket", session.getAuthToken(), parameters);
    }

    /**
     * Prepares the API for a file upload within a given bucket.
     *
     * @param session Session authenticated with the API, which will be used as Authorization
     * @param bucket The B2Bucket where the upload will take place
     * @return A B2UploadRequest instance representing where a file should be uploaded
     */
    public static B2UploadRequest getUploadURL(B2Session session, B2Bucket bucket){
        JSONObject parameters = new JSONObject();
        parameters.put("bucketId", bucket.getID());
        JSONObject result = call(session.getAPIURL(), "b2_get_upload_url", session.getAuthToken(), parameters);
        return new B2UploadRequest(bucket, result.getString("uploadUrl"), result.getString("authorizationToken"));
    }

    /**
     * Uploads a file to the API completing the upload request.
     *
     * @param upload An upload request created with the getUploadURL method
     * @param file The file which should be uploaded
     * @param name The name which should identify the file
     * @return A B2File instance
     */
    public static B2File uploadFile(B2UploadRequest upload, File file, String name){
        JSONObject result = uploadFile(file, name, upload);
        return new B2File(name, result.getString("contentType"), result.getString("fileId"));
    }

    /**
     * Downloads a file from the API.
     *
     * @param session Session authenticated with the API, which will be used as Authorization
     * @param file The file which should be downloaded
     * @param destination Where the file should be downloaded to
     */
    public static void downloadFile(B2Session session, B2File file, File destination){
        downloadFile(session.getDownloadURL(), session.getAuthToken(), file, destination);
    }

}