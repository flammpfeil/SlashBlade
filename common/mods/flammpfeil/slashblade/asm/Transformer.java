package mods.flammpfeil.slashblade.asm;

import java.io.IOException;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class Transformer implements IClassTransformer , Opcodes
{
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        try
        {
        	String pflmTarget = "PFLM_RenderPlayerV160";
            String targetClassName = "net.minecraft.client.renderer.entity.RenderPlayer";

            if (targetClassName.equals(transformedName))
            {
                System.out.println("Start SlashBlade asm:" + transformedName);
                ClassReader classReader = new ClassReader(bytes);
                ClassWriter classWriter = new ClassWriter(1);
                classReader.accept(new RenderPlayerVisitor(name,classWriter), 8);
                bytes = classWriter.toByteArray();
                System.out.println("Success SlashBlade asm:" + transformedName);
            }else if(name.equals(pflmTarget)){

                System.out.println("Start SlashBlade Custom asm:" + transformedName);
                ClassReader classReader = new ClassReader(bytes);
                ClassWriter classWriter = new ClassWriter(1);
                classReader.accept(new RenderPlayerVisitor(name,classWriter), 8);
                bytes = classWriter.toByteArray();
                System.out.println("Success SlashBlade Custom asm:" + transformedName);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("failed : slashblade.asm loading", e);
        }

        return bytes;
    }


    class RenderPlayerVisitor extends ClassVisitor
    {
    	String owner;
    	public RenderPlayerVisitor(String owner ,ClassVisitor cv)
    	{
    		super(Opcodes.ASM4,cv);
    		this.owner = owner;
    	}

    	static final String targetMethodName = "func_77029_c"; //renderEquippedItems
    	static final String targetMethodDesc = "(Lnet/minecraft/client/entity/EntityLivingBase;F)V";

    	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
    	{

    		if (targetMethodName.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                && targetMethodDesc.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc)))
    		{
    			MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

    			mv.visitCode();

    			mv.visitVarInsn(ALOAD, 1);
    			mv.visitVarInsn(ALOAD, 2);
    			mv.visitMethodInsn(INVOKESTATIC, "mods/flammpfeil/slashblade/ItemRendererBaseWeapon", "render", "(Lnet/minecraft/entity/EntityLivingBase;F)V");

    			mv.visitEnd();

    			return mv;
    		}
    		return super.visitMethod(access, name, desc, signature, exceptions);
    	}
    }
}
