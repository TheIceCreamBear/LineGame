package com.theicecreambear.linegame.player;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.theicecreambear.linegame.engine.GameEngine;
import com.theicecreambear.linegame.handlers.InputHandler;
import com.theicecreambear.linegame.refrence.KeyBindings;
import com.theicecreambear.linegame.refrence.Refrence;
import com.theicecreambear.linegame.screen.Screen;

public class LocalPlayer extends AbstractPlayer {
	
	private InputHandler handler;
	private Color color;
	
	private boolean isDead;
	
	private int moveWaitTimer = 0;
	private int lives;

	private int id;
	
	private int x = 0;
	private int y = 400;
	
	private int upKey;
	private int downKey;
	private int leftKey;
	private int rightKey;
	
	private Direction direction;
	private Direction previousDirection;

	protected ArrayList<Point> pointList;
	
	public LocalPlayer(Component c) {
		this(c, Color.blue, 1);
	}
	
	public LocalPlayer(Component c, Color color, int id) {
		this.color = color;
		this.handler = new InputHandler(c);
		this.pointList = new ArrayList<Point>();
		this.id = id;
		this.lives = 3;
		
		if (id == 1) {
			this.upKey = KeyBindings.player1Up;
			this.downKey = KeyBindings.player1Down;
			this.leftKey = KeyBindings.player1Left;
			this.rightKey = KeyBindings.player1Right;
			this.direction = Direction.RIGHT;
			this.x = 5;
		} else if (id == 2) {
			this.upKey = KeyBindings.player2Up;
			this.downKey = KeyBindings.player2Down;
			this.leftKey = KeyBindings.player2Left;
			this.rightKey = KeyBindings.player2Right;
			this.x = Screen.width - 5;
			this.direction = Direction.LEFT;
		}
		
		this.addPoint();
	}
	

	@Override
	public void update(double deltaTime) {
		if (this.moveWaitTimer > 0) {
			this.moveWaitTimer--;
			return;
		}
		
		if (this.isDead) {
			JOptionPane.showMessageDialog(null, "Player " + (this.id == 1 ? "2" : "1") + " wins!");
			
			// 0 == yes, 1 == no, 2 == cancel
			int decision = JOptionPane.showConfirmDialog(null, "Do you want to play again?");
			if (decision != 0) {
				JOptionPane.showMessageDialog(null, "Thank you for playing.");
				System.exit(0);
			}
			
			// FIGURE A WAY TO RESET EVERYTHING
			GameEngine.instance.resetRequested = true;
			return;
		}
		
		this.previousDirection = this.direction;
		
		if (Refrence.HARD_CORE_DEBUG_MODE) {
			System.err.println("Player: " + this + "  PointSize: " + this.pointList.size() + "  ID: " + this.id);
			for (int i = 0; i < this.pointList.size(); i++) {
				System.err.println(this.pointList.get(i));
			}
		}
		
		if (handler.isKeyDown(KeyEvent.VK_ESCAPE)) {
			System.exit(-1);
		}
		
		// Start direction change
		if (handler.isKeyDown(this.upKey) && !isOtherMoveKeyDown(KeyEvent.VK_W) && this.direction != Direction.DOWN) {
			this.direction = Direction.UP;
		}

		if (handler.isKeyDown(this.downKey) && !isOtherMoveKeyDown(KeyEvent.VK_S) && this.direction != Direction.UP) {
			this.direction = Direction.DOWN;
		}
		
		if (handler.isKeyDown(this.leftKey) && !isOtherMoveKeyDown(KeyEvent.VK_A) && this.direction != Direction.RIGHT) {
			this.direction = Direction.LEFT;
		}
		
		if (handler.isKeyDown(this.rightKey) && !isOtherMoveKeyDown(KeyEvent.VK_D) && this.direction != Direction.LEFT) {
			this.direction = Direction.RIGHT;
		}
		// end direction change
		
		// point list addition
		if (this.previousDirection != this.direction) {
			this.addPoint();
		}
		// end point list addition
		
		
		// Check colliding
		if (this.isColiding((LocalPlayer) GameEngine.instance.p1)) {
			this.kill();
		}
		
		if (this.isColiding((LocalPlayer) GameEngine.instance.p2)) {
			this.kill();
		}
		// end collision
		
		
		// start actual move
		switch (this.direction) {
			case UP:
				this.y -= 2;
				break;
			case DOWN:
				this.y += 2;
				break;
			case LEFT:
				this.x -= 2;
				break;
			case RIGHT:
				this.x += 2;
				break;
		}
		// end actual move
	}

	@Override
	public void draw(Graphics g, ImageObserver observer) {
		g.setColor(this.color);
		
		int maxIter = this.pointList.size();
		for (int i = 0; i < maxIter; i++) {
			
			// First Point or check if first can be skipped
			if (i - 1 < 0) {
				// if first can be skipped 
				if (this.pointList.size() > 1) {
					continue;
				}
				AbstractPlayer.Point p1 = this.pointList.get(i);
				AbstractPlayer.Point p2 = new AbstractPlayer.Point(x, y);
				g.drawLine(p1.x, p1.y, p2.x, p2.y);
				continue;
			}
			
			// Last two points in list, with line of current movement
			if (i == this.pointList.size() - 1) {
				AbstractPlayer.Point p0 = this.pointList.get(i - 1);
				AbstractPlayer.Point p1 = this.pointList.get(i);
				AbstractPlayer.Point p2 = new AbstractPlayer.Point(x, y);
				
				g.drawLine(p0.x, p0.y, p1.x, p1.y);
				g.drawLine(p1.x, p1.y, p2.x, p2.y);
				continue;
			}
			
			// points on iteration.this grabs the first point as well
			AbstractPlayer.Point p1 = this.pointList.get(i - 1);
			AbstractPlayer.Point p2 = this.pointList.get(i);
			
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
		
		g.setFont(new Font("Arial", 1, 20));
		String s = "Lives: " + new Integer(this.lives);
		
		if (id == 1) {
			g.drawString(s, 50, 75);
		} else {
			g.drawString(s, Screen.width - 100 - s.length(), 75);
		}
		
		g.fillRect(x - 2, y - 2, 5, 5);
	}

	private boolean isOtherMoveKeyDown(int currentKey) {
		if (currentKey == KeyEvent.VK_W) {
			if (handler.isKeyDown(this.downKey)) {
				return true;
			} else if (handler.isKeyDown(this.leftKey)) {
				return true;
			} else if (handler.isKeyDown(this.rightKey)) {
				return true;
			}
		} else if (currentKey == KeyEvent.VK_S) {
			if (handler.isKeyDown(this.upKey)) {
				return true;
			} else if (handler.isKeyDown(this.leftKey)) {
				return true;
			} else if (handler.isKeyDown(this.rightKey)) {
				return true;
			}
		} else if (currentKey == KeyEvent.VK_A) {
			if (handler.isKeyDown(this.downKey)) {
				return true;
			} else if (handler.isKeyDown(this.upKey)) {
				return true;
			} else if (handler.isKeyDown(this.rightKey)) {
				return true;
			}
		} else if (currentKey == KeyEvent.VK_D) {
			if (handler.isKeyDown(this.downKey)) {
				return true;
			} else if (handler.isKeyDown(this.leftKey)) {
				return true;
			} else if (handler.isKeyDown(this.upKey)) {
				return true;
			}
		}

		return false;
	}
	
	private void addPoint() {
		this.pointList.add(new Point(this.x, this.y));
	}
	
	/**
	 * checks if in bounds and not coliding
	 * @param other - player to be checked
	 * @return is this player is coliding with the wall or the given player
	 */
	protected boolean isColiding(LocalPlayer other) {
		ArrayList<Point> theirList = other.pointList;
		int ox = other.x;
		int oy = other.y;
		
		int maxIter = theirList.size();
		for (int i = 0; i < maxIter; i++) {
			
			// First Point or check if first can be skipped
			if (i - 1 < 0) {
				// if first can be skipped 
				if (theirList.size() > 1) {
					continue;
				}
				AbstractPlayer.Point p1 = theirList.get(i);
				AbstractPlayer.Point p2 = new AbstractPlayer.Point(ox, oy);
				
				if (p1.x == p2.x) {
					if (p1.x == this.x) {
						if (p1.y > p2.y) {
							if (this.y < p1.y && this.y > p2.y) {
								return true;
							}
						} else {
							if (this.y < p2.y && this.y > p1.y) {
								return true;
							}
						}
					}
				}
				
				if (p1.y == p2.y) {
					if (p1.y == this.y) {
						if (p1.x > p2.x) {
							if (this.x < p1.x && this.x > p2.x) {
								return true;
							}
						} else {
							if (this.x < p2.x && this.x > p1.x) {
								return true;
							}
						}
					}
				}
				continue;
			}
			
			// Last two points in list, with line of current movement
			if (i == theirList.size() - 1) {
				AbstractPlayer.Point p0 = theirList.get(i - 1);
				AbstractPlayer.Point p1 = theirList.get(i);
				AbstractPlayer.Point p2 = new AbstractPlayer.Point(ox, oy);
				if (p0.x == p1.x) {
					if (p0.x == this.x) {
						if (p0.y > p1.y) {
							if (this.y < p0.y && this.y > p1.y) {
								return true;
							}
						} else {
							if (this.y < p1.y && this.y > p0.y) {
								return true;
							}
						}
					}
				}
				
				if (p0.y == p1.y) {
					if (p0.y == this.y) {
						if (p0.x > p1.x) {
							if (this.x < p0.x && this.x > p1.x) {
								return true;
							}
						} else {
							if (this.x < p1.x && this.x > p0.x) {
								return true;
							}
						}
					}
				}
				
				
				
				if (p1.x == p2.x) {
					if (p1.x == this.x) {
						if (p1.y > p2.y) {
							if (this.y < p1.y && this.y > p2.y) {
								return true;
							}
						} else {
							if (this.y < p2.y && this.y > p1.y) {
								return true;
							}
						}
					}
				}
				
				if (p1.y == p2.y) {
					if (p1.y == this.y) {
						if (p1.x > p2.x) {
							if (this.x < p1.x && this.x > p2.x) {
								return true;
							}
						} else {
							if (this.x < p2.x && this.x > p1.x) {
								return true;
							}
						}
					}
				}
				continue;
			}
			
			// points on iteration.this grabs the first point as well
			AbstractPlayer.Point p1 = theirList.get(i - 1);
			AbstractPlayer.Point p2 = theirList.get(i);
			
			if (p1.x == p2.x) {
				if (p1.x == this.x) {
					if (p1.y > p2.y) {
						if (this.y < p1.y && this.y > p2.y) {
							return true;
						}
					} else {
						if (this.y < p2.y && this.y > p1.y) {
							return true;
						}
					}
				}
			}
			
			if (p1.y == p2.y) {
				if (p1.y == this.y) {
					if (p1.x > p2.x) {
						if (this.x < p1.x && this.x > p2.x) {
							return true;
						}
					} else {
						if (this.x < p2.x && this.x > p1.x) {
							return true;
						}
					}
				}
			}
			
		}
		
		if (this.x > Screen.width || this.x < 0) {
			return true;
		}
		
		if (this.y > Screen.height || this.y < 0) {
			return true;
		}
		
		return false;
	}
	
	private void kill() {
		this.lives--;
		if (this.lives == 0) {
			this.isDead = true;
		}
		
		this.pointList.clear();
		this.moveWaitTimer = 10;
		if (this.id == 1) {
			this.x = 5;
			this.y = 400;
			this.direction = Direction.RIGHT;
			this.addPoint();
		} else if (this.id == 2) {
			this.x = Screen.width - 5;
			this.y = 400;
			this.direction = Direction.LEFT;
			this.addPoint();
		}
	}
	
	public boolean isDead() {
		return this.isDead;
	}
}