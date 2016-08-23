package mods.flammpfeil.slashblade.stats;

import mods.flammpfeil.slashblade.gui.AchievementsExtendedGuiHandler;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import org.lwjgl.input.Mouse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Furia on 15/02/12.
 */
public class AchievementEx extends Achievement {
    String unlocalizedKey;
    public AchievementEx(String p_i45302_1_, String p_i45302_2_, int p_i45302_3_, int p_i45302_4_, ItemStack p_i45302_5_, Achievement p_i45302_6_) {
        super(p_i45302_1_, p_i45302_2_, p_i45302_3_, p_i45302_4_, p_i45302_5_, p_i45302_6_);
        unlocalizedKey = p_i45302_2_;
    }

    public Object content = null;

    @SideOnly(Side.CLIENT)
    @Override
    public String getDescription() {

        if(content != null && Mouse.isButtonDown(0) && !AchievementsExtendedGuiHandler.visible) {
            AchievementList.currentMouseOver = this;
        }

        return super.getDescription();
    }

    @Override
    public boolean getSpecial() {

        String posStr = I18n.translateToLocal("achievement." + unlocalizedKey + ".pos");

        Matcher mat = AchievementList.PosPattern.matcher(posStr.trim());
        if(mat.matches()){
            int x,y;
            x = Integer.parseInt(mat.group(1));
            //x = Math.max(minX,x);
            y = Integer.parseInt(mat.group(2));
            //y = Math.max(AchievementList.minY,y);

            if(x != this.displayColumn) {
                ReflectionHelper.setPrivateValue(Achievement.class, this, x, "displayColumn");
                //this.displayColumn = x;
            }
            if(y != this.displayRow) {
                ReflectionHelper.setPrivateValue(Achievement.class, this, y, "displayRow");
                //this.displayRow = y;
            }
        }

        return super.getSpecial();
    }
}
