package game.engine.monsters;

import game.engine.Role;

public class MultiTasker extends Monster {
	private int normalSpeedTurns;
	
	public MultiTasker(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
		this.normalSpeedTurns = 0;
	}

	public int getNormalSpeedTurns() {
		return normalSpeedTurns;
	}

	public void setNormalSpeedTurns(int normalSpeedTurns) {
		this.normalSpeedTurns = normalSpeedTurns;
	}
	public void setPosition(int position)
	{
		int change=position-getPosition();
		if(normalSpeedTurns == 0)
			change=change/2;
		position=getPosition()+change;
		super.setPosition(position);
	}
	public void setEnergy(int energy)
	{
		int change=energy-getEnergy();
		change=200+change;
		energy=getEnergy()+change;
		super.setEnergy(energy);
	}

	@Override
	public void executePowerupEffect(Monster opponentMonster) {
		this.normalSpeedTurns = 2;
	}
}