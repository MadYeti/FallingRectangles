package application;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Figure implements Runnable{

	private int y;
	private int x;
	private Rectangle rectangle;
	private Timeline timeline;
	
	public Figure() {
		this.y = 0;
		this.x = (int) (Math.random() * 650);
		this.rectangle = new Rectangle(x, y, 50, 50);
		rectangle.setFill(Color.BLUE);
		this.timeline = new Timeline();
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		
		KeyValue yValue = new KeyValue(rectangle.yProperty(), 600);
		KeyFrame frame = new KeyFrame(Duration.millis(2000), yValue);
		timeline.getKeyFrames().add(frame);
		timeline.play();
		
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	public Timeline getTimeline() {
		return timeline;
	}
	
}
