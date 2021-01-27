package com.github.codedoctorde.linwood.core.module;

import com.github.codedoctorde.linwood.core.entity.GeneralGuildEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

public interface GuildOperation {
    @NotNull
    default String translate(final GeneralGuildEntity entity, final String key){
        return entity.translate(getClass().getCanonicalName(), key);
    }
}