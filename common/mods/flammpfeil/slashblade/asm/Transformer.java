package mods.flammpfeil.slashblade.asm;

import java.io.IOException;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
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
        	String pflmTarget2 = "PFLM_RenderPlayerAetherV160";
            String targetClassName = "net.minecraft.client.renderer.entity.RenderPlayer";

            if (targetClassName.equals(transformedName))
            {
                System.out.println("Start SlashBlade asm:" + transformedName);
                bytes = replaceClass(name, transformedName, bytes);
                System.out.println("Success SlashBlade asm:" + transformedName);
            }else if(name.equals(pflmTarget)){

                System.out.println("Start SlashBlade Custom asm:" + transformedName);
                bytes = replaceClass2(name, transformedName, bytes);
                System.out.println("Success SlashBlade Custom asm:" + transformedName);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("failed : slashblade.asm loading", e);
        }

        return bytes;
    }

    private byte[] replaceClass(String name, String transformedName, byte[] bytes) throws IOException
    {
    	// ASMで、bytesに格納されたクラスファイルを解析します。
        ClassNode cnode = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(cnode, 0);

		MethodNode mnode = null;

/*
        String targetMethod = "a"; //doRender
		String targetDesc = "(Lber;DDDFF)V";

		for (MethodNode curMnode : (List<MethodNode>) cnode.methods)
		{
		    if (targetMethod.equals(curMnode.name)
		            && targetDesc.equals(curMnode.desc))
		    {
		        mnode = curMnode;
		        break;
		    }
		}
*/

        String targetMethodName = "func_130009_a"; //doRender
		String targetMethodDesc = "(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V";

        for (MethodNode curMnode : (List<MethodNode>) cnode.methods)
        {
            if (targetMethodName.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(name, curMnode.name, curMnode.desc))
                    && targetMethodDesc.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(curMnode.desc)))
            {
                mnode = curMnode;
                break;
            }
        }


		if (mnode != null)
		{
			LabelNode label = new LabelNode();

			/**
			 * aload_1
			 * fload 9
			 * invokestatic "mods/flammpfeil/slashblade/ItemRendererBaseWeapon", "render", "(Lnet/minecraft/entity/player/EntityPlayer;F)V"
			 */
			InsnList overrideList = new InsnList();
		    overrideList.add(new VarInsnNode(ALOAD, 1));
		    overrideList.add(new VarInsnNode(FLOAD, 9));
		    overrideList.add(new MethodInsnNode(INVOKESTATIC, "mods/flammpfeil/slashblade/ItemRendererBaseWeapon", "render", "(Lnet/minecraft/entity/player/EntityPlayer;F)V"));

		    mnode.instructions.insert(overrideList);
            System.out.println("Success SlashBlade asm:" + transformedName);
		}

		// 改変したクラスファイルをバイト列に書き出します
		//0);//
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		cnode.accept(cw);
		bytes = cw.toByteArray();

        return bytes;
    }


    private byte[] replaceClass2(String name, String transformedName, byte[] bytes) throws IOException
    {
    	// ASMで、bytesに格納されたクラスファイルを解析します。
        ClassNode cnode = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(cnode, 0);

		MethodNode mnode = null;


		/*
        String targetMethod = "a"; //doRender
		String targetDesc = "(Lnm;DDDFF)V";

		for (MethodNode curMnode : (List<MethodNode>) cnode.methods)
		{
		    if (targetMethod.equals(curMnode.name)
		            && targetDesc.equals(curMnode.desc))
		    {
		        mnode = curMnode;
		        break;
		    }
		}
		*/

		String targetMethodName = "func_76986_a";
		String targetMethodDesc = "(Lnet/minecraft/entity/Entity;DDDFF)V";

        for (MethodNode curMnode : (List<MethodNode>) cnode.methods)
        {
            if (targetMethodName.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(name, curMnode.name, curMnode.desc))
                    && targetMethodDesc.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(curMnode.desc)))
            {
                mnode = curMnode;
                break;
            }
        }

		if (mnode != null)
		{
			LabelNode label = new LabelNode();

			/**
			 * aload_1
			 * fload 9
			 * invokestatic "mods/flammpfeil/slashblade/ItemRendererBaseWeapon", "render", "(Lnet/minecraft/entity/player/EntityPlayer;F)V"
			 */
			InsnList overrideList = new InsnList();
		    overrideList.add(new VarInsnNode(ALOAD, 1));
		    overrideList.add(new TypeInsnNode(CHECKCAST, "net/minecraft/client/entity/AbstractClientPlayer"));
		    overrideList.add(new VarInsnNode(FLOAD, 9));
		    overrideList.add(new MethodInsnNode(INVOKESTATIC, "mods/flammpfeil/slashblade/ItemRendererBaseWeapon", "render", "(Lnet/minecraft/entity/player/EntityPlayer;F)V"));

		    mnode.instructions.insert(overrideList);
            System.out.println("doTransform SlashBlade asm:" + transformedName);
		}

		// 改変したクラスファイルをバイト列に書き出します
		//0);//
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		cnode.accept(cw);
		bytes = cw.toByteArray();

        return bytes;
    }

}
