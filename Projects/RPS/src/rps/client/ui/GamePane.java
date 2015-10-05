package rps.client.ui;

import java.awt.Container;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import rps.game.Game;
import rps.game.data.Player;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.RemoteException;

public class GamePane{
	private JTextField chatInput;
	
	private final JPanel gamePane = new JPanel();
	private final FieldPane fieldPane = new FieldPane();
	private final StatusPane buttonPane = new StatusPane(fieldPane.getListener());
	
	JTextArea chat = new JTextArea();
	
	private Game game;
	private Player player;
	private final JScrollPane scrollPane = new JScrollPane();

	/**
	 * Create the panel.
	 */
	public GamePane(Container parent) {
		//setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		//add(gamePane);
		gamePane.setLayout(null);
		scrollPane.setBounds(10, 354, 521, 113);
		
		gamePane.add(scrollPane);
		scrollPane.setViewportView(chat);
		chat.setLineWrap(true);
		chat.setEditable(false);
		
		chatInput = new JTextField();
		chatInput.setBounds(10, 329, 521, 20);
		gamePane.add(chatInput);
		chatInput.setColumns(10);
		gamePane.setVisible(false);
		//setVisible(false);
		
		
		buttonPane.setBounds(541, 11, 111, 456);
		gamePane.add(buttonPane);
		
		JPanel fieldArea = new JPanel();
		fieldArea.setBounds(10, 11, 521, 314);
		gamePane.add(fieldArea);
		fieldArea.setLayout(new BoxLayout(fieldArea, BoxLayout.X_AXIS));
		fieldArea.add(fieldPane);
		parent.add(gamePane);
		bindButtons();

	}
	public StatusPane getStatusPane(){
		return buttonPane;
	}
	
	public FieldPane getFieldPane() {
		return fieldPane;
	}
	
	private void bindButtons() {
		chatInput.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				boolean isEnter = e.getKeyCode() == KeyEvent.VK_ENTER;
				if (isEnter) {
					addToChat();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
	}
	
	private void addToChat() {
		String message = chatInput.getText().trim();
		if (message.length() > 0) {
			try {
				game.sendMessage(player, message);
				chatInput.setText("");
			} catch (RemoteException e) {
				
				e.printStackTrace();
			}
		}
	}
	
	public void hide() {
		gamePane.setVisible(false);
		fieldPane.setVisible(false);
	}

	public void startGame(Player player, Game game) {
		this.player = player;
		this.game = game;
		reset();
		gamePane.setVisible(true);
		fieldPane.startGame(player, game);
		buttonPane.startGame();
	}

	public void receivedMessage(Player sender, String message) {

		if (chat.getText().length() != 0) {
			chat.append("\n");
		}
		String formatted = sender.getNick() + ": " + message;
		chat.append(formatted);
		chat.setCaretPosition(chat.getDocument().getLength());
	}

	private void reset() {
		chat.setText(null);
	}
	public void setChat(String s){
		  if(chat.getText().length()!=0){
		   chat.append("\n");
		  }
		  chat.append(s);
		  chat.setCaretPosition(chat.getDocument().getLength());
		  
		 }
}
