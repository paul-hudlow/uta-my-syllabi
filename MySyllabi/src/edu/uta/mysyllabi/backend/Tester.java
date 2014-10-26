package edu.uta.mysyllabi.backend;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Tester {

	public static void main(String[] args) {
		System.out.print("Hello, world!");
		URL url;
		try {
			url = new URL("http://www.google.com");
			final URLConnection conn = url.openConnection();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.print("Network inaccessable.");
			e.printStackTrace();
		}
		System.out.print("Internet available!");
		
	}
	
}
