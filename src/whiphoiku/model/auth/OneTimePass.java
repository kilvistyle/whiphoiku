package whiphoiku.model.auth;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModificationDate;
import org.slim3.util.StringUtil;

import com.google.appengine.api.datastore.Key;

/**
 * OneTimePass.
 * 
 * @author kilvistyle
 */
@Model(schemaVersion = 1)
public class OneTimePass implements Serializable {

    private static final long serialVersionUID = 1L;

    @Attribute(primaryKey = true)
    private Key key;
    @Attribute(version = true)
    private Long version;

    /** ワンタイムパスワード（PK） */
    private String oneTimePass;
    /** ワンタイムパスワードの状態 */
    private OtpState state = OtpState.INIT;
    /** 認証パラメータ */
    @Attribute(lob=true)
    private Map<String, Object> paramsMap = new HashMap<String, Object>();
    /** 初期登録日時 */
    @Attribute(listener = CreationDate.class)
    private Date insertTime;
    /** 最終更新日時 */
    @Attribute(listener = ModificationDate.class)
    private Date updateTime;

    /**
     * Returns the key.
     *
     * @return the key
     */
    public Key getKey() {
        return key;
    }

    /**
     * Sets the key.
     *
     * @param key
     *            the key
     */
    public void setKey(Key key) {
        this.key = key;
        this.oneTimePass = key == null ? null : key.getName();
    }

    /**
     * Returns the version.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version
     *            the version
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
	 * @return the oneTimePass
	 */
	public String getOneTimePass() {
		return oneTimePass;
	}

	/**
	 * @param oneTimePass the oneTimePass to set
	 */
	public void setOneTimePass(String oneTimePass) {
		this.oneTimePass = oneTimePass;
		this.key = createKey(oneTimePass);
	}

	/**
	 * @return the state
	 */
	public OtpState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(OtpState state) {
		this.state = state;
	}
	
	/**
	 * @return the paramsMap
	 */
	public Map<String, Object> getParamsMap() {
		return paramsMap;
	}

	/**
	 * @param paramsMap the paramsMap to set
	 */
	public void setParamsMap(Map<String, Object> paramsMap) {
		if (paramsMap == null) {
			paramsMap = new HashMap<String, Object>();
		}
		this.paramsMap = paramsMap;
	}

	@SuppressWarnings("unchecked")
	public <V> V param(CharSequence name) {
		return (V) paramsMap.get(name.toString());
	}
	
	public void param(CharSequence name, Object value) {
		paramsMap.put(name.toString(), value);
	}

	/**
	 * @return the insertTime
	 */
	public Date getInsertTime() {
		return insertTime;
	}

	/**
	 * @param insertTime the insertTime to set
	 */
	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	/**
	 * @return the updateTime
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime the updateTime to set
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OneTimePass other = (OneTimePass) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }
    
    public static final Key createKey(String oneTimePass) {
    	return StringUtil.isEmpty(oneTimePass) ? null : Datastore.createKey(OneTimePass.class, oneTimePass);
    }

    /**
     * OtpState.
     * ワンタイムパスワードの状態を表します。
     * @author s.chiba
     */
    public enum OtpState {
    	/** 初期化中 */
    	INIT,
        /** 未使用 */
        NOT_USE,
        /** 使用済み */
        USED;
    }
}
