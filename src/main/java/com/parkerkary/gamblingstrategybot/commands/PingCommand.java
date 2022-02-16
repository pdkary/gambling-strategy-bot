package com.parkerkary.gamblingstrategybot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Component
public class PingCommand implements SlashCommand {
    public String name = "ping";
    public String description = "ping pong!";
    public List<ApplicationCommandOptionData> options = new ArrayList<>();

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event){
        return event.reply().withContent("Pong!");
    }
}
