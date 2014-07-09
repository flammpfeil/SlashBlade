package mods.flammpfeil.slashblade;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Furia on 14/05/26.
 */
public abstract class TagPropertyAccessor<T extends Comparable> {
    protected final String tagName;
    public TagPropertyAccessor(String tagName){
        this.tagName = tagName;
    }

    public boolean exists(NBTTagCompound tag){
        return tag.hasKey(this.tagName);
    }

    public void remove(NBTTagCompound tag){
        if(exists(tag))
            tag.removeTag(this.tagName);
    }

    abstract public T get(NBTTagCompound tag);
    public T get(NBTTagCompound tag, T defaultValue){
        return this.exists(tag) ? get(tag) : defaultValue;
    }
    abstract public T set(NBTTagCompound tag, T value);

    static public class TagPropertyString extends TagPropertyAccessor<String>{

        public TagPropertyString(String tagName) {
            super(tagName);
        }

        @Override
        public String get(NBTTagCompound tag) {
            return tag.getString(this.tagName);
        }

        @Override
        public String set(NBTTagCompound tag, String value) {
            tag.setString(this.tagName,value);
            return value;
        }
    }

    static public class TagPropertyBoolean extends TagPropertyAccessor<Boolean>{
        public TagPropertyBoolean(String tagName){
            super(tagName);
        }

        @Override
        public Boolean get(NBTTagCompound tag){
            return tag.getBoolean(this.tagName);
        }

        @Override
        public Boolean set(NBTTagCompound tag, Boolean value) {
            tag.setBoolean(this.tagName, value);
            return value;
        }

        public Boolean invert(NBTTagCompound tag){
            return set(tag,!get(tag));
        }
    }

    static public class TagPropertyLong extends TagPropertyAccessor<Long>{
        public TagPropertyLong(String tagName){
            super(tagName);
        }

        @Override
        public Long get(NBTTagCompound tag){
            return tag.getLong(this.tagName);
        }

        @Override
        public Long set(NBTTagCompound tag, Long value) {
            tag.setLong(this.tagName, value);
            return value;
        }

        public Long add(NBTTagCompound tag, Long value) {
            Long result = get(tag) + value;
            return this.set(tag, result);
        }
    }

    static public class TagPropertyInteger extends TagPropertyAccessor<Integer>{
        public TagPropertyInteger(String tagName){
            super(tagName);
        }

        @Override
        public Integer get(NBTTagCompound tag){
            return tag.getInteger(this.tagName);
        }

        @Override
        public Integer set(NBTTagCompound tag, Integer value) {
            tag.setInteger(this.tagName, value);
            return value;
        }

        public Integer add(NBTTagCompound tag, Integer value) {
            Integer result = get(tag) + value;
            return this.set(tag,result);
        }
    }

    static public class TagPropertyIntegerWithRange extends TagPropertyInteger{
        private int min;
        private int max;
        public TagPropertyIntegerWithRange(String tagName, int min, int max) {
            super(tagName);
            this.min = min;
            this.max = max;
        }

        @Override
        public Integer set(NBTTagCompound tag, Integer value) {
            if(value < min)
                value = min;
            else if(max < value)
                value = max;
            return super.set(tag, value);
        }

        public boolean tryAdd(NBTTagCompound tag, Integer add, boolean forceAdd){
            int value = get(tag);

            value = value + add;

            boolean doSet = forceAdd || (min <= value && value <= max);

            if(doSet){
                set(tag,value);
            }

            return doSet;
        }
    }

    static public class TagPropertyFloat extends TagPropertyAccessor<Float>{
        public TagPropertyFloat(String tagName){
            super(tagName);
        }

        @Override
        public Float get(NBTTagCompound tag){
            return tag.getFloat(this.tagName);
        }

        @Override
        public Float set(NBTTagCompound tag, Float value) {
            tag.setFloat(this.tagName, value);
            return value;
        }

        public float get(NBTTagCompound tag,float defaultValue){
            return tag.hasKey(this.tagName) ? tag.getFloat(this.tagName) : defaultValue;
        }
    }
}
