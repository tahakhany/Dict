package com.taha.dict;


public class Dictionary {
    private String word;
    private String definition;
    private String usedIn;

    public Dictionary(String word, String definition) {
        this.word = word;
        this.definition = definition;
        this.usedIn = "N/A";
    }

    public Dictionary(String word, String definition, String usedIn) {
        this.word = word;
        this.definition = definition;
        this.usedIn = usedIn;
    }

    public String getWord() {
        return word;
    }

    public String getDefinition() {
        return definition;
    }

    public String getUsedIn() {
        return usedIn;
    }
}