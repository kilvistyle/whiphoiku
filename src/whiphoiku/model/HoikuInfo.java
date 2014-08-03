package whiphoiku.model;

import java.io.Serializable;
import java.util.Date;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModificationDate;

import whiphoiku.util.IJsonable;
import whiphoiku.util.JsonUtil;

import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;

@Model(schemaVersion = 1)
public class HoikuInfo implements Serializable, IJsonable {

    private static final long serialVersionUID = 1L;

    @Attribute(primaryKey = true)
    private Key key;

    @Attribute(version = true)
    private Long version;

    // 名前
    private String name;
    
    // Geoコード
    private GeoPt geoPt;

    // 郵便番号
    private String zipcode;

    // 住所
    private String address;
    
    // 電話番号
    private String tellNo;

    // HP
    private String officialUrl;

    // 私立・公立の区分。 1:公立  2:私立
    private int schoolKubun;

    // 募集人数 ０歳児
    private Integer collectZeroYear;
    
    // 募集人数 １歳児
    private Integer collectOneYear;

    // 募集人数 ２歳児
    private Integer collectTwoYear;

    // 募集人数 ３歳児
    private Integer collectThreeYear;
    
    // 募集人数 ４歳児
    private Integer collectFourYear;

    // 募集人数 ５歳児
    private Integer collectFiveYear;

    // 開所時間
    private Date openingTime;

    // 備考
    private String remarks;

    // 距離 (非永続化情報)
    @Attribute(persistent=false)
    private Double distance;
    // 対象年齢募集人数 (非永続化情報)
    @Attribute(persistent=false)
    private int targetVacant; 

    /** 初期登録日時 */
    @Attribute(listener = CreationDate.class)
    private Date insertTime;
    /** 最終更新日時 */
    @Attribute(listener = ModificationDate.class)
    private Date updateTime;
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTellNo() {
        return tellNo;
    }

    public void setTellNo(String tellNo) {
        this.tellNo = tellNo;
    }
    
    public int getSchoolKubun() {
        return schoolKubun;
    }

    public void setSchoolKubun(int schoolKubun) {
        this.schoolKubun = schoolKubun;
    }


    public Integer getCollectZeroYear() {
        return collectZeroYear;
    }

    public void setCollectZeroYear(Integer collectZeroYear) {
        this.collectZeroYear = collectZeroYear;
    }

    public Integer getCollectOneYear() {
        return collectOneYear;
    }

    public void setCollectOneYear(Integer collectOneYear) {
        this.collectOneYear = collectOneYear;
    }

    public Integer getCollectTwoYear() {
        return collectTwoYear;
    }

    public void setCollectTwoYear(Integer collectTwoYear) {
        this.collectTwoYear = collectTwoYear;
    }

    public Integer getCollectThreeYear() {
        return collectThreeYear;
    }

    public void setCollectThreeYear(Integer collectThreeYear) {
        this.collectThreeYear = collectThreeYear;
    }

    public Integer getCollectFourYear() {
        return collectFourYear;
    }

    public void setCollectFourYear(Integer collectFourYear) {
        this.collectFourYear = collectFourYear;
    }

    public Integer getCollectFiveYear() {
        return collectFiveYear;
    }

    public void setCollectFiveYear(Integer collectFiveYear) {
        this.collectFiveYear = collectFiveYear;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public int getTargetVacant() {
        return targetVacant;
    }

    public void setTargetVacant(int targetVacant) {
        this.targetVacant = targetVacant;
    }

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
    }

    public GeoPt getGeoPt() {
        return geoPt;
    }

    public void setGeoPt(GeoPt geoPt) {
        this.geoPt = geoPt;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getOfficialUrl() {
		return officialUrl;
	}

	public void setOfficialUrl(String officialUrl) {
		this.officialUrl = officialUrl;
	}

	public Date getOpeningTime() {
		return openingTime;
	}

	public void setOpeningTime(Date openingTime) {
		this.openingTime = openingTime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
        HoikuInfo other = (HoikuInfo) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }

    @Override
    public String toJSON() {
        return JsonUtil.object(
            JsonUtil.param("key", key),
            JsonUtil.param("version", version),
            JsonUtil.param("name", name),
            JsonUtil.paramExcludeNull("lat", geoPt!=null?geoPt.getLatitude():null),
            JsonUtil.paramExcludeNull("lng", geoPt!=null?geoPt.getLongitude():null),
            JsonUtil.param("address", address),
            JsonUtil.param("tellNo", tellNo),
            JsonUtil.param("schoolKubun", schoolKubun),
            JsonUtil.param("collectZeroYear", collectZeroYear),
            JsonUtil.param("collectOneYear", collectOneYear),
            JsonUtil.param("collectTwoYear", collectTwoYear),
            JsonUtil.param("collectThreeYear", collectThreeYear),
            JsonUtil.param("collectFourYear", collectFourYear),
            JsonUtil.param("collectFiveYear", collectFiveYear),
            JsonUtil.paramExcludeNull("distance", distance),
            JsonUtil.param("targetVacant", targetVacant)
        );
    }
}
