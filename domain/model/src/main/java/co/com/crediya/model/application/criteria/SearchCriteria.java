package co.com.crediya.model.application.criteria;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SearchCriteria {
    private Map<String, Object> filters;
    private String sortBy;
    private String sortDirection;
    private int page;
    private int size;
}
