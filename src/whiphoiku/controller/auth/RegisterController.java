package whiphoiku.controller.auth;

import org.slim3.controller.Navigation;
import org.slim3.util.ApplicationMessage;

import com.google.appengine.api.datastore.GeoPt;

import whiphoiku.controller.AbstractController;
import whiphoiku.dao.auth.AppUserDao;
import whiphoiku.model.auth.AppUser;
import whiphoiku.model.auth.OneTimePass;
import whiphoiku.model.auth.OneTimePass.OtpState;
import whiphoiku.model.auth.enums.UserState;
import whiphoiku.service.auth.OneTimePassService;
import whiphoiku.util.AppProps;
import whiphoiku.util.NumUtil;

public class RegisterController extends AbstractController {

    @Override
    public Navigation run() throws Exception {
    	if (!isGet() || !has("otp")) {
    		// 不正なパラメータ遷移の場合はトップページへ
    		return redirect("/");
    	}
    	// ワンタイムパスワードをチェック
    	OneTimePassService otpService = OneTimePassService.getInstance();
    	OneTimePass otp = otpService.get(asString("otp"));
    	// ワンタイムパスワードが見つからない場合（不正なワンタイムパスワード）
    	if (otp == null) {
    		errors.put("message", ApplicationMessage.get("error.invalid.onetimepass.notfound"));
    		return forward("register.jsp");
    	}
    	// この認証処理用のワンタイムパスワードではない場合
    	if (!"regmail".equals(otp.param("certify.target"))) {
    		errors.put("message", ApplicationMessage.get("error.invalid.onetimepass.notfound"));
    		return forward("register.jsp");
    	}
    	// 既に利用済みのワンタイムパスワードの場合
    	if (OtpState.USED.equals(otp.getState())) {
    		errors.put("message", ApplicationMessage.get("error.invalid.onetimepass.alreadyused"));
    		return forward("register.jsp");
    	}
    	// ワンタイムパスワードが有効期限切れの場合
    	if (otpService.isExpired(otp)) {
    		errors.put("message", ApplicationMessage.get("error.invalid.onetimepass.expired", AppProps.OTP_VALIDTERM));
    		return forward("register.jsp");
    	}
    	// チェックOKのため登録する
    	AppUser user = new AppUser();
    	user.setState(UserState.ACTIVE);
    	user.setName((String)otp.param("mail"));
    	user.setMail((String)otp.param("mail"));
    	user.setAddress((String)otp.param("address"));
    	GeoPt geoPt = new GeoPt(
    			Float.parseFloat((String)otp.param("lat")),
    			Float.parseFloat((String)otp.param("lng")));
    	user.setGeoPt(geoPt);
    	user.setDistance(Double.parseDouble((String)otp.param("search_area")));
    	user.setTargetAge(Integer.parseInt((String)otp.param("age")));
    	user.setSchoolKubun(Integer.parseInt((String)otp.param("public_private")));
    	// 登録実行
    	new AppUserDao().put(user);
    	// ワンタイムパスワードを使用済みにする
    	otp.setState(OtpState.USED);
    	otpService.update(otp);
    	
        return redirect("/auth/success");
    }
}
