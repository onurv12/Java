package rps.game;

import java.rmi.RemoteException;

import rps.client.GameListener;
import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.AttackResult;
import rps.game.data.Move;
import rps.game.data.Player;

/**
 * The {@code GameImpl} is an implementation for the {@code Game} interface. It
 * contains the necessary logic to play a game.
 * 
 * @author Lukas Appelhans
 * 
 * FIXME: Unfortunately there is a lot of code to be cleaned up here, will be done asap
 */
public class GameImpl implements Game {

	private GameListener listener1;
	private GameListener listener2;
	private Player player1;
	private Player player2;
	private Figure [] field = new Figure[42];
    private FigureKind p1initialChoice;
    private FigureKind p2initialChoice;
    private Move previousMove;
    private boolean player1AssignmentDone = false;
    private boolean player2AssignmentDone = false;
    private Player surrendered = null;
    
    
	public GameImpl(GameListener listener1, GameListener listener2) {
		this.listener1 = listener1;
		this.listener2 = listener2;
		try {
			this.player1 = listener1.getPlayer();
			this.player2 = listener2.getPlayer();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void sendMessage(Player p, String message) throws RemoteException {
		listener1.chatMessage(p, message);
		listener2.chatMessage(p, message);
		// TODO Auto-generated method stub
	}

	@Override
	public void setInitialAssignment(Player p, FigureKind[] assignment) throws RemoteException {
		if ((p.equals(player1) && player1AssignmentDone) || (p.equals(player2) && player2AssignmentDone)) {
			throw new IllegalStateException("Cannot provide two initial assignments!");
		}
		boolean conflict = false;
		for (int i = 0; i != assignment.length; i++) {
			if (assignment[i] != null) {
				if (field[i] != null) {
					conflict = true;
				} else {
					field[i] = new Figure(assignment[i], p);
				}
			}
		}
		if (p.equals(player1)) {
			player1AssignmentDone = true;
		} else {
			player2AssignmentDone = true;
		}
		if (conflict) {
			player1AssignmentDone = false;
			player2AssignmentDone = false;
			listener1.provideInitialAssignment(this);
			listener2.provideInitialAssignment(this);
		} else if (player1AssignmentDone && player2AssignmentDone) {
			listener1.provideInitialChoice();
			listener2.provideInitialChoice();
		}
	}

	@Override
	public void setInitialChoice(Player p, FigureKind kind) throws RemoteException {
		if (p.equals(player1)) {
			p1initialChoice = kind;
		} else if (p.equals(player2)) {
			p2initialChoice = kind;
		}
		if (p1initialChoice != null && p2initialChoice != null) {
			AttackResult r = p1initialChoice.attack(p2initialChoice);
			switch (r) {
				case DRAW:
					p1initialChoice = null;
					p2initialChoice = null;
					listener1.provideInitialChoice();
					listener2.provideInitialChoice();
					break;
				case WIN:
					listener1.startGame();
					listener2.startGame();
					listener1.provideNextMove();
					break;
				case LOOSE:
					listener1.startGame();
					listener2.startGame();
					listener2.provideNextMove();
					break;
				default:
					throw new IllegalStateException("Error occurred, how can this be a flag or trap?");
			}
		}
	}

	@Override
	public void move(Player movingPlayer, int fromIndex, int toIndex) throws RemoteException {
		GameListener movingListener;
		GameListener opponentListener;
		Player opponentPlayer;
		if (movingPlayer.equals(player1)) {
			movingListener = listener1;
			opponentListener = listener2;
			opponentPlayer = player2;
		} else {
			movingListener = listener2;
			opponentListener = listener1;
			opponentPlayer = player1;
		}
		
		if (previousMove != null &&
				previousMove.getOldField()[previousMove.getFrom()].belongsTo(movingPlayer) && (movableCount(opponentPlayer) > 0)) {
			throw new IllegalStateException("Cannot move twice!");
		}
		
		if (field[fromIndex] == null) {
			movingListener.provideNextMove();
			throw new IllegalStateException("Cannot move no figure!"); //TODO better solution for exception handling here
		}
		
		//if (!field[fromIndex].getKind().isMovable()) {
		//	movingListener.provideNextMove();
		//	throw new IllegalStateException("Figure is not movable!"); //TODO better solution for exception handling here
		//}
		
		if ((field[toIndex] != null) && (field[toIndex].belongsTo(movingPlayer))) {
			movingListener.provideNextMove();
			throw new IllegalStateException("Cannot attack your own figure!"); //TODO better solution for exception handling here
		}
		
		movePriv(movingPlayer, opponentPlayer, movingListener, opponentListener, fromIndex, toIndex);
	}
	
	private int movableCount(Player p) {
		int count = 0;
		for (int i = 0; i != field.length; i++) {
			if (field[i] != null && field[i].belongsTo(p) && isMovable(i, p))
			    count++;
		}
		return count;
	}
	
	private boolean isMovable(int fieldnumber, Player player) {
		if (!field[fieldnumber].getKind().isMovable()) {
			return false;
		}
		int frontMoveField = (fieldnumber - 7);
		if (frontMoveField < 0)
			frontMoveField = -1;
		int backMoveField = (fieldnumber + 7);
		if (backMoveField > 41) {
			backMoveField = -1;
		}
		int rightSideMove = (fieldnumber + 1);
		if ((fieldnumber % 7) == 6) { //We're on the right side already
			rightSideMove = -1;
		}
		int leftSideMove = (fieldnumber - 1);
		if ((fieldnumber % 7) == 0) {
			leftSideMove = -1;
		}
		return !(((frontMoveField == -1) || ((field[frontMoveField] != null) && (field[frontMoveField].belongsTo(player)))) && 
				 ((backMoveField == -1) || ((field[backMoveField] != null) && (field[backMoveField].belongsTo(player)))) && 
			   	 ((rightSideMove == -1) || ((field[rightSideMove] != null) && (field[rightSideMove].belongsTo(player)))) && 
				 ((leftSideMove == -1) || ((field[leftSideMove] != null) && (field[leftSideMove].belongsTo(player)))));
	}
	
	private void movePriv(Player movingPlayer, Player opponentPlayer, GameListener movingListener, GameListener opponentListener, int fromIndex, int toIndex) throws RemoteException {
		previousMove = new Move(fromIndex, toIndex, field.clone());
		Figure [] oldField = field.clone();
		//Let's just move!
		if (field[toIndex] == null) {
			field[toIndex] = field[fromIndex];
			field[fromIndex] = null;
		} else { //Okay an attack is needed
			movingListener.figureAttacked();
			opponentListener.figureAttacked();
			AttackResult r = field[fromIndex].attack(field[toIndex]);
			oldField[fromIndex].setDiscovered();
			oldField[toIndex].setDiscovered();
			switch (r) {
			case DRAW:
				p1initialChoice = null;
				p2initialChoice = null;
				movingListener.provideChoiceAfterFightIsDrawn();
				opponentListener.provideChoiceAfterFightIsDrawn();
				return;
			case WIN:
				field[toIndex] = field[fromIndex];
				field[fromIndex] = null;
				field[toIndex].setDiscovered();
				break;
			case LOOSE_AGAINST_TRAP:
				field[toIndex] = null;
			case LOOSE:
				field[fromIndex] = null;
				if (field[toIndex] != null) {
					field[toIndex].setDiscovered();
				}
				break;
			case WIN_AGAINST_FLAG:
				movingListener.gameIsWon();
				opponentListener.gameIsLost();
				uncoverAll();
				return;

			}
		}
		opponentListener.figureMoved();
		movingListener.figureMoved();
		int movingCount = movableCount(movingPlayer);
		int opponentCount = movableCount(opponentPlayer);
		if (opponentCount > 0) {
			opponentListener.provideNextMove();
		} else if (movingCount > 0) {
			movingListener.provideNextMove();
		} else {
			opponentListener.gameIsDrawn();
			movingListener.gameIsDrawn();
		}
	}
	
	private void uncoverAll() {
		for (Figure f : field) {
			if (f != null)
				f.setDiscovered();
		}
	}

	@Override
	public void setUpdatedKindAfterDraw(Player p, FigureKind kind) throws RemoteException {
		if (player1.equals(p)) {
			p1initialChoice = kind;
		} else {
			p2initialChoice = kind;
		}

		if (p1initialChoice != null && p2initialChoice != null) {
			GameListener mL;
			GameListener oL;
			Player mP;
			Player oP;
			if (field[previousMove.getFrom()].belongsTo(player1)) {
				mL = listener1;
			    mP = player1;
				oP = player2;
				oL = listener2;
				field[previousMove.getFrom()] = new Figure(p1initialChoice, player1);
				field[previousMove.getTo()] = new Figure(p2initialChoice, player2);
			} else {
				mL = listener2;
			    mP = player2;
				oP = player1;
				oL = listener1;
				field[previousMove.getFrom()] = new Figure(p2initialChoice, player2);
				field[previousMove.getTo()] = new Figure(p1initialChoice, player1);
			}
			movePriv(mP, oP, mL, oL, previousMove.getFrom(), previousMove.getTo());
		}
	}

	@Override
	public void surrender(Player p) throws RemoteException {
		if (surrendered != null && p != surrendered) {
			throw new IllegalStateException("Two players cannot both surrender!");
		}
		listener1.chatMessage(p, "I surrender");
		listener2.chatMessage(p, "I surrender");
		if (p.equals(player1)) {
			listener1.gameIsLost();
			listener2.gameIsWon();
		} else {
			listener2.gameIsLost();
			listener1.gameIsWon();
		}
		uncoverAll();
		surrendered = p;
	}

	@Override
	public Figure[] getField() throws RemoteException {
		return field;
	}

	@Override
	public Move getLastMove() throws RemoteException {
		return previousMove;
	}

	@Override
	public Player getOpponent(Player p) throws RemoteException {
		if (p.equals(player1))
			return player2;
		return player1;
	}
}