// Turn blocker - prevents actions when player has already played this turn

let turnBlockerInterval = null;
let isBlockingTurn = false;

/**
 * Check if the player has played this turn and show/hide blocking overlay
 */
async function checkHasPlayedThisTurn() {
  try {
    const response = await fetch(`/api/has-played-this-turn?gameId=${gameId}`);
    
    if (!response.ok) {
      console.error('[TurnBlocker] Failed to fetch hasPlayedThisTurn:', response.status);
      return;
    }
    
    const data = await response.json();
    const hasPlayed = data.hasPlayedThisTurn;
    
    console.log('[TurnBlocker] Has played this turn:', hasPlayed);
    
    if (hasPlayed && !isBlockingTurn) {
      showTurnBlockerOverlay();
      isBlockingTurn = true;
    } else if (!hasPlayed && isBlockingTurn) {
      hideTurnBlockerOverlay();
      isBlockingTurn = false;
      // Turn has changed - reload all game data
      console.log('[TurnBlocker] Turn changed - reloading game data');
      if (typeof reloadAllGameData === 'function') {
        await reloadAllGameData();
      }
    }
  } catch (error) {
    console.error('[TurnBlocker] Error checking hasPlayedThisTurn:', error);
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
  checkHasPlayedThisTurn();
  
  // Then poll every 2 seconds
  turnBlockerInterval = setInterval(() => {
    checkHasPlayedThisTurn();
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
