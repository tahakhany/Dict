package com.taha.dict;

public class WordDatabase {

    private static final Dictionary[] mDictionary = {
            new Dictionary("a", "aaa", "a"),
            new Dictionary("b", "bbb", "b"),
            new Dictionary("c", "ccc", "c"),
            new Dictionary("d", "ddd", "d"),
            new Dictionary("e", "eee", "e"),
            new Dictionary("abc", "abcabcabc", "abc"),
            new Dictionary("bcd", "bcdbcdbcd", "bcd")
    };

    private static final String[] mSortByList = {
            "Sort by word",
            "Sort by usage"
    };

    public static Dictionary[] getDictionary() {
        return mDictionary;
    }

    public static String[] getmSortByList(){
        return mSortByList;
    }
}
