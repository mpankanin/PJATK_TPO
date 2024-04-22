package zad1.Language_Server.Languages;

import java.util.HashMap;
import java.util.Map;

public class LanguageEnglish extends LanguagePack {

    public LanguageEnglish() {
        super("EN", null);
        setDictionary(getDictionary());
    }

    public Map<String, String> getDictionary() {
        Map<String, String> dictionary = new HashMap<>();
        dictionary.put("witaj", "welcome");
        dictionary.put("do widzenia", "goodbay");
        dictionary.put("prosze", "please");
        dictionary.put("dziekuje", "thank you");
        dictionary.put("tak", "yes");
        return dictionary;
    }

}
