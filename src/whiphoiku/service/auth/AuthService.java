package whiphoiku.service.auth;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;
import org.slim3.util.RequestLocator;
import org.slim3.util.StringUtil;

import whiphoiku.model.auth.AuthUser;
import whiphoiku.util.CookieUtil;
import whiphoiku.util.TokenUtil;

/**
 * AuthService.
 * 
 * @author kilvistyle
 *
 */
public class AuthService {

    /**
     * ユーザ情報をセッションに格納するためのキー.
     */
    public static final String KEY_USER = "auth.user.key";
    /**
     * セキュアトークンキー.
     */
    public static final String KEY_SECURE_TOKEN = "auth.user.token";
    
    public static final String KEY_CONTINUE = "continue";
    
    /**
     * 
     */
    private AuthService() {
    }
    
    private static HttpServletRequest getRequest() {
        return RequestLocator.get();
    }

    /**
     * HttpSessionを取得する.<br/>
     *
     * @param create boolean falseを指定するとセッションの新規生成は行わない.
     * @return {@link HttpSession}
     */
    private static HttpSession getSession(boolean create) {
        return getRequest().getSession(create);
    }
    
    /**
     * 新しくセッションを生成する.<br/>
     * 現在のセッションを破棄し、新しいセッションを生成します。
     */
    public static HttpSession createNewSession() {
        // セッションを取得
        HttpSession ses = getSession(false);
        if (ses != null) {
            // 現在のセッションを開放
            ses.invalidate();
        }
        // 新しくセッションを生成
        return getSession(true);
    }
    
    /**
     * セッションスコープのオブジェクトを保持しつつ新しいセッションを生成する.<br/>
     */
    @SuppressWarnings("unchecked")
    public static void createNewSessionWithKeepObject() {
        // 現在のセッションの内容を退避するためのMapを生成
        Map<String, Object> tempMap = new HashMap<String, Object>();
        HttpSession ses = getSession(true);
        Enumeration<String> e = ses.getAttributeNames();
        // 現在のセッションの内容を退避
        while (e.hasMoreElements()) {
            String key = e.nextElement();
            tempMap.put(key, ses.getAttribute(key));
        }
        // 現在のセッションを破棄して再生成
        ses = createNewSession();
        // 退避した内容を復元する
        for (Entry<String, Object> entry : tempMap.entrySet()) {
            ses.setAttribute(entry.getKey(), entry.getValue());
        }
    }
    
    private static void sessionScope(String key, Object value) {
        getSession(true).setAttribute(key, value);
    }

    @SuppressWarnings("unchecked")
	private static <T> T sessionScope(String key) {
        HttpSession session = getSession(false);
        if (session == null) {
            return null;
        }
        return (T) session.getAttribute(key);
    }
    
    /**
     * ログイン状態を開始する.<br/>
     * AuthUserをセッションに格納してログイン済み状態にします。<br/>
     * SessionFixation対策として、ログイン後は新しいセッションを生成しますが、<br/>
     * セッションスコープで保持しているオブジェクトはそのまま維持します。<br/>
     *
     * @param authUser AuthUser AuthUserを継承したユーザモデルオブジェクト
     * @return String AuthFilterによりログインページに誘導された場合、元のページのURLを返却します。
     */
    public static String startLoginSession(AuthUser authUser) {
        // userIdの検証
        if (authUser == null || authUser.getKey() == null) {
            throw new IllegalArgumentException("authUserの状態が不正です。ログインセッションを開始できません。");
        }
        // これまでのセッションオブジェクトを維持しつつ、新しいセッションを生成（SessionFixation対策）
        createNewSessionWithKeepObject();
        // 新しいセッションにIDとロールを設定
        sessionScope(KEY_USER, authUser);
        // セキュア通信でのログインの場合はセッションハイジャック対策のトークンを発行する
        saveSecureTokenIfNecessary();
        // ログインページにリダイレクトされる前のURLを返却
        return getContinueURL();
    }
    
    /**
     * セキュア通信の場合にトークンを生成する.
     * セキュア通信の場合に限り、Cookieにトークンを生成することでセッションハイジャックを防ぎます。
     */
    private static void saveSecureTokenIfNecessary() {
    	if (getRequest().isSecure()) {
            // セキュアログインの場合はセッションハイジャック対策のトークンをCookieに埋め込む
    		String token = TokenUtil.generateToken();
    		sessionScope(KEY_SECURE_TOKEN, token);
    		CookieUtil.setUseCipher(false);
    		CookieUtil.setGlobalValue(KEY_SECURE_TOKEN, token, true);
    		CookieUtil.setUseCipher(true);
    	}
	}
    /**
     * セキュア通信の場合にトークンを判定する.
     * セキュア通信の場合に限り、Cookieのトークンとセッショントークンが同一か判定し、
     * 不一致の場合（＝セッションハイジャックの可能性がある場合）はfalseを返却します。
     * @throws InvalidUserException
     */
    private static boolean isValidSecureTokenIfNecessary() {
    	if (getRequest().isSecure()) {
    		// セキュア通信でログインした際に保存したトークンを取得
    		String token = sessionScope(KEY_SECURE_TOKEN);
    		// セッションにtokenが見つからない場合もfalse
    		if (StringUtil.isEmpty(token)) return false;
    		// セッションのトークンとCookieのトークンが一致しているか判定
    		return token.equals(CookieUtil.getValue(KEY_SECURE_TOKEN));
    	}
    	return true;
    }

	/**
     * ログインにリダイレクトするURLを取得する.
     * @return String URL
     */
    private static String getContinueURL() {
    	String continueUrl = (String)getRequest().getAttribute(KEY_CONTINUE);
    	if (StringUtil.isEmpty(continueUrl)) {
    		continueUrl = getRequest().getParameter(KEY_CONTINUE);
    	}
    	if (StringUtil.isEmpty(continueUrl)) {
    		return "/";
    	}
    	return continueUrl;
    }
    
    /**
     * ログイン状態を終了する.<br/>
     * 新しいセッションを生成して未ログインの状態にし、セッションスコープも開放する。<br/>
     *
     * @return boolean true=ログイン状態から正常にログアウトした場合, false=既に未ログイン状態だった場合
     */
    public static boolean endLoginSession() {
        // 現時点でログイン状態か判定しておく
        boolean isValidUserLogin = getCurrentUser() != null;
        // セッションを再生成
        createNewSession();
        // ログイン状態だった場合はtrueを、未ログイン状態（セッション切れ含む）ならfalseを返却
        return isValidUserLogin;
    }
    
    /**
     * ログイン状態を終了する.<br/>
     * 新しいセッションを生成して未ログインの状態にするが、セッションスコープは維持する。<br/>
     * @return
     */
    public static boolean endLoginSessionWithKeepObject() {
        // 現時点でログイン状態か判定しておく
        boolean isValidUserLogin = getCurrentUser() != null;
    	getSession(true).removeAttribute(KEY_USER);
    	getSession(true).removeAttribute(KEY_SECURE_TOKEN);
    	createNewSessionWithKeepObject();
        // ログイン状態だった場合はtrueを、未ログイン状態（セッション切れ含む）ならfalseを返却
        return isValidUserLogin;
    }
    
    /**
     * 現在のセッションがログイン中であるか判定する.<br/>
     * @return boolean true=ログイン中, false=ログイン中ではない
     */
    public static boolean isLogin() {
    	return getCurrentUser() != null;
    }

    /**
     * 現在のセッションのユーザを取得する.<br/>
     * 未ログインの場合は null を返します。<br/>
     * @return AuthUser 
     */
    @SuppressWarnings("unchecked")
	public static <M extends AuthUser> M getCurrentUser() {
        return (M)getSession(true).getAttribute(KEY_USER);
    }

    /**
     * 現在ログイン中のユーザが有効であるか判定する.<br/>
     * 最新のユーザ情報でログイン中であるか判定します。<br/>
     * ※未ログイン、セッション切れの場合も無効と判定します。<br/>
     * 
     * @return boolean true=最新情報でログイン済み, false=未ログイン状態（セッション切れ含む）、またはユーザ情報が陳腐化している
     */
    public static boolean isValidCurrentUser() {
        // 現在のログイン中のユーザ情報を取得
        AuthUser current = getCurrentUser();
        if (current == null) return false;
        // 最新のユーザ情報を取得
        AuthUser newest = getLatestUser(current);
        if (newest == null) return false;
        // ユーザ情報が陳腐化している場合
        if (!current.getVersion().equals(newest.getVersion())) return false;
        // セキュア通信の場合はトークンチェックを行う
        return isValidSecureTokenIfNecessary();
    }
    
    /**
     * 現在ログイン中のユーザが有効であるか判定する.
     * 最新のユーザ情報でログイン中であるか判定します。<br/>
     * セキュア通信でログイン
     * ※未ログイン、セッション切れの場合も無効と判定します。<br/>
     * @throws IllegalStateException ログイン中のユーザが無効な場合
     */
    public static void checkValidCurrenUser() throws IllegalStateException {
    	if (!isValidCurrentUser()) throw new IllegalStateException();
    }

    /**
     * 引数のユーザ情報が最新であれば取得する.
     * @param M 検証するユーザ
     * @return M 最新のユーザ情報（引数のユーザが最新ではない場合はnull）
     */
    @SuppressWarnings("unchecked")
	protected static <M extends AuthUser> M getLatestUser(M user) {
    	try {
            return (M) Datastore.get(
            	user.getClass(),
            	user.getKey(),
            	user.getVersion());
    	}
    	catch (ConcurrentModificationException e) {
    		// not latest
    		return null;
    	}
    	catch (EntityNotFoundRuntimeException e) {
    		// already deleted
    		return null;
		}
    }
    
    /**
     * ログインしているユーザのロールを取得する.<br/>
     * 未ログインの場合は null を返却します。<br/>
     * ログイン中の場合でロールがないユーザは new String[0] を返します。
     * @return String[] ログインユーザのロールリスト
     */
    public static String[] getUserRoles() {
        AuthUser current = getCurrentUser();
        // 未ログインの場合
        if (current == null) {
            return null;
        }
        // ログイン済みの場合はrolesを返却
        return current.getRoles() != null ? current.getRoles() : new String[0];
    }

    /**
     * 現在のログインユーザが指定されたロールを持っているか判定する.<br/>
     * @param role String 検査するロール
     * @return boolean true=ロールを持っている, false=ロールを持っていない
     */
    public static boolean hasRole(String role) {
        // 検査するロールがnullの場合
        if (role == null) {
            throw new NullPointerException("The roles is null.");
        }
        // ロールを取得
        String[] rs = getUserRoles();
        // 未ログインの場合
        if (rs == null) { return false; }
        // 指定されたロールを保持している場合はtrueを返却する
        return Arrays.asList(rs).contains(role);
    }
    
    /**
     * 現在のログインユーザが指定された全てのロールを持っているか判定する.<br/>
     * @param roles String...　検査したいロール（可変引数）
     * @return boolean true=指定されたロールを全て保持している, false=　〃　保持していない
     */
    public static boolean hasRolesAll(String...roles) {
        // 検査するロールがnullの場合
        if (roles == null) {
            throw new NullPointerException("The roles is null.");
        }
        // 検査するロールが空の場合
        if (roles.length == 0) {
            throw new IllegalArgumentException("The roles is empty.");
        }
        // ロールを取得
        String[] rs = getUserRoles();
        // 未ログインの場合
        if (rs == null) { return false; }
        // 指定されたロールを全て保持している場合はtrueを返却する
        return Arrays.asList(rs).containsAll(Arrays.asList(roles));
    }
    
    /**
     * 現在のログインユーザが指定されたロールのどれか一つを持っているか判定する.<br/>
     * @param roles String...　検査したいロール（可変引数）
     * @return boolean true=指定されたロールを一つ以上保持している, false=一つも保持していない
     */
    public static boolean hasRoleInAnyone(String...roles) {
        // 検査するロールがnullの場合
        if (roles == null) {
            throw new NullPointerException("The roles is null.");
        }
        // 検査するロールが空の場合
        if (roles.length == 0) {
            throw new IllegalArgumentException("The roles is empty.");
        }
        // ロールを取得
        String[] rs = getUserRoles();
        // 未ログインの場合
        if (rs == null) { return false; }
        // 指定されたロールをどれか一つでも保持している場合はtrueを返却する
        for (String role : roles) {
            if (Arrays.asList(rs).contains(role)) {
                return true;
            }
        }
        // どれも持っていない場合
        return false;
    }
}
