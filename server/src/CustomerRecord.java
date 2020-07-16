/*
 * CustomerRecord class
 * 
 * CosmosDB use
 * 
 * This class is only to work with the database documents, do not modify
 */

public class CustomerRecord {

    private String id;
    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}