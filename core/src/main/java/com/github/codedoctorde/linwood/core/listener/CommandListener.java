package com.github.codedoctorde.linwood.core.listener;

import com.github.codedoctorde.linwood.core.Linwood;
import com.github.codedoctorde.linwood.core.commands.Command;
import com.github.codedoctorde.linwood.core.commands.CommandEvent;
import com.github.codedoctorde.linwood.core.entity.GuildEntity;
import com.github.codedoctorde.linwood.core.exceptions.CommandSyntaxException;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.apache.tools.ant.types.Commandline;

import javax.annotation.Nonnull;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * @author CodeDoctorDE
 */
public class CommandListener {
    public static final Pattern pattern = Pattern.compile("(?<module>^[A-Z]+/)?(?<command>[A-Z]+$)");


    @SubscribeEvent
    public void onCommand(@Nonnull MessageReceivedEvent event) {
        var session = Linwood.getInstance().getDatabase().getSessionFactory().openSession();
        var guild = GuildEntity.get(session, event.getGuild().getIdLong());
        if (event.getChannelType() == ChannelType.TEXT && !event.getAuthor().isBot()) {
            var content = event.getMessage().getContentRaw();
            var prefixes = guild.getPrefixes();
            var id = event.getJDA().getSelfUser().getId();
            var nicknameMention = "<@!" + id + ">";
            var normalMention = "<@" + id + ">";
            String prefix = "";
            for (var current : prefixes)
                if (content.startsWith(current))
                    prefix = current;
            String split = null;
            if (!prefix.isBlank() && content.startsWith(prefix))
                split = prefix;
            else if (content.startsWith(nicknameMention))
                split = nicknameMention;
            else if (content.startsWith(normalMention))
                split = normalMention;
            if (split != null) {
                var bundle = getBundle(guild);
                var command = Commandline.translateCommandline(content.substring(split.length()));
                try {
                    execute(new CommandEvent(event.getMessage(), session, guild, prefix, command));
                }catch(CommandSyntaxException e){
                    event.getChannel().sendMessage(bundle.getString("Syntax")).append(e.getMessage()).queue();
                } catch(PermissionException e){
                    event.getChannel().sendMessage(bundle.getString("InsufficientPermission")).append(e.getMessage()).queue();
                }catch (Exception e) {
                    event.getChannel().sendMessage(bundle.getString("Error")).append(e.getMessage()).queue();
                    Sentry.captureException(e);
                }
            }
        }
        session.close();
    }

    public ResourceBundle getBundle(GuildEntity entity) {
        return ResourceBundle.getBundle("locale.Command", entity.getLocalization());
    }

    public boolean sendHelp(CommandEvent event) {
        var matcher = pattern.matcher(String.join(" ", event.getArguments()));
        if (!matcher.find())
            return false;
        var commandString = matcher.group("command");
        var moduleString = matcher.group("module");
        Command command = findCommand(commandString, moduleString);
        if (command == null)
            return false;
        command.sendHelp(event);
        return true;
    }
    public Command findCommand(String commandString, String moduleString){
        if (moduleString != null) {
            var module = Linwood.getInstance().getModule(moduleString);
            if (module != null)
                return module.getCommand(commandString);
        }
        for (var current :
                Linwood.getInstance().getModules()) {
            var currentCommand = current.getCommand(commandString);
            if (currentCommand != null)
                return currentCommand;
        }
        return null;
    }
    public void execute(CommandEvent commandEvent){

    }
}
