package Users.Commands;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import Communication.Message;
import JsonMemories.JsonAccessedData;
import JsonMemories.Userbook;
import ServerTasks.GenericTask;
import Users.Commands.CommandBehaviours.CommandBehaviour;

public class Credentials implements UserCommand{
    private String type = "credentials";
    private String accessType;
    private String username;
    private String password = new String();
    private String newPassword = new String();
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private Userbook userbook;
    private CommandBehaviour myBehaviour;
    private String user;
    private int unicode;

    public Credentials(String accessType,String user){
        this.username = user;
        this.accessType = accessType;
    }

    public Credentials(String accessType,String user, Userbook userbook,CommandBehaviour behaviour){
        this.username = user;
        this.accessType = accessType;
        this.userbook = userbook;
        this.myBehaviour = behaviour;
        this.unicode = behaviour.getUnicode();
        //setPayload({user,accessType});
    }

    public Credentials(String accessType,String user, String passwd, Userbook userbook, CommandBehaviour behaviour){
        this.username = user;
        this.password = passwd;
        this.accessType = accessType;
        this.userbook = userbook;
        this.myBehaviour = behaviour;
        this.unicode = behaviour.getUnicode();
    }

    public Credentials(String accessType,String user, String passwd, String newPasswd, Userbook userbook, CommandBehaviour behaviour){
        this.accessType = accessType;
        this.username = user;
        this.password = passwd;
        this.newPassword = newPasswd;
        this.accessType = accessType;
        this.userbook = userbook;
        this.myBehaviour = behaviour;
        this.unicode = behaviour.getUnicode();
    }

    @Override
    public Message execute(GenericTask context) {
        setUser(context.onlineUser);
        //this.user = context.onlineUser;
        System.out.println(this.user);
        return this.myBehaviour.executeOrder(this,context);
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getNewPassword() {
        return newPassword;
    }

    public String getPassword() {
        return password;
    }
    
    @Override
    public String[] getInfo(){
        String[] info = new String[4];
        info[0] = this.accessType;
        info[1] = this.username;
        info[2] = this.password;
        info[3] = this.newPassword;
        return info;
    }

    public String toString(){
        return "Credentials{" +
               "credType='" + this.type + '\'' +
               ", username='" + this.username+ '\'' +
               ", passwd=" + this.password +
               ", newPasswd=" + this.newPassword +
               "," +
               '}';
    }

    @Override
    public String getType() {
        return this.type;
    }

	@Override
	public JsonAccessedData getJsonAccessedData() {
        return this.userbook;
    }

    public int getUnicode() {
        return this.unicode;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // @Override
    // public int validateCommand(UserCommand cmd) {
    //     if (this.username.equals("none"))return 400;
    //     else return this.getUnicode();
    // }
    
    public BCryptPasswordEncoder getEncoder() {
        return encoder;
    }

    @Override
    public void setUser(String username) {
        this.user = username;
    }
}
