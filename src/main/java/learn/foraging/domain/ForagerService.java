package learn.foraging.domain;

import learn.foraging.data.DataException;
import learn.foraging.data.ForagerRepository;
import learn.foraging.models.Forage;
import learn.foraging.models.Forager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ForagerService {

    private final ForagerRepository repository;

    public ForagerService(ForagerRepository repository) {
        this.repository = repository;
    }

    public List<Forager> findByLastName(String prefix) {
        return repository.findByLastName(prefix);
    }

    public Result<Forager> add(Forager forager) throws DataException {
        Result<Forager> result = validate(forager);
        if (!result.isSuccess()) {
            return result;
        }
        result.setPayload(repository.add(forager));

        return result;
    }

    public Result<Forager> validate(Forager forager) {
        Result<Forager> result =validateNulls(forager);
        if (!result.isSuccess()) {
            return result;
        }
        validateFields(forager, result);

        return result;
    }

    public Result<Forager> validateNulls(Forager forager) {
        Result<Forager> result = new Result<>();

        if (forager == null) {
            result.addErrorMessage("Nothing to save.");
            return result;
        }

        if (forager.getFirstName() == null || forager.getFirstName().isBlank()) {
            result.addErrorMessage("Forager First Name is Required.");
        }
        if (forager.getLastName() == null || forager.getLastName().isBlank()) {
            result.addErrorMessage("Forager Last Name is Required.");
        }
        if (forager.getState() == null || forager.getState().isBlank()) {
            result.addErrorMessage("Forager State iS Required.");
        }
        return result;
    }

    public void validateFields(Forager forager, Result<Forager> result) {
        if (repository.findAll().stream()
                .anyMatch(i -> i.getFirstName().equalsIgnoreCase(forager.getFirstName())
                        && i.getLastName().equalsIgnoreCase(forager.getLastName())
                        && i.getState().equalsIgnoreCase(forager.getState()))) {
            result.addErrorMessage(
                    String.format("Forager first name '%s', last name '%s', and state '%s' is a duplicate.",
                            forager.getFirstName(),
                            forager.getLastName(),
                            forager.getState()));
        }
    }
}
