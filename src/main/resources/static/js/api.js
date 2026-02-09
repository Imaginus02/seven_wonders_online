function normalizePlayerState(raw = {}) {
  const normalizedPlayers = (raw.players || []).map((player) => {
    const normalizedState = player.state || {
      wonder: player.wonder || player.wonderImage || null,
      coins: player.coins ?? 0,
      playedCards: player.playedCards || [],
      cardBacks: player.cardBacks || []
    };
    return { ...player, state: normalizedState };
  });

  return {
    hasPlayedThisTurn: raw.hasPlayedThisTurn ?? false,
    hand: raw.hand || raw.cards || [],
    wonder: raw.wonder || raw.wonderImage || null,
    coins: raw.coins ?? 0,
    playedCards: raw.playedCards || [],
    cardBacks: raw.cardBacks || [],
    discarded: raw.discarded || [],
    availableActions: raw.availableActions || [],
    players: normalizedPlayers
  };
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

//     * - hasPlayedThisTurn: Whether the player has played this turn
//     * - hand: The player's current hand cards
//     * - wonder: The player's wonder image
//     * - coins: The player's coin count
//     * - playedCards: Cards the player has played
//     * - cardBacks: Cards used to build the wonder
//     * - discarded: Discard pile cards
//     * - availableActions: List of actions the player can take (play, build, discard, build_from_discard)
//     * - players: Array of other players with:
//     *   - id: Player state ID
//     *   - name: Player username
//     *   - state: Public player state (wonder, coins, playedCards, cardBacks)
//     *   - isNeighbor: Whether the player is a left or right neighbor
async function getPlayerState() {
  try {
    const response = await fetch(`/api/get-player-game-state?gameId=${gameId}`);
    const data = await response.json();
    const normalized = normalizePlayerState(data);
    console.log('[API] Player state:', normalized);
    return normalized;
  } catch (error) {
    console.error('Error fetching player state:', error);
    return normalizePlayerState({});
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
