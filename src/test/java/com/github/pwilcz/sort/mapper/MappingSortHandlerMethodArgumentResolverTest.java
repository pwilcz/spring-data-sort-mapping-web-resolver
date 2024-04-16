package com.github.pwilcz.sort.mapper;

import com.github.pwilcz.sort.mapper.domain.SortParam;
import com.github.pwilcz.sort.mapper.domain.SortParams;
import com.github.pwilcz.sort.mapper.web.MappingSortHandlerMethodArgumentResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

class MappingSortHandlerMethodArgumentResolverTest {

    final SortArgumentResolver sortResolver = new MappingSortHandlerMethodArgumentResolver();
    final static String[] SORT_PARAM = new String[]{"param0,asc", "param1,asc", "param2,desc"};
    NativeWebRequest request = Mockito.mock(NativeWebRequest.class);

    @BeforeEach
    void setup() {
        Mockito.when(request.getParameterValues("sort")).thenReturn(SORT_PARAM);
    }

    @Test
    void shouldMapSort() throws NoSuchMethodException {
        //given
        MethodParameter parameter = getParameter("methodWithSort", Sort.class);

        //when
        Sort sort = sortResolver.resolveArgument(parameter, null, request, null);

        //then
        assertSort(sort);
    }

    private static void assertSort(Sort sort) {
        Assertions.assertEquals(
                Sort.by(
                        new Sort.Order(Sort.Direction.ASC, "param0"),
                        new Sort.Order(Sort.Direction.ASC, "field1"),
                        new Sort.Order(Sort.Direction.DESC, "field2")
                ),
                sort);
    }

    @Test
    void shouldMapSortWithAnnotationDefinedMapping() throws NoSuchMethodException {
        //given
        MethodParameter parameter = getParameter("methodWithInheritedAnnotation", Sort.class);

        //when
        Sort sort = sortResolver.resolveArgument(parameter, null, request, null);

        //then
        assertSort(sort);
    }

    @Test
    void shouldMapPageable() throws NoSuchMethodException {
        //given
        MethodParameter parameter = getParameter("methodWithPageable", Pageable.class);

        //when
        Pageable pageable = new PageableHandlerMethodArgumentResolver(sortResolver).resolveArgument(parameter, null, request, null);

        //then
        assertSort(pageable.getSort());
    }

    private static MethodParameter getParameter(String method, Class<?> c) throws NoSuchMethodException {
        return MethodParameter.forExecutable(Controller.class.getDeclaredMethod(method, c), 0);
    }

    private static String[] sortParam(String... params) {
        return params;
    }


    static class Controller {
        void methodWithSort(@SortParams(
                values = {
                        @SortParam(name = "param1", target = "field1"),
                        @SortParam(name = "param2", target = "field2")
                }
        ) Sort sort) {
        }

        void methodWithInheritedAnnotation(@PredefinedSortMapping Sort sort) {
        }

        void methodWithPageable(@SortParams(
                values = {
                        @SortParam(name = "param1", target = "field1"),
                        @SortParam(name = "param2", target = "field2")
                }
        ) Pageable pageable) {
        }
    }

    @SortParams(
            values = {
                    @SortParam(name = "param1", target = "field1"),
                    @SortParam(name = "param2", target = "field2")
            }
    )
    @Retention(RetentionPolicy.RUNTIME)
    @interface PredefinedSortMapping {
    }

}
