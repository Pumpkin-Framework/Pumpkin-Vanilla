package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nl.jk_5.pumpkin.api.user.User;

@DatabaseTable(tableName = "users")
public class DatabaseUser implements User {

    @DatabaseField(generatedId = true, unique = true)
    private int id;

    @DatabaseField(width = 32, unique = true)
    private String username;

    @DatabaseField(width = 128, unique = true)
    private String email;

    @DatabaseField(width = 128, columnName = "fullname")
    private String fullName;

    @DatabaseField(width = 255, columnName = "passwordhash")
    private String passwordHash;

    @DatabaseField(width = 128, canBeNull = true)
    private String acttoken;

    public DatabaseUser() {
    }

    public DatabaseUser(String username, String email, String fullName, String passwordHash, String acttoken) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
        this.acttoken = acttoken;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getActtoken() {
        return acttoken;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setActtoken(String acttoken) {
        this.acttoken = acttoken;
    }
}
