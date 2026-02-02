package com.reynaud.wonders.manager;

import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.model.CardType;
import com.reynaud.wonders.model.Ressources;
import com.reynaud.wonders.model.Science;
import com.reynaud.wonders.service.LoggingService;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manager responsible for handling card play mechanics.
 * Determines if a card can be played and executes the play action.
 */
@Component
public class CardPlayManager {

    private final LoggingService loggingService;

    public CardPlayManager(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    /**
     * Processes a card play action, moving the specified card from the player's hand to their played cards.
     * 
     * @param playerState the player state entity performing the action
     * @param cardToPlay the card entity to be played
     * @return true if the card was successfully played, false if the player cannot afford the card cost
     */
    @Transactional
    public boolean playCard(PlayerStateEntity playerState, CardEntity cardToPlay) {
        loggingService.debug("Playing card - Player: " + playerState.getUser().getUsername() + ", Card: " + cardToPlay.getName() + ", HandSize: " + playerState.getHand().size(), "CardPlayManager.playCard");
        if (canPlayCard(playerState, cardToPlay)) {
            if (cardToPlay.getCoinCost() > 0) {
                int currentCoins = playerState.getCoins();
                playerState.setCoins(currentCoins - cardToPlay.getCoinCost());
                loggingService.debug("Paid coin cost - Player: " + playerState.getUser().getUsername() + ", CoinCost: " + cardToPlay.getCoinCost() + ", RemainingCoins: " + playerState.getCoins(), "CardPlayManager.playCard");
            } else {
                payCost(playerState, cardToPlay.getCost());
            }
            playerState.getHand().remove(cardToPlay);
            playerState.getPlayedCards().add(cardToPlay);
            applyCardEffect(playerState, cardToPlay);
            loggingService.info("Card played successfully - Player: " + playerState.getUser().getUsername() + ", Card: " + cardToPlay.getName() + ", NewHandSize: " + playerState.getHand().size() + ", TotalPlayedCards: " + playerState.getPlayedCards().size(), "CardPlayManager.playCard");
            return true;
        } else {
            loggingService.warning("Cannot play card - Insufficient resources/coins - Player: " + playerState.getUser().getUsername() + ", Card: " + cardToPlay.getName(), "CardPlayManager.playCard");
            return false;
        }
    }

    /**
     * Processes the payment for a resource cost, deducting coins from the player
     * and transferring coins to neighbors when resources are bought.
     * 
     * @param playerState the player state entity
     * @param resourceCost the resource cost map to pay
     */
    public void payCost(PlayerStateEntity playerState, Map<Ressources, Integer> resourceCost) {
        loggingService.debug("Paying resource cost - Player: " + playerState.getUser().getUsername() + ", Cost: " + resourceCost, "CardPlayManager.payCost");

        Map<Ressources, Integer> playerRessources = playerState.getResources();

        // Step 1: Calculate missing resources after using player's own resources
        Map<Ressources, Integer> missingResources = resourceCost.entrySet().stream()
                .filter(entry -> entry.getKey().isRessource())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Math.max(0, entry.getValue() - playerRessources.getOrDefault(entry.getKey(), 0))
                ));

        // Step 2: Apply mutable resources (wildcards) to cover missing resources
        int remainingMutableBase = playerRessources.getOrDefault(Ressources.MUTABLE_BASE, 0);
        int remainingMutableAdvanced = playerRessources.getOrDefault(Ressources.MUTABLE_ADVANCED, 0);

        // Deduct base resources covered by mutable base
        for (Map.Entry<Ressources, Integer> entry : missingResources.entrySet()) {
            if (entry.getKey().isBaseRessource() && entry.getValue() > 0 && remainingMutableBase > 0) {
                int usedMutable = Math.min(entry.getValue(), remainingMutableBase);
                entry.setValue(entry.getValue() - usedMutable);
                remainingMutableBase -= usedMutable;
            }
        }

        // Deduct advanced resources covered by mutable advanced
        for (Map.Entry<Ressources, Integer> entry : missingResources.entrySet()) {
            if (entry.getKey().isAdvancedRessource() && entry.getValue() > 0 && remainingMutableAdvanced > 0) {
                int usedMutable = Math.min(entry.getValue(), remainingMutableAdvanced);
                entry.setValue(entry.getValue() - usedMutable);
                remainingMutableAdvanced -= usedMutable;
            }
        }

        // Apply mutable pairs to cover missing base resources
        for (Ressources mutablePair : Ressources.values()) {
            if (mutablePair.isMutablePair()) {
                int pairCount = playerRessources.getOrDefault(mutablePair, 0);
                if (pairCount > 0) {
                    Ressources[] options = mutablePair.getPairOptions();
                    for (Ressources option : options) {
                        if (missingResources.containsKey(option) && missingResources.get(option) > 0) {
                            int usedPairs = Math.min(missingResources.get(option), pairCount);
                            missingResources.put(option, missingResources.get(option) - usedPairs);
                            pairCount -= usedPairs;
                            if (pairCount == 0) break;
                        }
                    }
                }
            }
        }

        // Step 3: Calculate cost to buy remaining resources from neighbors
        PlayerStateEntity leftNeighbor = playerState.getLeftNeighbor();
        PlayerStateEntity rightNeighbor = playerState.getRightNeighbor();
        Map<Ressources, Integer> leftResources = leftNeighbor.getResources();
        Map<Ressources, Integer> rightResources = rightNeighbor.getResources();
        
        // Add mutable pairs as available resources from neighbors (they can provide ONE of the pair)
        Map<Ressources, Integer> leftAvailableResources = new java.util.HashMap<>(leftResources);
        Map<Ressources, Integer> rightAvailableResources = new java.util.HashMap<>(rightResources);
        for (Ressources mutablePair : Ressources.values()) {
            if (mutablePair.isMutablePair()) {
                int leftPairs = leftResources.getOrDefault(mutablePair, 0);
                int rightPairs = rightResources.getOrDefault(mutablePair, 0);
                if (leftPairs > 0 || rightPairs > 0) {
                    Ressources[] options = mutablePair.getPairOptions();
                    for (Ressources option : options) {
                        leftAvailableResources.merge(option, leftPairs, Integer::sum);
                        rightAvailableResources.merge(option, rightPairs, Integer::sum);
                    }
                }
            }
        }

        int leftBasePrice = playerState.getLeftBaseRessourcePriceMultiplier();
        int rightBasePrice = playerState.getRightBaseRessourcePriceMultiplier();
        int leftAdvancedPrice = playerState.getLeftAdvancedRessourcePriceMultiplier();
        int rightAdvancedPrice = playerState.getRightAdvancedRessourcePriceMultiplier();

        int totalCost = 0;
        int coinsToLeft = 0;
        int coinsToRight = 0;

        for (Map.Entry<Ressources, Integer> entry : missingResources.entrySet()) {
            Ressources resource = entry.getKey();
            int amountNeeded = entry.getValue();

            if (amountNeeded <= 0) {
                continue;
            }

            int availableFromLeft = leftAvailableResources.getOrDefault(resource, 0);
            int availableFromRight = rightAvailableResources.getOrDefault(resource, 0);

            // Determine prices
            int priceFromLeft, priceFromRight;
            if (resource.isBaseRessource()) {
                priceFromLeft = leftBasePrice;
                priceFromRight = rightBasePrice;
            } else if (resource.isAdvancedRessource()) {
                priceFromLeft = leftAdvancedPrice;
                priceFromRight = rightAdvancedPrice;
            } else {
                continue;
            }

            // Buy from cheaper neighbor first
            int remaining = amountNeeded;
            if (priceFromLeft <= priceFromRight) {
                // Buy from left first
                int buyFromLeft = Math.min(remaining, availableFromLeft);
                int costFromLeft = buyFromLeft * priceFromLeft;
                coinsToLeft += costFromLeft;
                totalCost += costFromLeft;
                remaining -= buyFromLeft;

                // Buy remaining from right
                if (remaining > 0) {
                    int costFromRight = remaining * priceFromRight;
                    coinsToRight += costFromRight;
                    totalCost += costFromRight;
                }
            } else {
                // Buy from right first
                int buyFromRight = Math.min(remaining, availableFromRight);
                int costFromRight = buyFromRight * priceFromRight;
                coinsToRight += costFromRight;
                totalCost += costFromRight;
                remaining -= buyFromRight;

                // Buy remaining from left
                if (remaining > 0) {
                    int costFromLeft = remaining * priceFromLeft;
                    coinsToLeft += costFromLeft;
                    totalCost += costFromLeft;
                }
            }
        }

        // Deduct coins from player and add to neighbors
        playerState.setCoins(playerState.getCoins() - totalCost);
        if (coinsToLeft > 0) {
            leftNeighbor.setCoins(leftNeighbor.getCoins() + coinsToLeft);
            loggingService.debug("Paid coins to left neighbor - Player: " + playerState.getUser().getUsername() + ", Amount: " + coinsToLeft + ", Neighbor: " + leftNeighbor.getUser().getUsername(), "CardPlayManager.payCost");
        }
        if (coinsToRight > 0) {
            rightNeighbor.setCoins(rightNeighbor.getCoins() + coinsToRight);
            loggingService.debug("Paid coins to right neighbor - Player: " + playerState.getUser().getUsername() + ", Amount: " + coinsToRight + ", Neighbor: " + rightNeighbor.getUser().getUsername(), "CardPlayManager.payCost");
        }

        loggingService.info("Resource cost paid - Player: " + playerState.getUser().getUsername() + ", TotalCost: " + totalCost + ", RemainingCoins: " + playerState.getCoins(), "CardPlayManager.payCost");
    }

    /**
     * Applies the effect of a played card based on its type.
     * Effects are intentionally left blank for now.
     *
     * @param playerState the player state entity
     * @param cardToPlay the card being played
     */
    private void applyCardEffect(PlayerStateEntity playerState, CardEntity cardToPlay) {
        switch (cardToPlay.getType()) {
            case BROWN:
                switch (cardToPlay.getName()) {
                    case "Lumber Yard":
                        playerState.getResources().merge(Ressources.WOOD, 1, Integer::sum);
                        break;
                    case "Clay Pool":
                        playerState.getResources().merge(Ressources.BRICK, 1, Integer::sum);
                        break;
                    case "Stone Pit":
                        playerState.getResources().merge(Ressources.STONE, 1, Integer::sum);
                        break;
                    case "Ore Vein":
                        playerState.getResources().merge(Ressources.ORE, 1, Integer::sum);
                        break;
                    case "Timber Yard":
                        //TODO: Add mutable wood/stone resource
                        playerState.getResources().merge(Ressources.STONE_WOOD, 1, Integer::sum);
                        break;
                    case "Clay Pit":
                        //TODO: Add mutable brick/ore resource
                        playerState.getResources().merge(Ressources.ORE_BRICK, 1, Integer::sum);
                        break;
                    
                    case "Sawmill":
                        playerState.getResources().merge(Ressources.WOOD, 2, Integer::sum);
                        break;
                    case "Foundry":
                        playerState.getResources().merge(Ressources.ORE, 2, Integer::sum);
                        break;
                    case "Quarry":
                        playerState.getResources().merge(Ressources.STONE, 2, Integer::sum);
                        break;
                    case "Brickyard":
                        playerState.getResources().merge(Ressources.BRICK, 2, Integer::sum);
                        break;
                    default:
                        break;
                }
                break;
            case GREY:
                switch (cardToPlay.getName()) {
                    case "Glassworks":
                        playerState.getResources().merge(Ressources.GLASS, 1, Integer::sum);                        
                        break;
                    case "Press":
                        playerState.getResources().merge(Ressources.PAPER, 1, Integer::sum);
                        break;
                    case "Loom":
                        playerState.getResources().merge(Ressources.TEXTILE, 1, Integer::sum);
                        break;                
                    default:
                        break;
                }
                break;
            case BLUE:
                switch (cardToPlay.getName()) {
                    case "Altar":
                        playerState.setVictoryPoints(playerState.getVictoryPoints() + 2);
                        break;
                    case "Theater":
                        playerState.setVictoryPoints(playerState.getVictoryPoints() + 2);
                        break;
                    case "Bath":
                        playerState.setVictoryPoints(playerState.getVictoryPoints() + 3);
                        break;
                    case "Courthouse":
                        playerState.setVictoryPoints(playerState.getVictoryPoints() + 4);
                        break;
                    case "Temple":
                        playerState.setVictoryPoints(playerState.getVictoryPoints() + 3);
                        break;
                    case "Statue":
                        playerState.setVictoryPoints(playerState.getVictoryPoints() + 4);
                        break;
                    case "Aqueduct":
                        playerState.setVictoryPoints(playerState.getVictoryPoints() + 5);
                        break;
                    case "Gardens":
                        playerState.setVictoryPoints(playerState.getVictoryPoints() + 5);
                        break;
                    case "Senate":
                        playerState.setVictoryPoints(playerState.getVictoryPoints() + 6);
                        break;
                    case "Town Hall":
                        playerState.setVictoryPoints(playerState.getVictoryPoints() + 6);
                        break;
                    case "Pantheon":
                        playerState.setVictoryPoints(playerState.getVictoryPoints() + 7);
                        break;
                    case "Palace":
                        playerState.setVictoryPoints(playerState.getVictoryPoints() + 8);
                        break;
                    default:
                        break;
                }
                break;
            case YELLOW:
                switch (cardToPlay.getName()) {
                    case "West Trading Post":
                        playerState.setLeftBaseRessourcePriceMultiplier(1);
                        break;
                    case "East Trading Post":
                        playerState.setRightBaseRessourcePriceMultiplier(1);
                        break;
                    case "Marketplace":
                        playerState.setLeftAdvancedRessourcePriceMultiplier(1);
                        playerState.setRightAdvancedRessourcePriceMultiplier(1);
                        break;
                    case "Caravansery":
                        playerState.getResources().merge(Ressources.MUTABLE_BASE, 1, Integer::sum);
                        break;
                    case "Forum":
                        playerState.getResources().merge(Ressources.MUTABLE_ADVANCED, 1, Integer::sum);
                        break;
                    case "Vineyard":
                        // TODO: Take into account neighbors' brown cards played this turn, even if this player is the first to play
                        int vineyardCoins = 0;
                        List<CardEntity> combinedPlayedCards = new ArrayList<>(playerState.getPlayedCards());
                        combinedPlayedCards.addAll(playerState.getLeftNeighbor().getPlayedCards());
                        combinedPlayedCards.addAll(playerState.getRightNeighbor().getPlayedCards());
                        for (CardEntity card : combinedPlayedCards) {
                            if (card.getType() == CardType.BROWN) {
                                vineyardCoins += 1;
                            }
                        }
                        playerState.setCoins(playerState.getCoins() + vineyardCoins);
                        break;
                    case "Lighthouse":
                        // 1 coin and 1 victory point per yellow card played
                        for (CardEntity card : playerState.getPlayedCards()) {
                            if (card.getType() == CardType.YELLOW) {
                                playerState.setCoins(playerState.getCoins() + 1);
                                //playerState.setVictoryPoints(playerState.getVictoryPoints() + 1);
                            }
                        }
                        // TODO: The addition of victory points should be done at the end of the game
                        break;
                    case "Haven":
                        // 1 coin and 1 victory point per yellow card played
                        for (CardEntity card : playerState.getPlayedCards()) {
                            if (card.getType() == CardType.BROWN) {
                                playerState.setCoins(playerState.getCoins() + 1);
                                //playerState.setVictoryPoints(playerState.getVictoryPoints() + 1);
                            }
                        }
                        // TODO: The addition of victory points should be done at the end of the game
                        break;
                    case "Chamber of Commerce":
                        // 2 coins and 2 victory points per yellow card played
                        for (CardEntity card : playerState.getPlayedCards()) {
                            if (card.getType() == CardType.YELLOW) {
                                playerState.setCoins(playerState.getCoins() + 2);
                                //playerState.setVictoryPoints(playerState.getVictoryPoints() + 2);
                            }
                        }
                        // TODO: The addition of victory points should be done at the end of the game
                        break;
                    default:
                        break;
                }
                break;
            case RED:
                switch (cardToPlay.getName()) {
                    case "Guard Tower":
                        playerState.setMilitaryPoints(playerState.getMilitaryPoints() + 1);
                        break;
                    case "Barracks":
                        playerState.setMilitaryPoints(playerState.getMilitaryPoints() + 1);
                        break;
                    case "Stockade":
                        playerState.setMilitaryPoints(playerState.getMilitaryPoints() + 1);
                        break;
                    case "Stables":
                        playerState.setMilitaryPoints(playerState.getMilitaryPoints() + 2);
                        break;
                    case "Archery Range":
                        playerState.setMilitaryPoints(playerState.getMilitaryPoints() + 2);
                        break;
                    case "Walls":
                        playerState.setMilitaryPoints(playerState.getMilitaryPoints() + 2);
                        break;
                    case "Arsenal":
                        playerState.setMilitaryPoints(playerState.getMilitaryPoints() + 3);
                        break;
                    case "Siege Workshop":
                        playerState.setMilitaryPoints(playerState.getMilitaryPoints() + 3);
                        break;
                    case "Fortifications":
                        playerState.setMilitaryPoints(playerState.getMilitaryPoints() + 3);
                        break;
                    default:
                        break;
                }
                break;
            case GREEN:
                switch (cardToPlay.getName()) {
                    case "Scriptorium":
                        playerState.getScience().merge(Science.TABLET, 1, Integer::sum);
                        break;
                    case "Apothecary":
                        playerState.getScience().merge(Science.COMPASS, 1, Integer::sum);
                        break;
                    case "Workshop":
                        playerState.getScience().merge(Science.GEAR, 1, Integer::sum);
                        break;
                    case "Dispensary":
                        playerState.getScience().merge(Science.COMPASS, 1, Integer::sum);
                        break;
                    case "Laboratory":
                        playerState.getScience().merge(Science.GEAR, 1, Integer::sum);
                        break;
                    case "Library":
                        playerState.getScience().merge(Science.TABLET, 1, Integer::sum);
                        break;
                    case "School":
                        playerState.getScience().merge(Science.TABLET, 1, Integer::sum);
                        break;
                    case "University":
                        playerState.getScience().merge(Science.TABLET, 1, Integer::sum);
                        break;
                    case "Study":
                        playerState.getScience().merge(Science.GEAR, 1, Integer::sum);
                        break;
                    case "Lodge":
                        playerState.getScience().merge(Science.COMPASS, 1, Integer::sum);
                        break;
                    case "Academy":
                        playerState.getScience().merge(Science.COMPASS, 1, Integer::sum);
                        break;
                    case "Observatory":
                        playerState.getScience().merge(Science.GEAR, 1, Integer::sum);
                        break;
                    default:
                        break;
                }
                break;
            case VIOLET:
                switch (cardToPlay.getName()) {
                    case "Workers Guild":
                        // TODO: 1 victory point per brown card (neighbors only)
                        break;
                    case "Craftsmens Guild":
                        // TODO: 2 victory points per grey card (neighbors only)
                        break;
                    case "Magistrates Guild":
                        // TODO: 1 victory point per blue card (neighbors only)
                        break;
                    case "Traders Guild":
                        // TODO: 1 victory point per yellow card (neighbors only)
                        break;
                    case "Spies Guild":
                        // TODO: 1 victory point per red card (neighbors only)
                        break;
                    case "Philosophers Guild":
                        // TODO: 1 victory point per green card (neighbors only)
                        break;
                    case "Shipowners Guild":
                        // TODO: 1 victory point per brown + grey + violet card (self only)
                        break;
                    case "Scientists Guild":
                        playerState.getScience().merge(Science.MUTABLE, 1, Integer::sum);
                        break;
                    case "Decorators Guild":
                        // TODO: 7 victory point if wonder fully built
                        break;
                    case "Builders Guild":
                        // TODO: 1 victory point per wonder stage (self + neighbors)
                        break;
                    default:
                        break;
                }
                break;
            default:
                // TODO: Handle other or unknown card types
                break;
        }
    }

    /**
     * Determines if a player can play a given card based on cost affordability.
     * 
     * @param playerState the player state entity
     * @param cardToPlay the card to check
     * @return true if the player can afford the card cost, false otherwise
     */
    public boolean canPlayCard(PlayerStateEntity playerState, CardEntity cardToPlay) {
        loggingService.debug("Checking if card can be played - Player: " + playerState.getUser().getUsername() + ", Card: " + cardToPlay.getName() + ", CoinCost: " + cardToPlay.getCoinCost() + ", PlayerCoins: " + playerState.getCoins(), "CardPlayManager.canPlayCard");
        //TODO: Add test if the player has already played the card earlier in the game
        if (cardToPlay.getCoinCost() == 0) { 
            boolean canAfford = canAffordCost(playerState, cardToPlay.getCost());
            loggingService.debug("Resource cost check result - CanAfford: " + canAfford + ", Player: " + playerState.getUser().getUsername(), "CardPlayManager.canPlayCard");
            return canAfford;
        } else {
            boolean canAfford = cardToPlay.getCoinCost() <= playerState.getCoins();
            loggingService.debug("Coin cost check result - CanAfford: " + canAfford + ", Player: " + playerState.getUser().getUsername(), "CardPlayManager.canPlayCard");
            return canAfford;
        }
    }

    /**
     * Determines if a player can afford a resource-based cost.
     * Considers the player's own resources, mutable resources (wildcards), and neighbor resources.
     * 
     * @param playerState the player state entity
     * @param cardCost the cost map of resources needed
     * @return true if the cost can be afforded, false otherwise
     */
    public boolean canAffordCost(PlayerStateEntity playerState, Map<Ressources, Integer> cardCost) {
        loggingService.debug("Checking resource affordability - Player: " + playerState.getUser().getUsername() + ", CardCost: " + cardCost + ", PlayerResources: " + playerState.getResources(), "CardPlayManager.canAffordCost");
        Map<Ressources, Integer> playerRessources = playerState.getResources();

        // Step 1: Calculate missing resources after using player's own resources
        Map<Ressources, Integer> missingResources = cardCost.entrySet().stream()
                .filter(entry -> entry.getKey().isRessource())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Math.max(0, entry.getValue() - playerRessources.getOrDefault(entry.getKey(), 0))
                ));
        
        // Step 2: Apply mutable resources (wildcards) to cover missing resources
        int missingBaseResources = calculateMissingResourceCount(missingResources, true)
                - playerRessources.getOrDefault(Ressources.MUTABLE_BASE, 0);
        int missingAdvancedResources = calculateMissingResourceCount(missingResources, false)
                - playerRessources.getOrDefault(Ressources.MUTABLE_ADVANCED, 0);
        
        // Apply mutable pairs to remaining missing base resources
        for (Ressources mutablePair : Ressources.values()) {
            if (mutablePair.isMutablePair()) {
                int pairCount = playerRessources.getOrDefault(mutablePair, 0);
                if (pairCount > 0 && missingBaseResources > 0) {
                    Ressources[] options = mutablePair.getPairOptions();
                    for (Ressources option : options) {
                        if (missingResources.containsKey(option) && missingResources.get(option) > 0) {
                            int usedPairs = Math.min(missingResources.get(option), pairCount);
                            missingResources.put(option, missingResources.get(option) - usedPairs);
                            missingBaseResources -= usedPairs;
                            pairCount -= usedPairs;
                            if (pairCount == 0 || missingBaseResources <= 0) break;
                        }
                    }
                }
            }
        }

        if (missingBaseResources <= 0 && missingAdvancedResources <= 0) {
            return true;
        }

        // Step 3: Check if neighbors can provide the remaining missing resources and if player can afford them
        PlayerStateEntity leftNeighbor = playerState.getLeftNeighbor();
        PlayerStateEntity rightNeighbor = playerState.getRightNeighbor();
        Map<Ressources, Integer> leftResources = leftNeighbor.getResources();
        Map<Ressources, Integer> rightResources = rightNeighbor.getResources();
        
        // Add mutable pairs as available resources from neighbors
        Map<Ressources, Integer> leftAvailableResources = new java.util.HashMap<>(leftResources);
        Map<Ressources, Integer> rightAvailableResources = new java.util.HashMap<>(rightResources);
        for (Ressources mutablePair : Ressources.values()) {
            if (mutablePair.isMutablePair()) {
                int leftPairs = leftResources.getOrDefault(mutablePair, 0);
                int rightPairs = rightResources.getOrDefault(mutablePair, 0);
                if (leftPairs > 0 || rightPairs > 0) {
                    Ressources[] options = mutablePair.getPairOptions();
                    for (Ressources option : options) {
                        leftAvailableResources.merge(option, leftPairs, Integer::sum);
                        rightAvailableResources.merge(option, rightPairs, Integer::sum);
                    }
                }
            }
        }
        
        // Get price multipliers for buying from neighbors
        int leftBasePrice = playerState.getLeftBaseRessourcePriceMultiplier();
        int rightBasePrice = playerState.getRightBaseRessourcePriceMultiplier();
        int leftAdvancedPrice = playerState.getLeftAdvancedRessourcePriceMultiplier();
        int rightAdvancedPrice = playerState.getRightAdvancedRessourcePriceMultiplier();
        
        // Calculate minimum cost to buy missing resources
        int minimumCost = 0;
        boolean canBuyAllResources = true;
        
        for (Map.Entry<Ressources, Integer> entry : missingResources.entrySet()) {
            Ressources resource = entry.getKey();
            int amountNeeded = entry.getValue();
            
            if (amountNeeded <= 0) {
                continue;
            }
            
            int availableFromLeft = leftAvailableResources.getOrDefault(resource, 0);
            int availableFromRight = rightAvailableResources.getOrDefault(resource, 0);
            int totalAvailableFromNeighbors = availableFromLeft + availableFromRight;
            
            // Check if neighbors can provide enough of this resource
            if (totalAvailableFromNeighbors < amountNeeded) {
                canBuyAllResources = false;
                break;
            }
            
            // Determine the price for this resource type
            int priceFromLeft, priceFromRight;
            if (resource.isBaseRessource()) {
                priceFromLeft = leftBasePrice;
                priceFromRight = rightBasePrice;
            } else if (resource.isAdvancedRessource()) {
                priceFromLeft = leftAdvancedPrice;
                priceFromRight = rightAdvancedPrice;
            } else {
                continue; // Skip non-resource types
            }
            
            // Find the optimal way to buy this resource (greedy approach: buy from cheaper neighbor first)
            int remaining = amountNeeded;
            if (priceFromLeft <= priceFromRight) {
                // Buy from left first (cheaper or equal)
                int buyFromLeft = Math.min(remaining, availableFromLeft);
                minimumCost += buyFromLeft * priceFromLeft;
                remaining -= buyFromLeft;
                
                // Buy remaining from right if needed
                if (remaining > 0) {
                    minimumCost += remaining * priceFromRight;
                }
            } else {
                // Buy from right first (cheaper)
                int buyFromRight = Math.min(remaining, availableFromRight);
                minimumCost += buyFromRight * priceFromRight;
                remaining -= buyFromRight;
                
                // Buy remaining from left if needed
                if (remaining > 0) {
                    minimumCost += remaining * priceFromLeft;
                }
            }
        }
        
        boolean result = canBuyAllResources && minimumCost <= playerState.getCoins();
        loggingService.debug("Affordability check result - Player: " + playerState.getUser().getUsername() + ", CanAfford: " + result + ", CanBuyAll: " + canBuyAllResources + ", MinimumCost: " + minimumCost + ", PlayerCoins: " + playerState.getCoins(), "CardPlayManager.canAffordCost");
        return result;
    }

    /**
     * Calculate the total count of missing resources for either base or advanced resources
     */
    private int calculateMissingResourceCount(Map<Ressources, Integer> missingResources, boolean isBaseResource) {
        return missingResources.entrySet().stream()
                .filter(entry -> isBaseResource ? entry.getKey().isBaseRessource() : entry.getKey().isAdvancedRessource())
                .mapToInt(Map.Entry::getValue)
                .sum();
    }

    /**
     * Processes a card discard action, moving the specified card from the player's hand to the game's discard pile.
     * The player receives 3 coins as compensation for discarding the card.
     * Note: Caller is responsible for persisting the game entity changes.
     * 
     * @param playerState the player state entity performing the action
     * @param cardToDiscard the card entity to be discarded
     * @param gameDiscard the game's discard pile to add the card to
     */
    @Transactional
    public void discardCard(PlayerStateEntity playerState, CardEntity cardToDiscard, java.util.List<CardEntity> gameDiscard) {
        loggingService.debug("Discarding card - Player: " + playerState.getUser().getUsername() + ", Card: " + cardToDiscard.getName() + ", CurrentCoins: " + playerState.getCoins(), "CardPlayManager.discardCard");
        playerState.getHand().remove(cardToDiscard);
        gameDiscard.add(cardToDiscard);
        playerState.setCoins(playerState.getCoins() + 3);
        loggingService.info("Card discarded successfully - Player: " + playerState.getUser().getUsername() + ", Card: " + cardToDiscard.getName() + ", NewCoins: " + playerState.getCoins() + ", DiscardPileSize: " + gameDiscard.size(), "CardPlayManager.discardCard");
    }
}
