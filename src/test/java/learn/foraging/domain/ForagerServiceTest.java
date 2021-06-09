package learn.foraging.domain;

import learn.foraging.data.DataException;
import learn.foraging.data.doubles.ForagerRepositoryDouble;
import learn.foraging.models.Forager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

public class ForagerServiceTest {

    private ForagerService service;

    @BeforeEach
    void setUp() { service = new ForagerService(new ForagerRepositoryDouble()); }

    @Test
    void shouldNotSaveNullForagerObject() throws DataException {
        Result<Forager> result = service.add(null);
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotSaveNullFirstName() throws DataException {
        Forager forager = new Forager("", null, "Smith", "OR");
        Result<Forager> result = service.add(forager);
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotSaveBlankFirstName() throws DataException {
        Forager forager = new Forager("", "","Smith", "OR");
        Result<Forager> result = service.add(forager);
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotSaveNullLastName() throws DataException {
        Forager forager = new Forager("", "Bob", null, "OR");
        Result<Forager> result = service.add(forager);
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotSaveBlankLastName() throws DataException {
        Forager forager = new Forager("", "Bob","", "OR");
        Result<Forager> result = service.add(forager);
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotSaveNullState() throws DataException {
        Forager forager = new Forager("", "Bob", "Smith", null);
        Result<Forager> result = service.add(forager);
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotSaveBlankState() throws DataException {
        Forager forager = new Forager("", "Bob","Smith", "");
        Result<Forager> result = service.add(forager);
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    void shouldNotSaveDuplicateFirstNameLastNameStateCombination() throws DataException {
        Forager forager = new Forager("", "Jilly", "Sisse", "GA");
        Result<Forager> result = service.add(forager);
        assertFalse(result.isSuccess());
        assertNull(result.getPayload());
    }

    @Test
    void shouldSave() throws DataException {
        Forager forager = new Forager("", "Bob", "Smith", "OR");
        Result<Forager> result = service.add(forager);
        assertTrue(result.isSuccess());
        assertNotNull(result.getPayload());
        assertNotNull(result.getPayload().getId());
    }

}
