/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datastore;

import java.io.IOException;


public class MainClass {
        
    	    public static void main(String[] args) throws IOException {
	       String index="C:\\Users\\Acer\\Documents\\default.db\\index.txt",cur_file_path="C:\\Users\\Acer\\Documents\\default.db\\";
                Datastore dbClient = new Datastore(index,cur_file_path);
	        /**
	         * Create an Object
	         */
	       String incomingObject = "{\"name\":\"mkyong\",\"age\":35,\"position\":[\"Founder\",\"CTO\",\"Writer\"],\"skills\":[\"java\",\"python\",\"node\",\"kotlin\"],\"salary\":{\"2018\":14000,\"2012\":12000,\"2010\":10000}}\n";
	       // System.out.println( + " Is the Id of the object created.");
	
                String inObject = "{\"name\":\"mcdcdsng\",\"age\":35,\"position\":[\"Founder\",\"CTO\",\"Writer\"],\"skills\":[\"java\",\"python\",\"node\",\"kotlin\"],\"salary\":{\"2018\":14000,\"2012\":12000,\"2010\":10000}}\n";
	        String iObject = "{\"name\":\"dsvAS\",\"age\":35,\"position\":[\"Founder\",\"CTO\",\"Writer\"],\"skills\":[\"java\",\"python\",\"node\",\"kotlin\"],\"salary\":{\"2018\":14000,\"2012\":12000,\"2010\":10000}}\n";
                String io = "{\"name\":\"mcdcdsng\",\"age\":35,\"position\":[\"Founder\",\"CTO\",\"Writer\"],\"skills\":[\"java\",\"python\",\"node\",\"kotlin\"],\"salary\":{\"2018\":14000,\"2012\":12000,\"2010\":10000}}\n";
	        String ip = "{\"name\":\"dsvAS\",\"age\":35,\"position\":[\"Founder\",\"CTO\",\"Writer\"],\"skills\":[\"java\",\"python\",\"node\",\"kotlin\"],\"salary\":{\"2018\":14000,\"2012\":12000,\"2010\":10000}}\n";
	       String iq = "{\"name\":\"mcdcdsng\",\"age\":35,\"position\":[\"Founder\",\"CTO\",\"Writer\"],\"skills\":[\"java\",\"python\",\"node\",\"kotlin\"],\"salary\":{\"2018\":14000,\"2012\":12000,\"2010\":10000}}\n";
	        String ir = "{\"name\":\"dsvAS\",\"age\":35,\"position\":[\"Founder\",\"CTO\",\"Writer\"],\"skills\":[\"java\",\"python\",\"node\",\"kotlin\"],\"salary\":{\"2018\":14000,\"2012\":12000,\"2010\":10000}}\n";
	     
	       
             dbClient.createObject(incomingObject);
             dbClient.createObject(iObject);dbClient.createObject(io);
             dbClient.createObject(ip);
             dbClient.createObject(iq);dbClient.createObject(ir);
             
             
	        /**
	         * Get that Object
	         */
               
               /* System.out.println("Enter the key for which value must be searched");
                String key="e4681827-4323-4f2d-aedf-44abb97322ec";
                if(key==null)
                        System.out.println("Key not found");
                else
	        System.out.println(dbClient.getObjectForKey(key));
	*/
	        /**
	         * Delete the Object
	         */
             /*   String key="c2687a5a-b92c-4b49-96c6-bff47c818fe9";
	        dbClient.deleteObjectForKey(key);*/
	    }
}
	



