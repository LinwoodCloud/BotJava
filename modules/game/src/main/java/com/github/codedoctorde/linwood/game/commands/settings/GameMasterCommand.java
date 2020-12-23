package com.github.codedoctorde.linwood.game.commands.settings;

import com.github.codedoctorde.linwood.core.entity.GuildEntity;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author CodeDoctorDE
 */
public class GameMasterCommand extends Command {
    @Override
    public boolean onCommand(final CommandEvent event) {
        ResourceBundle bundle = getBundle(entity);
        if(args.length > 1)
            return false;
        if(args.length == 0)
            if((entity.getGameEntity().getGameCategoryId() != null))
                message.getChannel().sendMessageFormat(bundle.getString("Get"), entity.getGameEntity().getGameMasterRole().getName(), entity.getGameEntity().getGameMasterRoleId()).queue();
        else
            message.getChannel().sendMessage(bundle.getString("GetNull")).queue();
        else {
            try {
                Role role = null;
                try{
                    role = message.getGuild().getRoleById(args[0]);
                }catch(Exception ignored){

                }
                if(role == null){
                    var roles = message.getGuild().getRolesByName(args[0], true);
                    if(roles.size() < 1)
                        message.getChannel().sendMessage(bundle.getString("SetNothing")).queue();
                    else if(roles.size() > 1)
                        message.getChannel().sendMessage(bundle.getString("SetMultiple")).queue();
                    else
                        role = roles.get(0);
                }
                if(role == null)
                    return true;
                entity.getGameEntity().setGameMasterRole(role);
                entity.save(session);
                message.getChannel().sendMessageFormat(bundle.getString("Set"), entity.getGameEntity().getGameMasterRole().getName(), entity.getGameEntity().getGameMasterRoleId()).queue();
            }catch(NullPointerException e){
                message.getChannel().sendMessage(bundle.getString("NotValid")).queue();
            }
        }
        return true;
    }
    @Override
    public boolean hasPermission(final CommandEvent event) {
       var member = event.getMember();
       var entity = event.getEntity();
       return member.hasPermission(Permission.MANAGE_SERVER) || entity.getMaintainerId() != null && member.getRoles().contains(member.getGuild().getRoleById(entity.getMaintainerId()));
    }

    @Override
    public @NotNull Set<String> aliases(GuildEntity entity) {
        super(
                "gamemaster",
                "game-master",
                "master"
        );
    }
}