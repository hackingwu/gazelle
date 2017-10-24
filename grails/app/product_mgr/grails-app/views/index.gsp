<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>系统登录</title>
	<link type="text/css" href="css/login.css" rel="stylesheet" />
</head>
<body>
<div id="container">
	<div class="logo"><span class="title">生产管理系统</span></div>
	<div id="box">
		%{--<form action="index.html" method="POST">--}%
		<p class="main">
			<label>用户名: </label>
			<input id="input-username" name="login" />
			<label>密码: </label>
			<input type="password" id="input-password" name="password" >
		</p>
		<p class="space">
			<span style="color: #F00;padding-left: 105px;" id="message"></span>
			<input type="submit" id="login-btn" value="登录" class="login" />
		</p>
		%{--</form>--}%
	</div>
</div>
<script src="js/login/jq1.8.js"></script>
<script>
    function login(){
        var login
        if($('#input-username').val() ==  $('#input-username').attr('placeholder')){

            login = ""
        }else{
            login = $.trim($('#input-username').val());
        }
        var password = $.trim($('#input-password').val());


        $.ajax({
            type:"POST",
            url:"<g:createLink controller="login" action="login"/>",
            dataType: "json",
            data:{login:login,password:password},
            success: function (json) {
                if(json.success){
                    window.location.href='<g:createLink controller="home" action="index"/>';
                }else{
                    $(message).text(json.message);
                    $('#login-btn').removeAttr('disabled');
                }

            }
        });
    }

    function eventInit() {
        jQuery('#login-btn').click(function () {
            $('#login-btn').attr('disabled',true);
            login();
        });

        $("#login-btn,#input-password").keydown(function(e){
            if(e.keyCode == 13){
                $('#login-btn').attr('disabled',true);
                login();
            }
        });

        jQuery('#forget-password').click(function () {
            jQuery('#loginform').hide();
            jQuery('#forgotform').show(200);
        });

        jQuery('#forget-btn').click(function () {
            jQuery('#loginform').slideDown(200);
            jQuery('#forgotform').slideUp(200);
        });
    }
    jQuery(document).ready(function() {

        if($.browser.msie && parseInt($.browser.version)<9){
            alert("您使用的IE浏览器版本过低，为获得最佳的用户体验，建议您使用IE9及以上版本浏览器，谷歌浏览器或火狐浏览器登陆系统。");
        }

        eventInit();
        $("#input-username").focus();
    });
</script>
</body>
</html>