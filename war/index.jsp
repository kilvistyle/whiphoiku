<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<!DOCTYPE html>
<html lang="ja">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>ホイップ保育</title>

    <!-- Bootstrap -->
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/whiphoiku.css" rel="stylesheet">
    <link href="/style.css" rel="stylesheet">
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
  <script type="text/javascript" src="https://maps.google.com/maps/api/js?sensor=false" charset=UTF-8></script>
  <script type="text/javascript" src="/js/ext/jquery-1.11.0.min.js" charset="UTF-8"></script>
  <script type="text/javascript" src="/js/jq-global.js" charset="UTF-8"></script>
  <script type="text/javascript" src="/js/jq-geourl.js" charset="UTF-8"></script>
  <script type="text/javascript" src="/js/jq-gmaps-util.js" charset="UTF-8"></script>
  <script type="text/javascript" src="/js/hoikumap.js" charset="UTF-8"></script>
  <script src="/js/bootstrap.min.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
      $("#header").width($(window).width() - 20)
      $("#map").height(300);
    });
    $(window).resize(function() {
      $("#header").width($(window).width() - 20)
      $("#map").height(300);
    })
    function slideCustomSearch() {
      $("#custom_search").slideToggle();
    }
    function showRegistSet() {
      var left = parseInt($("#setting_regist").css("margin-left"));
      debugger
      if (left <= 0) {
        $("#setting_regist").css("margin-left", -300);
        $("#button_open_set").css("margin-left", 0);
      }
      if (left == -300) {
        $("#setting_regist").css("margin-left", 0);
        $("#button_open_set").css("margin-left", 300);
      }
    }
  </script>
  <script type="text/javascript">
    $(function() {
        // 保育マップ表示処理
        var mapObj = $('#map');
        mapObj.hoikuMap();
        // セキュア通信するパスにセキュアドメインを追加
        var mailform = $('form#reg_mail_form');
        mailform.appendDomain('${_secureDomain}');
        // 検索処理
        $('form#search_form').submit(function() {
            // メアド登録ボタンフォームをクリア
            mailform.empty();
            var address = $('#address').val();
            if (!address) {
                alert('お住まいを入力してください');
            } else {
                $('#btn_reg_mail').hide();
                // 検索条件を生成
                var cond = {
                    address : address,
                    search_area : $('#search_area').val(),
                    age : $('#age').val(),
                    public_private : $('#public_private').val()
                };
                // 検索実行
                mapObj.search(cond, showRegMailButton);
            }
            return false;
        });
        // 検索実行後にメール登録ボタンを表示
        function showRegMailButton(cond) {
            // メアド登録ボタンを表示する
            setTimeout(function(){
                mailform.empty();
                for (var key in cond) {
                    mailform.append($('<input type="hidden" name="'+key+'">').val(cond[key]));
                };
                mailform.append($('<input type="image" src="/images/btn_reg_mail.png" alt="空き情報をメールで受け取る">'));
                $('#reg_mail').slideDown(500);
                },
                2000);
        }
        
    });
  </script>
  </head>
<body>
<div class="container wh_container">
<div class="masthead">
  <h3 class="text-muted"><img alt="ホイップ保育ロゴマーク" width="370" class="img-responsive" src="/images/whiplogo.png" /></h3>
  <ul class="nav nav-justified">
    <li class="active"><a href="#">ホーム</a></li>
    <li><a href="/">空き状況検索</a></li>
    <li><a href="/nursery/">保育園一覧</a></li>
    <li><a href="#">料金計算</a></li>
    <li><a href="#">ホイップ保育？</a></li>
  </ul>
</div>

    <div id="whip_header">
      <div id="search_form_wrap">
        <form action="" method="post" name="search" accept-charset="utf-8" class="fm NiceIt" id="search_form">
          <fieldset>
            <input type="text" id="address" class="form-control" placeholder="お住まいを入力してください…"/>
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
                <option value="-1" selected="selected">指定なし</option>
                <option value="0">0歳</option>
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
    </div>
    
    <!-- /header -->
    
    <div id="search_custom" onclick="slideCustomSearch()"><i class="glyphicon glyphicon-th-list"></i><span>カスタム検索</span></div>
    <div id="reg_mail">
      <form id="reg_mail_form" action="/auth/regmail" method="post">
      </form>
    </div>
    <div id="map"></div>
    
    <div style="margin-top:30px;">
    <h3>更新情報</h3>
    <table class="table table-striped">
      <thead>
      <tr><th width="150">日付</th><th>内容</th></tr>
      </thead>
      <tr><td>2016.06.05</td><td>ほげほげ</td></tr>
      <tr><td>2016.06.05</td><td>ほげほげ</td></tr>
      <tr><td>2016.06.05</td><td>ほげほげ</td></tr>
    </table>
    </div>
    
    <!-- Site footer -->
    <div class="footer">
      <p>&copy; Company 2014</p>
    </div>
    
</div>
  </body>
</html>