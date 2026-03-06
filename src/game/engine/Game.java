package game.engine;

import game.engine.monsters.*;

import java.io.IOException;
import java.util.*;

import game.engine.dataloader.*;

public class Game {
	private Board board;
	private ArrayList<Monster> allMonsters;
	private Monster player;
	private Monster opponent;
	private Monster current;
	public Board getBoard() {
		return board;
	}
	public ArrayList<Monster> getAllMonsters() {
		return allMonsters;
	}
	public Monster getPlayer() {
		return player;
	}
	public Monster getOpponent() {
		return opponent;
	}
	public Monster getCurrent() {
		return current;
	}
	public void setCurrent(Monster current) {
		this.current = current;
	}
	
	public Game(Role playerRole) throws IOException{
		board = new Board(DataLoader.readCards());
		allMonsters = new ArrayList<Monster>(DataLoader.readMonsters());
		player = selectRandomMonsterByRole(playerRole);
		opponent = selectRandomMonsterByRole(playerRole.negate());
		current = player;
	}
	
	private Monster selectRandomMonsterByRole(Role role){
		Monster m = allMonsters.get((int)(Math.random()*allMonsters.size()));
		while(role != m.getRole())
			m = allMonsters.get((int)(Math.random()*allMonsters.size()));
		return m;
	}
	

}
