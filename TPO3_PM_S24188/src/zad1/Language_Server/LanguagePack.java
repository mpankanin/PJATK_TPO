package zad1.Language_Server;

import java.util.Map;

public abstract class LanguagePack {

    private final String languageCode;
    private Map<String, String> dictionary;

    public LanguagePack(String languageCode, Map<String, String> dictionary) {
        this.languageCode = languageCode;
        this.dictionary = dictionary;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public Map<String, String> getDictionary() {
        return dictionary;
    }

    public void setDictionary(Map<String, String> dictionary) {
        this.dictionary = dictionary;
    }

    public void addToDictionary(String word, String foreignWord){
        dictionary.put(word, foreignWord);
    }

    public void removeFromDictionary(String word){
        dictionary.remove(word);
    }

}
