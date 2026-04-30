package game.engine.monsters;

import java.util.ArrayList;

import game.engine.Constants;
import game.engine.Role;
import game.engine.Board;

public class Schemer extends Monster {
	
	public Schemer(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
	}
	
	private int stealEnergyFrom(Monster target){
		int gained = 0;
		if(Constants.SCHEMER_STEAL<target.getEnergy()){
			gained = Constants.SCHEMER_STEAL;
			target.alterEnergy(-gained);
		}
		else{
			gained = target.getEnergy();
			target.alterEnergy(-gained);
		}
		return gained;
	}
	
	@Override
	public void executePowerupEffect(Monster opponentMonster) {
		ArrayList<Monster> monsters = Board.getStationedMonsters();
		int total = stealEnergyFrom(opponentMonster);
		for(int i = 0; i<monsters.size(); i++){
			total += stealEnergyFrom(monsters.get(i));
		}
		this.alterEnergy(total);
		return;
	}
}
