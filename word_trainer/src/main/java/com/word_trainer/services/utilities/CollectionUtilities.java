package com.word_trainer.services.utilities;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtilities {

    public static <T> List<T> mergeCollections(List<T> collection1, List<T> collection2, int count, double collection1ResultProportion) {
        if (count == 0) {
            return List.of();
        }

        List<T> result = new ArrayList<>(count);

        int countFromCollection1 = (int) (count * collection1ResultProportion);

        int index1 = 0;
        while (index1 < collection1.size() && result.size() < countFromCollection1) {
            result.add(collection1.get(index1++));
        }

        int index2 = 0;
        while (index2 < collection2.size() && result.size() < count) {
            result.add(collection2.get(index2++));
        }

        while (result.size() < count && index1 < collection1.size()) {
            result.add(collection1.get(index1++));
        }
        return result;
    }
}
