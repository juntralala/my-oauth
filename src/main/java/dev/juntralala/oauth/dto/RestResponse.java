package dev.juntralala.oauth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@ToString
@Builder
@JsonInclude(NON_NULL)
public class RestResponse<T> {

    private T body;

    private String error;

    private Map<String, List<String>> errors;
}
