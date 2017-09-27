package com.newdun.cloud.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.newdun.cloud.domain.StockResult;

/**
 * Service Implementation for managing Tracert.
 */
@Service
@Transactional
public class StockService {
    private final Logger log = LoggerFactory.getLogger(StockService.class);

    public StockService() {
    }

    /**
     * Search for the tracert corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public StockResult get(String stock, Date begin, Date end) {
    	final String uri = "http://q.stock.sohu.com/hisHq?code=cn_{stock}&start={start_date}&end={end_date}&stat=1&order=A&period=d";//&callback=historySearchHandler&rt=jsonp";
        Map<String, String> params = new HashMap<String, String>();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");

        params.put("stock", stock);
        params.put("start_date", df.format(begin));
        params.put("end_date", df.format(end));

        RestTemplate restTemplate = new RestTemplate();

        List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;
                jsonConverter.setObjectMapper(new ObjectMapper() {
					private static final long serialVersionUID = 1L;

					@Override
                	public <T> T readValue(String content, JavaType valueType)
                          throws IOException, JsonParseException, JsonMappingException {
                		content = new String(content.getBytes("gbk"), "utf-8");
                		return super.readValue(content, valueType);
                	}
					
					@Override
                	public <T> T readValue(InputStream content, JavaType valueType)
                          throws IOException, JsonParseException, JsonMappingException {
						StringWriter writer = new StringWriter();
						IOUtils.copy(content, writer, Charset.forName("GBK"));
						String theString = writer.toString();
                		return super.readValue(theString, valueType);
                	}
                });
                jsonConverter.setSupportedMediaTypes(ImmutableList.of(
                		new MediaType("application", "json", MappingJackson2HttpMessageConverter.DEFAULT_CHARSET),
                		new MediaType("application", "x-javascript", Charset.forName("GBK"))));
            }
        }
        
        ResponseEntity<StockResult[]> responseEntity = restTemplate.getForEntity(uri, StockResult[].class, params);
        StockResult[] result = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();

        return result[0];
    }
}
