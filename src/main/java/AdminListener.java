import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


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
                    DiscordLogger.log("Ustawiono kanał do logowania na " + channel.getAsMention());
                }else{
                    String old = Settings.getValue("logger-channel");
                    Settings.pushValue("logger-channel", channel.getId());
                    DiscordLogger.reload();
                    DiscordLogger.log("Zmieniono kanał do logów z "+jda.getTextChannelById(old).getAsMention()+" na "+channel.getAsMention());
                }

            }else if(content.equalsIgnoreCase("?save")){
                Settings.saveToFile(Settings.getValue("file_name"));
                DiscordLogger.log("Zapis ustawień wykonany");
                channel.sendMessage("Zapis ustawień wykonany").queue();

            }else if(content.equalsIgnoreCase("?exit")){
                DiscordLogger.log("Zdalne wywołanie zamknięcia bota");
                channel.sendMessage("Zdalne wyłączanie bota zostało wywołane, za chwilę nastąpi jego wyłączenie").queue();
                jda.shutdown();
                System.exit(0);

            }else if(content.equalsIgnoreCase("?settings")){
                DiscordLogger.reload();
                DiscordLogger.log("Wywyłano komendę ?settings na kanale "+channel.getAsMention());
                if (channel != DiscordLogger.loggingChannel){
                    channel.sendMessage(Settings.keyValue.toString()).queue(new Consumer<Message>() {
                        @Override
                        public void accept(Message message) {
                            message.delete().queueAfter(5, TimeUnit.SECONDS);
                            event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
                        }
                    });
                }else{
                    channel.sendMessage(Settings.keyValue.toString()).queue();
                }

            }else if(content.equalsIgnoreCase("?trigger error")){
                DiscordLogger.error("Testowa wiadomość", channel, 5);
                event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
            }
        }
    }
}
