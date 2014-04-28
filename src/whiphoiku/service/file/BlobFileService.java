/**
 * 
 */
package whiphoiku.service.file;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.slim3.controller.upload.FileItem;
import org.slim3.util.ResponseLocator;
import org.slim3.util.StringUtil;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
// TODO 2014/04/22 kilvisytle 非推奨らしい。GCSへの移行を検討
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.Image.Format;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.mail.MailService.Attachment;

/**
 * BlobFileService.
 * BlobStore操作に関するサービスクラス。
 * 
 * @author kilvistyle
 */
public class BlobFileService {
	
	private static final Logger logger =
		Logger.getLogger(BlobFileService.class.getName());
	private static BlobFileService instance = null;
	private static final int RETRY_GET_BLOBKEY = 5;
	
	private BlobFileService() {
	}
	
	public static final BlobFileService getInstance() {
		if (instance == null) {
			instance = new BlobFileService();
		}
		return instance;
	}

	/**
	 * ファイルアイテムをImageオブジェクトに変換する.
	 * @param file FileItem アップロードされたファイルアイテム
	 * @return Image イメージオブジェクト
	 * @throws IllegalArgumentException アップロードされたファイルがイメージデータではない場合
	 */
	public Image toImage(FileItem file) throws IllegalArgumentException {
		return toImage(file.getData());
	}
	/**
     * データをImageオブジェクトに変換する.
	 * @param imageData byte[] イメージデータ
	 * @return Image イメージオブジェクト
	 * @throws IllegalArgumentException データがイメージデータではない場合
	 */
	public Image toImage(byte[] imageData) throws IllegalArgumentException {
        Image image = ImagesServiceFactory.makeImage(imageData);
        if (image == null) throw new IllegalArgumentException();
        if (image.getFormat() == null) throw new IllegalArgumentException();
        return image;
	}
	
	/**
	 * ファイルアイテムをBlobStoreにアップロードする.
	 * @param file FileItem ファイルアイテム
	 * @return BlobKey BlobStoreに格納したキー
	 * @throws IOException　書き込みに失敗した場合
	 */
	public BlobKey upload(FileItem file) throws IOException {
		return upload(file.getFileName(), file.getContentType(), file.getData());
	}
	
	/**
	 * イメージオブジェクトをBlobStoreにアップロードする.
	 * @param fileName String ファイル名
	 * @param image Image イメージオブジェクト
	 * @return BlobKey BlobStoreに格納したキー
	 * @throws IllegalArgumentException Imageオブジェクトがイメージではない場合
	 * @throws IOException　書き込みに失敗した場合
	 */
	public BlobKey upload(String fileName, Image image) throws IllegalArgumentException, IOException {
	    return upload(fileName ,getContentType(image), image.getImageData());
	}

    /**
     * ImageオブジェクトをBlobStoreにアップロードする.
     * @param image Image イメージオブジェクト
     * @return BlobKey BlobStoreに格納したキー
     * @throws IllegalArgumentException Imageオブジェクトがイメージではない場合
     * @throws IOException　書き込みに失敗した場合
     */
    public BlobKey upload(Image image) throws IllegalArgumentException, IOException {
        return upload(null, getContentType(image), image.getImageData());
    }
    
	private String getContentType(Image image) throws IllegalArgumentException {
	    Format format = image.getFormat();
	    if (format == null) throw new IllegalArgumentException("the format is null.");
	    switch (format) {
            case BMP: return "image/bmp";
            case GIF: return "image/gif";
            case ICO: return "image/ico";
            case JPEG: return "image/jpeg";
            case PNG: return "image/png";
            case TIFF: return "image/tiff";
            case WEBP: return "image/webp";
        }
	    throw new IllegalArgumentException("the unknown format.("+format+")");
	}

	/**
	 * データをBlobStoreにアップロードする.
	 * @param fileName String ファイル名
	 * @param contentType String コンテンツタイプ
	 * @param bytes byte[] ファイルデータ
     * @return BlobKey BlobStoreに格納したキー
	 * @throws IOException　書き込みに失敗した場合
	 */
	public BlobKey upload(String fileName, String contentType, byte[] bytes) throws IOException {
		com.google.appengine.api.files.FileService fileService =
			FileServiceFactory.getFileService();
		AppEngineFile blobFile;
		if (StringUtil.isEmpty(fileName)) {
			blobFile = fileService.createNewBlobFile(contentType);
		}
		else {
			blobFile = fileService.createNewBlobFile(contentType, fileName);
		}
		FileWriteChannel writeChannel = null;
		try {
			writeChannel = fileService.openWriteChannel(blobFile, true);
			writeChannel.write(ByteBuffer.wrap(bytes));
		}
		finally {
			if (writeChannel != null) {
				writeChannel.closeFinally();
			}
		}
		// 書き込み直後はBlobKeyが取得できないことがあるため時間差リトライを行う
		int retry = RETRY_GET_BLOBKEY;
		BlobKey blobKey = null;
		try {
			while (blobKey == null) {
				blobKey = fileService.getBlobKey(blobFile);
				if (blobKey != null || retry < 0) return blobKey;
				randomSleep();
				retry--;
			}
		}
		catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}
		if (blobKey == null) {
			logger.warning("upload filed. file name = "+fileName);
		}
		return blobKey;
	}
	
	private void randomSleep() throws InterruptedException {
        int min = 200;  // min sleep ms
        int max = 1000; // max sleep ms
        int sleep = min + (int) (Math.random() * ((max - min) + 1));
        logger.warning("sleep: " + sleep + " ms then retrying get blobkey:");
        Thread.sleep(sleep);
	}

	/**
	 * BlobKeyに該当するBlobデータをダウンロードする.
	 * @param bkey BlobKey キー
	 * @throws IOException　書き込みに失敗した場合
	 */
	public void serve(BlobKey bkey) throws IOException {
		BlobstoreServiceFactory
			.getBlobstoreService()
			.serve(bkey, ResponseLocator.get());
	}
	
	/**
	 * BlobKeyに該当するBlobデータをAppEngineFileとして取得する.
     * @param bkey BlobKey キー
	 * @return AppEngineFile ファイル
	 * @throws FileNotFoundException　キーに該当するファイルが見つからない場合
	 */
	public AppEngineFile getFile(BlobKey bkey) throws FileNotFoundException {
		return FileServiceFactory
				.getFileService()
				.getBlobFile(bkey);
	}

	/**
	 * BlobKeyに該当するデータを取得する.
     * @param bkey BlobKey キー
	 * @return byte[] データ
	 * @throws IOException　書き込みに失敗した場合
	 */
	public byte[] getBytes(BlobKey bkey) throws IOException {
		AppEngineFile file = getFile(bkey);
		FileService service = FileServiceFactory.getFileService();
		FileReadChannel readChannel = service.openReadChannel(file, false);
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		ByteBuffer bb = ByteBuffer.wrap(new byte[1024]);
		try {
			while (readChannel.read(bb) != -1) {
				bb.rewind();
				bao.write(bb.array());
				bb.clear();
			}
			return bao.toByteArray();
		}
		finally {
			bao.close();
		}
	}
	
	/**
	 * BlobKeyに該当するデータを取得する.
     * @param bkey BlobKey キー
	 * @return Attachment 添付ファイル
	 * @throws IOException　書き込みに失敗した場合
	 */
	public Attachment getAttachment(BlobKey bkey) throws IOException {
		AppEngineFile file = getFile(bkey);
		FileService service = FileServiceFactory.getFileService();
		FileReadChannel readChannel = service.openReadChannel(file, false);
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		ByteBuffer bb = ByteBuffer.wrap(new byte[1024]);
		try {
			while (readChannel.read(bb) != -1) {
				bb.rewind();
				bao.write(bb.array());
				bb.clear();
			}
			return new Attachment(file.getNamePart(), bao.toByteArray());
		}
		finally {
			bao.close();
		}
	}

	/**
	 * BlobKeyに該当するImageオブジェクトを取得する.
     * @param bkey BlobKey キー
	 * @return Image Imageオブジェクト
	 */
	public Image getImage(BlobKey bkey) {
		return ImagesServiceFactory.makeImageFromBlob(bkey);
	}
	
	/**
	 * BlobKeyに該当するImageオブジェクトを指すURLを取得する.
	 * @param bkey BlobKey キー
	 * @return String ImageのURL
	 */
	public String getImageURL(BlobKey bkey) {
		return ImagesServiceFactory
			.getImagesService()
			.getServingUrl(bkey);
	}
	
	/**
	 * BlobKeyに該当するImageオブジェクトを指すURLを取得する（サイズ指定可）.
	 * @param bkey BlobKey キー
	 * @param imageSize int サイズ（ピクセル単位）
	 * @param crop boolean cropするか否か
	 * @return String ImageのURL
	 */
	public String getImageURL(BlobKey bkey, int imageSize, boolean crop) {
		return ImagesServiceFactory
			.getImagesService()
			.getServingUrl(bkey, imageSize, crop);
	}
	
	/**
	 * BlobKeyに該当するデータを削除する.
	 * @param bkeys BlobKey　キー（可変引数）
	 */
	public void delete(BlobKey...bkeys) {
		BlobstoreServiceFactory.getBlobstoreService().delete(bkeys);
	}
	
}
