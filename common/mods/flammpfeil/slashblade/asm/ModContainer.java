package mods.flammpfeil.slashblade.asm;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.versioning.ArtifactVersion;
public class ModContainer extends DummyModContainer
{
    public ModContainer()
    {
        super(new ModMetadata());
        getMetadata();
    }

    @Override
    public List<ArtifactVersion> getDependencies()
    {
        return super.getDependencies();
    }

    @Override
    public ModMetadata getMetadata()
    {
        ModMetadata meta = super.getMetadata();
        meta.modId       = "flammpfeil.slashblade.asm";
        meta.name        = "SlashBladeAsm";
        meta.version     = "@VERSION@";
        meta.authorList  = Arrays.asList("Ferne");
        meta.description = "";
        meta.url         = "";
        meta.credits     = "Furia";
        return meta;
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        bus.register(this);
        return true;
    }


    @Subscribe
    public void preInit(FMLPreInitializationEvent event)
    {
    }

    @Subscribe
    public void Init(FMLInitializationEvent event)
    {
    }

    @Subscribe
    public void postInit(FMLPostInitializationEvent event)
    {
    }

    /*
    @Override
    public Class<?> getCustomResourcePackClass()
    {
        try
        {
            return Class.forName("cpw.mods.fml.client.FMLFileResourcePack", true, getClass().getClassLoader());
        }
        catch (ClassNotFoundException e)
        {
        }

        return null;
    }
    */

    @Override
    public File getSource()
    {
        return CorePlugin.location;
    }
}
