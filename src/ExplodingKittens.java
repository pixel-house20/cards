import processing.core.PImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ExplodingKittens extends CardGame {

    // Game State Variables
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

    public int currentPlayer = 1; // 1 = human, 2-4 = AI
    public int turnsRemaining = 1;

    public ExplodingKittens() {
        super();
    }

    public ExplodingKittens(HashMap<String, PImage> images) {
        super();
        this.images = images;
    }

    @Override
    public void createDeck() {
        // Add Exploding Kittens and Defuses (4 each)
        for (int i = 0; i < 4; i++) {
            deck.add(new Card("Explode", "Exploding", images.get("Explode")));
            deck.add(new Card("Defuse", "Defuse", images.get("Defuse")));
        }

        // Add Action Cards (4 of each type)
        String[] actions = {"Skip", "Shuffle", "SeeFuture", "Nope", "Favor", "Attack"};
        for (String action : actions) {
            for (int i = 0; i < 4; i++) {
                deck.add(new Card(action, "Action", images.get(action)));
            }
        }

        // Add Cat cards (5 of each type)
        String[] catTypes = {"Tacocat", "HairyPotato", "Cattermelon", "BeardCat", "RainbowCat"};
        for (String cat : catTypes) {
            for (int i = 0; i < 5; i++) {
                PImage img = images.get(cat);
                if (img == null) System.out.println("Image missing for: " + cat);
                deck.add(new Card(cat, "Cat", img));
            }
        }
    }

    @Override
    public void initializeGame() {
        super.initializeGame();

        // Separate Kittens and Defuses to deal fair hands
        ArrayList<Card> explodingCards = new ArrayList<>();
        ArrayList<Card> defuseCards = new ArrayList<>();
        for (int i = deck.size() - 1; i >= 0; i--) {
            Card c = deck.get(i);
            if (c.value.equals("Explode")) {
                explodingCards.add(deck.remove(i));
            } else if (c.value.equals("Defuse")) {
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

        // Give each player exactly one starting Defuse
        playerOneHand.add(defuseCards.remove(0));
        playerTwoHand.add(defuseCards.remove(0));
        playerThreeHand.add(defuseCards.remove(0));
        playerFourHand.add(defuseCards.remove(0));

        // Put remaining Defuses and all Kittens back and shuffle
        deck.addAll(defuseCards);
        deck.addAll(explodingCards);
        Collections.shuffle(deck);

        System.out.println("---- Game Initialized ----");
    }

    private boolean hasDefuse(List<Card> hand) {
        for (Card c : hand) {
            if (c.value.equals("Defuse")) return true;
        }
        return false;
    }

    private void removeDefuse(List<Card> hand) {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).value.equals("Defuse")) {
                hand.remove(i);
                return;
            }
        }
    }

    public void drawCard(List<Card> hand) {
        if (deck.isEmpty()) return;
        Card drawn = deck.remove(0);
        if (drawn.value.equals("Explode")) {
            if (hasDefuse(hand)) {
                removeDefuse(hand);
                System.out.println("Kitten drawn but DEFUSED!");
                deck.add(drawn);
                Collections.shuffle(deck);
            } else {
                System.out.println("BOOM! Exploding Kitten drawn.");
                hand.add(drawn); // Player is essentially out
            }
        } else {
            hand.add(drawn);
        }
    }

    public void resolvePendingAction() {
        if (!actionPending || pendingActionCard == null) return;

        nopeWindowOpen = false;
        if (actionCanceled) {
            System.out.println("Action was Noped and canceled!");
        } else {
            switch (pendingActionCard.value) {
                case "Skip":
                    System.out.println("Resolving Skip...");
                    skipCount = 1;
                    break;

                case "Shuffle":
                    System.out.println("Resolving Shuffle...");
                    Collections.shuffle(deck);
                    break;

                case "SeeFuture":
                    System.out.println("Resolving See the Future...");
                    futurePreview.clear();
                    for (int i = 0; i < Math.min(3, deck.size()); i++) {
                        futurePreview.add(deck.get(i));
                    }
                    break;

                case "Attack":
                    System.out.println("Resolving Attack...");
                    turnsRemaining = 0; // Ends current turn, forces next player to take 2
                    break;

                case "Favor":
                    System.out.println("Favor resolving...");
                    int targetPlayer = pendingPlayer + 1;
                    if (targetPlayer > 4) targetPlayer = 1;

                    List<Card> currentHand = getHand(pendingPlayer);
                    List<Card> targetHand = getHand(targetPlayer);

                    if (targetHand != null && !targetHand.isEmpty()) {
                        Collections.shuffle(targetHand);
                        Card stolenCard = targetHand.remove(0);
                        currentHand.add(stolenCard);
                        System.out.println("Player " + pendingPlayer + " stole " + stolenCard.value + " from Player " + targetPlayer);
                    } else {
                        System.out.println("Player " + targetPlayer + " has no cards to steal.");
                    }
                    break;
            }
        }

        // Reset Pending State
        actionPending = false;
        pendingActionCard = null;
        pendingPlayer = -1;
        actionCanceled = false;
    }

    // Helper to get hand list based on ID
    private List<Card> getHand(int playerID) {
        return switch (playerID) {
            case 1 -> playerOneHand;
            case 2 -> playerTwoHand;
            case 3 -> playerThreeHand;
            case 4 -> playerFourHand;
            default -> null;
        };
    }

    public boolean playCard(Card card, List<Card> hand) {
        // NOPE LOGIC
        if (card.value.equals("Nope")) {
            if (nopeWindowOpen && pendingActionCard != null) {
                System.out.println("Nope played! Action state toggled.");
                hand.remove(card);
                playPile.add(card);
                actionCanceled = !actionCanceled;
                return true;
            } else {
                System.out.println("Nothing to Nope right now.");
                return false;
            }
        }

        // CAT PAIR LOGIC
        if (card.type.equals("Cat")) {
            int count = 0;
            for (Card c : hand) {
                if (c.type.equals("Cat") && c.value.equals(card.value)) count++;
            }

            if (count >= 2) {
                int removed = 0;
                for (int i = hand.size() - 1; i >= 0 && removed < 2; i--) {
                    Card c = hand.get(i);
                    if (c.type.equals("Cat") && c.value.equals(card.value)) {
                        playPile.add(hand.remove(i));
                        removed++;
                    }
                }
                System.out.println("Played a pair of " + card.value + "s!");
                nextTurn();
                return true;
            } else {
                System.out.println("Need a pair to play Cats.");
                return false;
            }
        }

        // STANDARD ACTION CARD LOGIC
        if (card.type.equals("Action")) {
            hand.remove(card);
            playPile.add(card);
            pendingActionCard = card;
            pendingPlayer = currentPlayer;
            actionPending = true;
            nopeWindowOpen = true;
            actionCanceled = false;
            System.out.println(card.value + " played! Waiting for potential Nope...");
            return true;
        }

        return false;
    }

    public void nextTurn() {
        if (turnsRemaining > 0) {
            turnsRemaining--;
        }

        if (turnsRemaining <= 0) {
            currentPlayer++;
            if (currentPlayer > 4) currentPlayer = 1;

            if (pendingActionCard != null && pendingActionCard.value.equals("Attack")) {
                turnsRemaining = 2; 
                pendingActionCard = null;
            } else {
                turnsRemaining = 1;
            }

            if (skipCount > 0) {
                currentPlayer++;
                if (currentPlayer > 4) currentPlayer = 1;
                skipCount = 0;
            }
        }
        System.out.println("Turn Switch -> Player: " + currentPlayer + " | Turns Left: " + turnsRemaining);
    }

    public void printFullDeck() {
        System.out.println("Deck Size: " + deck.size());
    }

    private void printHand(String playerName, List<Card> hand) {
        System.out.print(playerName + ": ");
        for (Card c : hand) System.out.print(c.value + " ");
        System.out.println();
    }
}
