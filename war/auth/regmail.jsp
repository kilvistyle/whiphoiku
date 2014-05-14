<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>ホイップ保育 - メール登録 - </title>

    <!-- Bootstrap -->
    <link href="/css/bootstrap.min.css" rel="stylesheet">
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
  </head>
<body>
    <div id="header">
      <h1><img src="/images/hoiplogo.png" alt="ホイップ保育ロゴ" /></h1>
    </div>
    <div >
	    <form action="/auth/regmail" method="post">
	    <input type="hidden" ${f:text("token")}>
        <input type="hidden" ${f:text("address")}>
        <input type="hidden" ${f:text("search_area")}>
        <input type="hidden" ${f:text("age")}>
        <input type="hidden" ${f:text("public_private")}>
        <input type="hidden" ${f:text("lat")}>
        <input type="hidden" ${f:text("lng")}>
        <input type="hidden" name="register" value="true">
        <table>
            <tr>
	            <th>メールアドレス</th>
	            <td><input type="text" ${f:text("mail")} class="${f:errorClass('mail', 'err')}"><div class="err">${errors.mail}</div></td>
            </tr>
            <tr>
                <th>メールアドレス（確認）</th>
                <td><input type="text" ${f:text("mail_conf")} class="${f:errorClass('mail_conf', 'err')}"><div class="err">${errors.mail_conf}</div>
                </td>
            </tr>
        </table>
        <input type="submit" value=" 登録 ">
	    </form>
    </div>
  </body>
</html>