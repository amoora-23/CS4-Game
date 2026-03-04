package game.engine;

public final class Constants {

	//Board constants:
	public int BOARD_SIZE = 100;
	public int BOARD_ROWS = 10;
	public int BOARD_COLS = 10;
	public int WINNING_POSITION = 99;
	public int STARTING_POSITION = 0;
	
	//Special cells positions constants:
	public int[] MONSTER_CELL_INDICES = {2, 18, 34, 54, 82, 88};
	public int[] CONVEYOR_CELL_INDICES = {6, 22, 44, 52, 66}; //(Start of the belt)
	public int[] SOCK_CELL_INDICES = {32, 42, 74, 84, 98}; //(Start of the sock)
	public int[] CARD_CELL_INDICES = {4, 12, 28, 36, 48, 56, 60, 76, 86, 90};

	// energy constants:
	public int WINNING_ENERGY = 1000;
	public int MIN_ENERGY = 0;
	
	//Monster constants:
	public int MULTITASKER_BONUS = 200;
	public int SCHEMER_STEAL = 10;
	
	//Cell constants:
	public int SLIP_PENALTY = 100;
	
	//Power constants:
	public int POWERUP_COST = 500;
}
