package mods.flammpfeil.slashblade.client.model.obj;

import java.util.ArrayList;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GroupObject
{
    public String name;
    public ArrayList<Face> faces = new ArrayList<Face>();
    public int glDrawingMode;

    public GroupObject()
    {
        this("");
    }

    public GroupObject(String name)
    {
        this(name, -1);
    }

    public GroupObject(String name, int glDrawingMode)
    {
        this.name = name;
        this.glDrawingMode = glDrawingMode;
    }

    @SideOnly(Side.CLIENT)
    public void render()
    {
        if (faces.size() > 0)
        {
            Tessellator tessellator = Tessellator.getInstance();
            tessellator.getBuffer().begin(glDrawingMode, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
            render(tessellator);
            tessellator.draw();
        }
    }

    @SideOnly(Side.CLIENT)
    public void render(Tessellator tessellator)
    {
    	Face face;
        if (faces.size() > 0)
        {
        	for(int i = 0,j=faces.size();i<j;i++){
        	face=faces.get(i);
            face.addFaceForRender(tessellator,glDrawingMode);
        	}
        }
    }
}
            for (Face face : faces)
            {
                face.addFaceForRender(tessellator);
            }
        }
    }
}
