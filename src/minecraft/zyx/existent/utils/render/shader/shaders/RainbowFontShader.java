package zyx.existent.utils.render.shader.shaders;

import org.lwjgl.opengl.GL20;
import zyx.existent.utils.render.shader.Shader;

import java.io.Closeable;

public class RainbowFontShader extends Shader implements Closeable {
    public boolean isInUse;
    private float strengthX;
    private float strengthY;
    private float offset;

    public static final RainbowFontShader INSTANCE = new RainbowFontShader();

    public final boolean isInUse() {
        return this.isInUse;
    }

    public final float getStrengthX() {
        return this.strengthX;
    }

    public final void setStrengthX(float var1) {
        this.strengthX = var1;
    }

    public final float getStrengthY() {
        return this.strengthY;
    }

    public final void setStrengthY(float var1) {
        this.strengthY = var1;
    }

    public final float getOffset() {
        return this.offset;
    }

    public final void setOffset(float var1) {
        this.offset = var1;
    }

    public void setupUniforms() {
        this.setupUniform("offset");
        this.setupUniform("strength");
    }

    public void updateUniforms() {
        GL20.glUniform2f(this.getUniform("strength"), this.strengthX, this.strengthY);
        GL20.glUniform1f(this.getUniform("offset"), this.offset);
    }

    public void startShader() {
        super.startShader();
        this.isInUse = true;
    }

    public void stopShader() {
        super.stopShader();
        this.isInUse = false;
    }

    public void close() {
        if (this.isInUse) {
            this.stopShader();
        }
    }

    public RainbowFontShader() {
        super("rainbow_font_shader.frag");
    }

    public static final class Companion {
        public final RainbowFontShader begin(boolean enable, float x, float y, float offset) {
            RainbowFontShader instance = RainbowFontShader.INSTANCE;
            if (enable) {
                instance.setStrengthX(x);
                instance.setStrengthY(y);
                instance.setOffset(offset);
                instance.startShader();
            }
            return instance;
        }
    }
}
