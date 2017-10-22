package com.minz.bird;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemToggle;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.transitions.CCFadeTransition;
import org.cocos2d.types.CGPoint;

import android.content.SharedPreferences;
import android.view.KeyEvent;

public class MenuLayer extends CCLayer {

	CCSprite _bgAlert;
	CCMenuItemImage yes, no;

	public MenuLayer(boolean playMusic)
	{
		if (playMusic)
		{
			if (G.bgSound.isPlaying()) G.bgSound.pause();
			G.bgSound = G.soundMenu;
			if (G.music) G.bgSound.start();
		}
			
		setScale(G.scale);
		setAnchorPoint(0, 0);
		
		// background
		CCSprite bg = new CCSprite("menu/menu_bg.png");
		bg.setPosition(G.displayCenter());
		addChild(bg);
		
		CCSprite bird = new CCSprite("menu/bird.png");
		bird.setAnchorPoint(0, 0);
		bird.setPosition(0, 0);
		addChild(bird);

		CCSprite leaves = new CCSprite("menu/leaves.png");
		leaves.setAnchorPoint(0, 1);
		leaves.setPosition(0, G.height);
		addChild(leaves);

		CCSprite title = new CCSprite("menu/title.png");
		title.setPosition(G.width*0.47f, G.height*0.73f);
		addChild(title);

		// buttons
		CCMenuItemImage play = CCMenuItemImage.item("menu/play1.png", "menu/play2.png", this, "onPlay");
		play.setAnchorPoint(1, 0);
		play.setPosition(G.width, 0);
		
		CCMenuItemToggle sound = CCMenuItemToggle.item(this, "onSound",
			CCMenuItemImage.item("menu/sound_off.png", "menu/sound_off.png"),
			CCMenuItemImage.item("menu/sound_on.png", "menu/sound_on.png"));
		sound.setSelectedIndex(G.sound?1:0);
		sound.setPosition(G.width*0.4f, 70);

		CCMenuItemToggle music = CCMenuItemToggle.item(this, "onMusic",
			CCMenuItemImage.item("menu/music_off.png", "menu/music_off.png"),
			CCMenuItemImage.item("menu/music_on.png", "menu/music_on.png"));
		music.setSelectedIndex(G.music?1:0);
		music.setPosition(G.width*0.5f, 70);
		CCMenuItemImage help = CCMenuItemImage.item("menu/help1.png", "menu/help2.png", this, "onHelp");
		help.setPosition(G.width*0.6f, 70);

		CCMenu menu = CCMenu.menu(play, sound, music, help);
		menu.setPosition(0, 0);
		addChild(menu);
		
		_bgAlert = new CCSprite("game/bg.png");
		_bgAlert.setPosition(G.width*0.5f, -G.height*0.5f);
		_bgAlert.setScale(2.1f);
		addChild(_bgAlert);
		
		yes = CCMenuItemImage.item("game/yes.png", "game/yes.png", this, "onClickYes");
		yes.setPosition(G.width*0.35f, -G.height*0.4f);		
		no = CCMenuItemImage.item("game/no.png", "game/no.png", this, "onClickNo");
		no.setPosition(G.width*0.65f, -G.height*0.4f);
		
		yes.setScale(0.9f);
		no.setScale(0.9f);

		CCMenu menu1 = CCMenu.menu(yes, no);
		menu1.setPosition(0, 0);		
		addChild(menu1);

		
		setIsKeyEnabled(true);
	}
	
	public void onPlay(Object sender)
	{
		if( G.sound ) G.soundClick.start();
		
		CCScene s = CCScene.node();
		s.addChild(new SelectLayer(false));
		CCDirector.sharedDirector().replaceScene(CCFadeTransition.transition(0.7f, s));
	}

	public void onSound(Object sender)
	{
		if( G.sound ) G.soundClick.start();

		G.sound = !G.sound;
		SharedPreferences sp = CCDirector.sharedDirector().getActivity().getSharedPreferences("GameInfo", 0);
		SharedPreferences.Editor  et = sp.edit();
		et.putBoolean("sound", G.sound);
		et.commit();
	}
	
	void showMenu() {
		CGPoint p = _bgAlert.getPosition();
		_bgAlert.setPosition( p.x, -p.y);
		
		p = yes.getPosition();
		yes.setPosition(p.x, -p.y);
		
		p = no.getPosition();
		no.setPosition(p.x, -p.y);

	}
	
	public void onClickYes(Object sender)
	{
		if( G.isExit == false ) {
			G.isExit = true;
			return;
		}

		showMenu();
		
		int pid = android.os.Process.myPid();
		android.os.Process.killProcess(pid);
		Runtime r = Runtime.getRuntime();
		r.gc();
		System.gc();		
	}
	
	public void onClickNo(Object sender)
	{
		showMenu();		
	}

	public void onMusic(Object sender)
	{
		if( G.sound ) G.soundClick.start();
		
		G.music = !G.music;
		SharedPreferences sp = CCDirector.sharedDirector().getActivity().getSharedPreferences("GameInfo", 0);
		SharedPreferences.Editor  et = sp.edit();
		et.putBoolean("music", G.music);
		et.commit();

		if (G.music)
		{
			G.bgSound.start();
		}
		else
		{
			G.bgSound.pause();
		}
	}

	public void onHelp(Object sender)
	{
		if( G.sound ) G.soundClick.start();

		CCScene s = CCScene.node();
		s.addChild(new HelpLayer());
		CCDirector.sharedDirector().replaceScene(CCFadeTransition.transition(0.7f, s));
	}


	public boolean ccKeyDown(int keyCode, KeyEvent event)
	{
		G.isExit = false;
		showMenu();
		return true;
	}	
}
