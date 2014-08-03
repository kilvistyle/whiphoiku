package whiphoiku.service.master;

import java.util.List;

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
}
