/**
 * hoikuMap.js
 * @author kilvistyle
 */
$.fn.hoikuMap = function(s) {
	// 家マーカー
	var homeMarker = null;
	// オプションパラメータの初期値指定
	var defaults = {
		mode: 'get', // get|valid|latest|all
		id : null,   // if mode value is 'get', it required.
		admin: false, // admin mode.
		photo: false
	};
	// オプションパラメータの取得
	s = $.extend({}, defaults, s);
	// 保育園マーカー生成処理の定義
	var createHoikuInfoMarker = function(markerInfo) {
			// 地点が指定されていない場合はマーカーを立てない
			if (!markerInfo.lat || !markerInfo.lng) {
				return null;
			}
			// 家マーカーの場合
			if (markerInfo.home) {
				// 家マーカーを生成
		        return new google.maps.Marker({
		            icon: 'images/homepin.png',
		            position: toLatLng(markerInfo.lat, markerInfo.lng),
		            title: $.esc(markerInfo.address),
		            animation: google.maps.Animation.DROP,
		            zIndex: 3
		        });
			}
			// 保育園マーカーの場合
			else {
				// 保育園マーカーを生成
		        return new google.maps.Marker({
		            icon: 'images/hoippin.png',
		            position: toLatLng(markerInfo.lat, markerInfo.lng),
		            title: $.esc(markerInfo.address),
		            animation: google.maps.Animation.DROP,
		            zIndex: 2
		        });
			}
	    };
	// マーカーウィンドウ生成処理の定義
	var createHoikuInfoWindow = function(markerInfo) {
		// 家マーカーの場合
		if (markerInfo.home) {
			// 吹き出しウィンドウを生成
	        return new google.maps.InfoWindow({
	        	// TODO 吹き出しの見栄え再デザイン必要
	        	content: '<div class="section_marker_window" style="width:200px; height:30px;">'
	        		+'<p>'+$.esc(markerInfo.address)+'</p></div>'
	        });
		}
		// 保育園マーカーの場合
		else {
			// 私立／公立ラベル
			var schoolKubunLabel = markerInfo.schoolKubun == '1'?'公立':'私立';
			// 吹き出しウィンドウを生成
	        return new google.maps.InfoWindow({
	        	// TODO 吹き出しの見栄え再デザイン必要
	        	content: '<div class="section_marker_window" style="width:300px; height:80px;">'
	        		+'<p>'+$.esc(markerInfo.name)+' （'+schoolKubunLabel+'）</p>'
	        		+'空き人数：'+markerInfo.targetVacant+'<br>'
	        		+'住所：'+$.esc(markerInfo.address)+'<br>'
	        		//+'距離：'+markerInfo.distance+'
	        		+'</div>'
	        });
		}
    }
	// マップを生成
    var map = 
    	$(this).gmap({
	    	zoom: 10,                       // デフォルトのズーム値
	    	clickToEnableScrollWheel:true  // クリックでスクロールホイールを有効化
	    })
	    .itemMarkerMap({
	        createMarker: createHoikuInfoMarker, // Marker生成処理
		    createWindow: createHoikuInfoWindow, // InfoWindow生成処理
		    maxZoom : 18                         // 最大ズーム率
	    });
    
    // 地点を基点にした周辺検索
    this.search = function(cond) {
    	// 検索条件の初期値指定
    	var defaults = {
    		address : null,
    		lat : null,
    		lng : null,
    		search_area : 1000,    // 範囲1km
    		age : 0,            // 0歳の空き状況
    		public_private : 0  // 私立／公立両方
    	};
    	// 検索条件のオーバーライド
    	cond = $.extend({}, defaults, cond);
    	// 検索条件のチェック
    	if (isEmpty(cond.address)) {
    		alert('住所が指定されていません。');
    		return;
    	}
    	// 検索条件の住所からGeoPtを求めて検索条件に設定する処理
    	// cond.address から cond.lat , cond.lng を求める
    	var toGeoPt = function(cond, successCallback, errorCallback) {
        	// GeoPt変換が必要な場合は変換
        	if (!cond.lat || !cond.lng) {
        		// url shortener api call
        		toGeocode(cond.address,
        			// success callback
        			function(geocodeinfo) {
    	    			// 保育マップ表示処理
    	    			cond.lat = geocodeinfo[0].geometry.location.lat;
    	    			cond.lng = geocodeinfo[0].geometry.location.lng;
    	    			// 成功処理をコール
    	    			successCallback(cond);
        			},
        			// error callback
        			function (err) {
        	    		console.log('住所変換に失敗しました.'+$.esc(err.errMsg));
        	    		if (errorCallback) errorCallback(err);
        	    		return;
        			});
        	} 
        	else {
    			// 変換不要なのでそのま成功処理をコール
    			successCallback(cond);
        	}
    	}
    	// 検索条件のGeoPtから周辺の保育園を検索しマップにポイントする処理
        var findHoikuInfoByGeoPt = function(cond) {
        	// ホームマーカーを配置
        	map.setItems([{
        			home:true,
        			lat:cond.lat,
        			lng:cond.lng,
        			address: cond.address
        		}]);
        	
            // 保育園情報を取得
            $.ajax({
                type: 'GET',
                url: '/api/getHoikuInfo',
                data: {
                	lat: cond.lat,
                	lng: cond.lng,
                	search_area : cond.search_area,
                	age: cond.age,
                	public_private: cond.public_private 
                	},
                dataType: 'JSON',
                cache: false,
                timeout: 30000,
                success: function(json){
            	    // 保育園情報をマーカーとして追加で配置
                    if (!handleError(json)) {
                    	// 結果をマップに出力
                    	map.addItems(json.result);
                    }
                    else {
                    	console.log('情報の取得に失敗しました：'+$.esc(json.errMsg));
                    	alert('情報の取得に失敗しました：'+$.esc(json.errMsg));
                    }
                }
            });
        }
        
        // 検索処理を実行
        toGeoPt(
        	// 検索条件
        	cond,
        	// 住所変換に成功後の検索処理
        	// GeoPtから保育園情報を検索する処理をコール
        	findHoikuInfoByGeoPt,
        	// 住所変換に失敗した場合
        	function(err){
        		alert('住所の特定に失敗しました：'+$.esc(err.errMsg));
        });
    }
    
}

function convertURL(url) {
	return url.replace(/\\/g, '/');
}