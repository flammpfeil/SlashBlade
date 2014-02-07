package mods.flammpfeil.slashblade;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBaseWeapon extends ModelBase {
	//fields
	 ModelRenderer Shape1;
	 ModelRenderer Shape2;

	public ModelBaseWeapon()
	{
	 textureWidth = 64;
	 textureHeight = 32;

	   Shape1 = new ModelRenderer(this, 0, 0);
	   Shape1.addBox(0F, 0F, 0F, 2, 24, 1);
	   Shape1.setRotationPoint(0F, 0F, 0F);
	   Shape1.setTextureSize(64, 32);
	   Shape1.mirror = true;
	   setRotation(Shape1, 0F, 0F, 0F);
	   Shape2 = new ModelRenderer(this, 0, 0);
	   Shape2.addBox(-1F, 6F, -1F, 4, 1, 3);
	   Shape2.setRotationPoint(0F, 0F, 0F);
	   Shape2.setTextureSize(64, 32);
	   Shape2.mirror = true;
	   setRotation(Shape2, 0F, 0F, 0F);
	}

	public boolean isBroken = false;
	public boolean isEntity = false;

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
	 super.render(entity, f, f1, f2, f3, f4, f5);
	 setRotationAngles(f, f1, f2, f3, f4, f5);
	 Shape2.render(f5);
	 if(!isBroken){
		 Shape1.render(f5);
	 }else{
		 GL11.glPushMatrix();
		 GL11.glScalef(1.0f,0.5f,1.0f);
		 Shape1.render(f5);
		 GL11.glPopMatrix();

		 if(isEntity){
			 GL11.glPushMatrix();
			 GL11.glTranslated(-0, 15, 0);
			 GL11.glRotatef(70, 0, 0, -1);
			 GL11.glScalef(1.0f,0.5f,1.0f);
			 Shape1.render(f5);
			 GL11.glPopMatrix();
		 }
	 }
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
	 model.rotateAngleX = x;
	 model.rotateAngleY = y;
	 model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
	{
	}

}

