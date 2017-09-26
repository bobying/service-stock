package com.newdun.cloud.web.rest;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.newdun.cloud.domain.Info;
import com.newdun.cloud.domain.StockResult;
import com.newdun.cloud.service.InfoQueryService;
import com.newdun.cloud.service.InfoService;
import com.newdun.cloud.service.JudgeService;
import com.newdun.cloud.service.StockService;
import com.newdun.cloud.service.TracertService;
import com.newdun.cloud.service.dto.JudgeDTO;
import com.newdun.cloud.service.dto.TracertDTO;

/**
 * REST controller for managing Info.
 */
@RestController
@RequestMapping("/api")
public class ParseResource {

    private final Logger log = LoggerFactory.getLogger(ParseResource.class);

    private static final String ENTITY_NAME = "info";

    private final InfoService infoService;
    private final StockService stockService;
	private final TracertService tracertService;
	private final JudgeService judgeService;

    public ParseResource(InfoService infoService, InfoQueryService infoQueryService, 
    		StockService stockService, TracertService tracertService, JudgeService judgeService) {
        this.infoService = infoService;
        this.stockService = stockService;
        this.tracertService = tracertService;
        this.judgeService = judgeService;
    }

    /**
     * POST  /infos : Create a new info.
     *
     * @param infoDTO the infoDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new infoDTO, or with status 400 (Bad Request) if the info has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     * @throws ParseException 
     */
    @PostMapping("/infos/parse")
    @Timed
    public ResponseEntity<String> parseInfo() throws URISyntaxException, ParseException {
        log.debug("REST request to parse Info : {}");
        List<Info> infos = infoService.findAll();
        Iterator<Info> it = infos.iterator();
        while (it.hasNext()) {
        	Info info = it.next();
        	String title = info.getTitle();
        	
        	// 转化title的日期和股票
        	Pattern pattern = Pattern.compile("\\D*(\\d{4})[\\.|年](\\d+)[\\.|月](\\d+)\\D+(\\d+).*");

        	Matcher m = pattern.matcher(title);
        	m.matches();
            log.debug("info title：" + title);
            log.debug("group 1：" + m.group(1));
            log.debug("group 2：" + m.group(2));
            log.debug("group 3：" + m.group(3));
            log.debug("group 4：" + m.group(4));
        	Integer year = Integer.valueOf(m.group(1));
        	Integer month = Integer.valueOf(m.group(2));
        	Integer day = Integer.valueOf(m.group(3));
        	String stock = m.group(4); // always empty
        	
        	if (info.getDate() == null) {
	        	ZonedDateTime date = ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.of("Z"));
				info.setDate(date);
        	}
        	info.setStock(stock);
        	infoService.save(info);
        	
        	// 获取股票历史价格
        	Date begin = Date.from(info.getDate().toInstant());
        	Calendar c = Calendar.getInstance();
        	c.setTime(begin);
        	c.add(Calendar.DATE, 1);
        	Date end = c.getTime();
        	Float beginPrice = 0.0F;
        	Float maxPrice = 0.0F;
        	Integer increase_days = 0;
        	
        	StockResult result = stockService.get(stock, begin, end);
        	if (result.getStatus() == 0) {
        		Iterator<List<String>> iterator = result.getHq().iterator();
        		while (iterator.hasNext()) {
        			List<String> hq = iterator.next();
        			TracertDTO tracertDTO = new TracertDTO();
        			if (beginPrice < 0.01F) {
        				beginPrice = Float.valueOf(hq.get(2));
        			}
        			DateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CHINESE);
        			Date curdate = format.parse(hq.get(0));
        			tracertDTO.setDays(diffDays(curdate, begin));
        			tracertDTO.setHighest(Float.valueOf(hq.get(6)));
        			tracertDTO.setLowest(Float.valueOf(hq.get(5)));
        			tracertDTO.setIncrease_day(Float.valueOf(hq.get(9).replace("%", "")));
        			tracertDTO.setIncrease_total((tracertDTO.getHighest() - beginPrice) / beginPrice * 100);
        			tracertDTO.setInfoId(info.getId());
					tracertService.save(tracertDTO);
					
					if (tracertDTO.getHighest() > maxPrice) {
						maxPrice = tracertDTO.getHighest();
					}
					if (tracertDTO.getIncrease_day() > 0.0F) {
						increase_days ++;
					}
        		}
        	}
			// 更新结果判断
        	JudgeDTO judgeDTO = new JudgeDTO();
        	judgeDTO.setIncrease_days(increase_days);
        	judgeDTO.setIncrease_total((maxPrice - beginPrice) / beginPrice * 100);
        	judgeDTO.setInfoId(info.getId());
        	judgeService.save(judgeDTO);
        }
        return ResponseEntity.ok().build();
    }
    
    public static int diffDays(Date date1,Date date2)
    {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }

}
