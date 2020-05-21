package red.mohist.fabric.mixin.impl;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.CraftLink;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.mohist.fabric.AbstractServerImpl;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayer extends PlayerEntity implements CraftLink<Player> {

    @Shadow
    public ServerPlayNetworkHandler networkHandler;
    @Shadow
    @Final
    public MinecraftServer server;
    @Shadow
    @Final
    public ServerPlayerInteractionManager interactionManager;
    private CraftPlayer craftHandler;

    public MixinServerPlayer(World world_1, GameProfile gameProfile_1) { // Just needs to be here
        super(world_1, gameProfile_1);
    }

    //Testing code
    @Inject(at = @At("RETURN"), method = "tick")
    public void tick(CallbackInfo info) {
        //System.out.println("Items: " + craftHandler.getInventory().contains(Material.STONE));
    }

    @Shadow
    public abstract void sendChatMessage(Text textComponent_1, MessageType chatMessageType_1);

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onCreate(CallbackInfo info) {
        craftHandler = new CraftPlayer((AbstractServerImpl) Bukkit.getServer(), (ServerPlayerEntity) (Object) this);
    }

    @Override
    public Player getCraftHandler() {
        return craftHandler;
    }

}