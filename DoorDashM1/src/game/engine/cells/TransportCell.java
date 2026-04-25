package game.engine.cells;
import game.engine.monsters.*;


public abstract class TransportCell extends Cell {
	private int effect;

	public TransportCell(String name, int effect) {
		super(name);
		this.effect = effect;
	}

	public int getEffect() {
		return effect;
	}
	public void transport(Monster monster){
		if(monster instanceof Dasher){
			((Dasher)monster).moveParent(effect);
			return;
		}
			
		monster.move(effect);
	}

	@Override
	public void onLand(Monster landingMonster, Monster opponentMonster) {
		super.onLand(landingMonster, opponentMonster);
		transport(landingMonster);
	}

}
