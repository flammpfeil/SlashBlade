package mods.flammpfeil.slashblade;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.mapped.CacheUtil;

import java.nio.DoubleBuffer;

/**
 * Created by Furia on 14/10/28.
 */
public class ModelRendererProxy extends ModelRenderer {
    public DoubleBuffer buffer;
    public ModelRendererProxy(ModelBase p_i1173_1_,boolean createBuffer) {
        super(p_i1173_1_);
        if(createBuffer)
            buffer = CacheUtil.createDoubleBuffer(16);
    }

    @Override
    public int hashCode() {
        return 1857332014;
    }

    @Override
    public void render(float p_78785_1_) {
        if(buffer != null)
            GL11.glGetDouble(GL11.GL_MODELVIEW_MATRIX, buffer);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ModelRendererProxy;
    }
}
