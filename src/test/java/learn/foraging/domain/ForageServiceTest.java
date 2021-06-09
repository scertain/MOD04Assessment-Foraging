package learn.foraging.domain;

import learn.foraging.data.DataException;
import learn.foraging.data.doubles.ForageRepositoryDouble;
import learn.foraging.data.doubles.ForagerRepositoryDouble;
import learn.foraging.data.doubles.ItemRepositoryDouble;
import learn.foraging.models.Category;
import learn.foraging.models.Forage;
import learn.foraging.models.Forager;
import learn.foraging.models.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ForageServiceTest {

    private ForagerRepositoryDouble foragerRepository = new ForagerRepositoryDouble();
    private ItemRepositoryDouble itemRepository = new ItemRepositoryDouble();
    private ForageService service;

    @BeforeEach
        void setup() {
    ForageRepositoryDouble repository = new ForageRepositoryDouble(foragerRepository, itemRepository);
    service = new ForageService(repository, foragerRepository, itemRepository);
}


    @Test
    void shouldAddWithValidData() throws DataException {
        Forager forager = foragerRepository.findById(ForagerRepositoryDouble.FORAGER1_ID);
        Item item = itemRepository.findById(1);

        Forage forage = new Forage(null, LocalDate.of(2020, 1, 2),
                forager, item, 2.4);

        Result<Forage> result = service.add(forage);

        assertTrue(result.isSuccess());
        assertNotNull(result.getPayload().getId());
    }

    @Test
    void shouldNotAddWithoutForageObject() throws DataException {
        Result<Forage> result = service.add(null);

        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotAddWithoutDate() throws DataException {
        Forage forage = new Forage(null, null, null, null, 2.4);

        Result<Forage> result = service.add(null);

        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotAddWithoutForager() throws DataException {
        Forage forage = new Forage(null, LocalDate.of(2020, 1, 1),
                null, null, 2.4);

        Result<Forage> result = service.add(null);

        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotAddWithoutForagerId() throws DataException {
        Forager forager = new Forager();

        Forage forage = new Forage(null, LocalDate.of(2020, 1, 1),
                forager, null, 2.4);

        Result<Forage> result = service.add(null);

        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotAddWithoutItem() throws DataException {
        Forager forager = foragerRepository.findById(ForagerRepositoryDouble.FORAGER1_ID);

        Forage forage = new Forage(null, LocalDate.of(2020, 1, 1),
                forager, null, 2.4);

        Result<Forage> result = service.add(null);

        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotAddWithFutureDate() throws DataException {
        Forager forager = foragerRepository.findById(ForagerRepositoryDouble.FORAGER1_ID);
        Item item = itemRepository.findById(1);

        Forage forage = new Forage(null, LocalDate.now().plusDays(1),
                forager, item, 2.4);

        Result<Forage> result = service.add(forage);

        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotAddWithZeroKilograms() throws DataException {
        Forager forager = foragerRepository.findById(ForagerRepositoryDouble.FORAGER1_ID);
        Item item = itemRepository.findById(1);

        Forage forage = new Forage(null, LocalDate.of(2020, 1, 2),
                forager, item, ForageService.MIN_FORAGE_KILOGRAMS - 0.001);

        Result<Forage> result = service.add(forage);

        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotAddWithGreaterThanMaxKilograms() throws DataException {
        Forager forager = foragerRepository.findById(ForagerRepositoryDouble.FORAGER1_ID);
        Item item = itemRepository.findById(1);

        Forage forage = new Forage(null, LocalDate.of(2020, 1, 2),
                forager, item, ForageService.MAX_FORAGE_KILOGRAMS + 0.001);

        Result<Forage> result = service.add(forage);

        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotAddWithNonUniqueForagerItemDate() throws DataException {
        Forager forager = foragerRepository.findById(ForagerRepositoryDouble.FORAGER1_ID);
        Item item = itemRepository.findById(1);

        Forage forage = new Forage(null, LocalDate.of(2020, 1, 1),
                forager, item, 2.4);

        Result<Forage> result = service.add(forage);

        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldGenerate() throws DataException {
        Result<List<Forage>> result = service.generate(
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 1, 8),
                10);

        assertTrue(result.isSuccess());
        assertNotNull(result.getPayload());
        assertEquals(10, result.getPayload().size());
    }

    @Test
    void shouldNotGenerateWithNullStartDate() throws DataException {
        Result<List<Forage>> result = service.generate(
                null,
                LocalDate.of(2020, 1, 8),
                10);

        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotGenerateWithNullEndDate() throws DataException {
        Result<List<Forage>> result = service.generate(
                LocalDate.of(2020, 1, 1),
                null,
                10);

        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotGenerateWithFutureDates() throws DataException {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        Result<List<Forage>> result = service.generate(
                tomorrow,
                tomorrow.plusDays(7),
                10);

        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotGenerateWithFutureEndDate() throws DataException {
        Result<List<Forage>> result = service.generate(
                LocalDate.of(2020, 1, 1),
                LocalDate.now().plusDays(1),
                10);

        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotGenerateWithEndDateThatComesBeforeStartDate() throws DataException {
        Result<List<Forage>> result = service.generate(
                LocalDate.of(2020, 1, 8),
                LocalDate.of(2020, 1, 1),
                10);

        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotGenerateWithLowCount() throws DataException {
        Result<List<Forage>> result = service.generate(
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 1, 8),
                0);

        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotGenerateWithHighCount() throws DataException {
        Result<List<Forage>> result = service.generate(
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 1, 8),
                ForageService.GENERATE_FORAGES_LIMIT + 1);

        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }
}