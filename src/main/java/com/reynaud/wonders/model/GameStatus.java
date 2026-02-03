package com.reynaud.wonders.model;

public enum GameStatus {
    WAITING,      // Waiting for players to join
    STARTING,     // Game is being initialized
    PLAYING,
    FINISHED,     // Game completed
    CANCELLED     // Game was cancelled
}
