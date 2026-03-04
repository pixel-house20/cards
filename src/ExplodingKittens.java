import processing.core.PImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ExplodingKittens extends CardGame {

    boolean actionPending = false;
    Card pendingActionCard = null;
    boolean nopeWindowOpen = false;
    boolean actionCanceled = false;
    int pendingPlayer = -1;

    int extraTurns = 0;
    int skipCount = 0;

    ArrayList<Card> futurePreview = new ArrayList<>();
    HashMap<String, PImage> images;

    public List<Card> playerOneHand = new ArrayList<>();
    public List<Card> playerTwoHand = new ArrayList<>();
    public List<Card> playerThreeHand = new ArrayList<>();
    public List<Card> playerFourHand = new ArrayList<>();

    public ArrayList<Card> playPile = new ArrayList<>();

    public int currentPlayer = 1; 
    public int turnsRemaining = 1;
    private boolean gameOver = false;
    private int explodedPlayer = 0;

    public java.util.ArrayList<String> screenLog;
    public java.util.ArrayList<Integer> screenTimers;
    private processing.core.PApplet parent;

    public ExplodingKittens(HashMap<String, PImage> images, 
                            java.util.ArrayList<String> log, 
                            java.util.ArrayList<Integer> timers, 
                            processing.core.PApplet p) {
        super();
        this.images = images;
        this.screenLog = log;
        this.screenTimers = timers;
        this.parent = p;
    }

    @Override
    public void createDeck() {
        for (int i = 0; i < 4; i++) {
            deck.add(new Card("Explode", "Exploding", images.get("Explode")));
            deck.add(new Card("Defuse", "Defuse", images.get("Defuse")));
        }

        String[] actions = {"Skip", "Shuffle", "SeeFuture", "Nope", "Favor", "Attack"};
        for (String action : actions) {
            for (int i = 0; i < 4; i++) {
                deck.add(new Card(action, "Action", images.get(action)));
            }
        }

        String[] catTypes = {"Tacocat", "HairyPotato", "Cattermelon", "BeardCat", "RainbowCat"};
        for (String cat : catTypes) {
            for (int i = 0; i < 5; i++) {
                PImage img = images.get(cat);
                deck.add(new Card(cat, "Cat", img));
            }
        }
    }

    @Override
    public void initializeGame() {
        super.initializeGame();
        gameOver = false;
        explodedPlayer = 0;

        ArrayList<Card> explodingCards = new ArrayList<>();
        ArrayList<Card> defuseCards = new ArrayList<>();
        for (int i = deck.size() - 1; i >= 0; i--) {
            Card c = deck.get(i);
            if (c.value.equals("Explode")) explodingCards.add(deck.remove(i));
            else if (c.value.equals("Defuse")) defuseCards.add(deck.remove(i));
        }

        Collections.shuffle(deck);

        for (int i = 0; i < 7; i++) {
            playerOneHand.add(deck.remove(0));
            playerTwoHand.add(deck.remove(0));
            playerThreeHand.add(deck.remove(0));
            playerFourHand.add(deck.remove(0));
        }

        playerOneHand.add(defuseCards.remove(0));
        playerTwoHand.add(defuseCards.remove(0));
        playerThreeHand.add(defuseCards.remove(0));
        playerFourHand.add(defuseCards.remove(0));

        deck.addAll(defuseCards);
        deck.addAll(explodingCards);
        Collections.shuffle(deck);

        logEvent("Game Started!");
        printAllHands();
    }

    public void drawCard(List<Card> hand) {
        if (gameOver) return;
        if (deck.isEmpty()) return;
        Card drawn = deck.remove(0);
        String pName = "Player " + currentPlayer;

        if (drawn.value.equals("Explode")) {
            if (hasDefuse(hand)) {
                removeDefuse(hand);
                logEvent(pName + " DEFUSED the kitten!");
                deck.add((int) (Math.random() * deck.size()), drawn);
            } else {
                logEvent(pName + " EXPLODED!");
                gameOver = true;
                explodedPlayer = currentPlayer;
                actionPending = false;
                pendingActionCard = null;
                nopeWindowOpen = false;
                actionCanceled = false;
                if (currentPlayer == 1) {
                    logEvent("You lost");
                }
                hand.clear();
                return;
            }
        } else {
            hand.add(drawn);
        }
    }

public void resolvePendingAction() {
    if (!actionPending) return;
    if (pendingActionCard == null) {
        actionPending = false;
        nopeWindowOpen = false;
        actionCanceled = false;
        return;
    }

    nopeWindowOpen = false;
    if (actionCanceled) {
        logEvent("Action was Noped!");
        nextTurn();
    } else {
        switch (pendingActionCard.value) {
            case "Skip" -> {
                
                logEvent("Player " + pendingPlayer + " used Skip.");
                skipCount = 1;
                nextTurn(); 
            }
            case "Attack" -> {
                logEvent("ATTACK! Player " + pendingPlayer + " ends turn.");
                nextTurn(); 
            }
            case "Shuffle" -> { 
            if (!deck.isEmpty()) {
        Collections.shuffle(deck);
        logEvent("Player " + pendingPlayer + " shuffled the deck!");
    } else {
        logEvent("Shuffle failed — deck is empty.");
    }

    nextTurn();
         }
            case "SeeFuture" -> {
                futurePreview.clear();
                if (pendingPlayer == 1) {
                    for (int i = 0; i < Math.min(3, deck.size()); i++) {
                        futurePreview.add(deck.get(i));
                    }
                    logEvent("Future revealed (ooohhh!).");
                } else {
                    logEvent("Player " + pendingPlayer + " looked at the future.");
                }
                nextTurn();
         }
            case "Favor" -> {
                int target = (pendingPlayer % 4) + 1;
                List<Card> current = getHand(pendingPlayer);
                List<Card> tHand = getHand(target);
                if (tHand != null && !tHand.isEmpty()) {
                    current.add(tHand.remove((int) (Math.random() * tHand.size())));
                    logEvent("P" + pendingPlayer + " took a card from P" + target);
                }
                nextTurn(); 
            }
        }
    }
    actionPending = false;
    pendingActionCard = null;
    actionCanceled = false;
}


   
    public boolean playCard(Card card, List<Card> hand) {
        if (card.value.equals("Nope")) {
            if (nopeWindowOpen && pendingActionCard != null) {
                actionCanceled = !actionCanceled;
                hand.remove(card);
                playPile.add(card);
                logEvent("NOPE!");
                return true;
            }
            return false;
        }

        if (card.type.equals("Cat")) {
            hand.remove(card);
            playPile.add(card);
            return true;
        }

        hand.remove(card);
        playPile.add(card);
        pendingActionCard = card;
        pendingPlayer = currentPlayer;
        actionPending = true;
        nopeWindowOpen = true;
        logEvent("Player " + currentPlayer + " played " + card.value);
        return true;
    }


    public void nextTurn() {
    boolean isAttack = (pendingActionCard != null && pendingActionCard.value.equals("Attack"));
    boolean isSkip = (pendingActionCard != null && pendingActionCard.value.equals("Skip"));
    
    pendingActionCard = null; 

    if (turnsRemaining > 1 && !isAttack && !isSkip) {
        turnsRemaining--;
        logEvent("Player " + currentPlayer + " has " + turnsRemaining + " forced turns left.");
        printAllHands(); 
        return;
    }

    
    int nextPlayer = (currentPlayer % 4) + 1;

    if (isSkip || skipCount > 0) {
        logEvent("Player " + nextPlayer + " was skipped!");
        currentPlayer = (nextPlayer % 4) + 1; 
        turnsRemaining = 1;
        skipCount = 0; 
    } else if (isAttack) {
        currentPlayer = nextPlayer;
        turnsRemaining = 2; 
        logEvent("Player " + currentPlayer + " ATTACKED! Take 2 turns.");
    } else {
        currentPlayer = nextPlayer;
        turnsRemaining = 1;
    }

    printAllHands();
    logEvent("Current Turn: Player " + currentPlayer);
}

    private List<Card> getHand(int id) {
        return switch (id) {
            case 1 -> playerOneHand;
            case 2 -> playerTwoHand;
            case 3 -> playerThreeHand;
            case 4 -> playerFourHand;
            default -> null;
        };
    }

    private void logEvent(String msg) {
        System.out.println(msg);
        if (screenLog != null) {
            screenLog.add(msg);
            screenTimers.add(parent.millis());
        }
    }

    private boolean hasDefuse(List<Card> hand) {
        for (Card c : hand) if (c.value.equals("Defuse")) return true;
        return false;
    }

    private void removeDefuse(List<Card> hand) {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).value.equals("Defuse")) { hand.remove(i); return; }
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getExplodedPlayer() {
        return explodedPlayer;
    }

    public void printAllHands() {
    System.out.println("Current Hands");
    printHand("Player 1 (Player)", playerOneHand);
    printHand("Player 2 (AI)", playerTwoHand);
    printHand("Player 3 (AI)", playerThreeHand);
    printHand("Player 4 (AI)", playerFourHand);
    System.out.println("Deck Size: " + deck.size());
}

private void printHand(String label, List<Card> hand) {
    System.out.print(label + ": [ ");
    for (Card c : hand) {
        System.out.print(c.value + " ");
    }
    System.out.println("]");
}
}
