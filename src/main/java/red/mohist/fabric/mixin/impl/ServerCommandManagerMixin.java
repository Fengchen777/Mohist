package red.mohist.fabric.mixin.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.CraftLink;
import org.bukkit.entity.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import red.mohist.fabric.AbstractServerImpl;

@Mixin(CommandManager.class)
public class ServerCommandManagerMixin {

    @SuppressWarnings("unchecked")
    @Inject(at = @At("HEAD"), method = "execute", cancellable = true)
    public int execute(ServerCommandSource serverCommandSource_1, String commandLine, CallbackInfoReturnable<Integer> info) {
        try {
            ServerPlayerEntity player = serverCommandSource_1.getPlayer();
            if (player != null) {
                String bukkitCommand = commandLine;
                if (bukkitCommand.startsWith("/")) {
                    bukkitCommand = bukkitCommand.substring(1);
                }
                boolean worked = ((AbstractServerImpl) Bukkit.getServer()).getCommandMap().dispatch(((CraftLink<Player>) player).getCraftHandler(), bukkitCommand);
                if (worked) {
                    info.setReturnValue(0);
                    info.cancel();
                    return 0;
                }
            }
        } catch (CommandSyntaxException ex) {
        }
        return 0;
    }

}
