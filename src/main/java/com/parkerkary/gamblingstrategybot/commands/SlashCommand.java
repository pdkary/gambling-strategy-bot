package com.parkerkary.gamblingstrategybot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SlashCommand {
    String getName();

    String getDescription();

    List<ApplicationCommandOptionData> getOptions();

    Mono<Void> handle(ChatInputInteractionEvent event);
}
