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

// Clear current card selection
function clearSelection() {
  selectedIndex = null;
  overlayEl.classList.remove("active");
}

// Play selected card
async function playCard() {
  if (selectedIndex === null) return;
  const cardName = cards[selectedIndex];
  const result = await sendCardAction('play', cardName);
  
  if (result.success) {
    cards.splice(selectedIndex, 1);
    clearSelection();
    renderHand();
  } else {
    clearSelection();
    const errorMsg = result.message || "This card can't be played";
    showErrorMessage(errorMsg);
  }
}

// Build wonder with selected card
async function buildWonder() {
  if (selectedIndex === null) return;
  const cardName = cards[selectedIndex];
  const result = await sendCardAction('build', cardName);
  
  if (result.success) {
    cards.splice(selectedIndex, 1);
    clearSelection();
    renderHand();
  } else {
    clearSelection();
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
    clearSelection();
    renderHand();
  } else {
    clearSelection();
    const errorMsg = result.message || "Can't discard this card";
    showErrorMessage(errorMsg);
  }
}
