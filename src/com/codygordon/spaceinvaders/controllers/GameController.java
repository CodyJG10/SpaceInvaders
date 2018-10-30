package com.codygordon.spaceinvaders.controllers;

import java.awt.Point;
import java.util.ArrayList;

import com.codygordon.spaceinvaders.enemies.EnemyWave;
import com.codygordon.spaceinvaders.game.GameContainer;
import com.codygordon.spaceinvaders.gameobjects.GameObject;
import com.codygordon.spaceinvaders.gameobjects.barriers.Barrier;
import com.codygordon.spaceinvaders.gameobjects.player.Player;
import com.codygordon.spaceinvaders.input.FrameKeyListener;
import com.codygordon.spaceinvaders.input.GameKeyListener;
import com.codygordon.spaceinvaders.ui.screens.GameScreen;

public class GameController {
	
	private Player player;
	private GameScreen view;
	private GameKeyListener listener;
	
	private ArrayList<GameObject> physicsObjects = new ArrayList<GameObject>();
	private ArrayList<GameObject> objectsToDestroy = new ArrayList<GameObject>();	
	
	private EnemyWave currentWave;
	private EnemyWave enemyWavePreset;
	
	private int currentScore = 0;
	private int currentLives = 3;
	
	public GameController(GameScreen screen) {
		this.view = screen;				
		listener = new GameKeyListener();
		FrameKeyListener.getInstance().addListener(listener);
	}
	
	public void spawnNextWave() {
		currentWave.stop();
		EnemyWave newEnemyWave = enemyWavePreset.clone();
		spawnEnemyWave(newEnemyWave);
	}
	
	public void createPlayer(Player player) {
		this.player = player;
		initGameObject(player);
	}
	
	public void createBarrier(Barrier barrier) {
		initGameObject(barrier);
	}
	
	public void createEnemyWave(EnemyWave wave) {
		enemyWavePreset = wave.clone();
		spawnEnemyWave(wave);
	}
	
	private void spawnEnemyWave(EnemyWave wave) {
		this.currentWave = wave;
		this.currentWave.init();
	}
	
	public void update() {
		updatePhysics();
		destroyGameObjects();
	}
	
	public synchronized void initGameObject(GameObject obj) {
		physicsObjects.add(obj);
		view.getObjectsToDraw().add(obj);
	}
	
	public void destroyGameObject(GameObject obj) {
		objectsToDestroy.add(obj);
	}
	
	private void destroyGameObjects() {
		physicsObjects.removeAll(objectsToDestroy);
		view.getObjectsToDraw().removeAll(objectsToDestroy);
		objectsToDestroy.clear();
	}
	
	private void updatePhysics() {
		try {
			for(GameObject obj : physicsObjects) {
				for(GameObject otherObj : physicsObjects) {
					if(!otherObj.equals(obj) && !otherObj.getClass().getName().equals(obj.getClass().getName())) { 
						if(obj.getCollider().intersects(otherObj.getCollider())) {
							otherObj.onCollision(obj);
						}
					}
				}
			}
		} catch(Exception e) { }
	}
	
	public void killPlayer() {
		currentLives--;
		view.updateLives();
		if(currentLives > 0) {
			player.isAlive = false;
			respawnPlayer();
		} else {
			endGame();
		}
	}
	
	private void endGame() {
		//TODO
		System.out.println("Game Over!");
	}
	
	private void respawnPlayer() {
		int x = GameContainer.getInstance().getMainFrame().getWidth() / 2 - player.getWidth() / 2;
		int y = GameContainer.getInstance().getMainFrame().getHeight() - player.getHeight() * 2;
		player.setLocation(new Point(x, y));
		try {
			 Thread.sleep(3000);
			 player.isAlive = true;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public EnemyWave getCurrentEnemyWave() {
		return this.currentWave;
	}
	
	public int getScore() {
		return this.currentScore;
	}
	
	public void addToScore(int value) {
		currentScore += value;
		view.updateScore();
	}
	
	public int getCurrentLives() {
		return this.currentLives;
	}
}