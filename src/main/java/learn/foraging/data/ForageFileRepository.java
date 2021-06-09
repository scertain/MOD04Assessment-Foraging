package learn.foraging.data;

import learn.foraging.models.Category;
import learn.foraging.models.Forage;
import learn.foraging.models.Forager;
import learn.foraging.models.Item;
import learn.foraging.models.reports.CollectedCategoryValue;
import learn.foraging.models.reports.CollectedItemWeight;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ForageFileRepository implements ForageRepository {

    private static final String HEADER = "id,forager_id,item_id,kg";
    private final String directory;
    private final ForagerRepository foragerRepository;
    private final ItemRepository itemRepository;

    public ForageFileRepository(@Value("${forageDataDirectory:./data/forage_data}") String directory,
                                ForagerRepository foragerRepository, ItemRepository itemRepository) {
        this.directory = directory;
        this.foragerRepository = foragerRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Forage> findByDate(LocalDate date) {
        ArrayList<Forage> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(getFilePath(date)))) {

            reader.readLine(); // read header

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {

                String[] fields = line.split(",", -1);
                if (fields.length == 4) {
                    result.add(deserialize(fields, date));
                }
            }
        } catch (IOException ex) {
            // don't throw on read
        }

        // Retrieve a map of the foragers and items
        // to facilitate the lookup of a record by its id.
        Map<String, Forager> foragerMap = foragerRepository.findAll().stream()
                .collect(Collectors.toMap(i -> i.getId(), i -> i));
        Map<Integer, Item> itemMap = itemRepository.findAll().stream()
                .collect(Collectors.toMap(i -> i.getId(), i -> i));

        // Loop through the results and update the forager and item
        // fields with the corresponding record in the forager and item maps.
        for (Forage forage : result) {
            forage.setForager(foragerMap.get(forage.getForager().getId()));
            forage.setItem(itemMap.get(forage.getItem().getId()));
        }
        return result;
    }

    @Override
    public Forage findByDateForagerItem(LocalDate date, String foragerId, int itemId) {
        return findByDate(date).stream()
                .filter(f -> f.getForager().getId().equalsIgnoreCase(foragerId) && f.getItem().getId() == itemId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<CollectedItemWeight> findCollectedItemWeight(LocalDate date) {
        return findByDate(date).stream()
                .collect((Collectors.groupingBy(
                        f -> f.getItem().getName(),
                        Collectors.summarizingDouble(Forage::getKilograms))))
                .entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(es -> new CollectedItemWeight() {
                    public String getItemName() { return es.getKey(); }
                    public double getKilograms() { return es.getValue().getSum(); }
                })
                .collect((Collectors.toList()));
    }

    @Override
    public List<CollectedCategoryValue> findCollectedCategoryValue(LocalDate date) {
        return findByDate(date).stream()
                .collect((Collectors.groupingBy(f -> f.getItem().getCategory(),
                        Collectors.summarizingDouble(f -> f.getValue().doubleValue()))))
                .entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(es -> new CollectedCategoryValue() {
                    public Category getCategory() { return es.getKey(); }
                    public BigDecimal getValue() { return new BigDecimal(es.getValue().getSum()); }
                })
                .collect(Collectors.toList());
    }

    @Override
    public Forage add(Forage forage) throws DataException {
        List<Forage> all = findByDate(forage.getDate());
        forage.setId(java.util.UUID.randomUUID().toString());
        all.add(forage);
        writeAll(all, forage.getDate());
        return forage;
    }

//    @Override
//    public boolean update(Forage forage) throws DataException {
//        List<Forage> all = findByDate(forage.getDate());
//        for (int i = 0; i < all.size(); i++) {
//            if (all.get(i).getId().equals(forage.getId())) {
//                all.set(i, forage);
//                writeAll(all, forage.getDate());
//                return true;
//            }
//        }
//        return false;
//    }

    private String getFilePath(LocalDate date) {
        return Paths.get(directory, date + ".csv").toString();
    }

    private void writeAll(List<Forage> forages, LocalDate date) throws DataException {
        try (PrintWriter writer = new PrintWriter(getFilePath(date))) {

            writer.println(HEADER);

            for (Forage item : forages) {
                writer.println(serialize(item));
            }
        } catch (FileNotFoundException ex) {
            throw new DataException(ex);
        }
    }

    private String serialize(Forage item) {
        return String.format("%s,%s,%s,%s",
                item.getId(),
                item.getForager().getId(),
                item.getItem().getId(),
                item.getKilograms());
    }

    private Forage deserialize(String[] fields, LocalDate date) {
        Forage result = new Forage();
        result.setId(fields[0]);
        result.setDate(date);
        result.setKilograms(Double.parseDouble(fields[3]));

        Forager forager = new Forager();
        forager.setId(fields[1]);
        result.setForager(forager);

        Item item = new Item();
        item.setId(Integer.parseInt(fields[2]));
        result.setItem(item);
        return result;
    }
}
