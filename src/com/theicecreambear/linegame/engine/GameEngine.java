package com.theicecreambear.linegame.engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import com.theicecreambear.linegame.gameobject.GameObject;
import com.theicecreambear.linegame.gameobject.RenderLockObject;
import com.theicecreambear.linegame.interfaces.Drawable;
import com.theicecreambear.linegame.interfaces.Updateable;
import com.theicecreambear.linegame.player.AbstractPlayer;
import com.theicecreambear.linegame.player.LocalPlayer;
import com.theicecreambear.linegame.refrence.Refrence;
import com.theicecreambear.linegame.screen.Screen;
import com.theicecreambear.linegame.starter.Starter;
import com.theicecreambear.linegame.threads.RenderThread;

/**
 * 
 * @author Joseph Terribile - Current Maintainer
 * @author David Santamaria - Original Author
 *
 */
public class GameEngine {
	// Starter stuff
	public boolean notStarted = true;
	public int id;
	// end starter
	
	public static boolean running = true;
	public static Random rand;
	public static GameEngine instance;
	public static String stats = "";
	public int ticks;
	public JFrame frame;
	public Graphics g;
	public Graphics g2;
	public BufferedImage i;
	public AbstractPlayer p1;
	public AbstractPlayer p2;
	
	public boolean resetRequested = false;
	
	public RenderLockObject rlo;
	private RenderThread rtInstance;
	
	/* The three types of Game Objects */
	static ArrayList<GameObject> updateableAndDrawable = new ArrayList<GameObject>();
	static ArrayList<Updateable> updateable = new ArrayList<Updateable>();
	static ArrayList<Drawable> drawable = new ArrayList<Drawable>();
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		if (Refrence.DEBUG_MODE) {			
			System.out.println(Runtime.getRuntime().maxMemory());
			System.err.println("x: " + Screen.width + "y: " + Screen.height);
		}
		new GameEngine();
	}
	
	public static void startGameEngine() {
		new GameEngine();
	}
	
	public GameEngine() {
		rand = new Random();
		initialize();
		
		if (Refrence.STARTER) {
			// keeps the engine stopped until the player makes a decision.
			while (notStarted) {
				System.out.print("");
				if (Refrence.DEBUG_MODE) {				
					System.err.println("WAITING");
				}
			}
		} else {
			this.id = Refrence.LOCAL_1V1;
		}
		
		initializePlayers(id);
		run();
		boolean alive = true;
		while (alive) {
			reinitialize(id);
			run();
		}
	}
	
	public void reinitialize(int id) {
		updateableAndDrawable.remove(p1);
		updateableAndDrawable.remove(p2);
		
		p1 = null;
		p2 = null;
		
		System.gc();
		
		switch (id) {
			case Refrence.LOCAL_1V1: 
				p1 = new LocalPlayer(frame, new Color(27, 156, 255), 1);
				p2 = new LocalPlayer(frame, new Color(240, 70, 3), 2);
				updateableAndDrawable.add(p1);
				updateableAndDrawable.add(p2);
				break;
			case Refrence.LOCAL_1V1_AI: 
				p1 = new LocalPlayer(frame, new Color(130, 130, 130), 1);
				updateableAndDrawable.add(p1);
				break;
		}
	}

	public void initialize() {
		
		// TODO Server only sends clients the positions of the things and their color and the score.
		
		frame = new JFrame("Line game");
		frame.setBounds(0, 0, Screen.width, Screen.height);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		rlo = new RenderLockObject();
		rtInstance = new RenderThread("RenderThread", rlo, this);
		rtInstance.start();
		
		// TODO
		
		i = new BufferedImage(Screen.width, Screen.height, BufferedImage.TYPE_INT_RGB);
		g2 = i.createGraphics();
		g = frame.getGraphics();
		
		System.gc();

		if (Refrence.STARTER) {
			new Starter(frame);
		}
		
		instance = this;
	}

	public void initializePlayers(int id) {
		switch (id) {
			case Refrence.LOCAL_1V1: 
				p1 = new LocalPlayer(frame, new Color(27, 156, 255), 1);
				p2 = new LocalPlayer(frame, new Color(240, 70, 3), 2);
				updateableAndDrawable.add(p1);
				updateableAndDrawable.add(p2);
				break;
			case Refrence.LOCAL_1V1_AI: 
				p1 = new LocalPlayer(frame, new Color(130, 130, 130), 1);
				updateableAndDrawable.add(p1);
				break;
//			default: System.exit(-1);
		}
	}
	
	public void update(double deltaTime) {
		for(GameObject gameObject: updateableAndDrawable) {
			gameObject.update(deltaTime);
		}
		
		for(Updateable upject : updateable) {
			upject.update(deltaTime);
		}
	}
	
	public void updateNetwork(double deltaTime) {
		
	}

	public void render(Graphics g, ImageObserver observer) {
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, Screen.width, Screen.height);
		
		// Field Drawing
		
//		for (int i = 0; i < Screen.width; i += 30) {
//			idea to draw a grid
//		}
		
		if (Refrence.DEBUG_MODE) {
			g2.setColor(Color.blue);
		}
		
		// End Field Drawing
		
		for(GameObject gameObject : updateableAndDrawable) {
			gameObject.draw(g2, observer);
		}
		
		for(Drawable staject : drawable) {
			staject.draw(g, observer);
		}
		
		if (Refrence.DEBUG_MODE) {
			g2.setColor(Color.GREEN);
			g2.setFont(new Font("Arial", 1, 20));
			g2.drawString(stats, 25, 60);			
		}
		
		g.drawImage(i, 0, 0, frame);
	}

	public void run() {
		long time = System.nanoTime();
		final double tick = 60.0;
		double ms = 1000000000 / tick;
		double deltaTime = 0;
		ticks = 0;
		int fps = 0;
		long timer = System.currentTimeMillis();
		long frameLimit = 80;
		long currentTime;
		int seconds = 0;
		int minutes = 0;
		int hours = 0;

		while (running) {
			
			currentTime = System.nanoTime();
			deltaTime += (currentTime - time) / ms;
			time = currentTime;

			if (deltaTime >= 1) {
				ticks++;
				update(deltaTime);
				deltaTime--;
			}
			
			if (resetRequested) {
				this.resetRequested = false;
				return;
			}
			
			synchronized (rlo) {
				rlo.wasNotified = true;
				rlo.notify();
			}
			fps++;
			
			while (deltaTime < frameLimit) {
				currentTime = System.nanoTime();
				deltaTime += (currentTime - time) / ms;
				time = currentTime;
			}
			
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				seconds++;
				if(seconds > 60) {
					seconds %= 60;
					minutes++;
					
					if(minutes > 60) {
						minutes %= 60;
						hours++;
					}
				}
				
				// GT stands for GameTime. P.C stands for Player coordinates
				stats = "Ticks: " + ticks + ", FPS: " + fps + ", GT: " + ((hours < 10) ? "0" + hours : hours) + ":" + ((minutes < 10) ? "0" + minutes : minutes) + ":" + ((seconds < 10) ? "0" + seconds : seconds);
				if (Refrence.DEBUG_MODE) {					
					System.out.println(stats);
				}
				ticks = 0;
				fps = 0;
				if (Refrence.DEBUG_MODE) {					
					System.out.println(Runtime.getRuntime().freeMemory());
				}
				System.gc();
				if (Refrence.DEBUG_MODE) {					
					System.out.println(Runtime.getRuntime().freeMemory());
				}
			}
		}
	}

	public enum EngineType {
		CLIENT,
		SERVER,
		LOCAL_GAME;
	}
	
}

/*

-XX:+UnlockCommercialFeatures -XX:+FlightRecorder -XX:FlightRecorderOptions=stackdepth=1024 -XX:StartFlightRecording=duration=60m,filename=LineGame.jfr
*/