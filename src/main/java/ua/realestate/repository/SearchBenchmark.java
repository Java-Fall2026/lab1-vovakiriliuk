package ua.realestate.repository;

import ua.realestate.model.Landlord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SearchBenchmark {

    private static final int ITEM_COUNT = 100_000;
    private static final int WARMUP_RUNS = 20;
    private static final int MEASURE_RUNS = 25;

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

        System.out.println("Warming up JIT compiler...");
        for (int i = 0; i < WARMUP_RUNS; i++) {
            repository.findByIdentity(firstId);
            repository.findByIdentity(lastId);
            repository.findByIdentity(absentId);

            linearSearch(itemsList, firstId, extractor);
            linearSearch(itemsList, lastId, extractor);
            linearSearch(itemsList, absentId, extractor);
        }

        System.out.println("Running benchmark (" + MEASURE_RUNS + " iterations)...");

        long mapFirst = measureMap(repository, firstId);
        long mapLast = measureMap(repository, lastId);
        long mapAbsent = measureMap(repository, absentId);

        long listFirst = measureList(itemsList, firstId, extractor);
        long listLast = measureList(itemsList, lastId, extractor);
        long listAbsent = measureList(itemsList, absentId, extractor);

        System.out.println("\nBenchmark results (median time in nanoseconds / microseconds):");
        System.out.println("+----------------------+--------------------+--------------------+");
        System.out.println("| Scenario             | Map (HashMap/LHM)  | List (Linear)      |");
        System.out.println("+----------------------+--------------------+--------------------+");
        System.out.printf("| First element (0)    | %8d ns (%6.2f µs) | %8d ns (%6.2f µs) |\n",
                mapFirst, mapFirst / 1000.0, listFirst, listFirst / 1000.0);
        System.out.printf("| Last element (99999) | %8d ns (%6.2f µs) | %8d ns (%6.2f µs) |\n",
                mapLast, mapLast / 1000.0, listLast, listLast / 1000.0);
        System.out.printf("| Absent element       | %8d ns (%6.2f µs) | %8d ns (%6.2f µs) |\n",
                mapAbsent, mapAbsent / 1000.0, listAbsent, listAbsent / 1000.0);
        System.out.println("+----------------------+--------------------+--------------------+");
    }

    private static long measureMap(GenericRepository<Landlord> repo, String identity) {
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < MEASURE_RUNS; i++) {
            long start = System.nanoTime();
            Optional<Landlord> res = repo.findByIdentity(identity);
            long end = System.nanoTime();
            if (identity.equals("TAX-NONEXISTENT")) {
                if (res.isPresent()) throw new IllegalStateException();
            } else {
                if (res.isEmpty()) throw new IllegalStateException();
            }
            times.add(end - start);
        }
        Collections.sort(times);
        return times.get(times.size() / 2);
    }

    private static long measureList(List<Landlord> list, String identity, IdentityExtractor<Landlord> extractor) {
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < MEASURE_RUNS; i++) {
            long start = System.nanoTime();
            Optional<Landlord> res = linearSearch(list, identity, extractor);
            long end = System.nanoTime();
            if (identity.equals("TAX-NONEXISTENT")) {
                if (res.isPresent()) throw new IllegalStateException();
            } else {
                if (res.isEmpty()) throw new IllegalStateException();
            }
            times.add(end - start);
        }
        Collections.sort(times);
        return times.get(times.size() / 2);
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
