-- =====================================================================
-- CLEAR VARIABLE DATA
-- =====================================================================
-- This script clears all variable/transient data while preserving
-- static game data (cards, effects, wonders)
-- 
-- Use this script when you want to reset the application state
-- without losing the game definitions
-- =====================================================================

-- Clear all game-related data
DELETE FROM player_states;
DELETE FROM games;

-- Clear all logs
DELETE FROM logs;

-- Clear all users (this will also reload test users on next restart)
-- Comment out this line if you want to keep user accounts
DELETE FROM users;

-- =====================================================================
-- To execute this script:
-- 1. Access H2 Console at http://localhost:8080/h2-console
-- 2. Use JDBC URL: jdbc:h2:file:./data/wondersdb
-- 3. Username: sa (no password)
-- 4. Copy and paste this script
-- 5. Click "Run"
-- =====================================================================
