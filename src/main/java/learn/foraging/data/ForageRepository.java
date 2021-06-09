package learn.foraging.data;

import learn.foraging.models.Forage;
import learn.foraging.models.reports.CollectedCategoryValue;
import learn.foraging.models.reports.CollectedItemWeight;

import java.time.LocalDate;
import java.util.List;

public interface ForageRepository {
    List<Forage> findByDate(LocalDate date);

    Forage findByDateForagerItem(LocalDate date, String foragerId, int itemId);

    List<CollectedItemWeight> findCollectedItemWeight(LocalDate date);

    List<CollectedCategoryValue> findCollectedCategoryValue(LocalDate date);

    Forage add(Forage forage) throws DataException;

    //boolean update(Forage forage) throws DataException;
}
