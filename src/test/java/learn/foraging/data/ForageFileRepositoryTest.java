package learn.foraging.data;

import learn.foraging.data.doubles.ForagerRepositoryDouble;
import learn.foraging.data.doubles.ItemRepositoryDouble;
import learn.foraging.models.Category;
import learn.foraging.models.Forage;
import learn.foraging.models.Forager;
import learn.foraging.models.Item;
import learn.foraging.models.reports.CollectedCategoryValue;
import learn.foraging.models.reports.CollectedItemWeight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ForageFileRepositoryTest {

    private ForagerRepositoryDouble foragerRepository;
    private ItemRepositoryDouble itemRepository;

    private static final String TEST_DATA_DIR = "./data/forage_data_test";

    private ForageFileRepository repository;

    final LocalDate date = LocalDate.of(2020, 6, 26);

    @BeforeEach
    void setup() throws IOException {
        foragerRepository = new ForagerRepositoryDouble();
        itemRepository = new ItemRepositoryDouble();

        repository = new ForageFileRepository(
                TEST_DATA_DIR,  foragerRepository, itemRepository);
    }

    @Test
    void shouldFindByDate() {
        List<Forage> forages = repository.findByDate(date);
        assertNotNull(forages);
        assertTrue(forages.size() > 1);
    }

    @Test
    void shouldNotFindWithInvalidDate() {
        List<Forage> forages = repository.findByDate(LocalDate.of(2020, 1, 1));
        assertNotNull(forages);
        assertEquals(0, forages.size());
    }

    @Test
    void shouldFindByDateForagerItem() {
        Forage forage = repository.findByDateForagerItem(date, ForagerRepositoryDouble.FORAGER1_ID, 1);
        assertNotNull(forage);
    }

    @Test
    void shouldFindCollectedItemWeight() {
        List<CollectedItemWeight> result = repository.findCollectedItemWeight(date);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ItemRepositoryDouble.ITEM1_NAME, result.get(0).getItemName());
        assertTrue(result.get(0).getKilograms() > 0);
    }

    @Test
    void shouldFindCollectedCategoryValue() {
        List<CollectedCategoryValue> result = repository.findCollectedCategoryValue(date);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Category.EDIBLE, result.get(0).getCategory());
        assertTrue(result.get(0).getValue().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void shouldCreateForage() throws DataException {
        Forage forage = new Forage();
        forage.setDate(date);
        forage.setKilograms(0.75);

        Item item = itemRepository.findById(1);
        forage.setItem(item);

        Forager forager = foragerRepository.findById(ForagerRepositoryDouble.FORAGER1_ID);
        forage.setForager(forager);

        forage = repository.add(forage);

        assertNotNull(forage.getId());
        assertTrue(forage.getId().length() > 2);
    }
}