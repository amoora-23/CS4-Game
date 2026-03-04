package game.engine.dataloader;

import game.engine.cards.*;
import java.io.*;
import java.util.ArrayList;

public class DataLoader  {
	private static String CARDS_FILE_NAME = "E:/GUC Courses/New folder/el gayar - 69/Game CSV files/cards.csv";
	private static String CELLS_FILE_NAME = "E:/GUC Courses/New folder/el gayar - 69/Game CSV files/cells.csv";
	private static String MONSTERS_FILE_NAME = "E:/GUC Courses/New folder/el gayar - 69/Game CSV files/monsters.csv";
	
	public static ArrayList<Card> readCards() throws IOException {
	    ArrayList<Card> cards = new ArrayList<Card>();
	    
	    
	    BufferedReader br = new BufferedReader(new FileReader(CARDS_FILE_NAME));
	    String line;
	    
	    while ((line = br.readLine()) != null) {
	        String[] values = line.split(",");
	        
	        switch(values[0]){
	        case "SWAPPER": cards.add(new SwapperCard(values[1], values[2], Integer.parseInt(values[3]))); break;
	        case "STARTOVER": cards.add(new StartOverCard(values[1], values[2], Integer.parseInt(values[3]), Boolean.parseBoolean(values[4]))); break;
	        case "ENERGYSTEAL": cards.add(new EnergyStealCard(values[1], values[2], Integer.parseInt(values[3]), Integer.parseInt(values[4]))); break;
	        case "SHIELD": cards.add(new ShieldCard(values[1], values[2], Integer.parseInt(values[3]))); break;
	        case "CONFUSION": cards.add(new ConfusionCard(values[1], values[2], Integer.parseInt(values[3]), Integer.parseInt(values[4]))); break;
	        }
	        
	    }
	    
	    br.close();
	    return cards;
	}
}
