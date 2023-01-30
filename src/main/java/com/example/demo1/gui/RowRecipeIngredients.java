package com.example.demo1.gui;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.ImageView;

public class RowRecipeIngredients {

    private final SimpleStringProperty ingredientTable;
    private final SimpleIntegerProperty countTable;


    public RowRecipeIngredients(String name, Integer count) {
        ingredientTable = new SimpleStringProperty(name);
        countTable = new SimpleIntegerProperty(count);
    }

    public String getIngredient() { return ingredientTable.get(); }
    public Integer getCount() { return countTable.get(); }

}
