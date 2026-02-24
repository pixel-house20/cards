
// import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class ExplodingKittens extends CardGame {

// stuff dealing with player # and other things will be done later  
boolean actionPending = false;
Card pendingActionCard = null;

int extraTurns = 0;
int skipCount = 0;

ArrayList<Card> futurePreview = new ArrayList<>();
HashMap<String, PImage> images;

public List<Card> playerOneHand = new ArrayList<>();
public List<Card> playerTwoHand = new ArrayList<>();
public List<Card> playerThreeHand = new ArrayList<>();
public List<Card> playerFourHand = new ArrayList<>();

public ArrayList<Card> playPile = new ArrayList<>();

public int currentPlayer = 1; // 1 = human, 2-4 = AI
public int turnsRemaining = 1;



public ExplodingKittens(){
    super();
}


public ExplodingKittens(HashMap<String, PImage> images){
    super();
    this.images = images;
}

@Override

public void createDeck() {
    // 4 Actions
    String[] actions = { "Skip", "Shuffle", "SeeFuture", "Nope", "Favor", "Attack" };
    for (int i = 0; i < 4; i++) {
        deck.add(new Card("Explode", "Exploding", images.get("Explode")));
        deck.add(new Card("Defuse", "Defuse", images.get("Defuse")));
        for (String action : actions) {
            deck.add(new Card(action, "Action", images.get(action)));
        }
    }

    // 5 Cat cards
    String[] catTypes = { "Tacocat", "HairyPotato", "Cattermelon", "BeardCat", "RainbowCat" };
    for (String cat : catTypes) {
        for (int i = 0; i < 5; i++) {
            deck.add(new Card(cat, "Cat", images.get(cat)));
        }
    }

    for (String cat : catTypes) {
    for (int i = 0; i < 5; i++) {
        PImage img = images.get(cat);
        if (img == null) System.out.println("Image missing for: " + cat);
        deck.add(new Card(cat, "Cat", img));
    }
}
}

@Override
public void initializeGame(){
    super.initializeGame();
    
    // Add extra defuse cards for 4 players
    for (int i = 0; i < 4; i++) {
        deck.add(new Card("Defuse", "Defuse", images.get("Defuse")));
    }

    ArrayList<Card> explodingCards = new ArrayList<>();
    ArrayList<Card> defuseCards = new ArrayList<>();
    for(int i = deck.size()-1; i>=0;i--){
        Card c = deck.get(i);
        if(c.value.equals("Explode")){
            explodingCards.add(deck.remove(i));
        } else if(c.value.equals("Defuse")){
            defuseCards.add(deck.remove(i));
        }
    }

    Collections.shuffle(deck);
    printFullDeck(); // Debugging: print the full deck after creation and shuffling

    // Deal 7 cards to each player
    for (int i = 0; i < 7; i++) {
        playerOneHand.add(deck.remove(0));
        playerTwoHand.add(deck.remove(0));
        playerThreeHand.add(deck.remove(0));
        playerFourHand.add(deck.remove(0));
    }

    // Give each player one defuse
    playerOneHand.add(defuseCards.remove(0));
    playerTwoHand.add(defuseCards.remove(0));
    playerThreeHand.add(defuseCards.remove(0));
    playerFourHand.add(defuseCards.remove(0));

    deck.addAll(explodingCards); // add kittens back
    Collections.shuffle(deck);    // shuffle again

    System.out.println("---- Player Hands ----");
    printHand("Player One", playerOneHand);
    printHand("Player Two", playerTwoHand);
    printHand("Player Three", playerThreeHand);
    printHand("Player Four", playerFourHand);
}


private boolean hasDefuse(List<Card> hand){
    for(Card c : hand){
        if(c.value.equals("Defuse")){
            return true;
        }
    }
    return false;
}

private void removeDefuse(List<Card> hand){
    for(int i = 0; i < hand.size(); i++){
        if (hand.get(i).value.equals("Defuse")){
            hand.remove(i);
            return;
        }
    }
}
    public void drawCard(List<Card> hand){
    if(deck.isEmpty()) return;
    Card drawn = deck.remove(0);
    if(drawn.value.equals("Explode")){
        if(hasDefuse(hand)){
            removeDefuse(hand);
            System.out.println("Defused");
        } else {
            hand.add(drawn);
        }
    } else {
        hand.add(drawn);
    }
}
public boolean playCard(Card card, List<Card> hand){

    // CAT PAIR LOGIC 
    if(card.type.equals("Cat")){
        int count = 0;
        for(Card c : hand){
            if(c.type.equals("Cat") && c.value.equals(card.value)){
                count++;
            }
        }

        if(count >= 2){
            // Remove two matching cats
            int removed = 0;
            for(int i = hand.size() - 1; i >= 0 && removed < 2; i--){
                Card c = hand.get(i);
                if(c.type.equals("Cat") && c.value.equals(card.value)){
                    playPile.add(hand.remove(i));
                    removed++;
                }
            }

            System.out.println("Played two " + card.value + " cards!");

            switchTurns();
            return true;
        } else {
            System.out.println("Need two matching Cat cards.");
            return false;
        }
    }

    // ACTION CARDS 
    if(!card.type.equals("Action")){
        System.out.println("Invalid card type.");
        return false;
    }

    hand.remove(card);
    playPile.add(card);

    System.out.println(card.value + " played!");

    switch(card.value){
        case "Skip":
            System.out.println("Next player's turn will be skipped");
            skipCount = 1;
            return true;

        case "Shuffle":
            Collections.shuffle(deck);
            switchTurns();
            return true;

        case "SeeFuture":
            futurePreview.clear();
            for(int i = 0; i < Math.min(3, deck.size()); i++){
                futurePreview.add(deck.get(i));
            }
            switchTurns();
            return true;

        case "Attack":
           turnsRemaining = 0; 
            actionPending = true; 
            pendingActionCard = card; 
            return true;
    }

    System.out.println("---- Player Hands ----");
    printHand("Player One", playerOneHand);
    printHand("Player Two", playerTwoHand);
    printHand("Player Three", playerThreeHand);
    printHand("Player Four", playerFourHand);

    switchTurns();
    return true;

    
}

public void executePendingAction(){
  
    if(pendingActionCard==null) return;

        if(pendingActionCard.value.equals("SeeFuture")){    
            futurePreview.clear();
            futurePreview.clear();
            for(int i = 0; i < Math.min(3, deck.size()); i++){
                futurePreview.add(deck.get(i));
            }
        }
         if(pendingActionCard.value.equals("Attack")){
            extraTurns = 2;
            System.out.println("Attack, next player must take 2 turns");
        }
        if (pendingActionCard.value.equals("Stop")){
            if(extraTurns > 0){
                extraTurns = 0;
                System.out.println("Attack stopped");
            }
        }
}

private void printHand(String playerName, List<Card> hand){
    System.out.print(playerName + ": ");
    for(Card c : hand){
        System.out.print(c.value + " ");
    }
    System.out.println();
}

public void nextTurn() {
    if (turnsRemaining > 0) {
        turnsRemaining--;
    }

    if (turnsRemaining <= 0) {
        currentPlayer++;
        if (currentPlayer > 4) currentPlayer = 1;

        // CHECK: Was this move triggered by an Attack
        if (pendingActionCard != null && pendingActionCard.value.equals("Attack")) {
            turnsRemaining = 2; // Next player gets 2 turns
            pendingActionCard = null; // Clear the flag
        } else {
            turnsRemaining = 1; // Standard reset
        }

        // Handle skip
        if (skipCount > 0) {
            currentPlayer++;
            if (currentPlayer > 4) currentPlayer = 1;
            skipCount = 0; 
        }
    }
    System.out.println("Switching. Current Player: " + currentPlayer + " | Turns: " + turnsRemaining);
}


public void printFullDeck() {
    System.out.println("ALL CARDS (DEBUGGING) (" + deck.size() + " cards)");
    for (int i = 0; i < deck.size(); i++) {
        Card c = deck.get(i);
        System.out.println(i + ": " + c.value + " [" + c.type + "]");
    }
    
}
}
