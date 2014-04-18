/**
 * jq-gmaps-util.js
 * jQuery plugin for GoogleMaps.
 * 
 * Copyright 2011, kilvistyle, http://twitter.com/kilvistyle
 * 
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * 
 * Version: 1.0.0
 */

/** デフォルトのマップ中心座標 */
var LATLNG_CENTER = new google.maps.LatLng(35.659417, 139.751933);

/**
 * GoogleMapを生成する
 * @param options MapOptionsと拡張オプション
 * @return this
 * 
 * ===== options =====
 * MapOptionsをそのまま指定可(http://code.google.com/intl/en/apis/maps/documentation/javascript/reference.html#MapOptions)
 * viewonly ... Mapの操作、イベントを無効にする場合はtrueを指定する。デフォルトはfalse。
 * clickToEnableScrollWheel ... マップ上をクリックすることでスクロールホイールを有効化する場合はtrueを指定する。デフォルトはfalse。
 * 
 * ===== public methods =====
 * getMap() ... GoogleMapオブジェクトを取得する。
 * getInitialZoom() ... ズームの初期値を取得する。
 * isClickToEnableScrollWheel() ... clickToEnableScrollWheelか判定する。
 * setClickToEnableScrollWheel(boolean) ... clickToEnableScrollWheelにする場合はtrueを指定する。
 * isViewOnly() ... viewonlyか判定する。
 * setViewOnly(boolean) ... viewonlyにする場合はtrueを指定する。 
 * isMapControllable() ... 地図が操作可能な状態か判定する。（clickToEnableScrollWheelの場合で操作不可状態を判定する）
 */
$.fn.gmap = function(options) {
    var my = this;
	// MapOptions
	var defaults = {
		// default map options
        zoom : 8,
        center : LATLNG_CENTER,
        mapTypeId : google.maps.MapTypeId.ROADMAP,
        // extension options
        viewonly : false,
        clickToEnableScrollWheel : false
	};
	options = $.extend({}, defaults, options);
    // zoom初期値
    var initialZoom = options.zoom;
	// GoogleMap生成
    var map = new google.maps.Map(this.get(0), options);
	// viewonlyフラグ
    var viewonly = options.viewonly;
    // clickToEnableScrollWheelフラグ
    var clickToEnableScrollWheel = options.clickToEnableScrollWheel;
    /**
     * マップオブジェクトを取得する.
     * @return google.maps.Mapオブジェクト
     */
    this.getMap = function() {
    	return map;
    }
    /**
     * ズームの初期値を取得する.
     * @return zoom:number 
     */
    this.getInitialZoom = function() {
    	return initialZoom;
    }
    /**
     * clickToEnableScrollWheelか判定する.
     */
    this.isClickToEnableScrollWheel = function() {
    	return clickToEnableScrollWheel;
    }
    /**
     * clickToEnableScrollWheelを設定する.
     */
    this.setClickToEnableScrollWheel = function(b) {
        map.setOptions({
        	disableDefaultUI: viewonly || b,
        	scrollwheel: !viewonly && !b,
        	mapTypeControl: !viewonly && !b
        });
    	clickToEnableScrollWheel = b;
    }
    // viewonly切替処理の定義
    this.tobbleViewonlyFuncs = {};
    my.tobbleViewonlyFuncs['gmap'] = function(b){
	    	// Map操作の無効化
	    	map.setOptions({
	    		disableDefaultUI: b || my.isClickToEnableScrollWheel(),
	    		disableDoubleClickZoom: b,
	    		scrollwheel: !b && !my.isClickToEnableScrollWheel(),
	    		draggable: !b,
	    		keyboardShortcuts: !b,
	    		mapTypeControl: !b && !my.isClickToEnableScrollWheel()
	    	});
	    };
    /**
     * viewonlyか判定する.
     */
    this.isViewOnly = function() {
    	return viewonly;
    }
    /**
     * viewonlyを設定する.
     */
    this.setViewOnly = function(b) {
    	for (k in my.tobbleViewonlyFuncs) {
    		my.tobbleViewonlyFuncs[k](b);
    	}
    	viewonly = b;
    }
	// GoogleMap操作状態（true=操作状態, false=非操作状態）
	var mapControllable = !clickToEnableScrollWheel;
	/**
	 * 地図操作可能状態か判定する.
	 */
	this.isMapControllable = function() {
		return mapControllable;
	}
    // マップクリックでUIを表示、ホイールスクロールを有効化
    google.maps.event.addListener(map, 'mousedown', function(event) {
    	if (!viewonly
    		&& my.isClickToEnableScrollWheel()
    		&& !mapControllable) {
            map.setOptions({
            	disableDefaultUI: false,
            	scrollwheel: true
            });
            mapControllable = true;
    	}
    });
    // マップクリックでUIを非表示、ホイールスクロールを無効化
    google.maps.event.addListener(map, 'mouseout', function(event) {
    	// 地図の外に出た場合
    	if (!viewonly
    		&& my.isClickToEnableScrollWheel()
    		&& mapControllable
    		&& !map.getBounds().contains(event.latLng)) {
            map.setOptions({
            	disableDefaultUI: true,
            	scrollwheel: false
            });
            mapControllable = false;
    	}
    });
    // 初期処理
    my.setViewOnly(viewonly);
    my.setClickToEnableScrollWheel(clickToEnableScrollWheel);
    return this;
}

/**
 * PlacePickerMapを生成する
 * クリックした地点（latLng）を取得するGoogleMapを生成します。
 * @param options PlacePickerMapOptions オプション
 * @return this
 * 
 * ===== options =====
 * lat     ... 取得された緯度をセットするhiddenフォームのidを指定。デフォルトは'lat'。（必須）
 * lng     ... 取得された経度をセットするhiddenフォームのidを指定。デフォルトは'lng'。（必須）
 * address ... 住所検索用のtextフォームのidを指定。住所検索を行う場合に指定。デフォルトは'address'。（任意）
 * 
 * ===== callback methods =====
 * searchSuccess(latLng, result) ... 住所検索に成功した場合のコールバックメソッド。（任意）
 * searchError()         ... 住所検索に失敗した場合のコールバックメソッド。（任意）
 * 
 * ===== public methods =====
 * createPickMarker()     ... マーカー（google.maps.Marker）を作成する。オーバーライドして任意のマーカー作成処理を実装可能。（任意）
 * setAddressBy(latLng)   ... 住所検索用のtextフォームに指定ポイントの住所をセットする。
 * panTo(latLng)          ... 指定ポイントにマーカーを立て、位置情報を取得する。
 * search(address)        ... 住所から指定ポイントを検索しマーカーを立てる。検索成功で searchSuccess(latLng)　を、失敗で searchError() をコールバックする。 
 */
$.fn.placePickerMap = function(options) {
	var my = this;
	var map = my.getMap();
	if (!map) {
		// initialize gmap.
		my.gmap();
		map = my.getMap();
	}
	// Default placePickerMap Options
	var defaults = {
			lat: 'lat',
			lng: 'lng',
			address: 'address'
		};
	options = $.extend({}, defaults, options);
	var panLock = false;
	var pickedMarker = null;
	/**
	 * 住所検索成功時のコールバックメソッド.
	 * @param latLng 検索結果のポイント（google.maps.LatLng）
	 */
	this.searchSuccess = function(latLng){};
	/**
	 * 住所検索失敗時のコールバックメソッド.
	 */
	this.searchError = function(){};
	/**
	 * このマップに立てるマーカーオブジェクトを作成する.
	 * @return google.maps.Marker
	 */
	this.createPickMarker = function(){
		return new google.maps.Marker({
			animation: google.maps.Animation.DROP
		});
	};
	
	// 場所から住所情報をリフレッシュする
	this.setAddressBy = function(latLng) {
        // 場所から住所を取得
        findAddress({
        	latLng: latLng,
        	success: function(address, result) {
        		// 住所をマーカーに設定
        		pickedMarker.setTitle(address);
        		// 住所検索テキストボックスがある場合も指定
        		if (options.address) {
        			$('#'+options.address).val(address);
        		}
        	}
        });
	}
	/**
	 * 指定ポイントにマーカーを立て位置情報を取得する.
	 * @param latLng 指定されたポイント（google.maps.LatLng）
	 */
	this.panTo = function(latLng) {
    	// 以前のマーカーがある場合は削除
        if (pickedMarker) {
        	pickedMarker.setMap(null);
        	pickedMarker = null;
        }
    	if (!latLng) {
            $('#'+options.lat).val('');
            $('#'+options.lng).val('');
    		return;
    	}
    	// 指定された座標に移動
        map.panTo(latLng);
        // マーカー生成
        pickedMarker = my.createPickMarker();
        // ポイント指定
        pickedMarker.setPosition(latLng);
        // ドラッグ可否
        pickedMarker.setDraggable(!my.isViewOnly());
        // マップ上に配置
        pickedMarker.setMap(map);
        // latLngを設定
        $('#'+options.lat).val(latLng.lat());
        $('#'+options.lng).val(latLng.lng());
        
        // マーカードラッグイベントセットアップ
        google.maps.event.addListener(pickedMarker, 'mouseup', function(event) {
        	// 地図が操作可能のときにイベントを受け付ける
        	if (!my.isViewOnly()) {
                map.panTo(event.latLng);
                // この場所の住所に更新
                my.setAddressBy(event.latLng);
                // latLngを設定
                $('#'+options.lat).val(event.latLng.lat());
                $('#'+options.lng).val(event.latLng.lng());
        	}
        });
    };
	/**
	 * 住所から指定されたポイントを検索する.
	 * 検索成功した場合はそのポイントを取得し、searchSuccess をコールバックする。
	 * 検索失敗した場合はポイントをクリアし、searchError をコールバックする。
	 * @param address 住所文字列
	 */
	this.search = function(address) {
		if (panLock) return;
		panLock = true;
        findLatLng({
            address: address,
            success: function(location, result) {
                my.panTo(location);
	    		pickedMarker.setTitle(result.address_in_jp);
	    		map.fitBounds(result.geometry.viewport);
	    		// 検索成功コールバック
	    		my.searchSuccess(location, result);
	    		panLock = false;
            },
            error: function() {
                my.panTo(null);
            	// 検索エラーコールバック
            	my.searchError();
	    		panLock = false;
            }
        });
    };
    // マップクリックイベントセットアップ
    google.maps.event.addListener(map, 'click', function(event) {
    	// 地図が操作可能のときにイベントを受け付ける
    	if (!my.isViewOnly()) {
            my.panTo(event.latLng);
            // この場所の住所に更新
            my.setAddressBy(event.latLng);
    	}
    });
    // 住所検索用のフォームが指定されている場合
    if (options.address) {
    	var preInput = '';
        //  住所検索イベントセットアップ
        $('#'+options.address)
    	    .change(function(){
    	    	if (preInput == $(this).val()) return;
    	    	my.search($(this).val());
	    		preInput = $(this).val();
    	    })
    	    .keypress(function(e){
    	    	if(e.which == 13) {
        	    	if (preInput == $(this).val()) return;
    	    		my.search($(this).val());
    	    		preInput = $(this).val();
    	    		return false;
    	    	}
    	    });
    }
    // viewonly切替処理の設定
    my.tobbleViewonlyFuncs['placePicker'] = function(b){
	    	// placePicker操作の無効化
	    	if (pickedMarker) {
	    		pickedMarker.setDraggable(!b);
	    	}
	    };
    // 初期処理
    my.setViewOnly(my.isViewOnly());
    
    return this;
}

/**
 * ItemMarkerMapを生成する
 * 任意のアイテム（Object）からマーカーを作成して地図上に配置します。
 * アイテムからマーカーを作成する処理はcreateMarker(item)をオーバーライドして実装します。
 * アイテムから情報ウィンドウを作成する処理はcreateWindow(item)をオーバーライドして実装します。
 * @param options ItemMarkerMapOptions オプション
 * @return this
 * 
 * ===== options =====
 * createMarker ... アイテムからマーカーを作成する関数を指定。（必須）
 * createWindow ... アイテムから情報ウィンドウを作成する関数を指定。（任意）
 * unionWindows ... 同じ位置のマーカーを１まとめにして情報ウィンドウに表示する。デフォルオはtrue。
 * items        ... この地図に配置するアイテム配列。（任意）
 * minZoom      ... マーカー配置時にオートフィットする最小ズーム値。デフォルト0。
 * maxZoom      ... マーカー配置時にオートフィットする最大ズーム値。デフォルト12。
 * 
 * ===== public methods =====
 * isUnionWindows() ... 追加マーカーを１まとめにするか判定する。
 * setUnionWindows(boolean) ... 追加マーカーを１まとめにするかの判定をセットする。
 * getMinZoom() ... マーカー配置時にオートフィットする最小ズーム値を取得する。
 * setMinZoom(number) ... マーカー配置時にオートフィットする最小ズーム値をセットする。
 * getMaxZoom() ...　マーカー配置時にオートフィットする最大ズーム値を取得する。
 * setMaxZoom(number) ... マーカー配置時にオートフィットする最大ズーム値をセットする。
 * getMarkers() ... この地図に配置されている全てのマーカーを取得する。（addItem()、addMarker()、setItems()で追加したものに限る）
 * addMarker(marker) ... この地図にマーカーを配置する。
 * hideMarkers() ... この地図に配置されている全てのマーカーを非表示にする。
 * showMarkers() ... この地図に配置されている全ての非表示のマーカーを表示する。
 * deleteMarkers() ... この地図に配置されている全てのマーカーを削除する。
 * createMarker(item) ... アイテムからマーカーを作成する。オーバーライドして任意のマーカー作成処理を実装する。
 * createWindow(item) ... アイテムから情報ウィンドウを作成する。オーバーライドして任意の情報ウィンドウ作成処理を実装する。
 * addItem(item) ... この地図にアイテムを追加する。
 * getItems() ... この地図に配置されている全てのアイテムを取得する。
 * setItems(itemArray) ... この地図に全てのアイテムを配置する。
 * addItemsAsync(itemArray, delayMs) ... この地図に全てのアイテムを非同期で追加する。（既存のアイテムはそのままで追加します）
 * setItemsAsync(itemArray, delayMs) ... この地図に全てのアイテムを非同期で配置する。（既存のアイテムは一旦削除して配置し直します）
 */
$.fn.itemMarkerMap = function(options) {
	var my = this;
	var map = my.getMap();
	if (!map) {
		// initialize gmap.
		my.gmap();
		map = my.getMap();
	}
	// Default itemMarkerMap Options
	var defaults = {
			createMarker: function(item) {return new google.maps.Marker();},
			createWindow: function(item) {return null;},
			unionWindows: true,
			items: [],
			minZoom: 0,
			maxZoom: 12
		};
	options = $.extend({}, defaults, options);
	var unionWindows = options.unionWindows;
	var items = options.items;
	var minZoom = options.minZoom;
	var maxZoom = options.maxZoom;
    var markers = [];
    var openedMarker = null;
    var bounds = new google.maps.LatLngBounds();
    // 地点毎のマーカー
    var positionMarkers = {};
    /**
     * 追加するマーカーを１まとめにするか判定する。
     */
    this.isUnionWindows = function() {
    	return unionWindows;
    }
    /**
     * 追加するマーカーを１まとめにするかの判定をセットする.
     */
    this.setUnionWindows = function(is) {
    	unionWindows = is;
    }
    /**
     * マーカー配置時にオートフィットする最小ズーム値を取得する.
     */
    this.getMinZoom = function() {
    	return minZoom;
    }
    /**
     * マーカー配置時にオートフィットする最小ズーム値をセットする.
     */
    this.setMinZoom = function(num) {
    	minZoom = num;
    }
    /**
     * マーカー配置時にオートフィットする最大ズーム値を取得する.
     */
    this.getMaxZoom = function() {
    	return maxZoom;
    }
    /**
     * マーカー配置時にオートフィットする最大ズーム値をセットする.
     */
    this.setMaxZoom = function(num) {
    	maxZoom = num;
    }
    /**
     * オートフィットを実行する.
     * 配置されているマーカーの位置から最適なズームレベルに調整します。
     */
    this.autoFit = function() {
		// マップ操作中の場合はオートフィットしない
		if (my.isMapControllable()) return;
    	// マーカーがある場合はマーカーの位置でフィットさせる
    	if (0 < markers.length) {
        	map.fitBounds(bounds);
        	// ズームの調整
    	    setTimeout(function(){
                if (maxZoom < map.getZoom()) {
                    map.setZoom(maxZoom);
                }
                else if (minZoom > map.getZoom()) {
                	map.setZoom(minZoom);
                }
            }, 200);
    	}
    	// マーカーが一つもない場合は初期値へ
    	else {
    		map.setCenter(LATLNG_CENTER);
    		map.setZoom(my.getInitialZoom());
    	}
    }
    /**
     * この地図に配置されているマーカーを取得する.
     * @return google.maps.Markerオブジェクト配列
     */
    this.getMarkers = function() {
    	return markers;
    }
    /**
     * この地図にマーカーを配置する.
     * @param google.maps.Markerオブジェクト
     */
    this.addMarker = function(marker) {
    	// 同一箇所のピンをまとめる場合
    	if (unionWindows) {
    		var strLatLng = marker.getPosition().toString();
    		// この地点のマーカーが存在する場合
    		if (positionMarkers[strLatLng]) {
    			// マーカーを連結する
    			positionMarkers[strLatLng].union(marker);
    			// 地図に配置しないで終了
    			return;
    		}
    		// この地点のマーカーがまだ存在しない場合は保持
    		else {
    			positionMarkers[strLatLng] = marker;
    		}
    	}
    	// 地図に配置
    	marker.setMap(map);
    	markers.push(marker);
    	bounds.extend(marker.getPosition());
    }
    /**
     * この地図に配置されている全てのマーカーを非表示にする.
     */
    this.hideMarkers = function() {
    	for (var i=0; i < markers.length; i++) {
    		markers[i].setMap(null);
    	}
    }
    /**
     * この地図に配置されている全ての非表示のマーカーを表示する.
     */
    this.showMarkers = function() {
    	for (var i=0; i < markers.length; i++) {
    		if (!markers[i].getMap()) {
        		markers[i].setMap(map);
    		}
    	}
    }
    /**
     * この地図に配置されている全てのマーカーを削除する.
     */
    this.deleteMarkers = function() {
    	for (var i=0; i < markers.length; i++) {
    		markers[i].setMap(null);
    	}
    	markers.length = 0;
    	positionMarkers = {};
    	bounds = new google.maps.LatLngBounds();
    }
    /**
     * アイテムからマーカーを作成する.
     * addItem(item)で追加されたObjectからマーカーを作成します。
     * このメソッドをオーバーライドして任意のマーカーを作成します。
     * @return 新規に作成されたgoogle.maps.Markerオブジェクト
     * @see addItem
     */
    this.createMarker = options.createMarker;
    /**
     * アイテムから情報ウィンドウを作成する.
     * addItem(item)で追加されたObjectから情報ウィンドウを作成します。
     * このメソッドをオーバーライドして任意の情報ウィンドウを作成します。
     * @return 新規に作成されたgoogle.maps.InfoWindowオブジェクト
     * @see addItem
     */
    this.createWindow = options.createWindow;
    /**
     * この地図にアイテムを追加する.
     * createMarker()、createWindow()を用いてitemから
     * マーカーオブジェクトを作成して地図上に配置します。
     * @param item 任意のオブジェクト
     * @see createMarker
     * @see createWindow
     */
    this.addItem = function(item) {
    	// itemからMarkerを生成
    	var itemMarker = my.createMarker(item);
    	if (!itemMarker) return;
    	// itemからInfoWindowを生成
		var itemWindow = my.createWindow(item);
		// マーカーにInfoWindowを操作するメソッドをオーバーライド
		if (itemWindow) {
			var opened = false;
			itemMarker.openWindow = function() {
				if (openedMarker) {
					openedMarker.closeWindow();
				}
				itemWindow.open(map, itemMarker);
				openedMarker = itemMarker;
				opened = true;
			}
			itemMarker.closeWindow = function() {
				itemWindow.close();
				opened = false;
			}
			itemMarker.toggleWindow = function() {
				if (opened) {
					itemMarker.closeWindow();
				}
				else {
					itemMarker.openWindow();
				}
			}
			google.maps.event.addListener(itemMarker, 'click', function() {
				if (!my.isViewOnly()) itemMarker.toggleWindow();
            });
		}
		itemMarker.isExistWindow = function() {
			return itemWindow != null;
		}
		itemMarker.getWindow = function() {
			return itemWindow;
		}
		itemMarker.union = function(otherMarker) {
			var content = itemWindow.getContent();
			var otherContent = otherMarker.getWindow().getContent();
			// 両方のコンテンツを連結
			itemWindow.setContent(content+otherContent);
			// zIndexを加算
			itemMarker.setZIndex(itemMarker.getZIndex()+otherMarker.getZIndex());
		}
		// 作成したマーカーを地図に配置
		my.addMarker(itemMarker);
    }
    /**
     * この地図に配置されている全てのアイテムを取得する.
     * @return Ojbect配列
     */
    this.getItems = function() {
    	return items;
    }
    /**
     * この地図に全てのアイテムを追加する.
     * createMarker()、createWindow()を用いてitemから
     * マーカーオブジェクトを作成して地図上に追加します。
     * @param itemArray Object配列
     */
    this.addItems = function(itemArray) {
    	if (!itemArray) return;
    	for (var i=0; i < itemArray.length; i++) {
    		my.addItem(itemArray[i]);
    	}
    	my.autoFit();
    }
    /**
     * この地図に全てのアイテムを配置する.
     * createMarker()、createWindow()を用いてitemから
     * マーカーオブジェクトを作成して地図上に配置します。
     * @param itemArray Object配列
     */
    this.setItems = function(itemArray) {
    	my.deleteMarkers();
    	if (!itemArray) return;
    	for (var i=0; i < itemArray.length; i++) {
    		my.addItem(itemArray[i]);
    	}
    	my.autoFit();
    }
    /**
     * この地図に全てのアイテムを非同期で追加する.
     * createMarker()、createWindow()を用いてitemから
     * マーカーオブジェクトを作成して地図上に追加します。
     * @param itemArray Object配列
     * @param delayMs マーカーを配置する間隔（ミリ秒）
     */
    this.addItemsAsync = function(itemArray, delayMs) {
    	if (!itemArray) return;
    	if (!delayMs) delayMs = 100;
        if (0 < itemArray.length) {
            // アイテムがある場合は配置
            my.addItem(itemArray.shift());
            // 時間差で次の配置準備
            setTimeout(function(){
            	my.addItemsAsync(itemArray, delayMs);
            }, delayMs);
        }
        else {
            // 全て配置したらオートフィットして終了
        	my.autoFit();
        }
    }
    /**
     * この地図に全てのアイテムを非同期で配置する.
     * createMarker()、createWindow()を用いてitemから
     * マーカーオブジェクトを作成して地図上に配置します。
     * @param itemArray Object配列
     * @param delayMs マーカーを配置する間隔（ミリ秒）
     */
    this.setItemsAsync = function(itemArray, delayMs) {
    	my.deleteMarkers();
    	my.addItemsAsync(itemArray, delayMs);
    }
    // マップイベントセットアップ
	google.maps.event.addListener(map, 'click', function() {
		// マップがクリックされた場合はWindowを閉じる
		if (openedMarker) {
			openedMarker.closeWindow();
		}
    });
    // 初期処理
    my.setItems(items);
    
    return this;
}

/**
 * 緯度、経度からLatLngオブジェクトを生成する.
 * @param strLat 緯度（String）
 * @param strLng　経度（String）
 * @return google.maps.LatLngオブジェクト または null（緯度経度のいずれかが不正な場合）
 */
function toLatLng(strLat, strLng) {
    var lat = parseFloat(strLat);
    var lng = parseFloat(strLng);
    if (!isNaN(lat) && !isNaN(lng)) {
        return new google.maps.LatLng(lat, lng);
    }
    else {
    	return null;
    }
}

/**
 * 住所から地図地点（LatLng）を検索する.
 * @param s options
 * 
 * ===== options =====
 * address ... 住所や場所を表す文字列。（必須）
 * 
 * ===== callback options =====
 * success ... 検索成功時のコールバック関数。function(latLng, result)としてオーバーライドすること。（必須）
 * error   ... 検索失敗時のコールバック関数。引数なし関数としてオーバーライドすること。（任意）
 * 
 */
var geocoder;
function findLatLng(s) {
    if (!s) {
        if (s.error) s.error('the parameter is required.');
        return false;
    }
    if (!s.address) {
        if (s.error) s.error('the address is required.');
        return false;
    }
    if (!s.success) {
        if (s.error) s.error('the success callback is required.');
        return false;
    }
    if (!geocoder) {
        geocoder = new google.maps.Geocoder();
    }
    geocoder.geocode({'address':s.address}, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
			// 「日本, 」を除去した住所をaddress_in_jpプロパティに追加
			results[0].address_in_jp =
				results[0].formatted_address.replace('日本, ','');
            s.success(results[0].geometry.location, results[0]);
        } else {
            if (s.error) s.error('Geocode was not successful for the following reason: ' + status);
        }
    });
}
/**
 * 地図地点(LatLng)から住所を取得する.
 * @param s options
 * 
 * ===== options =====
 * latLng  ... 地図地点(google.maps.LatLng)（必須）
 * 
 * ===== callback options =====
 * success ... 検索成功時のコールバック関数。function(address, result)としてオーバーライドすること。（必須）
 * error   ... 検索失敗時のコールバック関数。引数なし関数としてオーバーライドすること。（任意）
 */
function findAddress(s) {
    if (!s) {
        if (s.error) s.error('the parameter is required.');
        return false;
    }
    if (!s.latLng) {
        if (s.error) s.error('the latLng is required.');
        return false;
    }
    if (!s.success) {
        if (s.error) s.error('the success callback is required.');
        return false;
    }
    if (!geocoder) {
        geocoder = new google.maps.Geocoder();
    }
    geocoder.geocode({'latLng':s.latLng}, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
        	for (var i=0; i < results.length; i++) {
            	for (var j=0; j < results[i].types.length; j++) {
                	// 検索結果から住所のものを返却
        			if ('political' == results[i].types[j]) {
        				// 「日本, 」を除去した住所をaddress_in_jpプロパティに追加
        				results[i].address_in_jp =
        					results[i].formatted_address.replace('日本, ','');
        				s.success(results[i].address_in_jp, results[i]);
        				return;
        			}
        		}
        	}
            if (s.error) s.error('Geocode was not successful for not found political type.');
        } else {
            if (s.error) s.error('Geocode was not successful for the following reason: ' + status);
        }
    });
}
