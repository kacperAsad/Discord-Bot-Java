import MapIO.MapReader;
import MapIO.MapSaver;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CommandListener extends ListenerAdapter {

    public TextChannel loggingChannel = null;
    JDA jda;

    public CommandListener(JDA jda){
        this.jda = jda;
    }

    public void error(String name, String context){
        if (loggingChannel != null){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.RED);
            eb.setTitle(context);

            loggingChannel.sendMessage(eb.build()).queue();
        }
    }

    public void log(String name , String context){
        if (loggingChannel != null){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.GREEN);
            eb.setTitle(context);

            loggingChannel.sendMessage(eb.build()).queue();
        }
    }
    public void memberLog(Member member, String context){
        if (loggingChannel != null){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setAuthor(member.getUser().getName(), member.getUser().getAvatarUrl());
            eb.setColor(Color.ORANGE);
            eb.setTitle(context);

            loggingChannel.sendMessage(eb.build()).queue();
        }
    }

    public void init(){
        load();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.getAuthor().isBot()){
            return;
        }
        if (event.isFromType(ChannelType.PRIVATE)){
            try {
                event.getAuthor().openPrivateChannel().flatMap(channel -> channel.sendMessage("Obecnie system dm nie jest wspierany...")).queue();
                return;
            } catch (Exception e){
                System.out.println(e);
            }

        }
        if (event.getTextChannel() != loggingChannel) {
            memberLog(event.getMember(), event.getMessage().getContentDisplay());
        }


        if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)){
            if (event.getMessage().getContentRaw().equalsIgnoreCase("!logging channel")){
                loggingChannel = event.getTextChannel();
                log(event.getMember().getNickname(), "Pomyślnie ustawiono kanał #"+event.getTextChannel().getName() + " jako kanał do wysyłania logów.");
            }else if (event.getMessage().getContentRaw().equalsIgnoreCase("!save")){
                save();
            }else if (event.getMessage().getContentRaw().equalsIgnoreCase("!load")) {
                load();
            }else if(event.getMessage().getContentRaw().startsWith("!clear")){
                if (event.getMessage().getContentRaw().equalsIgnoreCase("!clear")){
                    var messages = event.getTextChannel().getHistory().retrievePast(50).complete();
                    log("Deleting", "Deleting "+messages.size() + " messages in "+event.getTextChannel().getName());
                    event.getTextChannel().deleteMessages(messages).complete();
                }else{
                    var countStr = event.getMessage().getContentRaw().split(" ");
                    if (countStr.length > 1) {
                        Message m = new MessageBuilder().append("Błąd! Wystąpił błąd podczas parsowania komendy! Prawdopodobnie dałeś za dużo spacji albo źle użyłeś komendy").mention(event.getMember()).build();

                        event.getTextChannel().sendMessage(m).queue();
                        m.delete().queueAfter(2, TimeUnit.SECONDS);
                        log("", "Powinno byc");

                    }
                    int count = Integer.getInteger(countStr[1]);
                    log("Deleting", "Deleting "+ count + " messages in "+event.getTextChannel().getName());
                    event.getTextChannel().deleteMessages(event.getTextChannel().getHistory().retrievePast(count).complete()).complete();
                }
            }
            else if (event.getMessage().getContentRaw().equalsIgnoreCase("!stop") && event.getMember().getPermissions().contains(Permission.ALL_PERMISSIONS)){
                log("Bot", "Wyłączanie");
                save();
                jda.shutdown();
                System.exit(0);
            }
        }
    }

    public void save(){
        long id = loggingChannel.getIdLong();
        HashMap<String, String> settings = new HashMap<>();
        settings.put("logging-channel", Long.toString(id));

        if (MapSaver.saveMapInFile("data", settings)) System.out.println("Zapisano pomyślnie");
        log("Bot", "Zapisano pomyślnie");
    }
    public void load(){
        Map<String, String> settings = MapReader.loadMapWithFile("data");
        loggingChannel = jda.getTextChannelById(Long.parseLong(settings.get("logging-channel")));
        log("Bot", "Wczytano pomyślnie");
    }
}
