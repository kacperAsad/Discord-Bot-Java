import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


/**
    Admin Listener służy do wykonywania komend od od administracji, nasłuchuje on i wykonuje polecenia osób mających uprawnienia administratora

 */
public class AdminListener extends ListenerAdapter {


    JDA jda = null;

    public AdminListener(JDA jda){
        this.jda = jda;
    }


    /**
     * Analizowanie wiadomości przychodzących
     * @param event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User author = event.getAuthor();
        String content = event.getMessage().getContentRaw();
        TextChannel channel = event.getTextChannel();


        // Sprawdzanie, czy użytkownik nie jest botem
        if (author.isBot()){
            return;
        }
        // Sprawdzanie, czy ktoś napisał na dm
        if (event.isFromType(ChannelType.PRIVATE)){
            if (content.equalsIgnoreCase("?help")){
                channel.sendMessage("Obecnie pomoc nie jest wspierana dla wiadomości dm").queue();
            }else{
                channel.sendMessage("Obecnie wiadomości dm nie są ukończone").queue();
            }
            return;
        }
        Member member = event.getMember();

        // Sprawdzanie, czy użytkownik ma nadaną permissie administratora
        if (member.getPermissions().contains(Permission.ADMINISTRATOR)){
            if (content.equalsIgnoreCase("?log channel")){

                // Sprawdza, czy kanał był już ustawiony, jeśli nie, pierwsza opcja, jeśli tak, druga
                if (Settings.getValue("logger-channel").equalsIgnoreCase("")){
                    Settings.pushValue("logger-channel", channel.getId());
                    DiscordLogger.reload();
                    DiscordLogger.log("Ustawiono kanał do logowania na " + channel.getName());
                }else{
                    String old = Settings.getValue("logger-channel");
                    Settings.pushValue("logger-channel", channel.getId());
                    DiscordLogger.reload();
                    DiscordLogger.log("Zmieniono kanał do logów z #"+jda.getTextChannelById(old).getName()+" na #"+channel.getName());
                }
            }else if(content.equalsIgnoreCase("?save")){
                Settings.saveToFile(Settings.getValue("file_name"));
                DiscordLogger.log("Zapis ustawień wykonany");
            }else if(content.equalsIgnoreCase("?exit")){
                DiscordLogger.log("Wyłączanie bota");

                jda.shutdown();
                System.exit(0);
            }
        }
    }
}
