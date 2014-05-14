package whiphoiku.util;

import java.io.File;
import java.util.Map;

import whiphoiku.model.mail.SendMail;
import freemarker.template.TemplateException;

/**
 * MailUtil.
 * 
 * @author kilvistyle
 *
 */
public class MailUtil {

    public static final String KEY_SUBJECT = "Subject:";
    
    private MailUtil(){
    }

    /**
     * SendMailにテンプレートメッセージを設定する.
     * @param mail MailDto
     * @param ftlFile File テンプレートファイル(.ftl)
     * @param key_val Map<String, Object> 置換するキー：値のマップ
     * @throws TemplateException
     */
    public static void storeTemplate(SendMail mail, File ftlFile, Map<String, Object> key_val) throws TemplateException {
        storeTemplate(mail, ftlFile, key_val, null);
    }

    /**
     * SendMailにテンプレートメッセージを設定する（エンコード指定可能）.
     * @param mail SendMail
     * @param ftlFile File テンプレートファイル(.ftl)
     * @param key_val Map<String, Object> 置換するキー：値のマップ
     * @param encode String エンコード指定
     * @throws TemplateException
     */
    public static void storeTemplate(SendMail mail, File ftlFile, Map<String, Object> key_val, String encode) throws TemplateException {
        if (mail == null) {
            return;
        }
        // テンプレートメッセージを取得
        String templateMsg = FtlMessageUtil.getMessage(ftlFile, key_val, encode);
        
        // 件名が存在するかチェック
        boolean existSubject = isExistSubject(templateMsg);
        StringBuffer body = new StringBuffer();
        // 一行ずつ解析
        String[] arrMsgLine = StrUtil.splitLineBreak(templateMsg);
        for (String msgLine : arrMsgLine) {
            // 件名が存在する場合
            if (existSubject) {
                // 件名として設定
                mail.setSubject(msgLine.substring(KEY_SUBJECT.length()).trim());
                existSubject = false;
                continue;
            }
            // 以降は本文として取り込む
            if (0 < body.length()) {
                body.append("\r\n");
            }
            body.append(msgLine);
        }
        // 本文として設定
        mail.setBody(body.toString());
    }

    private static boolean isExistSubject(String msg) {
        if (msg == null || msg.length() < KEY_SUBJECT.length()) {
            return false;
        }
        // 件名を表す文字列から始まっているか
        return KEY_SUBJECT.equalsIgnoreCase(msg.substring(0, KEY_SUBJECT.length()));
    }
    
}
