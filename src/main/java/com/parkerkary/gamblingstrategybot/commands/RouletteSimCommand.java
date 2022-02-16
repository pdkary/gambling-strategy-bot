package com.parkerkary.gamblingstrategybot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Component
public class RouletteSimCommand implements SlashCommand {
    public static final String N_KEY = "N";
    public static final String START_VAL_KEY = "S";
    public static final String BET_VAL_KEY = "B";
    public static final String CHOICE_VAL_KEY = "C";
    public static final String WIN_MULT_KEY = "W";
    public static final String LOSE_MULT_KEY = "L";

    public static final int N_TYPE = ApplicationCommandOption.Type.INTEGER.getValue();
    public static final int START_VAL_TYPE = ApplicationCommandOption.Type.INTEGER.getValue();
    public static final int BET_VAL_TYPE = ApplicationCommandOption.Type.INTEGER.getValue();
    public static final int CHOICE_VAL_TYPE = ApplicationCommandOption.Type.STRING.getValue();
    public static final int WIN_MULT_TYPE = ApplicationCommandOption.Type.NUMBER.getValue();
    public static final int LOSE_MULT_TYPE = ApplicationCommandOption.Type.NUMBER.getValue();

    public static final String OUT_MESSAGE_TEMPLATE = "Played %d games, won %d, lost %d, total winnings: %d";

    public String name = "roulette-sim";
    public String description = "Simulate <N> games of roulette";
    public List<ApplicationCommandOptionData> options = List.of(
            buildOption(N_KEY,N_TYPE,"Number of games to simulate (Integer)"),
            buildOption(START_VAL_KEY,START_VAL_TYPE,"Starting Pot (Integer)"),
            buildOption(BET_VAL_KEY,BET_VAL_TYPE,"Starting Bet (Integer)"),
            buildOption(CHOICE_VAL_KEY,CHOICE_VAL_TYPE,"Desired Roulette value (Number,Black,Red,Green,Even,Odd)"),
            buildOption(WIN_MULT_KEY,WIN_MULT_TYPE,"Bet Multiplier on win (Float)"),
            buildOption(LOSE_MULT_KEY,LOSE_MULT_TYPE,"Bet Multiplier on Lose (Float, ideally >1)")
    );

    public ApplicationCommandOptionData buildOption(String key,int type,String desc){
        return ApplicationCommandOptionData.builder().name(key).type(type).required(true).description(desc).build();
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event){
        Long N = event.getOption(N_KEY)
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asLong)
                .orElse(1L);

        Long S = event.getOption(START_VAL_KEY)
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asLong)
                .orElse(1000L);

        Long B = event.getOption(BET_VAL_KEY)
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asLong)
                .orElse(10L);

        Long C = event.getOption(CHOICE_VAL_KEY)
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .map(this::parseChoice)
                .orElse(102L);

        Double W = event.getOption(WIN_MULT_KEY)
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asDouble)
                .orElse(0.5D);

        Double L = event.getOption(LOSE_MULT_KEY)
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asDouble)
                .orElse(2D);

        log.info(String.format("Received values: %d %d %d %d %f %f",N,S,B,C,W,L));

        Long currentPot = S;
        Long currentBet = B;
        Integer totalWins = 0;
        Integer totalLosses = 0;
        Random random = new Random();
        int i;
        for(i=0;i<N;i++){
            if(currentBet > currentPot){
                break;
            }
            currentPot -= currentBet;
            int roll = random.ints(0,100).findFirst().getAsInt();
            //chose black or even
            if(C == 101L){
                if(roll > 0 && roll % 2 ==0){
                    totalWins +=1;
                    currentPot += 2*currentBet;
                    currentBet = Math.round(currentBet*W);;
                } else {
                    totalLosses += 1;
                    currentBet = Math.round(currentBet*L);
                }
            }
            //chose red or odd
            if(C == 102L){
                if(roll > 0 && roll % 2 == 1){
                    totalWins +=1;
                    currentPot += 2*currentBet;
                    currentBet = Math.round(currentBet*W);
                } else {
                    totalLosses += 1;
                    currentBet = Math.round(currentBet*L);
                }
            }
            //was number
            if(C == roll){
                currentPot = currentPot + 100*currentBet;
                currentBet = Math.round(currentBet*W);
                totalWins += 1;
            }
        }
        String outmsg = String.format(OUT_MESSAGE_TEMPLATE,i,totalWins,totalLosses,currentPot);
        return event.reply().withContent(outmsg);
    }

    public Long parseChoice(String choice) throws IllegalArgumentException{
        try {
            return Long.parseLong(choice);
        } catch(NumberFormatException e){
            if(choice.equalsIgnoreCase("black") || choice.equalsIgnoreCase("even")) {
                return 101L;
            } else if( choice.equalsIgnoreCase("red") || choice.equalsIgnoreCase("odd")) {
                return 102L;
            } else if( choice.equalsIgnoreCase("green")) {
                return 0L;
            } else {
                throw new IllegalArgumentException("Choice is not within expected values");
            }
        }
    }
}
