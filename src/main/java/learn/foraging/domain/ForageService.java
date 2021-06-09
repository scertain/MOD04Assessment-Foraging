package learn.foraging.domain;

import learn.foraging.data.DataException;
import learn.foraging.data.ForageRepository;
import learn.foraging.data.ForagerRepository;
import learn.foraging.data.ItemRepository;
import learn.foraging.models.Forage;
import learn.foraging.models.Forager;
import learn.foraging.models.Item;
import learn.foraging.models.reports.CollectedCategoryValue;
import learn.foraging.models.reports.CollectedItemWeight;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ForageService {
    public static final double MIN_FORAGE_KILOGRAMS = 0.001;
    public static final double MAX_FORAGE_KILOGRAMS = 250;
    public static final int GENERATE_FORAGES_LIMIT = 500;

    private final ForageRepository forageRepository;
    private final ForagerRepository foragerRepository;
    private final ItemRepository itemRepository;

    public ForageService(ForageRepository forageRepository, ForagerRepository foragerRepository, ItemRepository itemRepository) {
        this.forageRepository = forageRepository;
        this.foragerRepository = foragerRepository;
        this.itemRepository = itemRepository;
    }

    public List<Forage> findByDate(LocalDate date) { return forageRepository.findByDate(date); }

    public List<CollectedItemWeight> findCollectedItemWeight(LocalDate date) {
        return forageRepository.findCollectedItemWeight(date);
    }

    public List<CollectedCategoryValue> findCollectedCategoryValue(LocalDate date) {
        return forageRepository.findCollectedCategoryValue(date);
    }

    public Result<Forage> add(Forage forage) throws DataException {
        Result<Forage> result = validate(forage);
        if (!result.isSuccess()) {
            return result;
        }
        result.setPayload(forageRepository.add(forage));

        return result;
    }

    public Result<List<Forage>> generate(LocalDate start, LocalDate end, int count) throws DataException {
        Result<List<Forage>> result = validateGenerate(start, end, count);
        if (!result.isSuccess()) {
            return result;
        }

        ArrayList<LocalDate> dates = new ArrayList<>();
        while (!start.isAfter(end)) {
            dates.add(start);
            start = start.plusDays(1);
        }

        List<Item> items = itemRepository.findAll();
        List<Forager> foragers = foragerRepository.findAll();

        ArrayList<Forage> generatedForages = new ArrayList<>();

        Random random = new Random();

        for (int i = 0; i < count; i++) {
            Forage forage = new Forage();
            forage.setDate(dates.get(random.nextInt(dates.size())));
            forage.setForager(foragers.get(random.nextInt(foragers.size())));
            forage.setItem(items.get(random.nextInt(items.size())));
            forage.setKilograms(random.nextDouble() * 5.0 + 0.1);

            generatedForages.add(forageRepository.add(forage));
        }
        result.setPayload(generatedForages);
        return result;
    }

    private Result<List<Forage>> validateGenerate(LocalDate start, LocalDate end, int count) {
        Result<List<Forage>> result = validateGenerateNulls(start, end);
        if (!result.isSuccess()) {
            return result;
        }
        validateGenerateValues(start, end, count, result);
        return result;
    }

    private Result<List<Forage>> validateGenerateNulls(LocalDate start, LocalDate end) {
        Result<List<Forage>> result = new Result<>();

        if (start == null) {
            result.addErrorMessage("Start date is required.");
        }

        if (end == null) {
            result.addErrorMessage("End date is required.");
        }

        return result;
    }

    private void validateGenerateValues(LocalDate start, LocalDate end, int count, Result<List<Forage>> result) {
        if (start.isAfter(LocalDate.now())) {
            result.addErrorMessage("Start date must be in the past.");
        }
        if (end.isAfter(LocalDate.now()) || end.isBefore(start)) {
            result.addErrorMessage("End date must be in the [ast and after the start date.");
        }
        if (count < 1 || count > GENERATE_FORAGES_LIMIT) {
            result.addErrorMessage(String.format("The number of generated forages must be between 1 and %s",
                    GENERATE_FORAGES_LIMIT));
        }
    }

    private Result<Forage> validateForageNulls(Forage forage) {
        Result<Forage> result = new Result<>();

        if (forage == null) {
            result.addErrorMessage("Nothing to save.");
            return result;
        }
        if (forage.getDate() == null) {
            result.addErrorMessage("Forage date is required.");
        }
        if (forage.getForager() == null) {
            result.addErrorMessage("Forager is required.");
        }
        if (forage.getItem() == null) {
            result.addErrorMessage("Item is required.");
        }
        return result;
    }

    private Result<Forage> validate(Forage forage) {

        Result<Forage> result = validateNulls(forage);
        if (!result.isSuccess()) {
            return result;
        }

        validateFields(forage, result);
        if (!result.isSuccess()) {
            return result;
        }

        validateChildrenExist(forage, result);

        return result;
    }

    private Result<Forage> validateNulls(Forage forage) {
        Result<Forage> result = new Result<>();

        if (forage == null) {
            result.addErrorMessage("Nothing to save.");
            return result;
        }

        if (forage.getDate() == null) {
            result.addErrorMessage("Forage date is required.");
        }

        if (forage.getForager() == null) {
            result.addErrorMessage("Forager is required.");
        }

        if (forage.getItem() == null) {
            result.addErrorMessage("Item is required.");
        }
        return result;
    }

    private void validateFields(Forage forage, Result<Forage> result) {
        // No future dates.
        if (forage.getDate().isAfter(LocalDate.now())) {
            result.addErrorMessage("Forage date cannot be in the future.");
        }

        if (forage.getKilograms() <= 0 || forage.getKilograms() > 250.0) {
            result.addErrorMessage("Kilograms must be a positive number less than 250.0");
        }
    }

    private void validateChildrenExist(Forage forage, Result<Forage> result) {

        if (forage.getForager().getId() == null
                || foragerRepository.findById(forage.getForager().getId()) == null) {
            result.addErrorMessage("Forager does not exist.");
        }

        if (itemRepository.findById(forage.getItem().getId()) == null) {
            result.addErrorMessage("Item does not exist.");
        }
    }
}
