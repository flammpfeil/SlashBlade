package mods.flammpfeil.slashblade.client.model.obj;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.model.obj.Face;
import org.lwjgl.opengl.GL11;

/**
 * Created by Furia on 14/09/08.
 */
public class FaceEx extends Face {

    public static boolean isSmoothShade = true;

    boolean hasVertexNormals = false;

    public FaceEx(Face face){

        this.vertices = face.vertices;
        this.vertexNormals = face.vertexNormals;
        this.faceNormal = face.faceNormal;
        this.textureCoordinates = face.textureCoordinates;

        this.hasVertexNormals = this.vertexNormals != null;
    }


    @SideOnly(Side.CLIENT)
    public void addFaceForRender(Tessellator tessellator, float textureOffset)
    {
        if(!isSmoothShade || !hasVertexNormals)
            super.addFaceForRender(tessellator,textureOffset);

        float averageU = 0F;
        float averageV = 0F;

        if ((textureCoordinates != null) && (textureCoordinates.length > 0))
        {
            for (int i = 0; i < textureCoordinates.length; ++i)
            {
                averageU += textureCoordinates[i].u;
                averageV += textureCoordinates[i].v;
            }

            averageU = averageU / textureCoordinates.length;
            averageV = averageV / textureCoordinates.length;
        }

        float offsetU, offsetV;

        for (int i = 0; i < vertices.length; ++i)
        {
            tessellator.setNormal(vertexNormals[i].x, vertexNormals[i].y, vertexNormals[i].z);

            if ((textureCoordinates != null) && (textureCoordinates.length > 0))
            {
                offsetU = textureOffset;
                offsetV = textureOffset;

                if (textureCoordinates[i].u > averageU)
                {
                    offsetU = -offsetU;
                }
                if (textureCoordinates[i].v > averageV)
                {
                    offsetV = -offsetV;
                }

                tessellator.addVertexWithUV(vertices[i].x, vertices[i].y, vertices[i].z, textureCoordinates[i].u + offsetU, textureCoordinates[i].v + offsetV);
            }
            else
            {
                tessellator.addVertex(vertices[i].x, vertices[i].y, vertices[i].z);
            }
        }
    }
}
