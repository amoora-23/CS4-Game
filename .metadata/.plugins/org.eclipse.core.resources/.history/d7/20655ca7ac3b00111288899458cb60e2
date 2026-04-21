package game.engine.monsters;

import game.engine.Role;

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
}
