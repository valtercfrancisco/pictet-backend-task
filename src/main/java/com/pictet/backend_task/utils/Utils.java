package com.pictet.backend_task.utils;

import com.pictet.backend_task.model.Category;
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

    public static Category parseCategory(String category) {
        try {
            return Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Category must be one of: FICTION, SCIENCE, HORROR, ADVENTURE, FANTASY, MYSTERY, THRILLER, ROMANCE, HISTORICAL, BIOGRAPHY");
        }
    }
}

