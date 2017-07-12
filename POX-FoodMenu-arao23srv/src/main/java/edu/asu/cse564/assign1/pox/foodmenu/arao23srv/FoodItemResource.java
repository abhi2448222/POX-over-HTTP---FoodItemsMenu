/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.asu.cse564.assign1.pox.foodmenu.arao23srv;

import java.io.File;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author Abhishek Rao
 */
@Path("inventory")
public class FoodItemResource {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(FoodItemResource.class);
    private static HashMap<Integer, FoodItem> map = new HashMap<Integer, FoodItem>();

    static {
        try {
            LOG.info("Trying to read the Xml file");
            String filename = "FoodItemData.xml";
            URL url = FoodItemResource.class.getClassLoader().getResource(filename);
            LOG.info("URI is {}", url);
            File file;
            try {
                file = new File(url.toURI());
                LOG.info("File read successfully {}", file);
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
                file = new File(url.getPath());
            }
            JAXBContext jc = JAXBContext.newInstance(FoodItemData.class);
            Unmarshaller unmar = jc.createUnmarshaller();
            FoodItemData foodItemData = (FoodItemData) unmar.unmarshal(file);

            Marshaller mar = jc.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            mar.marshal(foodItemData, System.out);
            LOG.info("marshalled successfully");
            for (FoodItem item : foodItemData.getFoodItems()) {
                map.put(item.getId(), item);
                LOG.info("ID ={} and Item={} added successfully in the Memory", item.getId(), item.getName());
            }

        } catch (JAXBException e) {
            LOG.info("Exception while raeding the file");
            e.printStackTrace();
        }

    }

    //Handling the POST Requests 
    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public String getOrAddfoodItems(String str) {
        LOG.info("Building a DOM parser");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        dbFactory.setNamespaceAware(true);
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new InputSource(new StringReader(str)));
            document.getDocumentElement().normalize();
            Element ele = document.getDocumentElement();
            String namespace = ele.getNamespaceURI();
            LOG.info("NAMESPACE is : {}", namespace);
            if (namespace == null || !namespace.equals("http://cse564.asu.edu/PoxAssignment")) {
                LOG.info("Invalid Namepace URI --Invalid message Input");
                return "<InvalidMessage xmlns=\"http://cse564.asu.edu/PoxAssignment\"/>";
            }
            String root = document.getDocumentElement().getNodeName();
            LOG.info("Root element :{}", root);
            if (root.equals("SelectedFoodItems")) {
                String xmlOut = "\n<RetrievedFoodItems xmlns=\"http://cse564.asu.edu/PoxAssignment\">";
                String inner = "";
                NodeList idList = document.getElementsByTagName("FoodItemId");
                int i = 0;
                for (i = 0; i < idList.getLength(); i++) {
                    String foodItemId = idList.item(i).getTextContent();
                    try {
                        if (map.containsKey(Integer.parseInt(foodItemId))) {
                            FoodItem foodItem = map.get(Integer.parseInt(foodItemId));
                            inner = inner + "\n\t<FoodItem country=\"" + foodItem.getCountry() + "\">\n\t\t<id>" + foodItem.getId()
                                    + "</id>\n\t\t<name>" + foodItem.getName() + "</name>\n\t\t<description>" + foodItem.getDescription()
                                    + "</description>\n\t\t<category>" + foodItem.getCategory() + "</category>\n\t\t<price>" + foodItem.getPrice()
                                    + "</price>\n\t</FoodItem>";
                        } else {
                            LOG.info("ID doesnt exist in memory--Invalid ID");
                            inner = inner + "\n\t<InvalidFoodItem>\n\t\t<FoodItemId>" + foodItemId
                                    + "</FoodItemId>\n\t</InvalidFoodItem>";
                        }
                    } catch (NumberFormatException e) {
                        LOG.info("Number format exception--Invalid ID");
                        inner = inner + "\n\t<InvalidFoodItem>\n\t\t<FoodItemId>" + foodItemId
                                + "</FoodItemId>\n\t</InvalidFoodItem>";
                    }

                }
                if (i == 0) {
                    LOG.info("Returning Invalid Input format");
                    return "\n<InvalidMessage xmlns=\"http://cse564.asu.edu/PoxAssignment\"/>";
                } else {
                    LOG.info("Returning Retrieved Fooditems in a XML");
                    return xmlOut + inner + "\n</RetrievedFoodItems>";
                }

            } else if (root.equals("NewFoodItems")) {
                NodeList fooditemList = document.getElementsByTagName("FoodItem");
                String inner = "";
                int i = 0;
                for (i = 0; i < fooditemList.getLength(); i++) {
                    try {
                        Node item = fooditemList.item(i);
                        if (item.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) item;
                            String country = element.getAttribute("country");
                            String name = element.getElementsByTagName("name").item(0).getTextContent();
                            String description = element.getElementsByTagName("description").item(0).getTextContent();
                            String category = element.getElementsByTagName("category").item(0).getTextContent();
                            String price = element.getElementsByTagName("price").item(0).getTextContent();
                            if ((country.equals("")) || (name.equals("")) || (description.equals("")) || (category.equals("")) || (price.equals(""))) {
                                LOG.info("Empty fields--Invalid Input format");
                                inner = "\n<InvalidMessage xmlns=\"http://cse564.asu.edu/PoxAssignment\"/>";
                            } else {
                                boolean flag = false;
                                int dupId = 0;
                                Iterator itr = map.entrySet().iterator();
                                while (itr.hasNext()) {
                                    Map.Entry pair = (Map.Entry) itr.next();
                                    FoodItem chkFoodItem = (FoodItem) pair.getValue();
                                    //Checking whether the FoodItem already exists by Name and Category
                                    if (chkFoodItem.getCategory().equals(category) && chkFoodItem.getName().equals(name)) {
                                        LOG.info("Food item already exists in memory");
                                        dupId = chkFoodItem.getId();
                                        flag = true;
                                        break;
                                    }
                                }
                                if (flag) {
                                    inner = "\n<FoodItemExists xmlns=\"http://cse564.asu.edu/PoxAssignment\">\n\t"
                                            + "<FoodItemId>" + dupId + "</FoodItemId>\n</FoodItemExists>";
                                } else {
                                    int generateId = 0;
                                    //Generating Random ID for new Food item
                                    while (true) {
                                        Random rand = new Random();
                                        generateId = rand.nextInt(1000) + 1;
                                        if (!map.containsKey(generateId)) {
                                            break;
                                        }
                                    }
                                    Float convPrice = Float.parseFloat(price);
                                    FoodItem newItem = new FoodItem(country, generateId, name, description, category, convPrice);
                                    map.put(generateId, newItem);
                                    LOG.info("New food Item added");
                                    inner = "\n<FoodItemAdded xmlns=\"http://cse564.asu.edu/PoxAssignment\">\n\t<FoodItemId>" + generateId + "</FoodItemId>\n</FoodItemAdded>";

                                }
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOG.info("Exception --Invalid Input format");
                        inner = "\n<InvalidMessage xmlns=\"http://cse564.asu.edu/PoxAssignment\"/>";
                    }
                    LOG.info("Invalid Input format");
                    return inner;
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();

        }
        LOG.info("Exception --Returning Invalid Input format");
        return "<InvalidMessage xmlns=\"http://cse564.asu.edu/PoxAssignment\"/>";
    }

}
