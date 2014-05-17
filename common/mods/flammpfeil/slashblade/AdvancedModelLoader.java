package mods.flammpfeil.slashblade;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Resource;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.ModelFormatException;
import net.minecraftforge.client.model.obj.ObjModelLoader;

public class AdvancedModelLoader {

	static ObjModelLoader modelLoader = new ObjModelLoader();


	static public void closeableFinally(Stack<Closeable> closeStack) throws IOException{
		if(!closeStack.isEmpty()){
			try{
				Closeable ca = closeStack.pop();
				ca.close();
			}finally{
				closeableFinally(closeStack);
			}
		}
	}

	static public IModelCustom loadModel(ResourceLocation modelLoc){
		try{
			ResourceManager rm = Minecraft.getMinecraft().getResourceManager();

			File tmpFile = File.createTempFile("mcflammpfeil", ".obj");
			tmpFile.createNewFile();
			tmpFile.deleteOnExit();

			Resource res = rm.getResource(modelLoc);

			Stack<Closeable> closeStack = new Stack<Closeable>();

			InputStream ins = null;
			BufferedInputStream bif = null;
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			try{

				ins = res.getInputStream();
				closeStack.push(ins);
				bif = new BufferedInputStream(ins);
				closeStack.push(bif);

				fos = new FileOutputStream(tmpFile);
				closeStack.push(fos);

				bos = new BufferedOutputStream(fos);
				closeStack.push(bos);

			    byte [] buffer = new byte[1024];
			    while(true) {
			        int len = bif.read(buffer);
			        if(len < 0) {
			            break;
			        }
			        bos.write(buffer, 0, len);
			    }

			}finally{
				//auto closeable使いたいっ
				closeableFinally(closeStack);
			}
			String modelName = modelLoc.toString();
			return modelLoader.loadInstance(modelName, tmpFile.toURI().toURL());
		}catch(Throwable e){
			throw new ModelFormatException();
		}

	}

}
