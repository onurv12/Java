package rps.client;


import javax.swing.JFrame;

import rps.client.ui.AboutPane;
import rps.client.ui.GamePane;
import rps.client.ui.Menu;
import rps.client.ui.StartupPane;
import rps.client.ui.WaitingPane;

public class UIController {

	private StartupPane startupPane;
	private WaitingPane waitingPane;
	private GamePane gamePane;
	private GameController gameController;
	private Menu menu;
	private AboutPane aboutPane;
	private JFrame frame;

	public void setComponents(Menu menu, StartupPane startupPane, WaitingPane waitingPane, GamePane gamePane,
			GameController gameController, AboutPane aboutPane, JFrame frame) {
		this.menu = menu;
		this.startupPane = startupPane;
		this.waitingPane = waitingPane;
		this.gamePane = gamePane;
		this.gameController = gameController;
		this.aboutPane=aboutPane;
		this.frame = frame;
	}

	public void handleSurrender() {
		gameController.surrender();
		menu.gameEnded();
	}

	public void handleExit() {
		gameController.exit();
		System.exit(0);
	}

	public void handleNewGame() {
		gameController.resetForNewGame();
		menu.reset();
		startupPane.show();
		waitingPane.hide();
		gamePane.hide();
		frame.setSize(180, 178);
	}

	public void switchToWaitingForOpponentPane() {
		startupPane.hide();
		waitingPane.show();
		aboutPane.hide();
		frame.setSize(180, 178);
	}

	public void stopWaitingAndSwitchBackToStartup() {
		gameController.unregister();
		switchBackToStartup();
	}

	public void switchToGamePane() {
		menu.gameStarted();
		waitingPane.hide();
		aboutPane.hide();
		frame.setSize(662, 535);
	}

	public void switchBackToStartup() {
		aboutPane.hide();
		waitingPane.hide();
		startupPane.show();
		frame.setSize(180, 178);
	}

	public void handleAbout() {
		startupPane.hide();
		aboutPane.show();
		frame.setSize(280, 215);
	}
}