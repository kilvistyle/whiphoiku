package whiphoiku.controller.api;

import org.slim3.controller.Navigation;

import whiphoiku.util.JsonUtil;
import whiphoiku.util.NavigateUtil;

/**
 * GetAplPropsController. 
 * 
 * @author kilvistyle
 *
 */
public class GetAplPropsController extends AbstractJsonController {

    @Override
    public Navigation run() throws Exception {
        return json(JsonUtil.object(
            JsonUtil.param("aplId", NavigateUtil.getAppId()),
        	JsonUtil.param("secureRootURL", NavigateUtil.getSecureRootURL()),
        	JsonUtil.param("nonSecureRootURL", NavigateUtil.getNonSecureRootURL())
        ));
    }
}
