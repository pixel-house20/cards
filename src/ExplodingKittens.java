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
    }

    public void drawCard(List<Card> hand) {
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
            }
        } else {
            hand.add(drawn);
            logEvent(pName + " drew a card.");
        }
    }

    public void resolvePendingAction() {
        if (!actionPending || pendingActionCard == null) return;

        nopeWindowOpen = false;
        if (actionCanceled) {
            logEvent("Action was Noped!");
        } else {
            switch (pendingActionCard.value) {
                case "Skip" -> { skipCount = 1; logEvent("Player " + pendingPlayer + " skipped."); }
                case "Shuffle" -> { Collections.shuffle(deck); logEvent("Deck shuffled."); }
                case "SeeFuture" -> {
                    futurePreview.clear();
                    for (int i = 0; i < Math.min(3, deck.size()); i++) futurePreview.add(deck.get(i));
                    logEvent("Looking into the future...");
                }
                case "Attack" -> { turnsRemaining = 0; logEvent("ATTACK!"); }
                case "Favor" -> {
                    int target = (pendingPlayer % 4) + 1;
                    List<Card> current = getHand(pendingPlayer);
                    List<Card> tHand = getHand(target);
                    if (tHand != null && !tHand.isEmpty()) {
                        current.add(tHand.remove((int) (Math.random() * tHand.size())));
                        logEvent("P" + pendingPlayer + " took a card from P" + target);
                    }
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
            // Pair logic here (simplified for space)
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
        if (turnsRemaining > 0) turnsRemaining--;
        if (turnsRemaining <= 0) {
            currentPlayer = (currentPlayer % 4) + 1;
            if (pendingActionCard != null && pendingActionCard.value.equals("Attack")) {
                turnsRemaining = 2;
                pendingActionCard = null;
            } else {
                turnsRemaining = 1;
            }
            if (skipCount > 0) {
                currentPlayer = (currentPlayer % 4) + 1;
                skipCount = 0;
            }
        }
        logEvent("It is now Player " + currentPlayer + "'s turn.");
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
}