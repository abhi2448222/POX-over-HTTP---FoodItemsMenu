/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.asu.cse564.assign1.pox.foodmenu.arao23cli;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Abhishek
 */
public class POXClient {

    private static final Logger LOG = LoggerFactory.getLogger(POXClient.class);
    private WebResource webResource = null;
    private Client clientPOXfoodMenu;
    private static final String BASE_URI = "http://localhost:8080/POX-FoodMenu-arao23srv/FoodItems/webapi";

    public POXClient() {
        LOG.info("Creating Pox-FoodMenu Client");
        ClientConfig config = new DefaultClientConfig();
        clientPOXfoodMenu = Client.create(config);
        webResource = clientPOXfoodMenu.resource(BASE_URI).path("inventory");
        LOG.info("Final Path URI : {}", webResource.getURI());
    }

    public String addOrGetFoodItems(String requestMessage) throws UniformInterfaceException {
        LOG.info("Request message :");
        System.out.println("\n" + requestMessage + "\n");
        ClientResponse response = webResource.type(MediaType.APPLICATION_XML)
                .post(ClientResponse.class, requestMessage);
        String outputFromServer = response.getEntity(String.class);
        return outputFromServer;
    }

    public void close() {
        clientPOXfoodMenu.destroy();
    }

}
