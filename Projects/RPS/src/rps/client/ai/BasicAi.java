package rps.client.ai;

import static rps.game.data.FigureKind.FLAG;
import static rps.game.data.FigureKind.PAPER;
import static rps.game.data.FigureKind.ROCK;
import static rps.game.data.FigureKind.SCISSORS;
import static rps.game.data.FigureKind.TRAP;
import rps.game.data.Figure;
import rps.game.data.Move;

import java.rmi.RemoteException;
import java.util.Random;
import java.util.ArrayList;

import rps.client.GameListener;
import rps.game.Game;
import rps.game.data.FigureKind;
import rps.game.data.Player;

/**
 * This class contains a very basic AI, that allows to play a game against it.
 * The main benefit is to be able to test the UI.
 */
public class BasicAi implements GameListener {

	private Player player = new Player("Basic AI");
	private Game game;
	private Random random = new Random();
	private ArrayList<Move> possibleMoves = new ArrayList<Move>(30);

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public void chatMessage(Player sender, String message)
			throws RemoteException {
		if (!player.equals(sender)) {
			game.sendMessage(player, "you said: " + message);
		}
	}

	@Override
	public void provideInitialAssignment(Game game) throws RemoteException {
		this.game = game;
		game.setInitialAssignment(player, new FigureKind[] { null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, PAPER, PAPER, ROCK, ROCK, ROCK,
				ROCK, SCISSORS, SCISSORS, SCISSORS, SCISSORS, FLAG, TRAP,
				PAPER, PAPER, });
	}

	@Override
	public void provideInitialChoice() throws RemoteException {
		int number = random.nextInt(3);
		switch (number) {
		case 0:
			game.setInitialChoice(player, FigureKind.ROCK);
			break;
		case 1:
			game.setInitialChoice(player, FigureKind.PAPER);
			break;
		default:
			game.setInitialChoice(player, FigureKind.SCISSORS);
			break;
		}
	}

	@Override
	public void startGame() throws RemoteException {
		game.sendMessage(player, "A game got started!");
		possibleMoves.add(new Move(28, 21, game.getField()));
		possibleMoves.add(new Move(29, 22, game.getField()));
		possibleMoves.add(new Move(30, 23, game.getField()));
		possibleMoves.add(new Move(31, 24, game.getField()));
		possibleMoves.add(new Move(32, 25, game.getField()));
		possibleMoves.add(new Move(33, 26, game.getField()));
		possibleMoves.add(new Move(34, 27, game.getField()));
	}

	@Override
	public void provideNextMove() throws RemoteException {
		int r = random.nextInt(possibleMoves.size() - 1);
		Move m = possibleMoves.get(r);
		Figure[] field = game.getField().clone();
		while (field[m.getFrom()] == null || 
				(field[m.getTo()] != null && field[m.getTo()].belongsTo(player)) ||
				(field[m.getFrom()] != null && !field[m.getFrom()].belongsTo(player))) {
			possibleMoves.remove(m);
			int rand = random.nextInt(possibleMoves.size() - 1);
			m = possibleMoves.get(rand);
		}
		
		int to = m.getTo();
		int from = m.getFrom();
		
		for (int i = 0; i < possibleMoves.size(); i++) {
			if (possibleMoves.get(i).getFrom() == from) {
				possibleMoves.remove(i);
			} else if (possibleMoves.get(i).getTo() == to) {
				possibleMoves.remove(i);
			}
		}

		field[to] = field[from];
		field[from] = null;
		
		if ((to - 1 != from) && (to % 7 > 0) && ((field[to - 1] == null) || field[to - 1].belongsTo(game.getOpponent(player)))){
			possibleMoves.add(new Move(to, to - 1, field));
		}
		if ((to + 1 != from) && (to % 7 < 6) && ((field[to + 1] == null) || field[to + 1].belongsTo(game.getOpponent(player)))) {
			possibleMoves.add(new Move(to, to + 1, field));
		}
		if ((to - 7 != from) && (to - 7 >= 0) && ((field[to - 7] == null) || field[to - 7].belongsTo(game.getOpponent(player)))) {
			possibleMoves.add(new Move(to, to - 7, field));
		}
		if ((to + 7 != from) && (to + 7 <= 41) && ((field[to + 7] == null) || field[to + 7].belongsTo(game.getOpponent(player)))) {
			possibleMoves.add(new Move(to, to + 7, field));
		}
		
		if ((from % 7 > 0) && (field[from - 1] != null) && field[from - 1].belongsTo(player) && field[from - 1].getKind().isMovable()) {
			possibleMoves.add(new Move(from - 1, from, field));
		}
		if ((from % 7 < 6) && (field[from + 1] != null) && field[from + 1].belongsTo(player) && field[from + 1].getKind().isMovable()) {
			possibleMoves.add(new Move(from + 1, from, field));
		}
		if ((from - 7 >= 0) && (field[from - 7] != null) && field[from - 7].belongsTo(player) && field[from - 7].getKind().isMovable()) {
			possibleMoves.add(new Move(from - 7, from, field));
		}
		if ((from + 7 <= 41) && (field[from + 7] != null) && field[from + 7].belongsTo(player) && field[from + 7].getKind().isMovable()) {
			possibleMoves.add(new Move(from + 7, from, field));
		}
		
		game.move(player, m.getFrom(), m.getTo());
	}

	@Override
	public void figureMoved() throws RemoteException {
		game.sendMessage(player, "We detected that a figure moved!");
	}

	@Override
	public void figureAttacked() throws RemoteException {
		game.sendMessage(player, "We detected that a figure got attacked!");
		Move m = game.getLastMove();
		Figure [] field = game.getField().clone();
		if (!m.getOldField()[m.getFrom()].belongsTo(player) && field[m.getTo()] != null && !field[m.getTo()].belongsTo(player)) {
			//This means our figure got attacked and lost
			if (m.getTo() % 7 > 0 && field[m.getTo() - 1] != null && 
					field[m.getTo() - 1].belongsTo(player) && field[m.getTo() - 1].getKind().isMovable()) {
				possibleMoves.add(new Move(m.getTo() - 1, m.getTo(), field));
			}
			if (m.getTo() % 7 < 6 && field[m.getTo() + 1] != null && 
					field[m.getTo() + 1].belongsTo(player) && field[m.getTo() + 1].getKind().isMovable()) {
				possibleMoves.add(new Move(m.getTo() + 1, m.getTo(), field));
			}
			if (m.getTo() + 7 < 42 && field[m.getTo() + 7] != null && 
					field[m.getTo() + 7].belongsTo(player) && field[m.getTo() + 7].getKind().isMovable()) {
				possibleMoves.add(new Move(m.getTo() + 7, m.getTo(), field));
			}
			if (m.getTo() - 7 >= 0 && field[m.getTo() - 7] != null && 
					field[m.getTo() - 7].belongsTo(player) && field[m.getTo() - 7].getKind().isMovable()) {
				possibleMoves.add(new Move(m.getTo() - 7, m.getTo(), field));
			}
		}
	}

	@Override
	public void provideChoiceAfterFightIsDrawn() throws RemoteException {
		int number = random.nextInt(3);
		switch (number) {
		case 0:
			game.setUpdatedKindAfterDraw(player, FigureKind.ROCK);
			break;
		case 1:
			game.setUpdatedKindAfterDraw(player, FigureKind.PAPER);
			break;
		default:
			game.setUpdatedKindAfterDraw(player, FigureKind.SCISSORS);
			break;
		}
	}

	@Override
	public void gameIsLost() throws RemoteException {
		game.sendMessage(player, "We have lost! :(");
	}

	@Override
	public void gameIsWon() throws RemoteException {
		game.sendMessage(player, "We have won! :)");
	}

	@Override
	public void gameIsDrawn() throws RemoteException {
		game.sendMessage(player, "We both have won!");
	}

	@Override
	public String toString() {
		return player.getNick();
	}
}