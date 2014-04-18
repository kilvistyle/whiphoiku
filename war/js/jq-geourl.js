function toGeocode(address, successCallback, errorCallback) {
    $.ajax({
        type: 'POST',
        url: 'http://maps.google.com/maps/api/geocode/json?address=' + address + '&sensor=false', // call to root.controller.api.GglController
        dataType: 'JSON',
        timeout: 10000,
        success: function(json, dataType){
        	// 結果のチェック
            if (!handleError(json)) {
            	// 正常時のコールバック
                successCallback(json.results);
            } else {
            	alert("false!!!");
            	// エラー時のコールバック
                if (errorCallback) errorCallback(json);
            }
        },
        error: function() {
            // タイムアウト時のコールバック
            if (errorCallback) errorCallback({errMsg:'timeout.'});
        }
    });
}