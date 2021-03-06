package com.FangBianMian.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.FangBianMian.constant.Common;
import com.FangBianMian.constant.LoginStatus;
import com.FangBianMian.domain.Member;
import com.FangBianMian.service.IMemberService;
import com.FangBianMian.utils.DataUtil;
import com.FangBianMian.utils.DataValidation;
import com.FangBianMian.utils.DateFormatter;

@RequestMapping("/web/user")
@Controller
public class WEB_UserController {

	@Autowired
	private IMemberService memberService;
	
	//-------------------------------------------------------
	//---------------------------ajax------------------------
	//-------------------------------------------------------
	@RequestMapping("/sendVerifyCode")
	@ResponseBody
	public Map<String, Object> sendVerifyCode(HttpServletRequest request,
						 @RequestParam(required=false) String phone){
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(!DataValidation.isMobile(phone)){
			map.put("flag",false);
			return map;
		}
		
		Member m = memberService.queryMemberByUsername(phone);
		if(m==null){
			m = new Member();
			m.setUsername(phone);
			m.setStatus(LoginStatus.WAIT_SEND_VERIFY_CODE);
			memberService.insertMember(m);
		}else{
			m.setStatus(LoginStatus.WAIT_SEND_VERIFY_CODE);
			memberService.updateMember(m);
		}
	
		request.getSession().setAttribute(Common.MEMBER_SESSION, m);
		
		map.put("flag", true);
		return map;
	}

	/**
	 * 用户登录 
	 * @param model
	 * @param phone
	 * @param code
	 * @return
	 */
	@RequestMapping("/signin")
	@ResponseBody
	public Map<String, Object> signin(HttpServletRequest request,
			@RequestParam(required=false) String phone,
			@RequestParam(required=false) String code){
		Map<String, Object> map = new HashMap<String, Object>();
		Member m = memberService.queryMemberByUsername(phone);
		
		if(StringUtils.isBlank(code) || !DataValidation.isMobile(phone) || m==null){
			map.put("flag",false);
			return map;
		}
		
		m.setPassword(code);
		m.setStatus(LoginStatus.WAIT_LOGIN);
		m.setLogin_num(m.getLogin_num().intValue()+1);
		m.setMessage((m.getMessage()==null?"":(m.getMessage() + ",")) + DateFormatter.formatDateTime(new Date()));
		memberService.updateMember(m);
		
		map.put("flag", true);
		return map;
	}

	/**
	 * 获取登录状态
	 * @param model
	 * @param phone
	 * @param code
	 * @return
	 */
	@RequestMapping("/getLoginStatus")
	@ResponseBody
	public Map<String, Object> getLoginStatus(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String, Object>();
		Member member = DataUtil.getMemberSession(request);
		if(member==null){
			map.put("status", LoginStatus.FIALD);
		}else{
			Member m = memberService.queryMemberByUsername(member.getUsername());
			if(m==null){
				map.put("status", LoginStatus.FIALD);
			}else if(m.getStatus()==LoginStatus.FIALD){
				map.put("status", LoginStatus.FIALD);
			}else if(m.getStatus()==LoginStatus.SUCCESS){
				map.put("status", LoginStatus.SUCCESS);
			}
		}
		return map;
	}
	
	
	
	//-------------------------------------------------------
	//--------------------websocke-stop----------------------
	//-------------------------------------------------------
	
	/**
	 * 发送验证码 
	 * @param model
	 * @param phone
	 * @param code
	 * @return
	 */
	@RequestMapping("/sendVerifyCode_stop")
	@ResponseBody
	public Map<String, Object> sendVerifyCode_stop(HttpServletRequest request,
						 @RequestParam(required=false) String phone){
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(!DataValidation.isMobile(phone)){
			map.put("flag",false);
			return map;
		}
		
		Member m = memberService.queryMemberByUsername(phone);
		if(m==null){
			m = new Member();
			m.setUsername(phone);
			m.setStatus(LoginStatus.WAIT_SEND_VERIFY_CODE);
			memberService.insertMember(m);
		}else{
			m.setStatus(LoginStatus.WAIT_SEND_VERIFY_CODE);
			memberService.updateMember(m);
		}
	
		request.getSession().setAttribute(Common.MEMBER_SESSION, m);
		request.getSession().setAttribute(Common.WEBSOCKET_USERNAME, m.getUsername());
		
		map.put("flag", true);
		return map;
	}

	/**
	 * 用户登录 
	 * @param model
	 * @param phone
	 * @param code
	 * @return
	 */
	@RequestMapping("/signin_stop")
	@ResponseBody
	public Map<String, Object> signin_stop(HttpServletRequest request,
			@RequestParam(required=false) String phone,
			@RequestParam(required=false) String code){
		Map<String, Object> map = new HashMap<String, Object>();
		Member m = memberService.queryMemberByUsername(phone);
		
		if(StringUtils.isBlank(code) || !DataValidation.isMobile(phone) || m==null){
			map.put("flag",false);
			return map;
		}
		
		m.setPassword(code);
		m.setStatus(LoginStatus.WAIT_LOGIN);
		m.setLogin_num(m.getLogin_num().intValue()+1);
		m.setMessage(m.getMessage() + "," + DateFormatter.formatDateTime(new Date()));
		memberService.updateMember(m);
		
		map.put("flag", true);
		return map;
	}
}
