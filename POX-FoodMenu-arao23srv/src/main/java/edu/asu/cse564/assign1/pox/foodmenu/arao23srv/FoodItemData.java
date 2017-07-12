/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.asu.cse564.assign1.pox.foodmenu.arao23srv;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Abhishek Rao
 */
@XmlRootElement(name = "FoodItemData")
@XmlAccessorType(XmlAccessType.FIELD)
public class FoodItemData {

    @XmlElement(name = "FoodItem")
    private List<FoodItem> foodItems;

    public FoodItemData(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }

    public FoodItemData() {
    }

    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }

}
