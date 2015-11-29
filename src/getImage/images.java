package getImage;

//got starter code from 
//http://stackoverflow.com/questions/11485578/how-do-save-image-from-google-images-using-google-api

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.json.JSONObject;

public class images
{
	public images(String jsonData, String location)
	{
		//all code got from stack overflow (upvoted of course)
		//any changes i have made are commented
		try{
			//concatenated location onto URL for API to get results based on user inputted ocation
	        URL url = new URL("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="+location);
	        URLConnection connection = url.openConnection();

	        String line;
	        StringBuilder builder = new StringBuilder();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        while((line = reader.readLine()) != null) {
	            builder.append(line);
	        }

	        JSONObject json = new JSONObject(builder.toString());
	        System.out.println(json);
	        String imageUrl = json.getJSONObject("responseData").getJSONArray("results").getJSONObject(0).getString("url");

	        BufferedImage image = ImageIO.read(new URL(imageUrl));

	        ImageIcon imageIcon = new ImageIcon(image); // load the image to a imageIcon
			Image resizingImage = imageIcon.getImage(); // transform it 
		    Image newimg = resizingImage.getScaledInstance(240, 240,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
		    imageIcon = new ImageIcon(newimg);  // transform it back
		    
		    //System.out.println(jsonData);
	        
	        JOptionPane.showMessageDialog(null, jsonData, location+ " weather report", JOptionPane.INFORMATION_MESSAGE, imageIcon);
	        

			//JOptionPane.showMessageDialog(null, jsonData, result_name+" weather report", JOptionPane.OK_OPTION, icon);
	    } catch(Exception e){
	        JOptionPane.showMessageDialog(null, e.getMessage(), "Failure", JOptionPane.ERROR_MESSAGE);
	        e.printStackTrace();
	    }
	}
}
