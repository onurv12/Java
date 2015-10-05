/**
 * 
 */
package rps.client.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import rps.game.Game;
import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Player;

/**
 * @author Paul Kiefer 1949092
 *
 */
public class FieldPane extends JPanel implements Runnable {

	private Game game;
	private Player player;
	private final PlayerListener listener;
	private static final String userdir = System.getProperty("user.dir");
	private static final String imagedir = "/src/rps/client/ui/images/";
		
	private static final long serialVersionUID = 1L;
		
	/**
	 * @return the listener
	 */
	public PlayerListener getListener() {
		return listener;
	}

	private BufferedImage field1Image = null;
	private BufferedImage field2Image = null;
	private BufferedImage trapGoodImage = null;
	private BufferedImage trapBadImage = null;
	private BufferedImage flagGoodImage = null;
	private BufferedImage flagBadImage = null;
	private BufferedImage paperGoodImage = null;
	private BufferedImage paperBadImage = null;
	private BufferedImage rockGoodImage = null;
	private BufferedImage rockBadImage = null;
	private BufferedImage scissorsGoodImage = null;
	private BufferedImage scissorsBadImage = null;
	private BufferedImage hiddenImage = null;
	private BufferedImage upImage=null;
	private BufferedImage downImage=null;
	private BufferedImage leftImage=null;
	private BufferedImage rightImage=null;
	private BufferedImage fromImage=null;
	private BufferedImage toImage=null;
	private BufferedImage shadowImage=null;
	
	FieldPane() {
		super(true); // doubleBuffered not really needed yet, might be useful later..
									
		// Mouselistener
		listener = new PlayerListener(game, player, this);
		this.addMouseListener(listener);
				
		// Loading images
		try {
			field1Image = ImageIO.read(new File(userdir + imagedir + "field1.gif")); //TODO image
			field2Image = ImageIO.read(new File(userdir + imagedir + "field2.gif")); //TODO image
			trapGoodImage = ImageIO.read(new File(userdir + imagedir + "trapGood.gif")); //TODO image
			trapBadImage = ImageIO.read(new File(userdir + imagedir + "trapBad.gif")); //TODO image
			paperGoodImage = ImageIO.read(new File(userdir + imagedir + "paperGood.gif")); //TODO image
			paperBadImage = ImageIO.read(new File(userdir + imagedir + "paperBad.gif")); //TODO image
			rockGoodImage = ImageIO.read(new File(userdir + imagedir + "rockGood.gif")); //TODO image
			rockBadImage = ImageIO.read(new File(userdir + imagedir + "rockBad.gif")); //TODO image
			scissorsGoodImage = ImageIO.read(new File(userdir + imagedir + "scissorsGood.gif")); //TODO image
			scissorsBadImage = ImageIO.read(new File(userdir + imagedir + "scissorsBad.gif")); //TODO image
			flagGoodImage = ImageIO.read(new File(userdir + imagedir + "flagGood.gif")); //TODO image
			flagBadImage = ImageIO.read(new File(userdir + imagedir + "flagGood.gif")); //TODO image
			hiddenImage = ImageIO.read(new File(userdir + imagedir + "hidden.gif")); //TODO image
			upImage=ImageIO.read(new File(userdir + imagedir + "up.gif"));//TODO image
			downImage=ImageIO.read(new File(userdir + imagedir + "down.gif"));//TODO image
			leftImage=ImageIO.read(new File(userdir + imagedir + "left.gif"));//TODO image
			rightImage=ImageIO.read(new File(userdir + imagedir + "right.gif"));//TODO image
			fromImage=ImageIO.read(new File(userdir + imagedir + "from.gif"));//TODO image
			toImage=ImageIO.read(new File(userdir + imagedir + "to.gif"));//TODO image
			shadowImage=ImageIO.read(new File(userdir + imagedir + "shadow.gif"));//TODO image
		} catch(IOException ex) {
			
			ex.printStackTrace(); // Could not load images
		}
	}
	public void startGame(Player player, Game game) {
		this.player = player;
		this.game = game;
		
		setVisible(true);
		
		// Thread to repaint the JPanel
		new Thread(this).start();
		listener.startGame(player, game);
	}
	
	private void paintField(Graphics2D g) {
		int width = getWidth() / 7;
		int height = getHeight() / 6;
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				if ((i + j) % 2 == 0) {
					g.drawImage(field1Image, width * i, height * j, width, height, this);
				} else {
					g.drawImage(field2Image, width * i, height * j, width, height, this);
				}
			}
		}
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				if (listener.getCurrentSelection() == i+j*7) {
					g.setColor(new Color(255, 0, 0, 50));
					g.fillRect(width * i, height * j, width, height); //TODO highlight playerchoice more beautiful
					try {
						if (!listener.isEnemy(listener.getCurrentSelection(),"here") && 
								listener.isMoveable(listener.getCurrentSelection())) {//If we selected our player, we can draw arrows
							if (listener.isUpPossible(listener.getCurrentSelection())) {
								if (!listener.isEnemy(listener.getCurrentSelection(),"up"))
									g.drawImage(dyeImage(upImage,Color.YELLOW),width*i,height*(j-1),width,height,this);
								else 
									g.drawImage(dyeImage(upImage,Color.BLACK),width*i,height*(j-1),width,height,this);
							}
							if (listener.isDownPossible(listener.getCurrentSelection())) {
								if (!listener.isEnemy(listener.getCurrentSelection(),"down"))
									g.drawImage(dyeImage(downImage,Color.YELLOW),width*i,height*(j + 1),width,height,this);
								else 
									g.drawImage(dyeImage(downImage,Color.BLACK),width*i,height*(j + 1),width,height,this);
							}
							if (listener.isLeftPossible(listener.getCurrentSelection())) {
								if (!listener.isEnemy(listener.getCurrentSelection(),"left"))
									g.drawImage(dyeImage(leftImage,Color.YELLOW),width*(i-1),height*j,width,height,this);
								else 
									g.drawImage(dyeImage(leftImage,Color.BLACK),width*(i-1),height*j,width,height,this);
							}
							if (listener.isRightPossible(listener.getCurrentSelection())) {
								if (!listener.isEnemy(listener.getCurrentSelection(),"right"))
									g.drawImage(dyeImage(rightImage,Color.YELLOW),width*(i+1),height*j,width,height,this);
								else
									g.drawImage(dyeImage(rightImage,Color.BLACK),width*(i+1),height*j,width,height,this);
							}
						}
					} catch (RemoteException e) {
						
						e.printStackTrace();
					}
				}
			}
		}
	}

	private BufferedImage dyeImage(BufferedImage im, Color c) {
		int width = im.getWidth();
        int height = im.getHeight();
        BufferedImage dyed = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dyed.createGraphics();
        g.drawImage(im, 0,0, null);
        g.setComposite(AlphaComposite.SrcAtop);
        g.setColor(c);
        g.fillRect(0,0,width,height);
        g.dispose();
        return dyed;
	}
	
	private void paintFigures(Graphics2D g) {
		try {
			Figure[] figures = game.getField();
			Figure[] field = new Figure[42];
			FigureKind[] initialAssignments;
			if (listener.isPlayerInitialAssignmentRequested()) { // in case the initial assignment isn't done yet
				initialAssignments = listener.getInitialAssignment();
				for (int i = 0; i < initialAssignments.length; i++) {
					if (initialAssignments[i] != null) {
						field[i] = new Figure(initialAssignments[i], player);
					}
				}
			}
			for (int i = 0; i < 42; i++) {
				if ((field[i] == null) && (figures[i] != null)) {
					field[i] = figures[i];
				}
			}
			
			int width = getWidth() / 7;
			int height = getHeight() / 6;
			for (int i = 0; i < 7; i++) { 	//since it's given as a 1D array i make it more clear by working with it like a 2D array
											//  0  1  2  3  4  5  6 
											//  7  8  9 10 11 12 13
											// ...
				for (int j = 0; j < 6; j++) {
					if (field[i+j*7] != null) {
						BufferedImage flag;
						BufferedImage paper;
						BufferedImage rock;
						BufferedImage scissors;
						BufferedImage trap;
						if (field[i+j*7].belongsTo(player)) {
							flag=flagGoodImage;
							paper=paperGoodImage;
							rock=rockGoodImage;
							scissors=scissorsGoodImage;
							trap=trapGoodImage;
						} else {
							flag=flagBadImage;
							paper=paperBadImage;
							rock=rockBadImage;
							scissors=scissorsBadImage;
							trap=trapBadImage;
						}
						switch (field[i+j*7].getKind()) {
						case FLAG:
							g.drawImage(flag, width * i, height * j, width, height, this);
							break;
						case HIDDEN:
							g.drawImage(hiddenImage, width * i, height * j, width, height, this);
							break;
						case PAPER:
							g.drawImage(paper, width * i, height * j, width, height, this);
							break;
						case ROCK:
							g.drawImage(rock, width * i, height * j, width, height, this);
							break;
						case SCISSORS:
							g.drawImage(scissors, width * i, height * j, width, height, this);
							break;
						case TRAP:
							g.drawImage(trap, width * i, height * j, width, height, this);
							break;
						}
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	private void paintPreviousMove(Graphics2D g){
		try {
			int width=getWidth()/7;
			int height=getHeight()/6;
			if(game.getLastMove()!=null){
				int from=game.getLastMove().getFrom();
				int to=game.getLastMove().getTo();
				g.drawImage(dyeImage(fromImage, Color.GRAY), width * (from%7), height * (from/7), width, height, this);
				g.drawImage(dyeImage(toImage, Color.GRAY), width * (to%7), height * (to/7), width, height, this);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void paintShadows(Graphics2D g){
		
		try {
			int width=getWidth()/7;
			int height=getHeight()/6;
			Figure[]figures=game.getField();
			for(int i=0;i<42;i++){
				if(figures!=null&&figures[i]!=null&&figures[i].belongsTo(player)&&!figures[i].isDiscovered()){
					g.drawImage(shadowImage, width * (i%7), height * (i/7), width, height, this);
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int fading = 0;
	
	private void paintRockPaperScissor(Graphics2D g) {
		int width = getWidth() / 3;
		int height = getHeight() / 3;
		
		g.setColor(new Color(50, 50, 50, fading));
		g.fillRect(0, 0, getWidth(), getHeight()); //TODO more beautiful
		
		g.setColor(new Color(255, 255, 255));
		
		g.fillRect((int)(fading / 200. * width - width), height, width + 5, height);
		g.drawImage(rockGoodImage, (int)(fading / 200. * width - width), height, width, height, this);
		
		g.fillRect(width, (int)(fading / 100. * height - height), width + 5, height);
		g.drawImage(paperGoodImage, width, (int)(fading / 100. * height - height), width, height, this);
		
		g.fillRect((int)(width * 3 - fading / 200. * width), height, width + 5, height);
		g.drawImage(scissorsGoodImage, (int)(width * 3 - fading / 200. * width), height, width, height, this);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		g2.clearRect(0, 0, getWidth(), getHeight());
				
		paintField(g2);
		paintPreviousMove(g2);
		paintShadows(g2);
		paintFigures(g2);
				
		if (listener.isPlayerChoiceRequested() || listener.isPlayerInitialChoiceRequested()) {
			paintRockPaperScissor(g2);
			if (fading < 200) {
				fading += 5;
			}
		} else {
			fading = 0;
		}
	}
	
	@Override
	public void run() {
		while (isShowing()) {
			repaint();
			try {
				Thread.sleep(10); //TODO 100 repaints per second (a bit less)
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
