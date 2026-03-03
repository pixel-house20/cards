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
    boolean restartQueued = false;
    int restartStartTime = 0;
    final int restartDelayMs = 2500;

    PImage startScreenImg;
    final int Startstate = 0;
    final int Gamestate = 1;
    int currentState = Startstate;

    java.util.ArrayList<String> gameLog = new java.util.ArrayList<>();
    java.util.ArrayList<Integer> logTimers = new java.util.ArrayList<>();
    final int FADE_TIME = 3000;


    public static void main(String[] args) {
        PApplet.main("App");
    }

    @Override
    public void settings() {
        size(1000, 1000);   
    }

@Override
public void setup() {
    String path = "src/Graphics/";

    stackImage = loadImage(path + "Background.jpg");
    startScreenImg = loadImage(path + "Startscreen.jpg");
    
    cardImages.put("Skip",       loadImage(path + "Skip.jpg"));
    cardImages.put("Explode",    loadImage(path + "Explode.jpg"));
    cardImages.put("Defuse",     loadImage(path + "Defuse.jpg"));
    cardImages.put("Shuffle",    loadImage(path + "Shuffle_.jpg"));
    cardImages.put("SeeFuture",  loadImage(path + "SeeFuture_.jpg"));
    cardImages.put("Nope",       loadImage(path + "Nope.jpg"));
    cardImages.put("Favor",      loadImage(path + "Favor.jpg"));
    cardImages.put("Attack",     loadImage(path + "Attack.jpg"));
    
    cardImages.put("Tacocat",     loadImage(path + "Tacocat_.jpg"));
    cardImages.put("HairyPotato", loadImage(path + "HairyPotato_.jpg"));
    cardImages.put("Cattermelon", loadImage(path + "Cattermelon_.jpg"));
    cardImages.put("BeardCat",    loadImage(path + "Beardcat_.jpg")); 
    cardImages.put("RainbowCat",  loadImage(path + "Rainbowcat_.jpg"));

    for (String key : cardImages.keySet()) {
        if (cardImages.get(key) == null) {
            println("Still missing image for: " + key);
        }
    }

    game = new ExplodingKittens(cardImages, gameLog, logTimers, this);    game.initializeGame();
    game.futurePreview.clear();
}

private void restartGame() {
    gameLog.clear();
    logTimers.clear();
    futurePrinted = false;
    aiWaiting = false;
    restartQueued = false;
    restartStartTime = 0;
    game = new ExplodingKittens(cardImages, gameLog, logTimers, this);
    game.initializeGame();
    game.futurePreview.clear();
    currentState = Gamestate;
}

private void handleGameOver() {
    fill(0, 170);
    rect(0, 0, width, height);
    fill(255, 80, 80);
    textAlign(CENTER, CENTER);
    textSize(36);
    String explodedText = game.getExplodedPlayer() == 1
        ? "You Exploded!"
        : "Player " + game.getExplodedPlayer() + " Exploded!";
    text(explodedText, width / 2f, height / 2f - 25);
    fill(255);
    textSize(20);
    text("Starting a new game...", width / 2f, height / 2f + 20);

    if (!restartQueued) {
        restartQueued = true;
        restartStartTime = millis();
    }

    if (millis() - restartStartTime >= restartDelayMs) {
        restartGame();
    }
}


@Override
public void draw() {
    background(40, 45, 50);
    if (currentState == Startstate) {
        if (startScreenImg != null) {
            image(startScreenImg, 0, 0, width, height);
        } else {
            textAlign(CENTER, CENTER);
            textSize(32);
            text("Exploding Kittens\nClick to Start", width/2, height/2);
        }
    } else {
        if (game.isGameOver()) {
            handleGameOver();
            drawGameLog();
            return;
        }
  
        if (game.actionPending && frameCount % 30 == 0) {
            game.resolvePendingAction();
        }

        // Safety valve: if the pending card was cleared unexpectedly, unblock turns.
        if (game.actionPending && game.pendingActionCard == null) {
            game.actionPending = false;
        }

        displayGameInfo();
        drawHand(game.playerOneHand, height - 350, "Your Hand", false);
        drawStacks();
        drawCenterDecks();
        drawPlayPile();

        if (!game.futurePreview.isEmpty()) {
            drawFuturePreview();

            // AI "See Future" should not block the game waiting for a human click.
            if (game.currentPlayer != 1) {
                game.futurePreview.clear();
                futurePrinted = false;
            }
        }

        if (game.currentPlayer != 1 && !game.actionPending) {
            if (!aiWaiting) {
                aiWaiting = true;
                aiStartTime = millis();


                aiDelay = (int) random(800, 1500);
            }

            if (aiWaiting && millis() - aiStartTime >= aiDelay) {
                takeAITurn(game.currentPlayer);
                aiWaiting = false; 
            }
        }
    }

    drawGameLog();
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
    if (currentState == Startstate) {
        currentState = Gamestate;
        return; 
    }

    if (game.isGameOver()) return;

    if (!game.futurePreview.isEmpty()) {
        game.futurePreview.clear();
        futurePrinted = false; 
        return; 
    }

    if(game.currentPlayer != 1 || game.actionPending) return;

    float cardW = 100;
    float cardH = 150;
    float deckX = width / 2f - cardW - 20;
    float deckY = height / 2f - cardH / 2f;

    if (mouseX >= deckX && mouseX <= deckX + cardW &&
        mouseY >= deckY && mouseY <= deckY + cardH) {
        game.drawCard(game.playerOneHand);
        game.nextTurn(); 
        return;
    }

    for (int i = game.playerOneHand.size() - 1; i >= 0; i--) {
        Card c = game.playerOneHand.get(i);
        if (c.isMouseOver(mouseX, mouseY)) {
            
            boolean played = game.playCard(c, game.playerOneHand);

            if (played) {
                if (c.type.equals("Cat")) {
                    game.nextTurn();
                }
                return;
            }
        }
    }
}
// SEE THE FUTURE HERE
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
    float pileX = width / 2f + 20;  
    float pileY = height / 2f - cardH / 2f;

    for (int i = 0; i < game.playPile.size(); i++) {
        Card c = game.playPile.get(i);

        float offset = i * 2; 
        c.setPosition(pileX + offset, pileY + offset, cardW, cardH);
        c.draw(this);
    }
}

public void takeAITurn(int player) {
    List<Card> hand = null;

    switch(player) {
        case 2: hand = game.playerTwoHand; break;
        case 3: hand = game.playerThreeHand; break;
        case 4: hand = game.playerFourHand; break;
    }

    if (hand == null || hand.isEmpty()) {
        game.nextTurn();
        return;
    }

    Card bestCard = null;
    int highestScore = -999;

    for (Card c : hand) {
        if (c.value.equals("Explode") || c.value.equals("Defuse")) continue;
        int score = c.getStrategicValue();

        if (c.type.equals("Cat")) {
            int count = 0;
            for (Card other : hand) {
                if (other.value.equals(c.value)) count++;
            }
            if (count < 2) continue; 
        }

        if (score > highestScore) {
            highestScore = score;
            bestCard = c;
        }
    }

    // AI DECISION LOGIC
    if (bestCard != null && highestScore > 2) {
        boolean played = game.playCard(bestCard, hand);
        if (played && bestCard.type.equals("Cat")) {
            game.nextTurn();
        }
    } else {
        // AI draws a card and ends turn
        game.drawCard(hand);
        game.nextTurn();
    }
}

public void drawGameLog() {
    if (gameLog.isEmpty()) return;

    int start = Math.max(0, gameLog.size() - 2);
    textAlign(LEFT, TOP);
    textSize(16);

    for (int i = start; i < gameLog.size(); i++) {
        int timeElapsed = millis() - logTimers.get(i);
        
        float starter = 255;
        if (timeElapsed > FADE_TIME) {
            starter = max(0, 255 - (timeElapsed - FADE_TIME) / 5.0f); 
        }

        if (starter > 0) {
            int yPos = 80 + (i - start) * 25;
            
            fill(0, starter * 0.6f);
            noStroke();
            rect(20, yPos - 2, 400, 22, 5);

            if (i == gameLog.size() - 1) fill(255, 255, 0, starter);
            else fill(255, starter);
            
            text("> " + gameLog.get(i), 30, yPos);
        }
    }
}
}
