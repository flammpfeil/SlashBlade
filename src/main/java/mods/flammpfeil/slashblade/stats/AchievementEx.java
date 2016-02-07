package mods.flammpfeil.slashblade.stats;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import org.lwjgl.input.Mouse;

/**
 * Created by Furia on 15/02/12.
 */
public class AchievementEx extends Achievement {
    public AchievementEx(String p_i45302_1_, String p_i45302_2_, int p_i45302_3_, int p_i45302_4_, ItemStack p_i45302_5_, Achievement p_i45302_6_) {
        super(p_i45302_1_, p_i45302_2_, p_i45302_3_, p_i45302_4_, p_i45302_5_, p_i45302_6_);
    }

    public Object content = null;

    @SideOnly(Side.CLIENT)
    @Override
    public String getDescription() {

        if(content != null && Mouse.isButtonDown(0)){
            AchievementList.currentMouseOver = this;
        }else{
            AchievementList.currentMouseOver = null;
        }

        return super.getDescription();
    }
}
