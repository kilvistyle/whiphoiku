//////////////////////
// jq-shorturl.js
// @author kilvistyle
//////////////////////

// google url shortener
function toShortUrl(longUrl, successCallback, errorCallback) {
    $.ajax({
        type: 'POST',
        url: '/api/ggl', // call to root.controller.api.GglController
        data: {url:longUrl},
        dataType: 'JSON',
        timeout: 10000,
        success: function(json, dataType){
        	// 結果のチェック
            if (!handleError(json)) {
            	// 正常時のコールバック
                successCallback(json.url);
            } else {
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