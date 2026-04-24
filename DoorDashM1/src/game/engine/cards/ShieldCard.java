package game.engine.cards;

import java.util.ArrayList;

import game.engine.Board;
import game.engine.monsters.Monster;

public class ShieldCard extends Card {
	
	public ShieldCard(String name, String description, int rarity) {
		super(name, description, rarity, true); 
	}
	public void performAction(Monster player, Monster opponent){
		ArrayList<Monster> monsters= Board.getStationedMonsters();
		if(opponent.isShielded())
			opponent.setShielded(false);
		for(int i=0; i<monsters.size();i++)
		{
			if(monsters.get(i).getRole()==player.getRole())
				monsters.get(i).setShielded(true);
			else
				monsters.get(i).setShielded(false);
		}
		player.setShielded(true);
	}

}
