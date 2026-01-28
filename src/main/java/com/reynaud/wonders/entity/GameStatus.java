package com.reynaud.wonders.entity;

public enum GameStatus {
    WAITING,      // Waiting for players to join
    STARTING,     // Game is being initialized
    AGE_I,        // Age I in progress
    AGE_II,       // Age II in progress
    AGE_III,      // Age III in progress
    FINISHED,     // Game completed
    CANCELLED     // Game was cancelled
}
