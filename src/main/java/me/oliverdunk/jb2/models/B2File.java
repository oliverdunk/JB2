package me.oliverdunk.jb2.models;

/**
 * Represents a file stored in the B2 cloud.
 */
public class B2File {

    private String name, contentType, ID;
    private long size, uploadTimestamp;

    /**
     * Constructs a B2File.
     *
     * @param name The fileName which is stored on the B2 cloud
     * @param contentType Automatically picked content type in MIME format
     * @param ID Unique file identifier
     * @param size Number of bytes in the file
     * @param uploadTimestamp UTC based epoch time when the file was uploaded
     */
    public B2File(String name, String contentType, String ID, long size, long uploadTimestamp){
        this.name = name;
        this.contentType = contentType;
        this.ID = ID;
        this.size = size;
        this.uploadTimestamp = uploadTimestamp;
    }

    /**
     * Gets the name.
     *
     * @return Returns the fileName stored on the B2 cloud
     */
    public String getName(){
        return name;
    }

    /**
     * Gets the content type.
     *
     * @return Automatically picked content type in MIME format
     */
    public String getContentType(){
        return contentType;
    }

    /**
     * Gets the ID.
     *
     * @return Unique file identifier
     */
    public String getID(){
        return ID;
    }

    /**
     * Gets the file size.
     *
     * @return Number of bytes in the file
     */
    public long getSize(){
        return size;
    }

    /**
     * Gets the time since upload, using milliseconds since the epoch (January 1st, 1970 UTC).
     *
     * @return Time when the file was uploaded
     */
    public long getUploadTimestamp(){
        return uploadTimestamp;
    }

}
