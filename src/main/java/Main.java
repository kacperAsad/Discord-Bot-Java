import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.entities.Activity;
import io.github.cdimascio.dotenv.Dotenv;


import javax.security.auth.login.LoginException;
import java.util.Map;

public class Main {

    static Dotenv environment = null;
    static boolean debugMode = false;

    static void loadEnv(){
        try {
            environment = Dotenv.load();
        } catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        if (environment != null && environment.get("test").equalsIgnoreCase("true")){
            debugMode = true;
        }

        JDA builder = JDABuilder.createDefault(args[0]).build();
        builder.getPresence().setActivity(Activity.watching("Budowa Trwa..."));
        builder.addEventListener(new CommandListener(environment));

    }
}
