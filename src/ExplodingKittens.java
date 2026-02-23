
import processing.core.PApplet;
import java.util.ArrayList;

public class ExplodingKittens extends CardGame {
// stuff dealing with player # and other things will be done later  
boolean actionPending = false;
Card pendingActionCard = null;
int extraTurns = 0;
ArrayList<Card> futurePreview = new ArrayList<>();

public ExplodingKittens(){
    super();
}


@Override
public void createDeck(){

    // actions but im prob going to have to go over this sometime
    for(int i = 0; i < 4; i++){
        deck.add(new Card("Explode", "Exploding"));
        deck.add(new Card("Defuse", "Defuse"));
        deck.add(new Card("Skip", "Action"));
        deck.add(new Card("Shuffle", "Action"));
        deck.add(new Card("SeeFuture", "Action"));
        //deck.add(new Card("Attack", "Action"));
        deck.add(new Card("Nope", "Action"));
        deck.add(new Card("Stop", "Action"));
        deck.add(new Card("Favor", "Action"));
        deck.add(new Card("Attack", "Action"));
        // attack, skip, defuse logic done 
        //to do = nope stop see future 

    }
    String[] catTypes = {"Tacocat", "HairyPotato", "Cattermelon", "BeardCat", "RainbowCat"};
    for(String cat : catTypes){
        for( int i = 0; i < 5; i++){
            deck.add(new Card(cat, "Cat"));

        }
    }
}

@Override
public void initializeGame(){
super.initializeGame();
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

dealCards(7);

playerOneHand.addCard(defuseCards.remove(0));
playerTwoHand.addCard(defuseCards.remove(0));
playerThreeHand.addCard(defuseCards.remove(0));
PlayerFourHand.addCard(defuseCards.remove(0));

deck.addAll(explodingCards);
//3 diffuses and 3 exploding kittens 
//total 56 cards 
}
private boolean hasDefuse(Hand hand){
    for(int i = 0; i < hand.getSize();i++){
        if(hand.getCard(i).value.equals("Defuse")){
            return true;
        }
    }
    return false;
}

private void removeDefuse(Hand hand){
    for(int i = 0; i < hand.getSize(); i++){
        if (hand.getCard(i).value.equals("Defuse")){
            hand.removeCard(hand.getCard(i));
            return;
        }
    }
}
    @Override
public void drawCard(Hand hand){
    if(deck.isEmpty()) return;
    Card drawn = deck.remove(0);
    if(drawn.value.equals("Explode")){
        if(hasDefuse(hand)){
            removeDefuse(hand);
            System.out.println("Defused");

        } else{
            
        }
    }


}
@Override
public boolean playCard(Card card, Hand hand){

    if(!isValidPlay(card)) return false;
    
    hand.removeCard(card);
    discardPile.add(card);

    if(card.value.equals("Skip)")){
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

    if(value.equals("SeeFuture")){
        futurePreview.clear();
        for(int i = 0;  i < deck.size(); i++){
            futurePreview.add(deck.get(i));
            System.out.println("Top 3 cards are");
            for(Card c : futurePreview){
                System.out.println(c.value);

            }

        }
}
if(value.equals("Attack")){
    extraTurns = 2;
    System.out.println("Attack, next player must take 2 turns");
}
if (value.equals("Stop")){
    if(extraTurns > 0){
        extraTurns = 0;
        System.out.println("Attack stopped");
    }
}
}
}
