package learn.foraging.data.doubles;

import learn.foraging.data.ForagerRepository;
import learn.foraging.models.Forager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ForagerRepositoryDouble implements ForagerRepository {
    public static final String FORAGER1_ID = "0e4707f4-407e-4ec9-9665-baca0aabe88c";
    public static final String FORAGER2_ID = "0ffc0532-8976-4859-af6e-fedbe192fca7";

    public final static Forager FORAGER = makeForager();

    private final ArrayList<Forager> foragers = new ArrayList<>();

    public ForagerRepositoryDouble() {
        foragers.add(FORAGER);
    }

    @Override
    public Forager findById(String id) {
        return foragers.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Forager> findAll() {
        return foragers;
    }

//    @Override
//    public List<Forager> findByState(String stateAbbr) {
//        return foragers.stream()
//                .filter(i -> i.getState().equalsIgnoreCase(stateAbbr))
//                .collect(Collectors.toList());
//    }

    @Override
    public List<Forager> findByLastName(String prefix) {
        return findAll().stream()
                .filter(i -> i.getLastName().toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Forager add(Forager forager) {
        forager.setId(java.util.UUID.randomUUID().toString());
        return forager;
    }

    private static Forager makeForager() {
        Forager forager = new Forager();
        forager.setId("0e4707f4-407e-4ec9-9665-baca0aabe88c");
        forager.setFirstName("Jilly");
        forager.setLastName("Sisse");
        forager.setState("GA");
        return forager;
    }

}
