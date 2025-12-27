package de.kifo.common.button.songlist;

import de.kifo.common.button.handle.ButtonBase;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.Objects;

import static de.kifo.commands.music.SongListCommand.*;
import static de.kifo.common.enums.button.ButtonType.PREVIOUS_PAGE;

@ButtonBase.BotButton(buttonType = PREVIOUS_PAGE)
public class PreviousPageButton extends ButtonBase {

    public PreviousPageButton(BotButton botButton) {
        super(botButton);
    }

    @Override
    public void onClick(ButtonInteractionEvent event) {
        Guild guild = event.getGuild();
        Message message = event.getMessage();
        int page = extractPage(Objects.requireNonNull(message.getEmbeds().getFirst().getTitle())) - 1;

        message.editMessageEmbeds(getSonglistPageEmbed(guild, page)).queue();
        message.editMessageComponents(getSonglistPageActionRow(guild, page)).queue();
    }
}
