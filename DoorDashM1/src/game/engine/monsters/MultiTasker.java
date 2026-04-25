package game.engine.monsters;

import game.engine.Constants;
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
	public void move(int distance)
	{
		if(normalSpeedTurns >0){
			super.move((int)distance);
			normalSpeedTurns--;
		}
		else
			super.move((int)distance/2);
	}
	public void setEnergy(int energy)
	{
		int change=energy-getEnergy();
		change=Constants.MULTITASKER_BONUS+change;
		energy=getEnergy()+change;
		super.setEnergy(energy);
	}
	
	

	@Override
	public void executePowerupEffect(Monster opponentMonster) {
		this.normalSpeedTurns = 2;
	}
}