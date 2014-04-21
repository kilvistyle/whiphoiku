package whiphoiku.model.mail;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModificationDate;

import whiphoiku.model.mail.enums.SendState;

@Model(schemaVersion = 1)
public class SendMail implements Serializable {

    private static final long serialVersionUID = 1L;

	@Attribute(primaryKey = true)
    private Key key;
    @Attribute(version = true)
    private Long version = 0L;
    private SendState state = SendState.BEFORE_SEND;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String from;
    private String subject;
    @Attribute(lob=true)
    private String body;
    @Attribute(lob=true)
    private String htmlBody;
    @Attribute(unindexed=true)
    private List<BlobKey> attachedKeyList;
    @Attribute(listener=CreationDate.class)
    private Date insertDate;
    @Attribute(listener=ModificationDate.class)
    private Date updateDate;
    
    /**
     * @return key
     */
    public Key getKey() {
        return key;
    }
    /**
     * @param key セットする key
     */
    public void setKey(Key key) {
        this.key = key;
    }
    /**
     * @return version
     */
    public Long getVersion() {
        return version;
    }
    /**
     * @param version セットする version
     */
    public void setVersion(Long version) {
        this.version = version;
    }
    /**
     * @return state
     */
    public SendState getState() {
        return state;
    }
    /**
     * @param state セットする state
     */
    public void setState(SendState state) {
        this.state = state;
    }
    /**
     * @return to
     */
    public List<String> getTo() {
        return to;
    }
    /**
     * @param to セットする to
     */
    public void setTo(List<String> to) {
        this.to = to;
    }
    /**
     * @return cc
     */
    public List<String> getCc() {
        return cc;
    }
    /**
     * @param cc セットする cc
     */
    public void setCc(List<String> cc) {
        this.cc = cc;
    }
    /**
     * @return bcc
     */
    public List<String> getBcc() {
        return bcc;
    }
    /**
     * @param bcc セットする bcc
     */
    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }
    /**
     * @return from
     */
    public String getFrom() {
        return from;
    }
    /**
     * @param from セットする from
     */
    public void setFrom(String from) {
        this.from = from;
    }
    /**
     * @return subject
     */
    public String getSubject() {
        return subject;
    }
    /**
     * @param subject セットする subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
    /**
     * @param body セットする body
     */
    public void setBody(String body) {
        this.body = body;
    }
    /**
     * @return body
     */
    public String getBody() {
        return body;
    }
	/**
	 * @return htmlBody
	 */
	public String getHtmlBody() {
		return htmlBody;
	}
	/**
	 * @param htmlBody セットする htmlBody
	 */
	public void setHtmlBody(String htmlBody) {
		this.htmlBody = htmlBody;
	}
	/**
	 * @return attachedKeyList
	 */
	public List<BlobKey> getAttachedKeyList() {
		return attachedKeyList;
	}
	/**
	 * @param attachedKeyList セットする attachedKeyList
	 */
	public void setAttachedKeyList(List<BlobKey> attachedKeyList) {
		this.attachedKeyList = attachedKeyList;
	}
	/**
	 * @return insertDate
	 */
	public Date getInsertDate() {
		return insertDate;
	}
	/**
	 * @param insertDate セットする insertDate
	 */
	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}
	/**
	 * @return updateDate
	 */
	public Date getUpdateDate() {
		return updateDate;
	}
	/**
	 * @param updateDate セットする updateDate
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
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
        SendMail other = (SendMail) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }

    public Long getId() {
    	return key == null ? null : key.getId();
    }
    
    public void setId(Long id) {
    	key = id == null ? null : Datastore.createKey(SendMail.class, id);
    }
    
}
