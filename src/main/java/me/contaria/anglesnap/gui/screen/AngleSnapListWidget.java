package me.contaria.anglesnap.gui.screen;

import me.contaria.anglesnap.AngleEntry;
import me.contaria.anglesnap.AngleSnap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AngleSnapListWidget extends ElementListWidget<AngleSnapListWidget.AbstractEntry> {
    private static final Text NAME_TEXT = Text.translatable("anglesnap.gui.screen.name");
    private static final Text YAW_TEXT = Text.translatable("anglesnap.gui.screen.yaw");
    private static final Text PITCH_TEXT = Text.translatable("anglesnap.gui.screen.pitch");
    private static final Text ICON_TEXT = Text.translatable("anglesnap.gui.screen.icon");
    private static final Text COLOR_TEXT = Text.translatable("anglesnap.gui.screen.color");
    private static final Text ADD_TEXT = Text.translatable("anglesnap.gui.screen.add");
    private static final Text DELETE_TEXT = Text.translatable("anglesnap.gui.screen.delete");
    private static final Text EDIT_TEXT = Text.translatable("anglesnap.gui.screen.edit");
    private static final Text SAVE_TEXT = Text.translatable("anglesnap.gui.screen.save");

    private static final Identifier ADD_TEXTURE = Identifier.of("anglesnap", "textures/gui/add.png");
    private static final Identifier DELETE_TEXTURE = Identifier.of("anglesnap", "textures/gui/delete.png");
    private static final Identifier EDIT_TEXTURE = Identifier.of("anglesnap", "textures/gui/edit.png");
    private static final Identifier SAVE_TEXTURE = Identifier.of("anglesnap", "textures/gui/save.png");

    private static final int HOVERED_COLOR = ColorHelper.getArgb(100, 200, 200, 200);

    private final AngleSnapScreen parent;

    public AngleSnapListWidget(MinecraftClient minecraftClient, int width, int height, int y, AngleSnapScreen parent) {
        super(minecraftClient, width, height, y, 20, 25);
        this.parent = parent;

        for (AngleEntry angle : AngleSnap.CONFIG.getAngles()) {
            this.addEntry(new Entry(angle));
        }
        this.addEntry(new AddAngleEntry());
    }

    @Override
    public int getRowLeft() {
        return 6;
    }

    @Override
    public int getRowWidth() {
        return this.width - 12;
    }

    @Override
    protected int getScrollbarX() {
        return this.width - 6;
    }

    @Override
    protected void renderHeader(DrawContext context, int x, int y) {
        TextRenderer textRenderer = this.client.textRenderer;
        context.drawText(textRenderer, NAME_TEXT, x + 5, y + (20 - textRenderer.fontHeight) / 2, Colors.WHITE, true);
        context.drawText(textRenderer, YAW_TEXT, x + 5 + 5 * this.getRowWidth() / 17, y + (20 - textRenderer.fontHeight) / 2, Colors.WHITE, true);
        context.drawText(textRenderer, PITCH_TEXT, x + 5 + 7 * this.getRowWidth() / 17, y + (20 - textRenderer.fontHeight) / 2, Colors.WHITE, true);
        context.drawText(textRenderer, ICON_TEXT, x + 5 + 9 * this.getRowWidth() / 17, y + (20 - textRenderer.fontHeight) / 2, Colors.WHITE, true);
        context.drawText(textRenderer, COLOR_TEXT, x + 5 + 11 * this.getRowWidth() / 17, y + (20 - textRenderer.fontHeight) / 2, Colors.WHITE, true);
    }

    public abstract static class AbstractEntry extends ElementListWidget.Entry<AbstractEntry> {
        protected final MinecraftClient client;
        protected final List<ClickableWidget> children;

        protected AbstractEntry() {
            this.client = MinecraftClient.getInstance();
            this.children = new ArrayList<>();
        }

        protected <T extends ClickableWidget> T addChild(T widget) {
            this.children.add(widget);
            return widget;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return this.children;
        }

        @Override
        public List<? extends Element> children() {
            return this.children;
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            if (!focused) {
                this.setFocused(null);
            }
        }

        protected void renderWidgetAt(DrawContext context, int mouseX, int mouseY, float tickDelta, ClickableWidget widget, int x, int y) {
            widget.setX(x);
            widget.setY(y);
            widget.render(context, mouseX, mouseY, tickDelta);
        }
    }

    public class Entry extends AbstractEntry {
        private final AngleEntry angle;

        private final TextFieldWidget name;
        private final TextFieldWidget yaw;
        private final TextFieldWidget pitch;
        private final IconButtonWidget icon;
        private final TextFieldWidget color;
        private final IconButtonWidget edit;
        private final IconButtonWidget save;
        private final IconButtonWidget delete;

        private boolean editing;

        public Entry() {
            this(AngleSnap.CONFIG.createAngle());
            this.setEditing(true);
            this.setFocused(this.name);
        }

        public Entry(AngleEntry angle) {
            this.angle = angle;

            this.name = this.addChild(new TextFieldWidget(
                    this.client.textRenderer,
                    5 * AngleSnapListWidget.this.getRowWidth() / 17 - 5,
                    20,
                    NAME_TEXT
            ));
            this.name.setText(this.angle.name);
            this.name.setChangedListener(name -> this.angle.name = name);
            this.name.setDrawsBackground(false);
            this.name.setEditableColor(Colors.WHITE);
            this.name.setUneditableColor(Colors.WHITE);

            this.yaw = this.addChild(new TextFieldWidget(
                    this.client.textRenderer,
                    2 * AngleSnapListWidget.this.getRowWidth() / 17 - 5,
                    20,
                    YAW_TEXT
            ));
            this.yaw.setText(String.valueOf(this.angle.yaw));
            this.yaw.setChangedListener(yaw -> {
                try {
                    this.angle.yaw = Float.parseFloat(yaw);
                } catch (NumberFormatException e) {
                    this.angle.yaw = 0.0f;
                }
            });
            this.yaw.setDrawsBackground(false);
            this.yaw.setEditableColor(Colors.WHITE);
            this.yaw.setUneditableColor(Colors.WHITE);

            this.pitch = this.addChild(new TextFieldWidget(
                    this.client.textRenderer,
                    2 * AngleSnapListWidget.this.getRowWidth() / 17 - 5,
                    20,
                    PITCH_TEXT
            ));
            this.pitch.setText(String.valueOf(this.angle.pitch));
            this.pitch.setChangedListener(pitch -> {
                try {
                    this.angle.pitch = Float.parseFloat(pitch);
                } catch (NumberFormatException e) {
                    this.angle.pitch = 0.0f;
                }
            });
            this.pitch.setDrawsBackground(false);
            this.pitch.setEditableColor(Colors.WHITE);
            this.pitch.setUneditableColor(Colors.WHITE);

            this.icon = this.addChild(new IconButtonWidget(Text.empty(), button -> ((IconButtonWidget) button).setTexture(this.angle.nextIcon()), this.angle.getIcon(), this.angle.color));

            this.color = this.addChild(new TextFieldWidget(
                    this.client.textRenderer,
                    3 * AngleSnapListWidget.this.getRowWidth() / 17 - 5,
                    20,
                    PITCH_TEXT
            ));
            this.color.setText(colorToString(this.angle.color));
            this.color.setChangedListener(color -> this.angle.color = this.parseColor(color));
            this.color.setTextPredicate(color -> color.length() <= (color.startsWith("#") ? 9 : 8));
            this.color.setDrawsBackground(false);
            this.color.setEditableColor(Colors.WHITE);
            this.color.setUneditableColor(Colors.WHITE);

            this.edit = this.addChild(new IconButtonWidget(EDIT_TEXT, button -> this.toggleEditing(), EDIT_TEXTURE));
            this.save = this.addChild(new IconButtonWidget(SAVE_TEXT, button -> this.toggleEditing(), SAVE_TEXTURE));
            this.delete = this.addChild(new IconButtonWidget(DELETE_TEXT, button -> this.delete(), DELETE_TEXTURE));

            this.setEditing(false);
        }

        private String colorToString(int color) {
            return String.format("#%08X", Integer.rotateLeft(color, 8));
        }

        private int parseColor(String color) {
            if (color.startsWith("#")) {
                color = color.substring(1);
            }
            color = color.toLowerCase(Locale.ROOT);
            try {
                // pad RGB bits with 0's and alpha bit with f's
                // this means an RGB input as generated by some websites will also work
                String hex = StringUtils.rightPad(StringUtils.leftPad(color, 6, '0'), 8, 'f');
                return Integer.rotateRight(Integer.parseUnsignedInt(hex, 16), 8);
            } catch (NumberFormatException e) {
                return Colors.WHITE;
            }
        }

        private void toggleEditing() {
            this.setEditing(!this.editing);
        }

        private void setEditing(boolean editing) {
            this.editing = editing;

            this.setEditing(this.name, editing);
            this.setEditing(this.yaw, editing);
            this.setEditing(this.pitch, editing);
            this.setEditing(this.icon, editing);
            this.setEditing(this.color, editing);

            if (!editing) {
                this.yaw.setText(String.valueOf(this.angle.yaw));
                this.pitch.setText(String.valueOf(this.angle.pitch));
                this.color.setText(this.colorToString(this.angle.color));
                this.icon.setColor(this.angle.color);
            }

            this.edit.visible = !editing;
            this.save.visible = editing;
        }

        private void setEditing(ClickableWidget widget, boolean editing) {
            widget.active = editing;
        }

        private void setEditing(TextFieldWidget widget, boolean editing) {
            widget.active = editing;
            widget.setEditable(editing);
            widget.setFocused(false);
            widget.setFocusUnlocked(editing);
            widget.setCursorToStart(false);
        }

        private void delete() {
            AngleSnap.CONFIG.removeAngle(this.angle);
            AngleSnapListWidget.this.removeEntry(this);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if (hovered) {
                context.fill(x, y, x + entryWidth, y + entryHeight, HOVERED_COLOR);
            }
            int textY = y + (entryHeight - this.client.textRenderer.fontHeight + 1) / 2;
            this.renderTextWidgetAt(context, mouseX, mouseY, tickDelta, this.name, x + 5, textY);
            this.renderNumberWidgetAt(context, mouseX, mouseY, tickDelta, this.yaw, x + 5 + 5 * entryWidth / 17, textY);
            this.renderNumberWidgetAt(context, mouseX, mouseY, tickDelta, this.pitch, x + 5 + 7 * entryWidth / 17, textY);
            this.renderWidgetAt(context, mouseX, mouseY, tickDelta, this.icon, x + 5 + 9 * entryWidth / 17, y);
            this.renderHexadecimalWidgetAt(context, mouseX, mouseY, tickDelta, this.color, x + 5 + 11 * entryWidth / 17, textY);
            this.renderWidgetAt(context, mouseX, mouseY, tickDelta, this.edit, x + entryWidth - 5 - 40, y);
            this.renderWidgetAt(context, mouseX, mouseY, tickDelta, this.save, x + entryWidth - 5 - 40, y);
            this.renderWidgetAt(context, mouseX, mouseY, tickDelta, this.delete, x + entryWidth - 5 - 20, y);
        }

        private void renderTextWidgetAt(DrawContext context, int mouseX, int mouseY, float tickDelta, TextFieldWidget widget, int x, int y) {
            if (this.editing) {
                this.renderWidgetAt(context, mouseX, mouseY, tickDelta, widget, x, y);
            } else {
                context.drawText(this.client.textRenderer, widget.getText(), x, y, Colors.WHITE, true);
            }
        }

        private void renderNumberWidgetAt(DrawContext context, int mouseX, int mouseY, float tickDelta, TextFieldWidget widget, int x, int y) {
            if (this.isNumberOrEmpty(widget.getText())) {
                this.renderTextWidgetAt(context, mouseX, mouseY, tickDelta, widget, x, y);
            } else {
                widget.setEditableColor(Colors.LIGHT_RED);
                widget.setUneditableColor(Colors.LIGHT_RED);
                this.renderTextWidgetAt(context, mouseX, mouseY, tickDelta, widget, x, y);
                widget.setEditableColor(Colors.WHITE);
                widget.setUneditableColor(Colors.WHITE);
            }
        }

        private boolean isNumberOrEmpty(String text) {
            if (text.isEmpty()) {
                return true;
            }
            try {
                Float.parseFloat(text);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        private void renderHexadecimalWidgetAt(DrawContext context, int mouseX, int mouseY, float tickDelta, TextFieldWidget widget, int x, int y) {
            if (this.isHexadecimalOrEmpty(widget.getText())) {
                this.renderTextWidgetAt(context, mouseX, mouseY, tickDelta, widget, x, y);
            } else {
                widget.setEditableColor(Colors.LIGHT_RED);
                widget.setUneditableColor(Colors.LIGHT_RED);
                this.renderTextWidgetAt(context, mouseX, mouseY, tickDelta, widget, x, y);
                widget.setEditableColor(Colors.WHITE);
                widget.setUneditableColor(Colors.WHITE);
            }
        }

        private boolean isHexadecimalOrEmpty(String text) {
            if (text.startsWith("#")) {
                text = text.substring(1);
            }
            if (text.isEmpty()) {
                return true;
            }
            text = text.toLowerCase(Locale.ROOT);
            try {
                // noinspection ResultOfMethodCallIgnored
                Integer.parseUnsignedInt(text, 16);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (super.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            if (!this.editing && button == 0) {
                AngleSnapListWidget.this.parent.snap(this.angle);
                return true;
            }
            return false;
        }
    }

    public class AddAngleEntry extends AbstractEntry {
        private final ButtonWidget add;

        public AddAngleEntry() {
            this.add = this.addChild(new IconButtonWidget(ADD_TEXT, button -> this.add(), ADD_TEXTURE));
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.renderWidgetAt(context, mouseX, mouseY, tickDelta, this.add, x + 5, y + (entryHeight - this.client.textRenderer.fontHeight) / 2);
        }

        private void add() {
            Entry entry = new Entry();
            AngleSnapListWidget.this.removeEntry(this);
            AngleSnapListWidget.this.addEntry(entry);
            AngleSnapListWidget.this.addEntry(this);
            AngleSnapListWidget.this.setFocused(entry);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            super.mouseClicked(mouseX, mouseY, button);
            return false;
        }
    }
}
