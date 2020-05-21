package red.mohist.fabric.mixin.impl;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.v1_15_R1.CraftLink;
import org.bukkit.craftbukkit.v1_15_R1.world.CraftWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld implements CraftLink<org.bukkit.World> {

    private CraftWorld craftHandler;

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onCreate(CallbackInfo info) {
        Environment enviroment = Environment.NORMAL;
        Identifier enviromentKey = DimensionType.getId(((World) (Object) this).getDimension().getType());
        if (enviromentKey.equals(DimensionType.getId(DimensionType.THE_END))) {
            enviroment = Environment.THE_END;
        } else if (enviromentKey.equals(DimensionType.getId(DimensionType.THE_NETHER))) {
            enviroment = Environment.NETHER;
        }
        craftHandler = new CraftWorld(((World) (Object) this), null, enviroment);
    }

    @Override
    public org.bukkit.World getCraftHandler() {
        return craftHandler;
    }

}