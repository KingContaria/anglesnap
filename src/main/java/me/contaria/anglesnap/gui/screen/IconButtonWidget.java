package me.contaria.anglesnap.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IconButtonWidget extends ButtonWidget {
    private Identifier texture;
    private int color = 0xFFFFFFFF;

    public IconButtonWidget(Text message, PressAction onPress, Identifier texture, int color) {
        this(message, onPress, texture);
        if (color != 0) {
            this.color = color;
        }

    }

    public IconButtonWidget(Text message, PressAction onPress, Identifier texture) {
        this(0, 0, 16, 16, message, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER, texture);
    }

    public IconButtonWidget(int x, int y, Text message, PressAction onPress, Identifier texture) {
        this(x, y, 16, 16, message, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER, texture);
    }

    protected IconButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier, Identifier texture) {
        super(x, y, width, height, message, onPress, narrationSupplier);
        this.texture = texture;
        this.setTooltip(Tooltip.of(message));
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(RenderLayer::getGuiTextured, this.texture, this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight(), 16, 16, color);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }
}
