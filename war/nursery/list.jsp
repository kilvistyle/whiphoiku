<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html>
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
</head>
<body>
<div class="container wh_container">
<div class="masthead">
  <h3 class="text-muted"><img alt="ホイップ保育ロゴマーク" width="370" class="img-responsive" src="/images/whiplogo.png" /></h3>
  <ul class="nav nav-justified">
    <li><a href="#">ホーム</a></li>
    <li><a href="/">空き状況検索</a></li>
    <li class="active"><a href="/nursery/list">保育園一覧</a></li>
    <li><a href="#">料金計算</a></li>
    <li><a href="#">ホイップ保育？</a></li>
  </ul>
</div>

<table class="table table-hover">
<c:if test="${empty hoikuList}">
<tr><td>登録データがありません。</td></tr>
</c:if>
<c:if test="${not empty hoikuList}">
<tr>
<th>地区</th>
<th>園名</th>
<th>保育園種別</th>
<th>郵便番号</th>
<th>住所</th>
<th>電話番号</th>
<th>ホームページ</th>
</tr>
</c:if>
<c:forEach var="hoiku" items="${hoikuList}" varStatus="status">
<tr>
<td>${hoiku.extractType}</td>
<td>${hoiku.name}</td>
<td>${hoiku.schoolKubun}</td>
<td>${hoiku.zipcode}</td>
<td>${hoiku.address}</td>
<td>${hoiku.tellNo}</td>
<td>${hoiku.officialUrl}</td>
</tr>
</c:forEach>
</table>
</div>
</body>
</html>
