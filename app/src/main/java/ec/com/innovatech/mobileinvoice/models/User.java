package ec.com.innovatech.mobileinvoice.models;

public class User {
    String id;
    String name;
    String identifier;
    String email;
    String admin;

    public User(){
    }
    public User(String id, String name, String identifier, String email, String admin){
        this.id = id;
        this.name = name;
        this.identifier = identifier;
        this.email = email;
        this.admin = admin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}
