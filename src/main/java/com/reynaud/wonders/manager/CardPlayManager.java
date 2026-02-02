package com.reynaud.wonders.manager;

import com.reynaud.wonders.dao.EffectDAO;
import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.entity.EffectEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.model.Ressources;
import com.reynaud.wonders.service.LoggingService;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manager responsible for handling card play mechanics.
 * Determines if a card can be played and executes the play action.
 */
@Component
public class CardPlayManager {

    private final LoggingService loggingService;

    //TODO: DAO shouldn't be accessed by manager, use service instead
    private final EffectDAO effectDAO;

    public CardPlayManager(LoggingService loggingService, EffectDAO effectDAO) {
        this.loggingService = loggingService;
        this.effectDAO = effectDAO;
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
     * Applies the effect of a played card using the effect system.
     * Maps card name to effect ID and applies via EffectExecutorService.
     *
     * @param playerState the player state entity
     * @param cardToPlay the card being played
     */
    private void applyCardEffect(PlayerStateEntity playerState, CardEntity cardToPlay) {
        String effectId = mapCardToEffectId(cardToPlay.getName());
        
        if (effectId == null) {
            loggingService.warning("No effect mapping for card: " + cardToPlay.getName(), "CardPlayManager.applyCardEffect");
            return;
        }
        
        EffectEntity effect = effectDAO.findByEffectId(effectId).orElse(null);
        if (effect == null) {
            loggingService.error("Effect not found: " + effectId + " for card: " + cardToPlay.getName(), "CardPlayManager.applyCardEffect");
            return;
        }

        playerState.addPendingEffect(effect);
        loggingService.debug("Added pending effect - Player: " + playerState.getUser().getUsername() +
                        ", Effect: " + effectId + ", Timing: " + effect.getTiming(),
                "CardPlayManager.applyCardEffect");
    }
    
    /**
     * Maps card name to effect ID.
     * 
     * @param cardName the name of the card
     * @return the effect ID, or null if no mapping exists
     */
    private String mapCardToEffectId(String cardName) {
        return switch (cardName) {
            // BROWN CARDS
            case "Lumber Yard" -> "LUMBER_YARD_WOOD_1";
            case "Clay Pool" -> "CLAY_POOL_BRICK_1";
            case "Stone Pit" -> "STONE_PIT_STONE_1";
            case "Ore Vein" -> "ORE_VEIN_ORE_1";
            case "Timber Yard" -> "TIMBER_YARD_STONE_WOOD_1";
            case "Clay Pit" -> "CLAY_PIT_ORE_BRICK_1";
            case "Sawmill" -> "SAWMILL_WOOD_2";
            case "Foundry" -> "FOUNDRY_ORE_2";
            case "Quarry" -> "QUARRY_STONE_2";
            case "Brickyard" -> "BRICKYARD_BRICK_2";
            
            // GREY CARDS
            case "Glassworks" -> "GLASSWORKS_GLASS_1";
            case "Press" -> "PRESS_PAPER_1";
            case "Loom" -> "LOOM_TEXTILE_1";
            
            // BLUE CARDS
            case "Altar" -> "ALTAR_VP_2";
            case "Theater" -> "THEATER_VP_2";
            case "Bath" -> "BATH_VP_3";
            case "Courthouse" -> "COURTHOUSE_VP_4";
            case "Temple" -> "TEMPLE_VP_3";
            case "Statue" -> "STATUE_VP_4";
            case "Aqueduct" -> "AQUEDUCT_VP_5";
            case "Gardens" -> "GARDENS_VP_5";
            case "Senate" -> "SENATE_VP_6";
            case "Town Hall" -> "TOWN_HALL_VP_6";
            case "Pantheon" -> "PANTHEON_VP_7";
            case "Palace" -> "PALACE_VP_8";
            
            // YELLOW CARDS
            case "West Trading Post" -> "WEST_TRADING_POST";
            case "East Trading Post" -> "EAST_TRADING_POST";
            case "Marketplace" -> "MARKETPLACE";
            case "Caravansery" -> "CARAVANSERY_MUTABLE_BASE_1";
            case "Forum" -> "FORUM_MUTABLE_ADVANCED_1";
            case "Vineyard" -> "VINEYARD_COINS_BROWN";
            case "Lighthouse" -> "LIGHTHOUSE_COINS_YELLOW";
            case "Haven" -> "HAVEN_COINS_BROWN";
            case "Chamber of Commerce" -> "CHAMBER_OF_COMMERCE_COINS_YELLOW";
            
            // RED CARDS
            case "Guard Tower" -> "GUARD_TOWER_MILITARY_1";
            case "Barracks" -> "BARRACKS_MILITARY_1";
            case "Stockade" -> "STOCKADE_MILITARY_1";
            case "Stables" -> "STABLES_MILITARY_2";
            case "Archery Range" -> "ARCHERY_RANGE_MILITARY_2";
            case "Walls" -> "WALLS_MILITARY_2";
            case "Arsenal" -> "ARSENAL_MILITARY_3";
            case "Siege Workshop" -> "SIEGE_WORKSHOP_MILITARY_3";
            case "Fortifications" -> "FORTIFICATIONS_MILITARY_3";
            
            // GREEN CARDS
            case "Scriptorium" -> "SCRIPTORIUM_TABLET_1";
            case "Apothecary" -> "APOTHECARY_COMPASS_1";
            case "Workshop" -> "WORKSHOP_GEAR_1";
            case "Dispensary" -> "DISPENSARY_COMPASS_1";
            case "Laboratory" -> "LABORATORY_GEAR_1";
            case "Library" -> "LIBRARY_TABLET_1";
            case "School" -> "SCHOOL_TABLET_1";
            case "University" -> "UNIVERSITY_TABLET_1";
            case "Study" -> "STUDY_GEAR_1";
            case "Lodge" -> "LODGE_COMPASS_1";
            case "Academy" -> "ACADEMY_COMPASS_1";
            case "Observatory" -> "OBSERVATORY_GEAR_1";
            
            // VIOLET CARDS
            case "Workers Guild" -> "WORKERS_GUILD_VP_BROWN";
            case "Craftsmens Guild" -> "CRAFTSMENS_GUILD_VP_GREY";
            case "Magistrates Guild" -> "MAGISTRATES_GUILD_VP_BLUE";
            case "Traders Guild" -> "TRADERS_GUILD_VP_YELLOW";
            case "Spies Guild" -> "SPIES_GUILD_VP_RED";
            case "Philosophers Guild" -> "PHILOSOPHERS_GUILD_VP_GREEN";
            case "Shipowners Guild" -> "SHIPOWNERS_GUILD_VP_BROWN_GREY_VIOLET";
            case "Scientists Guild" -> "SCIENTISTS_GUILD_MUTABLE";
            case "Decorators Guild" -> "DECORATORS_GUILD_VP_WONDER";
            case "Builders Guild" -> "BUILDERS_GUILD_VP_WONDER";
            
            default -> null;
        };
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
        loggingService.debug("Step1 - Missing resources after own resources - Missing: " + missingResources + ", PlayerResources: " + playerRessources, "CardPlayManager.canAffordCost");
        
        // Step 2: Apply mutable resources (wildcards) to cover missing resources
        int missingBaseResources = calculateMissingResourceCount(missingResources, true)
                - playerRessources.getOrDefault(Ressources.MUTABLE_BASE, 0);
        int missingAdvancedResources = calculateMissingResourceCount(missingResources, false)
                - playerRessources.getOrDefault(Ressources.MUTABLE_ADVANCED, 0);
        loggingService.debug("Step2 - Mutable resources applied (before pairs) - MissingBase: " + missingBaseResources + ", MissingAdvanced: " + missingAdvancedResources + ", MutableBase: " + playerRessources.getOrDefault(Ressources.MUTABLE_BASE, 0) + ", MutableAdvanced: " + playerRessources.getOrDefault(Ressources.MUTABLE_ADVANCED, 0), "CardPlayManager.canAffordCost");
        
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
                            loggingService.debug("Step2 - Applied mutable pair - Pair: " + mutablePair + ", Option: " + option + ", Used: " + usedPairs + ", RemainingPairCount: " + pairCount + ", MissingBaseNow: " + missingBaseResources + ", MissingResourcesNow: " + missingResources, "CardPlayManager.canAffordCost");
                            if (pairCount == 0 || missingBaseResources <= 0) break;
                        }
                    }
                }
            }
        }

        loggingService.debug("Step2 - After mutable pairs - MissingResources: " + missingResources + ", MissingBase: " + missingBaseResources + ", MissingAdvanced: " + missingAdvancedResources, "CardPlayManager.canAffordCost");

        if (missingBaseResources <= 0 && missingAdvancedResources <= 0) {
            return true;
        }

        // Step 3: Check if neighbors can provide the remaining missing resources and if player can afford them
        PlayerStateEntity leftNeighbor = playerState.getLeftNeighbor();
        PlayerStateEntity rightNeighbor = playerState.getRightNeighbor();
        Map<Ressources, Integer> leftResources = leftNeighbor.getResources();
        Map<Ressources, Integer> rightResources = rightNeighbor.getResources();
        loggingService.debug("Step3 - Neighbor resources - Left: " + leftResources + ", Right: " + rightResources, "CardPlayManager.canAffordCost");
        
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
        loggingService.debug("Step3 - Neighbor available resources (with pairs) - LeftAvailable: " + leftAvailableResources + ", RightAvailable: " + rightAvailableResources, "CardPlayManager.canAffordCost");
        
        // Get price multipliers for buying from neighbors
        int leftBasePrice = playerState.getLeftBaseRessourcePriceMultiplier();
        int rightBasePrice = playerState.getRightBaseRessourcePriceMultiplier();
        int leftAdvancedPrice = playerState.getLeftAdvancedRessourcePriceMultiplier();
        int rightAdvancedPrice = playerState.getRightAdvancedRessourcePriceMultiplier();
        loggingService.debug("Step3 - Price multipliers - LeftBase: " + leftBasePrice + ", RightBase: " + rightBasePrice + ", LeftAdvanced: " + leftAdvancedPrice + ", RightAdvanced: " + rightAdvancedPrice, "CardPlayManager.canAffordCost");
        
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

            loggingService.debug("Step3 - Resource availability - Resource: " + resource + ", Needed: " + amountNeeded + ", LeftAvailable: " + availableFromLeft + ", RightAvailable: " + availableFromRight + ", TotalAvailable: " + totalAvailableFromNeighbors, "CardPlayManager.canAffordCost");
            
            // Check if neighbors can provide enough of this resource
            if (totalAvailableFromNeighbors < amountNeeded) {
                canBuyAllResources = false;
                loggingService.debug("Step3 - Not enough resources from neighbors - Resource: " + resource + ", Needed: " + amountNeeded + ", TotalAvailable: " + totalAvailableFromNeighbors, "CardPlayManager.canAffordCost");
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

            loggingService.debug("Step3 - Resource pricing - Resource: " + resource + ", PriceLeft: " + priceFromLeft + ", PriceRight: " + priceFromRight, "CardPlayManager.canAffordCost");
            
            // Find the optimal way to buy this resource (greedy approach: buy from cheaper neighbor first)
            int remaining = amountNeeded;
            if (priceFromLeft <= priceFromRight) {
                // Buy from left first (cheaper or equal)
                int buyFromLeft = Math.min(remaining, availableFromLeft);
                minimumCost += buyFromLeft * priceFromLeft;
                remaining -= buyFromLeft;

                loggingService.debug("Step3 - Buy plan - Resource: " + resource + ", BuyFromLeft: " + buyFromLeft + ", Remaining: " + remaining + ", MinimumCost: " + minimumCost, "CardPlayManager.canAffordCost");
                
                // Buy remaining from right if needed
                if (remaining > 0) {
                    minimumCost += remaining * priceFromRight;
                    loggingService.debug("Step3 - Buy plan - Resource: " + resource + ", BuyFromRight: " + remaining + ", MinimumCost: " + minimumCost, "CardPlayManager.canAffordCost");
                }
            } else {
                // Buy from right first (cheaper)
                int buyFromRight = Math.min(remaining, availableFromRight);
                minimumCost += buyFromRight * priceFromRight;
                remaining -= buyFromRight;

                loggingService.debug("Step3 - Buy plan - Resource: " + resource + ", BuyFromRight: " + buyFromRight + ", Remaining: " + remaining + ", MinimumCost: " + minimumCost, "CardPlayManager.canAffordCost");
                
                // Buy remaining from left if needed
                if (remaining > 0) {
                    minimumCost += remaining * priceFromLeft;
                    loggingService.debug("Step3 - Buy plan - Resource: " + resource + ", BuyFromLeft: " + remaining + ", MinimumCost: " + minimumCost, "CardPlayManager.canAffordCost");
                }
            }
        }
        
        boolean result = canBuyAllResources && minimumCost <= playerState.getCoins();
        loggingService.debug("Step3 - Final cost evaluation - CanBuyAll: " + canBuyAllResources + ", MinimumCost: " + minimumCost + ", PlayerCoins: " + playerState.getCoins() + ", Result: " + result, "CardPlayManager.canAffordCost");
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
