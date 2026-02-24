import processing.core.PApplet;
import java.util.HashMap;
import processing.core.PImage;
// import java.util.List;

public class App extends PApplet {

    ExplodingKittens game;

    HashMap<String, PImage> cardImages = new HashMap<>();


    public static void main(String[] args) {
        PApplet.main("App");
    }

    @Override
    public void settings() {
        size(1000, 800);   
    }

    @Override
    public void setup() {

    cardImages.put("Skip", loadImage("Skip.jpg"));
    cardImages.put("Explode", loadImage("Explode.jpg"));
    cardImages.put("Defuse", loadImage("Defuse.jpg"));
    cardImages.put("Shuffle", loadImage("Shuffle_.jpg"));
    cardImages.put("SeeFuture", loadImage("SeeFuture_.jpg"));
    cardImages.put("Nope", loadImage("Nope.jpg"));
    cardImages.put("Favor", loadImage("Favor.jpg"));
    cardImages.put("Attack", loadImage("Attack.jpg"));
    
    cardImages.put("Tacocat", loadImage("Tacocat_.jpg"));
    cardImages.put("HairyPotato", loadImage("HairyPotato_.jpg"));
    cardImages.put("Cattermelon", loadImage("Cattermelon_.jpg"));
    cardImages.put("BeardCat", loadImage("BeardCat_.jpg"));
    cardImages.put("RainbowCat", loadImage("RainbowCat_.jpg"));

    // Now create the game with fully populated images map
    game = new ExplodingKittens(cardImages); 
    game.initializeGame();
}


    @Override
    public void draw() {
        background(40,45,50);

        displayGameInfo();

        drawHand(game.playerOneHand, height - 150, "Player One");
        drawHand(game.playerTwoHand, 50, "Player Two");

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

    public void drawFuturePreview(){
    fill(0, 200);
    rect(0, 0, width, height);

    fill(255);
    textSize(24);
    textAlign(CENTER, CENTER);
    text("Future Preview", width / 2, height / 2 - 100);

    for (int i = 0; i < game.futurePreview.size(); i++) {
        Card c = game.futurePreview.get(i);
        float x = width/2 - 150 + i * 110;
        c.setPosition(x, height/2, 100, 150);
        c.draw(this);  // draws image
    }

    text("Click anywhere to close", width / 2, height / 2 + 100);
}

    public void displayGameInfo() {
    // fill(255);
    // textSize(20);
    // textAlign(LEFT, TOP);
    // text("Player One Cards: " + game.playerOneHand.size(), 20, 20);
    // text("Player Two Cards: " + game.playerTwoHand.size(), 20, 50);
    }   

   public void drawHand(java.util.List<Card> hand, float y, String playerName) {
    float startX = 50;
    float spacing = 110;

    textSize(16);
    fill(255);
    textAlign(LEFT, BOTTOM);
    text(playerName, 50, y - 10);

    for (int i = 0; i < hand.size(); i++) {
        Card c = hand.get(i);
        float x = startX + i * spacing;
        c.setPosition(x, y, 100, 150);  
        c.draw(this);  // draws the image if assigned
    }
}
}