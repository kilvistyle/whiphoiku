package whiphoiku.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slim3.util.WrapRuntimeException;


/**
 * DigestUtil.
 *
 * @author kilvistyle
 * @since 2009/02/18
 *
 */
public class DigestUtil {

    /**
     * 文字列を暗号化する.<BR>
     * MD5によるダイジェスト生成を行い16進数として返却する.<BR>
     *
     * @param before 暗号化前の文字列
     * @return String 暗号化後の文字列
     */
    public static final String toMD5digest(String before) {
            // パスワードからキーを作成
            MessageDigest md = getDigest("MD5");
            md.update(before.getBytes());
            // 生成したダイジェストを16進数で返却
            return StrUtil.toHex(md.digest());
    }
    
    private static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new WrapRuntimeException(e);
        }
    }

}
