package game.engine.monsters;

import game.engine.Role;

public class Dasher extends Monster {
	private int momentumTurns;

	public Dasher(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
		this.momentumTurns = 0;
	}
	
	public int getMomentumTurns() {
		return momentumTurns;
	}
	
	public void setMomentumTurns(int momentumTurns) {
		this.momentumTurns = momentumTurns;
	}
	public void setPosition(int position)
	{	
		int change=position-getPosition();
		if(this.momentumTurns == 0)
			change=2*change;
		else
			change=3*change;
		position=getPosition()+change;
		super.setPosition(position);
	}

	@Override
	public void executePowerupEffect(Monster opponentMonster) {
		this.momentumTurns = 3;
	}

}