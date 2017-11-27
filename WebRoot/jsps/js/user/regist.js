$(function() {
	/*
	 * 1.得到所有错误信息，循环遍历
	 * 调用一个方法来确定是否显示错误信息
	 */
	$(".labelError").each(function() {
		showError($(this));
	});
	/*
	 * 2.切换注册按钮的图片
	 */
	$("#submit").hover(
			function() {
				$("#submit").attr("src","/goods/images/regist2.jpg");
			},
			function() {
				$("#submit").attr("src","/goods/images/regist1.jpg");
			}
	);
	
	/*
	 * 3.输入框得到焦点隐藏错误信息
	 */
	$(".input").focus(function(){
//		alert($(this).attr("id"));
		var labelId = $(this).attr("id")+"Error";
		$("#"+labelId).text("");
		showError($("#"+labelId));
	});
	
	/*
	 * 4.输入框失去焦点进行校验
	 */
	
	$(".input").blur(function(){
		var id = $(this).attr("id");
		var funName = "validate" + id.substring(0,1).toUpperCase() + id.substring(1) + "()";
		eval(funName);
	});
	
	/*
	 * 5表单提交时进行校验
	 */
	$("#registFrom").submit(function(){
		var bool = true;
		if(!validateLoginname()){
			bool = false;
		}if(!validateLoginpass()){
			bool = false;
		}if(!validateReloginpass()){
			bool = false;
		}if(!validateEmail()){
			bool = false;
		}if(!validateVerifyCode()){
			bool = false;
		}
		return bool;
	});
});

/*
 * 登录名校验方法
 */
function validateLoginname() {
	var id = "loginname";
	var value = $("#"+id).val();
	 // 1.非空校验
	if(!value){
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#"+id+"Error").text("用户名不能为空");
		showError($("#"+id+"Error"));
		return false;
	}
	//2.长度校验
	if(value.length < 4  || value.length > 20){
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#"+id+"Error").text("用户名长度必须在3-20之间 ");
		showError($("#"+id+"Error"));
		false;
		}
		// 3.是否注册校验
		$.ajax({
			url:"/goods/UserServlet",
			data:{method:"ajaxValidateLoginname",loginname:value},
			type:"POST",
			dataType:"json",
			async:false, 
			cache:false,
			success:function(result){
//				alert("result");
				if(!result){
					$("#"+id+"Error").text("用户名已注册");
					showError($("#"+id+"Error"));
					return false;
				}
			}
		});
		return true;
}

/*
 * 登录密码校验方法
 */
function validateLoginpass() {
	var id = "loginpass";
	var value = $("#"+id).val();
	 // 1.非空校验
	if(!value){
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#"+id+"Error").text("密码不能为空");
		showError($("#"+id+"Error"));
		return false;
	}
	//2.长度校验
	if(value.length < 4  || value.length > 20){
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#"+id+"Error").text("密码长度必须在3-20之间 ");
		showError($("#"+id+"Error"));
		false;
	}
		return true;
		// 3.是否注册校验
}

/*
 * 确认密码校验方法
 */
function validateReloginpass() {
	var id = "reloginpass";
	var value = $("#"+id).val();
	 // 1.非空校验
	if(!value){
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#"+id+"Error").text("密码不能为空");
		showError($("#"+id+"Error"));
		return false;
	}
	//2.确认密码校验
	if( value != $("#loginpass").val()){
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#"+id+"Error").text("两次密码输入不一致");
		showError($("#"+id+"Error"));
		false;
	}
		return true;
}

/*
 *Email校验方法
 */
function validateEmail() {
	var id = "email";
	var value = $("#"+id).val();
	 // 1.非空校验
	if(!value){
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#"+id+"Error").text("Email不能为空");
		showError($("#"+id+"Error"));
		return false;
	}
	//2.长度校验
	if(!/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(value)){
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#"+id+"Error").text("错误的Email格式 ");
		showError($("#"+id+"Error"));
		false;
	}
		return true;
		// 3.是否注册校验
}

/*
 * 验证码校验方法
 */
function validateVerifyCode() {
	var id = "verifyCode";
	var value = $("#"+id).val();
	 // 1.非空校验
	if(!value){
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#"+id+"Error").text("验证码不能为空");
		showError($("#"+id+"Error"));
		return false;
	}
	//2.长度校验
	if(value.length != 4){
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 */
		$("#"+id+"Error").text("验证码错误 ");
		showError($("#"+id+"Error"));
		false;
	}
		return true;
		// 3.是否注册校验
}


function showError(ele){
	var text = ele.text();//获取元素的内容
	if (!text) {
		ele.css("display","none");
	}else{
		ele.css("display","");
	}
} 

/*
    换一张验证码
 */
function _change() {
	/*
	 * 1.获取img元素
	 * 2、重新设置他的src
	 * 3、使用毫秒来添加参数
	 */
	$("#vCode").attr("src","/goods/VerifyCodeServlet?a="+new Date().getTime());
}