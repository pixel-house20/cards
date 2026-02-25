import processing.core.PApplet;
import java.util.HashMap;
import java.util.List;

import processing.core.PImage;

// import java.util.List;

public class App extends PApplet {

    ExplodingKittens game;

    HashMap<String, PImage> cardImages = new HashMap<>();
    PImage stackImage;
    boolean futurePrinted = false;
    Card hoveredCatCard = null;

    boolean aiWaiting = false;
    int aiStartTime = 0;
    int aiDelay = 0; 


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

    // TEMP:  futurePreview for debugging CHANGE THIS LATER*****
    for (int i = 0; i < Math.min(3, game.deck.size()); i++) {
        game.futurePreview.add(game.deck.get(i));
    }
    
}


    @Override
    public void draw() {
    background(40,45,50);

    displayGameInfo();

    drawHand(game.playerOneHand, height - 340, "Your Hand", false);
    drawStacks();
    drawCenterDecks();
    drawPlayPile();

   
    if(game.currentPlayer != 1){

        if(!aiWaiting){
            aiWaiting = true;
            aiStartTime = millis();


            int time = 6000;
            // 20000 for time


            // Random delay between 5 and 20 seconds
            aiDelay = (int) random(5000, time);

        }

        if(aiWaiting && millis() - aiStartTime >= aiDelay){

            takeAITurn(game.currentPlayer);
            aiWaiting = false;
        }
    }
}



    public void drawStacks() {
    int stackOffset = 3;
    float cardW = 100;
    float cardH = 150;

    // Player info is {label, x, y, textAlign}
    Object[][] players = {
        {"Player 2", width / 2f - cardW / 2f, 50f, CENTER},
        {"Player 3", 50f, height / 2f - cardH / 2f, LEFT},
        {"Player 4", width - 150f, height / 2f - cardH / 2f, RIGHT}
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

    if(game.currentPlayer != 1) return;

    if (!game.futurePreview.isEmpty()) {
        game.futurePreview.clear();
        return;
    }

    // ===== Check Deck Click First (Draw = one action) =====
    float cardW = 100;
    float cardH = 150;
    float deckX = width / 2f - cardW - 20;
    float deckY = height / 2f - cardH / 2f;

    if (mouseX >= deckX && mouseX <= deckX + cardW &&
        mouseY >= deckY && mouseY <= deckY + cardH) {

        System.out.println("Player draws a card");
        game.drawCard(game.playerOneHand);
        game.nextTurn();
        return;
    }

    // ===== Check Hand =====
    for (Card c : game.playerOneHand) {
        if (c.isMouseOver(mouseX, mouseY)) {

            boolean played = game.playCard(c, game.playerOneHand);

            if (played) {
                System.out.println("Player 1 performed one action");
                hoveredCatCard = null;

            
                if (!c.value.equals("Attack") && !c.value.equals("Skip")) {
                    game.nextTurn();
                }
                return; 
}
            return; // prevents multiple actions
        }
    }
}

   public void drawFuturePreview() {
    fill(0, 200);
    rect(0, 0, width, height);

    fill(255);
    textSize(24);
    textAlign(CENTER, CENTER);
    text("Future Preview", width / 2, height / 2 - 100);

    // Print the future cards once for debugging
   if (!futurePrinted) {
    System.out.println(" Future Preview Cards");
    for (int i = 0; i < game.futurePreview.size(); i++) {
        Card c = game.futurePreview.get(i);
        System.out.println("Card " + i + ": " + c.value);
    }
    futurePrinted = true;
}

    for (int i = 0; i < game.futurePreview.size(); i++) {
        Card c = game.futurePreview.get(i);
        float x = width / 2 - 150 + i * 110;
        c.setPosition(x, height / 2, 100, 150);
        c.draw(this);
    }

    text("Click anywhere to close", width / 2, height / 2 + 100);
}

    public void displayGameInfo() {
    // fill(255);
    // textSize(20);
    // textAlign(LEFT, TOP);
    // text("Player One Cards: " + game.playerOneHand.size(), 20, 20);
    // text("Player Two Cards: " + game.playerTwoHand.size(), 20, 50);'
    // 
    fill(255);
    textSize(22);
    textAlign(LEFT, TOP);
    
    String turnText = (game.currentPlayer == 1) ? "YOUR TURN" : "AI Player " + game.currentPlayer + " Turn";
    text(turnText, 20, 20);

    if (game.turnsRemaining > 1) {
        fill(255, 0, 0);
        text("FORCED TURNS: " + game.turnsRemaining, 20, 50);
    } 
    else {
        fill(200);
        text("Turns: 1", 20, 50);
    }
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
    strokeWeight(3); 
    noFill();
    rect(deckX, deckY, cardW, cardH);

    image(stackImage, deckX, deckY, cardW, cardH);
    textAlign(CENTER, BOTTOM);
    text("Deck", deckX + cardW / 2f, deckY - 10);

  
}

public void drawPlayPile() {
    float cardW = 100;
    float cardH = 150;
    float pileX = width / 2f + 20;  // Adjust with your layout
    float pileY = height / 2f - cardH / 2f;

    for (int i = 0; i < game.playPile.size(); i++) {
        Card c = game.playPile.get(i);
        // Slight offset so cards appear stacked
        float offset = i * 3;
        c.setPosition(pileX + offset, pileY + offset, cardW, cardH);
        c.draw(this);
    }
}


public void takeAITurn(int player){

    List<Card> hand = null;

    switch(player){
        case 2: hand = game.playerTwoHand; break;
        case 3: hand = game.playerThreeHand; break;
        case 4: hand = game.playerFourHand; break;
    }

    if(hand == null || hand.isEmpty()) return;

    Card bestCard = null;
    int highestScore = -999;

    for(Card c : hand){

        if(c.type.equals("Action") || c.type.equals("Cat")){

            int score = c.getStrategicValue();

            if(c.type.equals("Cat")){
                int count = 0;
                for(Card other : hand){
                    if(other.value.equals(c.value)) count++;
                }
                if(count < 2) continue;
            }

            if(score > highestScore){
                highestScore = score;
                bestCard = c;
            }
        }
    }

    if(bestCard != null){
        System.out.println("AI Player " + player + " plays: " + bestCard.value);
        game.playCard(bestCard, hand);
    } else {
        System.out.println("AI Player " + player + " draws");
        game.drawCard(hand);
    }

    // EXACTLY ONE ACTION COMPLETE
    game.nextTurn();
}

}