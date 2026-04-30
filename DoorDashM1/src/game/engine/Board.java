package game.engine;

import java.util.*;

import game.engine.cards.Card;
import game.engine.cells.*;
import game.engine.exceptions.InvalidMoveException;
import game.engine.monsters.Monster;

public class Board {
	private Cell[][] boardCells;
	private static ArrayList<Monster> stationedMonsters= new ArrayList<Monster>(); 
	private static ArrayList<Card> originalCards= new ArrayList<Card>();
	public static ArrayList<Card> cards= new ArrayList<Card>();
	
	
	public Board(ArrayList<Card> readCards) {
		this.boardCells = new Cell[Constants.BOARD_ROWS][Constants.BOARD_COLS];
		stationedMonsters = new ArrayList<Monster>();
		originalCards = readCards;
		cards = new ArrayList<Card>();
		setCardsByRarity();
		reloadCards();
	}
	
	public Cell[][] getBoardCells() {
		return boardCells;
	}
	
	public static ArrayList<Monster> getStationedMonsters() {
		return stationedMonsters;
	}
	
	public static void setStationedMonsters(ArrayList<Monster> stationedMonsters) {
		Board.stationedMonsters = stationedMonsters;
	}

	public static ArrayList<Card> getOriginalCards() {
		return originalCards;
	}
	
	public static ArrayList<Card> getCards() {
		return cards;
	}
	
	public static void setCards(ArrayList<Card> cards) {
		Board.cards = cards;
	}
	public static void reloadCards(){
		cards = new ArrayList<>(originalCards);
		Collections.shuffle(cards); 
	}
	public static Card drawCard(){
		if(cards.isEmpty())
			reloadCards();
		return cards.remove(0);
	}
	
	private int[] indexToRowCol(int index){
		int[] res = new int[2];
		int row = index/10;
		int column = index%10;
		res[0] = row;
		if(row%2!=0)
			res[1] = 9-column;
		else
			res[1] = column;
		return res;
	}
	
	private Cell getCell(int index){
		int[] pos = indexToRowCol(index);
		return boardCells[pos[0]][pos[1]];
	}
	
	private void setCell(int index, Cell cell){
		int[] pos = indexToRowCol(index);
		boardCells[pos[0]][pos[1]] = cell;
	}
	
	public void initializeBoard(ArrayList<Cell> specialCells){
		int index_conveyer = 0;
		int index_socks = 0;
		int odds = 1;
		for(Cell c: specialCells){
			if(c instanceof ConveyorBelt){
				setCell(Constants.CONVEYOR_CELL_INDICES[index_conveyer++], c);
			}
			else if (c instanceof ContaminationSock)
				setCell(Constants.SOCK_CELL_INDICES[index_socks++], c);
			else if (c instanceof DoorCell)
				{
					setCell(odds , c);
					odds+=2;
				}
		}
		for(int i = 0; i<Constants.CARD_CELL_INDICES.length; i++)
			setCell(Constants.CARD_CELL_INDICES[i], new CardCell("Card cell"));
		
		for(int i = 0; i<stationedMonsters.size() ; i++){
			setCell(Constants.MONSTER_CELL_INDICES[i], new MonsterCell(stationedMonsters.get(i).getName(), stationedMonsters.get(i)));
			stationedMonsters.get(i).setPosition(Constants.MONSTER_CELL_INDICES[i]);
		}
		
		for(int i = 0; i<Constants.BOARD_SIZE; i++){
			if(getCell(i)== null&&i%2==0)
				setCell(i, new Cell("Normal Cell"));	
		}
	}
	
	private void setCardsByRarity() {
	    ArrayList<Card> expandedCards = new ArrayList<>();
	    for (Card card : Board.getOriginalCards()) {
	        for (int i = 0; i < card.getRarity(); i++) {
	            expandedCards.add(card);
	        }
	    }
	    originalCards = expandedCards;
	}
	
	
	public void moveMonster(Monster currentMonster, int roll, Monster opponentMonster) throws InvalidMoveException{
		currentMonster.move(roll);
		boolean wasConfused = currentMonster.isConfused();
		int pos = currentMonster.getPosition();
		if(opponentMonster.getPosition() == pos){
			currentMonster.move(-1*roll);
			throw new InvalidMoveException();
		}
		Cell cell = getCell(pos);
		cell.onLand(currentMonster, opponentMonster);
		if(wasConfused){
			currentMonster.decrementConfusion();
			opponentMonster.decrementConfusion();
		}
		updateMonsterPositions(currentMonster, opponentMonster);
	}
	
	private void updateMonsterPositions(Monster player, Monster opponent){
		for( Cell[] row: boardCells)
			for( Cell c: row)
				c.setMonster(null);
		
		Cell c = getCell(player.getPosition());
		c.setMonster(player);
		
		c = getCell(opponent.getPosition());
		c.setMonster(opponent);
	}
	
}
