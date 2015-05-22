package me.Bentipa.updater;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author Benjamin | Bentipa
 * @version 1.0
 *  Created on 03.04.2015
 *
 */
public class SpigotPluginUpdater {


	private URL url;
	private JavaPlugin plugin;
	private String pluginurl;
	
	
	/**
	 * Create a new SpigotPluginUpdate to check and update your plugin
	 * @param plugin - your plugin
	 * @param pluginurl - the url to your plugin.html on your webserver 
	 */
	public SpigotPluginUpdater(JavaPlugin plugin, String pluginurl){
		try {
			url = new URL(pluginurl);
		} catch (MalformedURLException e) {
			System.out.println("[SpigotPluginUpdater] Error in checking update for: '" +pluginurl+"' invalid Pluginname!");
			System.out.println(" -- StackTrace --");
			e.printStackTrace();
			System.out.println(" -- End of StackTrace --");
		}
		this.plugin = plugin;
		this.pluginurl = pluginurl; 
	}
	
	
	private String version = "";
	private String downloadURL = "";
	private String changeLOG = "";
	
	private boolean out = true;
	/**
	 * Enable a console output if new Version is availible
	 */
	public void enableOut(){
		out = true;
	}
	
	/**
	 * Disable a console output if new Version is availible
	 */
	public void disableOut(){
		out = false;
	}
	
	/**
	 * Check for a new Update
	 * @return if new update is availible
	 */
	public boolean needsUpdate(){
		
		try {
			URLConnection con = url.openConnection();
			InputStream _in = con.getInputStream();
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(_in);
			
			Node nod = doc.getElementsByTagName("item").item(0);
			NodeList children = nod.getChildNodes();
			
			version = children.item(1).getTextContent();
			downloadURL = children.item(3).getTextContent();
			changeLOG = children.item(5).getTextContent();
			if(newVersion(plugin.getDescription().getVersion(),version.replaceAll("[a-zA-z ]", ""))) {
			if(out){
				System.out.println(" New Version found: " + version.replaceAll("[a-zA-z ]", ""));	
				System.out.println(" Download it here: " + downloadURL.toString());
				System.out.println(" Changelog: " + changeLOG);
			}
			
			return true;
			}
			
		} catch (IOException | SAXException | ParserConfigurationException e) {
			System.out.println("[SpigotPluginUpdater] Error in checking update for: '" +pluginurl+"' (invalid URL?) !");
			System.out.println(" -- StackTrace --");
			e.printStackTrace();
			System.out.println(" -- End of StackTrace --");
		}
		
		
		return false;
	}
	
	/**
	 * Checks if the newversion is really newer then the other one
	 * @param oldv
	 * @param newv
	 * @return if it is newer
	 */
	public boolean newVersion(String oldv, String newv){
		//System.out.println("Check " + oldv + " - " + newv);
		if(oldv != null  && newv != null ){
		oldv = oldv.replace('.', '_');
		newv = newv.replace('.', '_');
		//System.out.println("length: " + oldv.split("_").length +" || " + newv.split("_").length);
		if(oldv.split("_").length != 0 && oldv.split("_").length != 1 && newv.split("_").length != 0 && newv.split("_").length != 1){
			
		int vnum = Integer.valueOf(oldv.split("_")[0]);
		int vsec = Integer.valueOf(oldv.split("_")[1]);
		
		int newvnum = Integer.valueOf(newv.split("_")[0]);
		int newvsec = Integer.valueOf(newv.split("_")[1]);
		//System.out.println("Check2: " + vnum + " ? " + newvnum + " || " + vsec + " ? " + newvsec);
		if(newvnum > vnum)
			return true;
		
		if(newvnum == vnum){
			if(newvsec > vsec){
				return true;
			}
		}
		}
		}
		return false;
	}
	
	/**
	 * Executes the Update and trys to install it
	 */
	public void update(){
		try {
			URL download = new URL(getFolder(pluginurl)+downloadURL);
			
			 	BufferedInputStream in = null;
			    FileOutputStream fout = null;
			    try {
			    	if(out){
			    		plugin.getLogger().info("Trying to download from: " + getFolder(pluginurl)+downloadURL);
			    	}
			        in = new BufferedInputStream(download.openStream());
			        fout = new FileOutputStream("plugins/"+downloadURL);

			        final byte data[] = new byte[1024];
			        int count;
			        while ((count = in.read(data, 0, 1024)) != -1) {
			            fout.write(data, 0, count);
			        }
			    } finally {
			        if (in != null) {
			            in.close();
			        }
			        if (fout != null) {
			            fout.close();
			        }
			    }
			
			if(out){
				plugin.getLogger().info("Succesfully downloaded file: " + downloadURL);
				plugin.getLogger().info("To have the Features, you have to reload your Server now!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Executes the Update and trys to install it
	 * But uses an external link for downloading the File
	 */
	public void externalUpdate(){
		try {
			URL download = new URL(downloadURL);
			
			 	BufferedInputStream in = null;
			    FileOutputStream fout = null;
			    try {
			    	if(out){
			    		plugin.getLogger().info("Trying to download from: " + downloadURL);
			    	}
			        in = new BufferedInputStream(download.openStream());
			        fout = new FileOutputStream("plugins/"+plugin.getName());

			        final byte data[] = new byte[1024];
			        int count;
			        while ((count = in.read(data, 0, 1024)) != -1) {
			            fout.write(data, 0, count);
			        }
			    } finally {
			        if (in != null) {
			            in.close();
			        }
			        if (fout != null) {
			            fout.close();
			        }
			    }
			
			if(out){
				plugin.getLogger().info("Succesfully downloaded file: " + downloadURL);
				plugin.getLogger().info("To have the Features, you have to reload your Server now!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}




	private String getFolder(String s){
		String path = s;
		int lastslash = path.lastIndexOf("/");
//		for(int i = 0; i < path.length(); i++){
//			
//			if(c == '/'){
//				lastslash++;
//			}
//		}
		String folder = path.substring(0, lastslash+1);
		return folder;
	}
}
