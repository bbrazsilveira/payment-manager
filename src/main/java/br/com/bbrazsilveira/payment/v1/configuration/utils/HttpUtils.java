package br.com.bbrazsilveira.payment.v1.configuration.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class HttpUtils {

    private static HttpHeaders locationHeader(URI uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);
        return headers;
    }

    public static HttpHeaders locationHeaderById(String id) {
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
        return locationHeader(uri);
    }

    public static HttpHeaders locationHeaderByUrl(String url) {
        URI uri = URI.create(url);
        return locationHeader(uri);
    }
}
