// Load wonder and render it
async function loadWonder() {
  const wonderImage = await fetchWonderFromAPI();
  const wonderImgEl = document.getElementById("wonderImage");
  wonderImgEl.src = wonderBase + (wonderImage || "placeholder.png");
}

// Load card backs and render them
async function loadCardBacks() {
  const cardBackImages = await fetchCardBacksFromAPI();
  renderCardBacks(cardBackImages);
}

// Load coins and render them
async function loadCoins() {
  const coinsValue = await fetchCoinsFromAPI();
  const coinsIconEl = document.getElementById("coinsIcon");
  const coinsValueEl = document.getElementById("coinsValue");
  coinsIconEl.src = coinAsset;
  coinsValueEl.textContent = coinsValue;
}

// Load played cards and render them
async function loadPlayedCards() {
  const playedCards = await fetchPlayedCardsFromAPI();
  const gridEl = document.getElementById("playedGrid");
  const emptyEl = document.getElementById("playedEmpty");
  gridEl.innerHTML = "";

  if (!playedCards || playedCards.length === 0) {
    emptyEl.hidden = false;
    return;
  }

  emptyEl.hidden = true;
  playedCards.forEach((card) => {
    const img = document.createElement("img");
    img.src = toSrc(card);
    img.alt = card;
    gridEl.appendChild(img);
  });
}

// Load discarded cards and render them
async function loadDiscardedCards() {
  const discardedCards = await fetchDiscardedCardsFromAPI();
  renderDiscarded(discardedCards);
}

// Reload all game data from API
async function reloadAllGameData() {
  console.log('[Loader] Reloading all game data...');
  
  try {
    // Reload hand cards
    cards = await fetchCardsFromAPI();
    renderHand();
    
    // Reload all parallel data
    await Promise.all([
      loadWonder(),
      loadCardBacks(),
      loadCoins(),
      loadPlayedCards()
    ]);
    
    // Reload discarded cards
    await loadDiscardedCards();
    
    // Reload players list
    const players = await fetchPlayersFromAPI();
    renderPlayersList(players);
    
    console.log('[Loader] All game data reloaded successfully');
  } catch (error) {
    console.error('[Loader] Error reloading game data:', error);
  }
}
