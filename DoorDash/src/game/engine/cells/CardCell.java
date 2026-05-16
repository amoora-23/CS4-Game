package game.engine.cells;
 
import game.engine.Board;
import game.engine.cards.Card;
import game.engine.monsters.Monster;
 
/**
 * CardCell — draws a card on landing and notifies any registered GUI listener.
 *
 * The listener is optional; if none is set the cell works exactly as before.
 * The GameBoardController registers itself via CardCell.setCardDrawnListener(...)
 * before each turn so it can update the card panel.
 */
public class CardCell extends Cell {
 
    /** Simple functional interface so we don't need a JavaFX import here. */
    public interface CardDrawnListener {
        void onCardDrawn(Card card);
    }
 
    // Static listener — one per game session, set by the GUI controller.
    private static CardDrawnListener listener;
 
    public static void setCardDrawnListener(CardDrawnListener l) {
        listener = l;
    }
 
    public CardCell(String name) {
        super(name);
    }
 
    @Override
    public void onLand(Monster landingMonster, Monster opponentMonster) {
        super.onLand(landingMonster, opponentMonster);
 
        Card card = Board.drawCard();
 
        // Notify GUI (if connected) BEFORE the effect so the panel updates first
        if (listener != null) {
            listener.onCardDrawn(card);
        }
 
        card.performAction(landingMonster, opponentMonster);
    }
}