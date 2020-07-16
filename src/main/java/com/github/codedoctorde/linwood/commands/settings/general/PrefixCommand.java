package com.github.codedoctorde.linwood.commands.settings.general;

import com.github.codedoctorde.linwood.commands.Command;
import com.github.codedoctorde.linwood.entity.GuildEntity;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.hibernate.Session;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author CodeDoctorDE
 */
public class PrefixCommand implements Command {
    @Override
    public boolean onCommand(Session session, Message message, GuildEntity entity, String label, String[] args) {
        ResourceBundle bundle = getBundle(entity);
        assert bundle != null;
        if(args.length == 0)
            message.getChannel().sendMessage(MessageFormat.format(bundle.getString("Get"), entity.getPrefix())).queue();
        else {
            try {
                entity.setPrefix(String.join(" ", args));
                entity.save(session);
                message.getChannel().sendMessage(MessageFormat.format(bundle.getString("Set"), entity.getPrefix())).queue();
            }catch(NullPointerException e){
                message.getChannel().sendMessage(bundle.getString("NotValid")).queue();
            }
        }
        return true;
    }

    @Override
    public boolean hasPermission(Member member) {
        return member.hasPermission(Permission.MANAGE_SERVER);
    }

    @Override
    public Set<String> aliases(GuildEntity entity) {
        return new HashSet<>(Arrays.asList(
                "prefix",
                "pre-fix"
        ));
    }

    @Override
    public ResourceBundle getBundle(GuildEntity entity) {
        return ResourceBundle.getBundle("locale.commands.settings.general.Prefix", entity.getLocalization());
    }
}
