/**
 * Copyright (c) 2011 kilvistyle
 */
package whiphoiku.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slim3.util.AesCipher;
import org.slim3.util.Cipher;
import org.slim3.util.ConversionUtil;
import org.slim3.util.RequestLocator;
import org.slim3.util.ResponseLocator;
import org.slim3.util.StringUtil;

/**
 * CookieUtil.
 * クッキーの値の操作を行うユーティリティクラス.
 * @author kilvistyle
 * @since 2011/06/03
 */
public class CookieUtil {

    protected static ThreadLocal<Boolean> useCipher = new ThreadLocal<Boolean>();
    
    private CookieUtil() {
    }
    
    private static HttpServletRequest getRequest() {
        return RequestLocator.get();
    }
    
    private static HttpServletResponse getResponse() {
        return ResponseLocator.get();
    }
    
    /**
     * クッキーから値を取得する.
     * @param name String キー名
     * @return String 値
     */
    public static String getValue(String name) {
        if (StringUtil.isEmpty(name)) throw new IllegalArgumentException("the name is empty.");
        HttpServletRequest request = getRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
        	return null;
        }
        for (Cookie cookie : cookies) {
            if(name.equals(cookie.getName())) {
            	if (isUseCipher()) {
            		String val = createCipher().decrypt(cookie.getValue());
            		return val != null ? val : cookie.getValue();
            	}
            	else {
            		return cookie.getValue();
            	}
            }
        }
        return null;
    }

    /**
     * 指定されたクッキーの値をMapで取得する.
     * @param names String... キー名（可変引数）
     * @return Map<String, String> クッキーから取得した”キー：値”のMapオブジェクト
     */
    public static Map<String, String> getValuesMap(String...names) {
        if (names == null) throw new NullPointerException("the names is null.");
        if (names.length == 0) throw new IllegalArgumentException("the names is empty.");
        Map<String, String> valuesMap = new HashMap<String, String>(names.length);
        for (String name : names) {
            if (!StringUtil.isEmpty(name)) {
                String value = getValue(name);
                if (!StringUtil.isEmpty(value)) valuesMap.put(name, value);
            }
        }
        return valuesMap;
    }

    /**
     * リクエストスコープに値がない場合、クッキーの値をリクエストスコープにセットする.
     * @param names String... キー名（可変引数）
     */
    public static void setRequestScopeIfNecessary(String...names) {
        HttpServletRequest request = getRequest();
        for (String name : names) {
            if (!StringUtil.isEmpty(name)
                && request.getAttribute(name) == null) {
                request.setAttribute(name, getValue(name));
            }
        }
    }
    
    /**
     * クッキーに値をセットする.
     * @param name String キー名
     * @param value String 値
     */
    public static void setValue(String name, String value) {
        setValue(null, name, value, false, AppProps.COOKIE_MAX_AGE);
    }
    
    /**
     * クッキーに値をセットする.
     * @param name String キー名
     * @param value String 値
     * @param secure boolean true=セキュア通信時のみクッキーを送受信する, false=常時　〃　
     */
    public static void setValue(String name, String value, boolean secure) {
        setValue(null, name, value, secure, AppProps.COOKIE_MAX_AGE);
    }
    
    /**
     * クッキーに値をセットする.
     * @param name String キー名
     * @param value String 値
     * @param secure boolean true=セキュア通信時のみクッキーを送受信する, false=常時　〃　
     * @param maxAge int クッキー生存期間（秒）
     */
    public static void setValue(String name, String value, boolean secure, int maxAge) {
    	setValue(null, name, value, secure, maxAge);
    }

    /**
     * クッキーに値をセットする.
     * @param path String パス
     * @param name String キー名
     * @param value String 値
     */
    public static void setValue(String path, String name, String value) {
    	setValue(path, name, value, false, AppProps.COOKIE_MAX_AGE);
    }

    /**
     * クッキーに値をセットする.
     * @param path String パス
     * @param name String キー名
     * @param value String 値
     * @param secure boolean true=セキュア通信時のみクッキーを送受信する, false=常時　〃　
     */
    public static void setValue(String path, String name, String value, boolean secure) {
    	setValue(path, name, value, secure, AppProps.COOKIE_MAX_AGE);
    }
    
    /**
     * クッキーに値をセットする.
     * @param path String パス
     * @param name String キー名
     * @param value String 値
     * @param secure boolean true=セキュア通信時のみクッキーを送受信する, false=常時　〃　
     * @param maxAge int クッキー生存期間（秒）
     */
    public static void setValue(String path, String name, String value, boolean secure, int maxAge) {
    	if (isUseCipher()) {
    		value = createCipher().encrypt(value);
    	}
        Cookie cookie = new Cookie(name, value);
        if (!StringUtil.isEmpty(path)) {
            cookie.setPath(path);
        }
        cookie.setMaxAge(maxAge);
        cookie.setSecure(secure);
        getResponse().addCookie(cookie);
    }
    
    /**
     * クッキーにドメイン内でグローバルに参照可能な値をセットする.
     * @param name String キー名
     * @param value String 値
     * @param secure boolean true=セキュア通信時のみクッキーを送受信する, false=常時　〃　
     */
    public static void setGlobalValue(String name, String value, boolean secure) {
        setValue("/", name, value, secure, AppProps.COOKIE_MAX_AGE);
    }
    
    /**
     * リクエストスコープの値をクッキーにセットする.
     * @param names String... キー名（可変引数）
     */
    public static void setValuesFromRequestScope(String...names) {
    	setValuesFromRequestScope(false, AppProps.COOKIE_MAX_AGE, names);
    }

    /**
     * リクエストスコープの値をクッキーにセットする.
     * @param secure boolean true=セキュア通信時のみクッキーを送受信する, false=常時　〃　
     * @param names String... キー名（可変引数）
     */
    public static void setValuesFromRequestScope(boolean secure, String...names) {
    	setValuesFromRequestScope(secure, AppProps.COOKIE_MAX_AGE, names);
    }

    /**
     * リクエストスコープの値をクッキーにセットする.
     * @param secure boolean true=セキュア通信時のみクッキーを送受信する, false=常時　〃　
     * @param maxAge int クッキー生存期間（秒）
     * @param names String... キー名（可変引数）
     */
    public static void setValuesFromRequestScope(boolean secure, int maxAge, String...names) {
        HttpServletRequest request = getRequest();
        for (String name : names) {
            if (!StringUtil.isEmpty(name)) {
            	Object obj = request.getAttribute(name);
            	String value;
            	if (obj != null) {
            		value = ConversionUtil.convert(request.getAttribute(name), String.class);
            	}
            	else {
            		value = "";
            	}
                setValue(name, value, secure, maxAge);
            }
        }
    }
    
    /**
     * クッキーから値を削除する.
     * @param names String... キー名（可変引数）
     */
    public static void removeValue(String...names) {
        if (names == null) throw new NullPointerException("the names is null.");
        if (names.length == 0) throw new IllegalArgumentException("the names is empty.");
        for (String name : names) {
            if (!StringUtil.isEmpty(name)) {
                Cookie cookie = new Cookie(name, "");
                cookie.setMaxAge(0);
                getResponse().addCookie(cookie);
            }
        }
    }

    /**
     * クッキーから値を削除する.
     * @param names String... キー名（可変引数）
     */
    public static void removeGlobalValue(String...names) {
        if (names == null) throw new NullPointerException("the names is null.");
        if (names.length == 0) throw new IllegalArgumentException("the names is empty.");
        for (String name : names) {
            if (!StringUtil.isEmpty(name)) {
                Cookie cookie = new Cookie(name, "");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                getResponse().addCookie(cookie);
            }
        }
    }

    /**
     * このスレッドの以降のCookieUtilの操作では暗号化を利用しないよう設定する.
     * @param b
     */
    public static void setUseCipher(boolean b) {
    	useCipher.set(b);
    }
    
    /**
     * クッキー保存時に暗号化するか判定する.
     * @return
     */
    public static boolean isUseCipher() {
    	if (useCipher.get() != null && !useCipher.get()) {
    		return false;
    	}
    	try {
        	AesCipher.validateAesKey(AppProps.COOKIE_CIPHER_KEY);
        	return true;
    	}
    	catch (Exception e) {
    		return false;
		}
    }
    
    /**
     * クッキー保存時の暗号/複合クラス（Slim3のAesCipher）を生成
     * @return
     */
    protected static Cipher createCipher() {
        Cipher c = new AesCipher();
        c.setKey(AppProps.COOKIE_CIPHER_KEY);
        return c;
    }
    
    /**
     * リクエストのCookie情報をダンプ出力する（デバッグ用）
     */
    public static void dump() {
        HttpServletRequest request = getRequest();
        Cookie[] cookies = request.getCookies();
        int hash = request.hashCode();
        System.out.println("cookie dump start. id="+hash+". secure="+request.isSecure());
        if (cookies == null || cookies.length == 0) {
        	System.out.println("cookie is empty. id="+hash);
        	return;
        }
        for (Cookie cookie : cookies) {
        	System.out.println("cookie["+cookie.getName()+":"+cookie.getValue()+"] id="+hash);
        }
        System.out.println("cookie dump end. id="+hash);
    }
}
