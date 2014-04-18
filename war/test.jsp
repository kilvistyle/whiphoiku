<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Index</title>
<script type="text/javascript" src="https://maps.google.com/maps/api/js?sensor=false" charset=UTF-8></script>
<script type="text/javascript" src="/js/ext/jquery-1.11.0.min.js" charset="UTF-8"></script>
<script type="text/javascript" src="/js/jq-global.js" charset="UTF-8"></script>
<script type="text/javascript" src="/js/jq-shorturl.js" charset="UTF-8"></script>
<script type="text/javascript" src="/js/jq-geourl.js" charset="UTF-8"></script>
<script type="text/javascript" src="/js/jq-gmaps-util.js" charset="UTF-8"></script>
<script type="text/javascript" src="/js/hoikumap.js" charset="UTF-8"></script>
  <script type="text/javascript">
  <!--
    $(function() {
        // 保育マップ表示処理
        var mapObj = $('#map');
        mapObj.hoikuMap({
            mode : 'valid',
            photo : true
        });

        $('form').submit(function() {
            var address = $('#address').val();
            if (!address) {
                alert('住所が入力されていません');
            } else {
                // 検索条件を生成
                var cond = {
                    address : address,
                    search_area : $('#search_area').val(),
                    age : $('#age').val(),
                    public_private : $('#public_private').val()
                };
                // 検索実行
                mapObj.search(cond);
            }
            return false;
        });
    });
  // -->
  </script>
</head>
<body>
<h1>Hello Casley Hackathon !!!</h1>

<h3>URL Shortener API Test.</h3>
<div style="margin-bottom:47px;">
	<form id="url_form" method="post">
		<input placeholder="http://www.casley-hackathon.com..." id="url_text"　type="text">
		<button id="url_submit" type="submit">Go</button>
	</form>
</div>

<h3>Google Geocoding API Test.</h3>
<div>
    <form  method="post" name="search" accept-charset="utf-8" class="fm NiceIt">
      <fieldset>
        <input type="text" id="address" class="form-control" placeholder="東京都台東区" />
      </fieldset>
      <fieldset id="custom_search">
        <div class="select_form">
          <label for="search_area">検索範囲</label>
          <select name="search_area" id="search_area" class="form-control">
            <option value="1" selected="selected">1km</option>
            <option value="3">3km</option>
            <option value="5">5km</option>
            <option value="10">10km</option>
            <option value="15">15km</option>
            <option value="20">20km</option>
          </select>
        </div>
        <div class="select_form">
          <label for="age">年齢</label>
          <select name="age" id="age" class="form-control">
            <option value="0" selected="selected">0歳</option>
            <option value="1">1歳</option>
            <option value="2">2歳</option>
            <option value="3">3歳</option>
            <option value="4">4歳</option>
            <option value="5">5歳</option>
          </select>
      </div>
        <div class="select_form" class="form-control">
          <label for="public_private">公立／私立</label>
          <select name="public_private" id="public_private" class="form-control">
            <option value="0" selected="selected">指定無し</option>
            <option value="1">公立</option>
            <option value="2">私立</option>
          </select>
        </div>
      </fieldset>
      <button type="submit" class="btn btn-default" id="submit_button">
        <strong>
          <span>検索</span>
        </strong>
      </button>
    </form>
</div>
<div id="georesult">
</div>

<div id="map" style="height: 1000pt;"></div>
</body>
</html>
