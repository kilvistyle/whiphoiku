/**
 * Copyright (c) 2009 kilvistyle
 */
package whiphoiku.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slim3.util.RequestLocator;

/**
 * TokenUtil.
 * 
 * @author kilvistyle
 * @since 2009/11/17
 *
 */
public class TokenUtil {
	
	/** トークンを保持するキー名 ：token */
	public static final String TOKEN_KEY = "token";
    
    private TokenUtil() {
    }

    /**
     * 前回生成したトークン
     * （同じトークンを生成しないようタイムスタンプによるトークン生成を行い、さらに前回生成トークンと比較）
     */
    private static long previous;

    private static HttpServletRequest getRequest() {
        return RequestLocator.get();
    }
    
    /**
     * トークンが有効か判定する.
     * リクエストのトークンとセッションで保持しているトークンが一致しているかを判定する。
     * @return boolean true=トークンは有効, false=無効
     * @throws IllegalStateException リクエストパラメータにトークン（token）がセットされていない場合
     */
    public static synchronized boolean isTokenValid()
        throws IllegalStateException {
        return isTokenValid(false);
    }

    /**
     * トークンが有効か判定する.
     * リクエストのトークンとセッションで保持しているトークンが一致しているかを判定する。
     * @param reset boolean true=判定後にトークンをリセットする, false=　〃　しない
     * @return boolean true=トークンは有効, false=無効
     * @throws IllegalStateException リクエストパラメータにトークン（token）がセットされていない場合
     */
    public static synchronized boolean isTokenValid(boolean reset) throws IllegalStateException {
    	HttpServletRequest request = getRequest();
        // Retrieve the current session for this request
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        // Retrieve the transaction token from this session, and
        // reset it if requested
        String saved = (String) session.getAttribute(TOKEN_KEY);
        if (saved == null) {
            return false;
        }

        if (reset) {
            resetToken();
        }

        // Retrieve the transaction token included in this request
        String token = (String) request.getAttribute(TOKEN_KEY);
        if (token == null) {
        	return false;
//            throw new IllegalStateException("the Token parameter is null." +
//                " need save token on jsp files."+StrUtil.LS+
//                "for example)"+StrUtil.LS+ 
//                "<form method=\"post\" action=\"regist\">"+StrUtil.LS+
//                "  <input type=\"hideen\" ${sf:token()}/> <!-- save token -->"+StrUtil.LS+
//                "  <input type=\"submit\" value=\"Update\"/>"+StrUtil.LS+
//                "</form>");
        }

        return saved.equals(token);
    }

    /**
     * 判定用にセッションで保持しているトークンをリセットする.
     */
    public static synchronized void resetToken() {
        HttpSession session = getRequest().getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(TOKEN_KEY);
    }
    
    /**
     * トークンを発行して保持する.
     * 新しいトークンを生成し、リクエストスコープとセッションスコープにキー名「token」として保持する.
     * @return String 生成された新しいトークン
     */
    public static synchronized String saveToken() {
    	HttpServletRequest request = getRequest();
        HttpSession session = request.getSession();
        String token = generateToken();
        if (token != null) {
            request.setAttribute(TOKEN_KEY, token);
            session.setAttribute(TOKEN_KEY, token);
        }
        return token;
    }
    
    /**
     * トークンを生成する.
     * @return String 生成された新しいトークン
     */
    public static synchronized String generateToken() {
        HttpSession session = getRequest().getSession();
        try {
            byte id[] = session.getId().getBytes();
            long current = System.currentTimeMillis();
            if (current == previous) {
                current++;
            }
            previous = current;
            byte now[] = new Long(current).toString().getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(id);
            md.update(now);
            return toHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

    }

    /**
     * Convert a byte array to a String of hexadecimal digits and return it.
     * @param buffer The byte array to be converted
     */
    private static String toHex(byte buffer[]) {
        StringBuffer sb = new StringBuffer(buffer.length * 2);
        for (int i = 0; i < buffer.length; i++) {
            sb.append(Character.forDigit((buffer[i] & 0xf0) >> 4, 16));
            sb.append(Character.forDigit(buffer[i] & 0x0f, 16));
        }
        return sb.toString();
    }

}
