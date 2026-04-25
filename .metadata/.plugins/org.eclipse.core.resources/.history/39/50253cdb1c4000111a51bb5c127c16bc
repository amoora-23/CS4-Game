package game.engine.cells;

import game.engine.monsters.*;

public class MonsterCell extends Cell {
	private Monster cellMonster;

	public MonsterCell(String name, Monster cellMonster) {
		super(name);
		this.cellMonster = cellMonster;
	}

	public Monster getCellMonster() {
		return cellMonster;
	}

	public void onLand(Monster landingMonster, Monster opponentMonster) {
		super.onLand(landingMonster, opponentMonster);
		if(landingMonster.getRole()== cellMonster.getRole()){
			landingMonster.executePowerupEffect(opponentMonster);
			return;
		}
		else{
			int diff = cellMonster.getEnergy() -landingMonster.getEnergy();
			if(diff < 0){
				cellMonster.setEnergy(landingMonster.getEnergy());
				landingMonster.alterEnergy(diff);
			}
		}
	}
	
}
