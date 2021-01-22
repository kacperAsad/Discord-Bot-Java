import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Main {

    public static void main(String[] args) throws Exception {
        new Main().run(args);


    }

    void run(String[] args) throws Exception {
        Settings.pushValue("debug", "true");
        Settings.pushValue("file_name", "data");
        Settings.readFromFile(Settings.getValue("file_name"));


        JDA builder = JDABuilder.createDefault(args[0]).build();
        builder.getPresence().setActivity(Activity.playing("Budowanie to całkiem ciekawa sprawa..."));

        DiscordLogger.init(builder);

        AdminListener adminListener = new AdminListener(builder);
        builder.addEventListener(adminListener);
        DiscordLogger.reload();
        DiscordLogger.log("Inicjalizacja adminListener");
    }
}
