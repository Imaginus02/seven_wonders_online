package com.reynaud.wonders.service;

import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.model.CardType;
import com.reynaud.wonders.model.Science;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class GameScoringService {

    private final PlayerStateService playerStateService;

    public GameScoringService(PlayerStateService playerStateService) {
        this.playerStateService = playerStateService;
    }

    public int calculateSciencePoints(Map<Science, Integer> scienceSymbols) {
        int tablets = scienceSymbols.getOrDefault(Science.TABLET, 0);
        int compasses = scienceSymbols.getOrDefault(Science.COMPASS, 0);
        int gears = scienceSymbols.getOrDefault(Science.GEAR, 0);
        int mutableSymbols = scienceSymbols.getOrDefault(Science.MUTABLE, 0);

        ArrayDeque<int[]> states = new ArrayDeque<>();
        states.push(new int[] {tablets, compasses, gears, mutableSymbols});

        int bestScore = 0;

        while (!states.isEmpty()) {
            int[] state = states.pop();
            int currentTablets = state[0];
            int currentCompasses = state[1];
            int currentGears = state[2];
            int remainingMutableSymbols = state[3];

            if (remainingMutableSymbols == 0) {
                int score = currentTablets * currentTablets
                        + currentCompasses * currentCompasses
                        + currentGears * currentGears
                        + 7 * Math.min(currentTablets, Math.min(currentCompasses, currentGears));
                bestScore = Math.max(bestScore, score);
                continue;
            }

            states.push(new int[] {currentTablets + 1, currentCompasses, currentGears, remainingMutableSymbols - 1});
            states.push(new int[] {currentTablets, currentCompasses + 1, currentGears, remainingMutableSymbols - 1});
            states.push(new int[] {currentTablets, currentCompasses, currentGears + 1, remainingMutableSymbols - 1});
        }

        return bestScore;
    }

    public void applyBestCopiedGuild(PlayerStateEntity playerState) {
        List<CardEntity> neighborGuilds = new ArrayList<>(playerState.getLeftNeighbor().getPlayedCards().stream()
                .filter(card -> card.getType() == CardType.VIOLET)
                .toList());
        neighborGuilds.addAll(playerState.getRightNeighbor().getPlayedCards().stream()
                .filter(card -> card.getType() == CardType.VIOLET)
                .toList());

        if (neighborGuilds.isEmpty()) {
            return;
        }

        int currentSciencePoints = calculateSciencePoints(playerState.getScience());
        String bestGuildName = null;
        int bestGuildScore = Integer.MIN_VALUE;

        for (CardEntity guild : neighborGuilds) {
            int guildScore = switch (guild.getName()) {
                case "Workers Guild" -> countCardsByType(playerState.getLeftNeighbor(), CardType.BROWN)
                        + countCardsByType(playerState.getRightNeighbor(), CardType.BROWN);
                case "Craftsmens Guild" -> 2 * (countCardsByType(playerState.getLeftNeighbor(), CardType.GREY)
                        + countCardsByType(playerState.getRightNeighbor(), CardType.GREY));
                case "Magistrates Guild" -> countCardsByType(playerState.getLeftNeighbor(), CardType.BLUE)
                        + countCardsByType(playerState.getRightNeighbor(), CardType.BLUE);
                case "Traders Guild" -> countCardsByType(playerState.getLeftNeighbor(), CardType.YELLOW)
                        + countCardsByType(playerState.getRightNeighbor(), CardType.YELLOW);
                case "Spies Guild" -> countCardsByType(playerState.getLeftNeighbor(), CardType.RED)
                        + countCardsByType(playerState.getRightNeighbor(), CardType.RED);
                case "Philosophers Guild" -> countCardsByType(playerState.getLeftNeighbor(), CardType.GREEN)
                        + countCardsByType(playerState.getRightNeighbor(), CardType.GREEN);
                case "Shipowners Guild" -> countCardsByType(playerState, CardType.BROWN)
                        + countCardsByType(playerState, CardType.GREY)
                        + countCardsByType(playerState, CardType.VIOLET);
                case "Decorators Guild" -> playerState.getWonder() != null
                        && playerState.getWonderStage() >= playerState.getWonder().getNumberOfStages() - 1 ? 7 : 0;
                case "Builders Guild" -> playerStateService.getWonderStageOrZero(playerState)
                        + playerStateService.getWonderStageOrZero(playerState.getLeftNeighbor())
                        + playerStateService.getWonderStageOrZero(playerState.getRightNeighbor());
                case "Scientists Guild" -> {
                    Map<Science, Integer> copiedScience = new EnumMap<>(Science.class);
                    copiedScience.putAll(playerState.getScience());
                    copiedScience.merge(Science.MUTABLE, 1, Integer::sum);
                    yield calculateSciencePoints(copiedScience) - currentSciencePoints;
                }
                default -> Integer.MIN_VALUE;
            };

            if (guildScore > bestGuildScore) {
                bestGuildScore = guildScore;
                bestGuildName = guild.getName();
            }
        }

        if (bestGuildName == null) {
            return;
        }

        switch (bestGuildName) {
            case "Workers Guild" ->
                    playerStateService.addVictoryPoints(playerState,
                            countCardsByType(playerState.getLeftNeighbor(), CardType.BROWN)
                                    + countCardsByType(playerState.getRightNeighbor(), CardType.BROWN));
            case "Craftsmens Guild" ->
                    playerStateService.addVictoryPoints(playerState,
                            2 * (countCardsByType(playerState.getLeftNeighbor(), CardType.GREY)
                                    + countCardsByType(playerState.getRightNeighbor(), CardType.GREY)));
            case "Magistrates Guild" ->
                    playerStateService.addVictoryPoints(playerState,
                            countCardsByType(playerState.getLeftNeighbor(), CardType.BLUE)
                                    + countCardsByType(playerState.getRightNeighbor(), CardType.BLUE));
            case "Traders Guild" ->
                    playerStateService.addVictoryPoints(playerState,
                            countCardsByType(playerState.getLeftNeighbor(), CardType.YELLOW)
                                    + countCardsByType(playerState.getRightNeighbor(), CardType.YELLOW));
            case "Spies Guild" ->
                    playerStateService.addVictoryPoints(playerState,
                            countCardsByType(playerState.getLeftNeighbor(), CardType.RED)
                                    + countCardsByType(playerState.getRightNeighbor(), CardType.RED));
            case "Philosophers Guild" ->
                    playerStateService.addVictoryPoints(playerState,
                            countCardsByType(playerState.getLeftNeighbor(), CardType.GREEN)
                                    + countCardsByType(playerState.getRightNeighbor(), CardType.GREEN));
            case "Shipowners Guild" ->
                    playerStateService.addVictoryPoints(playerState,
                            countCardsByType(playerState, CardType.BROWN)
                                    + countCardsByType(playerState, CardType.GREY)
                                    + countCardsByType(playerState, CardType.VIOLET));
            case "Decorators Guild" -> {
                if (playerState.getWonder() != null
                        && playerState.getWonderStage() >= playerState.getWonder().getNumberOfStages() - 1) {
                    playerStateService.addVictoryPoints(playerState, 7);
                }
            }
            case "Builders Guild" ->
                    playerStateService.addVictoryPoints(playerState,
                            playerStateService.getWonderStageOrZero(playerState)
                                    + playerStateService.getWonderStageOrZero(playerState.getLeftNeighbor())
                                    + playerStateService.getWonderStageOrZero(playerState.getRightNeighbor()));
            case "Scientists Guild" -> playerState.getScience().merge(Science.MUTABLE, 1, Integer::sum);
            default -> {
            }
        }
    }

    private int countCardsByType(PlayerStateEntity playerState, CardType type) {
        if (playerState == null || playerState.getPlayedCards() == null) {
            return 0;
        }
        return (int) playerState.getPlayedCards().stream()
                .filter(card -> card.getType() == type)
                .count();
    }
}
