package mods.flammpfeil.slashblade.client.model.obj;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Face
{
    public static boolean isSmoothShade = true;
    public static int defaultColor = 0xFFFFFFFF;

    public static void setColor(int color){
        defaultColor = color;
    }
    public static void resetColor(){
        defaultColor = 0xFFFFFFFF;
    }

    public Vertex[] vertices;
    public Vertex[] vertexNormals;
    public Vertex faceNormal;
    public TextureCoordinate[] textureCoordinates;

    @SideOnly(Side.CLIENT)
    public void addFaceForRender(Tessellator tessellator)
    {
        addFaceForRender(tessellator, 0.0005F);
    }

    @SideOnly(Side.CLIENT)
    public void addFaceForRender(Tessellator tessellator, float textureOffset)
    {
        if (faceNormal == null)
        {
            faceNormal = this.calculateFaceNormal();
        }

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

        BufferBuilder wr = tessellator.getBuffer();

        for (int i = 0; i < vertices.length; ++i)
        {
            //todo : normal vertex expand
            wr.pos(vertices[i].x, vertices[i].y, vertices[i].z);

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


                wr.tex(textureCoordinates[i].u + offsetU, textureCoordinates[i].v + offsetV);
            }else{
                wr.tex(0, 0);
            }

            int r = defaultColor >> 16 & 255;
            int g = defaultColor >> 8 & 255;
            int b = defaultColor & 255;
            int a = defaultColor >> 24 & 255;
            wr.color(r,g,b,a);

            if(isSmoothShade && vertexNormals != null) {
                Vertex normal = vertexNormals[i];
                float scale = -1.05f;
                wr.normal(normal.x*scale, normal.y*scale, normal.z*scale);
            }else{

                wr.normal(faceNormal.x, faceNormal.y, faceNormal.z);
            }

            wr.endVertex();
        }
    }

    public Vertex calculateFaceNormal()
    {
        Vec3d v1 = new Vec3d(vertices[1].x - vertices[0].x, vertices[1].y - vertices[0].y, vertices[1].z - vertices[0].z);
        Vec3d v2 = new Vec3d(vertices[2].x - vertices[0].x, vertices[2].y - vertices[0].y, vertices[2].z - vertices[0].z);
        Vec3d normalVector = null;

        normalVector = v1.crossProduct(v2).normalize();

        return new Vertex((float) normalVector.x, (float) normalVector.y, (float) normalVector.z);
    }
}