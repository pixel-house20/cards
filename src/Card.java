import processing.core.PApplet;
import processing.core.PImage;

public class Card extends ClickableRectangle {
    public String value;   // e.g., "Skip", "Tacocat"
    public String type;    // "Action", "Cat", "Exploding", "Defuse"
    public PImage image;
    public boolean hovered = false;
    private boolean selected = false;
    private int baseY;
    private boolean hasBaseY = false;

    public Card(String value, String type, PImage image){
        this.value = value;
        this.type = type;
        this.image = image;
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
}