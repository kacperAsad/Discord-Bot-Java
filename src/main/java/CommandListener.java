import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class CommandListener extends ListenerAdapter {

    public TextChannel loggingChannel = null;
    private Dotenv environment;

    public CommandListener(Dotenv env){
        this.environment = env;
    }

    public void log(Member member , String context){
        if (loggingChannel != null){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setAuthor(member.getEffectiveName());
            eb.setColor(Color.GREEN);
            eb.addField("Wiadomość:", context, true);

            loggingChannel.sendMessage(eb.build()).queue();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.getAuthor().isBot()){
            return;
        }
        if (event.isFromType(ChannelType.PRIVATE)){
            try {
                event.getAuthor().openPrivateChannel().flatMap(channel -> channel.sendMessage("Obecnie system dm nie jest wspierany...")).queue();
            } catch (Exception e){
                System.out.println(e);
            }

        }

        if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)){
            if (event.getMessage().getContentRaw().equalsIgnoreCase("!logging channel")){
                loggingChannel = event.getTextChannel();
                log(event.getMember(), "Pomyślnie ustawiono kanał #"+event.getTextChannel().getName() + " jako kanał do wysyłania logów.");

            }
        }
    }
}
