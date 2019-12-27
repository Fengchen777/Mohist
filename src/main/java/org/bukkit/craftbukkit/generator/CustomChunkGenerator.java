package org.bukkit.craftbukkit.generator;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.world.spawner.CatSpawner;
import net.minecraft.world.spawner.PatrolSpawner;
import net.minecraft.world.spawner.PhantomSpawner;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;
import org.bukkit.generator.ChunkGenerator.ChunkData;

public class CustomChunkGenerator extends InternalChunkGenerator<GeneratorSettingsDefault> {
    private final ChunkGenerator generator;
    private final WorldServer world;
    private final long seed;
    private final Random random;
    private final StructureGenerator strongholdGen = WorldGenerator.STRONGHOLD;
    private final MobSpawnerPhantom mobSpawnerPhantom = new MobSpawnerPhantom();
    private final MobSpawnerPatrol mobSpawnerPatrol = new MobSpawnerPatrol();
    private final MobSpawnerCat mobSpawnerCat = new MobSpawnerCat();
    private final VillageSiege villageSiege = new VillageSiege();

    private class CustomBiomeGrid implements BiomeGrid {

        private final BiomeStorage biome;

        public CustomBiomeGrid(BiomeStorage biome) {
            this.biome = biome;
        }

        @Override
        public Biome getBiome(int x, int z) {
            return getBiome(x, 0, z);
        }

        @Override
        public void setBiome(int x, int z, Biome bio) {
            for (int y = 0; y < world.getWorld().getMaxHeight(); y++) {
                setBiome(x, y, z, bio);
            }
        }

        @Override
        public Biome getBiome(int x, int y, int z) {
            return CraftBlock.biomeBaseToBiome(biome.getBiome(x, y, z));
        }

        @Override
        public void setBiome(int x, int y, int z, Biome bio) {
            biome.setBiome(x, y, z, CraftBlock.biomeToBiomeBase(bio));
        }
    }

    public CustomChunkGenerator(World world, long seed, ChunkGenerator generator) {
        super(world, world.worldProvider.getChunkGenerator().getWorldChunkManager(), new GeneratorSettingsDefault());
        this.world = (WorldServer) world;
        this.generator = generator;
        this.seed = seed;

        this.random = new Random(seed);
    }

    @Override
    public void buildBase(RegionLimitedWorldAccess regionlimitedworldaccess, IChunkAccess ichunkaccess) {
        int x = ichunkaccess.getPos().x;
        int z = ichunkaccess.getPos().z;
        random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);

        // Get default biome data for chunk
        CustomBiomeGrid biomegrid = new CustomBiomeGrid(new BiomeStorage(ichunkaccess.getPos(), this.getWorldChunkManager()));

        ChunkData data;
        if (generator.isParallelCapable()) {
            data = generator.generateChunkData(this.world.getWorld(), random, x, z, biomegrid);
        } else {
            synchronized (this) {
                data = generator.generateChunkData(this.world.getWorld(), random, x, z, biomegrid);
            }
        }

        Preconditions.checkArgument(data instanceof CraftChunkData, "Plugins must use createChunkData(World) rather than implementing ChunkData: %s", data);
        CraftChunkData craftData = (CraftChunkData) data;
        ChunkSection[] sections = craftData.getRawChunkData();

        ChunkSection[] csect = ichunkaccess.getSections();
        int scnt = Math.min(csect.length, sections.length);

        // Loop through returned sections
        for (int sec = 0; sec < scnt; sec++) {
            if (sections[sec] == null) {
                continue;
            }
            ChunkSection section = sections[sec];

            csect[sec] = section;
        }

        // Set biome grid
        ((ProtoChunk) ichunkaccess).a(biomegrid.biome);

        if (craftData.getTiles() != null) {
            for (BlockPosition pos : craftData.getTiles()) {
                int tx = pos.getX();
                int ty = pos.getY();
                int tz = pos.getZ();
                Block block = craftData.getTypeId(tx, ty, tz).getBlock();

                if (block.isTileEntity()) {
                    TileEntity tile = ((ITileEntity) block).createTile(world);
                    ichunkaccess.setTileEntity(new BlockPosition((x << 4) + tx, ty, (z << 4) + tz), tile);
                }
            }
        }
    }

    @Override
    public void doCarving(BiomeManager biomemanager, IChunkAccess ichunkaccess, WorldGenStage.Features worldgenstage_features) {
    }

    @Override
    public void buildNoise(GeneratorAccess generatoraccess, IChunkAccess ichunkaccess) {
    }

    @Override
    public int getBaseHeight(int i, int j, HeightMap.Type heightmap_type) {
        return 0;
    }

    @Override
    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType type, BlockPosition position) {
        BiomeBase biomebase = world.getBiome(position);

        return biomebase == null ? null : biomebase.getMobs(type);
    }

    @Override
    public void addDecorations(RegionLimitedWorldAccess regionlimitedworldaccess) {
    }

    @Override
    public void addMobs(RegionLimitedWorldAccess regionlimitedworldaccess) {
    }

    @Override
    public BlockPosition findNearestMapFeature(World world, String type, BlockPosition position, int i, boolean flag) {
        return "Stronghold".equals(type) && this.strongholdGen != null ? this.strongholdGen.getNearestGeneratedFeature(world, this, position, i, flag) : null;
    }

    @Override
    public GeneratorSettingsDefault getSettings() {
        return settings;
    }

    @Override
    public void doMobSpawning(WorldServer worldserver, boolean flag, boolean flag1) {
        this.mobSpawnerPhantom.a(worldserver, flag, flag1);
        this.mobSpawnerPatrol.a(worldserver, flag, flag1);
        this.mobSpawnerCat.a(worldserver, flag, flag1);
        this.villageSiege.a(worldserver, flag, flag1);
    }

    @Override
    public boolean canSpawnStructure(BiomeBase biomebase, StructureGenerator<? extends WorldGenFeatureConfiguration> structuregenerator) {
        return biomebase.a(structuregenerator);
    }

    @Override
    public long getSeed() {
        return seed;
    }

    @Override
    public int getSpawnHeight() {
        return world.getSeaLevel() + 1;
    }

    @Override
    public int getGenerationDepth() {
        return world.getHeight();
    }
}