
import processing.core.PApplet;

public class ExplodingKittens extends CardGame {
// stuff dealing with player # and other things will be done later  

public ExplodingKittens(){
    initializeGame();
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

dealCards(7);

//create smth for when gavin decides 4 of players 
//3 diffuses and 3 exploding kittens 
//total 56 cards 



}
}
