package com.github.pwilcz.sort.mapper.web;

import com.github.pwilcz.sort.mapper.domain.SortParam;
import com.github.pwilcz.sort.mapper.domain.SortParams;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MappingSortHandlerMethodArgumentResolver extends SortHandlerMethodArgumentResolver {
    @Override
    public Sort resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Sort sort =  super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        MergedAnnotation<SortParams> sortParams = MergedAnnotations.from(parameter, parameter.getParameterAnnotations()).get(SortParams.class);

        if (!sortParams.isPresent()) {
            return sort;
        }

        return mapSort(sort, sortParams.synthesize());
    }

    private Sort mapSort(Sort sort, SortParams params) {
        Map<String, String> parameterMap = Arrays.stream(params.values())
                .collect(Collectors.toMap(
                        SortParam::name,
                        SortParam::target
                ));

        List<Sort.Order> orders = sort.stream()
                .map(order -> mapOrder(order, parameterMap.get(order.getProperty())))
                .toList();

        return Sort.by(orders);
    }

    private Sort.Order mapOrder(Sort.Order order, String value) {
        return value != null ? order.withProperty(value) : order;
    }
}
