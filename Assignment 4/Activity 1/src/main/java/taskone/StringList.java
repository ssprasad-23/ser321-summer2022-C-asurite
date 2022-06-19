package taskone;

import java.util.List;
import java.util.ArrayList;

class StringList {
    
    List<String> strings = new ArrayList<String>();

    public void add(String str) {
        int pos = strings.indexOf(str);
        if (pos < 0) {
            strings.add(str);
        }
    }

    public String pop() {

        if(strings.isEmpty()) {
            return null;
        }

        return strings.remove(0);
    }

    public String display() {
        return strings.toString();
    }

    public int count() {
        return strings.size();
    }


    public String switchElements(int index1, int index2) {
        if(index1 >= strings.size() || index1 < 0 || index2 >= strings.size() || index2 < 0) {
            return "INDEX IS NOT CORRECT";
        }

        String index1Str = strings.get(index1);
        String index2Str = strings.get(index2);

        strings.set(index1, index2Str);
        strings.set(index2, index1Str);

        return "SUCCESS";
    }

    public boolean contains(String str) {
        return strings.indexOf(str) >= 0;
    }

    public int size() {
        return strings.size();
    }

    public String toString() {
        return strings.toString();
    }

}