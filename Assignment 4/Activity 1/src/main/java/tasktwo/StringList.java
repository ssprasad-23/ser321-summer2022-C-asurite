package tasktwo;

import java.util.ArrayList;
import java.util.List;

class StringList {

    final List<String> strings = new ArrayList<String>();

    public void add(String str) {
        synchronized (strings) {
            int pos = strings.indexOf(str);
            if (pos < 0) {
                strings.add(str);
            }
        }
    }

    public String pop() {
        synchronized (strings) {
            if (strings.isEmpty()) {
                return null;
            }

            return strings.remove(0);
        }
    }

    public String display() {
        synchronized (strings) {
            return strings.toString();
        }
    }

    public int count() {
        synchronized (strings) {
            return strings.size();
        }
    }


    public String switchElements(int index1, int index2) {
        synchronized (strings) {
            if (index1 >= strings.size() || index1 < 0 || index2 >= strings.size() || index2 < 0) {
                return "INDEX IS NOT CORRECT";
            }

            String index1Str = strings.get(index1);
            String index2Str = strings.get(index2);

            strings.set(index1, index2Str);
            strings.set(index2, index1Str);

            return "SUCCESS";
        }
    }

    public boolean contains(String str) {
        synchronized (strings) {
            return strings.indexOf(str) >= 0;
        }
    }

    public int size() {
        synchronized (strings) {
            return strings.size();
        }
    }

    public String toString() {
        synchronized (strings) {
            return strings.toString();
        }
    }

}