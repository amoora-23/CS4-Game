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
	

	public void onLand(Monster landingMonster, Monster opponentMonster) {
		super.onLand(landingMonster, opponentMonster);
		if(!activated )
		{
			if(landingMonster.isShielded()&& landingMonster.getRole()!=role)
			{
				landingMonster.setShielded(false);
				ArrayList<Monster> Monsters = Board.getStationedMonsters();
				for(int i = 0 ; i < Monsters.size(); i++){
					if(Monsters.get(i).getRole() == landingMonster.getRole())
						Monsters.get(i).setShielded(false);
				}
			}
			else
				modifyCanisterEnergy(landingMonster, energy);
		}
		}
	}
	


