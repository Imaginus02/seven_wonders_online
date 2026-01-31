// Helper to convert card file to image source
function toSrc(cardFile) {
  return encodeURI(assetBase + cardFile);
}

// Render coins display
function renderCoins(value) {
  const coinsIconEl = document.getElementById("coinsIcon");
  const coinsValueEl = document.getElementById("coinsValue");
  coinsIconEl.src = coinAsset;
  coinsValueEl.textContent = value;
}

// Render wonder image
function renderWonder(image) {
  const wonderImgEl = document.getElementById("wonderImage");
  wonderImgEl.src = wonderBase + image;
}

// Render card backs
function renderCardBacks(cardBackImages = []) {
  const container = document.getElementById("cardBacksContainer");
  container.innerHTML = ""; // Clear existing content
  
  // Only create images for actual card backs from API
  cardBackImages.forEach((cardBack, index) => {
    const img = document.createElement("img");
    img.className = "card-back";
    img.alt = `Card back ${index + 1}`;
    img.src = "/assets/cards/back/" + cardBack;
    container.appendChild(img);
  });
}

// Render played cards
function renderPlayed(cards) {
  const gridEl = document.getElementById("playedGrid");
  const emptyEl = document.getElementById("playedEmpty");
  gridEl.innerHTML = "";
  if (!cards || cards.length === 0) {
    emptyEl.hidden = false;
    return;
  }
  emptyEl.hidden = true;
  cards.forEach((card) => {
    const img = document.createElement("img");
    img.src = toSrc(card);
    img.alt = card;
    gridEl.appendChild(img);
  });
}

// Render discarded cards
function renderDiscarded(cards) {
  const gridEl = document.getElementById("discardGrid");
  const emptyEl = document.getElementById("discardEmpty");
  gridEl.innerHTML = "";
  if (!cards || cards.length === 0) {
    emptyEl.hidden = false;
    return;
  }
  emptyEl.hidden = true;
  cards.forEach((card) => {
    const img = document.createElement("img");
    img.src = toSrc(card);
    img.alt = card;
    gridEl.appendChild(img);
  });
}

// Render hand cards
function renderHand() {
  const handEl = document.getElementById("hand");
  const emptyStateEl = document.getElementById("emptyState");
  
  handEl.innerHTML = "";
  emptyStateEl.hidden = cards.length > 0;

  cards.forEach((card, index) => {
    const btn = document.createElement("button");
    btn.className = "card-button";
    btn.title = `Select ${card}`;
    btn.addEventListener("click", () => selectCard(index));

    const img = document.createElement("img");
    img.src = toSrc(card);
    img.alt = card;
    btn.appendChild(img);
    handEl.appendChild(btn);
  });
}

// Render players list
function renderPlayersList(players) {
  const listEl = document.getElementById("playersList");
  listEl.innerHTML = "";
  players.forEach((p) => {
    const btn = document.createElement("button");
    btn.className = "player-chip";
    btn.textContent = p.name;
    btn.dataset.playerId = p.id;
    btn.addEventListener("click", () => showOtherPlayer(p.id));
    listEl.appendChild(btn);
  });
}
