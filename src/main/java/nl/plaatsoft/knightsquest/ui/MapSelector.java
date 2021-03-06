package nl.plaatsoft.knightsquest.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

import nl.plaatsoft.knightsquest.tools.MyButton;
import nl.plaatsoft.knightsquest.tools.MyData;
import nl.plaatsoft.knightsquest.tools.MyFactory;
import nl.plaatsoft.knightsquest.tools.MyImageView;
import nl.plaatsoft.knightsquest.tools.MyLabel;
import nl.plaatsoft.knightsquest.tools.MyPanel;
import nl.plaatsoft.knightsquest.tools.MySound;

/**
 * The Class MapSelector.
 * 
 * @author wplaat
 */
public class MapSelector extends MyPanel {

	/** The Constant log. */
	private static final Logger log = LogManager.getLogger(MapSelector.class);
	
	/** The level. */
	private int level = 0;			
	
	/** The label 1. */
	private MyLabel[] label1 = new MyLabel[6];
	
	/** The label 2. */
	private MyLabel[] label2 = new MyLabel[6];	
	
	/** The image. */
	private MyImageView[] image = new MyImageView[6];	
	
	/** The gc. */
	private GraphicsContext[] gc = new GraphicsContext[6];
		
	/**
	 * Creates the map.
	 *
	 * @param gc the gc
	 * @param map the map
	 */
	private void createMap(GraphicsContext gc, int map) {
				
		int size=3;		
		if (MyFactory.getSettingDAO().getSettings().getWidth()==800) {			
			size=4;			
		} else if (MyFactory.getSettingDAO().getSettings().getWidth()==1024) {			
			size=5;
		}
		
		MyData.setLevel(level);
		MyData.setMap(map);
		
		MyFactory.getLandDAO().createMap(gc, size, level+1);
		MyFactory.getLandDAO().draw();
		
		gc.getCanvas().setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				
				if (MyFactory.getSettingDAO().getSettings().getMapUnlocked(map)) 
				{
					MyData.setLevel(level);
					MyData.setMap(map);
					MyData.setMode(MyData.MODE_1P);
					Navigator.go(Navigator.GAME);
					
					MySound.play(MyFactory.getPlayerDAO().getHumanPlayer(), MySound.CLIP_START);			
				}
			}
		});
	}
	
		
	/**
	 * Creates the maps.
	 *
	 * @param page the page
	 */
	private void createMaps(int page) {
		for (int i=0; i<6; i++) {
			
			int map = (level*10)+i+1;
			createMap(gc[i], map);	
			label1[i].setText(""+map);
						
			if (MyFactory.getSettingDAO().getSettings().getMapUnlocked(map)) 
			{
				label2[i].setText("Score "+MyFactory.getSettingDAO().getSettings().getScore(map));
				label2[i].setVisible(true);
				image[i].setVisible(false);				
			} 
			else {
				image[i].setVisible(true);
				label2[i].setVisible(false);	
			}
		}
	}

	/**
	 * Creates the canvas.
	 *
	 * @param id the id
	 * @param x the x
	 * @param y the y
	 * @param size the size
	 */
	private void createCanvas(int id, int x, int y, int size) {

		Canvas canvas = new Canvas((Constants.SEGMENT_X * size * 4),(Constants.SEGMENT_Y * size));
		canvas.setLayoutX(x);
		canvas.setLayoutY(y);
		
		int height = Constants.SEGMENT_Y * size;

		gc[id] = canvas.getGraphicsContext2D();
		getChildren().add(canvas);
		
		label1[id] = new MyLabel(x+10,y+5,"", 36);
		getChildren().add(label1[id]);		
		
		label2[id] = new MyLabel(x+10,y+height-30,"", 20);
		getChildren().add(label2[id]);		
		
		image[id] = new MyImageView(x+30 ,y+15, canvas.getWidth()-30, canvas.getHeight()-30, "images/unlock.png");
		getChildren().add(image[id]);	
	}

	/**
	 * Draw canvas.
	 */
	private void drawCanvas() {
				
		int size=3;
		int offsetX = 200;
		int offsetY = 150;
		int startY = 100;
		
		if (MyFactory.getSettingDAO().getSettings().getWidth()==800) {
			
			size=4;
			startY = 120;
			offsetX = 250;
			offsetY = 200;
			
		} else if (MyFactory.getSettingDAO().getSettings().getWidth()==1024) {
			
			size=5;
			startY = 140;
			offsetX = 325;
			offsetY = 250;	
		}
		
		int y = startY;		
		int x = 30;		
		
		createCanvas(0, x, y, size);
		
		x += offsetX;
		createCanvas(1, x, y, size);
				
		x += offsetX;
		createCanvas(2, x, y, size);
		
		y += offsetY+10;

		x = 30;
		createCanvas(3, x, y, size);
		
		x += offsetX;
		createCanvas(4, x, y, size);
		
		x += offsetX;
		createCanvas(5, x, y, size);		
	}
	
	/**
	 * Inits the.
	 */
	public void init() {
				
		level = (MyFactory.getSettingDAO().getSettings().getHighestMap()/10);	
		
		Image image1 = new Image("images/background4.jpg");
		BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
		BackgroundImage backgroundImage = new BackgroundImage(image1, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
		setBackground(new Background(backgroundImage));

		getChildren().add(new MyLabel(0, 20, "Map Selector", 50, "white", "-fx-font-weight: bold;"));
		drawCanvas();
		createMaps(level);
				
		MyButton close = new MyButton(0, MyFactory.getSettingDAO().getSettings().getHeight()-60, "Close", 18, Navigator.MODE_SELECTOR);
		getChildren().add(close);
		
		MyButton prev= new MyButton(close.getLayoutX()-70, close.getLayoutY(), "<", 18, Navigator.NONE);
		prev.setPrefWidth(50);
		prev.setOnAction(new EventHandler<ActionEvent>() { 
			public void handle(ActionEvent event) {
				if (level>0) {
					level--;
				}
				createMaps(level);
			}
		});		
		getChildren().add(prev);
		
		MyButton next= new MyButton(close.getLayoutX()+200, close.getLayoutY(), ">", 18, Navigator.NONE);
		next.setPrefWidth(50);
		next.setOnAction(new EventHandler<ActionEvent>() { 
			public void handle(ActionEvent event) {
				if (level<(Constants.MAX_LEVELS-1)) {
					level++;
				}
				createMaps(level);
			}
		});
		getChildren().add(next);
	}

	/**
	 * Draw.
	 */
	@Override
	public void draw() {	
	}
}
