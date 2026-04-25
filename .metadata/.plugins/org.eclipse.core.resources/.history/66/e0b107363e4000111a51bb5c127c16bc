package game.engine.monsters;

import java.util.ArrayList;

import game.engine.Constants;
import game.engine.Role;
import game.engine.Board;

public class Schemer extends Monster {
	
	public Schemer(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
	}
	public void setEnergy(int energy)
	{
		int change=energy-getEnergy();
		change=10+change;
		energy=getEnergy()+change;
		super.setEnergy(energy);
	}
	
	private int stealEnergyFrom(Monster target){
		int gained = 0;
		if(Constants.SCHEMER_STEAL<target.getEnergy()){
			gained = Constants.SCHEMER_STEAL;
			target.setEnergy(target.getEnergy()-gained);
		}
		else{
			gained = target.getEnergy();
			target.setEnergy(0);
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
		this.setEnergy(this.getEnergy()+total);
		return;
	}
}
