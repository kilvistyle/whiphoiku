package whiphoiku.controller.auth;

import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.slim3.controller.Navigation;
import org.slim3.controller.validator.Validators;
import org.slim3.util.ApplicationMessage;

import whiphoiku.controller.AbstractController;
import whiphoiku.model.auth.OneTimePass;
import whiphoiku.model.mail.SendMail;
import whiphoiku.service.auth.OneTimePassService;
import whiphoiku.service.mail.SendMailService;
import whiphoiku.util.AppProps;
import whiphoiku.util.MailUtil;
import whiphoiku.util.NavigateUtil;
import whiphoiku.util.RegexpConstants;
import whiphoiku.util.ResourceConstants;
import whiphoiku.util.TokenUtil;

public class RegmailController extends AbstractController {

    @Override
    public Navigation run() throws Exception {
    	if (!isPost() || !validate()) {
    		// 不正な遷移の場合はトップページへリダイレクト
    		return redirect("/");
    	}
    	// メアド入力後の認証の場合
    	if (asBoolean("register") && validateMail() && TokenUtil.isTokenValid()) {
    		// 認証メール送信処理
            // ワンタイムパスワードを生成
            OneTimePassService otpService = OneTimePassService.getInstance();
            OneTimePass otp = otpService.create();
            // ユーザパラメータのセット
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
            	String paramName = paramNames.nextElement();
            	otp.param(paramName, request.getParameter(paramName));
            }
//            otp.setParamsMap(new HashMap<String, Object>(request.getParameterMap()));
            // メアド登録用のOTPを意味するパラメータをセット
            otp.param("certify.target", "regmail");
            // ワンタイムパスワードにユーザパラメータを保存
            otpService.update(otp);
            
            // 認証用メールを作成
            SendMail mail = new SendMail();
            mail.setTo(Arrays.asList(asString("mail")));
            mail.setBcc(Arrays.asList(AppProps.MAIL_FOR_BACKUP)); // 控え用
            mail.setFrom(AppProps.MAIL_FOR_CUSTOMER);
            // 送信メールテンプレートを生成
            File ftlFile = null;
            // テンプレートメッセージ用の置換変数を準備
            Map<String, Object> varMap = new HashMap<String, Object>();
            // 招待メールを作成
            ftlFile = new File(servletContext.getRealPath(ResourceConstants.FTL_REGMAIL));
            varMap.put("name", asString("mail"));
            varMap.put("regmailUrl", NavigateUtil.getSecureRootURL()+"/auth/register?otp="+otp.getOneTimePass());
            varMap.put("limitDay", AppProps.OTP_VALIDTERM);
            varMap.put("mailForCustomer", AppProps.MAIL_FOR_CUSTOMER);
            // メールテンプレートを読み込む
            MailUtil.storeTemplate(mail, ftlFile, varMap);
            // 生成したメールを送信
            SendMailService.getInstance().send(mail);
            
            // 認証メール送信完了ページへリダイレクト
            return redirect("/auth/checkYourEmail");
    	}
    	// メアド入力画面表示の場合
    	else {
    		// トークン発行
        	TokenUtil.saveToken();
    	}
        return forward("regmail.jsp");
    }
    
    private boolean validate() {
        Validators v = new Validators(request);
        v.add("address", v.required());
        v.add("search_area", v.required(), v.longRange(1, 20));
        v.add("age", v.required(), v.longRange(-1, 5));
        v.add("public_private", v.required(), v.longRange(0, 2));
        v.add("lat", v.required(), v.floatType());
        v.add("lng", v.required(), v.floatType());
        return v.validate();
    }
    
    private boolean validateMail() {
    	Validators v = new Validators(request);
    	v.add("mail", v.required(), v.regexp(RegexpConstants.MATCH_MAIL));
    	v.add("mail_conf", v.required());
    	// 入力値のチェック
        if (!v.validate()) {
            return false;
        }
        // メールアドレスの一致チェック
        boolean validate = true;
        if (!asString("mail").equals(asString("mail_conf"))) {
            // 入力された確認用メールアドレスと違う場合
            errors.put("mail_conf", ApplicationMessage.get("validator.diff", ApplicationMessage.get("label.mail"), ApplicationMessage.get("label.mail_conf")));
            validate = false;
        }
        return validate;
    }   
}
