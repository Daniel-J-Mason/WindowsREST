package service;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.regex.Pattern;

public final class FileSortComparator implements Comparator<String> {
    
    private static final Pattern NUMBERS =
            Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
    
    @Override
    public final int compare(String string1, String string2) {
    
        // Null last
        if (string1 == null || string2 == null)
            return string1 == null ? string2 == null ? 0 : -1 : 1;
    
        // Splitting both input strings by the above patterns
        String[] split1 = NUMBERS.split(string1);
        String[] split2 = NUMBERS.split(string2);
        int length = Math.min(split1.length, split2.length);
        
        // Loop through both
        for (int i = 0; i < length; i++) {
            char character1 = split1[i].charAt(0);
            char character2 = split2[i].charAt(0);
            int comparison = 0;
            
            // Digit first sort
            if (character1 >= '0' && character1 <= '9' && character2 >= '0' && character2 <= '9') {
                comparison = new BigInteger(split1[i]).compareTo(new BigInteger(split2[i]));
            }
            
            // If no numeric then lex
            if (comparison == 0){
                comparison = split1[i].compareTo(split2[i]);
            }
            
            // stops when not equal
            if (comparison != 0){
                return comparison;
            }
        }
        // if entirely equal order by length
        return split1.length - split2.length;
    }
}
