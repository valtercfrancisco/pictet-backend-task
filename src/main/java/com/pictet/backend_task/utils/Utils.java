package com.pictet.backend_task.utils;

import com.pictet.backend_task.repository.model.Difficulty;

public class Utils {

    private Utils() {}

    public static Difficulty parseDifficulty(String difficulty) {
        try {
            return Difficulty.valueOf(difficulty.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Difficulty must be one of: EASY, MEDIUM, HARD");
        }
    }
}

