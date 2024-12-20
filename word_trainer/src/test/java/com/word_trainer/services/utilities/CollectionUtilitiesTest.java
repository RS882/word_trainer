package com.word_trainer.services.utilities;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.word_trainer.services.utilities.CollectionUtilities.mergeCollections;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
class CollectionUtilitiesTest {

    @Nested
    @DisplayName(value = "Merge Collections tests")
    public class MergeCollectionsTests {

        private Map<String, Long> getCountOfElementsFromCollections(
                int count,
                double proportion,
                int sizeOfCollection1,
                int sizeOfCollection2) {

            List<String> collection1 = new ArrayList<>();
            List<String> collection2 = new ArrayList<>();
            for (int i = 0; i < sizeOfCollection1; i++) {
                collection1.add("1 test elem " + i);
            }
            for (int i = 0; i < sizeOfCollection2; i++) {
                collection2.add("2 test elem " + i);
            }

            List<String> mergeResult = mergeCollections(collection1, collection2, count, proportion);

            long countOfElementsByCollection1 = mergeResult.stream()
                    .filter(e -> e.startsWith("1"))
                    .count();
            long countOfElementsByCollection2 = mergeResult.stream()
                    .filter(e -> e.startsWith("2"))
                    .count();

            Map<String, Long> result = new HashMap<>();
            result.put("resultSize", (long) mergeResult.size());
            result.put("countOfElementsByCollection1", countOfElementsByCollection1);
            result.put("countOfElementsByCollection2", countOfElementsByCollection2);
            return result;
        }

        @ParameterizedTest
        @CsvSource({
                "10, 0.6, 10, 10",
                "15, 0.5, 10, 10",
                "8, 1, 10, 10",
                "9, 0, 10, 10",
                "0, 0, 10, 10"
        })
        public void merge_string_collections_when_count_elements_are_enough(
                int count,
                double proportion,
                int sizeOfCollection1,
                int sizeOfCollection2) {
            Map<String, Long> countsOfElementsFromCollections =
                    getCountOfElementsFromCollections(
                            count,
                            proportion,
                            sizeOfCollection1,
                            sizeOfCollection2);

            assertEquals(countsOfElementsFromCollections.get("resultSize"), count);
            assertEquals(countsOfElementsFromCollections.get("countOfElementsByCollection1"), (long) (count * proportion));
            assertEquals(countsOfElementsFromCollections.get("countOfElementsByCollection2"), count - (long) (count * proportion));
        }

        @ParameterizedTest
        @CsvSource({
                "10, 0.6, 3, 15",
                "10, 0.6, 0, 15"
        })
        public void merge_string_collections_when_count_elements_1_collection_are_missing(
                int count,
                double proportion,
                int sizeOfCollection1,
                int sizeOfCollection2) {

            Map<String, Long> countsOfElementsFromCollections =
                    getCountOfElementsFromCollections(
                            count,
                            proportion,
                            sizeOfCollection1,
                            sizeOfCollection2);

            assertEquals(countsOfElementsFromCollections.get("resultSize"), count);
            assertEquals(countsOfElementsFromCollections.get("countOfElementsByCollection1"), sizeOfCollection1);
            assertEquals(countsOfElementsFromCollections.get("countOfElementsByCollection2"), count - sizeOfCollection1);
        }

        @ParameterizedTest
        @CsvSource({
                "10, 0.6, 17, 2",
                "10, 0.6, 17, 0",
        })
        public void merge_string_collections_when_count_elements_2_collection_are_missing(
                int count,
                double proportion,
                int sizeOfCollection1,
                int sizeOfCollection2) {

            Map<String, Long> countsOfElementsFromCollections =
                    getCountOfElementsFromCollections(
                            count,
                            proportion,
                            sizeOfCollection1,
                            sizeOfCollection2);

            assertEquals(countsOfElementsFromCollections.get("resultSize"), count);
            assertEquals(countsOfElementsFromCollections.get("countOfElementsByCollection1"), count - sizeOfCollection2);
            assertEquals(countsOfElementsFromCollections.get("countOfElementsByCollection2"), sizeOfCollection2);
        }

        @ParameterizedTest
        @CsvSource({
                "10, 0.6, 2, 4",
                "10, 0.6, 5, 1",
                "10, 0.6, 4, 0",
                "10, 0.6, 0, 3",
                "10, 0.6, 0, 0",
                "10, 0.6, 8, 1",
                "10, 0.6, 2, 7",
        })
        public void merge_string_collections_when_count_elements_are_not_enough(
                int count,
                double proportion,
                int sizeOfCollection1,
                int sizeOfCollection2) {

            Map<String, Long> countsOfElementsFromCollections =
                    getCountOfElementsFromCollections(
                            count,
                            proportion,
                            sizeOfCollection1,
                            sizeOfCollection2);

            assertEquals(countsOfElementsFromCollections.get("resultSize"), sizeOfCollection1 + sizeOfCollection2);
            assertEquals(countsOfElementsFromCollections.get("countOfElementsByCollection1"), sizeOfCollection1);
            assertEquals(countsOfElementsFromCollections.get("countOfElementsByCollection2"), sizeOfCollection2);
        }
    }
}
