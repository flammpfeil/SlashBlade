package mods.flammpfeil.slashblade.util;


import net.minecraft.util.ResourceLocation;

/**
 * Created by Furia on 2016/11/17.
 */
public class ResourceLocationRaw extends ResourceLocation {
    protected final String resourcePathRaw;

    public ResourceLocationRaw(String resourceDomainIn, String resourcePathIn) {
        super(resourceDomainIn, resourcePathIn);

        resourcePathRaw = resourcePathIn;
    }
    public ResourceLocationRaw(String[] resourceName) {
        this(resourceName[0],resourceName[1]);
    }
    public ResourceLocationRaw(String resourceName) {
        this(splitObjectName(resourceName));
    }

    @Override
    public String getResourcePath() {
        return resourcePathRaw;
    }

    @Override
    public String toString()
    {
        return this.resourceDomain + ':' + this.resourcePath;
    }
}
