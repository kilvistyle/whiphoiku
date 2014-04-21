package whiphoiku.controller.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slim3.controller.Navigation;
import org.slim3.controller.validator.Validators;

import whiphoiku.dao.HoikuInfoDao;
import whiphoiku.model.HoikuInfo;

import com.google.appengine.api.datastore.GeoPt;

/**
 * GetHoikuInfoController.
 * 
 * @author kilvistyle
 *
 */
public class GetHoikuInfoController extends AbstractJsonController {
    
    @Override
    public Navigation run() throws Exception {
        // validation
        if (!validate()) {
            return validatorErrToJson();
        }
        // 各種リクエストパラメータ取得
        Float lat = asFloat("lat");
        Float lng = asFloat("lng");
        Integer distance = asInteger("search_area");
        Integer age = asInteger("age");
        Integer pp = asInteger("public_private");
        // Daoを生成
        HoikuInfoDao dao = new HoikuInfoDao();
        List<HoikuInfo> hoikuInfoList = new ArrayList<HoikuInfo>();
        // 初期表示時は全件検索
        if (age == null || pp == null) {
            hoikuInfoList = dao.findAll(); // 全件
        }
        else {
            hoikuInfoList = dao.findByCondition(pp, age); // 絞り込み条件
        }
        Iterator<HoikuInfo> iteHoiku = hoikuInfoList.iterator();
        while (iteHoiku.hasNext()) {
            HoikuInfo vo = iteHoiku.next();
            GeoPt geo = vo.getGeoPt();
            // GeoPtが登録されて居ない場合は除外
            if (geo == null) {
                iteHoiku.remove();
                continue;
            }
            // 基点からの距離を求める
            double result = getDistance(lat, lng, geo.getLatitude(), geo.getLongitude(), 3);
            // 距離が指定範囲外の場合は除外
            if (distance < result) {
                iteHoiku.remove();
                continue;
            }
            // 距離を設定
            vo.setDistance(result);
            // 検索対象年齢の空き人数を設定
            switch (age) {
            case 0:
                vo.setTargetVacant(vo.getCollectZeroYear());
                break;
            case 1:
                vo.setTargetVacant(vo.getCollectOneYear());
                break;
            case 2:
                vo.setTargetVacant(vo.getCollectTwoYear());
                break;
            case 3:
                vo.setTargetVacant(vo.getCollectThreeYear());
                break;
            case 4:
                vo.setTargetVacant(vo.getCollectFourYear());
                break;
            case 5:
                vo.setTargetVacant(vo.getCollectFiveYear());
                break;
            default:
                // 年齢指定がない場合は全年齢の募集人数を加算
                vo.setTargetVacant(
                    vo.getCollectOneYear()+
                    vo.getCollectTwoYear()+
                    vo.getCollectThreeYear()+
                    vo.getCollectFourYear()+
                    vo.getCollectFiveYear()
                );
                break;
            }
        }
        return json("result", hoikuInfoList);
    }
    private boolean validate() {
        Validators v = new Validators(request);
        v.add("lat", v.required(), v.doubleType());
        v.add("lng", v.required(), v.doubleType());
        v.add("search_area", v.required(), v.integerType());
        v.add("age", v.required(), v.longRange(-1, 5));
        v.add("public_private", v.required(), v.longRange(0, 2));
        return v.validate();
    }
    /**
     * 緯度経度で表された２点間の距離をキロメートル単位で求める.
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @param precision 精度
     * @return
     */
    public static float getDistance(double lat1, double lng1, double lat2, double lng2, int precision) {
        int R = 6371; // km
        double lat = Math.toRadians(lat2 - lat1);
        double lng = Math.toRadians(lng2 - lng1);
        double A = Math.sin(lat / 2) * Math.sin(lat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lng / 2) * Math.sin(lng / 2);
        double C = 2 * Math.atan2(Math.sqrt(A), Math.sqrt(1 - A));
        double decimalNo = Math.pow(10, precision);
        double distance = R * C;
        distance = Math.round(decimalNo * distance / 1) / decimalNo;
        return (float) distance;
    }
}
