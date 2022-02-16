package com.parkerkary.gamblingstrategybot;

import com.parkerkary.gamblingstrategybot.commands.SlashCommand;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalCommandRegistrar implements ApplicationRunner {
    private final Long applicationId;
    private final ApplicationService applicationService;
    private final Collection<SlashCommand> slashCommands;

    @Override
    public void run(ApplicationArguments args) {
        List<ApplicationCommandRequest> requests = slashCommands.stream().map(command ->
            ApplicationCommandRequest.builder()
                    .name(command.getName())
                    .description(command.getDescription())
                    .options(command.getOptions())
                    .build()).collect(Collectors.toList());
        applicationService.bulkOverwriteGlobalApplicationCommand(applicationId,requests);
    }
}
