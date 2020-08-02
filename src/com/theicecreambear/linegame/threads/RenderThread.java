package com.theicecreambear.linegame.threads;

import com.theicecreambear.linegame.engine.GameEngine;
import com.theicecreambear.linegame.gameobject.RenderLockObject;

public class RenderThread extends Thread {
	public RenderLockObject rlo;
	private GameEngine gEngine;
	public RenderThread(String name, RenderLockObject rlo, GameEngine ge) {
		super(name);
		this.rlo = rlo;
		this.gEngine = ge;
	}
	
	@Override
	public void run() {
		synchronized (rlo) {
			while (GameEngine.running) {
				try {
					rlo.wait();
					if (!rlo.wasNotified) {
						continue;
					} else {
						gEngine.render(gEngine.g, gEngine.frame);
						rlo.wasNotified = false;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}
	}
}