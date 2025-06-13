package de.oliver.fancyperks;

import java.util.Map;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

import de.oliver.fancylib.LanguageConfig;

public class LanguageHelper {

    private static final LanguageConfig lang = FancyPerks.getInstance().getLanguageConfig();

    public static String getLocalizedMessage(String messageKey, Map<String, String> replacements) {
        
        String messageTemplate = lang.get(messageKey);
        String prefix = lang.get("prefix");

        if (!"".equals(prefix)) {
            prefix = prefix + " ";
        }
        if (messageTemplate == null) { 
            getLogger().log(Level.WARNING, "Message key {0} not found.", messageKey);
            return prefix + "[message key not found]";
        }

        if (replacements != null && !replacements.isEmpty()) {
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                messageTemplate = messageTemplate.replace("${" + entry.getKey() + "}", entry.getValue());
            }
        }

        return prefix + messageTemplate;
        
    }
}
