/**
 * 
 */
package rps.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author Paul Kiefer 1949092
 *
 */
public class StatusPane extends JPanel {

	private static final long serialVersionUID = 1L;
	
	JButton btnReady = new JButton("Ready");
	JButton btnShuffle = new JButton("Shuffle");
	JButton btnShuffleAll = new JButton("Shuffle All");
	JButton btnClear = new JButton("Clear");
	PlayerListener listener;
	private final JTextArea statusBar = new JTextArea();
	
	/**
	 * 
	 */
	public StatusPane(PlayerListener l) {
		this.listener = l;
		this.listener.setStatusPane(this);
		
		this.add(btnReady);		
		btnReady.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (listener.setInitialAssignment()) {
					btnReady.setEnabled(false);
					btnShuffle.setEnabled(false);
					btnShuffleAll.setEnabled(false);
					btnClear.setEnabled(false);
				}
			}
		});
		
		this.add(btnShuffle);
		btnShuffle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				listener.shuffle();
				btnReady.setEnabled(true);
			}
		});

		this.add(btnShuffleAll);
		btnShuffleAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.shuffleAll();
				btnReady.setEnabled(true);
				btnShuffle.setEnabled(true);
			}
		});

		this.add(btnClear);
		statusBar.setEditable(false);
		
		add(statusBar);
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				listener.clear();
				btnReady.setEnabled(false);
				btnShuffle.setEnabled(false);
			}
		});
	}
	
	public void startGame() {
		btnReady.setVisible(true);
		btnReady.setEnabled(false);
		btnShuffle.setVisible(true);
		btnShuffle.setEnabled(false);
		btnShuffleAll.setVisible(true);
		btnShuffleAll.setEnabled(true);
		btnClear.setVisible(true);
		btnClear.setEnabled(true);
		setStatusBar("Set your flag...");
	}
	public void setStatusBar(String s){
		statusBar.setText(s);
	}
}
