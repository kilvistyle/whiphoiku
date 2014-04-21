/**
 * Copyright (c) 2009 Satoshi Chiba
 */
package whiphoiku.service.mail;

import java.util.ArrayList;
import java.util.List;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;
import org.slim3.util.BeanUtil;

import whiphoiku.meta.mail.SendMailMeta;
import whiphoiku.model.mail.SendMail;
import whiphoiku.model.mail.enums.SendState;
import whiphoiku.service.file.BlobFileService;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.mail.MailService.Attachment;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * SendMailService.
 * 
 * @author kilvistyle
 * @since 2009/11/16
 *
 */
public class SendMailService {
	
	public static final String TASK_PATH = "/task/mail/sendMail";
	public static final String QUEUE_NAME = "sendmail-queue";

    /** メール送信処理をするためのGETパラメータキー */
    public static final String SEND_MAIL_KEY = "SEND_MAIL_KEY";
    /** メール送信後に添付ファイルをデータストアから削除する為に使うGETパラメータキー */
    public static final String DELETE_ATTACHMENT_AFTER_SEND_MAIL_KEY = "DELETE_ATTACHMENT_AFTER_SEND_MAIL_KEY";
    
    private SendMailMeta meta = SendMailMeta.get();
    
    private static SendMailService instance = null;
    
    private SendMailService() {
    }
    
    public static SendMailService getInstance() {
    	if (instance == null) {
    		instance = new SendMailService();
    	}
    	return instance;
    }

    public SendMail get(Key key, Long version) {
        try {
            return Datastore.get(meta, key, version);
        }
        catch(EntityNotFoundRuntimeException e) {
            return null;
        }
    }

    public SendMail get(Key key) {
        try {
            return Datastore.get(meta, key);
        }
        catch(EntityNotFoundRuntimeException e) {
            return null;
        }
    }

    public List<SendMail> getAll() {
        return Datastore.query(meta).asList();
    }
    
    public List<SendMail> findByState(SendState state) {
        return Datastore.query(meta).filter(meta.state.equal(state)).asList();
    }
    
    /**
     * メールを送信する.
     * @param sendMail SendMail 送信メール
     */
    public void send(SendMail sendMail) {
        if (sendMail == null) return;
        // トランザクション開始
        Transaction tx = Datastore.beginTransaction();
        // 送信メールのKeyを生成
        sendMail.setKey(Datastore.allocateId(meta));
        // 送信メールをDatastoreに保存
        Datastore.put(tx, sendMail);
        // トランザクショナルな送信用TaskQueueを登録
        String keyString = KeyFactory.keyToString(sendMail.getKey());
        TaskOptions taskOptions =
            TaskOptions.Builder
            	.withUrl(TASK_PATH)
                .param(SEND_MAIL_KEY, keyString);
        QueueFactory.getQueue(QUEUE_NAME).add(tx, taskOptions);
        // コミット
        tx.commit();
    }
    
    /**
     * 添付ファイル付きのメールを送信する
     * @param sendMail SendMail 送信メール
     * @param isDeleteAttachmentsAfterSend boolean true=メール送信後に添付ファイルをDatastoreから削除, false=メール送信後も添付ファイルをDatastoreに残す
     * @param attachments Attachment... 添付ファイル
     */
    public void send(SendMail sendMail, boolean isDeleteAttachmentsAfterSend, Attachment... attachments) throws Exception {
        if (sendMail == null) return;
    	// 添付ファイルが指定されていない場合
    	if (attachments == null || attachments.length == 0) {
    		sendMail.setAttachedKeyList(null);
    		this.send(sendMail);
    		// 通常の送信を行い終了
    		return;
    	}
		// 添付ファイルが指定されている場合
    	BlobFileService fileService = BlobFileService.getInstance();
    	List<BlobKey> attachedKeyList = new ArrayList<BlobKey>(attachments.length);
    	// 添付ファイルをDatastoreに一旦格納
		for (Attachment attachment : attachments) {
			BlobKey bkey =
				fileService.upload(attachment.getFileName(), "", attachment.getData());
			attachedKeyList.add(bkey);
		}
		// Datastoreに格納したKeyリストを送信メールに設定
		sendMail.setAttachedKeyList(attachedKeyList);
        // トランザクション開始
        Transaction tx = Datastore.beginTransaction();
        // 送信メールのKeyを生成
        sendMail.setKey(Datastore.allocateId(meta));
        // 送信メールをDatastoreに保存
        Datastore.put(tx, sendMail);
        // トランザクショナルな送信用TaskQueueを登録
        String keyString = KeyFactory.keyToString(sendMail.getKey());
        TaskOptions taskOptions =
            TaskOptions.Builder
            	.withUrl(TASK_PATH)
                .param(SEND_MAIL_KEY, keyString)
                .param(DELETE_ATTACHMENT_AFTER_SEND_MAIL_KEY,
                		Boolean.toString(isDeleteAttachmentsAfterSend));
        QueueFactory.getQueue(QUEUE_NAME).add(tx, taskOptions);
        // コミット
        tx.commit();
    }

    protected void insert(SendMail sendMail) {
        Transaction tx = Datastore.beginTransaction();
        sendMail.setKey(Datastore.allocateId(meta));
        Datastore.put(tx, sendMail);
        tx.commit();
    }

    public SendMail update(SendMail sendMail) {
        Transaction tx = Datastore.beginTransaction();
        SendMail storedMail = Datastore.get(tx, meta, sendMail.getKey(), sendMail.getVersion());
        BeanUtil.copy(sendMail, storedMail);
        Datastore.put(tx, storedMail);
        tx.commit();
        return storedMail;
    }

    public void delete(Key key, Long version) {
        Transaction tx = Datastore.beginTransaction();
        SendMail sendMail = Datastore.get(tx, meta, key, version);
        Datastore.delete(tx, sendMail.getKey());
        tx.commit();
    }
}
