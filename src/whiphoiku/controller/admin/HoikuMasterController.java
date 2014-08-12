package whiphoiku.controller.admin;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.util.BeanUtil;
import org.slim3.util.StringUtil;

import com.google.appengine.api.datastore.GeoPt;

import whiphoiku.model.HoikuInfo;
import whiphoiku.service.master.NurseryService;

public class HoikuMasterController extends Controller {

    public static final String GET_ZIPCODE = "zipcode";

    private NurseryService nurseryService = new NurseryService();

    @Override
    public Navigation run() throws Exception {

        if(isPost()){
            HoikuInfo hoikuInfo = new HoikuInfo();
            BeanUtil.copy(request, hoikuInfo);
            hoikuInfo.setGeoPt(new GeoPt(asFloat("lat"), asFloat("lng"))); //座標の設定
            NurseryService nurseryService = new NurseryService();
            nurseryService.putHoikuInfo(hoikuInfo, (String)requestScope("hoikuId"));
            // 登録後はリダイレクトする
            return redirect("hoikuMaster");
        }
        if(isGet() &&  ! StringUtil.isEmpty((String)requestScope("dataId"))){
            HoikuInfo editTarget = nurseryService.getHoikuInfoById((String)requestScope("dataId"));
            requestScope("initData", editTarget);
        }
        requestScope("hoikuList", nurseryService.getHoikuInfoAll());
        return forward("hoikuMaster.jsp");
    }
}
