import java.time.LocalDate;

public final class SearchCriteria {
    public final String attribute;
    public final String value;

    public SearchCriteria(String attribute, String value, LocalDate travelDate) {
        if (attribute == null || attribute.isBlank()) throw new IllegalArgumentException("attribute required");
        if (value == null || value.isBlank()) throw new IllegalArgumentException("value required");
        this.attribute = attribute.trim().toLowerCase();
        this.value = value.trim();
    }

    @Override public String toString() {
        return "SearchCriteria{" + attribute + "=" + value  + "}";
    }
}