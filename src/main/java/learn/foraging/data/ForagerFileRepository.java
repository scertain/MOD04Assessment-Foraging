package learn.foraging.data;

import learn.foraging.models.Forager;
import learn.foraging.models.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ForagerFileRepository implements ForagerRepository {
    private static final String HEADER = "id, first_name, last_name, state";
    private static final String COMMA_TOKEN = "~~~";

    private final String filePath;

    public ForagerFileRepository(@Value("${foragerDataFilePath:./data/foragers.csv}") String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<Forager> findAll() {
        ArrayList<Forager> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            reader.readLine(); // read header

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {

                String[] fields = line.split(",", -1);
                if (fields.length == 4) {
                    result.add(deserialize(fields));
                }
            }
        } catch (IOException ex) {
            // don't throw on read
        }
        return result;
    }

    @Override
    public Forager findById(String id) {
        return findAll().stream()
                .filter(i -> i.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Forager> findByLastName(String prefix) {
        return findAll().stream()
                .filter(i -> i.getLastName().toLowerCase().startsWith(prefix.toLowerCase()))
                .sorted(Comparator.comparing(Forager::getLastName).thenComparing(Forager::getFirstName))
                .collect(Collectors.toList());
    }

    @Override
    public Forager add(Forager forager) throws DataException {
        if (forager == null) {
            return null;
        }
        List<Forager> all = findAll();
        forager.setId(java.util.UUID.randomUUID().toString());
        all.add(forager);
        writeAll(all);
        return forager;
    }

//    @Override
//    public List<Forager> findByState(String stateAbbr) {
//        return findAll().stream()
//                .filter(i -> i.getState().equalsIgnoreCase(stateAbbr))
//                .collect(Collectors.toList());
//    }

    private String serialize(Forager forager) {
        return String.format("%s,%s,%s,%s",
                forager.getId(),
                replaceCommasWithToken(forager.getFirstName()),
                replaceCommasWithToken(forager.getLastName()),
                replaceCommasWithToken(forager.getState()));
    }
    
    private Forager deserialize(String[] fields) {
        Forager result = new Forager();
        result.setId(fields[0]);
        result.setFirstName(fields[1]);
        result.setLastName(fields[2]);
        result.setState(fields[3]);
        return result;
    }

    private String replaceCommasWithToken(String value) { return  value.replace(",", COMMA_TOKEN); }

    private String replaceTokensWithCommas(String value) { return value.replace(COMMA_TOKEN, ","); }

    private void writeAll(List<Forager> foragers) throws DataException {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.println(HEADER);
            if (foragers == null) {
                return;
            }

            for (Forager forager : foragers) {
                writer.println(serialize(forager));
            }
        } catch (FileNotFoundException ex) {
            throw new DataException(ex);
        }
    }
}
