package whiphoiku.controller;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.util.IntegerUtil;
import org.slim3.util.StringUtil;

import com.google.appengine.api.datastore.Cursor;

/**
 * AbstractController.
 * 
 * @author kilvistyle.
 *
 */
public abstract class AbstractController extends Controller {
	
	/**
	 * リクエストパラメータが存在するか判定する.
	 * 値がStringの場合、空文字はfalseと判定します。
	 * @param name CharSequence
	 * @return boolean
	 */
	protected boolean has(CharSequence name) {
		Object o = super.requestScope(name);
		if (o == null) return false;
		return !StringUtil.isEmpty(o.toString());
	}

	/**
	 * リクエストパラメータをBoolean型で取得する。
	 * 指定されたパラメータが存在しない場合はfalseとし、必ずBooleanの結果を返却します。
	 * nullはありえません。
     * @param name CharSequence キー名
     * @return Boolean 
	 */
    protected Boolean asBoolean(CharSequence name) {
    	Boolean b = super.asBoolean(name);
    	return b == null ? Boolean.FALSE : b;
    }

    /**
     * リクエストパラメータをEnum型で取得する。
     * @param name CharSequence キー名
     * @param enumClass Enum型
     * @return Enumオブジェクト
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	protected <T extends Enum> T asEnum(CharSequence name, Class<T> enumClass) {
        Object value = super.requestScope(name);
        if (value == null) return null;
        if (value.getClass() == String.class) {
            return (T)Enum.valueOf(enumClass, (String) value);
        }
        if (value instanceof Number) {
            int ordinal = IntegerUtil.toPrimitiveInt(value);
            return (T)enumClass.getEnumConstants()[ordinal];
        }
        return (T)value;
    }
    
    /**
     * リクエストパラメータをCursorに変換して取得する。
     * Cursorがない場合や不正な値の場合はnullを返却する.
     * @param name CharSequence キー名
     * @return Cursorオブジェクト
     */
    protected Cursor asCursor(CharSequence name) {
    	String cursor = super.asString(name);
    	if (StringUtil.isEmpty(cursor)) return null;
		try {
			return Cursor.fromWebSafeString(cursor);
		}
		catch (IllegalArgumentException e) {
			return null;
		}
    }

    /**
     * 共通エラーハンドリング処理
     */
	@Override
	protected Navigation handleError(Throwable error) throws Throwable {
		return super.handleError(error);
	}
    
}
