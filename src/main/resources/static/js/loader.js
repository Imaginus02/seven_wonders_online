let lastPlayerState = null;

function applyPlayerState(state) {
  lastPlayerState = state || {};

  cards = lastPlayerState.hand || [];
  renderHand();

  const wonderImage = lastPlayerState.wonder || "placeholder.png";
  renderWonder(wonderImage);
  renderCardBacks(lastPlayerState.cardBacks || []);
  renderCoins(lastPlayerState.coins ?? 0);
  renderPlayed(lastPlayerState.playedCards || []);

  currentDiscardedCards = lastPlayerState.discarded || [];
  renderDiscarded(currentDiscardedCards, canSelectDiscard);

  renderPlayersList(lastPlayerState.players || []);
}

// Load wonder and render it
async function loadWonder(state = lastPlayerState) {
  const wonderImage = (state && state.wonder) || "placeholder.png";
  renderWonder(wonderImage);
}

// Load card backs and render them
async function loadCardBacks(state = lastPlayerState) {
  renderCardBacks((state && state.cardBacks) || []);
}

// Load coins and render them
async function loadCoins(state = lastPlayerState) {
  renderCoins(state && typeof state.coins === 'number' ? state.coins : 0);
}

// Load played cards and render them
async function loadPlayedCards(state = lastPlayerState) {
  renderPlayed((state && state.playedCards) || []);
}

// Load discarded cards and render them
let currentDiscardedCards = [];
let canSelectDiscard = false;

async function loadDiscardedCards(state = lastPlayerState) {
  currentDiscardedCards = (state && state.discarded) || [];
  renderDiscarded(currentDiscardedCards, canSelectDiscard);
}

// Enable or disable discard card selection
function enableDiscardCardSelection(enabled) {
  console.log('[Loader] Discard card selection enabled:', enabled);
  canSelectDiscard = enabled;
  // Re-render discarded cards with updated selection state
  renderDiscarded(currentDiscardedCards, canSelectDiscard);
}

// Reload all game data from API
async function reloadAllGameData() {
  console.log('[Loader] Reloading all game data...');
  
  try {
    const state = await getPlayerState();
    applyPlayerState(state);
    console.log('[Loader] All game data reloaded successfully');
  } catch (error) {
    console.error('[Loader] Error reloading game data:', error);
  }
}
