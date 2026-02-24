import processing.core.PApplet;
import java.util.HashMap;
import processing.core.PImage;

// import java.util.List;

public class App extends PApplet {

    ExplodingKittens game;

    HashMap<String, PImage> cardImages = new HashMap<>();
    PImage stackImage;


    public static void main(String[] args) {
        PApplet.main("App");
    }

    @Override
    public void settings() {
        size(1000, 1000);   
    }

    @Override
    public void setup() {

     stackImage = loadImage("Background.jpg");


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

    drawHand(game.playerOneHand, height - 340, "Your Hand", false);
    drawStacks();
    drawCenterDecks();

   
    if (!game.futurePreview.isEmpty()) {
        drawFuturePreview();
    }   
}

    public void drawStacks() {
    int stackOffset = 3;
    float cardW = 100;
    float cardH = 150;

    // Player info is {label, x, y, textAlign}
    Object[][] players = {
        {"Jerry", width / 2f - cardW / 2f, 50f, CENTER},
        {"Arnold", 50f, height / 2f - cardH / 2f, LEFT},
        {"Matilda", width - 150f, height / 2f - cardH / 2f, RIGHT}
    };

    for (Object[] player : players) {
        String name = (String) player[0];
        float x = (float) player[1];
        float y = (float) player[2];
        int align = (int) player[3];

        for (int i = 0; i < 7; i++) {
            float xi = x + i * stackOffset;
            float yi = y + i * stackOffset;
            image(stackImage, xi, yi, cardW, cardH);

            // Draw thick black border
            stroke(0);
            strokeWeight(2);
            noFill();
            rect(xi, yi, cardW, cardH);
        }

        // Draw player label
        fill(255);
        textAlign(align, BOTTOM);
        text(name, x + cardW / 2, y - 10);
    }

    strokeWeight(1);
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

        c.hovered = c.isMouseOver(mouseX, mouseY);

        c.setPosition(x, y, 100, 150);
        c.draw(this);
    }
    }

public void drawHand(java.util.List<Card> hand, float y, String playerName, boolean shrink) {
    float startX = 50;
    float spacing = 110;

    float cardW = shrink ? 70 : 100;
    float cardH = shrink ? 105 : 150;

    textSize(16);
    fill(255);
    textAlign(LEFT, BOTTOM);
    text(playerName, 50, y - 10);

    for (int i = 0; i < hand.size(); i++) {
        Card c = hand.get(i);
        float x = startX + i * spacing;

        c.hovered = c.isMouseOver(mouseX, mouseY);

        c.setPosition(x, y, cardW, cardH);
        c.draw(this);
    }
}



public void drawCenterDecks() {
    float cardW = 100;
    float cardH = 150;

    // Positions for the two central stacks
    float centerX = width / 2f;
    float centerY = height / 2f;

    // Master deck – left
    float deckX = centerX - cardW - 20;
    float deckY = centerY - cardH / 2f;

    fill(204, 204, 183); 
    rect(deckX, deckY, cardW, cardH); 

    stroke(0);
    strokeWeight(3); // thick black border
    noFill();
    rect(deckX, deckY, cardW, cardH);

    fill(255);
    textAlign(CENTER, BOTTOM);
    text("Deck", deckX + cardW / 2f, deckY - 10);

    // Play pile – right
    float pileX = centerX + 20;
    float pileY = centerY - cardH / 2f;

    fill(204, 204, 183); 
    rect(pileX, pileY, cardW, cardH);

    stroke(0);
    strokeWeight(3);
    noFill();
    rect(pileX, pileY, cardW, cardH);

    fill(255);
    textAlign(CENTER, BOTTOM);
    text("Play Pile", pileX + cardW / 2f, pileY - 10);

    // Reset stroke weight
    strokeWeight(1);
}
}