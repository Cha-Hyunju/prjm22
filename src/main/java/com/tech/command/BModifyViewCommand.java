package com.tech.command;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.ui.Model;

import com.tech.prjm09.dao.BDao;
import com.tech.prjm09.dto.BDto;

import jakarta.servlet.http.HttpServletRequest;

public class BModifyViewCommand implements BCommand{

	@Override
	public void execute(Model model) {
		BDao dao=new BDao();
		Map<String, Object> map=model.asMap();
		HttpServletRequest request=(HttpServletRequest) map.get("request");	
		String bid=request.getParameter("bid");
		
		BDto dto=dao.modifyView(bid);
		
		model.addAttribute("content_view",dto);
	}

}
