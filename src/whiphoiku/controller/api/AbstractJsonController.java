package whiphoiku.controller.api;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slim3.controller.Navigation;
import org.slim3.controller.validator.Validators;
import org.slim3.util.ThrowableUtil;

import whiphoiku.controller.AbstractController;
import whiphoiku.util.AppProps;
import whiphoiku.util.IJsonable;
import whiphoiku.util.JsonUtil;

/**
 * AbstractJsonController.
 * レスポンスにJson出力する抽象コントローラクラス.
 * 
 * @author kilvistyle
 */
public abstract class AbstractJsonController extends AbstractController {
	
    // default encoding
	/** デフォルト文字コード　UTF-8 */
	public static final String DEFAULT_ENCODING = AppProps.DEFAULT_CHARSET;
	
	// error code
	/** ERR_VALIDATORS：バリデーションエラー */
	public static final String ERR_VALIDATORS = "ERR_VALIDATORS";
	/** ERR_INVALID_REQUEST：無効なリクエストエラー */
	public static final String ERR_INVALID_REQUEST = "ERR_INVALID_REQUEST";
	/** ERR_DS_LEADONRY：データストア読み取り専用エラー（DSメンテナンス中） */
	public static final String ERR_DS_LEADONRY = "ERR_DS_LEADONRY";
	/** ERR_DS_UNKNOWN：データストア不明なエラー */
	public static final String ERR_DS_UNKNOWN  = "ERR_DS_UNKNOWN";
	/** ERR_URLFETCH_IO：URLフェッチエラー（外部アクセスエラー） */
	public static final String ERR_URLFETCH_IO = "ERR_URLFETCH_IO";
	/** ERR_UNKNOWN:不明なエラー */
	public static final String ERR_UNKNOWN = "ERR_UNKNOWN";

    /**
     * レスポンスにJson出力する.
     * @param json　String Json文字列
     * @return
     */
    protected Navigation json(String json) {
        return json(json, DEFAULT_ENCODING);
    }

    /**
     * レスポンスにJson出力する（エンコード指定可）.
     * @param json　String Json文字列
     * @param encoding 文字コード
     * @return
     */
    protected Navigation json(String json, String encoding) {
        try {
            response.setContentType("text/html; charset="+encoding);
            PrintWriter out = response.getWriter();
            try {
                out.print(json);
                out.flush();
            }
            finally {
                out.close();
            }
        }
        catch (Exception e) {
            ThrowableUtil.wrapAndThrow(e);
        }
        return null;
    }
    
	/**
	 * レスポンスにJson出力する.
	 * @param jsonable IJsonableの実装クラス.
	 * @return
	 */
	protected Navigation json(IJsonable jsonable) {
		return json(jsonable.toJSON());
	}

	/**
	 * レスポンスにJson出力する（エンコード指定可）.
	 * @param jsonable IJsonableの実装クラス.
	 * @param encoding 文字コード
	 * @return
	 */
	protected Navigation json(IJsonable jsonable, String encoding) {
		return json(jsonable.toJSON(), encoding);
	}

	/**
	 * レスポンスにオブジェクトをJson出力する.
     * ＜出力形式＞
     * {"(name)":(jsonable)}
     * 
	 * @param name
	 * @param jsonable
	 * @return
	 */
	protected Navigation json(String name, Object jsonable) {
		return json(JsonUtil.object(
			JsonUtil.param(name, jsonable)
		));
	}
	/**
	 * レスポンスにオブジェクトをJson出力する（エンコード指定可）.
     * ＜出力形式＞
     * {"(name)":(jsonable)}
     * 
	 * @param name
	 * @param jsonable
	 * @param encoding
	 * @return
	 */
	protected Navigation json(String name, Object jsonable, String encoding) {
		return json(JsonUtil.object(
			JsonUtil.param(name, jsonable)
		), encoding);
	}
	
	/**
     * レスポンスにThrowableエラーをJson出力する.
     * ＜出力形式＞
     * {
     *   "errCode":"(errCode)",
     *   "errMsg" :"(throwable.getMessage())",
     *   "errDtl" :"(throwable.toString())"
     * }
     * @param errCode String エラーコード
     * @param throwable Throwable エラー内容
     * @return
     */
    protected Navigation errorToJson(String errCode, Throwable throwable) {
    	return errorToJson(errCode, throwable.getMessage(), throwable.toString());
    }
    
    /**
     * レスポンスに任意のエラーをJson出力する.
     * ＜出力形式＞
     * {
     *   "errCode":"(errCode)",
     *   "errMsg" :"(message)",
     *   "errDtl" :"(detail)"
     * }
     * @param errCode String エラーコード
     * @param message String エラーメッセージ
     * @param detail String 詳細メッセージ
     * @return
     */
    protected Navigation errorToJson(String errCode, String message, String detail) {
		String strJsonObj = 
			JsonUtil.object(
				JsonUtil.param("errCode", errCode),
				JsonUtil.param("errMsg", message),
				JsonUtil.param("errDtl", detail)
			);
    	return json(strJsonObj);
    }
    
    /**
     * レスポンスにバリデーションエラー(Errors)をJson出力する.
     * ＜出力形式＞
     * {
     *   "errCode":"ERR_VALIDATORS",
     *   "errMsg" :"(プロパティ1のエラーメッセージ)",
     *   "errDtl" :"(プロパティ1のエラーメッセージ)"
     *   "errors":[
     *       {"name":"(プロパティ1名)","message":"(エラーメッセージ1)"},
     *       {"name":"(プロパティ2名)","message":"(エラーメッセージ2)"},
     *       {"name":"(プロパティ3名)","message":"(エラーメッセージ3)"},
     *       ...
     *     ]
     * }
     * @see Validators#errors
     * @return
     */
    protected Navigation validatorErrToJson() {
        // バリデーションエラーリストを取得
        List<JsonError> errorList = getErrors();
        // 先頭のエラーメッセージを抽出
        String message = "";
        String detail = "";
        if (!errorList.isEmpty()) {
            message = detail = errorList.get(0).message;
        }
        String strJsonObj = 
            JsonUtil.object(
                JsonUtil.param("errCode", ERR_VALIDATORS),
                JsonUtil.param("errMsg", message),
                JsonUtil.param("errDtl", detail),
                JsonUtil.param("errors", errorList)
            );
        return json(strJsonObj);
    }
    
    /**
     * JsonControllerのエラーハンドリングのデフォルト処理.
     * レスポンスにThrowableエラーをJson出力する.
     * ＜出力形式＞
     * {
     *   "errCode":"ERR_UNKNOWN",
     *   "errMsg" :"(error.getMessage())",
     *   "errDtl" :"(error.toString())"
     * }
     */
	@Override
	protected Navigation handleError(Throwable error) throws Throwable {
		Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, error.getMessage(), error);
		return errorToJson(ERR_UNKNOWN, error);
	}
    
    private List<JsonError> getErrors() {
        if (errors.isEmpty()) throw new IllegalStateException("the error is nothing.");
        List<JsonError> errorJsons = new ArrayList<JsonError>();
        for (Entry<String, String> entry : errors.entrySet()) {
            errorJsons.add(new JsonError(entry.getKey(), entry.getValue()));
        }
        return errorJsons;
    }
    class JsonError implements IJsonable {
        private String name;
        private String message;
        public JsonError(String name, String message) {
            this.name = name;
            this.message = message;
        }
        public String toJSON() {
            return JsonUtil.object(
                JsonUtil.param("name", name),
                JsonUtil.param("message", message)
            );
        }
    }
}
