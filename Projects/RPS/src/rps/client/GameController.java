package rps.client;

import static rps.network.NetworkUtil.hostNetworkGame;

import java.rmi.RemoteException;

import rps.client.ui.GamePane;
import rps.client.ui.PlayerListener;
import rps.client.ui.StatusPane;
import rps.game.Game;
import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Player;
import rps.network.GameRegistry;
import rps.network.NetworkUtil;

/**
 * this class is responsible for controlling all game related events.
 */
public class GameController implements GameListener {

	private UIController uiController;
	private GamePane gamePane;

	private GameRegistry registry;
	private Player player;
	private Game game;
	private StatusPane statusPane;

	public void setComponents(UIController uiController, GamePane gamePane) {
		this.uiController = uiController;
		this.gamePane = gamePane;
		this.statusPane=gamePane.getStatusPane();
	}

	public void startHostedGame(Player player, String host) {
		this.player = player;
		registry = hostNetworkGame(host);
		register(player, this);
	}

	public void startJoinedGame(Player player, String host) {
		this.player = player;
		registry = NetworkUtil.requestRegistry(host);
		register(player, this);
	}

	public void startAIGame(Player player, GameListener ai) {
		this.player = player;
		registry = NetworkUtil.hostLocalGame();
		register(new Player(ai.toString()), ai);
		register(player, this);
	}

	private void register(Player player, GameListener listener) {
		try {
			GameListener multiThreadedListener = decorateListener(listener);
			registry.register(multiThreadedListener);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	private static GameListener decorateListener(GameListener listener) {
		try {
			listener = new MultiThreadedGameListener(listener);
			listener = new RMIGameListener(listener);
			return listener;
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	public void unregister() {
		try {
			if (registry != null) {
				registry.unregister(player);
			}
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	public void surrender() {
		try {
			game.surrender(player);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	public void resetForNewGame() {
		surrender();
	}

	public void exit() {
		if (registry != null) {
			unregister();
		}
		if (game != null) {
			surrender();
		}
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public void chatMessage(Player sender, String message)
			throws RemoteException {
		gamePane.receivedMessage(sender, message);
	}

	@Override
	public void provideInitialAssignment(Game game) throws RemoteException {
		this.game = game;
		uiController.switchToGamePane();
		gamePane.startGame(player, game);
		PlayerListener listener = gamePane.getFieldPane().getListener();
		listener.requestInitialAssignment();
	}

	@Override
	public void provideInitialChoice() throws RemoteException {
		PlayerListener listener = gamePane.getFieldPane().getListener();
		listener.requestInitialChoice();
	}

	@Override
	public void startGame() throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void provideNextMove() throws RemoteException {
		PlayerListener listener = gamePane.getFieldPane().getListener();
		listener.requestMove();
	}

	@Override
	public void figureMoved() throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void figureAttacked() throws RemoteException {
		Player attacker;
		Player attacked;
		FigureKind attackerKind;
		FigureKind attackedKind;
		if(game.getLastMove()!=null){
			Figure[] oldField=game.getLastMove().getOldField();
			int from=game.getLastMove().getFrom();
			int to=game.getLastMove().getTo();
			if(oldField[from].belongsTo(player)){
				attacker=player;
				attacked=game.getOpponent(player);
			}else{
				attacker=game.getOpponent(player);
				attacked=player;
			}
			if(oldField[from]!=null&&oldField[to]!=null){
				attackerKind=oldField[from].getKind();
				attackedKind=oldField[to].getKind();
				gamePane.setChat(attackerKind+" ("+attacker.getNick()+") attacked "+attackedKind+" ("+attacked.getNick()+")");
			}
		}
	}

	@Override
	public void provideChoiceAfterFightIsDrawn() throws RemoteException {
		PlayerListener listener = gamePane.getFieldPane().getListener();
		listener.requestChoice();
	}

	@Override
	public void gameIsLost() throws RemoteException {
		statusPane.setStatusBar("You lost!");

	}

	@Override
	public void gameIsWon() throws RemoteException {
		statusPane.setStatusBar("You won!");

	}

	@Override
	public void gameIsDrawn() throws RemoteException {
		statusPane.setStatusBar("Game is drawn!");

	}
}