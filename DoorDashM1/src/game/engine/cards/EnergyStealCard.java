package game.engine.cards;

import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;

public class EnergyStealCard extends Card implements CanisterModifier {
	private int energy;

	public EnergyStealCard(String name, String description, int rarity, int energy) {
		super(name, description, rarity, true);
		this.energy = energy;
	}
	
	public int getEnergy() {
		return energy;
	}
	public void modifyCanisterEnergy(Monster monster, int canisterValue) {
		monster.alterEnergy(canisterValue);
		
	}

	void performAction(Monster player, Monster opponent) {
		if(this.getRarity()==3){
			if(opponent.getEnergy()<50){
				player.alterEnergy(opponent.getEnergy());
				opponent.alterEnergy(-opponent.getEnergy());
				modifyCanisterEnergy(player, opponent.getEnergy());
				modifyCanisterEnergy(opponent, -opponent.getEnergy());
			}
			else{
			player.alterEnergy(50);
			opponent.alterEnergy(-50);
			modifyCanisterEnergy(player, 50);
            modifyCanisterEnergy(opponent, -50);

			}
		}
		else if(this.getRarity()==2){
			if(opponent.getEnergy()<100){
				player.alterEnergy(opponent.getEnergy());
				opponent.alterEnergy(-opponent.getEnergy());
				modifyCanisterEnergy(player, opponent.getEnergy());
				modifyCanisterEnergy(opponent, -opponent.getEnergy());
			}
			else{
			player.alterEnergy(100);
			opponent.alterEnergy(-100);
			modifyCanisterEnergy(player, 100);
            modifyCanisterEnergy(opponent, -100);

			}
		}
		else if(this.getRarity()==1){
			if(opponent.getEnergy()<150){
				player.alterEnergy(opponent.getEnergy());
				opponent.alterEnergy(-opponent.getEnergy());
				modifyCanisterEnergy(player, opponent.getEnergy());
				modifyCanisterEnergy(opponent, -opponent.getEnergy());
			}
			else{
			player.alterEnergy(150);
			opponent.alterEnergy(150);
			modifyCanisterEnergy(player, 150);
			modifyCanisterEnergy(opponent, -150);

			}
		}
	}
}

