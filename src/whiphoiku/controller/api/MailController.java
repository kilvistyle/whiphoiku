package whiphoiku.controller.api;

import java.util.Arrays;

import org.slim3.controller.Navigation;
import org.slim3.controller.validator.Validators;

import whiphoiku.model.mail.SendMail;
import whiphoiku.service.mail.SendMailService;
import whiphoiku.util.JsonUtil;
import whiphoiku.util.RegexpConstants;

public class MailController extends AbstractJsonController {

    @Override
    public Navigation run() throws Exception {
        // validation
    	if (!isPost()) {
    	    return errorToJson(ERR_INVALID_REQUEST, "invalid request.", null);
    	}
    	if (!validate()) {
    	    return validatorErrToJson();
    	}
    	// パラメータを取得
    	String to = asString("to");
    	String from = asString("from");
    	String subject = asString("subject");
    	String body = asString("body");
    	// メール作成
    	SendMail mail = new SendMail();
    	mail.setTo(Arrays.asList(to));
    	mail.setFrom(from);
    	mail.setSubject(subject);
    	mail.setBody(body);
    	// メール送信
    	SendMailService.getInstance().send(mail);
    	
        return json(JsonUtil.object(
        		JsonUtil.param("result", "success")
        		));
    }

    private boolean validate() {
        Validators v = new Validators(request);
        v.add("to", v.required(), v.regexp(RegexpConstants.MATCH_MAIL));
        v.add("from", v.required(), v.regexp(RegexpConstants.MATCH_MAIL));
        v.add("subject", v.required());
        v.add("body", v.required());
        return v.validate();
    }
}
