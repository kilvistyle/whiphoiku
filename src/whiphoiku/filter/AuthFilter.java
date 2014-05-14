package whiphoiku.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slim3.util.RequestLocator;
import org.slim3.util.ResponseLocator;
import org.slim3.util.StringUtil;

import whiphoiku.service.auth.AuthService;
import whiphoiku.util.AppProps;
import whiphoiku.util.NavigateUtil;
import whiphoiku.util.StrUtil;

/**
 * AuthFilter.
 * @author kilvistyle
 */
public class AuthFilter implements Filter {

	/** ContextKey：ログインページURL */
    public static final String KEY_LOGIN_URL = "LOGIN_URL";
    /** ContextKey：権限 */
    public static final String KEY_REQUIRED_ROLES = "REQUIRED_ROLES";
    /** 権限の区切り文字 */
    public static final String DELIMITER_ROLES = ",";
    /** ContextKey：権限なし合のリダイレクトURL */
    public static final String KEY_NO_ROLE_URL = "NO_ROLE_URL";

    /** RequestParameterKey : リダイレクト後にエラー理由を通知するキー */
    public static final String REASON_KEY = "reason";
    
    private String loginUrl = null;
    private String[] roles = null;
    private String noRoleUrl = null;
    
    @Override
    public void init(FilterConfig cfg) throws ServletException {
        // 認証ページURL取得
        loginUrl = getInitParameter(cfg, KEY_LOGIN_URL);
        // 認証ページが指定されていない場合は認証フィルターを利用しない
        if (StringUtil.isEmpty(loginUrl)) {
        	return;
        }
        // アクセスに必要な権限を取得
        String requiredRoles = getInitParameter(cfg, KEY_REQUIRED_ROLES);
        // 権限設定がされている場合
        if (!StringUtil.isEmpty(requiredRoles)) {
        	// 指定されている権限を取得
            String[] arrRole = StrUtil.splittrim(requiredRoles, DELIMITER_ROLES);
            // 文字列をRoleに変換
            if (arrRole != null) {
            	List<String> lstRole = new ArrayList<String>();
            	for (String role : arrRole) {
            		lstRole.add(role);
            	}
            	roles = lstRole.toArray(new String[0]);
            }
            // 権限エラーの場合の遷移先URL取得
            noRoleUrl = getInitParameter(cfg, KEY_NO_ROLE_URL);
        }
    }
    
    private String getInitParameter(FilterConfig cfg, String paramName) {
    	try {
    		return cfg.getInitParameter(paramName);
    	}
    	catch (Exception e) {
    		return null;
		}
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {
        // 認証ページが指定されていない場合は認証フィルターを利用しない
        if (StringUtil.isEmpty(loginUrl)) {
        	chain.doFilter(req, resp);
        	return;
        }
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        // このフィルター内で変更する前のrequest,responseを退避しておく
        HttpServletRequest preRequest = RequestLocator.get();
        HttpServletResponse preResponse = ResponseLocator.get();
        RequestLocator.set(request);
        ResponseLocator.set(response);
        try {
            // 認証チェック
            if (!AuthService.isValidCurrentUser()) {
            	// 未ログインの場合
            	if (AuthService.getCurrentUser() == null) {
                    response.sendRedirect(NavigateUtil.getSecureRootURL()
                            +loginUrl+"?"
                            +REASON_KEY+"="+Reason.NOT_LOGIN+"&"
                            +AuthService.KEY_CONTINUE+"="+getContinueURL());
            	}
            	// ログイン中のユーザが最新の情報ではない場合
            	else {
                    response.sendRedirect(NavigateUtil.getSecureRootURL()
                            +loginUrl+"?"
                            +REASON_KEY+"="+Reason.RETRY+"&"
                            +AuthService.KEY_CONTINUE+"="+getContinueURL());
            	}
                return;
            }
            // 権限チェック
            if (roles != null && !AuthService.hasRoleInAnyone(roles)) {
            	// 権限なしページが指定されていない場合
            	if (StringUtil.isEmpty(noRoleUrl)) {
            		// 認証ページへ
                    response.sendRedirect(NavigateUtil.getSecureRootURL()
                            +loginUrl+"?"
                            +REASON_KEY+"="+Reason.NO_ROLE+"&"
                            +AuthService.KEY_CONTINUE+"="+getContinueURL());
            	}
            	else {
                    // 権限がない場合は権限なしページへ
                    response.sendRedirect(noRoleUrl);
            	}
                return;
            }        
            // 正常に認証完了した場合
            chain.doFilter(req, resp);
        }
        finally {
            // 変更前のrequest,responseの状態に戻す
            RequestLocator.set(preRequest);
            ResponseLocator.set(preResponse);
        }
    }
    
    private String getContinueURL() {
    	try {
        	return URLEncoder.encode(NavigateUtil.getRequestURLWithQuery(), AppProps.DEFAULT_CHARSET);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
		}
		return NavigateUtil.urlEncode(NavigateUtil.getRequestURLWithQuery(), AppProps.DEFAULT_CHARSET);
    }
    
    @Override
    public void destroy() {
    }

    /**
     * 認証チェックエラー理由
     */
    public enum Reason {
    	/** 未ログインなため */
    	NOT_LOGIN,
    	/** 権限が足りないため */
    	NO_ROLE,
    	/** 再認証が必要なため */
    	RETRY
    }
}
