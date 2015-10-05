package rps.client.ui;

import static javax.swing.BoxLayout.Y_AXIS;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import rps.client.UIController;

public class AboutPane {

	private final UIController controller;

	private final JPanel aboutPane = new JPanel();
	private final JButton backBtn = new JButton("back");

	public AboutPane(Container parent, UIController controller) {
		this.controller = controller;
		aboutPane.setLayout(new BoxLayout(aboutPane, Y_AXIS));

		aboutPane.add(new JLabel("Lukas Appelhans, Informatik, 1. Semester"));
		aboutPane.add(new JLabel(" "));
		aboutPane.add(new JLabel("Muharrem Onur Vural, Informatik, 1. Semester"));
		aboutPane.add(new JLabel(" "));
		aboutPane.add(new JLabel("Paul Kiefer, Mathe, 1. Semester"));
		aboutPane.add(new JLabel(" "));
		aboutPane.add(new JLabel("Yasin Turkac, Physik, 1. Semester"));
		aboutPane.add(new JLabel(" "));
		aboutPane.add(backBtn);
		aboutPane.setVisible(false);
		parent.add(aboutPane);

		bindActions();
	}

	private void bindActions() {
		backBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.switchBackToStartup();
			}
		});
	}

	public void show() {
		aboutPane.setVisible(true);
	}

	public void hide() {
		aboutPane.setVisible(false);
	}
}