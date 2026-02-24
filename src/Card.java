import processing.core.PImage;
import processing.core.PApplet;

public class Card extends ClickableRectangle {
    public String value;
    public String type;
    public PImage image;
    public String suit;
    public boolean turned = false;
    public boolean hovered = false;

    // Primary constructor
    public Card(String value, String type, PImage image){
        this.value = value;
        this.type = type;
        this.image = image;
    }

    // Optional secondary constructor
    public Card(String value, String suit){
        this.value = value;
        this.suit = suit;
    }

    // Draw the card
    @Override
    public void draw(PApplet sketch){
        float drawX = x;
        float drawY = y;
        float drawWidth = width;
        float drawHeight = height;

        if(hovered){
            drawWidth *= 1.4;
            drawHeight *= 1.4;
            drawX -= (drawWidth - width)/2;
            drawY -= (drawHeight - height)/2;
        }

        sketch.stroke(0);
        if(image != null){
            sketch.image(image, drawX, drawY, drawWidth, drawHeight);
        } else {
            sketch.fill(255, 100, 100);
            sketch.rect(drawX, drawY, drawWidth, drawHeight);
            sketch.fill(0);
            sketch.text(value, drawX + 10, drawY + 20);
        }
    }

    public boolean isMouseOver(float mx, float my){
        return mx >= x && mx <= x + width &&
               my >= y && my <= y + height;
    }

    // Set position
    public void setPosition(float x, float y, float w, float h){
        this.x = (int)x;
        this.y = (int)y;
        this.width = (int)w;
        this.height = (int)h;
    }
}