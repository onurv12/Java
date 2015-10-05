package rps.game;

import java.rmi.RemoteException;

import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Move;
import rps.game.data.Player;

/**
 * This decorator is used to remove all information from get* methods that are
 * not visible for the corresponding player. Most importantly this is the
 * FigureKind of all Figure on the field that are undiscovered yet and do belong
 * to the other player. All Figures that are included to a fight are set
 * discovered and are not hidden anymore for the corresponding
 * 
 * @author Yasin
 */
public class FigureHidingGame implements Game {

	private final Game game;
	private final Player player;

	public FigureHidingGame(Game game, Player player) throws RemoteException {
		this.game = game;
		this.player = player;
	}

	@Override
	public void setInitialAssignment(Player p, FigureKind[] assignment)
			throws RemoteException {
		game.setInitialAssignment(p, assignment);
	}

	@Override
	public Figure[] getField() throws RemoteException {
		Figure[] oldField = game.getField().clone();
		Figure[] newField = new Figure[42];
		for (int i = 0; i < 42; i++) {
			if (oldField[i] != null) {
				Figure m = oldField[i];

				if (m.belongsTo(player) || m.isDiscovered()) {
					newField[i] = m.clone();
				} else
					newField[i] = m.cloneWithHiddenKind();

			}
		}

		return newField;
	}

	@Override
	public void move(Player p, int from, int to) throws RemoteException {
		Figure figto = game.getField()[to];
		Figure figfrom = game.getField()[from];
		// an attack is started, all hidden Figures that are involved to the
		// fight are not hidden anymore
		if ((figto != null) && (!figto.belongsTo(player))) {
			if (!figfrom.isDiscovered()) {
				figfrom.setDiscovered();
			}
			if (!figto.isDiscovered()) {
				figto.setDiscovered();

			}
		}
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
		game.setInitialChoice(p, kind);
	}

	@Override
	public void setUpdatedKindAfterDraw(Player p, FigureKind kind)
			throws RemoteException {
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