package game.engine.cards;

import game.engine.Role;
import game.engine.monsters.Monster;

public class ConfusionCard extends Card {
	private int duration;
	
	public ConfusionCard(String name, String description, int rarity, int duration) {
		super(name, description, rarity, false);
		this.duration = duration;
	}
	
	public int getDuration() {
		return duration;
	}
	void performAction(Monster player, Monster opponent){
		if(this.getRarity()==3)
			duration=2;
		else if(this.getRarity()==2)
			duration=3;
		player.setConfusionTurns(duration);
		opponent.setConfusionTurns(duration);
		Role temp = player.getRole();
		player.setRole(opponent.getRole());
		opponent.setRole(temp);
	}

}
