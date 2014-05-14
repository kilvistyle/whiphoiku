//////////////////////
// jq-global.js
// @author kilvistyle
//////////////////////

// バリデーションエラー時にDOMに追加するClass名
// handleErrorにてバリデーションエラー時に利用
var errorClass = 'err';
// 文字列に含まれるURLから<a …/>を生成した際に埋め込むClass名
// $.convLinkText()にて利用
var linkClass = 'linktext';
// アプリケーションプロパティ情報
var aplProps = null;

//jqueryに共通処理追加
(function($){
	// JSONパース処理（IE6,7ではJSON.parseが利用できないため）
	$.parseJSON = function(jsonString) {
		// 文字列ではない場合はそのまま返却
		if (!isString(jsonString)) {
			return jsonString;
		}
	    try {
	        // まずはJSON.parseを試行
	        return JSON.parse(jsonString);
	    }
	    // JSON.parseをサポートしていないブラウザの場合（IE6,7など）
	    catch (e) {
	        // evalでJSONを評価
	        return eval("("+jsonString+")");
	    }
	};
    // HTMLエスケープ処理
    $.esc = function(val) {
        val = val.replace(/&/g,"&amp;");
        val = val.replace(/"/g,"&quot;");
        val = val.replace(/'/g,"&#039;");
        val = val.replace(/</g,"&lt;");
        val = val.replace(/>/g,"&gt;");
        return val;
    };
    // ハッシュオブジェクトの各パラメータの値をHTMLエスケープ＆改行変換した新しいハッシュオブジェクトに変換処理
    $.escHash = function(obj) {
        var hash = {};
        for (var name in obj) {
            var val = obj[name];
            if (isString(val)) {
                hash[name] = $.convBR($.esc(val));
            }
            else {
                hash[name] = val;
            }
        }
        return hash;
    }
    // 改行(BR)変換処理
    $.convBR = function(val) {
        val = val.replace(/\r\n/g, '<br/>');
        val = val.replace(/(\n|\r)/g, '<br/>');
        return val;
    };
    // テキスト内のURLをリンク付きに変換処理
    $.convLinkText = function(text, brLength) {
        if (!text) text = '';
        text = text.replace(/<br>|<br\/>|<BR>|<BR\/>/g,'\n');
        text = text.replace(/((?:https?|ftp):\/\/[^\s\n 　]+)/g,'<a href="$1" target="_blank" class="'+linkClass+'">$1</a>');
        // リンクテキスト内での折り返しが指定されている場合は指定lengthで改行を入れる
        if (brLength) {
            $('a.'+linkClass).each(function(){
                var replaced = '';
                var origin = $(this).text();
                while (brLength < origin.length) {
                    replaced += origin.substring(0, brLength) +'\n';
                    origin = origin.substring(brLength);
                }
                replaced += origin;
                $(this).text(replaced);
            });
        }
        text = $.convBR(text);
        return text;
    }
    $.fn.convLinkText = function(brLength) {
        // 要素セットの内容を順にURL→リンク生成処理
        return this.each(function() {
            var text = $(this);
            text.html($.convLinkText(text.html(), brLength));
        });
    }
    // インプットのdisable要素のトグルスイッチ
    $.fn.toggleDisable = function(disable) {
        return this.each(function(){
            if (disable) {
                $(this).attr("disabled", "disabled");
            }
            else {
                $(this).removeAttr("disabled");
            }
        });
    }
    // Domain appender
    $.fn.appendDomain = function(domain) {
    	return this.each(function(){
    		var domObj = $(this);
    		var tagName = domObj.get(0).tagName;
    		// aタグ
    		if (tagName.toLowerCase() == 'a') {
    			var path = domObj.attr('href');
    			if (isEmpty(path) || startsWith(path,'http')) {
    				return;
    			}
    			if (startsWith(path,'/')) {
    				domObj.attr('href',domain+path);
    			}
    			else {
    				// TODO 相対パス指定時のURL書き換えは後ほど対応
    			}
    		}
    		// formタグ
    		else if (tagName.toLowerCase() == 'form') {
    			var path = domObj.attr('action');
    			if (isEmpty(path) || startsWith(path,'http')) {
    				return;
    			}
    			if (startsWith(path,'/')) {
    				domObj.attr('action',domain+path);
    			}
    			else {
    				// TODO 相対パス指定時のURL書き換えは後ほど対応
    			}
    		}
    	});
    }
    
    // ieのconsole未対応対策
    if (typeof window.console === "undefined") window.console = {};
    if (typeof window.console.log !== "function") window.console.log = function(){};
})(jQuery);

function getAplProps(successCallback, errorCallback) {
	// すでにアプリケーションプロパティを保持している場合はそのまま正常コールバック
	if (aplProps) {
    	// 正常コールバック
    	if (successCallback) successCallback(aplProps);
	}
	else {
	    // アプリケーションプロパティ情報を取得
	    $.ajax({
	        type: 'GET',
	        url: '/api/getAplProps',
	        dataType: 'JSON',
	        cache: false,
	        timeout: 30000,
	        success: function(json){
	            if (!handleError(json)) {
	            	// アプリケーションプロパティに保持
	            	aplProps = json;
	            	// 正常コールバック
	            	if (successCallback) successCallback(aplProps);
	            }
	            else {
	            	console.log('情報の取得に失敗しました：'+$.esc(json.errMsg));
	            	// 異常コールバック
	            	if (errorCallback) errorCallback(json);
	            }
	        },
	        error: function() {
	        	console.log('タイムアウトしました：');
	        	// 異常コールバック
	        	if (errorCallback) errorCallback({errCode:'ERR_DS_UNKNOWN', errMsg:'タイムアウトしました'});
	        }
	    });
	}
}

// handle error
function handleError(json) {
	if (!json) {
		alert('サーバーとの通信に失敗しました');
		return true;
	}
	if (json.errCode) {
		var code = json.errCode;
		// validator error
		if ('ERR_VALIDATORS' == code) {
			var errs = json.errors;
			for (var i=0; i<errs.length; i++) {
				console.log('入力エラー('+(i+1)+')： '
					+'name='+$.esc(errs[i].name)+', msg='+$.esc(errs[i].message));
			}
		}
		// invalid request
		if ('ERR_INVALID_REQUEST' == code) console.log(json.errMsg);
		// server error message
		if ('ERR_DS_LEADONRY' == code) console.log('サーバメンテナンス中です。データを登録することができません。');
		if ('ERR_DS_UNKNOWN' == code) console.log('サーバでエラーが発生しました。 詳細：'+json.errMsg);
		if ('ERR_URLFETCH_IO' == code) console.log(json.errMsg);
		return true;
	}
	return false;
}

function getUrl() {
	var href = window.location.href;
	if (0 < href.indexOf('#')) {
    	return href.slice(0, href.indexOf('#'));
	} else {
    	return href;
	}
}
function getUrlQueryString() {
	var url = getUrl();
	return url.slice(url.indexOf('?')+1);
}
function getUrlQueryHash() {
    var vars = [], hash;
    var hashes = getUrlQueryString().split('&');
    for(var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}
function getUrlBasePath() {
	var url = getUrl();
	return url.slice(0, url.lastIndexOf('/')+1);
}

/**
 * 引数のオブジェクトが文字列かどうか判定する.
 * @param obj
 * @return
 */
function isString(obj) {
    return ( typeof(obj) == "string" || obj instanceof String ) ;
}

function isEmpty(text) {
    return !text || text == '';
}

function isNumber(text) {
	if (isEmpty(text)) return false;
	var num = text-0;
	return !isNaN(num);
}
function startsWith(text, target) {
	return text.lastIndexOf(target, 0)==0;
}

/**
 * ブラウザ幅を取得する.
 * @return ブラウザの幅
 */
function getBrowserWidth() {
    if ( window.innerWidth ) {
            return window.innerWidth;
    }
    else if ( document.documentElement && document.documentElement.clientWidth != 0 ) {
            return document.documentElement.clientWidth;
    }
    else if ( document.body ) {
            return document.body.clientWidth;
    }
    return 0;
}
/**
 * ブラウザ高さを取得する.
 * @return ブラウザの高さ
 */
function getBrowserHeight() {
    if ( window.innerHeight ) {
            return window.innerHeight;
    }
    else if ( document.documentElement && document.documentElement.clientHeight != 0 ) {
            return document.documentElement.clientHeight;
    }
    else if ( document.body ) {
            return document.body.clientHeight;
    }
    return 0;
}

