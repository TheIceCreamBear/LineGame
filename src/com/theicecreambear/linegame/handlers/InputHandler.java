package com.theicecreambear.linegame.handlers;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author David Santamaria
 * @author Joseph Terribile
 * @version 0.3.1
 */
public class InputHandler {

	private boolean[] keys = new boolean[256];
	private boolean[] mouse = new boolean[4];
	private MouseEvent[] mEvents = new MouseEvent[4];
	private Component c;
	private KeyListener kL;
	private MouseListener mL;

	public InputHandler(Component c) {
		this.c = c;
		
		this.kL = new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				keys[e.getKeyCode()] = true;
			}

			public void keyReleased(KeyEvent e) {
				keys[e.getKeyCode()] = false;
			}
		};
		this.mL = new MouseListener() {

			public void mouseClicked(MouseEvent e) {

			}

			public void mousePressed(MouseEvent e) {
				mouse[e.getButton()] = true;
				mEvents[e.getButton()] = e;
			}

			public void mouseReleased(MouseEvent e) {
				mouse[e.getButton()] = false;
				mEvents[e.getButton()] = e;
			}

			public void mouseEntered(MouseEvent e) {

			}

			public void mouseExited(MouseEvent e) {

			}
		};
		c.addKeyListener(this.kL);
		c.addMouseListener(this.mL);
	}

	public synchronized boolean isKeyDown(int keyCode) {
		if (keyCode > 0 && keyCode < keys.length) {
			return keys[keyCode];
		}

		return false;
	}

	public boolean isMouseDown(int button) {
		if (button > 0 && button <= 3) {
			return mouse[button];
		}
		return false;
	}

	public MouseEvent getEvent(int event) {
		if (event > 0 && event <= 3) {
			return mEvents[event];
		}

		return null;
	}
	
	public synchronized boolean isMoveKeyDown() {
		// right, left, up, down
		if (this.isKeyDown(KeyEvent.VK_RIGHT) || this.isKeyDown(KeyEvent.VK_D)) {
			return true;
		} else if (this.isKeyDown(KeyEvent.VK_LEFT) || this.isKeyDown(KeyEvent.VK_A)) {
			return true;
		} else if (this.isKeyDown(KeyEvent.VK_UP) || this.isKeyDown(KeyEvent.VK_W)) {
			return true;
		} else if (this.isKeyDown(KeyEvent.VK_DOWN) || this.isKeyDown(KeyEvent.VK_S)) {
			return true;
		}
		return false;
	}
	
	public void deInit() {
		this.c.removeKeyListener(kL);
		this.c.removeMouseListener(mL);
	}
}
