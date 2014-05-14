package whiphoiku.controller.task.mail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slim3.controller.Navigation;
import org.slim3.util.StringUtil;
import org.slim3.util.WrapRuntimeException;

import whiphoiku.controller.AbstractController;
import whiphoiku.model.mail.SendMail;
import whiphoiku.model.mail.enums.SendState;
import whiphoiku.service.file.BlobFileService;
import whiphoiku.service.mail.SendMailService;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailService.Attachment;
import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.mail.MailServiceFactory;

/**
 * SendMailController.
 * 
 * @author kilvistyle
 *
 */
public class SendMailController extends AbstractController {
	
	private static final Logger logger =
		Logger.getLogger(SendMailController.class.getName());

    @Override
    public Navigation run() throws Exception {
        // 送信メールキーを取得
        String keyString = asString(SendMailService.SEND_MAIL_KEY);
        if (StringUtil.isEmpty(keyString)) {
        	if (logger.isLoggable(Level.WARNING)) {
        		logger.log(Level.WARNING, "Send mail failed. the key is empty.");
        	}
        	return null;
        }
        SendMailService sendMailService = SendMailService.getInstance();
        // 送信メールを取得
        SendMail sMail = sendMailService.get(KeyFactory.stringToKey(keyString));
        if (sMail == null || !SendState.BEFORE_SEND.equals(sMail.getState())) {
            // メールが取得できない場合、または送信待ち以外の場合は何もしない
            return null;
        }
        // 送信前に送信中ステータスに変更（Datastoreの書き込み可否チェック）
        sMail.setState(SendState.SENDING);
        sMail = sendMailService.update(sMail);
        // 送信実行
        MailService mailService = MailServiceFactory.getMailService();
        Message message = new Message();
        message.setSubject(sMail.getSubject());
        message.setTextBody(sMail.getBody());
        message.setSender(sMail.getFrom());
        if (!StringUtil.isEmpty(sMail.getHtmlBody())) {
        	message.setHtmlBody(sMail.getHtmlBody());
        }
        if (isExists(sMail.getTo())) {
            message.setTo(sMail.getTo());
        }
        if (isExists(sMail.getCc())) {
        	message.setCc(sMail.getCc());
        }
        if (isExists(sMail.getBcc())) {
        	message.setBcc(sMail.getBcc());
        }
        List<BlobKey> attachedKeyList = null;
        if (isExists(sMail.getAttachedKeyList())) {
        	BlobFileService fileService = BlobFileService.getInstance();
        	List<Attachment> attachments = new ArrayList<Attachment>();
        	attachedKeyList = sMail.getAttachedKeyList();
        	for (BlobKey attachedKey : attachedKeyList) {
        		Attachment attachment = fileService.getAttachment(attachedKey);
        		if (attachment == null) {
                	if (logger.isLoggable(Level.WARNING)) {
                		logger.log(Level.WARNING, "Send mail failed("+sMail.getKey()
                				+"). the attachment file("+attachedKey+") is notfound.");
                	}
                	return null;
        		}
        		attachments.add(attachment);
        	}
        	message.setAttachments(attachments);
        	// 送信後に添付ファイルを削除する場合
        	if (asBoolean(SendMailService.DELETE_ATTACHMENT_AFTER_SEND_MAIL_KEY)) {
        		sMail.setAttachedKeyList(null);
        	}
        }
        try {
            mailService.send(message);
        } catch (IOException e) {
            throw new WrapRuntimeException(e.getMessage(),e);
        }
        // 送信完了ステータスに変更
        sMail.setState(SendState.SEND_SUCCEEDED);
        sendMailService.update(sMail);
    	// 送信後に添付ファイルを削除する場合
    	if (asBoolean(SendMailService.DELETE_ATTACHMENT_AFTER_SEND_MAIL_KEY)) {
    		BlobFileService.getInstance().delete(attachedKeyList.toArray(new BlobKey[]{}));
    	}
    	return null;
    }

    private boolean isExists(Collection<?> collection) {
    	return collection != null && !collection.isEmpty();
    }
    
}
