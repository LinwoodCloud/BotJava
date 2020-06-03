package com.github.codedoctorde.linwood.game.mode.whatisit;

import com.github.codedoctorde.linwood.Main;
import com.github.codedoctorde.linwood.entity.GuildEntity;
import com.github.codedoctorde.linwood.game.Game;
import com.github.codedoctorde.linwood.game.GameMode;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.hibernate.Session;

import java.text.MessageFormat;
import java.util.*;

/**
 * @author CodeDoctorDE
 */
public class WhatIsIt implements GameMode {
    private Game game;
    private WhatIsItRound round;
    private long textChannelId;
    private final List<Long> wantWriter = new ArrayList<>();
    private long wantWriterMessageId;
    private int maxRounds;
    private int currentRounds;
    private final HashMap<Long, Integer> points = new HashMap<>();

    @Override
    public void start(Game game) {
        this.game = game;

        var session = Main.getInstance().getDatabase().getSessionFactory().openSession();
        var guild = GuildEntity.get(session, game.getGuildId());
        Category category = null;
        if(guild.getGameCategoryId() != null)
        category = guild.getGameCategory();
        var bundle = getBundle(session);
        Category finalCategory = category;
        game.getGuild().createTextChannel(MessageFormat.format(bundle.getString("TextChannel"), game.getId())).queue((textChannel -> {
            this.textChannelId = textChannel.getIdLong();
            if(finalCategory != null)
                textChannel.getManager().setParent(finalCategory).queue();
        }));
    }

    @Override
    public void stop() {
        getTextChannel().delete().queue();
    }

    public void chooseNextPlayer(){

    }
    public void nextRound(Session session, int writerId, String word){
        round = new WhatIsItRound(writerId, word, this);
        var bundle = getBundle(session);

        getTextChannel().sendMessage(bundle.getString("Next")).queue(message -> message.addReaction("\uD83D\uDD90️").queue(aVoid ->
                message.addReaction("⛔").queue()));
    }
    public void finishRound(){

    }

    public void finishGame(){
        var timer = new Timer();
        var session = Main.getInstance().getDatabase().getSessionFactory().openSession();
        var bundle = getBundle(session);
        session.close();
        timer.schedule(
                new TimerTask() {
                    int time = 30;
                    @Override
                    public void run() {
                        if(getTextChannel() == null || time <= 0) {
                            timer.cancel();
                            Main.getInstance().getGameManager().stopGame(game);
                        }
                        else
                            switch (time){
                                case 30:
                                case 20:
                                case 15:
                                case 10:
                                case 5:
                                case 4:
                                case 3:
                                case 2:
                                    getTextChannel().sendMessage(MessageFormat.format(bundle.getString("DeleteMultiple"), time)).queue();
                                    break;
                                case 1:
                                    getTextChannel().sendMessage(bundle.getString("DeleteSingle")).queue();
                                    break;
                            }
                        time--;
                    }
                },
                1000, 1000
        );
    }

    public ResourceBundle getBundle(Session session){
        return ResourceBundle.getBundle("locale.game.WhatIsIt", Main.getInstance().getDatabase().getGuildById(session, game.getGuildId()).getLocalization());
    }

    public long getTextChannelId() {
        return textChannelId;
    }

    public TextChannel getTextChannel(){
        return Main.getInstance().getJda().getTextChannelById(textChannelId);
    }

    public Game getGame() {
        return game;
    }

    public WhatIsItRound getRound() {
        return round;
    }

    public HashMap<Long, Integer> getPoints() {
        return points;
    }
    public void givePoints(Member member, int number){
        points.put(member.getIdLong(), points.getOrDefault(member.getIdLong(), 0) + number);
    }
    public int getPoints(Member member){
        return points.getOrDefault(member.getIdLong(), 0);
    }

    public int getCurrentRounds() {
        return currentRounds;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public List<Long> getWantWriter() {
        return wantWriter;
    }

    public long getWantWriterMessageId() {
        return wantWriterMessageId;
    }

    public void wantWriter(Session session, Member member) {
        if(!wantWriter.contains(member.getIdLong())) wantWriter.add(member.getIdLong());
        getTextChannel().sendMessage(MessageFormat.format(getBundle(session).getString("Join"), member.getUser().getName())).queue();
    }

    public void removeWriter(Session session, Member member) {
        wantWriter.remove(member.getIdLong());
        getTextChannel().sendMessage(MessageFormat.format(getBundle(session).getString("Leave"), member.getUser().getName())).queue();
    }
    public void giveWriterPoints(Session session){
        if(round == null)
            return;
        givePoints(round.getWriter(), round.getGuesser().size() * 2);
    }
}
