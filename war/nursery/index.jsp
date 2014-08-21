<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html lang="ja">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>ホイップ保育 - メール登録 - </title>

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
  <script type="text/javascript" src="/js/ext/jquery-1.11.0.min.js" charset="UTF-8"></script>
  <script type="text/javascript" src="/js/jq-global.js" charset="UTF-8"></script>
  <script type="text/javascript" src="/js/bootstrap.min.js"></script>
  
  <style type="text/css">
<!--
.district {margin-right:30px;}
-->
</style>

  </head>
<body>
	<div class="container wh_container">
		<div class="masthead">
		  <h3 class="text-muted"><img alt="ホイップ保育ロゴマーク" width="370" class="img-responsive" src="/images/whiplogo.png" /></h3>
		  <ul class="nav nav-justified">
		    <li class="active"><a href="#">ホーム</a></li>
		    <li><a href="#">空き状況検索</a></li>
		    <li><a href="#">保育園一覧</a></li>
		    <li><a href="#">料金計算</a></li>
		    <li><a href="#">ホイップ保育？</a></li>
		  </ul>
		</div>
    
	    <div>
			<div class="page-header">
				<h1>保育園一覧</h1>
			</div>
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">都心地域</h3>
				</div>
				<div class="panel-body">
					<span class="district"><a href="/nursery/list?ward=chiyoda">千代田区</a></span><span class="district"><a href="#">中央区</a></span><span class="district"><a href="#">港区</a></span>
				</div>
			</div>
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">副都心地域</h3>
				</div>
				<div class="panel-body">
					<span class="district"><a href="#">新宿区</a></span><span class="district"><a href="#">文京区</a></span><span class="district"><a href="#">渋谷区</a></span><span class="district"><a href="#">豊島区</a></span>
				</div>
			</div>
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">城東地域</h3>
				</div>
				<div class="panel-body">
					<span class="district"><a href="#">台東区</a></span><span class="district"><a href="#">墨田区</a></span><span class="district"><a href="#">江東区</a></span><span class="district"><a href="#">荒川区</a></span><span class="district"><a href="#">足立区</a></span><span class="district"><a href="#">葛飾区</a></span><span class="district"><a href="#">江戸川区</a></span>
				</div>
			</div>
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">城南地域</h3>
				</div>
				<div class="panel-body">
					<span class="district"><a href="#">品川区</a></span><span class="district"><a href="#">目黒区</a></span><span class="district"><a href="#">大田区</a></span>
				</div>
			</div>
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">城西地域</h3>
				</div>
				<div class="panel-body">
					<span class="district"><a href="#">世田谷区</a></span><span class="district"><a href="#">中野区</a></span><span class="district"><a href="#">杉並区</a></span><span class="district"><a href="#">練馬区</a></span>
				</div>
			</div>
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">城北地域</h3>
				</div>
				<div class="panel-body">
					<span class="district"><a href="#">北区</a></span><span class="district"><a href="#">板橋区</a></span>
				</div>
			</div>
	    </div>
    </div>
  </body>
</html>