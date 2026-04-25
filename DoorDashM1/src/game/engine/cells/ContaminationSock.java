package game.engine.cells;

import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;
import game.engine.*;

public class ContaminationSock extends TransportCell implements CanisterModifier {

	public ContaminationSock(String name, int effect) {
		super(name, effect);
	}

	@Override
	public void modifyCanisterEnergy(Monster monster, int canisterValue) {
		monster.alterEnergy(canisterValue);
	}

	@Override
	public void onLand(Monster landingMonster, Monster opponentMonster) {
		super.onLand(landingMonster, opponentMonster);
	}

	@Override
	public void transport(Monster monster) {
		super.transport(monster);
		if(monster.isShielded())
			monster.setShielded(false);
		else
			modifyCanisterEnergy(monster,-Constants.SLIP_PENALTY);
	}
	

	
	
	
}

