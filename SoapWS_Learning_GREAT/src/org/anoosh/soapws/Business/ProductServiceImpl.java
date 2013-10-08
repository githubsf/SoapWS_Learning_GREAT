package org.anoosh.soapws.Business;

import java.util.ArrayList;
import java.util.List;

/*
 * This Business tier class will have all the business logic in one place
 * so that we can use it via delegation/composition both from our Web Service classes
 * like ProductCatalog to support other applications using it as a web service,
 * and also use it in our MVC classes if we have a web front end for actual human users.
 */

public class ProductServiceImpl {
	
	List<String> booklist = new ArrayList<String>();
	List<String> movielist = new ArrayList<String>();
	List<String> musiclist = new ArrayList<String>();
	
	public  ProductServiceImpl(){
		booklist.add("The MongoDB Definitive Guide");
		booklist.add("War and Peace");
		booklist.add("Power of Now");
		
	    movielist.add("Rain Man");
		movielist.add("Superman");
		movielist.add("Dr. Zhivago");
		
		musiclist.add("Best of Pink Floyd");
		musiclist.add("Armin Van Burren ASOT 100");
		musiclist.add("Bossa Nova");
		
		}
	
	public List<String> getProductCategories(){
		List<String> categories = new ArrayList<String>();
		categories.add("Books");
		categories.add("Movies");
		categories.add("Music");
		return categories;
	}
	
	public List<String> getProduct(String category){
		switch (category.toLowerCase()){
		case "books" :return booklist;
		case "movies" :return movielist;
		case "music" :return musiclist;		
		}
		return null;
	}
	
	public boolean addProduct(String category, String Product){
		switch (category.toLowerCase()){
		case "books" :return booklist.add(Product);
		case "movies" :return movielist.add(Product);
		case "music" :return musiclist.add(Product);		
		}
		return false;
	}
	

}
