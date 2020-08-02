package com.theicecreambear.linegame.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import com.theicecreambear.linegame.screen.Screen;

public class AIPlayer extends AbstractPlayer {
	private Color color;

	private int x = Screen.width - 21;
	private int y = 37;
	
	private Direction direction;
	private Direction previousDirection;

	protected ArrayList<Point> pointList;

	// TODO difficulty stuff

	public AIPlayer() {
		this(Color.white);
	}

	public AIPlayer(Color color) {
		this.color = color;
		this.direction = Direction.DOWN;
	}

	@Override
	public void update(double deltaTime) {
		this.previousDirection = this.direction;
		
		// TODO, AI
		
		if (this.previousDirection != this.direction) {
			this.addPoint();
		}
	}

	@Override
	public void draw(Graphics g, ImageObserver observer) {
		g.setColor(this.color);
		
		int maxIter = this.pointList.size();
		for (int i = 0; i < maxIter; i++) {
			if (i - 1 < 0) {
				if (this.pointList.size() > 1) {
					continue;
				}
				AbstractPlayer.Point p1 = this.pointList.get(i);
				AbstractPlayer.Point p2 = new AbstractPlayer.Point(x, y);
				g.drawLine(p1.x, p1.y, p2.x, p2.y);
				continue;
			}
			
			if (i == this.pointList.size() - 1) {
				AbstractPlayer.Point p0 = this.pointList.get(i - 1);
				AbstractPlayer.Point p1 = this.pointList.get(i);
				AbstractPlayer.Point p2 = new AbstractPlayer.Point(x, y);
				
				g.drawLine(p0.x, p0.y, p1.x, p1.y);
				g.drawLine(p1.x, p1.y, p2.x, p2.y);
				continue;
			}
			
			AbstractPlayer.Point p1 = this.pointList.get(i);
			AbstractPlayer.Point p2 = this.pointList.get(i - 1);
			
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
		
		g.fillRect(x - 2, y - 2, 5, 5);
	}
	
	private void addPoint() {
		this.pointList.add(new Point(this.x, this.y));
	}
}