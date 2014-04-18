package whiphoiku.util;

import java.util.ResourceBundle;

/**
 * アプリケーションプロパティ.
 * アプリケーション設定ファイル（app.properties）に定義されているプロパティです。
 * @author kilvistyle
 */
public class AppProps {

    private static ResourceBundle bundle = null;
    private static final String SPLIT_DELIM = ",";
    
    static {
        bundle = ResourceBundle.getBundle("app"); 
    }
    /** ドメイン名 */
    public static final String DOMAIN_NAME = bundle.getString("domain.name");
    /** デフォルト文字コード */
    public static final String DEFAULT_CHARSET = bundle.getString("charset");
    /** Cookieのデフォルト生存期間（秒） */
    public static final int COOKIE_MAX_AGE = Integer.parseInt(bundle.getString("cookie.maxage"));
    /** Cookieの暗号キー(128bit) */
    public static final String COOKIE_CIPHER_KEY = bundle.getString("cookie.cipherkey");

    /** google url shortener url */
    public static final String GOOGLE_URL_SHORTENER_URL = bundle.getString("google.url.shortener.url");
    /** google url shortener key */
    public static final String GOOGLE_URL_SHORTENER_KEY = bundle.getString("google.url.shortener.key");
}
