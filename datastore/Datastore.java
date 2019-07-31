/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datastore;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.Date;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import static com.oracle.jrockit.jfr.ContentType.Timestamp;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;
import static jdk.nashorn.internal.ir.debug.ObjectSizeCalculator.getObjectSize;
import jdk.nashorn.internal.parser.JSONParser;




public class Datastore {

    /**
     * @param args the command line arguments
     */
    
    private String index;
    private double max_size;
    private String cur_file_path;
    private int file_num;
    private String fname;
    private String txt;
    
    public Datastore()
    {
        
    }
    public Datastore(String index,String cur_file_path) throws IOException {
        max_size=10;
        file_num=1;
        fname="db";
        txt=".txt";
        this.cur_file_path=cur_file_path;
        this.index=index;
        // Create the index file,data file in the default.db folder
        
        CreateFile(index);
        
        String f=fname+file_num+txt;
        String location = cur_file_path+f;
        CreateFile(location);
    }
    
    
     //read operation is impemented
    public synchronized JsonElement getObjectForKey(String key) throws FileNotFoundException{
       //search the index file for the key
      String file_name=searchIndex(key);
        
      if(   !"".equals(file_name))
        {
            //search the specified file
            
            String search_file=cur_file_path+file_name;
            File f=new File(search_file);
            System.out.println("reading the data file");
            
        final Scanner sc = new Scanner(f);
        
        while (sc.hasNextLine())
        {
        String lineFromFile =sc.nextLine();          //string to json
        JsonObject jsonObject = new JsonParser().parse(lineFromFile).getAsJsonObject();

        String data= jsonObject.get("id").getAsString();
        
        System.out.println(data+"  "+key);
       
        
//time stamp checking
        long resultantTime=check_ttl(jsonObject);
        
        //if current timestamp is positive then time exceeded
        if(data.equals(key) && resultantTime<0) { 
            // a match!
            JsonElement actualData=jsonObject.get("actualObjectToBeReturned");//;getAsJsonObject(lineFromFile).get("actualObjectToBeReturned").getAsString();
            return actualData;
            }
        else if(data.equals(key) && resultantTime>=0)
        {
            System.out.println("Time exceeded so unavailable for read");
        }
       }
        sc.close();
    }
    return null;
}


    public synchronized void deleteObjectForKey(String key) throws FileNotFoundException, IOException{
    
        String file_name=searchIndex(key);
        if(!"".equals(file_name))
        {
        deleteHelperMethod(file_name,key,true);
        deleteHelperMethod(index,key,false);
        }
        else
        {
        System.out.println("File not found");
        }
  
 }
public void deleteHelperMethod(String file_name,String key,boolean flag)
    {
        File oldFile;
        if(flag) {
            oldFile=new File(cur_file_path+file_name);
        } else {
            oldFile=new File(file_name);
        }

            System.out.println("reading the data file");
            String tempFile=cur_file_path+"temp.txt";
            File newFile=new File(tempFile);
            try
            {
                FileWriter fw=new FileWriter(newFile,true);
                BufferedWriter bw=new BufferedWriter(fw);
                PrintWriter pw=new PrintWriter(bw);
                Scanner sc=new Scanner(oldFile);
                while(sc.hasNextLine())
                {
                String lineFromFile =sc.nextLine();   
                String data;
                if(flag)
                {
                JsonObject jsonObject = new JsonParser().parse(lineFromFile).getAsJsonObject();
                data = jsonObject.get("id").getAsString();
                }
                else
                {
                String ar[]=lineFromFile.split(":");
                data = ar[0].replace("\"","");
                
                }
                //compare tthe data and the key
                if(!data.equals(key)) {
                   pw.println(lineFromFile); 
                }
                }
                sc.close();
                pw.flush();
                pw.close();
                
                //oldFile.delete();
                 if (!oldFile.delete()) {
                      System.out.println("Could not delete file");
                      return;
                  }

                  // Rename the new file to the filename the original file had.
                  if (!newFile.renameTo(oldFile))
                      System.out.println("Could not rename file");
               // File dump=new File(file_name);
                //newFile.renameTo(dump);
                //oldFile.delete();
                System.out.println(key + " was deleted.");
            }
            catch(Exception e)
            {
                e.printStackTrace();
                System.out.println("Error in deleting the key value pair");
            }
    }

   //creating json object

    public synchronized String createObject(String incomingObject) throws IOException {

       
        String idToBeCreated = String.valueOf(UUID.randomUUID());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", idToBeCreated);
        jsonObject.addProperty("createTimeStamp" ,System.currentTimeMillis());//new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())
        jsonObject.addProperty("ttlInSeconds" , 1);//3600


        try{
        JsonObject actualJsonObject = new JsonParser().parse(incomingObject).getAsJsonObject();
        //should check the size limit of the value
        double s;
         s = getObjectSize(actualJsonObject)/1000;
        if(s<=16)
        {
        jsonObject.add("actualObjectToBeReturned",actualJsonObject);
        System.out.println(jsonObject.get("actualObjectToBeReturned"));
        System.out.println(idToBeCreated + " was created.");
        insert(jsonObject,(double)getObjectSize(jsonObject)/1000);
        
        return idToBeCreated;
        }
        else
         System.out.println("Object size exceeded");
         
        }
        catch(JsonSyntaxException e)
        {
            System.out.println("The value is not of type JSON ");
        }
        
        return null;
}
    

    private void CreateFile(String DbLocation) throws IOException {
        File file=new File(DbLocation);
         try{
             file.createNewFile();
            System.out.println(DbLocation+" File Created");
        }
         catch(IOException e)
         {  
             System.out.println("File "+DbLocation+" already exists");
         }
      }
    
    //insert the keyvalue into the file

    private void insert(JsonObject jsonObject,double sz) throws IOException {
            File fp=new File(cur_file_path+fname+file_num);
            double len=fp.length()/1000000;
            
            sz=sz/1000;
            System.out.println(len+" "+sz);
            //converting json object into string
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(jsonObject.toString());
            stringBuilder.append("\n");
          
            if(len+sz>=max_size)
            {file_num++;
            CreateFile(cur_file_path+fname+file_num+txt);
            }
                StringBuilder sb = new StringBuilder();
             String f=fname+file_num+txt;
             sb.append(jsonObject.get("id")+":"+f);
             sb.append("\n");

        try
        {
            Files.write(Paths.get(cur_file_path+fname+file_num+txt), stringBuilder.toString().getBytes(), StandardOpenOption.APPEND);
            Files.write(Paths.get(index),sb.toString().getBytes(),StandardOpenOption.APPEND);
        }
        catch (IOException e) {
            //exception handling left as an exercise for the reader
            e.printStackTrace();
            System.out.println("Error in writing");
            
            }
    }

    private String searchIndex(String key) throws FileNotFoundException {
          String file_name="";
          String ans=null;
          File file =  new File(index); 
          Scanner scanner = new Scanner(file);
          while (scanner.hasNextLine())
          {
            String lineFromFile = scanner.nextLine();
            String ar[]=lineFromFile.split(":");
            String k="\""+key+"\"";
            
            if(ar[0].equals(k)) { 
            // a match!
            file_name=ar[1];
            break;
            }
           }
          scanner.close();
             return file_name;
    }

    private long check_ttl(JsonObject jsonObject) {
        long time=jsonObject.get("createTimeStamp").getAsLong();
        long createTimeStamp = (time/1000L);   
        long ttl=jsonObject.get("ttlInSeconds").getAsLong();
        long currentTimeStamp =(System.currentTimeMillis()/1000L);
        long resultantTime= currentTimeStamp-(createTimeStamp + ttl); 
        return resultantTime;
    }
}
