package learn.foraging.data.doubles;

import learn.foraging.data.DataException;
import learn.foraging.data.ItemRepository;
import learn.foraging.models.Category;
import learn.foraging.models.Item;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRepositoryDouble implements ItemRepository {

    public static final String ITEM1_NAME = "Chanterelle";
    private final ArrayList<Item> items = new ArrayList<>();

    //public final static Item ITEM = new Item(1, "Chanterelle", Category.EDIBLE, new BigDecimal("9.99"));

    public ItemRepositoryDouble() {
        items.add(new Item(1,ITEM1_NAME, Category.EDIBLE, new BigDecimal("9.99")));
        items.add(new Item(2, "Ramps", Category.EDIBLE, new BigDecimal("5.00")));
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items);
    }

    @Override
    public List<Item> findByCategory(Category category) {
        return findAll().stream()
                .filter(i -> i.getCategory() == category)
                .collect(Collectors.toList());
    }

    @Override
    public Item findById(int id) {
        return findAll().stream()
                .filter(i -> i.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Item add(Item item) throws DataException {
        List<Item> all = findAll();

        int nextId = all.stream()
                .mapToInt(Item::getId)
                .max()
                .orElse(0) + 1;

        item.setId(nextId);

        all.add(item);
        return item;
    }
}
