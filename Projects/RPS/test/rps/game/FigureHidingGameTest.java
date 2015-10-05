package rps.game;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rps.game.GameImplFixture.getStartingFieldAfterValidAssignmentsForFirstAndSecondPlayer;
import static rps.game.GameImplFixture.getValidInitialAssignmentForFirstPlayer;
import static rps.game.data.FigureKind.HIDDEN;
import static rps.game.data.FigureKind.PAPER;
import static rps.game.data.FigureKind.SCISSORS;

import java.rmi.RemoteException;

import org.junit.Before;
import org.junit.Test;

import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Player;

public class FigureHidingGameTest {
	private Game game;
	private Player thePlayer;
	private Player otherPlayer;

	private Figure[] fields;

	private FigureHidingGame sut;

	@Before
	public void setup() throws RemoteException {

		game = mock(Game.class);

		fields = new Figure[42];
		when(game.getField()).thenReturn(fields);

		thePlayer = new Player("A");
		otherPlayer = new Player("B");
		sut = new FigureHidingGame(game, thePlayer);
	}

	@Test
	public void moveIsPropagatedToGame() throws RemoteException {
		fields[1] = createFigureForPlayer(thePlayer);
		sut.move(thePlayer, 1, 2);
		verify(game).move(thePlayer, 1, 2);
	}

	@Test
	public void getFieldIsPropagatedToGame() throws RemoteException {
		sut.getField();
		verify(game).getField();
	}

	@Test
	public void initialAssignmentsArePropagatedToGame() throws RemoteException {
		sut.setInitialAssignment(thePlayer,
				getValidInitialAssignmentForFirstPlayer());
		verify(game).setInitialAssignment(thePlayer,
				getValidInitialAssignmentForFirstPlayer());
	}

	@Test
	public void getLastMoveIsPropagatedToGame() throws RemoteException {
		sut.getLastMove();
		verify(game).getLastMove();
	}

	@Test
	public void sendMessageIsPropagatedToGame() throws RemoteException {
		sut.sendMessage(thePlayer, "HELLO");
		verify(game).sendMessage(thePlayer, "HELLO");
	}

	@Test
	public void setInitialChoiceIsPropagatedToGame() throws RemoteException {
		sut.setInitialChoice(thePlayer, PAPER);
		verify(game).setInitialChoice(thePlayer, PAPER);
	}

	@Test
	public void setUpdatedKindAfterDrawIsPropagatedToGame()
			throws RemoteException {
		sut.setUpdatedKindAfterDraw(thePlayer, PAPER);
		verify(game).setUpdatedKindAfterDraw(thePlayer, PAPER);
	}

	@Test
	public void surrenderIsPropagatedToGame() throws RemoteException {
		sut.surrender(thePlayer);
		verify(game).surrender(thePlayer);
	}

	@Test
	public void getOpponentTest() throws RemoteException {

		assertTrue(sut.getOpponent(thePlayer).equals(otherPlayer));// TODO
	}

	@Test
	public void PlayerFiguresIsNotHiddenBeforeFight() throws RemoteException {
		fields[0] = new Figure(PAPER, thePlayer);
		assertTrue(sut.getField()[0].getKind() == PAPER);
	}

	@Test
	public void OppenentFigureIsHiddenBeforeFight() throws RemoteException {
		fields[0] = new Figure(PAPER, otherPlayer);
		assertTrue(sut.getField()[0].getKind() == HIDDEN);
	}

	@Test
	public void PlayerFigureIsNotHiddenAfterMove() throws RemoteException {
		fields[0] = new Figure(PAPER, thePlayer);
		sut.move(thePlayer, 0, 1);
		assertTrue(sut.getField()[0].getKind() == PAPER);
	}

	@Test
	public void OppenentFigureIsHiddenAfterMove() throws RemoteException {
		fields[0] = new Figure(PAPER, otherPlayer);
		sut.move(thePlayer, 0, 1);
		assertTrue(sut.getField()[0].getKind() == HIDDEN);
	}

	@Test
	public void OppenentFigureIsNotHiddenAfterFight() throws RemoteException {
		fields[0] = new Figure(PAPER, thePlayer);
		fields[1] = new Figure(SCISSORS, otherPlayer);
		sut.move(thePlayer, 0, 1);
		assertTrue(sut.getField()[1].isDiscovered());
		assertTrue(sut.getField()[1].getKind() == SCISSORS);

	}

	@Test
	public void PlayerFigureIsNOTDiscoveredBeforeFight() throws RemoteException {
		fields[0] = new Figure(SCISSORS, thePlayer);
		fields[1] = new Figure(PAPER, otherPlayer);
		assertFalse(sut.getField()[1].isDiscovered());
	}

	@Test
	public void PlayerFigureIsDiscoveredAfterFight() throws RemoteException {
		fields[0] = new Figure(SCISSORS, thePlayer);
		fields[1] = new Figure(PAPER, otherPlayer);
		sut.move(thePlayer, 0, 1);
		assertTrue(sut.getField()[1].isDiscovered());

	}

	// TODO

	@Test
	public void PlayerFiguresAreNotHiddenAtStart() throws RemoteException {
		fields = createStartField(fields, thePlayer, otherPlayer);
		for (int i = 0; i < 42; i++) {
			Figure m;
			if (i < 14) {
				m = sut.getField()[i];
				assertFalse(m.isDiscovered());
				assertTrue(m.getKind() == HIDDEN);
			}

			if (28 <= i) {
				m = sut.getField()[i];
				assertTrue(m.getKind() == fields[i].getKind());
				assertFalse(m.isDiscovered());
			}
		}
	}

	private static Figure createFigureForPlayer(Player p) {
		return new Figure(PAPER, p);
	}

	private static Figure[] createStartField(Figure[] field, Player player,
			Player oppenent) {
		FigureKind[] kindField = getStartingFieldAfterValidAssignmentsForFirstAndSecondPlayer();
		for (int i = 0; i < 42; i++) {
			if (i < 14) {
				field[i] = new Figure(kindField[i], oppenent);
			}
			if (28 <= i) {
				field[i] = new Figure(kindField[i], player);
			}
		}
		return field;
	}
}