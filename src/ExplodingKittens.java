
import processing.core.PApplet;
import java.util.ArrayList;

public class ExplodingKittens extends CardGame {
// stuff dealing with player # and other things will be done later  

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
        deck.add(new Card("Attack", "Action"));
        deck.add(new Card("Nope", "Action"));
        deck.add(new Card("Stop", "Action"));
        deck.add(new Card("Favor", "Action"));
        deck.add(new Card("Attack", "Action"));

    }
    String[] catTypes = {"Tacocat", "HairyPotato", "Cattermelon", "BeardCat"};
    for(String cat : catTypes){
        for( int i = 0; i < 4; i++){
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
//create smth for when gavin decides 4 of players 
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
}
