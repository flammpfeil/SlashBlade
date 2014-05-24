package mods.flammpfeil.slashblade;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.RegistryNamespaced;
import net.minecraftforge.event.world.WorldEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Furia on 14/05/22.
 */
public class StatManager {

    private static Map<String,StatBase> registerdStats = Maps.newHashMap();

    public class StatTypeUseItem extends StatTypeHasViewList<Item>{

        public StatTypeUseItem(StatBase[] stats,RegistryNamespaced registry, String keyBase, List viewlist){
            super(stats,registry,keyBase,viewlist);
        }

        @Override
        protected void AddViewList(StatCrafting stat, Item viewItem) {
            if(!(viewItem instanceof ItemBlock))
                super.AddViewList(stat, viewItem);
        }
    }

    public class StatTypeHasViewList<T> extends StatType<T>{

        protected List viewlist;

        public StatTypeHasViewList(StatBase[] stats,RegistryNamespaced registry, String keyBase, List viewlist){
            super(stats,registry,keyBase);
            this.viewlist = viewlist;
        }

        /**
         * 統計を追加します
         * @param use　使用対象です。
         * @param viewItem　カウント表示時のアイコン用です。Nullの場合使用対象がそのまま表示されます。
         * @param key　保存時に使用するキーです。同一キーで登録した場合、統合してカウントされます。
         */
        @Override
        public StatCrafting registerStat(T use,Item viewItem,String key){
            Boolean isAdded = registerdStats.containsKey(keyBase + "." + key);

            StatCrafting stat = super.registerStat(use,viewItem,key);

            if(stat != null && viewlist != null && !isAdded)
                AddViewList(stat,viewItem);

            return stat;
        }

        protected void AddViewList(StatCrafting stat,Item viewItem){
            viewlist.add(stat);
        }
    }

    public class StatType<T>{
        protected Multimap<StatCrafting,Integer> registerdStatsSlotIndex = HashMultimap.create();
        protected Multimap<StatCrafting,T> registerdStatsSlotObject = HashMultimap.create();

        protected StatBase[] stats;
        protected RegistryNamespaced registry;
        protected String keyBase;

        public StatType(StatBase[] stats,RegistryNamespaced registry, String keyBase){
            this.stats = stats;
            this.registry = registry;
            this.keyBase = keyBase;
        }

        public void remapStats(){

            {//clear
                Set<Integer> workedIds = Sets.newHashSet();
                for(Map.Entry<StatCrafting, Integer> entry : registerdStatsSlotIndex.entries()){
                    int id = entry.getValue();
                    if(!workedIds.contains(id) && stats[id] == entry.getKey()){
                        stats[id] = null;
                        workedIds.add(id);
                    }
                }
                registerdStatsSlotIndex.clear();
            }

            {//reset
                for(Map.Entry<StatCrafting, T> entry : registerdStatsSlotObject.entries()){
                    int id = registry.getIDForObject(entry.getValue());
                    StatCrafting stat = entry.getKey();
                    stats[id] = stat;
                    registerdStatsSlotIndex.put(stat, id);
                }
            }

        }

        private StatCrafting getStat(Item viewItem,String key){
            String statKey = keyBase + "." + key;
            StatCrafting stat;
            if(!registerdStats.containsKey(statKey)){
                stat = new StatCrafting(statKey
                        , new ChatComponentTranslation(keyBase
                        , new Object[] {(new ItemStack(viewItem)).func_151000_E()})
                        , viewItem);
                stat.registerStat();
                registerdStats.put(statKey, stat);
            }else{
                stat = (StatCrafting)registerdStats.get(statKey);
            }

            return stat;
        }

        /**
         * 統計を追加します
         * @param use　使用対象です。
         * @param viewItem　カウント表示時のアイコン用です。Nullの場合使用対象がそのまま表示されます。
         * @param key　保存時に使用するキーです。同一キーで登録した場合、統合してカウントされます。
         */
        public StatCrafting registerStat(T use,Item viewItem,String key){
            if(viewItem == null){
                viewItem = Item.getItemById(registry.getIDForObject(use));
            }

            if (use != null && viewItem != null)
            {
                StatCrafting stat = getStat(viewItem,key);
                registerdStatsSlotObject.put(stat, use);

                return stat;
            }else{
                return null;
            }
        }
    }

    public StatTypeUseItem StatUse;
    public StatType<Item> StatCrafts;
    public StatType<Item> StatBreak;

    public StatTypeHasViewList<Block> StatMineBlock;

    public StatManager(){
        StatUse = new StatTypeUseItem(StatList.objectUseStats,Item.itemRegistry,"stat.useItem",StatList.itemStats);
        StatCrafts = new StatType<Item>(StatList.objectCraftStats,Item.itemRegistry,"stat.craftItem");
        StatBreak = new StatType<Item>(StatList.objectBreakStats,Item.itemRegistry,"stat.breakItem");

        StatMineBlock = new StatTypeHasViewList<Block>(StatList.mineBlockStatArray,Block.blockRegistry,"stat.mineBlock",StatList.objectMineStats);
    }

    @SubscribeEvent
    public void WoldLoadEvent(WorldEvent.Load event){
        StatUse.remapStats();
        StatCrafts.remapStats();
        StatBreak.remapStats();
        StatMineBlock.remapStats();
    }

    /**
     * アイテム統計群を追加します
     * @param useItem　使用対象アイテムです。
     * @param viewItem　カウント表示時のアイコン用アイテムです。Nullの場合使用対象がそのまま表示されます。
     * @param key　保存時に使用するキーです。同一キーで登録した場合、統合してカウントされます。
     */
    public void registerItemStat(Item useItem,Item viewItem,String key){
        StatUse.registerStat(useItem, viewItem, key);
        StatCrafts.registerStat(useItem, viewItem, key);
        StatBreak.registerStat(useItem, viewItem, key);
    }

    /**
     * ブロック統計群を追加します
     * @param useBlock　使用対象ブロックです。
     * @param viewItem　カウント表示時のアイコン用です。Nullの場合使用対象がそのまま表示されます。
     * @param key　保存時に使用するキーです。同一キーで登録した場合、統合してカウントされます。
     */
    public void registerBlockStat(Block useBlock,ItemBlock viewItem,String key){
        Item useItem = Item.getItemFromBlock(useBlock);
        if(useItem != null){
            StatUse.registerStat(useItem, viewItem, key);
            StatCrafts.registerStat(useItem, viewItem, key);
        }
        StatMineBlock.registerStat(useBlock, viewItem, key);
    }
}
