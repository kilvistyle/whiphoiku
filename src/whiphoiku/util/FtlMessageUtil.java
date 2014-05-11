package whiphoiku.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.slim3.util.WrapRuntimeException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * FtlMessageUtil.
 * テンプレートファイル（.ftl）を元に補完したメッセージを取得する。
 * 
 * @author kilvistyle
 */
public class FtlMessageUtil {

	/**
	 * インスタンスを隠蔽
	 */
	private FtlMessageUtil() {
	}

	/**
	 * 指定されたテンプレートファイル(.ftl)と置換変数から補完された文字列を取得する
	 * @param ftlFile File テンプレートファイル(.ftl)
	 * @param key_val Map<String, Object> [置換対象キー : 値]のMapオブジェクト
	 * @return String 補完されたテンプレートメッセージ
	 * @throws TemplateException
	 */
	public static String getMessage(File ftlFile, Map<String, Object> key_val) throws TemplateException {
		return getMessage(ftlFile, key_val, null);
	}

	/**
	 * 指定されたテンプレートファイル(.ftl)と置換変数から補完された文字列を取得する（エンコード指定可能）
	 * @param ftlFile File テンプレートファイル(.ftl)
	 * @param key_val Map<String, Object> [置換対象キー : 値]のMapオブジェクト
	 * @param encoding String エンコード指定
	 * @return String 補完されたテンプレートメッセージ
	 * @throws TemplateException
	 */
	public static String getMessage(File ftlFile, Map<String, Object> key_val, String encoding) throws TemplateException {
		// コンフィグレーション
	    Configuration cfg = new Configuration();
	    try {
		    // テンプレートディレクトリを指定
			cfg.setDirectoryForTemplateLoading(ftlFile.getParentFile());
		    // テンプレートを読み込み
			encoding = encoding != null ? encoding : "UTF-8";
		    Template temp = cfg.getTemplate(ftlFile.getName(), encoding);
		    // テンプレート処理
		    Writer out = new StringWriter();
		    temp.process(key_val, out);
		    // テンプレート処理結果返却
		    return out.toString();
		} catch (IOException e) {
			throw new WrapRuntimeException(e);
		}
	}
}
