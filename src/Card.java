import processing.core.PApplet;
import processing.core.PImage;

public class Card extends ClickableRectangle {
    public String value;   // e.g., "Skip", "Tacocat"
    public String type;    // "Action", "Cat", "Exploding", "Defuse"
    public String suit;    // legacy support for CardGame debug output
    public PImage image;
    public boolean hovered = false;
    private boolean selected = false;
    private boolean turned = false;
    private int baseY;
    private boolean hasBaseY = false;

    public Card(String value, String type, PImage image){
        this.value = value;
        this.type = type;
        this.suit = type;
        this.image = image;
    }

    // Legacy constructor used by CardGame's generic deck implementation.
    public Card(String value, String suit) {
        this.value = value;
        this.suit = suit;
        this.type = suit;
        this.image = null;
    }

    // Make setSelected public so App can call it
    public void setSelected(boolean selected, int raiseAmount) {
        if (selected && !this.selected) {
            baseY = y;
            hasBaseY = true;
            y = baseY - raiseAmount; // visually raise card
        } else if (!selected && this.selected && hasBaseY) {
            y = baseY; // reset position
        }
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setTurned(boolean turned) {
        this.turned = turned;
    }

    public boolean isMouseOver(float mx, float my) {
        return mx >= x && mx <= x + width &&
               my >= y && my <= y + height;
    }

    @Override
    public void draw(PApplet sketch) {
        float drawX = x;
        float drawY = y;
        float drawWidth = width;
        float drawHeight = height;

        if (hovered) {
            drawWidth *= 1.4;
            drawHeight *= 1.4;
            drawX -= (drawWidth - width) / 2;
            drawY -= (drawHeight - height) / 2;
        }

        if (turned) {
            sketch.fill(70, 70, 90);
            sketch.rect(drawX, drawY, drawWidth, drawHeight);
            sketch.fill(255);
            sketch.textAlign(PApplet.CENTER, PApplet.CENTER);
            sketch.text("CARD", drawX + drawWidth / 2, drawY + drawHeight / 2);
            return;
        }

        sketch.stroke(0);
        if (image != null) {
            sketch.image(image, drawX, drawY, drawWidth, drawHeight);
        } else {
            sketch.fill(255, 100, 100);
            sketch.rect(drawX, drawY, drawWidth, drawHeight);
            sketch.fill(0);
            sketch.text(value, drawX + 10, drawY + 20);
        }
    }

    public void setPosition(float x, float y, float width, float height) {
    this.x = (int) x;
    this.y = (int) y;
    this.width = (int) width;
    this.height = (int) height;
}

public int getStrategicValue() {

    switch(this.type) {

        case "Exploding":
            return -100;   // never playable

        case "Defuse":
            return 5;      // keep, rarely play

        case "Cat":
            return 1;      // weak unless paired

        case "Action":
            switch(this.value) {
                case "Attack": return 6;
                case "Skip": return 5;
                case "Shuffle": return 3;
                case "SeeFuture": return 4;
                case "Favor": return 4;
                case "Nope": return 5;
            }
    }

    return 0;
}
}
