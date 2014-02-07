package mods.flammpfeil.slashblade.asm;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class Transformer implements IClassTransformer , Opcodes
{
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        try
        {
            final String targetClassName = "net.minecraft.client.renderer.entity.RenderPlayer";
            if (targetClassName.equals(transformedName)) {
            	System.out.println("start transform SlashBlade > RenderPlayer");
                ClassReader classReader = new ClassReader(bytes);
                ClassWriter classWriter = new ClassWriter(1);
                classReader.accept(new RenderPlayerVisitor(targetClassName,classWriter), 8);
                return classWriter.toByteArray();
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("failed : scaffolding loading", e);
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

    	static final String targetMethodName = "func_76986_a"; //doRender
    	static final String targetMethodDesc = "(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V";

    	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
    	{

    		if (targetMethodName.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                && targetMethodDesc.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc)))
    		{
    			MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

    			//mv.visitCode();

    			mv.visitVarInsn(ALOAD, 1);
    			mv.visitVarInsn(ALOAD, 9);
    			mv.visitMethodInsn(INVOKESTATIC, "mods/flammpfeil/slashblade/ItemRendererBaseWeapon", "render", "(Lnet/minecraft/entity/player/EntityPlayer;F)V");

    			return mv;
    		}
    		return super.visitMethod(access, name, desc, signature, exceptions);
    	}
    }

}
