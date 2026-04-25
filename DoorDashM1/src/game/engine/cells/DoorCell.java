package game.engine.cells;
import java.util.*;
import game.engine.*;
import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;

public class DoorCell extends Cell implements CanisterModifier {
	private Role role;
	private int energy;
	private boolean activated;
	
	public DoorCell(String name, Role role, int energy) {
		super(name);
		this.role = role;
		this.energy = energy;
		this.activated = false;
	}
	
	public Role getRole() {
		return role;
	}
	
	public int getEnergy() {
		return energy;
	}
	
	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean isActivated) {
		this.activated = isActivated;
	}

	public void modifyCanisterEnergy(Monster monster, int canisterValue) {
		int change = (monster.getRole()==role) ? canisterValue : -canisterValue;
		int initial = monster.getEnergy();
		monster.alterEnergy(change);
		ArrayList<Monster> Monsters = Board.getStationedMonsters();
		for(int i = 0 ; i < Monsters.size(); i++){
			if(Monsters.get(i).getRole() == monster.getRole())
				Monsters.get(i).alterEnergy(change);
		}
		if(initial != monster.getEnergy())
			activated = true;
		
	}
	
	/*public void modifyCanisterEnergy(Monster monster, int canisterValue) {
		int initial = monster.getEnergy();
		monster.setEnergy(initial+canisterValue);
		ArrayList<Monster> Monsters = Board.getStationedMonsters();
		if(canisterValue>0){
			monster.setEnergy(initial+canisterValue);
			for(int i = 0 ; i < Monsters.size(); i++){
				initial = Monsters.get(i).getEnergy();
				if(Monsters.get(i).getRole() == monster.getRole())
					Monsters.get(i).setEnergy(initial+canisterValue);
			}
		}
		else{
			if(monster.isShielded()){
				monster.setShielded(false);
				for(int i = 0 ; i < Monsters.size(); i++){
					if(Monsters.get(i).getRole() == monster.getRole())
						Monsters.get(i).setShielded(false);
				}
			}
			else{
				monster.setEnergy(initial+canisterValue);
				for(int i = 0 ; i < Monsters.size(); i++){
					initial = Monsters.get(i).getEnergy();
					if(Monsters.get(i).getRole() == monster.getRole())
						Monsters.get(i).setEnergy(initial+canisterValue);
				}
			}
		}
		
	}*/
	
	/*@Override
	public void modifyCanisterEnergy(Monster monster, int canisterValue) {
		boolean isSameRole = (monster.getRole() == this.role);
		int change = isSameRole ? canisterValue : -canisterValue;

		boolean protectedByShield = (!isSameRole && monster.isShielded());

		monster.alterEnergy(change);

		if (!protectedByShield) {
			for (Monster teammate : Board.getStationedMonsters()) {
				if (teammate.getRole() == monster.getRole()) {
					teammate.alterEnergy(change);
				}
			}
		}
	}*/
	
	/*@Override
	public void onLand(Monster landingMonster, Monster opponentMonster) {
		super.onLand(landingMonster, opponentMonster);
		if (activated)
			return;

		boolean hadShield = landingMonster.isShielded();
		modifyCanisterEnergy(landingMonster, this.energy);

		if (landingMonster.getRole() == this.role || !hadShield) {
			activated = true;
		}
	}*/

	public void onLand(Monster landingMonster, Monster opponentMonster) {
		super.onLand(landingMonster, opponentMonster);
		if(!activated )
			modifyCanisterEnergy(landingMonster, energy);
		}
	}
	


