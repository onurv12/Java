package rps.game;

import static rps.game.data.FigureKind.FLAG;
import static rps.game.data.FigureKind.HIDDEN;
import static rps.game.data.FigureKind.PAPER;
import static rps.game.data.FigureKind.ROCK;
import static rps.game.data.FigureKind.SCISSORS;
import static rps.game.data.FigureKind.TRAP;

import java.rmi.RemoteException;

import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Move;
import rps.game.data.Player;

;

/**
 * this class validates the parameters that are sent by the Client. It indicates
 * not allowed invalid moves and attempted cheating, for example if a move
 * contains border crossing, the a client tries to move Figures from the
 * oppenent player, or a an invalid Assignment is set.
 * 
 * @author Yasin Turkac
 * @version 2.3
 * 
 */

public class ValidatingGame implements Game {

	private final Game game;
	private final Player player;

	public ValidatingGame(Game game, Player player) throws RemoteException {
		this.game = game;
		this.player = player;
	}

	@Override
	public void setInitialAssignment(Player p, FigureKind[] assignment)
			throws RemoteException {
		if (!p.equals(this.player)) {
			throw new IllegalArgumentException(
					"assignment from different player");
		}
		// initialization of counters for the amount of the different kinds
		// placed on the game field
		int flags = 0;
		int traps = 0;
		int rocks = 0;
		int papers = 0;
		int scissors = 0;
		for (int i = 0; i < 42; i++) {
			FigureKind m = assignment[i];
			if (((i < 28) && (m != null)) || ((i >= 28) && (m == null)))
				throw new IllegalArgumentException(
						"wrong assignment: Fields Nr. 0-27 must be null and 28-41 must not be null ");
			// 28 is the starting Index for the Figures of the current Player
			if (i >= 28) {
				if (m.equals(ROCK)) {
					rocks++;
				}

				else if (m.equals(PAPER)) {
					papers++;

				} else if (m.equals(SCISSORS)) {
					scissors++;
				}

				else if (m.equals(FLAG)) {
					flags++;

				} else if (m.equals(TRAP)) {
					traps++;
				}
			}
		}
		// a valid assingment must include exactly 1 flag,1 trap, 4rocks, 4
		// papers and 4 scissors
		if (assignment.length != 42 || flags != 1 || traps != 1 || rocks != 4
				|| scissors != 4 || papers != 4)
			throw new IllegalArgumentException(
					"the amaount of one figure kind is to high or to less");
		game.setInitialAssignment(p, assignment);
	}

	@Override
	public Figure[] getField() throws RemoteException {
		return game.getField();
	}

	@Override
	public void move(Player p, int from, int to) throws RemoteException {
		if (from < 0 || to < 0 || from > 41 || to > 41)
			throw new IllegalArgumentException(
					"this position is out of the field" + " from: " + from
							+ " to: " + to);
		if (from == to) {
			throw new IllegalArgumentException(
					"Start and end position are the same" + " from: " + from
							+ " to: " + to);
		}
		if (((from - 6) % 7 == 0 && to == from + 1)
				|| ((from) % 7 == 0 && to == from - 1)) {
			throw new IllegalArgumentException("crossing borders not allowed"
					+ " from: " + from + " to: " + to);

		}
		int diff = Math.abs(from - to);
		if (diff != 7 && diff != 1) {
			throw new IllegalArgumentException(
					"to big steps or border crossing not allowed" + " from: "
							+ from + " to: " + to);

		}
		Figure figfrom = game.getField()[from];
		Figure figto = game.getField()[to];
		if (figfrom == null)
			throw new IllegalArgumentException("null field selected"
					+ " from: " + from + " to: " + to);
		if (!figfrom.belongsTo(p))
			throw new IllegalArgumentException("oppenent figure selected"
					+ " from: " + from + " to: " + to);
		if (figto != null && figto.belongsTo(p))
			throw new IllegalArgumentException(
					"cant attack own players,because its stupid" + " from: "
							+ from + " to: " + to);

		if (!figfrom.getKind().isMovable())
			throw new IllegalArgumentException("flag and trap are not moveable"
					+ " from: " + from + " to: " + to);
		game.move(p, from, to);

	}

	@Override
	public Move getLastMove() throws RemoteException {
		return game.getLastMove();
	}

	@Override
	public void sendMessage(Player p, String message) throws RemoteException {
		game.sendMessage(p, message);
	}

	@Override
	public void setInitialChoice(Player p, FigureKind kind)
			throws RemoteException {
		if (kind == FigureKind.HIDDEN || kind == FigureKind.FLAG
				|| kind == FigureKind.TRAP)
			throw new IllegalArgumentException(
					"no hidden player,flag or trap is allowed, for initial choice");
		game.setInitialChoice(p, kind);
	}

	@Override
	public void setUpdatedKindAfterDraw(Player p, FigureKind kind)
			throws RemoteException {
		if (kind == HIDDEN || kind == FLAG || kind == TRAP)
			throw new IllegalArgumentException(
					"KindAfterDraw must not be Hidden,Flag or a Trap");
		game.setUpdatedKindAfterDraw(p, kind);
	}

	@Override
	public void surrender(Player p) throws RemoteException {
		game.surrender(p);
	}

	@Override
	public Player getOpponent(Player p) throws RemoteException {
		return game.getOpponent(p);
	}
}