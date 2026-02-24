import processing.core.PImage;
import processing.core.PApplet;

public class Card extends ClickableRectangle {
    String value;
    String suit;
    PImage img;
    boolean turned = false;
    public boolean hovered = false;

    private int clickableWidth = 30; // Width of the left sliver that is clickable
    private boolean selected = false;
    private int baseY;
    private boolean hasBaseY = false;

    Card(String value, String suit) {
        this.value = value;
        this.suit = suit;
    }

    Card(String value, String suit, PImage img) {
        this.value = value;
        this.suit = suit;
        this.img = img;
    }

    public void setTurned(boolean turned) {
        this.turned = turned;
    }

    public void setClickableWidth(int width) {
        this.clickableWidth = width;
    }

    public void setSelected(boolean selected, int raiseAmount) {
        if (selected && !this.selected) {
            baseY = y;
            hasBaseY = true;
            y = baseY - raiseAmount;
        } else if (!selected && this.selected && hasBaseY) {
            y = baseY;
        }
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public boolean isClicked(int mouseX, int mouseY) {
        // Only the left sliver of the card is clickable
        return mouseX >= x && mouseX <= x + clickableWidth &&
                mouseY >= y && mouseY <= y + height;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setPosition(float x, float y, float width, float height) {
    this.x = (int)x;
    this.y = (int)y;
    this.width = (int)width;
    this.height = (int)height;
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
    if (img != null) {
        sketch.image(img, drawX, drawY, drawWidth, drawHeight);
    } else {
        sketch.fill(255, 100, 100);
        sketch.rect(drawX, drawY, drawWidth, drawHeight);
        sketch.fill(0);
        sketch.text(value, drawX + 10, drawY + 20);
    }
    }

    public boolean isMouseOver(float mx, float my) {
    return mx >= x && mx <= x + width &&
           my >= y && my <= y + height;
}
}
