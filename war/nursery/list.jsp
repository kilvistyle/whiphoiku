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
		  <h3 class="text-muted"><img alt="ホイップ保育ロゴマーク" width="370" class="img-responsive" src="http://whip-hoiku.appspot.com/images/whiplogo.png" /></h3>
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
			<div class="table-responsive">
				<table class="table table-striped table-bordered">
					<tr><th>保育園名</th><th>種別</th><th>電話番号</th><th>住所</th></tr>
					<tr><td><a href="/nursery/hoikuen?id=">千代田区立神田保育園</a></td><td>認可（公立）</td><td>03-3253-6258</td><td>千代田区神田淡路町2-109</td></tr>
					<tr><td><a href="#">千代田区立西神田保育園</a></td><td>認可（公立）</td><td>03-3253-6258</td><td>千代田区西神田2-6-2</td></tr>
					<tr><td><a href="#">千代田区立麹町保育園</a></td><td>認可（公立）</td><td>03-3253-6258</td><td>千代田区三番町7</td></tr>
					<tr><td><a href="#">千代田区立四番町保育園</a></td><td>認可（公立）</td><td>03-3253-6258</td><td>千代田区四番町11番地</td></tr>
					<tr><td><a href="#">ポピンズナーサリースクール一番町</a></td><td>認可（私立）</td><td>102-0082</td><td>千代田区一番町10-8 ウエストビル2Ｆ</td></tr>
					<tr><td><a href="#">アスク二番町保育園</a></td><td>認可（私立）</td><td>03-3253-6258</td><td>千代田区二番町2-1 二番町TSビル1～3階</td></tr>
				</table>
			</div>
	    </div>
    </div>
  </body>
</html>