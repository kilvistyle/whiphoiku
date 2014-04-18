package whiphoiku.controller.api;

import java.net.URL;
import java.util.HashMap;
import java.util.logging.Logger;

import net.arnx.jsonic.JSON;

import org.slim3.controller.Navigation;
import org.slim3.controller.validator.Validators;

import whiphoiku.util.AppProps;
import whiphoiku.util.JsonUtil;
import whiphoiku.util.RegexpConstants;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

/**
 * Call Google URL Shortener.
 * 
 * @author kilvistyle
 *
 */
public class GglController extends AbstractJsonController {
	
	private static final Logger logger =
		Logger.getLogger(GglController.class.getName());

    @SuppressWarnings("unchecked")
	@Override
    public Navigation run() throws Exception {
        // validation
    	if (!isPost()) {
    	    return errorToJson(ERR_INVALID_REQUEST, "invalid request.", null);
    	}
    	if (!validate()) {
    	    return validatorErrToJson();
    	}
    	
    	String shortUrl = null;
    	try {
    		// set long url (json)
            String data = "{\"longUrl\": \""+asString("url")+"\"}";
            URLFetchService ufs = URLFetchServiceFactory.getURLFetchService();
            HTTPRequest req =
            	new HTTPRequest(
            		new URL(AppProps.GOOGLE_URL_SHORTENER_URL+
            			"?key="+AppProps.GOOGLE_URL_SHORTENER_KEY),
            		HTTPMethod.POST);
            req.addHeader(new HTTPHeader("Content-Type", "application/json"));
            req.setPayload(data.getBytes());
            // fetch to goo.gl
            HTTPResponse resp = ufs.fetch(req);
            // get json data from response
            byte[] content = resp.getContent();
            HashMap<String, String> map =
            	JSON.decode(new String(content, "UTF-8"), HashMap.class );
            shortUrl = map.get("id");
    	}
    	catch (Exception e) {
    		logger.warning("goo.gl url shortener failed. "+e.toString());
    		e.printStackTrace(System.err);
    		return errorToJson(ERR_URLFETCH_IO, e);
		}
    	logger.info("called goo.gl shortener. before="+asString("url")+", after="+shortUrl);
    	
    	// json response
    	return json(JsonUtil.object(
    	        JsonUtil.paramExcludeNull("url", shortUrl)
    	    ));
    }
    
    private boolean validate() {
    	Validators v = new Validators(request);
    	v.add("url", v.required(), v.regexp(RegexpConstants.MATCH_URL));
    	return v.validate();
    }
}
