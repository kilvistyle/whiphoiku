/**
 * Copyright (c) 2009 kilvistyle
 */
package whiphoiku.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.slim3.util.RequestLocator;
import org.slim3.util.StringUtil;
import org.slim3.util.WrapRuntimeException;

import com.google.apphosting.api.ApiProxy;

/**
 * NavigateUtil.
 * 
 * @author kilvistyle
 * @since 2009/11/14
 *
 */
public class NavigateUtil {
    
    private static final String[] DEV_LOCAL_HOSTS =
        new String[]{"http://localhost","http://127.0.0.1"};
    
    private NavigateUtil() {
    }

    private static HttpServletRequest getRequest() {
        return RequestLocator.get();
    }

    /**
     * 現在のリクエストのルートURLを取得する.
     * ex1)http://hoge.appspot.com/fuga/hogera.html → http://hoge.appspot.com
     * ex2)https://hoge.appspot.com/fuga/pigu?a=true → https://hoge.appspot.com
     * ex3)http://hoge.jp/fuga/hogera.html → http://hoge.jp
     * @param request
     * @return
     */
    public static String getRequestRootURL() {
    	HttpServletRequest request = getRequest();
        StringBuffer sb = request.getRequestURL();
        int uriLength = request.getRequestURI().length();
        return sb.delete(sb.length()-uriLength, sb.length()).toString();
    }
    
    /**
     * 現在のリクエストのノンセキュアなルートURLを取得する.
     * もし、独自ドメインを利用している場合、独自ドメインのルートURLを返します。
     * ex1)http://hoge.appspot.com/fuga/hogera.html → http://hoge.appspot.com （独自ドメインを利用していない場合）
     * ex2)https://hoge.appspot.com/fuga/pigu?a=true → http://hoge.appspot.com （独自ドメインを利用していない場合）
     * ex3)http://hoge.jp/fuga/hogera.html → http://hoge.jp （独自ドメインを利用している場合）
     * ex4)https://hoge.appspot.com/fuga/pigu?a=true → http://hoge.jp （独自ドメインを利用している場合）
     * @param request
     * @return
     */
    public static String getNonSecureRootURL() {
        String requestRootURL = getRequestRootURL();
    	if (isDevelopment()) {
    		// ローカルの開発サーバではそのままのURLを返す
    		return requestRootURL;
    	}
    	// 独自ドメインを設定している場合は独自ドメインURLを返す
    	if (!StringUtil.isEmpty(AppProps.DOMAIN_NAME)) {
    		// www.始まりのドメイン名が指定されている場合はそのまま
    		if (AppProps.DOMAIN_NAME.startsWith("www.")) {
    			return "http://"+AppProps.DOMAIN_NAME;
    		}
    		// ネイキッドドメインの場合はwww.を追加
    		else {
        		return "http://www."+AppProps.DOMAIN_NAME;
    		}
    	}
    	// セキュア通信の場合はノンセキュアに書き換えて返す
        if (getRequest().isSecure()) {
            return requestRootURL.replaceFirst("https://", "http://");
        }
        return requestRootURL;
    }

    /**
     * 現在のリクエストのセキュアなルートURLを取得する.
     * もし、独自ドメインを利用している場合、https://(app-id).appspot.comを返します。
     * ex1)http://hoge.appspot.com/fuga/hogera.html → https://hoge.appspot.com 
     * ex2)https://hoge.appspot.com/fuga/pigu?a=true → https://hoge.appspot.com
     * ex3)http://hoge.jp/fuga/hogera.html → https://hoge.appspot.com
     * @param request
     * @return
     */
    public static String getSecureRootURL() {
    	if (isDevelopment()) {
    		// ローカルの開発サーバではそのままのURLを返す
    		return getRequestRootURL();
    	}
    	// 本番環境ではセキュアなルートURLを生成
    	return "https://"+getAppId()+".appspot.com";
    }
    
    /**
     * AppIdを取得する.
     * High Replication Datastoreの場合でも正しいappIdを返却します。
     * @return String 
     */
    public static String getAppId() {
    	// appIdを取得
    	String appId = ApiProxy.getCurrentEnvironment().getAppId();
    	// High Replication Datastoreだと「s~」が先頭に付くので除去。
    	if (appId.startsWith("s~")) {
    		appId = appId.substring("s~".length());
    	}
    	return appId;
    }
    
    /**
     * クエリー文字列を含めた完全なリクエストURLを取得する.
     * @return
     */
    public static String getRequestURLWithQuery() {
    	HttpServletRequest request = getRequest();
        StringBuffer url = request.getRequestURL();
        // クエリー文字列がある場合は連結
        String qs = request.getQueryString();
        if (!StringUtil.isEmpty(qs)) {
            url.append("?");
            url.append(qs);
        }
        return url.toString();
    }
    
    /**
     * クエリー文字列を含めたリクエストURIを取得する.
     * @return
     */
    public static String getRequestURIWithQuery() {
    	HttpServletRequest request = getRequest();
        StringBuffer uri = new StringBuffer(request.getRequestURI());
        // クエリー文字列がある場合は連結
        String qs = request.getQueryString();
        if (!StringUtil.isEmpty(qs)) {
            uri.append("?");
            uri.append(qs);
        }
        return uri.toString();
    }
    
    /**
     * クエリー文字列をMapに変換する.
     * @param queryString "name=kilvistyle&sex=male"などのクエリー文字列
     * @return 
     */
    public static Map<String, String> getQueryStringMap(String queryString) {
    	if (StringUtil.isEmpty(queryString)) return Collections.emptyMap();
    	String[] queries = StringUtil.split(queryString, "&");
        Map<String, String> map = new HashMap<String, String>(queries.length);
        for (String query : queries) {
        	int i;
        	if ((i = query.indexOf("=")) < 0) {
        		throw new IllegalArgumentException("not query string part. "+query);
        	}
        	String name = query.substring(0, i);
        	String value = query.substring(i+1);
        	map.put(name, value);
        }
    	return map;
    }

    /**
     * クエリー文字列をURLエンコードしつつMapに変換する.
     * @param queryString "name=きるびす&sex=男"などのクエリー文字列
     * @return 
     */
    public static Map<String, String> getEncodedQueryStringMap(String url, String encode) {
    	Map<String, String> qsMap = getQueryStringMap(url);
    	for (Entry<String, String> entry : qsMap.entrySet()) {
    		try {
				entry.setValue(URLEncoder.encode(entry.getValue(), encode));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				throw new WrapRuntimeException(e);
			}
    	}
    	return qsMap;
    }
    
    /**
     * URLエンコードを行う.
     * ex1)http://hoge.com/aaa/bbb => http://hoge.com/aaa/bbb 
     * ex2)http://hoge.com/aaa/bbb?name=あああ&value=http://fuga.com/ =>http://hoge.com/aaa/bbb?value=http%3A%2F%2Ffuga.com%2F&name=%E3%81%82%E3%81%82%E3%81%82 
     * ex3)http://hoge.com/aaa/bbb?name=あああ&type=&value=http://fuga.com/ => http://hoge.com/aaa/bbb?value=http%3A%2F%2Ffuga.com%2F&type=&name=%E3%81%82%E3%81%82%E3%81%82 
     * ex4)http://hoge.com/tag/テスト => http://hoge.com/tag/%E3%83%86%E3%82%B9%E3%83%88
     * @param url
     * @param enc
     * @return
     */
    public static String urlEncode(String url, String enc) {
    	if (StringUtil.isEmpty(url)) return url;
    	StringBuffer result = new StringBuffer();
    	String preUrl = null;
    	String queryStrings = null;
    	int index;
    	if (0 <= (index = url.indexOf("?"))) {
    		preUrl = url.substring(0, index);
        	queryStrings = url.substring(index+1);
    	}
    	else {
    		preUrl = url;
    	}
    	// fix protocol
    	if (preUrl.startsWith("http://")) {
    		preUrl = preUrl.substring("http://".length());
    		int i = preUrl.indexOf("/");
    		if (i < 0) return url;
    		result.append("http://").append(preUrl.substring(0, i+1));
    		preUrl = preUrl.substring(i+1);
    	}
    	if (preUrl.startsWith("https://")) {
    		preUrl = preUrl.substring("https://".length());
    		int i = preUrl.indexOf("/");
    		if (i < 0) return url;
    		result.append("https://").append(preUrl.substring(0, i+1));
    		preUrl = preUrl.substring(i+1);
    	}
    	// fix bodys
    	try {
        	for (int i; 0 <= (i = preUrl.indexOf("/"));) {
        		result.append(URLEncoder.encode(preUrl.substring(0, i), enc)+"/");
        		preUrl = preUrl.substring(i+1);
        	}
    		result.append(URLEncoder.encode(preUrl, enc));
    		// fix query strings
        	if (!StringUtil.isEmpty(queryStrings)) {
        		result.append("?");
        		Map<String, String> queryStringMap = getEncodedQueryStringMap(queryStrings, enc);
        		int i = 0;
        		for (Entry<String, String> entry : queryStringMap.entrySet()) {
        			if (0 < i) result.append("&");
        			result.append(entry.getKey()).append("=").append(entry.getValue());
        			i++;
        		}
        	}
        	return result.toString();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		throw new WrapRuntimeException(e);
		}
    }
    
    /**
     * URLエンコードしつつクエリー文字列に復元する.
     * @param queryStringMap Map 分解されたクエリー文字列Map
     * @param enc String エンコード
     * @return　String クエリー文字列
     */
    public static String createQueryString(Map<String, String> queryStringMap, String enc) {
    	if (queryStringMap == null || queryStringMap.isEmpty()) return "";
    	StringBuffer sb = new StringBuffer();
    	try {
        	for (Entry<String, String> entry : queryStringMap.entrySet()) {
        		if (0 < sb.length()) sb.append("&");
        		sb.append(entry.getKey()+"="+URLEncoder.encode(entry.getValue(), enc));
        	}
        	return sb.toString();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		throw new WrapRuntimeException(e);
		}
    }
    
    /**
     * 現在のリクエストに対応するControllerのパッケージ(root.controller以降)を取得する.
     * ex1)http://hoge.appspot.com/fuga/pigu?a=true → .fuga
     * ex2)http://hoge.appspot.com/fuga/hogera/post → .fuga.hogera
     * @param request
     * @return
     */
    public static String getActionPackage() {
    	HttpServletRequest request = getRequest();
        String uri = request.getRequestURI();
        String actionPath = uri.substring(0, uri.lastIndexOf("/"));
        if (StringUtil.isEmpty(actionPath)) return null;
        return StrUtil.replace(actionPath, "/", ".");
    }
    
    /**
     * 実行しているサーバが開発サーバであるか判定する.
     * @param request
     * @return
     */
    public static boolean isDevelopment() {
        String requestRootURL = getRequestRootURL();
        for (String localhost : DEV_LOCAL_HOSTS) {
            if (requestRootURL.startsWith(localhost)) {
                return true;
            }
        }
        return false;
    }
    
}
