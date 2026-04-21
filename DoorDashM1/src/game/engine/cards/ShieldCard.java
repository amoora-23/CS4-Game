package game.engine.cards;

import game.engine.monsters.Monster;

public class ShieldCard extends Card {
	
	public ShieldCard(String name, String description, int rarity) {
		super(name, description, rarity, true); 
	}
	void performAction(Monster player, Monster opponent){
		if(player.isShielded()==false)
			player.setShielded(true);
		if(opponent.isShielded()==true)
			player.setShielded(false);
	}

}
