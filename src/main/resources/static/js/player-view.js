// Set UI visibility based on who is viewing
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

// Show another player's view
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

// Show self view with all player info
async function showSelfView() {
  setSelfViewUI(true);
  await Promise.all([loadWonder(), loadCardBacks(), loadCoins(), loadPlayedCards()]);
  await loadDiscardedCards();
  renderHand();
}
