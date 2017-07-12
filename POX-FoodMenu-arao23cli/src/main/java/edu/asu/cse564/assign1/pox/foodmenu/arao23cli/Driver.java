/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.asu.cse564.assign1.pox.foodmenu.arao23cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Abhishek Rao
 */
public class Driver {

    private static final Logger LOG = LoggerFactory.getLogger(Driver.class);

    public static void main(String[] args) {
        POXClient poxClient = new POXClient();
        //Sample reqest for Add Food Item
        String requestMessage = "<NewFoodItems xmlns=\"http://cse564.asu.edu/PoxAssignment\">\n"
                + "<FoodItem country=\"GB\">\n"
                + "<name>Cornish Pasty</name>\n"
                + "<description>Tender cubes of steak, potatoes and swede wrapped in flakey short crust pastry.  Seasoned with lots of pepper.  Served with mashed potatoes, peas and a side of gravy</description>\n"
                + "<category>Dinner</category>\n"
                + "<price>15.95</price>\n"
                + "</FoodItem>\n"
                + "</NewFoodItems>";
         
        //Sample reqest for Get Food Item
        /* String requestMessage="<SelectedFoodItems xmlns=\"http://cse564.asu.edu/PoxAssignment\">\n" +
                                    "<FoodItemId>100</FoodItemId>\n" +
                                    "<FoodItemId>156</FoodItemId>\n" +                                 
                               "</SelectedFoodItems>";*/
        
        String output = poxClient.addOrGetFoodItems(requestMessage);
        LOG.info("Response From Server : ");
        System.out.println(output);
        poxClient.close();
    }

}
