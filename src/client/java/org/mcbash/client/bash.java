package org.mcbash.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.io.*;
import java.nio.charset.StandardCharsets;

import org.mcbash.client.ansi.*;

import static org.mcbash.client.ansi.convert;

public class bash {

    public static Process bashProcess;
    public static BufferedWriter bashWriter;
    public static void handleBashCommand(String cmd) {
        try {
            if (bashProcess == null || !bashProcess.isAlive()) {
                startBashProcess();
            }
            if (bashWriter != null) {
                bashWriter.write(cmd + "\n");
                bashWriter.flush();
            }
        } catch (IOException e) {
            print(e.getMessage());
        }
    }

    public static void sendInteractiveInput(String input) {
        try {
            if (bashWriter != null && bashProcess != null && bashProcess.isAlive()) {
                bashWriter.write(input + "\n");
                bashWriter.flush();
            } else {
                print("Bash NOT RUNNING");
            }
        } catch (IOException e) {
            print(e.getMessage());
        }
    }

    public static void startBashProcess() {
        try {
            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-s");
            pb.redirectErrorStream(true);
            bashProcess = pb.start();
            bashWriter = new BufferedWriter(new OutputStreamWriter(bashProcess.getOutputStream(), StandardCharsets.UTF_8));
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(bashProcess.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String finalLine = line;
                        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal(convert(finalLine))));
                    }
                } catch (IOException ignored) {}
            }).start();
        } catch (IOException e) {
            print(e.getMessage());
            bashProcess = null;
            bashWriter = null;
        }
    }

    public static void stopBashProcess() {
        if (bashProcess != null) {
            bashProcess.destroy();
            bashProcess = null;
            bashWriter = null;
        }
    }

    public static void print(String message) {
        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal(message)));
    }
}
