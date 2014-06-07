package mods.flammpfeil.slashblade.asm;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.commons.LocalVariablesSorter;

public class Transformer implements IClassTransformer , Opcodes
{
    static final boolean isRelease = /*@isrelease@*/ false;

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
                classReader.accept(new RenderPlayerVisitor(name,classWriter), 8);
                return classWriter.toByteArray();
            }else{
                final String targetClassNamePflm = "modchu.lib.characteristic.Modchu_RenderPlayerDummy";
                if (targetClassNamePflm.equals(transformedName)) {
                    System.out.println("start transform SlashBlade > PFLMRenderPlayer");
                    ClassReader classReader = new ClassReader(bytes);
                    ClassWriter classWriter = new ClassWriter(1);
                    classReader.accept(new PFLMRenderPlayerVisitor(name,classWriter), 8);
                    return classWriter.toByteArray();
                }else{

                    final String targetClassNamePflm2 = "modchu.lib.characteristic.Modchu_RenderPlayer";
                    if (targetClassNamePflm2.equals(transformedName)) {
                        System.out.println("start transform SlashBlade > PFLMRenderPlayer2");
                        ClassReader classReader = new ClassReader(bytes);
                        ClassWriter classWriter = new ClassWriter(1);
                        classReader.accept(new PFLMRenderPlayerVisitor(name,classWriter), 8);
                        return classWriter.toByteArray();
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("failed : scaffolding loading", e);
        }

        return bytes;
    }

    static class RenderPlayerVisitor extends ClassVisitor
    {
        String owner;
        public RenderPlayerVisitor(String owner ,ClassVisitor cv)
        {
            super(Opcodes.ASM4,cv);
            this.owner = owner;
        }

        static final String targetMethodName = isRelease ? "func_77029_c" : "renderEquippedItems";
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

    class PFLMRenderPlayerVisitor extends ClassVisitor
    {
        String owner;
        public PFLMRenderPlayerVisitor(String owner ,ClassVisitor cv)
        {
            super(Opcodes.ASM4,cv);
            this.owner = owner;
        }


        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
        {
/*
            {

                final String targetMethodName = "func_76986_a"; //
                final String targetMethodDesc = "(Lnet/minecraft/client/Entity;DDDFF)V";
                if (targetMethodName.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                        && targetMethodDesc.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc)))
                {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                    mv = new AddCustomCallVisitor(access,desc,mv);

                    return mv;
                }
            }
            *//*
            {

                final String targetMethodName = "func_77029_c"; //
                final String targetMethodDesc = "(Lnet/minecraft/client/EntityLivingBase;F)V";
                if (targetMethodName.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                        && targetMethodDesc.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc)))
                {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                    mv = new AddCustomCallVisitor2(access,desc,mv);

                    return mv;
                }
            }
            */
            {

                final String targetMethodName = isRelease ? "func_77029_c" : "renderEquippedItems";
                final String targetMethodDesc = "(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V";
                if (targetMethodName.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                        && targetMethodDesc.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc)))
                {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                    mv = new AddCustomCallVisitor2(access,desc,mv);

                    return mv;
                }
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        private class AddCustomCallVisitor extends LocalVariablesSorter{

            public AddCustomCallVisitor(int access, String desc, MethodVisitor mv) {
                super(access, desc, mv);
            }

            @Override
            public void visitCode() {
                super.visitCode();

                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(FLOAD, 5);
                mv.visitMethodInsn(INVOKESTATIC, "mods/flammpfeil/slashblade/ItemRendererBaseWeapon", "renderPFLM", "(Ljava/lang/Object;F)V");
            }
        }

        private class AddCustomCallVisitor2 extends LocalVariablesSorter{

            public AddCustomCallVisitor2(int access, String desc, MethodVisitor mv) {
                super(access, desc, mv);
            }

            @Override
            public void visitCode() {
                super.visitCode();

                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(FLOAD, 2);
                mv.visitMethodInsn(INVOKESTATIC, "mods/flammpfeil/slashblade/ItemRendererBaseWeapon", "renderPFLM", "(Ljava/lang/Object;F)V");
            }
        }
    }

}
