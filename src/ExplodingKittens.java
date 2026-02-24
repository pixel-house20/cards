
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
ArrayList<Card> futurePreview = new ArrayList<>();
HashMap<String, PImage> images;

public List<Card> playerOneHand = new ArrayList<>();
public List<Card> playerTwoHand = new ArrayList<>();
public List<Card> playerThreeHand = new ArrayList<>();
public List<Card> playerFourHand = new ArrayList<>();

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
    if(!isValidPlay(card)) return false;
    hand.remove(card);
    discardPile.add(card);

            if(card.value.equals("Skip")){
            System.out.println("Turn Skipped");
        switchTurns();
        return true;
    }
//    if(card.value.equals("Attack")){
//     System.out.println("Attack Played");
//      switchTurns();
//    return true;
//    }
  
   if(card.value.equals("Nope")){
    if(actionPending){
    System.out.println("Nope, action was cancelled");
    actionPending = false;
     pendingActionCard = null;
 
    }
    switchTurns();
    return true;
}

    if (card.value.equals("Shuffle")){
        System.out.println("Deck Shuffled!");
        java.util.Collections.shuffle(deck);
        switchTurns();
        return true;
        
    }
        
    
if(actionPending){
    executePendingAction();
}
actionPending = true;
pendingActionCard = card;
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
}
