package whiphoiku.service.master;

import java.util.ArrayList;
import java.util.List;

import jp.co.casleyconsulting.www.nurseryVacancy.constants.ExtractType;
import jp.co.casleyconsulting.www.nurseryVacancy.dto.NurseryVacancyInfo;
import jp.co.casleyconsulting.www.nurseryVacancy.factory.VacancyExtractorFactory;

import org.slim3.util.StringUtil;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import whiphoiku.dao.HoikuInfoDao;
import whiphoiku.model.HoikuInfo;

/**
 * SendMailService.
 * 
 * @author mmike
 *
 */
public class NurseryService {

    private HoikuInfoDao hoikuInfoDao = new HoikuInfoDao();

    public HoikuInfo getHoikuInfoById(String id){
        Key key = KeyFactory.createKey("HoikuInfo", Long.parseLong(id) );
        return hoikuInfoDao.get(key);
    }

    public List<HoikuInfo> getHoikuInfoAll(){
        return this.hoikuInfoDao.findAll();
    }

    public List<HoikuInfo> getHoikuInfoByZipCode(String zipcode){
        return this.hoikuInfoDao.findByZipCode(zipcode);
    }

    public void putHoikuInfo(HoikuInfo hoikuInfo, String id){
        if(! StringUtil.isEmpty(id)){
            Key key = KeyFactory.createKey("HoikuInfo", Long.parseLong(id) );
            hoikuInfo.setKey(key);
        }
        this.hoikuInfoDao.put(hoikuInfo);
    }

    public void delHoikuInfo(String id){
        Key delKey = KeyFactory.createKey("HoikuInfo", Long.parseLong(id) );
        this.hoikuInfoDao.delete(delKey);
    }

    /**
     * @param extractType
     */
    public void setNurseryVacancyInfo(){

        List<NurseryVacancyInfo> extracted = new ArrayList<NurseryVacancyInfo>();
        extracted.addAll(VacancyExtractorFactory.create(ExtractType.MEGURO).extract("http://www.city.meguro.tokyo.jp/kurashi/kosodate/hoikuen/akijokyo.html"));
        extracted.addAll(VacancyExtractorFactory.create(ExtractType.MINATO).extract("http://www.city.minato.tokyo.jp/kodomo/kodomo/kodomo/hoikuen/aki.html"));
        extracted.addAll(VacancyExtractorFactory.create(ExtractType.TAITO).extract("http://www.city.taito.lg.jp/index/kurashi/kosodate/hoikutakuji/hoikuen/hoikuennyuen/25-4ninnzuu.html"));
        extracted.addAll(VacancyExtractorFactory.create(ExtractType.NAKANO).extract("http://www.city.tokyo-nakano.lg.jp/dept/244000/d001477.html"));
        extracted.addAll(VacancyExtractorFactory.create(ExtractType.SETAGAYA).extract("http://www.city.setagaya.lg.jp/kurashi/103/129/458/2107/"));

        List<HoikuInfo> hoikuInfoList = new ArrayList<HoikuInfo>();

        for (NurseryVacancyInfo nurseryVacancyInfo : extracted) {
            List<HoikuInfo> hoikuInfoCache = hoikuInfoDao.findByName(nurseryVacancyInfo.name);
            HoikuInfo hoikuInfo = (hoikuInfoCache == null || hoikuInfoCache.size() == 0) ? new HoikuInfo(): hoikuInfoCache.get(0);
            hoikuInfo.setExtractType(nurseryVacancyInfo.extractType);
            hoikuInfo.setName(nurseryVacancyInfo.name);
            hoikuInfo.setCollectZeroYear(numeric (nurseryVacancyInfo.zeroCnt));
            hoikuInfo.setCollectOneYear(numeric(nurseryVacancyInfo.firstCnt));
            hoikuInfo.setCollectTwoYear(numeric(nurseryVacancyInfo.secondCnt));
            hoikuInfo.setCollectThreeYear(numeric(nurseryVacancyInfo.thirdCnt));
            hoikuInfo.setCollectFourYear(numeric(nurseryVacancyInfo.fourthCnt));
            hoikuInfo.setCollectFiveYear(numeric(nurseryVacancyInfo.fifthCnt));
            hoikuInfoList.add(hoikuInfo);
        }

        hoikuInfoDao.put(hoikuInfoList);
    }

    public static Integer numeric (String val) {
        Integer ret = 0;
        try{
            ret = Integer.parseInt(val);
        }catch(Exception e){
            ret = null;
        }
        return ret;
    }
}
