package whiphoiku.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slim3.util.AppEngineUtil;
import org.slim3.util.BooleanUtil;
import org.slim3.util.RequestLocator;
import org.slim3.util.RequestUtil;
import org.slim3.util.ResponseLocator;
import org.slim3.util.StringUtil;

import whiphoiku.util.AppProps;
import whiphoiku.util.CookieUtil;
import whiphoiku.util.NavigateUtil;
import whiphoiku.util.StrUtil;

/**
 * AppSSLFilter.
 * URLパターン毎にセキュア／ノンセキュア通信の切替を行うフィルタクラス。
 * セキュア通信が必要なリクエストではhttps://(appId).appspot.comドメインへ切り替えてリダイレクトすることが可能です。
 * 
 * １つのパスに対し、複数のAppSSLFilterが指定されている場合は、最初の指定が有効になり、以降のAppSSLFilterは無視されます。
 * そのパスに対し最も優先したいAppSSLFilterを最初に定義して下さい。
 * 
 * ドメインを切り替えてもセッションを維持するため、URLのクエリー（keep.sidパラメータ）でセッションIDを引き継ぎますが、
 * セッション固定攻撃、およびセッションID漏洩によるなりすまし対策として以下を施しています。
 * ・ログインの開始時にセッションIDをリフレッシュします。
 * ・セキュア通信でのログイン時に、SessionとCookieに対しTokenを発行しておき認証します。
 * 
 * 【セキュリティ上の注意点】
 * これらのセキュリティを正しく利用するには、以下のようにアプリケーションを実装する必要がります。
 * １）ユーザのログインはセキュア通信にてAuthService.startLoginSession()を使用します。
 * ２）ログインユーザの情報を扱うページでは、セキュア通信にてAuthService.isValidCurrentUser()を使用し、
 *  現在ログイン中のユーザが”なりすまし”ではないことをチェックします。
 *  ※セキュア通信かつAuthFilterで権限を設定しているURLは自動でこのチェックが行われます。
 * 
 * @author kilvistyle
 */
public class AppSSLFilter implements Filter {
	
	/** ContextKey:独自ドメイン名 */
	public static final String KEY_DOMAIN_NAME = "DOMAIN_NAME";
	/** ContextKey:セキュアモード（true=セキュア, false=ノンセキュア） */
	public static final String KEY_SECURE = "SECURE";
	/** ContextKey:ドメインスイッチをスキップするURLパターン（カンマ区切り） */
	public static final String KEY_SKIP_URL_PATTERNS = "SKIP_URL_PATTERNS";
	
	/** RequestParameterKey：ドメイン切替済み判定パラメータ名 */
	public static final String KEY_DOMAIN_CHANGED = "domain.changed";
	/** RequestParameterKey:セッションIDパラメータ名 */
	public static final String KEY_KEEP_SID = "keep.sid";
	
	// Cookieに保持されるセッションID
	private static final String JSESSIONID = "JSESSIONID";

	private String domainName = null;
	private boolean secure = false;
	private String[] skipUrlPatterns = null;
	
	@Override
	public void init(FilterConfig cfg) throws ServletException {
		domainName = getDomainName(cfg);
		secure = getBoolValue(cfg, KEY_SECURE);
		skipUrlPatterns = getSkipUrlPatterns(cfg);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        // このフィルター内で変更する前のrequest,responseを退避しておく
        HttpServletRequest preRequest = RequestLocator.get();
        HttpServletResponse preResponse = ResponseLocator.get();
        RequestLocator.set(request);
        ResponseLocator.set(response);
        try {
    		if (
    	    	// 開発環境の場合は何もしない
    			AppEngineUtil.isDevelopment()
	    		// GET以外の時も何もしない
	    		|| !isGet()
	    		// スキップ対象のURLの場合は何もしない
	    		|| isSkip()
	    		// 静的ファイルアクセスの場合は何もしない
	    		|| isStatic(request.getRequestURI())
	            // 既にドメイン切替済みの場合は何もしない
	            || isDomainChanged()
            ) {
                chain.doFilter(req, resp);
                return;
            }
        	// セキュアプロトコルへの変換が必要な場合
        	if (secure) {
        		if (!request.isSecure()) {
        			String redirectURL =
        				"https://"+getAppIdDomainName()+createRequestURIWithSid();
            		redirect(redirectURL);
            		return;
        		}
        	}
        	// ノンセキュアプロトコルへの変換が必要場合
        	else {
        		if (request.isSecure()) {
        			String redirectURL =
        				"http://"+domainName+createRequestURIWithSid();
            		redirect(redirectURL);
            		return;
        		}
        	}
        	// ドメイン変更後でsessionIdを引き継ぐ必要がある場合
    		if (isKeepSid()) {
    			redirect(keepSidAndRediretURI());
    			return;
    		}
            // 多重にドメイン切替を行わないよう変更済みにする
    		request.setAttribute(KEY_DOMAIN_CHANGED, true);
        	// ドメイン切替済み
			chain.doFilter(req, resp);
			return;
        }
        finally {
            // 変更前のrequest,responseの状態に戻す
            RequestLocator.set(preRequest);
            ResponseLocator.set(preResponse);
        }
	}

	/**
	 * 現在のURIがスキップ対象か判定する.
	 * @return
	 */
	private boolean isSkip() {
		// スキップURLパターンが指定されていない場合はスキップしない
		if (skipUrlPatterns == null || skipUrlPatterns.length == 0) {
			return false;
		}
		// リクエストURIを取得
		String path = RequestLocator.get().getRequestURI();
		for (String urlPattern : skipUrlPatterns) {
			// ワルドカードが指定されている場合
			if (urlPattern.endsWith("/*")) {
				// ワイルドカードまでのパスが現在のURIに一致しているか判定
				String startWith =
					urlPattern.substring(0, urlPattern.lastIndexOf("/*"));
				if (path.startsWith(startWith)) {
					return true;
				}
			}
			// ワイルドカードが指定されていない場合は完全一致判定
			else if (path.equals(urlPattern)) {
				return true;
			}
		}
		// このURIはスキップしない
		return false;
	}

	/**
	 * 静的ファイルへのパスであるか判定する.
	 * @param path String 
	 * @return
	 * @throws NullPointerException
	 */
    public boolean isStatic(String path) throws NullPointerException {
        if (path == null) {
            throw new NullPointerException("The path parameter is null.");
        }
        if (path.startsWith("/_ah/")) {
            return false;
        }
        String extension = RequestUtil.getExtension(path);
        return extension != null && !extension.startsWith("s3");
    }

	/**
	 * セッションIDを保持する必要があるか判定する.
	 * @return boolean 
	 */
	private boolean isKeepSid() {
		// クエリー文字列にセッションIDが指定されているか判定する
		String sid = RequestLocator.get().getParameter(KEY_KEEP_SID);
		return sid != null && !StringUtil.isEmpty(sid);
	}
	/**
	 * セッションIDを引き継ぎ、元々のリダイレクトURIを復元して返却する.
	 * @return String 元々のリダイレクトURI
	 */
	private String keepSidAndRediretURI() {
		HttpServletRequest req = RequestLocator.get();
		// クエリー文字列をMapに変換
		Map<String, String> qsMap = NavigateUtil.getQueryStringMap(req.getQueryString());
		// クエリー文字列MapからセッションIDを取得しつつ削除
		qsMap.remove(KEY_KEEP_SID);
		String sid = req.getParameter(KEY_KEEP_SID);
		// セッションIDを引き継ぐ
		keepSidAt(sid);
		// クエリー文字列が無い場合はURIを返却
		String redirectURL = null;
		if (qsMap.isEmpty()) {
			redirectURL = req.getRequestURI();
		}
		else {
			redirectURL = req.getRequestURI()+"?"+NavigateUtil.createQueryString(qsMap, AppProps.DEFAULT_CHARSET);
		}
		// URIに元々のクエリー文字列を復元して返却
		return redirectURL;
	}

	/**
	 * 指定されたセッションIDを受け入れる.
	 * @param sid String セッションID
	 * @return boolean true=セッションを受け入れた場合
	 */
	protected boolean keepSidAt(String sid) {
		HttpServletRequest req = RequestLocator.get();
		if (req == null) return false;
		// 既存のセッションIDを削除
		CookieUtil.removeValue(JSESSIONID);
		CookieUtil.removeGlobalValue(JSESSIONID);
		// 指定されたセッションIDを引き継ぐ
		CookieUtil.setUseCipher(false); // 一時的に暗号化解除
		CookieUtil.setGlobalValue(JSESSIONID, sid, req.isSecure());
		CookieUtil.setUseCipher(true); // 暗号化戻し
		return true;
	}
	
	/**
	 * SID付きのリクエストURIを生成する.
	 * @return String uri
	 */
	private String createRequestURIWithSid() {
		HttpServletRequest req = RequestLocator.get();
		HttpSession ses = req.getSession(false);
		String uriWithQs = NavigateUtil.getRequestURIWithQuery();
		// セッションが未生成、または独自ドメインを利用していない場合はSID不要
		if (ses == null || StringUtil.isEmpty(AppProps.DOMAIN_NAME)) {
			return uriWithQs;
		}
		// クエリー文字列がない場合はSIDをクエリー文字列として返却
		if (uriWithQs.indexOf("?") < 0) {
			return uriWithQs+"?"+KEY_KEEP_SID+"="+ses.getId();
		}
		// クエリー文字列がある場合はSIDを連結して返却
		else {
			return uriWithQs+"&"+KEY_KEEP_SID+"="+ses.getId();
		}
	}

	/**
	 * このリクエストで既にドメイン変更済みであるか判定する.
	 * DomainSwitchFilterによるリダイレクトループを回避するための判定です。
	 * @return
	 */
	private boolean isDomainChanged() {
        HttpServletRequest req = RequestLocator.get();
        Boolean changed = (Boolean)req.getAttribute(KEY_DOMAIN_CHANGED);
        if (changed == null) {
        	changed = BooleanUtil.toBoolean(req.getParameter(KEY_DOMAIN_CHANGED));
        }
        return changed == null ? false : changed.booleanValue();
	}
	
	private void redirect(String url) throws IOException {
		HttpServletResponse resp = ResponseLocator.get();
		url = resp.encodeRedirectURL(url);
		resp.sendRedirect(url);
	}
	
	@Override
	public void destroy() {
	}

	// このリクエストがGETメソッドか判定する
    protected boolean isGet() {
        return "get".equalsIgnoreCase(RequestLocator.get().getMethod());
    }

	// ドメイン名を取得する
	private String getDomainName(FilterConfig cfg) {
		// web.xmlのfilterのinit-parameterで指定されているドメイン名を取得
		String domainName = getInitParameter(cfg, KEY_DOMAIN_NAME);
		// 指定がなければプロパティファイルからドメイン名を取得
		if (StringUtil.isEmpty(domainName)) {
			domainName = AppProps.DOMAIN_NAME;
		}
		// 指定がなければAppEngineのapp-idを返却
		if (StringUtil.isEmpty(domainName)) {
			return getAppIdDomainName();
		}
		// 指定されたドメインがある場合、ネイキッドドメインの場合はwww.を追加
		if (!domainName.startsWith("www.")) {
			domainName = "www."+domainName;
		}
		return domainName;
	}
	
	// ドメイン切替をスキップするURLパターンを取得する.
	private String[] getSkipUrlPatterns(FilterConfig cfg) {
		String values = getInitParameter(cfg, KEY_SKIP_URL_PATTERNS);
		// 指定がなければ空の配列を返却
		if (StringUtil.isEmpty(values)) {
			return new String[]{};
		}
		// 指定があればカンマ区切り配列として取得
		return StrUtil.splittrim(StrUtil.replace(values, "?", "*"), ",");
	}
	
	// AppEngineのapp-idのドメインを取得する
	private String getAppIdDomainName() {
    	return NavigateUtil.getAppId()+".appspot.com";
	}

	// web.xmlに定義されたfilterのinit-parameterを取得する
    private String getInitParameter(FilterConfig cfg, String paramName) {
    	try {
    		return cfg.getInitParameter(paramName);
    	}
    	catch (Exception e) {
    		return null;
		}
    }

	// web.xmlに定義されたfilterのinit-parameterをboolean型で取得する
	private boolean getBoolValue(FilterConfig cfg, String paramName) {
		String val = getInitParameter(cfg, paramName);
		if (StringUtil.isEmpty(val)) {
			return false;
		}
		try {
			return BooleanUtil.toPrimitiveBoolean(val);
		}
		catch (Exception e) {
			return false;
		}
	}

}
