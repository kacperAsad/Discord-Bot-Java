import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DiscordLogger {

    public static TextChannel loggingChannel = null;
    static JDA jda = null;

    public static void init(JDA j){
        jda = j;
        String id = Settings.getValue("logger-channel");
        if (!id.equals("")){
            loggingChannel = jda.getTextChannelById(id);
        }

    }
    public static void reload(){
        String id = Settings.getValue("logger-channel");
        if (!id.equals("")){
            loggingChannel = jda.getTextChannelById(id);
        }
    }

    /**
     Wysyła wiadomość na kanał z logami serwera

     @param message wiadomość, jaka zostanie wysłana na kanał przypisany logom serwera
     */
    public static void log(String message){
        if (loggingChannel != null){
            MessageBuilder builder = new MessageBuilder();
            builder.append("Log ");
            builder.append("[").append(getNow()).append("]").append("\n");
            builder.append(message);
            loggingChannel.sendMessage(builder.build()).queue();
        }
    }

    /**
        Wysyła wiadomość z błędem na podany kanał, która po podanym czasie zniknie. Czas jest mierzony w sekundach

        @param message wiadomość, jaką dostanie użytkownik
        @param channel kanał, na jaki ma zostać wysłana wiadomość
        @param timeout czas w sekundach, po którym wiadomość zostanie usunięta
     */
    public static void error(String message, TextChannel channel, int timeout){
        MessageBuilder builder = new MessageBuilder();
        builder.append("Błąd ");
        builder.append("[").append(getNow()).append("]").append("\n");
        builder.append(message);
        Message m = builder.build();


        channel.sendMessage(m).queue(new Consumer<Message>()
        {
            @Override
            public void accept(Message t)
            {
                t.delete().queueAfter(timeout, TimeUnit.SECONDS);
            }
        });
        if (loggingChannel != null) {
            loggingChannel.sendMessage(builder.build()).queue();
        }
    }
    private static String getNow(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}
