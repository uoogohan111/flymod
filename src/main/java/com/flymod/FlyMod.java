package com.flymod;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class FlyMod implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
    }

    private void registerCommands(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(
            CommandManager.literal("fly")
                .executes(ctx -> {
                    ServerCommandSource source = ctx.getSource();
                    ServerPlayerEntity player = source.getPlayerOrThrow();

                    boolean newState = !player.getAbilities().allowFlying;
                    player.getAbilities().allowFlying = newState;

                    // If turning flight off and currently flying, bring them down
                    if (!newState) {
                        player.getAbilities().flying = false;
                    }

                    player.sendAbilitiesUpdate();

                    String status = newState ? "§aenabled" : "§cdisabled";
                    player.sendMessage(Text.literal("§6[FlyMod] §fFlight " + status + "§f."), false);

                    return 1;
                })
        );
    }
}
