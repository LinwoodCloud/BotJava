package com.github.codedoctorde.linwood.main.commands;

import com.github.codedoctorde.linwood.core.commands.Command;
import com.github.codedoctorde.linwood.core.commands.CommandEvent;
import net.dv8tion.jda.api.Permission;

/**
 * @author CodeDoctorDE
 */
public class ClearCommand extends Command {
    @Override
    public boolean onCommand(final CommandEvent event) {
        var args = event.getArguments();
        var entity = event.getEntity();
        if(args.length != 1)
            return false;
        int count;
        var bundle = getBundle(entity);
        try{
            count = Integer.parseInt(args[0]);
        }catch(Exception ignored){
            event.reply(bundle.getString("Invalid")).queue();
            return true;
        }
        if(count <= 0 || count > 100)
            event.reply(bundle.getString("Between")).queue();
        else
            event.getTextChannel().getHistory().retrievePast(count).queue(messages -> {
                messages.forEach(deleteMessage -> deleteevent.getMessage().delete().queue());
                event.replyFormat(bundle.getString("Success"), messages.size()).queue();
            });
        return true;
    }
    @Override
    public boolean hasPermission(final CommandEvent event) {
        var member = event.getMember();
        return member.hasPermission(Permission.MANAGE_CHANNEL);
    }

    public ClearCommand(){
        super(
                "clear", "c", "clearchat","clear-chat"
        );
    }
}
