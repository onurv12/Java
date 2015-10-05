package rps.client.ai;

import static rps.game.data.FigureKind.FLAG;
import static rps.game.data.FigureKind.PAPER;
import static rps.game.data.FigureKind.ROCK;
import static rps.game.data.FigureKind.SCISSORS;
import static rps.game.data.FigureKind.TRAP;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

import rps.client.GameListener;
import rps.game.Game;
import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Move;
import rps.game.data.Player;

/**
 * This class contains an advanced AI, that should participate in the
 * tournament.
 */
public class TournamentAi implements GameListener {
	// player nick must end with group number, e.g. "Tournament AI (#123)"
	private final Player player = new Player("Tournament AI (#10)");
	private final int maxDurationForMoveInMilliSeconds;
	private final int maxDurationForAllMovesInMilliSeconds;
	
	private Game game;
		
	private Board board;
	private Move bestMove = null;
	
	public TournamentAi(int maxDurationForMoveInMilliSeconds,
			int maxDurationForAllMovesInMilliSeconds) {
		this.maxDurationForMoveInMilliSeconds = maxDurationForMoveInMilliSeconds;
		this.maxDurationForAllMovesInMilliSeconds = maxDurationForAllMovesInMilliSeconds;
		// TODO Auto-generated constructor stub
	}

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
		
		game.setInitialAssignment(player, new FigureKind[] 
				{ null, null, null, null, null, null, null, 
				null, null, null, null, null, null, null, 
				null, null, null, null, null, null, null, 
				null, null, null, null, null, null, null, 
				ROCK, PAPER, ROCK, SCISSORS, PAPER, TRAP, ROCK, 
				PAPER, SCISSORS, PAPER, ROCK, SCISSORS, FLAG, SCISSORS});
	}

	@Override
	public void provideInitialChoice() throws RemoteException {
		int number = new Random().nextInt(3);
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
		board = new Board(player, game.getOpponent(player), game.getField());
		game.sendMessage(player, "A game got started!");
	}
	
	@Override
	public void provideNextMove() throws RemoteException {
		long moveCalculationStartedAt = System.nanoTime();

		board.field = game.getField();
		board.updatePossibleMoves();
		
		ArrayList<Move> possibleMoves = board.getPossibleMoves(player);
		bestMove = possibleMoves.get(0);
		
		int MAX_DEPTH = (int) (Math.log(300000) / Math.log(possibleMoves.size() + 1)); //less possible moves, higher depth
		
		int maxScore = Integer.MIN_VALUE;
		for (Move m : possibleMoves) {
			if ((maxDurationForAllMovesInMilliSeconds < 500) || (System.nanoTime() - moveCalculationStartedAt > (maxDurationForMoveInMilliSeconds - 500) * 1000000)) {
				break;
			}
			int score = 0;
			if (board.isFight(m)) {
				if (board.isHiddenFight(m)) {
					Board nextBoard = board.update(m, true);
					score = (int) (board.getWinChance(player, board.field[m.getFrom()].getKind(), m.getTo()) * min(nextBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, MAX_DEPTH));
					
					nextBoard = board.update(m, false);
					score += (int) ((1 - board.getWinChance(player, board.field[m.getFrom()].getKind(), m.getTo())) * min(nextBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, MAX_DEPTH));
				} else if (board.isDrawFight(m)) {
					Board nextBoard = board.update(m, true);
					score = (int) (0.5 * min(nextBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, MAX_DEPTH));
					
					nextBoard = board.update(m, false);
					score += (int) (0.5 * min(nextBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, MAX_DEPTH));
				} else {
					Board nextBoard = board.update(m);
					score = min(nextBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, MAX_DEPTH);
				}
			} else {
				Board nextBoard = board.update(m);
				score = min(nextBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, MAX_DEPTH);
			}
			if (score > maxScore) {
				maxScore = score;
				bestMove = m;
			}
		}
		game.move(player, bestMove.getFrom(), bestMove.getTo());
	}
	
	private int max(Board b, int alpha, int beta, int depth) throws RemoteException {
		if (depth == 0) {
			return b.score(player);
		}
		int maxScore = Integer.MIN_VALUE;
		for (Move m : b.getPossibleMoves(player)) {
			if (board.isFight(m)) {
				if (board.isHiddenFight(m)) {
					Board nextBoard = board.update(m, true);
					int score = (int) (board.getWinChance(player, board.field[m.getFrom()].getKind(), m.getTo()) * min(nextBoard, alpha, beta, depth - 1));
					
					nextBoard = board.update(m, false);
					score += (int) ((1 - board.getWinChance(player, board.field[m.getFrom()].getKind(), m.getTo())) * min(nextBoard, alpha, beta, depth - 1));
					
					maxScore = Math.max(maxScore, score);
				} else if (board.isDrawFight(m)) {
					Board nextBoard = board.update(m, true);
					int score = (int) (0.5 * min(nextBoard, alpha, beta, depth - 1));
					
					nextBoard = board.update(m, false);
					score += (int) (0.5 * min(nextBoard, alpha, beta, depth - 1));
					
					maxScore = Math.max(maxScore, score);
				} else {
					Board nextBoard = board.update(m);
					maxScore = Math.max(maxScore, min(nextBoard, alpha, beta, depth - 1));
				}
			} else {
				Board nextBoard = board.update(m);
				maxScore = Math.max(maxScore, min(nextBoard, alpha, beta, depth - 1));
			}
			if (maxScore >= beta) {
				return maxScore;
			}
			alpha = Math.max(alpha, maxScore);
		}
		return maxScore;
	}
	
	private int min(Board b, int alpha, int beta, int depth) throws RemoteException {
		if (depth == 0) {
			return b.score(player);
		}
		int minScore = Integer.MAX_VALUE;
		for (Move m : b.getPossibleMoves(game.getOpponent(player))) {
			if (board.isFight(m)) {
				if (board.isHiddenFight(m)) {
					Board nextBoard = board.update(m, true);
					int score = (int) (1 - board.getWinChance(player, board.field[m.getTo()].getKind(), m.getTo()) * max(nextBoard, alpha, beta, depth - 1));
					
					nextBoard = board.update(m, false);
					score += (int) ((board.getWinChance(player, board.field[m.getTo()].getKind(), m.getTo())) * max(nextBoard, alpha, beta, depth - 1));
					
					minScore = Math.min(minScore, score);
				} else if (board.isDrawFight(m)) {
					Board nextBoard = board.update(m, true);
					int score = (int) (0.5 * max(nextBoard, alpha, beta, depth - 1));
					
					nextBoard = board.update(m, false);
					score += (int) (0.5 * max(nextBoard, alpha, beta, depth - 1));
					
					minScore = Math.min(minScore, score);
				} else {
					Board nextBoard = board.update(m);
					minScore = Math.min(minScore, max(nextBoard, alpha, beta, depth - 1));
				}
			} else {
				Board nextBoard = board.update(m);
				minScore = Math.min(minScore, max(nextBoard, alpha, beta, depth - 1));
			}
			if (minScore <= alpha) {
				return minScore;
			}
			beta = Math.min(beta, minScore);
		}
		return minScore;
	}

	@Override
	public void figureMoved() throws RemoteException {
		board.update(game.getField(), game.getLastMove());

		// TODO Auto-generated method stub
        // TODO: Update our field and possible moves
	}

	@Override
	public void figureAttacked() throws RemoteException {
		// TODO Auto-generated method stub
        // TODO: Update our field and possible moves

	}

	@Override
	public void provideChoiceAfterFightIsDrawn() throws RemoteException {
		// TODO Auto-generated method stub
        // TODO: IN general we should keep a log of some choices of the opponent
		game.setUpdatedKindAfterDraw(player, board.getChoice(player));
	}

	@Override
	public void gameIsLost() throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void gameIsWon() throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void gameIsDrawn() throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return "Tournament AI";
	}
}