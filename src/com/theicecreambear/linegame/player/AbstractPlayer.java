package com.theicecreambear.linegame.player;

import com.theicecreambear.linegame.gameobject.GameObject;

public abstract class AbstractPlayer extends GameObject {
	protected class Point {
		public int x;
		public int y;
		
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public String toString() {
			return "x: " + x + " y: " + y;
		}
	}
	
	protected enum Direction {
		UP,
		DOWN,
		LEFT,
		RIGHT;
	}
	
	
	
}