package me.oliverdunk.jb2.models;

public class B2Upload {

    private B2Bucket bucket;
    private String uploadURL, authorizationToken;

    /**
     * Constructs a B2Upload instance.
     * @param bucket The bucket which the upload will take place in.
     * @param uploadURL The URL which should be used for uploading the files.
     * @param authorizationToken A token which will be used to authenticate the upload.
     */
    public B2Upload(B2Bucket bucket, String uploadURL, String authorizationToken){
        this.bucket = bucket;
        this.uploadURL = uploadURL;
        this.authorizationToken = authorizationToken;
    }

    /**
     * Gets the B2Bucket which the upload will take place in.
     * @return An instance of B2Bucket representing the upload destination.
     */
    public B2Bucket getBucket(){
        return bucket;
    }

    /**
     * Get the uploadURL.
     * @return Gets the URL which should be used for uploading the files.
     */
    public String getUploadURL(){
        return uploadURL;
    }

    /**
     * Gets the authorizationToken.
     * @return A token which will be used to authenticate the upload.
     */
    public String getAuthorizationToken(){
        return authorizationToken;
    }

}