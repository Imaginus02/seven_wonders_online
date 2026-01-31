// Initialize the application
async function init() {
  // Load and render initial game state
  cards = await fetchCardsFromAPI();
  renderHand();
  await Promise.all([loadWonder(), loadCardBacks(), loadCoins(), loadPlayedCards()]);
  await loadDiscardedCards();
  
  // Load and render players list
  const players = await fetchPlayersFromAPI();
  renderPlayersList(players);

  // Setup button event listeners
  const playBtn = document.getElementById("playButton");
  const buildBtn = document.getElementById("buildButton");
  const discardBtn = document.getElementById("discardButton");
  const cancelBtn = document.getElementById("cancelButton");
  const backBtn = document.getElementById("backToSelf");
  const trashBtn = document.getElementById("trashButton");
  const closeTrash = document.getElementById("closeTrash");
  const trashModal = document.getElementById("trashModal");
  const trashBackdrop = document.getElementById("trashBackdrop");

  // Card action listeners
  playBtn.addEventListener("click", playCard);
  buildBtn.addEventListener("click", buildWonder);
  discardBtn.addEventListener("click", discardCard);
  cancelBtn.addEventListener("click", clearSelection);

  // Player view listeners
  backBtn.addEventListener("click", showSelfView);

  // Trash modal listeners
  const openModal = () => { trashModal.classList.add("active"); };
  const closeModal = () => { trashModal.classList.remove("active"); };
  trashBtn.addEventListener("click", openModal);
  closeTrash.addEventListener("click", closeModal);
  trashBackdrop.addEventListener("click", closeModal);

  // Start turn blocker polling
  startTurnPolling();
}

// Start the application when DOM is ready
init();
