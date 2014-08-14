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

    /**
     * @param extractType
     * @return
     */
    public List<HoikuInfo> findByNameExtractType(String extractType){
        ExtractType[] extractTypeArray = ExtractType.values();
        ExtractType searchExtractType = null;
        for (ExtractType checkExtractType : extractTypeArray) {
            if(checkExtractType.name().toString().equals(extractType)){
                searchExtractType = checkExtractType;
                break;
            }
        }
        return hoikuInfoDao.findByNameExtractType(searchExtractType);
    }
    /**
     * @param extractType
     * @return
     */
    public List<HoikuInfo> findByNameExtractType(ExtractType extractType){
        return hoikuInfoDao.findByNameExtractType(extractType);
    }

    public List<HoikuInfo> getHoikuInfoAll(){
        return this.hoikuInfoDao.findAll();
    }

    public List<HoikuInfo> getHoikuInfoByZipCode(String zipcode){
        return this.hoikuInfoDao.findByZipCode(zipcode);
    }

    public void putHoikuInfo(HoikuInfo hoikuInfo, String id){
        HoikuInfo putHoikuInfo = hoikuInfo;
        if(! StringUtil.isEmpty(id)) {
            Key key = KeyFactory.createKey("HoikuInfo", Long.parseLong(id) );
            putHoikuInfo = this.hoikuInfoDao.get(key);
            putHoikuInfo.setName(hoikuInfo.getName());
            putHoikuInfo.setZipcode(hoikuInfo.getZipcode());
            putHoikuInfo.setAddress(hoikuInfo.getAddress());
            putHoikuInfo.setTellNo(hoikuInfo.getTellNo());
            putHoikuInfo.setOfficialUrl(hoikuInfo.getOfficialUrl());
            putHoikuInfo.setSchoolKubun(hoikuInfo.getSchoolKubun());
            putHoikuInfo.setCollectZeroYear(hoikuInfo.getCollectZeroYear());
            putHoikuInfo.setCollectOneYear(hoikuInfo.getCollectOneYear());
            putHoikuInfo.setCollectTwoYear(hoikuInfo.getCollectTwoYear());
            putHoikuInfo.setCollectThreeYear(hoikuInfo.getCollectThreeYear());
            putHoikuInfo.setCollectFourYear(hoikuInfo.getCollectFourYear());
            putHoikuInfo.setCollectFiveYear(hoikuInfo.getCollectFiveYear());
            putHoikuInfo.setRemarks(hoikuInfo.getRemarks());
        }
        this.hoikuInfoDao.put(putHoikuInfo);
    }

    public void delHoikuInfo(String id){
        Key delKey = KeyFactory.createKey("HoikuInfo", Long.parseLong(id) );
        this.hoikuInfoDao.delete(delKey);
    }

    /**
     * lastest nursery vacancy infomation
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
            List<HoikuInfo> hoikuInfoCache = hoikuInfoDao.findByNameAndArea(
                    nurseryVacancyInfo.name
                    , nurseryVacancyInfo.extractType.getAddressPrx());

            HoikuInfo hoikuInfo = (hoikuInfoCache == null || hoikuInfoCache.size() == 0) ? new HoikuInfo(): hoikuInfoCache.get(0);

            // set nursery infomation
            hoikuInfo.setExtractType(nurseryVacancyInfo.extractType);
            hoikuInfo.setName(nurseryVacancyInfo.name);
            if(StringUtil.isEmpty(hoikuInfo.getAddress()) ){
                hoikuInfo.setAddress(nurseryVacancyInfo.extractType.getAddressPrx());
            }

            // set vacancy infomation
            hoikuInfo.setCollectZeroYear(collectNum (nurseryVacancyInfo.zeroCnt));
            hoikuInfo.setCollectOneYear(collectNum(nurseryVacancyInfo.firstCnt));
            hoikuInfo.setCollectTwoYear(collectNum(nurseryVacancyInfo.secondCnt));
            hoikuInfo.setCollectThreeYear(collectNum(nurseryVacancyInfo.thirdCnt));
            hoikuInfo.setCollectFourYear(collectNum(nurseryVacancyInfo.fourthCnt));
            hoikuInfo.setCollectFiveYear(collectNum(nurseryVacancyInfo.fifthCnt));

            // add register list
            hoikuInfoList.add(hoikuInfo);
        }
        hoikuInfoDao.put(hoikuInfoList);
    }

    public static Integer collectNum (String val) {
        Integer ret = 0;
        try{
            ret = Integer.parseInt(val);
        }catch(Exception e){
            ret = null;
        }
        return ret;
    }
}
