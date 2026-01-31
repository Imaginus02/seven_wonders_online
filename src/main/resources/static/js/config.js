// Asset paths
const assetBase = "/assets/cards/";
const wonderBase = "/assets/wonders/";
const coinAsset = "/assets/tokens/coin.png";

// Player identification
const selfPlayerId = "self";

// Get gameId from URL parameter or default to 1 for testing
const urlParams = new URLSearchParams(window.location.search);
const gameId = urlParams.get('gameId') || '1';

// Get CSRF token and parameter name from meta tags
function getCsrfTokenAndParam() {
  const tokenMeta = document.querySelector('meta[name="_csrf"]');
  const paramMeta = document.querySelector('meta[name="_csrf_parameter_name"]');
  
  const token = tokenMeta ? tokenMeta.getAttribute('content') : '';
  const paramName = paramMeta ? paramMeta.getAttribute('content') : '_csrf';
  
  return { token, paramName };
}
