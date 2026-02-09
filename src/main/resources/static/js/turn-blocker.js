// Turn blocker - prevents actions when player has already played this turn

let turnBlockerInterval = null;
let isBlockingTurn = false;
let currentAvailableActions = [];

function isSelfViewActive() {
  const backBtn = document.getElementById("backToSelf");
  return !backBtn || backBtn.hidden === true;
}

function arePrimitiveArraysEqual(a = [], b = []) {
  if (a === b) return true;
  if (!Array.isArray(a) || !Array.isArray(b)) return false;
  if (a.length !== b.length) return false;
  for (let i = 0; i < a.length; i += 1) {
    if (a[i] !== b[i]) return false;
  }
  return true;
}

function getDiscardKey(card) {
  if (typeof card === 'string') return `s:${card}`;
  if (!card || typeof card !== 'object') return 'o:';
  return `o:${card.id ?? ''}:${card.image ?? ''}`;
}

function areDiscardedEqual(a = [], b = []) {
  if (a === b) return true;
  if (!Array.isArray(a) || !Array.isArray(b)) return false;
  if (a.length !== b.length) return false;
  for (let i = 0; i < a.length; i += 1) {
    if (getDiscardKey(a[i]) !== getDiscardKey(b[i])) return false;
  }
  return true;
}

function arePlayersListEqual(a = [], b = []) {
  if (a === b) return true;
  if (!Array.isArray(a) || !Array.isArray(b)) return false;
  if (a.length !== b.length) return false;
  for (let i = 0; i < a.length; i += 1) {
    if (a[i].id !== b[i].id || a[i].name !== b[i].name) return false;
  }
  return true;
}

function updateUiFromState(state) {
  if (!state) return;
  const isSelf = isSelfViewActive();

  if (!lastPlayerState) {
    if (isSelf && typeof applyPlayerState === 'function') {
      applyPlayerState(state);
    }
    lastPlayerState = state;
    return;
  }

  if (isSelf) {
    const prevHand = lastPlayerState.hand || [];
    const nextHand = state.hand || [];
    if (!arePrimitiveArraysEqual(prevHand, nextHand)) {
      cards = nextHand;
      renderHand();
    }

    const prevWonder = lastPlayerState.wonder || null;
    const nextWonder = state.wonder || null;
    if (prevWonder !== nextWonder) {
      renderWonder(nextWonder || "placeholder.png");
    }

    const prevCardBacks = lastPlayerState.cardBacks || [];
    const nextCardBacks = state.cardBacks || [];
    if (!arePrimitiveArraysEqual(prevCardBacks, nextCardBacks)) {
      renderCardBacks(nextCardBacks);
    }

    const prevCoins = lastPlayerState.coins ?? 0;
    const nextCoins = state.coins ?? 0;
    if (prevCoins !== nextCoins) {
      renderCoins(nextCoins);
    }

    const prevPlayed = lastPlayerState.playedCards || [];
    const nextPlayed = state.playedCards || [];
    if (!arePrimitiveArraysEqual(prevPlayed, nextPlayed)) {
      renderPlayed(nextPlayed);
    }

    const prevDiscarded = lastPlayerState.discarded || [];
    const nextDiscarded = state.discarded || [];
    if (!areDiscardedEqual(prevDiscarded, nextDiscarded)) {
      currentDiscardedCards = nextDiscarded;
      renderDiscarded(currentDiscardedCards, canSelectDiscard);
    }
  }

  const prevPlayers = lastPlayerState.players || [];
  const nextPlayers = state.players || [];
  if (!arePlayersListEqual(prevPlayers, nextPlayers)) {
    renderPlayersList(nextPlayers);
  }

  lastPlayerState = state;
}

/**
 * Check available actions and update UI accordingly
 */
async function checkAvailableActions() {
  try {
    const state = await getPlayerState();
    const actions = state.availableActions || [];
    currentAvailableActions = actions;
    
    console.log('[TurnBlocker] Available actions:', actions);
    
    // If player can do standard actions (play, build, discard), they haven't played yet
    const canPlayStandardActions = actions.includes('play');
    const canBuildFromDiscard = actions.includes('build_from_discard');
    
    if (!canPlayStandardActions && !canBuildFromDiscard && !isBlockingTurn) {
      // No actions available - player has played and waiting
      showTurnBlockerOverlay();
      isBlockingTurn = true;
    } else if (canPlayStandardActions && isBlockingTurn) {
      // Turn changed - player can play again
      hideTurnBlockerOverlay();
      isBlockingTurn = false;
      // Turn has changed - update data seamlessly
      console.log('[TurnBlocker] Turn changed - updating game data');
    } else if (canBuildFromDiscard && isBlockingTurn) {
      // Special action available - hide blocker
      hideTurnBlockerOverlay();
      isBlockingTurn = false;
    }
    
    // Enable/disable discard cards based on build_from_discard action
    if (isSelfViewActive()) {
      if (typeof enableDiscardCardSelection === 'function') {
        enableDiscardCardSelection(canBuildFromDiscard);
      }
    } else {
      canSelectDiscard = canBuildFromDiscard;
    }

    updateUiFromState(state);
  } catch (error) {
    console.error('[TurnBlocker] Error checking available actions:', error);
  }
}

/**
 * Show the blocking overlay that prevents further actions
 */
function showTurnBlockerOverlay() {
  // Create or get the blocker overlay
  let blocker = document.getElementById('turnBlockerOverlay');
  
  if (!blocker) {
    blocker = document.createElement('div');
    blocker.id = 'turnBlockerOverlay';
    blocker.className = 'turn-blocker-overlay';
    blocker.innerHTML = `
      <div class="turn-blocker-modal">
        <div class="turn-blocker-content">
          <div class="turn-blocker-icon">⏳</div>
          <div class="turn-blocker-message">You have already played this turn</div>
          <div class="turn-blocker-submessage">Waiting for other players...</div>
        </div>
      </div>
    `;
    document.body.appendChild(blocker);
  }
  
  blocker.style.display = 'flex';
}

/**
 * Hide the blocking overlay
 */
function hideTurnBlockerOverlay() {
  const blocker = document.getElementById('turnBlockerOverlay');
  if (blocker) {
    blocker.style.display = 'none';
  }
}

/**
 * Start polling for turn status every 2 seconds
 */
function startTurnPolling() {
  console.log('[TurnBlocker] Starting turn polling...');
  
  // Check immediately
  checkAvailableActions();
  
  // Then poll every 2 seconds
  turnBlockerInterval = setInterval(() => {
    checkAvailableActions();
  }, 2000);
}

/**
 * Stop polling for turn status
 */
function stopTurnPolling() {
  console.log('[TurnBlocker] Stopping turn polling...');
  
  if (turnBlockerInterval) {
    clearInterval(turnBlockerInterval);
    turnBlockerInterval = null;
  }
}

// Initialize turn blocker when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
  // Add CSS for the turn blocker overlay
  if (!document.getElementById('turnBlockerStyles')) {
    const style = document.createElement('style');
    style.id = 'turnBlockerStyles';
    style.textContent = `
      .turn-blocker-overlay {
        display: none;
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background-color: rgba(0, 0, 0, 0.7);
        z-index: 10000;
        justify-content: center;
        align-items: center;
      }
      
      .turn-blocker-modal {
        background-color: white;
        border-radius: 16px;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
        padding: 40px;
        text-align: center;
        max-width: 400px;
        animation: slideInScale 0.3s ease-out;
      }
      
      @keyframes slideInScale {
        from {
          opacity: 0;
          transform: scale(0.9);
        }
        to {
          opacity: 1;
          transform: scale(1);
        }
      }
      
      .turn-blocker-icon {
        font-size: 48px;
        margin-bottom: 16px;
        animation: pulse 1.5s infinite;
      }
      
      @keyframes pulse {
        0%, 100% {
          opacity: 1;
        }
        50% {
          opacity: 0.6;
        }
      }
      
      .turn-blocker-message {
        font-size: 20px;
        font-weight: 600;
        color: #333;
        margin-bottom: 8px;
        font-family: 'Space Grotesk', sans-serif;
      }
      
      .turn-blocker-submessage {
        font-size: 14px;
        color: #666;
        font-family: 'Space Grotesk', sans-serif;
      }
    `;
    document.head.appendChild(style);
  }
});
