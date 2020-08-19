package game2048;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.canvas.GraphicsContext;

class Position {
	
	public int x = 0;
	public int y = 0;
	
	public Position (int x, int y) {
		
		this.x = x;
		this.y = y;
		
	}
	
}

class Tile {
	
	long value;
	Position prevPos;
	
	boolean merged;
	
	public Tile(long value) {
		
		this.value = value;
		prevPos = new Position(-1, -1);
		merged = false;
		
	}
	
	public Tile(long value, Position prevPos, boolean merged) {
		
		this.value = value;
		this.prevPos = prevPos;
		this.merged = merged;
		
	}
	
}

public class Game extends Application {
	
	final Random random = new Random();
	
	// GAME SETTINGS
	final static int WIDTH = 6;
	final static int HEIGHT = 7;
	
	final static int CELL_SIZE = 70;
	final static int CELL_ROUND = 4;
	final static int PADDING = 5;
	
	final static int START_TILES = 1;
	final static int GROW_TILES = 1;
	
	final static float DOUBLE_THRESHOLD = 0.9f;
	final static boolean CANT_PLACE_DEATH = false;
	
	final static double SPEED = 40;
	final static double GROW_SPEED = 10;
	final static double GROW_DELAY = 0.2;
	
	// STAGE
	final static int width = WIDTH * CELL_SIZE;
	final static int height = HEIGHT * CELL_SIZE;
	
	Stage primaryStage = new Stage();
	GraphicsContext gc;
	
	Tile[][] board = new Tile[WIDTH][HEIGHT];
	
	boolean lose = false;
	boolean cheating = false;
	
	static long[] values;
	final static Color[] colors = {
		Color.rgb(196, 173, 125),
		Color.rgb(196, 155, 68),
		Color.rgb(214, 173, 70),
		Color.rgb(229, 131, 50),
		Color.rgb(255, 115, 0),
		Color.rgb(224, 78, 11),
		Color.rgb(255, 221, 56),
		Color.rgb(58, 255, 61), //256
		Color.rgb(38, 193, 41), 
		Color.rgb(25, 170, 103),
		Color.rgb(24, 169, 155),
		Color.rgb(23, 144, 168),
		Color.rgb(22, 97, 168),
		Color.rgb(21, 39, 168),
		Color.rgb(96, 21, 168),
		Color.rgb(168, 21, 150),
		Color.rgb(72, 32, 84),
		Color.rgb(46, 38, 48),
		Color.rgb(32, 30, 33),
	};
	double time = 0;
	
	long score = 0;
	
	void checkLose() {
		
		boolean canMove = false;
		
		loop:
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				
				if (board[i][j].value == 0) {
					canMove = true;
				}
					
				
				if (i > 0) {
					if (board[i][j].value == board[i-1][j].value) {
						canMove = true;
					}
				}
				if (i < WIDTH - 1) {
					if (board[i][j].value == board[i+1][j].value) {
						canMove = true;
					}
				}
				
				if (j > 0) {
					if (board[i][j].value == board[i][j-1].value) {
						canMove = true;
					}
				}
				if (j < HEIGHT - 1) {
					if (board[i][j].value == board[i][j+1].value) {
						canMove = true;
					}
				}
				
				if (canMove)
					break loop;
			}
		}
		
		if (!canMove) {
			lose = true;
		}
		
	}
	
	void addScore(int value) {
		if (value <= 0)
			return;
		score += values[value-1];
	}
	
	void moveRight() {
		
		if (lose)
			return;

		boolean moved = false;
		//HashSet<String> positions = new HashSet<String>();
		
		for (int j = 0; j < HEIGHT; j++) {
			for (int i = WIDTH - 1; i >= 0; i--) {
				
				if (board[i][j].value > 0) {
					
					board[i][j].prevPos = new Position(i,j);
					
					if (i < WIDTH - 1) {
						int insert = -1;
						for (int c = i + 1; c < WIDTH; c++) {
							
							if (board[c][j].merged)
								break;
							
							long value = board[c][j].value;
							if (value == board[i][j].value) {
								insert = c;
								break;
							}
							else if (value > 0) {
								break;
							}
							
						}
						
						if (insert >= 0) {
							board[i][j].value++;
							board[insert][j].merged = true;
							addScore((int)board[i][j].value);
							board[insert][j].value = 0;
							moved = true;
						}
						
					}
					
					for (int x = WIDTH - 1; x > i; x--) {
						if (board[x][j].value == 0) {
							
							board[x][j].prevPos = new Position(i,j);
							board[x][j].value = board[i][j].value;
							board[i][j].value = 0;
							
							moved = true;
							
							break;
							
						}
					}
				
				}
				
			}
		}
		
		if (moved) 
			makeMove();
		
	}
	
	void moveLeft() {
		
		if (lose)
			return;

		boolean moved = false;
		
		for (int j = 0; j < HEIGHT; j++) {
			for (int i = 0; i < WIDTH; i++) {
				
				if (board[i][j].value > 0) {
					
					board[i][j].prevPos = new Position(i,j);
					
					if (i > 0) {
						int insert = -1;
						for (int c = i - 1; c >= 0; c--) {
							
							if (board[c][j].merged)
								break;
							
							long value = board[c][j].value;
							if (value == board[i][j].value) {
								insert = c;
								break;
							}
							else if (value > 0) {
								break;
							}
							
						}
						
						if (insert >= 0) {
							board[i][j].value++;
							board[insert][j].merged = true;
							addScore((int)board[i][j].value);
							board[insert][j].value = 0;
							moved = true;
						}
						
					}
					
					for (int x = 0; x < i; x++) {
						if (board[x][j].value == 0) {
							
							board[x][j].prevPos = new Position(i,j);
							board[x][j].value = board[i][j].value;
							board[i][j].value = 0;
							
							moved = true;
							
							break;
							
						}
					}
				
				}
				
			}
		}
		
		if (moved) 
			makeMove();
		
	}
	
	
	
	
	
	
	
	
	void moveDown() {
		
		if (lose)
			return;

		boolean moved = false;
		
		for (int i = 0; i < WIDTH; i++) {
			for (int j = HEIGHT - 1; j >= 0; j--) {
				
				if (board[i][j].value > 0) {
					
					board[i][j].prevPos = new Position(i,j);
					
					if (j < HEIGHT - 1) {
						int insert = -1;
						for (int c = j + 1; c < HEIGHT; c++) {
							
							if (board[i][c].merged)
								break;
							
							long value = board[i][c].value;
							if (value == board[i][j].value) {
								insert = c;
								break;
							}
							else if (value > 0) {
								break;
							}
							
						}
						
						if (insert >= 0) {
							board[i][j].value++;
							board[i][insert].merged = true;
							addScore((int)board[i][j].value);
							board[i][insert].value = 0;
							moved = true;
						}
						
					}
					
					for (int y = HEIGHT - 1; y > j; y--) {
						if (board[i][y].value == 0) {
							
							board[i][y].prevPos = new Position(i,j);
							board[i][y].value = board[i][j].value;
							board[i][j].value = 0;
							
							moved = true;
							
							break;
							
						}
					}
				
				}
				
			}
		}
		
		if (moved) 
			makeMove();
		
	}
	
	void moveUp() {
		
		if (lose)
			return;

		boolean moved = false;
		
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				
				if (board[i][j].value > 0) {
					
					board[i][j].prevPos = new Position(i,j);
					
					if (j > 0) {
						int insert = -1;
						for (int c = j - 1; c >= 0; c--) {
							
							if (board[i][c].merged)
								break;
							
							long value = board[i][c].value;
							if (value == board[i][j].value) {
								insert = c;
								break;
							}
							else if (value > 0) {
								break;
							}
							
						}
						
						if (insert >= 0) {
							board[i][j].value++;
							board[i][insert].merged = true;
							addScore((int)board[i][j].value);
							board[i][insert].value = 0;
							moved = true;
						}
						
					}
					
					for (int y = 0; y < j; y++) {
						if (board[i][y].value == 0) {
							
							board[i][y].prevPos = new Position(i,j);
							board[i][y].value = board[i][j].value;
							board[i][j].value = 0;
							
							moved = true;
							
							break;
							
						}
					}
				
				}
				
			}
		}
		
		if (moved) 
			makeMove();
		
	}
	
	String string(long in) {
		return in + "";//Long.toBinaryString(in);
	}
	
	void makeMove() {
		
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				board[i][j].merged = false;
			}
		}
		
		for (int i = 0; i < GROW_TILES; i++)
			placeRandom();
		
		time = 0;
		
		checkLose();
		
	}

	@Override
	public void start(Stage stage) {
		
		Group root = new Group();
	    	Scene s = new Scene(root, width, height + CELL_SIZE / 2, Color.BLACK);
	
	    	final Canvas canvas = new Canvas(width, height + CELL_SIZE / 2);
	    	gc = canvas.getGraphicsContext2D();
	    	 
	    	root.getChildren().add(canvas);
	    
	    	primaryStage.setTitle("2048");
	    	primaryStage.setScene(s);
	    	primaryStage.setResizable(false);
	    	primaryStage.initStyle(StageStyle.UNDECORATED);
	    	primaryStage.show();
	    	
	    	canvas.setFocusTraversable(true);
	    	
	    	canvas.setOnKeyPressed(e -> {
	    		switch (e.getCode()) {
	    		
	    			case SPACE:
	    				if (lose) {
		    				System.out.println(score + "");
		    				startGame();
	    				}
	    				break;
	    			case DOWN:
	    			case S:
	    				moveDown();
	    				break;
	    			case UP:
	    			case W:
	    				moveUp();
	    				break;
	    			case RIGHT:
	    			case D:
	    				moveRight();
	    				break;
	    			case LEFT:
	    			case A:
	    				moveLeft();
	    				break;
	    			case H:
	    				cheating = true;
	    				break;
	    			
	    			default:
	    				break;
	    				
	    		}
	    	});
	    	
	    	canvas.setOnKeyReleased(e -> {
	    		switch (e.getCode()) {
	    			case H:
	    				cheating = false;
	    				break;
	    			default:
	    				break;
	    		}
	    	});
	    
	    	primaryStage.setOnCloseRequest(e -> {
		    	
		    	System.exit(0);
		    	
	    	});
	    	
	    	AnimationTimer timer = new AnimationTimer() {
	    		
	
	        	final LongProperty lastUpdateTime = new SimpleLongProperty(0);
	    		
	    	    @Override
	    	    public void handle(long dt) {
	    	    	
	    	        if (lastUpdateTime.get() > 0) {
	    	        	
	    	            double deltaTime = (dt - lastUpdateTime.get()) / 1000000000.0;
	    	            
	    	            time += deltaTime;
	    	            
	    	            draw();
	    	            
	    	        }
	    	        
	    	        /*Random random = new Random();
	    	        // GAY-I
	    	        if (!lose)for (int i = 0; i < 50000; i++) {
	    	        		switch(random.nextInt(4)) {
	    	        		case 0:
			    	        moveLeft();
			    	        break;
	    	        		case 1:
			    	        moveUp();
			    	        break;
	    	        		case 2:
			    	        moveRight();
			    	        break;
	    	        		case 3:
			    	        moveDown();
			    	        break;
	    	        		}
		    	        if (lose) {
		    	        		break;
		    	        }
	    	        }
	    	        
	    	        if (lose) {
	    	        		System.out.println("" + score);
	    	        		startGame();
	    	        }
	    	        */
	    	       
	    	        if (cheating) for (int i = 0; i < 10; i++){
//		    	        moveLeft();
//		    	        moveRight();
//		    	        moveUp();
		    	        moveDown();
		    	        if (lose)
		    	        		break;
	    	        }
	    	        
	    	        lastUpdateTime.set(dt);
	    	        
	    	    }
	    	    
	    	};
	    	
	    	timer.start();
	    	
	    	startGame();
	    	
	    	long[] v = new long[WIDTH*HEIGHT];
		v[0] = 2;
		for (int i = 1; i < WIDTH * HEIGHT; i++) {
			v[i] = v[i-1] * 2;
		}
		
		values = v;
		
	}
	
	void startGame() {
		
		score = 0;
		board = new Tile[WIDTH][HEIGHT];
		
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				
				board[i][j] = new Tile(0);
				
			}
		}
		
		lose = false;
		
		for (int i = 0; i < START_TILES; i++) {
			placeRandom();
		}
		
		time = 0;
		
		draw();
		
	}
	
	public void placeRandom() {
		
		if (lose)
			return;
		
		ArrayList<Position> positions = new ArrayList<Position>();
		
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				
				if (board[i][j].value == 0) {
					positions.add(new Position(i, j));
				}
				
			}
		}
		
		if (positions.size() > 0) {
			
			int randomPos = random.nextInt(positions.size());
			
			Position position = positions.get(randomPos);
			board[position.x][position.y].value = (byte)(random.nextFloat() < DOUBLE_THRESHOLD ? 1 : 2);
			board[position.x][position.y].prevPos = new Position(-1,-1);
			
		}
		else {
			if (CANT_PLACE_DEATH)
				lose = true;
		}
		
	}
	
	double getPositionX(int x, int y) {
		
		double pos = 0;
		int prevX = board[x][y].prevPos.x;
		
		if (x == prevX || prevX < 0)
			return x;
		
		if (prevX < x) {
			pos = prevX + SPEED * time;
			if (pos > x) {
				return x;
			}
			else {
				return pos;
			}
		}
		else {
			pos = prevX - SPEED * time;
			if (pos < x) {
				return x;
			}
			else {
				return pos;
			}
		}
		
	}
	
	double getPositionY(int x, int y) {
		
		double pos = 0;
		int prevY = board[x][y].prevPos.y;
		
		if (y == prevY || prevY < 0) 
			return y;
		
		if (prevY < y) {
			pos = prevY + SPEED * time;
			if (pos > y) {
				return y;
			}
			else {
				return pos;
			}
		}
		else {
			pos = prevY - SPEED * time;
			if (pos < y) {
				return y;
			}
			else {
				return pos;
			}
		}
		
	}
	
	double getSize() {
		if (time >= GROW_DELAY) {
			double size = (time - GROW_DELAY) * GROW_SPEED;
			return Math.min(1, size);
		}
		return 0;
	}
	
	public void draw() {

		gc.clearRect(0, 0, width, height + CELL_SIZE);
		
		gc.setFill(Color.rgb(125, 125, 125));
		gc.fillRect(0, 0, width, height);
		
		gc.setFill(Color.WHITE);
		gc.setTextAlign(TextAlignment.LEFT);
		gc.setTextBaseline(VPos.BOTTOM);
		gc.setFont(new Font("Helvetica", CELL_SIZE / 2.2));
		if (lose) {
			gc.setFont(new Font("Helvetica", CELL_SIZE / 4));
			gc.fillText("Game Over! SPACE = RESTART", 4, height + CELL_SIZE * 0.275);
		}
		gc.fillText("Score: " + score, 4, height + CELL_SIZE * 0.525);
		
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				
				gc.setFill(Color.rgb(200,200,200));
				gc.fillRoundRect(i * CELL_SIZE + PADDING / 2f, j * CELL_SIZE + PADDING / 2f, CELL_SIZE - PADDING, CELL_SIZE - PADDING, CELL_ROUND, CELL_ROUND);
			
			}
		}
		
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				
				if (board[i][j].value > 0) {
					
					gc.setFill(colors[Math.min((int)board[i][j].value-1,colors.length-1)]);
					double pX = i;
					double pY = j;
					if (board[i][j].prevPos.x < 0) {
						
						double size = getSize();
						gc.fillRoundRect(i * CELL_SIZE + PADDING / 2f, j * CELL_SIZE + PADDING / 2f, (CELL_SIZE - PADDING) * size, (CELL_SIZE - PADDING) * size, CELL_ROUND, CELL_ROUND);
						
					}
					else {
						
						double x = getPositionX(i,j);
						double y = getPositionY(i,j);
						pX = x;
						pY = y;
						gc.fillRoundRect(x * CELL_SIZE + PADDING / 2f, y * CELL_SIZE + PADDING / 2f, CELL_SIZE - PADDING, CELL_SIZE - PADDING, CELL_ROUND, CELL_ROUND);
					
					}
					
					if (board[i][j].prevPos.x < 0 && getSize() < 0.5)
						continue;
					gc.setFill(Color.WHITE);
					gc.setTextAlign(TextAlignment.CENTER);
					gc.setTextBaseline(VPos.CENTER);
					gc.setFont(Font.font("Arial", FontWeight.BOLD, CELL_SIZE / 4));
					
					gc.fillText(string(values[Math.min(values.length-1,(int)board[i][j].value-1)]), pX * CELL_SIZE + CELL_SIZE / 2, pY * CELL_SIZE + CELL_SIZE / 2);
					
				}
				
			}
		}
		
	}
	
	public static void main(String[] args) {	
		launch(args);
		
	}
	
}
