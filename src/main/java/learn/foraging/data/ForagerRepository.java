package learn.foraging.data;

import learn.foraging.models.Forage;
import learn.foraging.models.Forager;

import java.util.List;

public interface ForagerRepository {

    List<Forager> findAll();

    Forager findById(String id);

    List<Forager> findByLastName(String prefix);

    Forager add(Forager forager) throws DataException;
}
