package whiphoiku.model.auth;

import java.util.Date;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModificationDate;

import whiphoiku.model.auth.enums.UserState;

import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;

@Model(schemaVersion = 1)
public class AppUser extends AuthUser {

    private static final long serialVersionUID = 1L;

    /**
     * ユーザIDをキーとしたプライマリキー.
     */
    @Attribute(primaryKey = true)
    private Key key;
    @Attribute(version = true)
    private Long version;
    
    /** ユーザID */
    private Long id;
    /** ユーザ名 */
    private String name;
    /** ユーザメールアドレス */
    private String mail;
    /** ユーザ住所 */
    private String address;
    /** ユーザ住所Geopoint */
    private GeoPt geoPt;
    /** ステータス */
    private UserState state = UserState.CERTIFIES;
    
    /** 検索条件：距離（km） */
    private Double distance;
    /** 検索条件：公立／私立（1=公立, 2=私立） */
    private Integer schoolKubun;
    /** 検索条件：年齢 */
    private Integer targetAge;
    
    @Attribute(lob=true)
    /** 権限 */
    private String[] roles;
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
        this.id = key == null ? null : key.getId();
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
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
		this.key = createKey(id);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the mail
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * @param mail the mail to set
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the geoPt
	 */
	public GeoPt getGeoPt() {
		return geoPt;
	}

	/**
	 * @param geoPt the geoPt to set
	 */
	public void setGeoPt(GeoPt geoPt) {
		this.geoPt = geoPt;
	}

	/**
	 * @return the state
	 */
	public UserState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(UserState state) {
		this.state = state;
	}

	/**
	 * @return the distance
	 */
	public Double getDistance() {
		return distance;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setDistance(Double distance) {
		this.distance = distance;
	}

	/**
	 * @return the schoolKubun
	 */
	public Integer getSchoolKubun() {
		return schoolKubun;
	}

	/**
	 * @param schoolKubun the schoolKubun to set
	 */
	public void setSchoolKubun(Integer schoolKubun) {
		this.schoolKubun = schoolKubun;
	}

	/**
	 * @return the targetAge
	 */
	public Integer getTargetAge() {
		return targetAge;
	}

	/**
	 * @param targetAge the targetAge to set
	 */
	public void setTargetAge(Integer targetAge) {
		this.targetAge = targetAge;
	}

	/**
	 * @return the roles
	 */
	public String[] getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(String[] roles) {
		this.roles = roles;
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
        AppUser other = (AppUser) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }

    public static final Key createKey(Long id) {
    	return id == null ? null : Datastore.createKey(AppUser.class, id);
    }

}
