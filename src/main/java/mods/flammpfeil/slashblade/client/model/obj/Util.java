package mods.flammpfeil.slashblade.client.model.obj;

import com.google.common.collect.Lists;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;

import java.util.ArrayList;

/**
 * Created by Furia on 14/09/08.
 */
public class Util {
    public static void replaceFace(WavefrontObject obj){
        for(GroupObject group : obj.groupObjects){
            ArrayList<Face> newFaces = Lists.newArrayList();
            for(Face face : group.faces){
                newFaces.add(new FaceEx(face));
            }
            group.faces = newFaces;
        }
    }
}
