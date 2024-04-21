package zad1.Language_Server;

import java.util.HashMap;
import java.util.Map;

public class LanguageGerman extends LanguagePack {

    public LanguageGerman() {
        super("DE", null);
        setDictionary(getDictionary());
    }

    public Map<String, String> getDictionary() {
        Map<String, String> dictionary = new HashMap<>();
        dictionary.put("witaj", "willkommen");
        dictionary.put("do widzenia", "auf Wiedersehen");
        dictionary.put("prosze", "bitte");
        dictionary.put("dziekuje", "danke");
        dictionary.put("tak", "ja");
        return dictionary;
    }

}
