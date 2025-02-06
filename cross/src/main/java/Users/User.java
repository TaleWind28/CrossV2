package Users;
public class User {
    private String username;
    private String password;
    private boolean isLogged;
    
    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.isLogged = false;
    }

    public void setLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    
    public String getUsername() {
        return username;
    }

    

    public boolean getLogged(){
        return this.isLogged;
    }

    @Override
    public String toString() {
        return "User{" +
           "username='" + username + '\'' +
           ", password='" + password + '\'' +
           ", isLogged=" + isLogged +
           '}';
    }

}
