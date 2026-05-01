package org.mcbash.client;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import java.io.*;

import static org.mcbash.client.bash.*;


public class McbashClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("bash").executes(context -> {stopBashProcess();
                                return 1;
                            })
                            .then(ClientCommandManager.argument("cmd", StringArgumentType.greedyString()).executes(context -> {
                                        handleBashCommand(StringArgumentType.getString(context, "cmd"));
                                        return 1;
                                    })
                            )
            );
            dispatcher.register(ClientCommandManager.literal("b")
                            .then(ClientCommandManager.argument("input", StringArgumentType.greedyString())
                                    .executes(context -> {
                                        sendInteractiveInput(StringArgumentType.getString(context, "input"));
                                        return 1;
                                    })
                            )
            );
        });


        Runtime.getRuntime().addShutdownHook(
                new Thread(org.mcbash.client.bash::stopBashProcess)
        );
    }


}