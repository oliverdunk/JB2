package me.oliverdunk.jb2.models;

/**
 * Represents a file stored in the B2 cloud.
 */
public class B2File {

    private String name, contentType, ID;

    /**
     * Constructs a B2File.
     *
     * @param name The fileName which is stored on the B2 cloud
     * @param contentType Automatically picked content type in MIME format
     * @param ID Unique file identifier
     */
    public B2File(String name, String contentType, String ID){
        this.name = name;
        this.contentType = contentType;
        this.ID = ID;
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

}
