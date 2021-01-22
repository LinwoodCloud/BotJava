package com.github.codedoctorde.linwood.main.commands.settings;

import com.github.codedoctorde.linwood.core.commands.Command;
import com.github.codedoctorde.linwood.core.commands.CommandEvent;
import net.dv8tion.jda.api.Permission;

import java.util.ResourceBundle;

/**
 * @author CodeDoctorDE
 */
public class ClearMaintainerCommand extends Command {
    @Override
    public boolean onCommand(final CommandEvent event) {
        var entity = event.getEntity();
        ResourceBundle bundle = getBundle(entity);
        if(event.getArguments().length != 0)
            return false;
        entity.getGameEntity().setGameMasterRole(null);
        entity.save(event.getSession());
        event.reply(bundle.getString("Clear")).queue();
        return true;
    }
    @Override
    public boolean hasPermission(final CommandEvent event) {
       var member = event.getMember();
       var entity = event.getEntity();
       return member.hasPermission(Permission.MANAGE_SERVER) || entity.getMaintainerId() != null && member.getRoles().contains(member.getGuild().getRoleById(entity.getMaintainerId()));
    }

    public ClearMaintainerCommand() {
        super("clear-control", "clear-controller", "clear-maint", "clear-maintainer", "clearcontrol", "clearcontroller", "clearmaint", "clearmaintainer");
    }
}
