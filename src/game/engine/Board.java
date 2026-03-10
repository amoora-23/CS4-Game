package game.engine;

import java.util.ArrayList;

import game.engine.cards.*;
import game.engine.cells.*;
import game.engine.monsters.*;

public class Board {
	private Cell[][] boardCells; //BOARD ROWS x BOARD COLS
	private static ArrayList<Monster> stationedMonsters; 
	private static ArrayList<Card> originalCards;
	public static ArrayList<Card> cards;
	
	public Cell[][] getBoardCells() {
		return boardCells;
	}
	public static ArrayList<Monster> getStationedMonsters() {
		return stationedMonsters;
	}
	public static ArrayList<Card> getOriginalCards() {
		return originalCards;
	}
	public static ArrayList<Card> getCards() {
		return cards;
	}
	public static void setStationedMonsters(ArrayList<Monster> newStationedMonsters) {
		stationedMonsters = newStationedMonsters;
	}
	public static void setCards(ArrayList<Card> newCards) {
		cards = newCards;
	}	
	public Board(ArrayList<Card> readCards){
		Constants c = new Constants();
		boardCells = new Cell[c.BOARD_ROWS][c.BOARD_COLS];
		stationedMonsters = new ArrayList<Monster>();
		cards = new ArrayList<Card>();
		originalCards = readCards;
	}
	
	
	

}
