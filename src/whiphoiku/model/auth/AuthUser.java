package whiphoiku.model.auth;

import java.io.Serializable;

import com.google.appengine.api.datastore.Key;

/**
 * AuthUser.
 * ユーザを表す抽象モデルクラス.
 * アプリケーションでユーザのログインや権限による制限を行う場合は、このクラスを継承してユーザを表すモデルクラスを作成する.
 * AuthUserは
 * ・AuthServiceを使うことで、安全なログインセッションの開始（セッション固定攻撃対応）や権限チェックなどが可能。
 * ・AuthFilterを使うことで、url毎に権限によるアクセス制限を行い、必要に応じてログインページへ誘導することが可能。
 * 
 * @author kilvistyle
 *
 */
public abstract class AuthUser implements Serializable {
	
	private static final long serialVersionUID = 1L;

    /**
     * ユーザのプライマリキーを取得する.
     * ※プライマリキーはユーザIDで作られたKeyである必要があります。
     * @return
     */
    public abstract Key getKey();
    
    /**
     * ロールを取得する.
     * @return String[]
     */
    public abstract String[] getRoles();
    
    /**
     * ユーザ情報のバージョンを取得する.
     * @return Long
     */
    public abstract Long getVersion();
    
    /**
     * ユーザの名前を取得する.
     * @return
     */
    public abstract String getName();

}

