package ua.realestate.repository;

import ua.realestate.model.Landlord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SearchBenchmark {

    private static final int ITEM_COUNT = 100_000;
    private static final int WARMUP_RUNS = 100;
    private static final int MEASURE_RUNS = 25;

    private static final int BATCH_SIZE_MAP = 10_000;
    private static final int BATCH_SIZE_LIST = 100;

    private static int sink = 0;

    public static void main(String[] args) {
        IdentityExtractor<Landlord> extractor = Landlord::taxCode;
        GenericRepository<Landlord> repository = new GenericRepository<>("BenchmarkRepo", extractor);

        System.out.println("Generating " + ITEM_COUNT + " objects...");
        for (int i = 0; i < ITEM_COUNT; i++) {
            String taxCode = String.format("TAX-%06d", i);
            String name = "Landlord " + i;
            String phone = String.format("+38050%07d", i);
            repository.add(new Landlord(taxCode, name, phone));
        }

        List<Landlord> itemsList = repository.findAll();

        String firstId = String.format("TAX-%06d", 0);
        String lastId = String.format("TAX-%06d", ITEM_COUNT - 1);
        String absentId = "TAX-NONEXISTENT";

        System.out.println("Warming up JIT compiler (" + WARMUP_RUNS + " iterations)...");
        for (int i = 0; i < WARMUP_RUNS; i++) {
            runMapBatch(repository, firstId, 1000);
            runMapBatch(repository, lastId, 1000);
            runMapBatch(repository, absentId, 1000);

            runListBatch(itemsList, firstId, extractor, 50);
            runListBatch(itemsList, lastId, extractor, 10);
            runListBatch(itemsList, absentId, extractor, 10);
        }

        System.out.println("Running benchmark (" + MEASURE_RUNS + " measurement runs per scenario)...");

        double mapFirst = measureMap(repository, firstId);
        double mapLast = measureMap(repository, lastId);
        double mapAbsent = measureMap(repository, absentId);

        double listFirst = measureList(itemsList, firstId, extractor);
        double listLast = measureList(itemsList, lastId, extractor);
        double listAbsent = measureList(itemsList, absentId, extractor);

        System.out.println("\nBenchmark results (median time per single operation):");
        System.out.println("+----------------------+-------------------------+-------------------------+");
        System.out.println("| Scenario             | Map (LinkedHashMap)     | List (Linear Search)    |");
        System.out.println("+----------------------+-------------------------+-------------------------+");
        System.out.printf("| First element (0)    | %9.1f ns (%7.3f µs) | %9.1f ns (%7.3f µs) |\n",
                mapFirst, mapFirst / 1000.0, listFirst, listFirst / 1000.0);
        System.out.printf("| Last element (99999) | %9.1f ns (%7.3f µs) | %9.1f ns (%7.3f µs) |\n",
                mapLast, mapLast / 1000.0, listLast, listLast / 1000.0);
        System.out.printf("| Absent element       | %9.1f ns (%7.3f µs) | %9.1f ns (%7.3f µs) |\n",
                mapAbsent, mapAbsent / 1000.0, listAbsent, listAbsent / 1000.0);
        System.out.println("+----------------------+-------------------------+-------------------------+");

        if (sink == 42) {
            System.out.print("");
        }
    }

    private static double measureMap(GenericRepository<Landlord> repo, String identity) {
        List<Double> perOpTimes = new ArrayList<>();
        for (int i = 0; i < MEASURE_RUNS; i++) {
            long start = System.nanoTime();
            runMapBatch(repo, identity, BATCH_SIZE_MAP);
            long end = System.nanoTime();
            perOpTimes.add((double) (end - start) / BATCH_SIZE_MAP);
        }
        Collections.sort(perOpTimes);
        return perOpTimes.get(perOpTimes.size() / 2);
    }

    private static void runMapBatch(GenericRepository<Landlord> repo, String identity, int batchSize) {
        for (int b = 0; b < batchSize; b++) {
            Optional<Landlord> res = repo.findByIdentity(identity);
            sink ^= res.hashCode();
        }
    }

    private static double measureList(List<Landlord> list, String identity, IdentityExtractor<Landlord> extractor) {
        List<Double> perOpTimes = new ArrayList<>();
        for (int i = 0; i < MEASURE_RUNS; i++) {
            long start = System.nanoTime();
            runListBatch(list, identity, extractor, BATCH_SIZE_LIST);
            long end = System.nanoTime();
            perOpTimes.add((double) (end - start) / BATCH_SIZE_LIST);
        }
        Collections.sort(perOpTimes);
        return perOpTimes.get(perOpTimes.size() / 2);
    }

    private static void runListBatch(List<Landlord> list, String identity, IdentityExtractor<Landlord> extractor, int batchSize) {
        for (int b = 0; b < batchSize; b++) {
            Optional<Landlord> res = linearSearch(list, identity, extractor);
            sink ^= res.hashCode();
        }
    }

    private static <E> Optional<E> linearSearch(
            List<E> items, String identity, IdentityExtractor<E> extractor) {
        for (E item : items) {
            if (identity.equals(extractor.identityOf(item))) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }
}
