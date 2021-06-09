package learn.foraging.models.reports;

import learn.foraging.models.Category;

import java.math.BigDecimal;

public interface CollectedCategoryValue {
    Category getCategory();
    BigDecimal getValue();
}
