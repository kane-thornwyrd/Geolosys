package com.oitsjustjose.geolosys.blocks;

import com.oitsjustjose.geolosys.Geolosys;
import com.oitsjustjose.geolosys.items.ItemCluster;
import com.oitsjustjose.geolosys.util.Lib;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockOreVanilla extends Block
{
    public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create("variant", EnumType.class);

    public BlockOreVanilla()
    {
        super(Material.ROCK);
        this.setRegistryName(new ResourceLocation(Lib.MODID, "ore_vanilla"));
        this.setHardness(7.5F);
        this.setResistance(10F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumType.COAL));
        this.setUnlocalizedName(this.getRegistryName().toString().replaceAll(":", "."));
        this.setHarvestLevels();
        ForgeRegistries.BLOCKS.register(this);
        ForgeRegistries.ITEMS.register(new ItemBlockOre(this));
    }

    private void setHarvestLevels()
    {
        for (EnumType t : EnumType.values())
            this.setHarvestLevel("pickaxe", t.getToolLevel(), this.getDefaultState().withProperty(VARIANT, t));
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        return state.getBlock().getMetaFromState(state) != 2;
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state)
    {
        switch (blockState.getBlock().getMetaFromState(state))
        {
            case 0:
                return new ItemStack(Blocks.COAL_ORE);
            case 1:
                return new ItemStack(Blocks.REDSTONE_ORE);
            case 3:
                return new ItemStack(Blocks.LAPIS_ORE);
            case 4:
                return new ItemStack(Blocks.QUARTZ_ORE);
            case 5:
                return new ItemStack(Blocks.DIAMOND_ORE);
            default:
                return new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        Random random = new Random();
        int meta = state.getBlock().getMetaFromState(state);
        if (meta == 0)
        {
            drops.add(new ItemStack(Blocks.COAL_ORE.getItemDropped(state, random, fortune), Blocks.COAL_ORE.quantityDroppedWithBonus(fortune, random), Blocks.COAL_ORE.damageDropped(state)));
        }
        else if (meta == 1)
        {
            drops.add(new ItemStack(Blocks.REDSTONE_ORE.getItemDropped(state, random, fortune), Blocks.REDSTONE_ORE.quantityDroppedWithBonus(fortune, random), Blocks.REDSTONE_ORE.damageDropped(state)));
            // Compat for ExtraUtils 2
            final Item EXU_INGREDIENT = ForgeRegistries.ITEMS.getValue(new ResourceLocation("extrautils2", "ingredients"));
            if (EXU_INGREDIENT != null && random.nextInt(30) == 0)
                drops.add(new ItemStack(EXU_INGREDIENT));

        }
        else if (meta == 2)
        {
            drops.add(new ItemStack(Geolosys.CLUSTER, 1, ItemCluster.META_GOLD));
        }
        else if (meta == 3)
        {
            drops.add(new ItemStack(Blocks.LAPIS_ORE.getItemDropped(state, random, fortune), Blocks.LAPIS_ORE.quantityDroppedWithBonus(fortune, random), Blocks.LAPIS_ORE.damageDropped(state)));
        }
        else if (meta == 4)
        {
            drops.add(new ItemStack(Blocks.QUARTZ_ORE.getItemDropped(state, random, fortune), Blocks.QUARTZ_ORE.quantityDroppedWithBonus(fortune, random), Blocks.QUARTZ_ORE.damageDropped(state)));
            int fortuneDropCalc = 1 + random.nextInt(fortune + 1);
            for (int i = 0; i < fortuneDropCalc; i++)
            {
                int rng = random.nextInt(25);
                // Compat for certus quartz & black quartz
                Item certusQuartz = ForgeRegistries.ITEMS.getValue(new ResourceLocation("appliedenergistics2", "material"));
                Item blackQuartz = ForgeRegistries.ITEMS.getValue(new ResourceLocation("actuallyadditions", "item_misc"));
                if (certusQuartz != null)
                {
                    if (rng < 2) // 2 / 25
                        drops.add(new ItemStack(certusQuartz, 1, 0));
                    else if (rng < 3) // 1 / 25
                        drops.add(new ItemStack(certusQuartz, 1, 1));
                }
                if (blackQuartz != null)
                {
                    if (rng >= 3 && rng < 5) // 2 / 25
                        drops.add(new ItemStack(blackQuartz, 1, 5));
                }
            }
        }
        else if (meta == 5)
        {
            drops.add(new ItemStack(Blocks.DIAMOND_ORE.getItemDropped(state, random, fortune), Blocks.DIAMOND_ORE.quantityDroppedWithBonus(fortune, random), Blocks.DIAMOND_ORE.damageDropped(state)));
        }
    }


    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune)
    {
        switch (state.getBlock().getMetaFromState(state))
        {
            case 0:
                return Blocks.COAL_ORE.getExpDrop(state, world, pos, fortune);
            case 1:
                return Blocks.REDSTONE_ORE.getExpDrop(state, world, pos, fortune);
            case 3:
                return Blocks.LAPIS_ORE.getExpDrop(state, world, pos, fortune);
            case 4:
                return Blocks.QUARTZ_ORE.getExpDrop(state, world, pos, fortune);
            case 5:
                return Blocks.DIAMOND_ORE.getExpDrop(state, world, pos, fortune);
            default:
                return 0;
        }
    }


    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(state.getBlock(), 1, this.getMetaFromState(state));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return this.getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, VARIANT);
    }

    public enum EnumType implements IStringSerializable
    {
        COAL(0, 0, "coal"),
        CINNABAR(1, 2, "cinnabar"),
        GOLD(2, 2, "gold"),
        LAPIS(3, 1, "lapis"),
        QUARTZ(4, 1, "quartz"),
        KIMBERLITE(5, 2, "kimberlite");

        private static final EnumType[] META_LOOKUP = new EnumType[values().length];
        private final int meta;
        private final int toolLevel;
        private final String unlocalizedName;

        EnumType(int meta, int toolLevel, String name)
        {
            this.meta = meta;
            this.toolLevel = toolLevel;
            this.unlocalizedName = name;
        }

        public int getToolLevel()
        {
            return this.toolLevel;
        }

        public int getMetadata()
        {
            return this.meta;
        }

        public String toString()
        {
            return this.unlocalizedName;
        }

        public static EnumType byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName()
        {
            return this.unlocalizedName;
        }

        static
        {
            for (EnumType type : values())
            {
                META_LOOKUP[type.getMetadata()] = type;
            }
        }
    }

    /**
     * An ItemBlock class for this block allowing it to
     * support subtypes with proper placement
     */
    public class ItemBlockOre extends ItemBlock
    {

        ItemBlockOre(Block block)
        {
            super(block);
            this.setHasSubtypes(true);
            this.setRegistryName(block.getRegistryName());
            this.setMaxDamage(0);
            this.registerModels();
        }

        @Override
        public int getMetadata(int damage)
        {
            return damage;
        }

        @Override
        public String getUnlocalizedName(ItemStack stack)
        {
            return stack.getItem().getRegistryName().toString().replaceAll(":", ".") + "." + EnumType.byMetadata(stack.getMetadata()).getName();
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list)
        {
            if (this.isInCreativeTab(tab))
                for (int i = 0; i < EnumType.values().length; ++i)
                    list.add(new ItemStack(this, 1, i));
        }

        private void registerModels()
        {
            for (int i = 0; i < EnumType.values().length; i++)
                Geolosys.clientRegistry.register(new ItemStack(this, 1, i), VARIANT.getName() + "=" + EnumType.byMetadata(i).getName());
        }
    }
}
