// Fetch cards in hand from API
async function fetchCardsFromAPI() {
  try {
    const response = await fetch(`/api/hand?gameId=${gameId}`);
    const data = await response.json();
    console.log('[API] Hand cards:', data.cards);
    return data.cards;
  } catch (error) {
    console.error('Error fetching cards:', error);
    return []; // fallback to empty array
  }
}

// Fetch wonder from API
async function fetchWonderFromAPI() {
  try {
    const response = await fetch(`/api/wonder?gameId=${gameId}`);
    const data = await response.json();
    console.log('[API] Wonder:', data.wonderImage);
    return data.wonderImage;
  } catch (error) {
    console.error('Error fetching wonder:', error);
    return null; // display an error
  }
}

// Fetch card backs from API
async function fetchCardBacksFromAPI() {
  try {
    const response = await fetch(`/api/card-backs?gameId=${gameId}`);
    const data = await response.json();
    console.log('[API] Card backs:', data.cardBacks);
    return data.cardBacks; // Array of card backs for wonder cards
  } catch (error) {
    console.error('Error fetching card backs:', error);
    return []; // fallback to empty array
  }
}

// Fetch coins from API
async function fetchCoinsFromAPI() {
  try {
    const response = await fetch(`/api/coins?gameId=${gameId}`);
    const data = await response.json();
    console.log('[API] Coins:', data.coins);
    return data.coins;
  } catch (error) {
    console.error('Error fetching coins:', error);
    return 0; // fallback to zero coins
  }
}

// Fetch played cards from API
async function fetchPlayedCardsFromAPI() {
  try {
    const response = await fetch(`/api/played?gameId=${gameId}`);
    const data = await response.json();
    console.log('[API] Played cards:', data.playedCards);
    return data.playedCards;
  } catch (error) {
    console.error('Error fetching played cards:', error);
    return []; // fallback to empty array
  }
}

// Fetch discarded cards from API
async function fetchDiscardedCardsFromAPI() {
  try {
    const response = await fetch(`/api/discarded?gameId=${gameId}`);
    const data = await response.json();
    console.log('[API] Discarded cards:', data.discarded);
    return data.discarded;
  } catch (error) {
    console.error('Error fetching discarded cards:', error);
    return []; // fallback to empty array
  }
}

// Fetch players list from API
async function fetchPlayersFromAPI() {
  try {
    const response = await fetch(`/api/players?gameId=${gameId}`);
    const data = await response.json();
    console.log('[API] Players:', data.players);
    return data.players;
  } catch (error) {
    console.error('Error fetching players:', error);
    return [{ id: selfPlayerId, name: "You" }]; // fallback to mock data
  }
}

// Fetch a specific player's public state (wonder/coins/played)
async function fetchPlayerStateFromAPI(playerId) {
  try {
    const response = await fetch(`/api/player/${playerId}?gameId=${gameId}`);
    const data = await response.json();
    console.log(`[API] Player state (${playerId}):`, data);
    return { wonder: data.wonderImage, coins: data.coins, playedCards: data.playedCards, cardBacks: data.cardBacks };
  } catch (error) {
    console.error('Error fetching player state:', error);
    // Fallback to mock data
    if (playerId === selfPlayerId) {
      return { wonder: null, coins: 0, playedCards: [], cardBacks: [] };
    }
    const found = otherPlayersMock.find((p) => p.id === playerId);
    if (found) {
      return { wonder: found.wonder, coins: found.coins, playedCards: found.playedCards, cardBacks: found.cardBacks };
    }
    return { wonder: null, coins: 0, playedCards: [], cardBacks: [] };
  }
}

// Send card action to API
async function sendCardAction(action, cardName) {
  try {
    const { token, paramName } = getCsrfTokenAndParam();
    
    // Build URL with CSRF token as query parameter
    let url = '/api/card-action';
    if (token) {
      url += `?${paramName}=${encodeURIComponent(token)}`;
      console.log(`[CSRF] Using query parameter '${paramName}' with token`);
    }
    
    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ action, card: cardName, gameId: parseInt(gameId) })
    });
    const data = await response.json();
    console.log(`[API] Card action response (${action} - ${cardName}):`, data);
    return data;
  } catch (error) {
    console.error('Error sending card action:', error);
    return { success: false };
  }
}

// Fetch available actions from API
async function fetchAvailableActionsFromAPI() {
  try {
    const response = await fetch(`/api/available-actions?gameId=${gameId}`);
    const data = await response.json();
    console.log('[API] Available actions:', data.availableActions);
    return data.availableActions;
  } catch (error) {
    console.error('Error fetching available actions:', error);
    return [];
  }
}

// Send select discard card to API
async function sendSelectDiscardCard(cardId, action) {
  try {
    const { token, paramName } = getCsrfTokenAndParam();
    
    // Build URL with CSRF token as query parameter
    let url = `/api/select-discard-card?gameId=${gameId}&cardId=${cardId}&action=${action}`;
    if (token) {
      url += `&${paramName}=${encodeURIComponent(token)}`;
      console.log(`[CSRF] Using query parameter '${paramName}' with token`);
    }
    
    const response = await fetch(url, {
      method: 'POST'
    });
    const data = await response.json();
    console.log(`[API] Select discard card response (cardId: ${cardId}, action: ${action}):`, data);
    return data;
  } catch (error) {
    console.error('Error selecting discard card:', error);
    return { error: 'Failed to select card' };
  }
}
