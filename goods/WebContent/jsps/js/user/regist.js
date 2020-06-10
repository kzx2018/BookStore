$(function(){
	$(".errorClass").each(function(){
		showError($(this))
	})
	
	$("#submitBtn").hover(
			function(){
				$("#submitBtn").attr("src","/goods/images/regist2.jpg")
			},
			function(){
				$("#submitBtn").attr("src","/goods/images/regist1.jpg")
			}
	)
	
	$(".inputClass").focus(function(){
		var labelId = $(this).attr("id")+"Error";
		$("#"+labelId).text("");
		showError($("#"+labelId));
	})
	
	$(".inputClass").blur(function(){
		var id = $(this).attr("id");
		var funName = "validate"+id.substring(0,1).toUpperCase()+id.substring(1)+"()";
		eval(funName);
	})
	
	$("#registForm").submit(function(){
		var bool = true;
		if(!validateLoginname()){
			bool = false;
		}
		if(!validateLoginpass()){
			bool = false;
		}
		if(!validateReloginpass()){
			bool = false;
		}
		if(!validateEmail()){
			bool = false;
		}
		if(!validateVerifyCode()){
			bool = false;
		}
		return bool;
	})
})

function validateLoginname(){
	/*
	 * 1.非空校验
	 * 2.长度校验
	 * 3.是否注册
	 */
	var id = "loginname";
	var value = $("#"+id).val();
	if(!value){
		$("#"+id+"Error").text("用户名不能为空！");
		showError($("#"+id+"Error"));
		return false;
	}
	
	if(value.length<3||value.length>20){
		$("#"+id+"Error").text("用户名长度在（3-20）之间！");
		showError($("#"+id+"Error"));
		return false;
	}
	
	$.ajax({
		url:"/goods/UserServlet",
		data:{method:"ajaxValidateLoginname",loginname:value},
		async:false,
		cache:false,
		type:"POST",
		dataType:"json",
		success:function(result){
			if(!result){
				$("#"+id+"Error").text("用户名已被注册！");
				showError($("#"+id+"Error"));
				return false;
			}
		}
	});

	return true;
}

function validateLoginpass(){
	var id = "loginpass";
	var value = $("#"+id).val();
	if(!value){
		$("#"+id+"Error").text("密码不能为空！");
		showError($("#"+id+"Error"));
		return false;
	}
	
	if(value.length<3||value.length>20){
		$("#"+id+"Error").text("密码长度在（3-20）之间！");
		showError($("#"+id+"Error"));
		return false;
	}
	
	return true;
}

function validateReloginpass(){
	var id = "reloginpass";
	var value = $("#"+id).val();
	if(!value){
		$("#"+id+"Error").text("重输密码不能为空！");
		showError($("#"+id+"Error"));
		return false;
	}
	//两次输入是否一致
	if(value!=$("#loginpass").val()){
		$("#"+id+"Error").text("两次输入密码不一致！");
		showError($("#"+id+"Error"));
		return false;
	}
	
	return true;
}

function validateEmail(){
	var id = "email";
	var value = $("#"+id).val();
	if(!value){
		$("#"+id+"Error").text("邮箱不能为空！");
		showError($("#"+id+"Error"));
		return false;
	}
	//email的格式校验
	if(!/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(value)){
		$("#"+id+"Error").text("错误的email格式！");
		showError($("#"+id+"Error"));
		return false;
	}
	
	$.ajax({
		url:"/goods/UserServlet",
		data:{method:"ajaxValidateEmail",email:value},
		async:false,
		cache:false,
		type:"POST",
		dataType:"json",
		success:function(result){
			if(!result){
				$("#"+id+"Error").text("邮箱已被注册！");
				showError($("#"+id+"Error"));
				return false;
			}
		}
	});
	
	return true;
}

function validateVerifyCode(){
	var id = "verifyCode";
	var value = $("#"+id).val();
	if(!value){
		$("#"+id+"Error").text("验证码不能为空！");
		showError($("#"+id+"Error"));
		return false;
	}
	
	if(value.length!=4){
		$("#"+id+"Error").text("验证码错误！");
		showError($("#"+id+"Error"));
		return false;
	}
	
	$.ajax({
		url:"/goods/UserServlet",
		data:{method:"ajaxValidateVerifyCode",verifyCode:value},
		async:false,
		cache:false,
		type:"POST",
		dataType:"json",
		success:function(result){
			if(!result){
				$("#"+id+"Error").text("验证码错误！");
				showError($("#"+id+"Error"));
				return false;
			}
		}
	});
	
	return true;
}


function showError(ele){
	var text = ele.text();
	if(!text){
		ele.css("display","none")
	}
	else{
		ele.css("display","")
	}
}

function _hyz(){
	$("#imgVerifyCode").attr("src","/goods/VerifyCodeServlet?a=" + new Date().getTime())
}