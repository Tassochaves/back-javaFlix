package com.dev.java_flix.util;

import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.dev.java_flix.dto.response.PageResponse;


//padroniza a paginação e a conversão de respostas da API.
public class PaginationUtils {

    private PaginationUtils(){

    }

    //Cria um objeto Pageable com ordenação decrescente
    public static Pageable createPageRequest(int page, int size, String sortBy){
        
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
    }

    //Cria um objeto Pageable simples, sem ordenação específica
    public static Pageable createPageRequest(int page, int size){
        
        return PageRequest.of(page, size);
    }

    /**
     * Converte uma Page do Spring (com Entidades) para um PageResponse (com DTOs).
     * @param <T> Tipo da Entidade original (ex: Filme).
     * @param <R> Tipo do DTO de resposta (ex: FilmeResponse).
     * @param page O objeto Page retornado pelo Repository.
     * @param mapper função que define como converter T para R.
     * @return Um objeto PageResponse formatado para o Frontend.
     */
    public static <T, R>PageResponse<R> toPageResponse(Page<T> page, Function<T, R> mapper){

        List<R> content = page.getContent().stream().map(mapper).toList();

        return new PageResponse<>(content, page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize());
    }


    //Útil quando a conversão é complexa demais para uma função simples.
    public static <R> PageResponse<R> toPageResponse(Page<?> page, List<R> mappedContent){

        return new PageResponse<>(
            mappedContent,
            page.getTotalElements(),
            page.getTotalPages(),
            page.getNumber(),
            page.getSize()
        );
    }
}
