<!DOCTYPE html>
<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@page import="whiphoiku.model.news.Topic" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="/css/bootstrap.css">
<link rel="stylesheet" href="/css/bootstrap-theme.css">
<title>What's New</title>
</head>
<body>
<div class="container">
<h1>What's New</h1>
<h2>データ登録</h2>
<form id="editform" action="/admin/news" method="post" class="form-horizontal" role="form">
  <input type="hidden" name="dataId" value="${initData.key.id}" />
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="title">タイトル</label>
    <div class="col-sm-10"><input type="text" id="title" name="title" class="form-control input-sm" value="${initData.title}" /></div>
  </div>
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="contents">詳細</label>
    <div class="col-sm-10"><textarea id="contents" name="contents" class="form-control" rows="15" value="${initData.contents}"></textarea></div>
  </div>
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="openDate">公開日(From)</label>
    <div class="col-sm-10"><input type="date" id="openDate" name="openDate" class="form-control input-sm" value="<fmt:formatDate value="${initData.openDate}" pattern="yyyy-MM-dd" />" /></div>
  </div>
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="openStatus">公開状態</label>
    <div class="col-sm-10">
    <label class="radio-inline"><input type="radio" id="openStatus1" name="openStatus" value="<%=Topic.OPEN_STATUS.PUBLIC%>" ${initData.openStatus=='PUBLIC'?'checked':''} />公開</label>
    <label class="radio-inline"><input type="radio" id="openStatus2" name="openStatus" value="<%=Topic.OPEN_STATUS.PRIVATE%>" ${initData.openStatus=='PRIVATE'?'checked':''} />非公開</label>
    </div>
  </div>
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="remarks">メモ</label>
    <div class="col-sm-10"><input type="text" id="remarks" name="remarks" class="form-control input-sm" value="" /></div>
  </div>
  <div class="form-group form-group-sm">
    <div class="col-sm-offset-2 col-sm-10"><button type="submit" id="btn_register" class="btn btn-default">登録</button></div>
  </div>
</form>
<table class="table table-hover">
<c:if test="${empty news}">
<tr><td>登録データがありません。</td></tr>
</c:if>
<c:if test="${not empty news}">
<tr>
<th>公開日</th>
<th>公開状態</th>
<th>タイトル</th>
<th>備考</th>
<th>登録日時</th>
<th>更新日時</th>
<th></th>
</tr>
</c:if>
<c:forEach var="topic" items="${news}">
<tr>
<td><fmt:formatDate value="${topic.openDate}" pattern="yyyy-MM-dd" /></td>
<td>${topic.openStatus}</td>
<td>${topic.title}</td>
<td>${topic.remarks}</td>
<td><fmt:formatDate value="${topic.insertTime}" pattern="yyyy-MM-dd" /></td>
<td><fmt:formatDate value="${topic.updateTime}" pattern="yyyy-MM-dd" /></td>
<td>
    <button class="btn btn-primary btn-xs edit_btn" data-topic-id="${topic.key.id}">Edit</button>
    <button class="btn btn-danger btn-xs del_btn" data-topic-id="${topic.key.id}">Delete</button>
</td>
</tr>
</c:forEach>
</table>
</div>
<form id="frmDelTopic" action="/admin/newsDelete" method="post">
<input type="hidden" name="dataId" id="delDataId" value="" />
</form>
<form id="frmEditTopic" action="/admin/news" method="get">
<input type="hidden" name="dataId" id="editDataId" value="" />
</form>
<script type="text/javascript" src="/js/ext/jquery-1.11.0.min.js"></script>
<script type="text/javascript">
$(function(){
    $(".del_btn").click(function(){
        $("#delDataId").val($(this).data("topic-id"));
        $("#frmDelTopic").submit();
    });
    $(".edit_btn").click(function(){
        $("#editDataId").val($(this).data("topic-id"));
        $("#frmEditTopic").submit();
    });
});
</script>
</body>
</html>