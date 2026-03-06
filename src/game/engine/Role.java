package game.engine;

public enum Role {
	SCARER,
	LAUGHER;
	
	  public Role negate() {
        return (this == SCARER) ? LAUGHER : SCARER;
	}
    
}
