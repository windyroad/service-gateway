package au.com.windyroad.servicegateway;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@RequestMapping("/test/ping")
public class Ping {

	public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> get(final HttpServletRequest request,
			final HttpServletResponse response) {
		LOGGER.info("PING!");
		return ResponseEntity.noContent().build();
	}

}
