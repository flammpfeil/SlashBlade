package mods.flammpfeil.slashblade.asm;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions(value = {"mods.flammpfeil.slashblade.asm"})
public class CorePlugin implements IFMLLoadingPlugin, IFMLCallHook
{
    @Override
    public String[] getLibraryRequestClass()
    {
        return null;
    }

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[]
               {
                   "mods.flammpfeil.slashblade.asm.Transformer"
               };
    }

    @Override
    public String getModContainerClass()
    {
        return "mods.flammpfeil.slashblade.asm.ModContainer";
    }

    @Override
    public String getSetupClass()
    {
        return "mods.flammpfeil.slashblade.asm.CorePlugin";
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        if (data.containsKey("coremodLocation"))
        {
            location = (File) data.get("coremodLocation");
        }
    }

    @Override
    public Void call() throws Exception
    {
        return null;
    }

    public static File location;
}
