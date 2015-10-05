/**
 * 
 */
package rps.client.ai;

import java.util.ArrayList;
import java.util.Random;

import rps.game.data.AttackResult;
import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Move;
import rps.game.data.Player;

/**
 * @author Paul Kiefer 1949092
 *
 */
public class Board {

	private Player p1;
	private int p1Scissors = 4;
	private int p1Papers = 4;
	private int p1Rocks = 4;
	private int p1Traps = 1;
	private int p1Flags = 1;
	private ArrayList<Move> p1PossibleMoves = new ArrayList<Move>(30);
	
	private Player p2;
	private int p2Scissors = 4;
	private int p2Papers = 4;
	private int p2Rocks = 4;
	private int p2Traps = 1;
	private int p2Flags = 1;
	private ArrayList<Move> p2PossibleMoves = new ArrayList<Move>(30);
	
	Figure[] field = new Figure[42];
	boolean[] moved = {
			false, false, false, false, false, false, false,
			false, false, false, false, false, false, false
			};
	int notMovedAmount = 12;
	
	Board(Player p1, Player p2, Figure[] field) {
		this.p1 = p1;
		this.p2 = p2;
		System.arraycopy(field, 0, this.field, 0, 42);
		updatePossibleMoves();
	}
	
	public ArrayList<Move> getPossibleMoves(Player p) {
		if (p.equals(p1)) {
			return p1PossibleMoves;
		} else if (p.equals(p2)) {
			return p2PossibleMoves;
		}
		return null;
	}
	
	/**
	 * calculates the score of the current field
	 */
	int score(Player p) {
		if (p1Flags == 0) {
			return Integer.MIN_VALUE;
		} else if (p2Flags == 0) {
			return Integer.MAX_VALUE;
		}
		
		double score = 0;
		if (Double.isInfinite((double)p1Rocks / (double)p2Papers)) {
			score += 2000;
		} else {
			score += 100 * (double)p1Rocks / (double)p2Papers;
		}
		if (Double.isInfinite((double)p1Papers / (double)p2Scissors)) {
			score += 2000;
		} else {
			score += 100 * (double)p1Papers / (double)p2Scissors;
		}
		if (Double.isInfinite((double)p1Scissors / (double)p2Rocks)) {
			score += 2000;
		} else {
			score += 100 * (double)p1Scissors / (double)p2Rocks;
		}
		
		if (Double.isInfinite((double)p2Rocks / (double)p1Papers)) {
			score -= 2000;
		} else {
			score -= 100 * (double)p2Rocks / (double)p1Papers;
		}
		if (Double.isInfinite((double)p2Papers / (double)p1Scissors)) {
			score -= 2000;
		} else {
			score -= 100 * (double)p2Papers / (double)p1Scissors;
		}
		if (Double.isInfinite((double)p2Scissors / (double)p1Rocks)) {
			score -= 2000;
		} else {
			score -= 100 * (double)p2Scissors / (double)p1Rocks;
		}
		
		score += p1Traps - p2Traps;
		
		return (int) score;
	}
	
	/*
	 * makes a copy of the current board
	 */
	@Override
	public Board clone() {
		Board b = new Board(p1, p2, field);
		b.p1Scissors = p1Scissors;
		b.p1Papers = p1Papers;
		b.p1Rocks = p1Rocks;
		b.p1Traps = p1Traps;
		b.p1Flags = p1Flags;
		b.p2Scissors = p2Scissors;
		b.p2Papers = p2Papers;
		b.p2Rocks = p2Rocks;
		b.p2Traps = p2Traps;
		b.p2Flags = p2Flags;
		b.notMovedAmount = notMovedAmount;
		b.moved = new boolean[14];
		System.arraycopy(moved, 0, b.moved, 0, 12);
		return b;
	}
	
	/*
	 * recalculates the possible moves
	 */
	public void updatePossibleMoves() {
		Figure[] emptyField = { null };

		for (int i = 0; i < 12; i++) {
			if (((field[i] == null) || field[i].isDiscovered()) && !moved[i]) {
				moved[i] = true;
				notMovedAmount--;
			}
		}
		
		p1PossibleMoves.clear();
		for (int i = 0; i < field.length; i++) {
			if (field[i] == null || field[i].belongsTo(p2) || !field[i].getKind().isMovable())
				continue;
			
			int frontMoveField = (i - 7);
			if (frontMoveField >= 0 && (field[frontMoveField] == null || field[frontMoveField].belongsTo(p2)))
				p1PossibleMoves.add(new Move(i, frontMoveField, emptyField));

			int backMoveField = (i + 7);
			if (backMoveField <= 41 && (field[backMoveField] == null || field[backMoveField].belongsTo(p2)))
				p1PossibleMoves.add(new Move(i, backMoveField, emptyField));

			int rightSideMove = (i + 1);
			if ((i % 7) < 6 && (field[rightSideMove] == null || field[rightSideMove].belongsTo(p2)))
				p1PossibleMoves.add(new Move(i, rightSideMove, emptyField));

			int leftSideMove = (i - 1);
			if ((i % 7) > 0 && (field[leftSideMove] == null || field[leftSideMove].belongsTo(p2)))
				p1PossibleMoves.add(new Move(i, leftSideMove, emptyField));
		}

		p2PossibleMoves.clear();
		for (int i = 0; i < field.length; i++) {
			if (field[i] == null || field[i].belongsTo(p1) || (!field[i].getKind().isMovable() && !field[i].getKind().equals(FigureKind.HIDDEN)))
				continue;
			
			int frontMoveField = (i - 7);
			if (frontMoveField >= 0 && (field[frontMoveField] == null || field[frontMoveField].belongsTo(p1)))
				p2PossibleMoves.add(new Move(i, frontMoveField, emptyField));

			int backMoveField = (i + 7);
			if (backMoveField <= 41 && (field[backMoveField] == null || field[backMoveField].belongsTo(p1)))
				p2PossibleMoves.add(new Move(i, backMoveField, emptyField));

			int rightSideMove = (i + 1);
			if ((i % 7) < 6 && (field[rightSideMove] == null || field[rightSideMove].belongsTo(p1)))
				p2PossibleMoves.add(new Move(i, rightSideMove, emptyField));

			int leftSideMove = (i - 1);
			if ((i % 7) > 0 && (field[leftSideMove] == null || field[leftSideMove].belongsTo(p1)))
				p2PossibleMoves.add(new Move(i, leftSideMove, emptyField));
		}
	}
	
	/*
	 * just a small helpfunction to to add figures to a player
	 */
	private void addFigure(FigureKind fk, Player p) {
		if (p.equals(p1)) {
			switch (fk) {
			case FLAG:
				p1Flags++;
				break;
			case PAPER:
				p1Papers++;
				break;
			case ROCK:
				p1Rocks++;
				break;
			case SCISSORS:
				p1Scissors++;
				break;
			case TRAP:
				p1Traps++;
				break;
			}
		} else if (p.equals(p2)) {
			switch (fk) {
			case FLAG:
				p2Flags++;
				break;
			case PAPER:
				p2Papers++;
				break;
			case ROCK:
				p2Rocks++;
				break;
			case SCISSORS:
				p2Scissors++;
				break;
			case TRAP:
				p2Traps++;
				break;
			}
		}
	}
	
	/*
	 * just a small helpfunction to to remove figures to a player
	 */
	private void removeFigure(FigureKind fk, Player p) {
		if (p.equals(p1)) {
			switch (fk) {
			case FLAG:
				p1Flags--;
				break;
			case PAPER:
				p1Papers--;
				break;
			case ROCK:
				p1Rocks--;
				break;
			case SCISSORS:
				p1Scissors--;
				break;
			case TRAP:
				p1Traps--;
				break;
			}
		} else if (p.equals(p2)) {
			switch (fk) {
			case FLAG:
				p2Flags--;
				break;
			case PAPER:
				p2Papers--;
				break;
			case ROCK:
				p2Rocks--;
				break;
			case SCISSORS:
				p2Scissors--;
				break;
			case TRAP:
				p2Traps--;
				break;
			}
		}
	}

	/*
	 * a function to update the current board object
	 */
	public void update(Figure[] newField, Move lastMove) {
		if (lastMove.getOldField()[lastMove.getTo()] != null) {
			if (newField[lastMove.getTo()] != lastMove.getOldField()[lastMove.getTo()]) {
				if (lastMove.getOldField()[lastMove.getFrom()].belongsTo(p1)) {
					removeFigure(lastMove.getOldField()[lastMove.getTo()].getKind(), p1);
					removeFigure(lastMove.getOldField()[lastMove.getFrom()].getKind(), p2);
					if (newField[lastMove.getTo()] != null) {
						addFigure(newField[lastMove.getTo()].getKind(), p2);
					}
				} else {
					removeFigure(lastMove.getOldField()[lastMove.getTo()].getKind(), p2);
					removeFigure(lastMove.getOldField()[lastMove.getFrom()].getKind(), p1);
					if (newField[lastMove.getTo()] != null) {
						addFigure(newField[lastMove.getTo()].getKind(), p1);
					}
				}
			} else {
				if (lastMove.getOldField()[lastMove.getFrom()].belongsTo(p1)) {
					removeFigure(lastMove.getOldField()[lastMove.getFrom()].getKind(), p1);
					removeFigure(lastMove.getOldField()[lastMove.getTo()].getKind(), p2);
					addFigure(newField[lastMove.getTo()].getKind(), p2);
				} else {
					removeFigure(lastMove.getOldField()[lastMove.getFrom()].getKind(), p2);
					removeFigure(lastMove.getOldField()[lastMove.getTo()].getKind(), p1);
					addFigure(newField[lastMove.getTo()].getKind(), p1);
				}
			}
		}
		this.field = newField;
		updatePossibleMoves();
	}
	
	/*
	 * boolean showing whether the move is a fight with at least one non-discovered figure
	 */
	public boolean isHiddenFight(Move m) {
		return (!field[m.getFrom()].isDiscovered()) || (!field[m.getTo()].isDiscovered());
	}
	
	/*
	 * boolean showing whether the move is a fight which is a draw
	 */
	public boolean isDrawFight(Move m) {
		return field[m.getFrom()].getKind() == field[m.getTo()].getKind();
	}
	
	/*
	 * boolean showing wether the move is a fight
	 */
	public boolean isFight(Move m) {
		return field[m.getTo()] != null;
	}
		
	/*
	 * returns an updated version of the current board after the given move
	 * it makes a copy, thus the old field is not changed
	 */
	public Board update(Move m) {
		Figure[] field = new Figure[42];
		System.arraycopy(this.field, 0, field, 0, 42);
		if (isFight(m)) {
			AttackResult ar = field[m.getFrom()].attack(field[m.getTo()]);
			switch (ar) {
			case LOOSE:
				if (field[m.getFrom()].belongsTo(p1)) {
					removeFigure(field[m.getFrom()].getKind(), p1);
				} else {
					removeFigure(field[m.getFrom()].getKind(), p2);
				}
				break;
			case LOOSE_AGAINST_TRAP:
				if (field[m.getFrom()].belongsTo(p1)) {
					removeFigure(field[m.getFrom()].getKind(), p1);
					removeFigure(FigureKind.TRAP, p2);
				} else {
					removeFigure(field[m.getFrom()].getKind(), p2);
					removeFigure(FigureKind.TRAP, p1);
				}
				break;
			case WIN:
				if (field[m.getTo()].belongsTo(p1)) {
					removeFigure(field[m.getTo()].getKind(), p1);
				} else {
					removeFigure(field[m.getTo()].getKind(), p2);
				}
				break;
			case WIN_AGAINST_FLAG:
				if (field[m.getFrom()].belongsTo(p1)) {
					removeFigure(FigureKind.FLAG, p2);
				} else {
					removeFigure(FigureKind.FLAG, p1);
				}
				break;
			}
		} else {
			field[m.getTo()] = field[m.getFrom()];
			field[m.getFrom()] = null;
		}
		Board board = clone();
		board.field = field;
		return board;
	}
	
	/*
	 * returns the current amount of figures of the given figurekind of the given player
	 */
	private int getAmountOfKind(FigureKind fk, Player p) {
		if (p.equals(p1)) {
			switch (fk) {
			case FLAG:
				return p1Flags;
			case PAPER:
				return p1Papers;
			case ROCK:
				return p1Rocks;
			case SCISSORS:
				return p1Scissors;
			case TRAP:
				return p1Traps;
			}
		} else if (p.equals(p2)) {
			switch (fk) {
			case FLAG:
				return p2Flags;
			case PAPER:
				return p2Papers;
			case ROCK:
				return p2Rocks;
			case SCISSORS:
				return p2Scissors;
			case TRAP:
				return p2Traps;
			}
		}
		return 0;
	}
	
	/*
	 * returns the winning kind of the given figure kind
	 * returns null for a trap/flag/hidden
	 */
	private static FigureKind getWinningKind(FigureKind fk) {
		switch (fk) {
		case PAPER:
			return FigureKind.SCISSORS;
		case ROCK:
			return FigureKind.PAPER;
		case SCISSORS:
			return FigureKind.ROCK;
		default:
			return null;
		}
	}
	
	/*
	 * returns the current winning chance of the given figure kind to the given position
	 */
	public double getWinChance(Player p, FigureKind fk, int to) {
		if (fk == FigureKind.TRAP) {
			return 1;
		} else if (fk == FigureKind.FLAG) {
			return 0;
		} else if (p.equals(p1)) {
			if ((to < 12) && (!moved[to])) {
				return (double) getAmountOfKind(getWinningKind(fk), p2) / (double) (p2Scissors + p2Papers + p2Rocks + p2Traps + p2Flags);
			} else {
				return (double) getAmountOfKind(getWinningKind(fk), p2) / (double) (p2Scissors + p2Papers + p2Rocks);
			}
		} else {
			if ((to < 12) && (!moved[to])) {
				return (double) getAmountOfKind(getWinningKind(fk), p1) / (double) (p1Scissors + p1Papers + p1Rocks + p1Traps + p1Flags);
			} else {
				return (double) getAmountOfKind(getWinningKind(fk), p1) / (double) (p1Scissors + p1Papers + p1Rocks);
			}
		}
	}
	
	/*
	 * updates the board with the given move assuming the fight follows by the move is going to be won (b=true) or lost (b=false)
	 */
	public Board update(Move m, boolean b) {
		Figure[] field = new Figure[42];
		System.arraycopy(this.field, 0, field, 0, 42);
		if (b) {
			field[m.getTo()] = field[m.getFrom()];
			field[m.getFrom()] = null;
		} else {
			field[m.getFrom()] = field[m.getTo()];
			field[m.getTo()] = null;
		}
		Board board = clone();
		board.field = field;
		return board;
	}

	/*
	 * function to determine the choice of the given player
	 */
	public FigureKind getChoice(Player p) {
		if (p.equals(p1)) {
			if (p2Scissors == 0) {
				return FigureKind.PAPER;
			}
			if (p2Papers == 0) {
				return FigureKind.ROCK;
			}
			if (p2Rocks == 0) {
				return FigureKind.SCISSORS;
			}
			if (p1Scissors == 0) {
				return FigureKind.SCISSORS;
			}
			if (p1Papers == 0) {
				return FigureKind.PAPER;
			}
			if (p1Rocks == 0) {
				return FigureKind.ROCK;
			}
		} else {
			if (p1Scissors == 0) {
				return FigureKind.PAPER;
			}
			if (p1Papers == 0) {
				return FigureKind.ROCK;
			}
			if (p1Rocks == 0) {
				return FigureKind.SCISSORS;
			}
			if (p2Scissors == 0) {
				return FigureKind.SCISSORS;
			}
			if (p2Papers == 0) {
				return FigureKind.PAPER;
			}
			if (p2Rocks == 0) {
				return FigureKind.ROCK;
			}
		}
		int number = new Random().nextInt(3);
		switch (number) {
		case 0:
			return FigureKind.ROCK;
		case 1:
			return FigureKind.PAPER;
		default:
			return FigureKind.SCISSORS;
		}
	}
}
