/**
 * 
 */
package rps.client.ui;

import static rps.game.data.FigureKind.FLAG;
import static rps.game.data.FigureKind.PAPER;
import static rps.game.data.FigureKind.ROCK;
import static rps.game.data.FigureKind.SCISSORS;
import static rps.game.data.FigureKind.TRAP;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rps.game.Game;
import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Player;

/**
 * Catches the players interaction with the userinterface.
 * 
 * @author Paul Kiefer 1949092
 *
 */
public class PlayerListener implements MouseListener{

	private Game game;
	private Player player;
	private FieldPane fieldPane;
	private StatusPane statusPane;
		
	private int currentSelection = -1;
	
	private boolean playerInitialAssignmentRequested = false;
	private boolean playerMoveRequested = false;
	private boolean playerInitialChoiceRequested = false;
	private boolean playerChoiceRequested = false;
	private boolean isTrapRequested = false;
	
	private FigureKind[] initialAssignment = new FigureKind[42];
	
	public PlayerListener(Game game, Player player, FieldPane fieldPane) {
		this.game = game;
		this.player = player;
		this.fieldPane = fieldPane;
	}
	
	public void startGame(Player player, Game game) { // reseting everything such that it works for the new game
		this.player = player;
		this.game = game;
		playerInitialAssignmentRequested = false;
		playerMoveRequested = false;
		playerChoiceRequested = false;
		playerInitialChoiceRequested = false;
		isTrapRequested = false;
		currentSelection = -1;
		initialAssignment = new FigureKind[42];
	}
	
	public boolean setInitialAssignment() {
		try {
			playerInitialAssignmentRequested = false;
			game.setInitialAssignment(player, initialAssignment);
			return true;
		} catch (RemoteException e) {
			
			e.printStackTrace();
			return false;
		} catch (IllegalArgumentException e) {
			playerInitialAssignmentRequested = true;
			return false;
		}
	}
	
	void setStatusPane(StatusPane p)
	{
		statusPane = p;
	}
	
	
	public void shuffleAll(){
		FigureKind[]figures=new FigureKind[]{PAPER, PAPER, ROCK, ROCK, ROCK, ROCK, SCISSORS,
				SCISSORS, SCISSORS, SCISSORS, FLAG, TRAP, PAPER, PAPER};
		List<FigureKind> list=Arrays.asList(figures);
		Collections.shuffle(list);
		figures=(FigureKind[]) list.toArray();
		System.arraycopy(figures, 0, initialAssignment, 28, figures.length);
		statusPane.setStatusBar("Ready?");
	}
	
	public void shuffle(){
		FigureKind[]figures=new FigureKind[]{PAPER,PAPER,PAPER,PAPER,ROCK,ROCK,
				ROCK,ROCK,SCISSORS,SCISSORS,SCISSORS,SCISSORS};
		List<FigureKind>list=Arrays.asList(figures);
		Collections.shuffle(list);
		figures=(FigureKind[])list.toArray();
		for(int i=0,k=28;i<12;i++,k++){
			if(initialAssignment[k]==FLAG||initialAssignment[k]==TRAP){
				i--;
				continue;
			}
			initialAssignment[k]=figures[i];
		}
		if (statusPane != null) {
			statusPane.btnReady.setEnabled(true);
			statusPane.btnShuffle.setEnabled(true);
			statusPane.setStatusBar("Ready?");
		}
	}

	public void clear(){
		for(int i=0;i<42;i++)
			initialAssignment[i]=null;
		isTrapRequested=false;
		statusPane.setStatusBar("Set your flag...");
	}
	
	private void playerInitialAssignment(int entry) { // swaps two figures with clicking on them after each other
		if ((initialAssignment[entry] != null) && (initialAssignment[currentSelection] != null)) {
			FigureKind temp = initialAssignment[entry];
			initialAssignment[entry] = initialAssignment[currentSelection];
			initialAssignment[currentSelection] = temp;
		}
	}
	
	private void setFlag(int entry){
		if(initialAssignment[entry]==null&&entry>27&&entry<42){
			initialAssignment[entry]=FLAG;
			isTrapRequested=true;
			statusPane.setStatusBar("Set your trap...");
		}
	}
	private void setTrap(int entry){
		if(initialAssignment[entry]==null&&entry>27&&entry<42){
			initialAssignment[entry]=TRAP;
			shuffle();
		}
	}
	private void playerChoice(int x, int y) {
		int height = fieldPane.getHeight() / 3;
		int width = fieldPane.getWidth() / 3;
		if (y / height == 1) {
			try {
				switch (x / width) {
				case 0:
					playerChoiceRequested = false;
					game.setUpdatedKindAfterDraw(player, ROCK);
					break;
				case 1:
					playerChoiceRequested = false;
					game.setUpdatedKindAfterDraw(player, PAPER);
					break;
				case 2:
					playerChoiceRequested = false;
					game.setUpdatedKindAfterDraw(player, SCISSORS);
					break;
				}
				if(playerMoveRequested){
					statusPane.setStatusBar("Make a move...");
				}else{
					statusPane.setStatusBar("Opponent's turn...");
				}
			} catch (RemoteException e) {
				
				e.printStackTrace();
			}
		}
	}
	
	private void playerInitialChoice(int x, int y) {
		int height = fieldPane.getHeight() / 3;
		int width = fieldPane.getWidth() / 3;
		if (y / height == 1) {
			try {
				switch (x / width) {
				case 0:
					playerInitialChoiceRequested = false;
					game.setInitialChoice(player, ROCK);
					break;
				case 1:
					playerInitialChoiceRequested = false;
					game.setInitialChoice(player, PAPER);
					break;
				case 2:
					playerInitialChoiceRequested = false;
					game.setInitialChoice(player, SCISSORS);
					break;
				}
				if(playerMoveRequested){
					statusPane.setStatusBar("Make a move...");
				}else{
					statusPane.setStatusBar("Opponent's turn...");
				}
			} catch (RemoteException e) {
				
				e.printStackTrace();
			}
		}
	}
	
	private void playerMove(int entry) {
		try {
			if (game.getField()[currentSelection] == null || 
					!game.getField()[currentSelection].getKind().isMovable() ||
					!game.getField()[currentSelection].belongsTo(player))
				return;
			playerMoveRequested = false;
			game.move(player, currentSelection, entry);
			statusPane.setStatusBar("Opponent's turn...");
		} catch (RemoteException e) {
			
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			playerMoveRequested = true;
		}
	}
	
	boolean isUpPossible(int entry) throws RemoteException{
		Figure[]field=game.getField();
		return (entry-7 >= 0) && (field[entry-7] == null || !field[entry-7].belongsTo(player));
	}
	
	boolean isDownPossible(int entry) throws RemoteException{
		Figure[]field=game.getField();
		return (entry+7 <= 41) && (field[entry+7] == null || !field[entry+7].belongsTo(player));
	}
	
	boolean isLeftPossible(int entry) throws RemoteException{
		Figure[]field=game.getField();
		return ((entry % 7) > 0) && (field[entry-1] == null || !field[entry-1].belongsTo(player));
	}
	
	boolean isRightPossible(int entry) throws RemoteException{
		Figure[]field=game.getField();
		return (entry%7 < 6) && (field[entry+1] == null || !field[entry+1].belongsTo(player));
	}
	
	boolean isMoveable(int entry)throws RemoteException{
		Figure[]field=game.getField();
		return ((field[entry] != null) && (field[entry].getKind().isMovable()));
	}
	
	boolean isEnemy(int entry,String direction) throws RemoteException{
		Figure[]field=game.getField();
		if(direction=="up"){
			if(isUpPossible(entry)&&field[entry-7]!=null)
				return true;
			else return false;
		}
		else if(direction=="down"){
			if(isDownPossible(entry)&&field[entry+7]!=null)
				return true;
			else return false;
		}
		else if(direction=="left"){
			if(isLeftPossible(entry)&&field[entry-1]!=null)
				return true;
			else return false;
		}
		else if(direction=="right"){
			if(isRightPossible(entry)&&field[entry+1]!=null)
				return true;
			else return false;
		}
		else if(direction=="here"){
			if ((field[entry] != null ) && (field[entry].belongsTo(player) == false))
				return true;
			else return false;
		}
		else 
			return false;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int width = fieldPane.getWidth() / 7;
		int height = fieldPane.getHeight() / 6;

		int x = e.getX() / width;
		int y = e.getY() / height;
		int entry = x + y * 7;

		if (playerInitialAssignmentRequested) {

			if(currentSelection==-1&&initialAssignment[entry]==null&&isTrapRequested){
				setTrap(entry);
			}
			else if(currentSelection==-1&&initialAssignment[entry]==null){
				setFlag(entry);
			}
			else if (currentSelection == -1) {
				currentSelection = entry;
			} 
			else {
				playerInitialAssignment(entry); //for the initial assignment of the figures
				currentSelection = -1;
			}
		}
		
		if (playerMoveRequested) {
			if (currentSelection == -1) {
				currentSelection = entry;
			} else {
				playerMove(entry);
				currentSelection = -1;
			}
		}

		if (playerInitialChoiceRequested) {
			playerInitialChoice(e.getX(), e.getY()); //for the initial choice to determine who begins
			currentSelection = -1;
		}

		if (playerChoiceRequested) {
			playerChoice(e.getX(), e.getY()); //for the draw choice to determine who wins
			currentSelection = -1;
		}
	}
	
	/**
	 * @return the playerInitialAssignmentRequested
	 */
	public boolean isPlayerInitialAssignmentRequested() {
		return playerInitialAssignmentRequested;
	}
	
	/**
	 * @return the playerChoiceRequested
	 */
	public boolean isPlayerInitialChoiceRequested() {
		return playerInitialChoiceRequested;
	}

	/**
	 * @return the playerChoiceRequested
	 */
	public boolean isPlayerChoiceRequested() {
		return playerChoiceRequested;
	}
	
	/**
	 * @return the initialAssignment
	 */
	public FigureKind[] getInitialAssignment() {
		return initialAssignment;
	}
	
	/**
	 * @return the currentSelection
	 */
	public int getCurrentSelection() {
		return currentSelection;
	}
	
	public void requestInitialAssignment() {
		playerInitialAssignmentRequested = true;
	}
	
	public void requestMove() {
		playerMoveRequested = true;
		statusPane.setStatusBar("Make a move...");
	}
	
	public void requestInitialChoice() {
		playerInitialChoiceRequested = true;
		statusPane.setStatusBar("Make your choice...");
	}
	
	public void requestChoice() {
		statusPane.setStatusBar("Make your choice...");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		playerChoiceRequested = true;
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}
}
