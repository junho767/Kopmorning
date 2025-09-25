package com.personal.kopmorning.domain.article.article.entity;

public enum Category {
    FREE("자유"),
    FOOTBALL("해외 축구");

    private final String categoryName;
    
    Category(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
}
