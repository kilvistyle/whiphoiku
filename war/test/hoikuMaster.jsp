<!DOCTYPE html>
<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="/css/bootstrap.css">
<link rel="stylesheet" href="/css/bootstrap-theme.css">
<title>Index</title>
</head>
<body>
<h1>保育園情報登録</h1>
<h2>データ登録</h2>
<form action="/test/hoikuMaster" method="post" class="form-horizontal" role="form">
  <c:if test="${not empty initData}"><input type="hidden" name="hoikuId" value="${initData.key.id}" /></c:if>
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="name">園名</label>
    <div class="col-sm-10"><input type="text" id="name" name="name" class="form-control input-sm" value="${initData.name}" /></div>
  </div>
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="name">郵便番号</label>
    <div class="col-sm-10"><input type="text" id="zipcode" name="zipcode" class="form-control input-sm" value="${initData.zipcode}" /></div>
  </div>
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="name">住所</label>
    <div class="col-sm-10"><input type="text" id="address" name="address" class="form-control input-sm" value="${initData.address}" /></div>
  </div>
<%--
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="name">開園時間</label>
    <div class="col-sm-10"><input type="text" id="openingTime" name="openingTime" class="form-control input-sm" /></div>
  </div>
 --%>
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="name">電話番号</label>
    <div class="col-sm-10"><input type="text" id="tellNo" name="tellNo" class="form-control input-sm"  value="${initData.tellNo}"/></div>
  </div>
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="name">Web-URL</label>
    <div class="col-sm-10"><input type="text" id="officialUrl" name="officialUrl" class="form-control input-sm" value="${initData.officialUrl}" /></div>
  </div>
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="name">保育園種別</label>
    <div class="col-sm-10">
    <label class="radio-inline"><input type="radio" id="schoolKubun1" name="schoolKubun" value="1" ${initData.schoolKubun=='1'?'checked':''} />公立</label>
    <label class="radio-inline"><input type="radio" id="schoolKubun2" name="schoolKubun" value="2" ${initData.schoolKubun=='2'?'checked':''} />私立</label>
    </div>
  </div>
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="name">0歳児空き</label>
    <div class="col-sm-10"><input type="text" id="collectZeroYear" name="collectZeroYear" class="form-control input-sm" value="${initData.collectZeroYear}" /></div>
  </div>
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="name">1歳児空き</label>
    <div class="col-sm-10"><input type="text" id="collectOneYear" name="collectOneYear" class="form-control input-sm" value="${initData.collectOneYear}" /></div>
  </div>
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="name">2歳児空き</label>
    <div class="col-sm-10"><input type="text" id="collectTwoYear" name="collectTwoYear" class="form-control input-sm" value="${initData.collectTwoYear}" /></div>
  </div>
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="name">3歳児空き</label>
    <div class="col-sm-10"><input type="text" id="collectThreeYear" name="collectThreeYear" class="form-control input-sm" value="${initData.collectThreeYear}" /></div>
  </div>
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="name">4歳児空き</label>
    <div class="col-sm-10"><input type="text" id="collectFourYear" name="collectFourYear" class="form-control input-sm" value="${initData.collectFourYear}" /></div>
  </div>
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="name">5歳児空き</label>
    <div class="col-sm-10"><input type="text" id="collectFiveYear" name="collectFiveYear" class="form-control input-sm" value="${initData.collectFiveYear}" /></div>
  </div>
  <div class="form-group form-group-sm">
    <label class="col-sm-2 control-label" for="name">備考</label>
    <div class="col-sm-10"><input type="text" id="remarks" name="remarks" class="form-control input-sm" value="${initData.remarks}" /></div>
  </div>
  <div class="form-group form-group-sm">
    <div class="col-sm-offset-2 col-sm-10"><button type="submit" class="btn btn-default">登録</button></div>
  </div>
</form>
<br />
<h2>登録データ</h2>
<table class="table table-hover">
<c:if test="${empty hoikuList}">
<tr><td>登録データがありません。</td></tr>
</c:if>
<c:if test="${not empty hoikuList}">
<tr>
<th>園名</th>
<th>郵便番号</th>
<th>住所</th>
<th>電話番号</th>
<th>Web-URL</th>
<th>保育園種別</th>
<th>0歳児空き</th>
<th>1歳児空き</th>
<th>2歳児空き</th>
<th>3歳児空き</th>
<th>4歳児空き</th>
<th>5歳児空き</th>
<th>備考</th>
<th></th>
</tr>
</c:if>
<c:forEach var="hoiku" items="${hoikuList}" varStatus="status">
<tr>
<td>${hoiku.name}</td>
<td>${hoiku.zipcode}</td>
<td>${hoiku.address}</td>
<td>${hoiku.tellNo}</td>
<td>${hoiku.officialUrl}</td>
<td>${hoiku.schoolKubun}</td>
<%-- <td>${hoiku.openingTime}</td> --%>
<td>${hoiku.collectZeroYear}</td>
<td>${hoiku.collectOneYear}</td>
<td>${hoiku.collectTwoYear}</td>
<td>${hoiku.collectThreeYear}</td>
<td>${hoiku.collectFourYear}</td>
<td>${hoiku.collectFiveYear}</td>
<td>${hoiku.remarks}</td>
<td>
    <button class="btn btn-primary btn-xs edit_btn" data-hoiku-id="${hoiku.key.id}">Edit</button>
    <button class="btn btn-danger btn-xs del_btn" data-hoiku-id="${hoiku.key.id}">Delete</button>
</td>
</tr>
</c:forEach>
</table>
<form id="frmDelHoiku" action="/test/hoikuDelete" method="post">
<input type="hidden" name="dataId" id="delDataId" value="" />
</form>
<form id="frmEditHoiku" action="/test/hoikuMaster" method="get">
<input type="hidden" name="dataId" id="editDataId" value="" />
</form>
<script type="text/javascript" src="/js/ext/jquery-1.11.0.min.js"></script>
<script type="text/javascript">
$(function(){
    $(".del_btn").click(function(){
        $("#delDataId").val($(this).data("hoiku-id"));
        $("#frmDelHoiku").submit();
    });
    $(".edit_btn").click(function(){
        $("#editDataId").val($(this).data("hoiku-id"));
        $("#frmEditHoiku").submit();
    });
});
</script>
</body>
</html>
