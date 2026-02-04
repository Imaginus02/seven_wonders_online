// State for card selection
let cards = [];
let selectedIndex = null;

// DOM elements for card handling
const handEl = document.getElementById("hand");
const overlayEl = document.getElementById("selectedOverlay");
const selectedImgEl = document.getElementById("selectedImage");
const emptyStateEl = document.getElementById("emptyState");

// Show error message to user
function showErrorMessage(message) {
  // Create or get error message element
  let errorEl = document.getElementById('errorMessage');
  
  if (!errorEl) {
    errorEl = document.createElement('div');
    errorEl.id = 'errorMessage';
    errorEl.className = 'error-message';
    document.body.appendChild(errorEl);
    
    // Add CSS if not already present
    if (!document.getElementById('errorMessageStyles')) {
      const style = document.createElement('style');
      style.id = 'errorMessageStyles';
      style.textContent = `
        .error-message {
          position: fixed;
          top: 20px;
          left: 50%;
          transform: translateX(-50%);
          background-color: #f44336;
          color: white;
          padding: 16px 24px;
          border-radius: 8px;
          box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
          font-family: 'Space Grotesk', sans-serif;
          font-size: 16px;
          font-weight: 600;
          z-index: 10001;
          opacity: 0;
          transition: opacity 0.3s ease;
        }
        
        .error-message.show {
          opacity: 1;
        }
      `;
      document.head.appendChild(style);
    }
  }
  
  // Set message and show
  errorEl.textContent = message;
  errorEl.classList.add('show');
  
  // Hide after 3 seconds
  setTimeout(() => {
    errorEl.classList.remove('show');
  }, 3000);
}

// Select a card from hand
function selectCard(index) {
  selectedIndex = index;
  selectedImgEl.src = toSrc(cards[index]);
  overlayEl.classList.add("active");
}

// Clear current hand card selection
function clearHandSelection() {
  selectedIndex = null;
  overlayEl.classList.remove("active");
}

// Play selected card
async function playHandCard() {
  if (selectedIndex === null) return;
  const cardName = cards[selectedIndex];
  const result = await sendCardAction('play', cardName);
  
  if (result.success) {
    cards.splice(selectedIndex, 1);
    clearHandSelection();
    renderHand();
  } else {
    clearHandSelection();
    const errorMsg = result.message || "This card can't be played";
    showErrorMessage(errorMsg);
  }
}

// Build wonder with selected card
async function buildWonderWithHandCard() {
  if (selectedIndex === null) return;
  const cardName = cards[selectedIndex];
  const result = await sendCardAction('build', cardName);
  
  if (result.success) {
    cards.splice(selectedIndex, 1);
    clearHandSelection();
    renderHand();
  } else {
    clearHandSelection();
    const errorMsg = result.message || "Can't build wonder with this card";
    showErrorMessage(errorMsg);
  }
}

// Discard selected card
async function discardCard() {
  if (selectedIndex === null) return;
  const cardName = cards[selectedIndex];
  const result = await sendCardAction('discard', cardName);
  
  if (result.success) {
    cards.splice(selectedIndex, 1);
    clearHandSelection();
    renderHand();
  } else {
    clearHandSelection();
    const errorMsg = result.message || "Can't discard this card";
    showErrorMessage(errorMsg);
  }
}

// Variables for discard card selection
let selectedDiscardCardId = null;
let selectedDiscardCardImage = null;

// Select a card from the discard pile
function selectDiscardCard(cardId, cardImage) {
  selectedDiscardCardId = cardId;
  selectedDiscardCardImage = cardImage;
  selectedImgEl.src = toSrc(cardImage);
  overlayEl.classList.add("active");
  
  // Show both play and build buttons for discard cards
  const playBtn = document.getElementById("playButton");
  const buildBtn = document.getElementById("buildButton");
  const discardBtn = document.getElementById("discardButton");
  
  // Update button text to indicate this is from discard
  playBtn.textContent = "Play from Discard";
  buildBtn.textContent = "Build from Discard";
  playBtn.style.display = "block";
  buildBtn.style.display = "block";
  discardBtn.style.display = "none";
}

// Play selected card from discard pile
async function playCardFromDiscard() {
  if (selectedDiscardCardId === null) return;
  
  const result = await sendSelectDiscardCard(selectedDiscardCardId, 'play');
  
  if (result.success || !result.error) {
    clearDiscardSelection();
    // Reload game data to reflect changes
    if (typeof reloadAllGameData === 'function') {
      await reloadAllGameData();
    }
  } else {
    clearDiscardSelection();
    const errorMsg = result.error || "Failed to play card from discard";
    showErrorMessage(errorMsg);
  }
}

// Build wonder with selected card from discard pile
async function buildWonderFromDiscard() {
  if (selectedDiscardCardId === null) return;
  
  const result = await sendSelectDiscardCard(selectedDiscardCardId, 'build');
  
  if (result.success || !result.error) {
    clearDiscardSelection();
    // Reload game data to reflect changes
    if (typeof reloadAllGameData === 'function') {
      await reloadAllGameData();
    }
  } else {
    clearDiscardSelection();
    const errorMsg = result.error || "Failed to build wonder from discard";
    showErrorMessage(errorMsg);
  }
}

// Clear discard card selection
function clearDiscardSelection() {
  selectedDiscardCardId = null;
  selectedDiscardCardImage = null;
  clearHandSelection();
  
  // Reset button text and visibility
  const playBtn = document.getElementById("playButton");
  const buildBtn = document.getElementById("buildButton");
  const discardBtn = document.getElementById("discardButton");
  
  playBtn.textContent = "Play";
  buildBtn.textContent = "Build";
  playBtn.style.display = "block";
  buildBtn.style.display = "block";
  discardBtn.style.display = "block";
}

// Override playCard to handle both hand and discard cards
async function playCard() {
  if (selectedDiscardCardId !== null) {
    await playCardFromDiscard();
  } else {
    await playHandCard();
  }
}

// Override buildWonder to handle both hand and discard cards
async function buildWonder() {
  if (selectedDiscardCardId !== null) {
    await buildWonderFromDiscard();
  } else {
    await buildWonderWithHandCard();
  }
}

// Override clearSelection to handle discard cards too
function clearSelection() {
  if (selectedDiscardCardId !== null) {
    clearDiscardSelection();
  } else {
    clearHandSelection();
  }
}
