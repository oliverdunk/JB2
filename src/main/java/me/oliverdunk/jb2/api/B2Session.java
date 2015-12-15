package me.oliverdunk.jb2.api;

/**
 * Represents a session obtained using the b2_authorize_account method.
 */
public class B2Session {

    private String authorizationToken, accountID, APIURL, downloadURL;

    /**
     * Constructs a B2Session using values which are returned by the B2 API.
     * @param authorizationToken Represents the authorizationToken returned by B2.
     * @param accountID Represents the accountID returned by B2.
     * @param APIURL Represents the api URI which should be used for further API calls.
     * @param downloadURL Represents the download URL which should be used for retrieving files.
     */
    public B2Session(String authorizationToken, String accountID, String APIURL, String downloadURL){
        this.authorizationToken = authorizationToken;
        this.accountID = accountID;
        this.APIURL = APIURL;
        this.downloadURL = downloadURL;
    }

    /**
     * Returns the authorization token for this session.
     * @return Authorization used for the HTTP Authorization header in future requests.
     */
    public String getAuthToken(){
        return authorizationToken;
    }

    /**
     * Returns the account ID which owns this session.
     * @return Account ID representing the owner of the session.
     */
    public String getAccountID(){
        return accountID;
    }

    /**
     * Returns the APIURL which should be used for further requests.
     * @return APIURL which should be used for futher requests.
     */
    public String getAPIURL(){
        return APIURL;
    }

    /**
     * Returns the downloadURL for this session.
     * @return downloadURL which should be used when retrieving files.
     */
    public String getDownloadURL(){
        return downloadURL;
    }

}
