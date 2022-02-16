package com.parkerkary.gamblingstrategybot.listeners;

import com.parkerkary.gamblingstrategybot.commands.SlashCommand;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class SlashCommandListener {
    private final Collection<SlashCommand> commands;

    public SlashCommandListener(List<SlashCommand> slashCommands, GatewayDiscordClient client){
        commands = slashCommands;
        for(SlashCommand command : slashCommands){
            log.info("Listening to command: " + command.getName());
        }
        client.on(ChatInputInteractionEvent.class,this::handle).subscribe();
    }

    public Mono<Void> handle(ChatInputInteractionEvent event){
        return Flux.fromIterable(commands)
                .filter(command -> command.getName().equalsIgnoreCase(event.getCommandName()))
                .next()
                .flatMap(command -> command.handle(event));
    }
}
