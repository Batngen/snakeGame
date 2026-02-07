import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*; 




public class SnakeGame extends JPanel implements ActionListener ,KeyListener{

    private class Tile{
        int x;
        int y;
        Tile(int x,int y){
            this.x=x;
            this.y=y;
        }
    }

    int boardWidth;
    int boardHeight;
    int tileSize=25;

    //snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    //food
    Tile food;
    Random random;

    //gameLogic
    Timer gameLoop;
    int velocityX;
    int velocityY;
    boolean gameOver= false;

     // ✅ High Score
    int highScore = 0;

    public SnakeGame( int boardWidth,int boardHeight) {
        this.boardWidth=boardWidth;
        this.boardHeight=boardHeight;
        setPreferredSize(new Dimension(this.boardWidth,this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5,5);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(10,10);
        random = new Random();
        placeFood();

        velocityX = 0;
        velocityY= 0;

        gameLoop= new Timer(100,this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        //Grid
        for(int i =0;i<boardWidth/tileSize;i++){
            g.drawLine(i*tileSize,0,i*tileSize,boardHeight);
            g.drawLine(0,i*tileSize,boardWidth,i*tileSize);
        }

       //food
        g.setColor(Color.blue);
        //g.fillRect(food.x*tileSize,food.y*tileSize,tileSize,tileSize);
        g.fill3DRect(food.x*tileSize,food.y*tileSize,tileSize,tileSize,true);
 

        // snake head
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(
            snakeHead.x * tileSize, snakeHead.y * tileSize, Color.RED,
            (snakeHead.x+1) * tileSize, (snakeHead.y+1) * tileSize, new Color(128, 0, 128) // purple shade
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, 10, 10);


        // Snake eyes
        g.setColor(Color.WHITE);
        g.fillOval(snakeHead.x * tileSize + 5, snakeHead.y * tileSize + 5, 6, 6); // left eye
        g.fillOval(snakeHead.x * tileSize + 15, snakeHead.y * tileSize + 5, 6, 6); // right eye

        g.setColor(Color.RED);
        g.fillOval(snakeHead.x * tileSize + 7, snakeHead.y * tileSize + 7, 3, 3); // left pupil
        g.fillOval(snakeHead.x * tileSize + 17, snakeHead.y * tileSize + 7, 3, 3); // right pupil


        //snake body
        for(int i = 0;i<snakeBody.size(); i++){
            Tile snakePart = snakeBody.get(i);
           // g.fillRect(snakePart.x*tileSize,snakePart.y * tileSize,tileSize,tileSize);
            g.fill3DRect(snakePart.x*tileSize,snakePart.y * tileSize,tileSize,tileSize,true);
        }

        //Score
         g.setFont(new Font("Arial",Font.PLAIN,16));
        if (gameOver){
            g.setColor(Color.red);
            g.drawString("Game Over:"+ String.valueOf(snakeBody.size()), tileSize-16,tileSize);
        }else{
            g.drawString("Score:"+ String.valueOf(snakeBody.size()), tileSize-16,tileSize);
        }


       
        // ✅ High Score (always show it)
        g.setColor(Color.WHITE);
        g.drawString("High Score: " + highScore, tileSize - 16, tileSize * 2);


    }
    public void placeFood(){
        food.x = random.nextInt(boardWidth/tileSize);// 600/25=24
        food.y = random.nextInt(boardHeight/tileSize);
    }

    public boolean collision(Tile tile1,Tile tile2){
        return tile1.x == tile2.x && tile1.y ==tile2.y;
    }
    public void move(){
        //eat food
        if (collision(snakeHead,food)){
            snakeBody.add(new Tile(food.x,food.y));
            placeFood();
        }

        //snake body
        for(int i = snakeBody.size()-1;i>=0;i--){
            Tile snakePart = snakeBody.get(i);
            if(i == 0){
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            }
            else{
                Tile prevSnakePart = snakeBody.get(i-1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;

            }
        }

        //Snake head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        //game over condition
        for(int i =0;i<snakeBody.size();i++){
            Tile snakePart = snakeBody.get(i);

            //collide with the snakePart
            if (collision(snakeHead,snakePart)){
                gameOver=true;
            }
        }

        if(snakeHead.x*tileSize<0 || snakeHead.x*tileSize > boardWidth ||
        snakeHead.y*tileSize<0 || snakeHead.y*tileSize > boardWidth){
            gameOver = true;
        }
    }

    @Override
public void actionPerformed(ActionEvent e) {
    if (gameOver) {
        gameLoop.stop();

        // ✅ update high score here
        if (snakeBody.size() > highScore) {
            highScore = snakeBody.size();
        }

        showRestartButton();
        return;
    }

    // game keeps running normally
    move();
    
    repaint();
}


    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP){
            velocityX = 0;
            velocityY=-1;
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN){
            velocityX = 0;
            velocityY=1;
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT){
            velocityX = -1;
            velocityY=0;
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            velocityX = 1;
            velocityY=0;
        }
        
    }


   // button -> restart style and structure
   // Custom rounded border class
class RoundedBorder implements javax.swing.border.Border {
    private int radius;
    RoundedBorder(int radius) {
        this.radius = radius;
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.drawRoundRect(x, y, width-1, height-1, radius, radius);
    }
}

private void showRestartButton() {
    JButton restartBtn = new JButton("Restart");

    // Base style
    restartBtn.setBounds(boardWidth/2 - 60, boardHeight/2, 120, 40);
    restartBtn.setBackground(Color.RED);        
    restartBtn.setForeground(Color.BLACK);      
    restartBtn.setFocusPainted(false);          
    restartBtn.setFont(new Font("Arial", Font.BOLD, 16)); // bigger text
    restartBtn.setBorder(new RoundedBorder(20)); // rounded corners

    // Hover effect
    restartBtn.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            restartBtn.setBackground(new Color(220, 20, 60)); // darker red
            restartBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            restartBtn.setBackground(Color.RED); // back to normal
        }
    });

    restartBtn.addActionListener(ev -> restartGame());

    this.setLayout(null);
    this.add(restartBtn);
    this.repaint();
}





private void restartGame() {
    // Reset snake
    snakeHead = new Tile(5, 5);
    snakeBody.clear();
    velocityX = 0;
    velocityY = 0;
    gameOver = false;

    // Remove the restart button
    this.removeAll();
    this.revalidate();
    this.repaint();

    // Restart game loop
    gameLoop.start();
}



    // do not need
    @Override
    public void keyTyped(KeyEvent e) {
       
    }


    @Override
    public void keyReleased(KeyEvent e) {
        
    }
}
