package whiphoiku.service.auth;

import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Random;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;
import org.slim3.util.BeanUtil;
import org.slim3.util.DateUtil;

import whiphoiku.model.auth.OneTimePass;
import whiphoiku.model.auth.OneTimePass.OtpState;
import whiphoiku.util.AppProps;
import whiphoiku.util.DigestUtil;
import whiphoiku.util.StrUtil;

import com.google.appengine.api.datastore.Transaction;

/**
 * OneTimePassService.
 * ワンタイムパスワードの生成、使用などを行います。
 * 
 * @author kilvistyle
 */
public class OneTimePassService {
	
	private static OneTimePassService instance = null;
	
	private OneTimePassService() {
	}
	
	public static final OneTimePassService getInstance() {
		if (instance == null) {
			instance = new OneTimePassService();
		}
		return instance;
	}
	
	/**
	 * 未使用のOneTimePassオブジェクトを生成して返却する.
	 * @return
	 * @throws Exception
	 */
	public OneTimePass create() {
		// まだ生成されていないワンタイムパスワードを生成する.
        while (true) {
            String newOTP = generateKey();
            Transaction tx = Datastore.beginTransaction();
            try {
            	Datastore.get(OneTimePass.class, OneTimePass.createKey(newOTP));
            }
            catch (EntityNotFoundRuntimeException e) {
            	// 生成したワンタイムパスワードがユニークな場合にPUTして返却
            	OneTimePass otp = new OneTimePass();
            	otp.setOneTimePass(newOTP);
            	try {
                	Datastore.put(tx, otp);
                	tx.commit();
                	// 次の更新に備えてNOT_USEに変更して返却
                	otp.setState(OtpState.NOT_USE);
                	return otp;
            	}
            	catch (ConcurrentModificationException cme) {
            		// 登録時に競合したら再生成
				}
			}
            // 存在する場合はロールバックして再生成
            if (tx.isActive()) {
                tx.rollback();
            }
        }
	}

	public OneTimePass update(OneTimePass otp) throws EntityNotFoundRuntimeException, ConcurrentModificationException {
        Transaction tx = Datastore.beginTransaction();
        try {
            // このKeyで既に存在する場合はfalse
        	OneTimePass m = Datastore.get(tx, otp.getClass(), otp.getKey(), otp.getVersion());
            BeanUtil.copy(otp, m);
            Datastore.put(tx, m);
            tx.commit();
            return m;
        }
        catch (EntityNotFoundRuntimeException e) {
            // 存在しない場合はロールバックして終了
            if (tx.isActive()) {
            	tx.rollback();
            }
            throw e;
        }
        catch (ConcurrentModificationException e) {
            // 追い越し更新となる場合はロールバックして終了
            if (tx.isActive()) {
            	tx.rollback();
            }
            throw e;
        }
    }
	
	/**
	 * パス文字列からOneTimePassを取得する.
	 * @param oneTimePass
	 * @return
	 */
	public OneTimePass get(String oneTimePass) {
		return Datastore.getOrNull(OneTimePass.class, OneTimePass.createKey(oneTimePass));
	}

	/**
	 * ワンタイムパスワードを生成する.
	 * @return
	 * @throws Exception
	 */
	public static synchronized String generateKey() {
		// ４桁の乱数を生成
		String prefix = StrUtil.paddingChar(""+new Random().nextInt(10000), '0', 4);
		// 現在の時間を文字列形式で取得
		String now = DateUtil.toString(new Date(), "yyyyMMddHHmmssSSS");
		// これらを連結してダイジェスト変換した結果を返却
		return DigestUtil.toMD5digest(prefix + now);
	}

    /**
     * ワンタイムパスワードが有効期限切れであるか判定する.
     * @param otp
     * @return
     */
    public boolean isExpired(OneTimePass otp) {
    	Date issueDate = otp.getInsertTime();
    	Calendar cal = DateUtil.toCalendar(issueDate);
    	// 有効期間（単位：日）を加算
    	cal.add(Calendar.DATE, AppProps.OTP_VALIDTERM);
    	// 有効期限が現在より過去の場合は期限切れ
    	return cal.before(new Date());
    }

}
