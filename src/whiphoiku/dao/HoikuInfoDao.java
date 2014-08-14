package whiphoiku.dao;

import java.util.List;

import jp.co.casleyconsulting.www.nurseryVacancy.constants.ExtractType;

import org.slim3.datastore.DaoBase;
import org.slim3.datastore.ModelQuery;

import whiphoiku.meta.HoikuInfoMeta;
import whiphoiku.model.HoikuInfo;

public class HoikuInfoDao extends DaoBase<HoikuInfo>{
    
    private static final HoikuInfoMeta meta = HoikuInfoMeta.get();
    
    public List<HoikuInfo> findAll() {
        return super.query().asList();
    }

    /**
     * @param zipcode
     * @return
     */
    public List<HoikuInfo> findByZipCode(String zipcode){
        return super.query()
                .filter(meta.zipcode.equal(zipcode))
                .asList();
    }

    /**
     * @param zipcode
     * @return
     */
    public List<HoikuInfo> findByName(String name){
        return super.query()
                .filter(meta.name.equal(name))
                .asList();
    }

    /**
     * @param extractType
     * @return
     */
    public List<HoikuInfo> findByNameExtractType(ExtractType extractType){
        return super.query()
                .filter(meta.extractType.equal(extractType))
                .asList();
    }

    /**
     * @param name
     * @param area
     * @return
     */
    public List<HoikuInfo> findByNameAndArea(String name, String area){
        return super.query()
                .filter(meta.address.startsWith(area))
                .filter(meta.name.equal(name))
                .asList();
    }

    public List<HoikuInfo> findByCondition(int schoolKubun, int age) {
        // クエリ生成
        ModelQuery<HoikuInfo> query = super.query();
        // 私立／公立区分の絞り込み条件を追加
        if (schoolKubun != 0) {
            query = query.filter(meta.schoolKubun.equal(schoolKubun));
        }
        // 空き年齢の絞り込み条件を追加
        switch (age) {
            case 0:
                query = query.filter(meta.collectZeroYear.greaterThan(0));
                break;
            case 1:
                query = query.filter(meta.collectOneYear.greaterThan(0));
                break;
            case 2:
                query = query.filter(meta.collectTwoYear.greaterThan(0));
                break;
            case 3:
                query = query.filter(meta.collectThreeYear.greaterThan(0));
                break;
            case 4:
                query = query.filter(meta.collectFourYear.greaterThan(0));
                break;
            case 5:
                query = query.filter(meta.collectFiveYear.greaterThan(0));
                break;
            default:
                break;
        }
        // 検索結果をリストで取得
        return query.asList();
    }
}
