<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>ホイップ保育 - 受信メールをご確認下さい - </title>

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
  <script src="/js/bootstrap.min.js"></script>
  <script type="text/javascript" charset="UTF-8">
  $(function() {
	  // セキュア通信しないリンクのノンセキュアドメインを追加
	  $('a').appendDomain('${_nonSecureDomain}');
  });
  </script>
  </head>
<body>
<div class="container wh_container">
<div class="masthead">
  <h3 class="text-muted"><img alt="ホイップ保育ロゴマーク" width="370" class="img-responsive" src="../images/whiplogo.png" /></h3>
  <ul class="nav nav-justified">
    <li class="active"><a href="#">ホーム</a></li>
    <li><a href="#">空き状況検索</a></li>
    <li><a href="#">保育園一覧</a></li>
    <li><a href="#">料金計算</a></li>
    <li><a href="#">ホイップ保育？</a></li>
  </ul>
</div>
    <h2>お知らせメールの登録が完了しました！</h2>
    <p>ご利用ありがとうございます。指定された条件にマッチする保育園の空きが見つかり次第お知らせメールをお送りします。</p>
    <p><a href="/">トップページに戻る</a></p>
    </div>
  </body>
</html>