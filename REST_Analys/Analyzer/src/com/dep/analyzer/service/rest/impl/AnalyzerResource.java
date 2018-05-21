package com.dep.analyzer.service.rest.impl;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/ar/")
public class AnalyzerResource {
	
 List list;
	
 @GET
 @Produces({MediaType.APPLICATION_XML})
 @Path("/xml")
 public  List<Data> getData() {

	  list = new ArrayList();

	 Data data1 =  new Data() ;
     data1.setTitre("dinguerie");
     data1.setAlbum("premier etage");
     data1.setGenre("rap");
     data1.setAuteur("keblack");
     
     Data data2 =  new Data() ;
     data2.setTitre("fable");
     data2.setAlbum("premier etage");
     data2.setGenre("rap");
     data2.setAuteur("keblack");
     
     Data data3 =  new Data() ;
     data3.setTitre("paname");
     data3.setAlbum("bout de reves");
     data3.setGenre("rap");
     data3.setAuteur("slimane");
     
     Data data4 =  new Data() ;
     data4.setTitre("le million");
     data4.setAlbum("bout de reves");
     data4.setGenre("rap");
     data4.setAuteur("slimane");
     
     list.add(data1);
     list.add(data2);
     list.add(data3);
     list.add(data4);
    
     return list;

    }
 
 @GET
 @Produces("application/json")
 @Path("{name}")
 public String getAnalyser(@PathParam("name") String name) {
	 
	 String mots[] = null ;
     String temps[] = null ;
     
     String musique = null;
     String action = null;
    
     String [] tab = {"",""};
     List<String> lists = new ArrayList<String>();
     if(name.equals("pause")==true)
    {
    	musique = "song";
    	action="pause";
    }
    else if (name.equals("lecture")==true)
    {
    	musique = "song";
    	action="start";
    }
    else if (name.equals("stop")==true)
    {
    	musique = "song";
    	action="stop";
    }
    else
    {
     mots = name.split("supprime");
     
     for(String mot : mots)
		{
    	 if(mot.equals(name)==true)
    	 {
    		 temps = mot.split("écouter");
    		 
    		 	for(String temp : temps)
    		 	{
    		 		musique = temp;
    		 		action="play";
 			
    		 	}
    	 }
    	 else
    	 {
    		   String value = "supprime";
    		   String p ="de la playlist";
    		   int posA = name.indexOf(value);
    	        if (posA == -1) {
    	            musique =  " ";
    	        }
    	        int posB = name.lastIndexOf(p);
    	        if (posB == -1) {
    	           musique = " ";
    	        }
    	        int adjustedPosA = posA + value.length();
    	        if (adjustedPosA >= posB) {
    	            musique = " ";
    	        }
    	        musique =  name.substring(adjustedPosA, posB);
    	        action="delete";
    	 }
    	
    	 
    	 
		}
    }
    lists.add(action);
    lists.add(musique);
    
    
    return lists.toString();

    }

}
