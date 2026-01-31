const assetBase = "/assets/cards/";
const wonderBase = "/assets/wonders/";
const coinAsset = "/assets/tokens/coin.png";
const selfPlayerId = "self";

// Get gameId from URL parameter or default to 1 for testing
const urlParams = new URLSearchParams(window.location.search);
const gameId = urlParams.get('gameId') || '1';


// Function to fetch cards in hand from API
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

// Function to fetch wonder from API
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

// Function to fetch card backs from API
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

// Function to fetch coins from API
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

// Function to fetch played cards from API
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

// Function to fetch discarded cards from API
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

// Function to fetch players list from API
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

// Function to fetch a specific player's public state (wonder/coins/played)
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

async function loadWonder() {
  const wonderImage = await fetchWonderFromAPI();
  const wonderImgEl = document.getElementById("wonderImage");
  wonderImgEl.src = wonderBase + (wonderImage || "placeholder.png");
}

async function loadCardBacks() {
  const cardBackImages = await fetchCardBacksFromAPI();
  renderCardBacks(cardBackImages);
}

async function loadCoins() {
  const coinsValue = await fetchCoinsFromAPI();
  const coinsIconEl = document.getElementById("coinsIcon");
  const coinsValueEl = document.getElementById("coinsValue");
  coinsIconEl.src = coinAsset;
  coinsValueEl.textContent = coinsValue;
}

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

async function loadDiscardedCards() {
  const discardedCards = await fetchDiscardedCardsFromAPI();
  renderDiscarded(discardedCards);
}

function renderCoins(value) {
  const coinsIconEl = document.getElementById("coinsIcon");
  const coinsValueEl = document.getElementById("coinsValue");
  coinsIconEl.src = coinAsset;
  coinsValueEl.textContent = value;
}

function renderWonder(image) {
  const wonderImgEl = document.getElementById("wonderImage");
  wonderImgEl.src = wonderBase + image;
}

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

function setSelfViewUI(isSelf) {
  const handSection = document.getElementById("hand");
  const backBtn = document.getElementById("backToSelf");
  const viewHint = document.getElementById("viewHint");
  const overlayEl = document.getElementById("selectedOverlay");

  handSection.style.display = isSelf ? "flex" : "none";
  backBtn.hidden = isSelf;
  viewHint.textContent = isSelf
    ? "Tap a card to inspect, then play or cancel."
    : "Viewing another player (no hand shown).";

  if (!isSelf) {
    overlayEl.classList.remove("active");
  }
}

async function showOtherPlayer(playerId) {
  if (playerId === selfPlayerId) {
    return showSelfView();
  }
  setSelfViewUI(false);
  const state = await fetchPlayerStateFromAPI(playerId);
  renderWonder(state.wonder);
  renderCoins(state.coins);
  renderPlayed(state.playedCards);
  renderCardBacks(state.cardBacks);
  renderDiscarded([]); // Hide discard contents when viewing others (not visible)
}

async function showSelfView() {
  setSelfViewUI(true);
  await Promise.all([loadWonder(), loadCardBacks(), loadCoins(), loadPlayedCards()]);
  await loadDiscardedCards();
  renderHand();
}

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

let cards = [];
let selectedIndex = null;

const handEl = document.getElementById("hand");
const overlayEl = document.getElementById("selectedOverlay");
const selectedImgEl = document.getElementById("selectedImage");
const emptyStateEl = document.getElementById("emptyState");
const playBtn = document.getElementById("playButton");
const buildBtn = document.getElementById("buildButton");
const discardBtn = document.getElementById("discardButton");
const cancelBtn = document.getElementById("cancelButton");

// Function to get CSRF token and parameter name from meta tags
function getCsrfTokenAndParam() {
  const tokenMeta = document.querySelector('meta[name="_csrf"]');
  const paramMeta = document.querySelector('meta[name="_csrf_parameter_name"]');
  
  const token = tokenMeta ? tokenMeta.getAttribute('content') : '';
  const paramName = paramMeta ? paramMeta.getAttribute('content') : '_csrf';
  
  return { token, paramName };
}

// Function to send card action to API
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

function toSrc(cardFile) {
  return encodeURI(assetBase + cardFile);
}

function renderHand() {
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

function selectCard(index) {
  selectedIndex = index;
  selectedImgEl.src = toSrc(cards[index]);
  overlayEl.classList.add("active");
}

function clearSelection() {
  selectedIndex = null;
  overlayEl.classList.remove("active");
}

async function playCard() {
  if (selectedIndex === null) return;
  const cardName = cards[selectedIndex];
  await sendCardAction('play', cardName);
  cards.splice(selectedIndex, 1);
  clearSelection();
  renderHand();
}

async function buildWonder() {
  if (selectedIndex === null) return;
  const cardName = cards[selectedIndex];
  await sendCardAction('build', cardName);
  cards.splice(selectedIndex, 1);
  clearSelection();
  renderHand();
}

async function discardCard() {
  if (selectedIndex === null) return;
  const cardName = cards[selectedIndex];
  await sendCardAction('discard', cardName);
  cards.splice(selectedIndex, 1);
  clearSelection();
  renderHand();
}

playBtn.addEventListener("click", playCard);
buildBtn.addEventListener("click", buildWonder);
discardBtn.addEventListener("click", discardCard);
cancelBtn.addEventListener("click", clearSelection);

async function init() {
  cards = await fetchCardsFromAPI();
  renderHand();
  await Promise.all([loadWonder(), loadCardBacks(), loadCoins(), loadPlayedCards()]);
  await loadDiscardedCards();
  const players = await fetchPlayersFromAPI();
  renderPlayersList(players);

  const backBtn = document.getElementById("backToSelf");
  backBtn.addEventListener("click", showSelfView);

  const trashBtn = document.getElementById("trashButton");
  const closeTrash = document.getElementById("closeTrash");
  const trashModal = document.getElementById("trashModal");
  const trashBackdrop = document.getElementById("trashBackdrop");
  const openModal = () => { trashModal.classList.add("active"); };
  const closeModal = () => { trashModal.classList.remove("active"); };
  trashBtn.addEventListener("click", openModal);
  closeTrash.addEventListener("click", closeModal);
  trashBackdrop.addEventListener("click", closeModal);
}

init();
