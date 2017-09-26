package com.newdun.cloud.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.newdun.cloud.domain.StockDayInfo;
import com.newdun.cloud.domain.StockResult;
import com.newdun.cloud.repository.TracertRepository;
import com.newdun.cloud.repository.search.TracertSearchRepository;
import com.newdun.cloud.service.dto.TracertDTO;
import com.newdun.cloud.service.mapper.TracertMapper;

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
    	final String uri = "http://q.stock.sohu.com/hisHq?code=cn_{stock}&start={start_date}& amp;end={end_date}&stat=1&order=D&period=d& callback=historySearchHandler&rt=jsonp";
        Map<String, String> params = new HashMap<String, String>();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");

        params.put("stock", "1");
        params.put("start_date", df.format(begin));
        params.put("end_date", df.format(end));
         
        RestTemplate restTemplate = new RestTemplate();
        StockResult result = restTemplate.getForObject(uri, StockResult.class, params);
        return result;
    }
}
