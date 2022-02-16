package com.parkerkary.gamblingstrategybot.config;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class DiscordBotConfig {

    @Value("${com.parkerkary.gamblingstrategybot.token}")
    public String BOT_TOKEN;

    @Bean
    public GatewayDiscordClient gatewayDiscordClient(DiscordBotConfig config) {
        return DiscordClientBuilder.create(config.BOT_TOKEN).build()
                .gateway()
                .setInitialPresence(ignore -> ClientPresence.online(
                        ClientActivity.listening("to /commands")))
                .login()
                .block();
    }

    @Bean
    public RestClient discordRestClient(GatewayDiscordClient client){
        return client.getRestClient();
    }

    @Bean
    public ApplicationService applicationService(RestClient discordRestClient){
        return discordRestClient.getApplicationService();
    }

    @Bean
    public Long applicationId(RestClient discordRestClient){
        return discordRestClient.getApplicationId().block();
    }
}
