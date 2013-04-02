package com.frenberg.tid;

import java.net.URL;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import net.sf.jcarrierpigeon.WindowPosition;
import net.sf.jtelegraph.Telegraph;
import net.sf.jtelegraph.TelegraphQueue;
import net.sf.jtelegraph.TelegraphType;

public class NotificationTimerTask extends TimerTask {

	@Override
	public void run() {
        Telegraph telegraph = new Telegraph(
				"Tid:", 
				"Nu har du jobbat dina timmar och kan med gott samvete stämpla ut.", 
				TelegraphType.CALENDAR, 
				WindowPosition.TOPRIGHT, 
				10000);
		new TelegraphQueue().add(telegraph);
		try{
			URL file = getClass().getClassLoader().getResource("alert.wav");
            Clip clip = AudioSystem.getClip();
            AudioInputStream ais = AudioSystem.getAudioInputStream( file );
            clip.open(ais);
            clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
