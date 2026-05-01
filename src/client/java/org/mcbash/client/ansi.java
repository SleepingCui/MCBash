package org.mcbash.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ansi {
    public static String convert(String text) {
        if (text == null) return "";

        Pattern pattern = Pattern.compile("\u001B\\[([0-9;]+)m");
        Matcher matcher = pattern.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String codeGroup = matcher.group(1);
            String mcCode = converta(codeGroup);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(mcCode));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String converta(String ansiCode) {
        String[] parts = ansiCode.split(";");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            int code;
            try {
                code = Integer.parseInt(part);
            } catch (NumberFormatException e) {
                continue;
            }

            if (code == 0) {
                result.append("§r");
            } else if (code >= 30 && code <= 37) {
                String[] colors = {"§0", "§4", "§2", "§6", "§1", "§5", "§3", "§7"};
                result.append(colors[code - 30]);
            } else if (code >= 90 && code <= 97) {
                String[] brightColors = {"§8", "§c", "§a", "§e", "§9", "§d", "§b", "§f"};
                result.append(brightColors[code - 90]);
            }
        }

        return result.toString();
    }
}
