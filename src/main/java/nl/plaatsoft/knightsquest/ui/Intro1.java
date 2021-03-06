package nl.plaatsoft.knightsquest.ui;

import javafx.animation.AnimationTimer;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import nl.plaatsoft.knightsquest.network.CloudCheck;
import nl.plaatsoft.knightsquest.network.CloudProduct;
import nl.plaatsoft.knightsquest.network.CloudScore;
import nl.plaatsoft.knightsquest.network.CloudUser;
import nl.plaatsoft.knightsquest.tools.MyFactory;
import nl.plaatsoft.knightsquest.tools.MyImageView;
import nl.plaatsoft.knightsquest.tools.MyLabel;
import nl.plaatsoft.knightsquest.tools.MyPanel;

/**
 * The Class Intro1.
 * 
 * @author wplaat
 */
public class Intro1 extends MyPanel {

	/** The image view 1. */
	private MyImageView imageView1;
	
	/**
	 * Draw.
	 */
	public void draw() {		
				
		Image image1 = new Image("images/background1.jpg");
		BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
		BackgroundImage backgroundImage = new BackgroundImage(image1, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
		setBackground( new Background(backgroundImage));
		
		getChildren().add( new MyLabel(0,30,"Created by PlaatSoft",26));
		getChildren().add( new MyLabel(0,70,"www.plaatsoft.nl",26));
				
		imageView1 = new MyImageView((MyFactory.getSettingDAO().getSettings().getWidth()/2)-(376/2), (MyFactory.getSettingDAO().getSettings().getHeight()/2)-(80/2), "images/logo1.png",1);		
		getChildren().add(imageView1);
		getChildren().add( new MyLabel(0,(MyFactory.getSettingDAO().getSettings().getHeight()-70),"This software is open source and may be copied, distributed or modified",16));
		getChildren().add( new MyLabel(0,(MyFactory.getSettingDAO().getSettings().getHeight()-50),"under the terms of the GNU General Public License (GPL) version 3",16));
		
		setOnMousePressed(new EventHandler<MouseEvent>() {
	        public void handle(MouseEvent t) {
	        	Navigator.go(Navigator.INTRO2);			
	        }
	    });		
		
		AnimationTimer timer = new AnimationTimer() {			 
			float size = (float) 0.025;
			 	
			@Override
			public void handle(long now) {
	            		
				size+=0.02;
				if (size>=1.3) {
					size=(float) 1.3;
				}
				imageView1.setScaleX(size);
				imageView1.setScaleY(size);
			}
		};
	    
		Task<Void> task = new Task<Void>() {
	        public Void call() {
	        		        	
	        	if (CloudCheck.isReachableByTCP("service.plaatsoft.nl")) {	        	
	        		CloudProduct.getPid(); 
	        		CloudUser.getUid();
	        		CloudScore.getLocal();
	        	}
	        	return null;
	        }
		};
		
		timer.start();
		new Thread(task).start();
	}
}
