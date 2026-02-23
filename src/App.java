import processing.core.PApplet;

public class App extends PApplet {

    ExplodingKittens game;

    public static void main(String[] args) {
        PApplet.main("App");
    }

    @Override
    public void settings() {
        size(1000, 800);   
    }

    @Override
    public void setup() {
        game = new ExplodingKittens();
        game.initializeGame();
    }

    @Override
    public void draw() {
        background(40,45,50);

        displayGameInfo();
        // Draw UI elements and game state here

        drawHand(game.playerOneHand, height - 150, "Player One");
        drawHand(game.playerTwoHand, 50, "Player Two");
        // make this changable based on the current player

        if (!game.futurePreview.isEmpty()) {
            drawFuturePreview();
        }   

    }

    @Override
    public void mousePressed() {
        if (!game.futurePreview.isEmpty()) {
            game.futurePreview.clear();
            return;
        }   
    }
}


public void drawFuturePreview(){

    fill (0, 200);
    rect(0, 0, width, height);

    fill(255);
    textSize(24);
    textAlighn(CENTER, CENTER);
    text("Future Preview", width / 2, height / 2 - 100);

    // see 3 cards
    for (int i = 0; i < game.futurePreview.size(); i++){
        Card c = game.futurePreview.get(i);

        float x = (width/2 - 150) * (i *110);
        c.setPosition(x, height / 2, 100, 150);
        c.display(this);

    }

    text("Click anywhere to close", width / 2, height / 2 + 100);





}