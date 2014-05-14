package whiphoiku.util;

/**
 * ResourceConstants.
 * 
 * @author kilvistyle
 *
 */
public class ResourceConstants {
    private ResourceConstants() {
    }
    
    private static final String FS = StrUtil.FS;
    
    /** イメージディレクトリ（/images） */
    public static final String IMAGES_DIR = FS+"images"+FS;
    
    /** テンプレートディレクトリ（/templates） */
    public static final String TEMPLATE_DIR = FS+"ftls"+FS;
    /** メアド認証用メールテンプレート */
    public static final String FTL_REGMAIL = TEMPLATE_DIR + "regmail.ftl";
}
