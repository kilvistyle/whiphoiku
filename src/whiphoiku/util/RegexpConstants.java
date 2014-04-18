package whiphoiku.util;

/**
 * 正規表現マッチパターン定義.
 * よく使うヤツまとめ。
 * @author kilvistyle
 */
public class RegexpConstants {
	
	/** ASCIIのみ */
	public static final String MATCH_ASCII = "^[\\u0020-\\u007E]+$";
	/** 半角英数のみ */
	public static final String MATCH_NUMALPHA = "^[0-9a-zA-Z]+$";
	/** 半角英語のみ */
	public static final String MATCH_ALPHA = "^[a-zA-Z]+$";
	/** 半角数字のみ */
	public static final String MATCH_NUMBER = "^[0-9]+$";
	/** ひらがなのみ */
	public static final String MATCH_HIRAGANA = "^[\\u3040-\\u309F]+$";
	/** カタカナのみ */
	public static final String MATCH_KATAKANA = "^[\\u30A0-\\u30FF]+$";
	/** メールアドレス */
	public static final String MATCH_MAIL = "[\\w\\.\\-]+@(?:[\\w\\-]+\\.)+[\\w\\-]+";
	/** 電話番号（簡易） */
	public static final String MATCH_PHONE = "\\d{2,4}-\\d{2,4}-\\d{4}";
	/** 郵便番号（前３桁） */
	public static final String MATCH_ZIPCODE_LEFT = "\\d{3}";
	/** 郵便番号（後４桁） */
	public static final String MATCH_ZIPCODE_RIGHT = "\\d{4}";
	/** 郵便番号（000-0000） */
	public static final String MATCH_ZIPCODE = "\\d{3}-\\d{4}";
	/** URL */
	public static final String MATCH_URL = 
		  "^(https?|ftp)(:\\/\\/[-_.!~*\\'()a-zA-Z0-9;\\/?:\\@&=+\\$,%#]+)$";

}
