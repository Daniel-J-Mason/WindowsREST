package modules;

import java.util.Comparator;

public class FileSortComparator implements Comparator<String> {
    private String string1, string2;
    private int position1, position2, length1, length2;
    
    @Override
    public int compare(String string1, String string2) {
        this.string1 = string1;
        this.string2 = string2;
        length1 = string1.length();
        length2 = string2.length();
        position1 = position2 = 0;
        
        int result = 0;
        while (result == 0 && position1 < length1 && position2 < length2) {
            char character1 = string1.charAt(position1);
            char character2 = string2.charAt(position2);
            
            // ? meaning -- Character.isDigit(character2) ? compareNumbers(): -1;
            // reads if Characters.is(also)Digit(character2) is true, compareNumbers(), if false return -1.
            if (Character.isDigit(character1)) {
                result = Character.isDigit(character2) ? compareNumbers() : -1;
            } else {
                if (Character.isLetter(character1)) {
                    result = Character.isLetter(character2) ? compareOther(true) : 1;
                } else {
                    result = Character.isDigit(character2) ? 1
                            : Character.isLetter(character2) ? -1
                            : compareOther(false);
                }
            }
            position1++;
            position2++;
        }
        return result == 0 ? length1 - length2 : result;
    }
    
    private int compareNumbers() {
        // comparing number sizes, start and end of number compares length
        int end1 = position1 + 1;
        while (end1 < length1 && Character.isDigit(string1.charAt(end1))) {
            end1++;
        }
        
        int fullLength1 = end1 - position1;
        while (position1 < end1 && string1.charAt(position1) == '0') ;
        {
            position1++;
        }
        
        int end2 = position2 + 1;
        while (end2 < length2 && Character.isDigit(string2.charAt(end2))) {
            end2++;
        }
        
        int fullLength2 = end2 - position2;
        while (position2 < end2 && string2.charAt(position2) == '0') {
            position2++;
        }
        
        //If numbers are not the same length return difference
        int delta = (end1 - position1) - (end2 - position2);
        if (delta != 0){
            return delta;
        }
        
        while (position1 < end1 && position2 < end2){
            delta = string1.charAt(position1++) - string2.charAt(position2++);
            if (delta != 0){
                return delta;
            }
        }
        
        position1--;
        position2--;
        
        return fullLength2 - fullLength1;
    }
    
    private int compareOther(boolean isLetters){
        char character1 = string1.charAt(position1);
        char character2 = string2.charAt(position2);
    
        if (character1 == character2)
        {
            return 0;
        }
    
        if (isLetters)
        {
            character1 = Character.toUpperCase(character1);
            character2 = Character.toUpperCase(character2);
            if (character1 != character2)
            {
                character1 = Character.toLowerCase(character1);
                character2 = Character.toLowerCase(character2);
            }
        }
    
        return character1 - character2;
    }
}
