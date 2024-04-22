package zad1.Language_Server.Languages;

import java.util.HashMap;
import java.util.Map;

public class LanguageSpanish extends LanguagePack {

    public LanguageSpanish() {
        super("ES", null);
        setDictionary(getDictionary());
    }

    public Map<String, String> getDictionary() {
        Map<String, String> dictionary = new HashMap<>();
        dictionary.put("witaj", "bienvenido");
        dictionary.put("do widzenia", "adios");
        dictionary.put("prosze", "por favor");
        dictionary.put("dziekuje", "gracias");
        dictionary.put("tak", "si");
        return dictionary;
    }

}
