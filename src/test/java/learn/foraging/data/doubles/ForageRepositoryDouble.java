package learn.foraging.data.doubles;

import learn.foraging.data.DataException;
import learn.foraging.data.ForageRepository;
import learn.foraging.data.ForagerRepository;
import learn.foraging.data.ItemRepository;
import learn.foraging.models.Forage;
import learn.foraging.models.Forager;
import learn.foraging.models.Item;
import learn.foraging.models.reports.CollectedCategoryValue;
import learn.foraging.models.reports.CollectedItemWeight;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ForageRepositoryDouble implements ForageRepository {

    public final static LocalDate FORAGE_DATE = LocalDate.of(2020, 1, 1);

    private final ArrayList<Forage> forages = new ArrayList<>();

    public ForageRepositoryDouble(ForagerRepository foragerRepository, ItemRepository itemRepository) {
        Forager forager1 = foragerRepository.findById(ForagerRepositoryDouble.FORAGER1_ID);
        Forager forager2 = foragerRepository.findById(ForagerRepositoryDouble.FORAGER2_ID);

        Item item1 = itemRepository.findById(1);
        Item item2 = itemRepository.findById(2);

        forages.add(new Forage("2915610e-b286-4c96-8cee-05928b3ac924", FORAGE_DATE, forager1, item1, 1.2));
        forages.add(new Forage("e90bc038-4a48-4513-ba73-01e989527efa", FORAGE_DATE, forager2, item2, 2.3));
    }

    @Override
    public List<Forage> findByDate(LocalDate date) {
        return forages.stream()
                .filter(i -> i.getDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public Forage findByDateForagerItem(LocalDate date, String foragerId, int itemId) {
        List<Forage> forages = findByDate(date);

        return forages.stream()
                .filter(f -> f.getId().equalsIgnoreCase(foragerId) && f.getItem().getId() == itemId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<CollectedItemWeight> findCollectedItemWeight(LocalDate date) {
        return null;
    }

    @Override
    public List<CollectedCategoryValue> findCollectedCategoryValue(LocalDate date) {
        return null;
    }

    @Override
    public Forage add(Forage forage) throws DataException {
        forage.setId(java.util.UUID.randomUUID().toString());
        return forage;
    }

//    @Override
//    public boolean update(Forage forage) throws DataException {
//        return false;
//    }
}
